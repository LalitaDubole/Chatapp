package client;

import gui.ChatFrame;
import gui.LoginFrame;
import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * CLIENT ENTRY POINT (run this to start a chat client).
 *
 * Responsibilities:
 *   1. Open TCP Socket connection to server
 *   2. Send protocol messages (login, register, chat)
 *   3. Read auth responses (before MessageListener starts)
 *   4. Launch LoginFrame GUI at startup
 *   5. Start MessageListener background thread after login
 *
 * FLOW:
 *   main() → LoginFrame appears
 *   User clicks Login → Client.connect() → Client.login()
 *   On AUTH_SUCCESS → ChatFrame opens → startListening() begins
 */
public class Client {

    private static final String HOST = "localhost";
    private static final int    PORT = 12345;

    private Socket         socket;
    private BufferedReader reader;
    private PrintWriter    writer;
    private boolean        connected = false;
    private String         username;
    private MessageListener messageListener;

    // ── Application entry point ──────────────────────────────
    public static void main(String[] args) {
        // All Swing creation must happen on the Event Dispatch Thread
        SwingUtilities.invokeLater(() -> {
            Client client = new Client();
            new LoginFrame(client);
        });
    }

    // ── Open TCP connection to server ────────────────────────
    public boolean connect() {
        try {
            socket    = new Socket(HOST, PORT);
            reader    = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            writer    = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
            connected = true;
            System.out.println("[Client] Connected to " + HOST + ":" + PORT);
            return true;
        } catch (IOException e) {
            System.err.println("[Client] Cannot connect: " + e.getMessage());
            return false;
        }
    }

    // ── Send raw text to server ──────────────────────────────
    public void sendToServer(String message) {
        if (writer != null && connected) {
            writer.println(message);
        }
    }

    // ── Login → returns true if AUTH_SUCCESS ─────────────────
    public boolean login(String user, String pass) {
        sendToServer("LOGIN:" + user + ":" + pass);
        try {
            String response = reader.readLine();
            System.out.println("[Client] Login response: " + response);
            if ("AUTH_SUCCESS".equals(response)) {
                this.username = user;
                return true;
            }
        } catch (IOException e) {
            System.err.println("[Client] Login read error: " + e.getMessage());
        }
        return false;
    }

    // ── Register → returns true if REGISTER_SUCCESS ──────────
    public boolean register(String user, String pass, String email) {
        sendToServer("REGISTER:" + user + ":" + pass + ":" + email);
        try {
            String response = reader.readLine();
            return "REGISTER_SUCCESS".equals(response);
        } catch (IOException e) {
            System.err.println("[Client] Register read error: " + e.getMessage());
        }
        return false;
    }

    // ── Send a chat message ───────────────────────────────────
    public void sendChatMessage(String text) {
        sendToServer("MSG:" + text);
    }

    // ── Start background reader thread ────────────────────────
    public void startListening(ChatFrame chatFrame) {
        messageListener = new MessageListener(reader, chatFrame);
        Thread t = new Thread(messageListener);
        t.setDaemon(true);
        t.start();
        System.out.println("[Client] MessageListener started.");
    }

    // ── Disconnect cleanly ────────────────────────────────────
    public void disconnect() {
        sendToServer("DISCONNECT");
        if (messageListener != null) messageListener.stop();
        try {
            if (socket != null && !socket.isClosed()) socket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connected = false;
    }

    public String  getUsername()  { return username;  }
    public boolean isConnected()  { return connected;  }
    public BufferedReader getReader() { return reader; }
}