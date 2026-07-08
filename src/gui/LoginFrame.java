package gui;

import client.Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * FIRST WINDOW the user sees.
 * Has two tabs: Login and Register.
 *
 * KEY SWING CONCEPTS USED HERE:
 *   JFrame          = the window
 *   JPanel          = invisible container for grouping
 *   JTabbedPane     = tab switcher (Login / Register)
 *   JTextField      = text input box
 *   JPasswordField  = password box (shows ***)
 *   JButton         = clickable button
 *   GridBagLayout   = flexible grid for form layout
 *   SwingWorker     = run code in background without freezing GUI
 */
public class LoginFrame extends JFrame {

    // ── Colors ────────────────────────────────────────────────
    private static final Color BG      = new Color(30, 30, 46);
    private static final Color PANEL   = new Color(49, 50, 68);
    private static final Color ACCENT  = new Color(137, 180, 250);
    private static final Color TEXT    = new Color(205, 214, 244);
    private static final Color BTN_BG  = new Color(180, 190, 254);
    private static final Color BTN_FG  = new Color(30, 30, 46);
    private static final Color RED     = new Color(243, 139, 168);
    private static final Color GREEN   = new Color(166, 227, 161);

    // ── Components ────────────────────────────────────────────
    private final JTabbedPane   tabbedPane;
    private final JTextField     loginUserField;
    private final JPasswordField loginPassField;
    private final JButton        loginButton;
    private final JLabel         loginStatus;

    private final JTextField     regUserField;
    private final JPasswordField regPassField;
    private final JTextField     regEmailField;
    private final JButton        registerButton;
    private final JLabel         regStatus;

    private final Client client;

    public LoginFrame(Client client) {
        this.client = client;

        setTitle("ChatApp — Login");
        setSize(420, 390);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        // ── Header ────────────────────────────────────────────
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(BG);
        header.setBorder(new EmptyBorder(20, 0, 10, 0));

        JLabel title = new JLabel("💬 ChatApp", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 24));
        title.setForeground(ACCENT);

        JLabel subtitle = new JLabel("Real-Time Multi-User Chat", SwingConstants.CENTER);
        subtitle.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        subtitle.setForeground(TEXT);

        header.add(title,    BorderLayout.CENTER);
        header.add(subtitle, BorderLayout.SOUTH);

