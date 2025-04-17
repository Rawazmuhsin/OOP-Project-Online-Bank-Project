package bank.pr;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;

public class UserProfile extends JFrame {

    public UserProfile() {
        setTitle("Online Banking - Profile");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createProfileContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(26, 32, 44)); // #1a202c
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(250, 800));
        sidebarPanel.setLayout(null);

        // Sidebar title
        JLabel titleLabel = new JLabel("Online Banking");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 200, 30);
        sidebarPanel.add(titleLabel);

        // Active sidebar button (Profile)
        RoundedButton profileButton = new RoundedButton("Profile", 5);
        profileButton.setBackground(new Color(52, 58, 64)); // #343a40
        profileButton.setForeground(Color.WHITE);
        profileButton.setFont(new Font("Arial", Font.PLAIN, 16));
        profileButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        profileButton.setBounds(20, 100, 210, 40);
        profileButton.setHorizontalAlignment(SwingConstants.LEFT);
        sidebarPanel.add(profileButton);

        // Other sidebar buttons
        String[] menuItems = {"Dashboard", "Accounts", "Transactions", "Transfer", "Settings"};
        int yPos = 180;
        for (String item : menuItems) {
            JLabel menuLabel = new JLabel(item);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setBounds(60, yPos, 200, 30);
            
            // Make labels clickable like buttons
            menuLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            menuLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    JOptionPane.showMessageDialog(null, "Navigating to " + item);
                    dispose();
                    // Here you would normally open the appropriate screen
                   
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    menuLabel.setForeground(new Color(200, 200, 200));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    menuLabel.setForeground(Color.WHITE);
                }
            });
            
            sidebarPanel.add(menuLabel);
            yPos += 40;
        }

        return sidebarPanel;
    }

    private JPanel createProfileContentPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(240, 244, 248)); // #f0f4f8
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // Main content area (white rounded rectangle)
        RoundedPanel contentPanel = new RoundedPanel(10);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(20, 20, 530, 740);
        contentPanel.setLayout(null);
        mainPanel.add(contentPanel);

        // User Profile Header
        JLabel titleLabel = new JLabel("User Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64)); // #343a40
        titleLabel.setBounds(30, 30, 200, 30);
        contentPanel.add(titleLabel);

        // Profile Photo Placeholder
        JPanel photoPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(235, 237, 239)); // #ebedef
                g2.fillOval(0, 0, getWidth(), getHeight());
            }
        };
        photoPanel.setBounds(350, 70, 120, 120);
        contentPanel.add(photoPanel);

        JLabel photoLabel = new JLabel("Profile Photo", SwingConstants.CENTER);
        photoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        photoLabel.setForeground(new Color(134, 144, 156)); // #86909c
        photoLabel.setBounds(350, 200, 120, 20);
        contentPanel.add(photoLabel);

        // Profile Fields
        String[] fieldLabels = {"Name", "Email", "Phone", "Address"};
        String[] fieldValues = {
            "Alex Johnson", 
            "alex.johnson@example.com", 
            "(555) 123-4567", 
            "123 Main Street\nNew York, NY 10001"
        };
        int[] yPositions = {250, 320, 390, 460};
        boolean[] isMultiline = {false, false, false, true};

        for (int i = 0; i < fieldLabels.length; i++) {
            // Field Label
            JLabel fieldLabel = new JLabel(fieldLabels[i]);
            fieldLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            fieldLabel.setForeground(new Color(52, 58, 64)); // #343a40
            fieldLabel.setBounds(30, yPositions[i], 100, 20);
            contentPanel.add(fieldLabel);

            // Field Value
            if (isMultiline[i]) {
                JTextArea fieldValue = new JTextArea(fieldValues[i]);
                fieldValue.setFont(new Font("Arial", Font.PLAIN, 14));
                fieldValue.setForeground(new Color(52, 58, 64)); // #343a40
                fieldValue.setBounds(30, yPositions[i] + 20, 470, 60);
                fieldValue.setBackground(Color.WHITE);
                fieldValue.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239), 1));
                fieldValue.setEditable(false);
                contentPanel.add(fieldValue);
            } else {
                JTextField fieldValue = new JTextField(fieldValues[i]);
                fieldValue.setFont(new Font("Arial", Font.PLAIN, 14));
                fieldValue.setForeground(new Color(52, 58, 64)); // #343a40
                fieldValue.setBounds(30, yPositions[i] + 20, 470, 30);
                fieldValue.setBackground(Color.WHITE);
                fieldValue.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239), 1));
                fieldValue.setEditable(false);
                contentPanel.add(fieldValue);
            }
        }

        // Action Buttons
        RoundedButton editButton = new RoundedButton("Edit Profile", 5);
        editButton.setBackground(new Color(13, 110, 253)); // #0d6efd
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.PLAIN, 14));
        editButton.setBounds(30, 600, 160, 30);
        editButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Edit Profile clicked!"));
        contentPanel.add(editButton);

        RoundedButton changePasswordButton = new RoundedButton("Change Password", 5);
        changePasswordButton.setBackground(new Color(235, 237, 239)); // #ebedef
        changePasswordButton.setForeground(new Color(13, 110, 253)); // #0d6efd
        changePasswordButton.setFont(new Font("Arial", Font.PLAIN, 14));
        changePasswordButton.setBounds(220, 600, 160, 30);
        changePasswordButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Change Password clicked!"));
        contentPanel.add(changePasswordButton);

        RoundedButton deleteButton = new RoundedButton("Delete Account", 5);
        deleteButton.setBackground(new Color(255, 204, 204)); // #ffcccc
        deleteButton.setForeground(new Color(220, 53, 69)); // #dc3545
        deleteButton.setFont(new Font("Arial", Font.PLAIN, 14));
        deleteButton.setBounds(30, 660, 470, 30);
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete your account?", 
                "Confirm Deletion", 
                JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Account deletion requested!");
            }
        });
        contentPanel.add(deleteButton);

        return mainPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserProfile gui = new UserProfile();
            gui.setVisible(true);
        });
    }

    // Custom Rounded Panel class
    static class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int cornerRadius) {
            super();
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    // Custom Rounded Button class
    static class RoundedButton extends JButton {
        private int cornerRadius;

        public RoundedButton(String text, int cornerRadius) {
            super(text);
            this.cornerRadius = cornerRadius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}