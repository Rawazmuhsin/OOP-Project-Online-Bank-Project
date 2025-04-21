package bank.pr;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class UserProfile extends JFrame {
    
    private String userName = ""; // Will be set via setUserInfo
    private int userId = 0; // Will be set via setUserInfo
    
    // UI components that need to be updated with real data
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressField;
    
    // Edit mode tracking
    private boolean editMode = false;
    private RoundedButton editButton;
    private RoundedButton saveButton;

    public UserProfile() {
        setTitle(" Kurdish - O - Banking (KOB) - Profile");
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
        JLabel titleLabel = new JLabel("Kurdish - O - Banking");
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
        String[] menuItems = {"Dashboard", "Balance", "Transactions", "Transfer", "Withdraw", "Deposit"};
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
                    handleNavigation(item);
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
        String[] fieldLabels = {"Username", "Email", "Phone", "Account Info"};
        String[] fieldValues = {"", "", "", ""}; // Empty values initially
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
                
                // Save reference to address field
                if (i == 3) {
                    addressField = fieldValue;
                }
            } else {
                JTextField fieldValue = new JTextField(fieldValues[i]);
                fieldValue.setFont(new Font("Arial", Font.PLAIN, 14));
                fieldValue.setForeground(new Color(52, 58, 64)); // #343a40
                fieldValue.setBounds(30, yPositions[i] + 20, 470, 30);
                fieldValue.setBackground(Color.WHITE);
                fieldValue.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239), 1));
                fieldValue.setEditable(false);
                contentPanel.add(fieldValue);
                
                // Save references to other fields
                if (i == 0) {
                    nameField = fieldValue;
                } else if (i == 1) {
                    emailField = fieldValue;
                } else if (i == 2) {
                    phoneField = fieldValue;
                }
            }
        }

        // Action Buttons
        editButton = new RoundedButton("Edit Profile", 5);
        editButton.setBackground(new Color(13, 110, 253)); // #0d6efd
        editButton.setForeground(Color.WHITE);
        editButton.setFont(new Font("Arial", Font.PLAIN, 14));
        editButton.setBounds(30, 600, 160, 30);
        editButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                toggleEditMode();
            }
        });
        contentPanel.add(editButton);
        
        // Save button (initially hidden)
        saveButton = new RoundedButton("Save Changes", 5);
        saveButton.setBackground(new Color(40, 167, 69)); // Green
        saveButton.setForeground(Color.WHITE);
        saveButton.setFont(new Font("Arial", Font.PLAIN, 14));
        saveButton.setBounds(30, 600, 160, 30);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                saveChanges();
            }
        });
        saveButton.setVisible(false);
        contentPanel.add(saveButton);

        RoundedButton changePasswordButton = new RoundedButton("Change Password", 5);
        changePasswordButton.setBackground(new Color(235, 237, 239)); // #ebedef
        changePasswordButton.setForeground(new Color(13, 110, 253)); // #0d6efd
        changePasswordButton.setFont(new Font("Arial", Font.PLAIN, 14));
        changePasswordButton.setBounds(220, 600, 160, 30);
        changePasswordButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openChangePasswordScreen();
            }
        });
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
    
    // Method to open the change password screen
    private void openChangePasswordScreen() {
        SwingUtilities.invokeLater(() -> {
            ChangePassword changePasswordScreen = new ChangePassword(userName, userId);
            changePasswordScreen.setVisible(true);
            this.dispose(); // Close the profile screen
        });
    }
    
    // Toggle edit mode
    private void toggleEditMode() {
        editMode = !editMode;
        
        // Toggle field editability
        nameField.setEditable(editMode);
        emailField.setEditable(editMode);
        phoneField.setEditable(editMode);
        
        // Change background color to indicate editable fields
        if (editMode) {
            nameField.setBackground(new Color(255, 255, 220)); // Light yellow
            emailField.setBackground(new Color(255, 255, 220));
            phoneField.setBackground(new Color(255, 255, 220));
            
            // Show save button, hide edit button
            saveButton.setVisible(true);
            editButton.setVisible(false);
        } else {
            nameField.setBackground(Color.WHITE);
            emailField.setBackground(Color.WHITE);
            phoneField.setBackground(Color.WHITE);
            
            // Show edit button, hide save button
            editButton.setVisible(true);
            saveButton.setVisible(false);
        }
    }
    
    // Save changes to database
    private void saveChanges() {
        String newUsername = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newPhone = phoneField.getText().trim();
        
        // Validate input
        if (newUsername.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Username cannot be empty", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if username contains spaces
        if (newUsername.contains(" ")) {
            JOptionPane.showMessageDialog(this, "Username cannot contain spaces", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Validate email format (simple check)
        if (!newEmail.isEmpty() && !newEmail.contains("@")) {
            JOptionPane.showMessageDialog(this, "Please enter a valid email address", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Update database
        try (Connection conn = DatabaseConnection.getConnection()) {
            // First, check if username already exists (if changed)
            if (!newUsername.equals(userName)) {
                String checkQuery = "SELECT COUNT(*) FROM accounts WHERE username = ? AND account_id != ?";
                PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
                checkStmt.setString(1, newUsername);
                checkStmt.setInt(2, userId);
                ResultSet rs = checkStmt.executeQuery();
                
                if (rs.next() && rs.getInt(1) > 0) {
                    JOptionPane.showMessageDialog(this, 
                            "Username '" + newUsername + "' is already taken. Please choose another.", 
                            "Username Conflict", 
                            JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // Perform update
            String updateQuery = "UPDATE accounts SET username = ?, email = ?, phone = ? WHERE account_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setString(1, newUsername);
            updateStmt.setString(2, newEmail);
            updateStmt.setString(3, newPhone);
            updateStmt.setInt(4, userId);
            
            int rowsAffected = updateStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, 
                        "Profile updated successfully!", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Update current username variable
                userName = newUsername;
                
                // Update window title with username
                setTitle(" Kurdish - O - Banking (KOB) - Profile: " + userName);
                
                // Exit edit mode
                toggleEditMode();
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to update profile. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                    "Database error: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Method to set user info and load data from database
    public void setUserInfo(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        
        // Load user data from database
        loadUserData();
    }
    
    // Method to load user data from database
    private void loadUserData() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT username, email, phone, account_number FROM accounts WHERE account_id = ? OR username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, userName);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Get user data from result set
                String username = rs.getString("username");
                String email = rs.getString("email");
                String phone = rs.getString("phone");
                String accountNumber = rs.getString("account_number");
                
                // Update username if needed
                if (!username.equals(userName)) {
                    userName = username;
                }
                
                // Set data to UI fields
                nameField.setText(username);
                emailField.setText(email != null ? email : "");
                phoneField.setText(phone != null ? phone : "");
                
                // Set address field - using account number as an example of additional information
                String address = "Account #: " + (accountNumber != null ? accountNumber : "N/A") + "\n";
                address += "User ID: " + userId;
                addressField.setText(address);
                
                // Update window title with username
                setTitle(" Kurdish - O - Banking (KOB) - Profile: " + username);
            } else {
                // User not found
                JOptionPane.showMessageDialog(this, 
                        "User information not found in database.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                    "Database error: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Method to handle navigation between pages
    private void handleNavigation(String destination) {
        System.out.println("Navigating to: " + destination);
        this.dispose(); // Close current window

        // Open the selected page
        switch (destination) {
            case "Dashboard":
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                });
                break;
            case "Balance":
                SwingUtilities.invokeLater(() -> {
                    BalancePage balancePage = new BalancePage(userName, userId);
                    balancePage.setVisible(true);
                });
                break;
            case "Transactions":
                SwingUtilities.invokeLater(() -> {
                    Transaction transactionScreen = new Transaction(userId, userName);
                    transactionScreen.setVisible(true);
                });
                break;
            case "Transfer":
                SwingUtilities.invokeLater(() -> {
                    Transfer transferScreen = new Transfer();
                    transferScreen.setUserInfo(userName, userId);
                    transferScreen.setVisible(true);
                });
                break;
            case "Withdraw":
                SwingUtilities.invokeLater(() -> {
                    Withdraw withdrawScreen = new Withdraw();
                    withdrawScreen.setUserInfo(userName, userId);
                    withdrawScreen.setVisible(true);
                });
                break;
            case "Deposit":
                SwingUtilities.invokeLater(() -> {
                    Deposite depositScreen = new Deposite();
                    depositScreen.setUserInfo(userName, userId);
                    depositScreen.setVisible(true);
                });
                break;
            default:
                System.out.println("Navigation to " + destination + " not implemented");
                break;
        }
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