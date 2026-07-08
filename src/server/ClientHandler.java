package server;

import dao.MessageDAO;
import dao.UserDAO;
import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.Set;

/**
 * One ClientHandler is created PER connected client.
 * It runs in its own thread (implements Runnable).
 *
 * COMMUNICATION PROTOCOL (simple text over TCP):
 *
 *   Client → Server:
 *     "LOGIN:username:password"
 *     "REGISTER:username:password:email"
 *     "MSG:Hello everyone!"
 *     "DISCONNECT"
 *     "GET_ONLINE_USERS"
 *
 *   Server → Client:
 *     "AUTH_SUCCESS"
 *     "AUTH_FAIL:reason"
 *     "REGISTER_SUCCESS"
 *     "REGISTER_FAIL:reason"
 *     "HISTORY_START"
 *     "HISTORY:formatted message"
 *     "HISTORY_END"
 *     "ONLINE_USERS:alice,bob,..."
 *     "[alice]: Hello!"   ← broadcast message
 */
public class ClientHandler implements Runnable {

    private final Socket             socket;
    private final Set<ClientHandler> allClients;
    private       BufferedReader     reader;
    private       PrintWriter        writer;
    private       String             username;

    private final UserDAO    userDAO    = new UserDAO();
    private final MessageDAO messageDAO = new MessageDAO();

    public ClientHandler(Socket socket, Set<ClientHandler> allClients) {
        this.socket     = socket;
        this.allClients = allClients;
    }

    @Override
    public void run() {
        try {
            // Wrap raw streams for convenient line-by-line I/O
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            // 'true' = auto-flush after every println
            writer = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);

            String line;
            while ((line = reader.readLine()) != null) {
                handleMessage(line);
            }

        } catch (IOException e) {
            System.out.println("[ClientHandler] Lost connection: "
                    + (username != null ? username : "unknown"));
        } finally {
            disconnect();
        }
    }

    // ── Route incoming message to correct handler ──────────────
    private void handleMessage(String line) {
        System.out.println("[Received] " + line);

        if      (line.startsWith("LOGIN:"))       handleLogin(line);
        else if (line.startsWith("REGISTER:"))    handleRegister(line);
        else if (line.startsWith("MSG:"))         handleChatMessage(line);
        else if (line.equals("DISCONNECT"))       disconnect();
        else if (line.equals("GET_ONLINE_USERS")) sendMessage(Server.getOnlineUsers());
        else System.out.println("[ClientHandler] Unknown command: " + line);
    }

    // ── LOGIN ─────────────────────────────────────────────────
    private void handleLogin(String line) {
        // Format: "LOGIN:username:password"
        String[] parts = line.split(":", 3);
        if (parts.length < 3) { sendMessage("AUTH_FAIL:Invalid format"); return; }

        String user = parts[1].trim();
        String pass = parts[2].trim();

        if (userDAO.loginUser(user, pass)) {
            this.username = user;
            sendMessage("AUTH_SUCCESS");
            userDAO.logEvent(username, "JOIN");
            sendChatHistory();
            Server.broadcastToAll("SERVER: " + username + " has joined the chat! 👋");
            Server.broadcastToAll(Server.getOnlineUsers());
            System.out.println("[Server] " + username + " logged in. Online: " + Server.getOnlineCount());
        } else {
            sendMessage("AUTH_FAIL:Invalid username or password");
        }
    }

    // ── REGISTER ──────────────────────────────────────────────
    private void handleRegister(String line) {
        // Format: "REGISTER:username:password:email"
        String[] parts = line.split(":", 4);
        if (parts.length < 4) { sendMessage("REGISTER_FAIL:Invalid format"); return; }

        String user  = parts[1].trim();
        String pass  = parts[2].trim();
        String email = parts[3].trim();

        if (userDAO.registerUser(user, pass, email)) {
            sendMessage("REGISTER_SUCCESS");
            System.out.println("[Server] New user registered: " + user);
        } else {
            sendMessage("REGISTER_FAIL:Username already exists");
        }
    }

    // ── CHAT MESSAGE ──────────────────────────────────────────
    private void handleChatMessage(String line) {
        if (username == null) { sendMessage("ERROR:Please login first"); return; }

        String content   = line.substring(4);   // strip "MSG:" prefix
        String formatted = "[" + username + "]: " + content;

        messageDAO.saveMessage(username, content);   // persist to DB
        Server.broadcastToAll(formatted);            // send to everyone
    }

    // ── Send last 50 messages as history ─────────────────────
    private void sendChatHistory() {
        List<String> history = messageDAO.getChatHistory(50);
        sendMessage("HISTORY_START");
        if (history.isEmpty()) {
            sendMessage("HISTORY:No previous messages.");
        } else {
            for (String msg : history) sendMessage("HISTORY:" + msg);
        }
        sendMessage("HISTORY_END");
    }

    // ── Write one line to this client's socket ────────────────
    public void sendMessage(String message) {
        if (writer != null) writer.println(message);
    }

    // ── Clean up on disconnect ────────────────────────────────
    private void disconnect() {
        try {
            if (username != null) {
                userDAO.logEvent(username, "LEAVE");
                Server.removeClient(this);
                Server.broadcastToAll("SERVER: " + username + " has left the chat.");
                Server.broadcastToAll(Server.getOnlineUsers());
                System.out.println("[Server] " + username + " disconnected.");
                username = null;
            }
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            System.err.println("[ClientHandler] Disconnect error: " + e.getMessage());
        }
    }

    public String getUsername() { return username; }
}