package server;

import dao.MessageDAO;
import dao.UserDAO;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * SERVER ENTRY POINT.
 *
 * HOW SOCKETS WORK:
 *   ServerSocket(12345) → opens port 12345, like a shop's front door
 *   serverSocket.accept() → BLOCKS until a client connects
 *   Returns a Socket → private two-way channel with that client
 *   Each client gets their own ClientHandler thread
 *
 * HOW THREADS WORK:
 *   Without threads: Client B waits while server reads Client A (bad)
 *   With threads:    Main thread only runs accept() loop
 *                    Each client gets a dedicated thread that runs forever
 */
public class Server {

    private static final int PORT = 12345;

    // Thread-safe set of all currently connected handlers
    private static final Set<ClientHandler> clients =
            Collections.synchronizedSet(new HashSet<>());

    static final UserDAO    userDAO    = new UserDAO();
    static final MessageDAO messageDAO = new MessageDAO();

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════╗");
        System.out.println("║  Chat Server starting on port 12345  ║");
        System.out.println("╚══════════════════════════════════════╝");

        // Shutdown hook: runs when you press Ctrl+C
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("\n[Server] Shutting down...");
            broadcastToAll("SERVER: Server is shutting down. Goodbye!");
        }));

        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("[Server] Listening for connections...\n");

            while (true) {
                Socket clientSocket = serverSocket.accept();  // BLOCKS here
                System.out.println("[Server] New connection: "
                        + clientSocket.getInetAddress().getHostAddress());

                ClientHandler handler = new ClientHandler(clientSocket, clients);
                clients.add(handler);

                Thread t = new Thread(handler);
                t.setDaemon(true);   // dies when main thread dies
                t.start();
            }

        } catch (IOException e) {
            System.err.println("[Server] Fatal error: " + e.getMessage());
        }
    }

    /** Send a message to all clients except the one specified (pass null to send to all) */
    public static void broadcast(String message, ClientHandler exclude) {
        synchronized (clients) {
            for (ClientHandler c : clients) {
                if (c != exclude) c.sendMessage(message);
            }
        }
    }

    /** Send to every connected client including sender */
    public static void broadcastToAll(String message) {
        broadcast(message, null);
    }

    public static void removeClient(ClientHandler handler) {
        clients.remove(handler);
    }

    public static int getOnlineCount() {
        return clients.size();
    }

    /** Returns "ONLINE_USERS:alice,bob,charlie" */
    public static String getOnlineUsers() {
        StringBuilder sb = new StringBuilder("ONLINE_USERS:");
        synchronized (clients) {
            for (ClientHandler c : clients) {
                if (c.getUsername() != null) {
                    sb.append(c.getUsername()).append(",");
                }
            }
        }
        return sb.toString();
    }
}