        // ── LOGIN TAB ─────────────────────────────────────────
        JPanel loginPanel = new JPanel(new GridBagLayout());
        loginPanel.setBackground(PANEL);
        loginPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.3;
        loginPanel.add(label("Username:", TEXT), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        loginUserField = textField();
        loginPanel.add(loginUserField, gbc);

        gbc.gridx = 0; gbc.gridy = 1; gbc.weightx = 0.3;
        loginPanel.add(label("Password:", TEXT), gbc);
        gbc.gridx = 1; gbc.weightx = 0.7;
        loginPassField = new JPasswordField(15);
        styleField(loginPassField);
        loginPanel.add(loginPassField, gbc);

        gbc.gridx = 0; gbc.gridy = 2; gbc.gridwidth = 2;
        loginButton = button("🔐 Login", BTN_BG, BTN_FG);
        loginPanel.add(loginButton, gbc);

        gbc.gridy = 3;
        loginStatus = new JLabel(" ", SwingConstants.CENTER);
        loginStatus.setForeground(RED);
        loginPanel.add(loginStatus, gbc);

        // ── REGISTER TAB ──────────────────────────────────────
        JPanel regPanel = new JPanel(new GridBagLayout());
        regPanel.setBackground(PANEL);
        regPanel.setBorder(new EmptyBorder(20, 30, 10, 30));

        GridBagConstraints rgbc = new GridBagConstraints();
        rgbc.insets = new Insets(6, 6, 6, 6);
        rgbc.fill   = GridBagConstraints.HORIZONTAL;

        rgbc.gridx = 0; rgbc.gridy = 0; rgbc.weightx = 0.3;
        regPanel.add(label("Username:", TEXT), rgbc);
        rgbc.gridx = 1; rgbc.weightx = 0.7;
        regUserField = textField();
        regPanel.add(regUserField, rgbc);

        rgbc.gridx = 0; rgbc.gridy = 1; rgbc.weightx = 0.3;
        regPanel.add(label("Password:", TEXT), rgbc);
        rgbc.gridx = 1; rgbc.weightx = 0.7;
        regPassField = new JPasswordField(15);
        styleField(regPassField);
        regPanel.add(regPassField, rgbc);

        rgbc.gridx = 0; rgbc.gridy = 2; rgbc.weightx = 0.3;
        regPanel.add(label("Email:", TEXT), rgbc);
        rgbc.gridx = 1; rgbc.weightx = 0.7;
        regEmailField = textField();
        regPanel.add(regEmailField, rgbc);

        rgbc.gridx = 0; rgbc.gridy = 3; rgbc.gridwidth = 2;
        registerButton = button("✅ Register", BTN_BG, BTN_FG);
        regPanel.add(registerButton, rgbc);

        rgbc.gridy = 4;
        regStatus = new JLabel(" ", SwingConstants.CENTER);
        regStatus.setForeground(GREEN);
        regPanel.add(regStatus, rgbc);

        // ── Tabs ──────────────────────────────────────────────
        tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(PANEL);
        tabbedPane.setForeground(TEXT);
        tabbedPane.addTab("  Login  ",    loginPanel);
        tabbedPane.addTab("  Register  ", regPanel);

        // ── Assemble ──────────────────────────────────────────
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(BG);
        main.add(header,     BorderLayout.NORTH);
        main.add(tabbedPane, BorderLayout.CENTER);
        setContentPane(main);

        // ── Button Actions ────────────────────────────────────
        loginButton.addActionListener(e -> doLogin());
        registerButton.addActionListener(e -> doRegister());
        loginPassField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) doLogin();
            }
        });

        setVisible(true);
    }

    // ── Login logic (runs in background thread via SwingWorker) ──
    private void doLogin() {
        String user = loginUserField.getText().trim();
        String pass = new String(loginPassField.getPassword()).trim();
        if (user.isEmpty() || pass.isEmpty()) {
            loginStatus.setText("Please fill in both fields.");
            return;
        }
        loginButton.setEnabled(false);
        loginStatus.setForeground(ACCENT);
        loginStatus.setText("Connecting...");

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                if (!client.isConnected()) client.connect();
                return client.login(user, pass);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        loginStatus.setText("✓ Success!");
                        dispose();
                        new ChatFrame(client);
                    } else {
                        loginStatus.setForeground(RED);
                        loginStatus.setText("❌ Invalid username or password.");
                        loginButton.setEnabled(true);
                    }
                } catch (Exception ex) {
                    loginStatus.setText("Error: " + ex.getMessage());
                    loginButton.setEnabled(true);
                }
            }
        }.execute();
    }

    // ── Register logic ────────────────────────────────────────
    private void doRegister() {
        String user  = regUserField.getText().trim();
        String pass  = new String(regPassField.getPassword()).trim();
        String email = regEmailField.getText().trim();

        if (user.isEmpty() || pass.isEmpty() || email.isEmpty()) {
            regStatus.setForeground(RED);
            regStatus.setText("All fields are required.");
            return;
        }
        if (pass.length() < 4) {
            regStatus.setForeground(RED);
            regStatus.setText("Password must be at least 4 characters.");
            return;
        }

        registerButton.setEnabled(false);
        regStatus.setForeground(ACCENT);
        regStatus.setText("Registering...");

        new SwingWorker<Boolean, Void>() {
            @Override protected Boolean doInBackground() {
                if (!client.isConnected()) client.connect();
                return client.register(user, pass, email);
            }
            @Override protected void done() {
                try {
                    if (get()) {
                        regStatus.setForeground(GREEN);
                        regStatus.setText("✓ Registered! Please login.");
                        tabbedPane.setSelectedIndex(0);
                        loginUserField.setText(user);
                    } else {
                        regStatus.setForeground(RED);
                        regStatus.setText("❌ Username already exists.");
                    }
                } catch (Exception ex) {
                    regStatus.setText("Error: " + ex.getMessage());
                }
                registerButton.setEnabled(true);
            }
        }.execute();
    }

    // ── UI helpers ────────────────────────────────────────────
    private JLabel label(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private JTextField textField() {
        JTextField tf = new JTextField(15);
        styleField(tf);
        return tf;
    }

    private void styleField(JTextField tf) {
        tf.setBackground(new Color(69, 71, 90));
        tf.setForeground(TEXT);
        tf.setCaretColor(TEXT);
        tf.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        tf.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(ACCENT, 1),
                BorderFactory.createEmptyBorder(4, 8, 4, 8)));
    }

    private JButton button(String text, Color bg, Color fg) {
        JButton btn = new JButton(text);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFont(new Font("Segoe UI", Font.BOLD, 13));
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 16, 8, 16));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return btn;
    }
}