package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

 class UserProfile extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Colors scheme - same as Dashbord and Transfer
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    
    // Components
    private String userName = "";
    private int userId = 0;
    private JLabel greeting;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JTextArea addressField;
    private List<JButton> menuButtons = new ArrayList<>();
    private JPanel sidebarPanel;
    
    // Edit mode tracking
    private boolean editMode = false;
    private JButton editButton;
    private JButton saveButton;
    
    // Profile photo components
    private BufferedImage profileImage = null;
    private ProfilePhotoPanel photoPanel;
    private JLabel photoLabel;

    public UserProfile() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Kurdish-O-Banking (KOB) - User Profile");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create components
        createSidebar();
        createHeaderPanel();
        createMainContent();
        
        // Set icon if available
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
    }
    
    private void createSidebar() {
        sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR, 
                    0, getHeight(), new Color(10, 20, 50)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        
        // Logo panel
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        
        JLabel bankName = new JLabel("Kurdish-O-Banking");
        bankName.setForeground(Color.WHITE);
        bankName.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        JLabel tagline = new JLabel("Your Future, Your Bank");
        tagline.setForeground(new Color(200, 200, 200));
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 12));
        
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.add(bankName);
        namePanel.add(Box.createVerticalStrut(3));
        namePanel.add(tagline);
        
        logoPanel.add(namePanel, BorderLayout.CENTER);
        
        // Try to add logo image if available
        try {
            ImageIcon logoIcon = new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png");
            java.awt.Image image = logoIcon.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(image);
            JLabel logoLabel = new JLabel(logoIcon);
            logoPanel.add(logoLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.err.println("Error loading small logo: " + e.getMessage());
        }
        
        sidebarPanel.add(logoPanel);
        
        // Add separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(70, 80, 120));
        separator.setBackground(new Color(70, 80, 120));
        separator.setMaximumSize(new Dimension(220, 1));
        sidebarPanel.add(separator);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Menu items with icons
        String[] menuItems = {"Dashboard", "Balance", "Accounts", "Deposit", "Withdraw", "Transfers", "Transactions",  "QR Codes"};
        String[] iconNames = {"dashboard", "balance", "accounts", "deposit", "withdraw", "transfers", "transactions",  "qrcode"};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton button = createMenuButton(menuItems[i], iconNames[i]);
            
            final String item = menuItems[i];
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleButtonClick(item);
                    updateSelectedButton(item);
                }
            });
            
            sidebarPanel.add(button);
            menuButtons.add(button);
            sidebarPanel.add(Box.createVerticalStrut(5));
        }
        
        // Set Accounts as initially selected
        updateSelectedButton("Accounts");
        
        // Add logout at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        JSeparator bottomSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        bottomSeparator.setForeground(new Color(70, 80, 120));
        bottomSeparator.setBackground(new Color(70, 80, 120));
        bottomSeparator.setMaximumSize(new Dimension(220, 1));
        sidebarPanel.add(bottomSeparator);
        
        JButton logoutButton = createMenuButton("Logout", "logout");
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                // Open login page
                SwingUtilities.invokeLater(() -> {
                    LoginUI loginPage = new LoginUI();
                    loginPage.setVisible(true);
                });
            }
        });
        
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private JButton createMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setMaximumSize(new Dimension(220, 45));
        button.setPreferredSize(new Dimension(220, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
        
        // Try to load icon if available
        try {
            ImageIcon icon = new ImageIcon("icons/" + iconName + ".png");
            java.awt.Image image = icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            button.setIcon(icon);
        } catch (Exception e) {
            // If icon not found, use text only
            System.err.println("Icon not found: " + iconName);
        }
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(new Color(45, 55, 95));
                    button.setContentAreaFilled(true);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setContentAreaFilled(false);
                }
            }
        });
        
        return button;
    }
    
    private void updateSelectedButton(String selectedItem) {
        for (JButton button : menuButtons) {
            if (button.getText().equals(selectedItem)) {
                button.setBackground(SECONDARY_COLOR);
                button.setContentAreaFilled(true);
                button.setFont(new Font("SansSerif", Font.BOLD, 14));
                button.putClientProperty("selected", true);
            } else {
                button.setContentAreaFilled(false);
                button.setFont(new Font("SansSerif", Font.PLAIN, 14));
                button.putClientProperty("selected", false);
            }
        }
    }
    
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        // Greeting panel
        JPanel greetingPanel = new JPanel(new BorderLayout());
        greetingPanel.setOpaque(false);
        
        greeting = new JLabel("Account Profile");
        greeting.setFont(new Font("SansSerif", Font.BOLD, 20));
        greeting.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel("View and manage your account information");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        greetingPanel.add(greeting, BorderLayout.NORTH);
        greetingPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(greetingPanel, BorderLayout.WEST);
        
        // Current date/time panel
        JPanel dateTimePanel = new JPanel(new BorderLayout());
        dateTimePanel.setOpaque(false);
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        
        JLabel dateLabel = new JLabel(now.format(dateFormatter));
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel timeLabel = new JLabel(now.format(timeFormatter));
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timeLabel.setForeground(LIGHT_TEXT_COLOR);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        dateTimePanel.add(dateLabel, BorderLayout.NORTH);
        dateTimePanel.add(timeLabel, BorderLayout.SOUTH);
        
        headerPanel.add(dateTimePanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createMainContent() {
        // Main content panel with ScrollPane for responsiveness
        JPanel mainContentPanel = new JPanel();
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        mainContentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Profile Info Panel
        JPanel profileInfoPanel = createSectionPanel("Profile Information");
        profileInfoPanel.setLayout(new BorderLayout(20, 0));
        
        // Photo panel on the left
        JPanel photoContainerPanel = new JPanel();
        photoContainerPanel.setLayout(new BoxLayout(photoContainerPanel, BoxLayout.Y_AXIS));
        photoContainerPanel.setOpaque(false);
        photoContainerPanel.setBorder(BorderFactory.createEmptyBorder(25, 25, 25, 15));
        photoContainerPanel.setPreferredSize(new Dimension(200, 300));
        
        // Create profile photo panel with fixed size
        photoPanel = new ProfilePhotoPanel();
        photoPanel.setPreferredSize(new Dimension(150, 150));
        photoPanel.setMinimumSize(new Dimension(150, 150)); // Set minimum size to prevent zero width/height
        photoPanel.setMaximumSize(new Dimension(150, 150));
        photoPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        photoLabel = new JLabel("Profile Photo", SwingConstants.CENTER);
        photoLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        photoLabel.setForeground(LIGHT_TEXT_COLOR);
        photoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton uploadButton = new JButton("Upload Photo");
        uploadButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        uploadButton.setBackground(SECONDARY_COLOR);
        uploadButton.setForeground(Color.WHITE);
        uploadButton.setFocusPainted(false);
        uploadButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        uploadButton.setOpaque(true);  
        uploadButton.setBorderPainted(false);  
        uploadButton.setMaximumSize(new Dimension(150, 30));
        uploadButton.addActionListener(e -> uploadProfilePhoto());
        
        photoContainerPanel.add(Box.createVerticalStrut(10));
        photoContainerPanel.add(photoPanel);
        photoContainerPanel.add(Box.createVerticalStrut(15));
        photoContainerPanel.add(photoLabel);
        photoContainerPanel.add(Box.createVerticalStrut(15));
        photoContainerPanel.add(uploadButton);
        photoContainerPanel.add(Box.createVerticalGlue());
        
        // Profile fields on the right
        JPanel fieldPanel = new JPanel();
        fieldPanel.setOpaque(false);
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 25));
        
        // Create form fields with labels
        fieldPanel.add(createFormField("Username", nameField = new JTextField(), false));
        fieldPanel.add(Box.createVerticalStrut(20));
        fieldPanel.add(createFormField("Email", emailField = new JTextField(), false));
        fieldPanel.add(Box.createVerticalStrut(20));
        fieldPanel.add(createFormField("Phone", phoneField = new JTextField(), false));
        fieldPanel.add(Box.createVerticalStrut(20));
        
        // Account info field (read-only)
        addressField = new JTextArea();
        addressField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        addressField.setEditable(false);
        addressField.setRows(3);
        addressField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        
        JPanel accountInfoPanel = new JPanel();
        accountInfoPanel.setLayout(new BorderLayout(0, 5));
        accountInfoPanel.setOpaque(false);
        accountInfoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        accountInfoPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 120));
        
        JLabel accountInfoLabel = new JLabel("Account Information");
        accountInfoLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        accountInfoLabel.setForeground(TEXT_COLOR);
        
        accountInfoPanel.add(accountInfoLabel, BorderLayout.NORTH);
        accountInfoPanel.add(addressField, BorderLayout.CENTER);
        
        fieldPanel.add(accountInfoPanel);
        
        // Add buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        buttonPanel.setOpaque(false);
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        editButton = new JButton("Edit Profile");
        editButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        editButton.setBackground(SECONDARY_COLOR);
        editButton.setForeground(Color.WHITE);
        editButton.setFocusPainted(false);
        editButton.addActionListener(e -> toggleEditMode());
        editButton.setOpaque(true);  
        editButton.setBorderPainted(false); 
        
        saveButton = new JButton("Save Changes");
        saveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        saveButton.setBackground(SUCCESS_COLOR);
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setVisible(false);
        saveButton.addActionListener(e -> saveChanges());
        
        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        changePasswordButton.setBackground(new Color(240, 240, 240));
        changePasswordButton.setForeground(TEXT_COLOR);
        changePasswordButton.setFocusPainted(false);
        changePasswordButton.addActionListener(e -> openChangePasswordScreen());
        
        buttonPanel.add(editButton);
        buttonPanel.add(saveButton);
        buttonPanel.add(changePasswordButton);
        
        fieldPanel.add(Box.createVerticalStrut(20));
        fieldPanel.add(buttonPanel);
        
        // Add photo and field panels to profile info panel
        profileInfoPanel.add(photoContainerPanel, BorderLayout.WEST);
        profileInfoPanel.add(fieldPanel, BorderLayout.CENTER);
        
        // Account Actions Panel
        JPanel accountActionsPanel = createSectionPanel("Account Management");
        accountActionsPanel.setLayout(new BorderLayout());
        
        JPanel actionsContent = new JPanel();
        actionsContent.setLayout(new BoxLayout(actionsContent, BoxLayout.Y_AXIS));
        actionsContent.setOpaque(false);
        actionsContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Security settings section
        JPanel securityPanel = new JPanel(new BorderLayout(0, 10));
        securityPanel.setOpaque(false);
        securityPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel securityTitle = new JLabel("Security Settings");
        securityTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        securityTitle.setForeground(TEXT_COLOR);
        
        JLabel securityDesc = new JLabel("Manage your account security settings");
        securityDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        securityDesc.setForeground(LIGHT_TEXT_COLOR);
        
        JPanel securityLabelPanel = new JPanel(new BorderLayout());
        securityLabelPanel.setOpaque(false);
        securityLabelPanel.add(securityTitle, BorderLayout.NORTH);
        securityLabelPanel.add(securityDesc, BorderLayout.SOUTH);
        
        securityPanel.add(securityLabelPanel, BorderLayout.NORTH);
        
        // Security buttons
        JPanel securityButtonsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        securityButtonsPanel.setOpaque(false);
        
        JButton passwordButton = new JButton("Change Password");
        passwordButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        passwordButton.setBackground(new Color(240, 240, 240));
        passwordButton.setForeground(TEXT_COLOR);
        passwordButton.setFocusPainted(false);
        passwordButton.addActionListener(e -> openChangePasswordScreen());
        
        JButton securityQuestionsButton = new JButton("Security Questions");
        securityQuestionsButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        securityQuestionsButton.setBackground(new Color(240, 240, 240));
        securityQuestionsButton.setForeground(TEXT_COLOR);
        securityQuestionsButton.setFocusPainted(false);
        
        securityButtonsPanel.add(passwordButton);
        securityButtonsPanel.add(securityQuestionsButton);
        
        securityPanel.add(securityButtonsPanel, BorderLayout.CENTER);
        
        // Account deletion section
        JPanel deletionPanel = new JPanel(new BorderLayout(0, 10));
        deletionPanel.setOpaque(false);
        deletionPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JLabel deletionTitle = new JLabel("Delete Account");
        deletionTitle.setFont(new Font("SansSerif", Font.BOLD, 16));
        deletionTitle.setForeground(ERROR_COLOR);
        
        JLabel deletionDesc = new JLabel("Permanently delete your account and all associated data");
        deletionDesc.setFont(new Font("SansSerif", Font.PLAIN, 14));
        deletionDesc.setForeground(LIGHT_TEXT_COLOR);
        
        JPanel deletionLabelPanel = new JPanel(new BorderLayout());
        deletionLabelPanel.setOpaque(false);
        deletionLabelPanel.add(deletionTitle, BorderLayout.NORTH);
        deletionLabelPanel.add(deletionDesc, BorderLayout.SOUTH);
        
        deletionPanel.add(deletionLabelPanel, BorderLayout.NORTH);
        
        // Delete button
        JButton deleteButton = new JButton("Delete My Account");
        deleteButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        deleteButton.setBackground(new Color(255, 235, 235));
        deleteButton.setForeground(ERROR_COLOR);
        deleteButton.setFocusPainted(false);
        deleteButton.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to delete your account? This action cannot be undone.", 
                "Confirm Account Deletion", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);
            if (confirm == JOptionPane.YES_OPTION) {
                deleteAccount();
            }
        });
        
        JPanel deleteButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        deleteButtonPanel.setOpaque(false);
        deleteButtonPanel.add(deleteButton);
        
        deletionPanel.add(deleteButtonPanel, BorderLayout.CENTER);
        
        // Add sections to account actions content
        actionsContent.add(securityPanel);
        actionsContent.add(new JSeparator());
        actionsContent.add(deletionPanel);
        
        accountActionsPanel.add(actionsContent, BorderLayout.CENTER);
        
        // Add panels to main content
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.6;
        mainContentPanel.add(profileInfoPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        mainContentPanel.add(accountActionsPanel, gbc);
        
        // Add to frame with scroll pane
        add(new JScrollPane(mainContentPanel), BorderLayout.CENTER);
    }
    
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(CARD_COLOR);
        titleBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        panel.add(titleBar, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createFormField(String labelText, JTextField field, boolean required) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 5));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.setMaximumSize(new Dimension(Short.MAX_VALUE, 60));
        
        JLabel label = new JLabel(labelText + (required ? " *" : ""));
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(TEXT_COLOR);
        
        field.setFont(new Font("SansSerif", Font.PLAIN, 14));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        field.setEditable(false);
        
        panel.add(label, BorderLayout.NORTH);
        panel.add(field, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Method to upload profile photo
    private void uploadProfilePhoto() {
        BufferedImage selectedImage = ImageUploader.browseForImage(this);
        
        if (selectedImage != null) {
            // Resize the image to fit our photo panel
            BufferedImage resizedImage = ImageUploader.resizeImage(selectedImage, 150, 150);
            
            // Save the image
            boolean saved = ImageUploader.saveUserImage(userId, resizedImage);
            
            if (saved) {
                // Update the profile photo display
                profileImage = resizedImage;
                photoPanel.setProfileImage(profileImage);
                photoLabel.setText("Profile Photo");
                
                JOptionPane.showMessageDialog(this, 
                        "Profile photo updated successfully!",
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to save profile photo. Please try again.",
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
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
                setTitle("Kurdish-O-Banking (KOB) - Profile: " + userName);
                
                // Update greeting
                greeting.setText("Account Profile - " + userName);
                
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
    
    // Method to delete the account from database
    private void deleteAccount() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Delete the account from the database
            String deleteQuery = "DELETE FROM accounts WHERE account_id = ?";
            PreparedStatement deleteStmt = conn.prepareStatement(deleteQuery);
            deleteStmt.setInt(1, userId);
            
            int rowsAffected = deleteStmt.executeUpdate();
            
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, 
                        "Your account has been successfully deleted.", 
                        "Account Deleted", 
                        JOptionPane.INFORMATION_MESSAGE);
                
                // Return to login screen
                SwingUtilities.invokeLater(() -> {
                    LoginUI loginScreen = new LoginUI();
                    loginScreen.setVisible(true);
                    this.dispose();
                });
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to delete account. Please try again.", 
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
    
    // Method to open the change password screen
    private void openChangePasswordScreen() {
        SwingUtilities.invokeLater(() -> {
            ChangePassword changePasswordScreen = new ChangePassword(userName, userId);
            changePasswordScreen.setVisible(true);
            this.dispose(); // Close the profile screen
        });
    }
    
    // Method to set user info and load data from database
    public void setUserInfo(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        
        // Update window title and greeting
        setTitle("Kurdish-O-Banking (KOB) - Profile: " + userName);
        if (greeting != null) {
            greeting.setText("Account Profile - " + userName);
        }
        
        // Load user data from database
        loadUserData();
        
        // Load profile image first (don't generate default avatar yet)
        loadProfileImage();
    }
    
    // Method to load profile image
    private void loadProfileImage() {
        BufferedImage loadedImage = ImageUploader.loadUserImage(userId);
        
        if (loadedImage != null) {
            profileImage = loadedImage;
            photoPanel.setProfileImage(profileImage);
            photoLabel.setText("Profile Photo");
        } else {
            profileImage = null;
            photoPanel.setProfileImage(null);
            
            // Only create default avatar once we're visible and panel has dimensions
            if (photoPanel.getWidth() > 0 && photoPanel.getHeight() > 0) {
                photoPanel.generateDefaultAvatar();
                photoLabel.setText("Default Avatar");
            } else {
                // Defer default avatar generation until component is sized
                SwingUtilities.invokeLater(() -> {
                    if (photoPanel.isDisplayable() && photoPanel.getWidth() > 0 && photoPanel.getHeight() > 0) {
                        photoPanel.generateDefaultAvatar();
                        photoLabel.setText("Default Avatar");
                    }
                });
            }
        }
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
                setTitle("Kurdish-O-Banking (KOB) - Profile: " + username);
                if (greeting != null) {
                    greeting.setText("Account Profile - " + username);
                }
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
    private void handleButtonClick(String buttonName) {
        System.out.println("Button clicked: " + buttonName);
        
        switch (buttonName) {
            case "Dashboard":
                // Go to Dashboard page
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                    this.dispose();
                });
                break;
            case "Balance":
                // Go to Balance page
                SwingUtilities.invokeLater(() -> {
                    BalancePage balancePage = new BalancePage(userName, userId);
                    balancePage.setVisible(true);
                    this.dispose();
                });
                break;
            case "Accounts":
                // Refresh current page
                SwingUtilities.invokeLater(() -> {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setUserInfo(userName, userId);
                    userProfile.setVisible(true);
                    this.dispose();
                });
                break;
            case "Deposit":
                // Go to Deposit page
                SwingUtilities.invokeLater(() -> {
                    Deposite depositScreen = new Deposite();
                    depositScreen.setUserInfo(userName, userId);
                    depositScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Withdraw":
                // Go to Withdraw page
                SwingUtilities.invokeLater(() -> {
                    Withdraw withdrawScreen = new Withdraw();
                    withdrawScreen.setUserInfo(userName, userId);
                    withdrawScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transfers":
                // Go to transfers page
                SwingUtilities.invokeLater(() -> {
                    Transfer transferScreen = new Transfer();
                    transferScreen.setUserInfo(userName, userId);
                    transferScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transactions":
                // Go to transactions page
                SwingUtilities.invokeLater(() -> {
                    Transaction transactionScreen = new Transaction(userId, userName);
                    transactionScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Cards":
                // Cards feature not implemented yet
                JOptionPane.showMessageDialog(this, "Cards feature coming soon!", "Information", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "QR Codes":
                // Show QR Codes tab on dashboard
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                    // Switch to QR Codes tab handled in dashboard
                    this.dispose();
                });
                break;
            default:
                break;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            UserProfile userProfile = new UserProfile();
            userProfile.setUserInfo("Test User", 12345);
            userProfile.setVisible(true);
        });
    }
    
    // Custom Profile Photo Panel class
    private class ProfilePhotoPanel extends JPanel {
        private BufferedImage profileImage;
        private BufferedImage defaultAvatar;
        
        public ProfilePhotoPanel() {
            setBackground(new Color(235, 237, 239)); // Light gray background
            // Add a component listener to handle resize events
            addComponentListener(new java.awt.event.ComponentAdapter() {
                @Override
                public void componentResized(java.awt.event.ComponentEvent e) {
                    // Re-generate default avatar when component is resized
                    if (profileImage == null && userName != null && !userName.isEmpty()) {
                        if (getWidth() > 0 && getHeight() > 0) {
                            generateDefaultAvatar();
                        }
                    }
                }
            });
        }
        
        public void setProfileImage(BufferedImage image) {
            this.profileImage = image;
            repaint(); // Force panel to redraw with the new image
        }
        
        /**
         * Generate default avatar based on username
         */
        public void generateDefaultAvatar() {
            if (userName != null && !userName.isEmpty() && getWidth() > 0 && getHeight() > 0) {
                try {
                    this.defaultAvatar = DefaultAvatarGenerator.generateDefaultAvatar(userName, getWidth(), getHeight());
                    if (profileImage == null) {
                        repaint();
                    }
                } catch (Exception e) {
                    System.err.println("Error generating default avatar: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Create a circular clipping shape
            java.awt.Shape clip = new java.awt.geom.Ellipse2D.Double(0, 0, getWidth(), getHeight());
            g2.setClip(clip);
            
            // Draw profile image if available
            if (profileImage != null) {
                // Draw the image
                g2.drawImage(profileImage, 0, 0, getWidth(), getHeight(), null);
            } else if (defaultAvatar != null) {
                // Draw default avatar with initials
                g2.drawImage(defaultAvatar, 0, 0, getWidth(), getHeight(), null);
            } else {
                // Draw background
                g2.setColor(new Color(235, 237, 239)); // Light gray
                g2.fillOval(0, 0, getWidth(), getHeight());
                
                // Draw placeholder icon (simple silhouette)
                g2.setColor(new Color(200, 200, 200));
                
                // Head
                int headSize = getWidth() / 3;
                g2.fillOval(getWidth()/2 - headSize/2, getHeight()/4, headSize, headSize);
                
                // Body
                g2.fillOval(getWidth()/2 - getWidth()/4, getHeight()/2, getWidth()/2, getHeight()/2);
            }
            
            // Draw circular border
            g2.setClip(null);
            g2.setColor(new Color(220, 220, 220));
            g2.drawOval(0, 0, getWidth()-1, getHeight()-1);
        }
    }
    
    // Custom gradient panel for backgrounds with rounded corners
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color gradientStart;
        private Color gradientEnd;
        
        public RoundedPanel(int radius, Color baseColor) {
            super();
            this.cornerRadius = radius;
            this.gradientStart = baseColor;
            this.gradientEnd = darkenColor(baseColor, 0.2f);
            setOpaque(false);
        }
        
        private Color darkenColor(Color color, float factor) {
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - factor));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gradient = new GradientPaint(
                0, 0, gradientStart,
                0, getHeight(), gradientEnd
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2d.dispose();
        }
    }
}