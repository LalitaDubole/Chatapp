package dao;

import db.DBConnection;
import java.sql.*;

/**
 * All database operations for the "users" and "user_logs" tables.
 *
 * WHY DAO PATTERN?
 *   Keeps all SQL in one place. Server/GUI code never writes raw SQL.
 *   If you switch databases later, only DAO files change.
 */
public class UserDAO {

    // ── Register new user ────────────────────────────────────────
    public boolean registerUser(String username, String password, String email) {
        String sql = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ps.setString(3, email);
            return ps.executeUpdate() > 0;

        } catch (SQLIntegrityConstraintViolationException e) {
            System.err.println("[UserDAO] Username already exists: " + username);
            return false;
        } catch (SQLException e) {
            System.err.println("[UserDAO] registerUser error: " + e.getMessage());
            return false;
        }
    }

    // ── Verify login credentials ─────────────────────────────────
    public boolean loginUser(String username, String password) {
        String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                updateLastLogin(username);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("[UserDAO] loginUser error: " + e.getMessage());
        }
        return false;
    }

    // ── Check if username is taken ───────────────────────────────
    public boolean usernameExists(String username) {
        String sql = "SELECT user_id FROM users WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            return ps.executeQuery().next();

        } catch (SQLException e) {
            System.err.println("[UserDAO] usernameExists error: " + e.getMessage());
        }
        return false;
    }

    // ── Update last login timestamp ──────────────────────────────
    public void updateLastLogin(String username) {
        String sql = "UPDATE users SET last_login = NOW() WHERE username = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[UserDAO] updateLastLogin error: " + e.getMessage());
        }
    }

    // ── Log JOIN or LEAVE event ──────────────────────────────────
    public void logEvent(String username, String eventType) {
        String sql = "INSERT INTO user_logs (username, event_type) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, eventType);   // "JOIN" or "LEAVE"
            ps.executeUpdate();

        } catch (SQLException e) {
            System.err.println("[UserDAO] logEvent error: " + e.getMessage());
        }
    }
}