package client;

import gui.ChatFrame;
import java.io.BufferedReader;
import java.io.IOException;

/**
 * Background thread on the CLIENT side.
 * Reads lines from the server socket continuously.
 *
 * WHY A SEPARATE THREAD?
 *   readLine() BLOCKS until data arrives.
 *   If we called this on the Swing GUI thread (EDT), the
 *   entire window would freeze while waiting for messages.
 *
 * SWING THREAD SAFETY:
 *   Swing components can only be updated from the EDT.
 *   We use SwingUtilities.invokeLater() to safely schedule
 *   GUI updates from this background thread.
 */
public class MessageListener implements Runnable {

    private final BufferedReader reader;
    private final ChatFrame      chatFrame;
    private       boolean        running = true;

    public MessageListener(BufferedReader reader, ChatFrame chatFrame) {
        this.reader    = reader;
        this.chatFrame = chatFrame;
    }

    @Override
    public void run() {
        try {
            String line;
            while (running && (line = reader.readLine()) != null) {
                final String msg = line;
                processMessage(msg);
            }
        } catch (IOException e) {
            if (running) chatFrame.appendMessage("⚠ Connection to server lost.");
        }
    }

    private void processMessage(String message) {
        System.out.println("[MessageListener] " + message);

        if (message.startsWith("ONLINE_USERS:")) {
            String csv = message.substring("ONLINE_USERS:".length());
            javax.swing.SwingUtilities.invokeLater(() -> chatFrame.updateOnlineUsers(csv));

        } else if (message.equals("HISTORY_START")) {
            javax.swing.SwingUtilities.invokeLater(() ->
                    chatFrame.appendMessage("── Chat History ──────────────────────"));

        } else if (message.startsWith("HISTORY:")) {
            String hist = message.substring("HISTORY:".length());
            javax.swing.SwingUtilities.invokeLater(() -> chatFrame.appendMessage(hist));

        } else if (message.equals("HISTORY_END")) {
            javax.swing.SwingUtilities.invokeLater(() ->
                    chatFrame.appendMessage("── Live Chat ─────────────────────────"));

        } else if (message.startsWith("ERROR:")) {
            String err = message.substring("ERROR:".length());
            javax.swing.SwingUtilities.invokeLater(() -> chatFrame.appendMessage("⚠ " + err));

        } else {
            // Regular broadcast message
            javax.swing.SwingUtilities.invokeLater(() -> chatFrame.appendMessage(message));
        }
    }

    public void stop() { running = false; }
}
