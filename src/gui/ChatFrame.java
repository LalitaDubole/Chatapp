package gui;

import client.Client;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ChatFrame extends JFrame {

    private static final Color BG_DARK  = new Color(25, 25, 35);
    private static final Color BG_MID   = new Color(35, 36, 52);
    private static final Color BG_INPUT = new Color(60, 62, 86);
    private static final Color ACCENT   = new Color(114, 137, 218);
    private static final Color TEXT     = new Color(220, 221, 222);
    private static final Color TEXT_SUB = new Color(148, 155, 164);
    private static final Color GREEN    = new Color(87, 242, 135);

    private static final Color[] AVATAR_COLORS = {
            new Color(114, 137, 218),
            new Color(87, 242, 135),
            new Color(255, 115, 179),
            new Color(254, 231, 92),
            new Color(155, 89, 182),
            new Color(255, 149, 0),
            new Color(52, 199, 89)
    };

    private JTextPane chatArea;
    private JTextField messageInput;
    private DefaultListModel<String> userListModel;
    private final Client client;
    private final DateTimeFormatter timeFormat =
            DateTimeFormatter.ofPattern("hh:mm a");
    private final DateTimeFormatter dateFormat =
            DateTimeFormatter.ofPattern("dd MMM yyyy");

    public ChatFrame(Client client) {
        this.client = client;

        setTitle("💬 ChatApp — " + client.getUsername());
        setSize(1000, 650);
        setMinimumSize(new Dimension(750, 500));
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel root = new JPanel(new BorderLayout());
        root.add(buildSidebar(),  BorderLayout.WEST);
        root.add(buildMainChat(), BorderLayout.CENTER);
        setContentPane(root);

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                int c = JOptionPane.showConfirmDialog(
                        ChatFrame.this,
                        "Disconnect and exit?",
                        "Exit ChatApp",
                        JOptionPane.YES_NO_OPTION);
                if (c == JOptionPane.YES_OPTION) {
                    client.disconnect();
                    System.exit(0);
                }
            }
        });

        setVisible(true);
        client.startListening(this);
        appendMessage("SERVER", "Welcome " +
                client.getUsername() + "! You are now online 🟢");
    }

    private JPanel buildSidebar() {
        Color sidebarBg = new Color(20, 20, 30);

        JPanel logoPanel = new JPanel(new GridLayout(2, 1));
        logoPanel.setBackground(sidebarBg);
        logoPanel.setBorder(new EmptyBorder(16, 14, 12, 14));

        JLabel logo = new JLabel("💬 ChatApp");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 16));
        logo.setForeground(ACCENT);

        JLabel sub = new JLabel("General Chat");
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        sub.setForeground(TEXT_SUB);

        logoPanel.add(logo);
        logoPanel.add(sub);

        JLabel sectionLabel = new JLabel("  ONLINE MEMBERS");
        sectionLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        sectionLabel.setForeground(TEXT_SUB);
        sectionLabel.setBackground(sidebarBg);
        sectionLabel.setOpaque(true);
        sectionLabel.setBorder(new EmptyBorder(10, 10, 6, 10));

        userListModel = new DefaultListModel<>();
        JList<String> userList = new JList<>(userListModel);
        userList.setBackground(sidebarBg);
        userList.setFixedCellHeight(42);
        userList.setCellRenderer(new AvatarUserRenderer());

        JScrollPane usersScroll = new JScrollPane(userList);
        usersScroll.setBorder(BorderFactory.createEmptyBorder());
        usersScroll.setBackground(sidebarBg);

        // My user panel
        JPanel myPanel = new JPanel(new BorderLayout(8, 0));
        myPanel.setBackground(new Color(15, 15, 22));
        myPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        String name = client.getUsername();

        JLabel myAvatar = new JLabel(
                String.valueOf(name.charAt(0)).toUpperCase(),
                SwingConstants.CENTER);
        myAvatar.setFont(new Font("Segoe UI", Font.BOLD, 13));
        myAvatar.setForeground(Color.WHITE);
        myAvatar.setOpaque(true);
        myAvatar.setBackground(getAvatarColor(name));
        myAvatar.setPreferredSize(new Dimension(32, 32));

        JLabel myName = new JLabel(name);
        myName.setFont(new Font("Segoe UI", Font.BOLD, 12));
        myName.setForeground(TEXT);

        JLabel myStatus = new JLabel("🟢 Online");
        myStatus.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        myStatus.setForeground(GREEN);

        JPanel nameStatus = new JPanel(new GridLayout(2, 1));
        nameStatus.setBackground(new Color(15, 15, 22));
        nameStatus.add(myName);
        nameStatus.add(myStatus);

        JButton logoutBtn = new JButton("⏻");
        logoutBtn.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        logoutBtn.setForeground(TEXT_SUB);
        logoutBtn.setBackground(new Color(15, 15, 22));
        logoutBtn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
        logoutBtn.setFocusPainted(false);
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        logoutBtn.setToolTipText("Logout");
        logoutBtn.addActionListener(e -> {
            client.disconnect();
            dispose();
            SwingUtilities.invokeLater(() -> new LoginFrame(new Client()));
        });

        myPanel.add(myAvatar,   BorderLayout.WEST);
        myPanel.add(nameStatus, BorderLayout.CENTER);
        myPanel.add(logoutBtn,  BorderLayout.EAST);

        JPanel listArea = new JPanel(new BorderLayout());
        listArea.setBackground(sidebarBg);
        listArea.add(sectionLabel, BorderLayout.NORTH);
        listArea.add(usersScroll,  BorderLayout.CENTER);
        listArea.add(myPanel,      BorderLayout.SOUTH);

        JPanel sidebar = new JPanel(new BorderLayout());
        sidebar.setBackground(sidebarBg);
        sidebar.setPreferredSize(new Dimension(200, 0));
        sidebar.setBorder(BorderFactory.createMatteBorder(
                0, 0, 0, 1, new Color(60, 62, 86)));
        sidebar.add(logoPanel, BorderLayout.NORTH);
        sidebar.add(listArea,  BorderLayout.CENTER);

        return sidebar;
    }

    private JPanel buildMainChat() {
        // Header
        JPanel chatHeader = new JPanel(new BorderLayout());
        chatHeader.setBackground(BG_MID);
        chatHeader.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(
                        0, 0, 1, 0, new Color(60, 62, 86)),
                new EmptyBorder(12, 16, 12, 16)));

        JLabel channelName = new JLabel("# general");
        channelName.setFont(new Font("Segoe UI", Font.BOLD, 16));
        channelName.setForeground(TEXT);

        JLabel dateLabel = new JLabel(
                LocalDateTime.now().format(dateFormat));
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        dateLabel.setForeground(TEXT_SUB);

        chatHeader.add(channelName, BorderLayout.WEST);
        chatHeader.add(dateLabel,   BorderLayout.EAST);

        // Chat area
        chatArea = new JTextPane();
        chatArea.setEditable(false);
        chatArea.setBackground(BG_DARK);
        chatArea.setForeground(TEXT);
        chatArea.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        chatArea.setMargin(new Insets(12, 16, 12, 16));

        JScrollPane chatScroll = new JScrollPane(chatArea);
        chatScroll.setBorder(BorderFactory.createEmptyBorder());
        chatScroll.setVerticalScrollBarPolicy(
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);

        // Input
        messageInput = new JTextField();
        messageInput.setBackground(BG_INPUT);
        messageInput.setForeground(TEXT_SUB);
        messageInput.setCaretColor(TEXT);
        messageInput.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        messageInput.setBorder(BorderFactory.createEmptyBorder(6, 0, 6, 0));
        messageInput.setText("Message #general");

        messageInput.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (messageInput.getText().equals("Message #general")) {
                    messageInput.setText("");
                    messageInput.setForeground(TEXT);
                }
            }
            @Override
            public void focusLost(FocusEvent e) {
                if (messageInput.getText().isEmpty()) {
                    messageInput.setText("Message #general");
                    messageInput.setForeground(TEXT_SUB);
                }
            }
        });

        JButton sendBtn = new JButton("➤");
        sendBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        sendBtn.setForeground(ACCENT);
        sendBtn.setBackground(BG_INPUT);
        sendBtn.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 4));
        sendBtn.setFocusPainted(false);
        sendBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        ActionListener sendAction = e -> {
            String text = messageInput.getText().trim();
            if (!text.isEmpty() && !text.equals("Message #general")) {
                client.sendChatMessage(text);
                messageInput.setText("");
                messageInput.setForeground(TEXT);
                messageInput.requestFocus();
            }
        };
        sendBtn.addActionListener(sendAction);
        messageInput.addActionListener(sendAction);

        // Emoji button
        JButton emojiBtn = new JButton("😊");
        emojiBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        emojiBtn.setBackground(BG_INPUT);
        emojiBtn.setBorder(BorderFactory.createEmptyBorder(4, 0, 4, 8));
        emojiBtn.setFocusPainted(false);
        emojiBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        emojiBtn.addActionListener(e -> {
            String[] emojis = {
                    "😀","😂","😍","🥰","😎","🤩","😭","😡",
                    "👍","❤️","🔥","✨","🎉","🙏","💯","🥳",
                    "😊","🤔","😴","🤯","👀","💪","🫂","🎊"
            };
            JPopupMenu popup = new JPopupMenu();
            popup.setBackground(BG_MID);
            JPanel grid = new JPanel(new GridLayout(3, 8, 2, 2));
            grid.setBackground(BG_MID);
            grid.setBorder(new EmptyBorder(8, 8, 8, 8));
            for (String emoji : emojis) {
                JButton eb = new JButton(emoji);
                eb.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
                eb.setBackground(BG_MID);
                eb.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
                eb.setFocusPainted(false);
                eb.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                eb.addActionListener(ev -> {
                    String cur = messageInput.getText();
                    if (cur.equals("Message #general")) cur = "";
                    messageInput.setText(cur + emoji);
                    messageInput.setForeground(TEXT);
                    messageInput.requestFocus();
                    popup.setVisible(false);
                });
                grid.add(eb);
            }
            popup.add(grid);
            popup.show(emojiBtn, 0, -popup.getPreferredSize().height - 10);
        });

        JPanel inputBox = new JPanel(new BorderLayout(8, 0));
        inputBox.setBackground(BG_INPUT);
        inputBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(80, 82, 110), 1),
                new EmptyBorder(4, 12, 4, 8)));
        inputBox.add(emojiBtn,     BorderLayout.WEST);
        inputBox.add(messageInput, BorderLayout.CENTER);
        inputBox.add(sendBtn,      BorderLayout.EAST);

        JPanel inputWrapper = new JPanel(new BorderLayout());
        inputWrapper.setBackground(BG_DARK);
        inputWrapper.setBorder(new EmptyBorder(0, 16, 16, 16));
        inputWrapper.add(inputBox, BorderLayout.CENTER);

        JPanel mainChat = new JPanel(new BorderLayout());
        mainChat.setBackground(BG_DARK);
        mainChat.add(chatHeader,   BorderLayout.NORTH);
        mainChat.add(chatScroll,   BorderLayout.CENTER);
        mainChat.add(inputWrapper, BorderLayout.SOUTH);

        return mainChat;
    }

    public void appendMessage(String message) {
        String sender  = "SERVER";
        String content = message;

        if (message.startsWith("[") && message.contains("]:")) {
            int end = message.indexOf("]:");
            sender  = message.substring(1, end);
            content = message.substring(end + 2).trim();
        } else if (message.startsWith("SERVER:")) {
            content = message.substring(7).trim();
        }

        appendMessage(sender, content);
    }

    public void appendMessage(String sender, String content) {
        if (chatArea == null) return;
        StyledDocument doc = chatArea.getStyledDocument();
        String time = LocalDateTime.now().format(timeFormat);
        boolean isMe     = sender.equals(client.getUsername());
        boolean isServer = sender.equals("SERVER");

        try {
            if (isServer) {
                SimpleAttributeSet a = new SimpleAttributeSet();
                StyleConstants.setForeground(a, TEXT_SUB);
                StyleConstants.setItalic(a, true);
                StyleConstants.setFontSize(a, 11);
                StyleConstants.setFontFamily(a, "Segoe UI");
                doc.insertString(doc.getLength(),
                        "  — " + content + " —\n\n", a);
                chatArea.setCaretPosition(doc.getLength());
                return;
            }

            // Sender name
            SimpleAttributeSet nameA = new SimpleAttributeSet();
            StyleConstants.setForeground(nameA,
                    isMe ? ACCENT : getAvatarColor(sender));
            StyleConstants.setFontFamily(nameA, "Segoe UI");
            StyleConstants.setFontSize(nameA, 13);
            StyleConstants.setBold(nameA, true);
            doc.insertString(doc.getLength(), sender, nameA);

            // Time
            SimpleAttributeSet timeA = new SimpleAttributeSet();
            StyleConstants.setForeground(timeA, TEXT_SUB);
            StyleConstants.setFontSize(timeA, 10);
            StyleConstants.setFontFamily(timeA, "Segoe UI");
            doc.insertString(doc.getLength(), "  " + time + "\n", timeA);

            // Content
            SimpleAttributeSet msgA = new SimpleAttributeSet();
            StyleConstants.setForeground(msgA,
                    isMe ? new Color(210, 220, 255) : TEXT);
            StyleConstants.setFontFamily(msgA, "Segoe UI");
            StyleConstants.setFontSize(msgA, 14);
            doc.insertString(doc.getLength(),
                    "  " + content + "\n\n", msgA);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
        chatArea.setCaretPosition(doc.getLength());
    }

    public void updateOnlineUsers(String csv) {
        if (userListModel == null) return;
        userListModel.clear();
        if (csv == null || csv.isEmpty()) return;
        for (String u : csv.split(",")) {
            u = u.trim();
            if (!u.isEmpty()) userListModel.addElement(u);
        }
    }

    private Color getAvatarColor(String name) {
        int idx = Math.abs(name.hashCode()) % AVATAR_COLORS.length;
        return AVATAR_COLORS[idx];
    }

    private class AvatarUserRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(
                JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {

            String uname = value.toString();
            JPanel cell = new JPanel(new BorderLayout(10, 0));
            cell.setBackground(isSelected
                    ? new Color(60, 62, 86)
                    : new Color(20, 20, 30));
            cell.setBorder(new EmptyBorder(6, 10, 6, 10));

            JLabel avatar = new JLabel(
                    String.valueOf(uname.charAt(0)).toUpperCase(),
                    SwingConstants.CENTER);
            avatar.setFont(new Font("Segoe UI", Font.BOLD, 12));
            avatar.setForeground(Color.WHITE);
            avatar.setOpaque(true);
            avatar.setBackground(getAvatarColor(uname));
            avatar.setPreferredSize(new Dimension(28, 28));

            JLabel nameLabel = new JLabel(uname);
            nameLabel.setFont(new Font("Segoe UI", Font.PLAIN, 13));
            nameLabel.setForeground(
                    uname.equals(client.getUsername()) ? ACCENT : TEXT);

            JLabel dot = new JLabel("●");
            dot.setForeground(GREEN);
            dot.setFont(new Font("Segoe UI", Font.PLAIN, 8));

            cell.add(avatar,    BorderLayout.WEST);
            cell.add(nameLabel, BorderLayout.CENTER);
            cell.add(dot,       BorderLayout.EAST);

            return cell;
        }
    }
}