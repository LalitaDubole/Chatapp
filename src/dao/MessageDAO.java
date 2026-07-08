package dao;

import db.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * All database operations for the "messages" table.
 * Saves messages and retrieves chat history.
 */
public class MessageDAO {

    // ── Save a chat message ──────────────────────────────────────
    public boolean saveMessage(String sender, String content) {
        String sql = "INSERT INTO messages (sender, content) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, sender);
            ps.setString(2, content);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            System.err.println("[MessageDAO] saveMessage error: " + e.getMessage());
            return false;
        }
    }

    // ── Get last N messages ──────────────────────────────────────
    // Subquery trick: get latest N in DESC order, then flip to ASC for display
    public List<String> getChatHistory(int limit) {
        List<String> history = new ArrayList<>();
        String sql = "SELECT sender, content, sent_at " +
                "FROM (SELECT sender, content, sent_at FROM messages " +
                "      ORDER BY sent_at DESC LIMIT ?) sub " +
                "ORDER BY sent_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String sender  = rs.getString("sender");
                String content = rs.getString("content");
                Timestamp ts   = rs.getTimestamp("sent_at");
                // Format: [10:35] alice: Hello!
                String time = ts.toLocalDateTime().toLocalTime()
                        .withSecond(0).withNano(0).toString();
                history.add("[" + time + "] " + sender + ": " + content);
            }

        } catch (SQLException e) {
            System.err.println("[MessageDAO] getChatHistory error: " + e.getMessage());
        }
        return history;
    }

    // ── Get all messages by a specific user ──────────────────────
    public List<String> getMessagesBySender(String username) {
        List<String> result = new ArrayList<>();
        String sql = "SELECT content, sent_at FROM messages WHERE sender = ? ORDER BY sent_at ASC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                result.add(rs.getTimestamp("sent_at") + " → " + rs.getString("content"));
            }

        } catch (SQLException e) {
            System.err.println("[MessageDAO] getMessagesBySender error: " + e.getMessage());
        }
        return result;
    }
}