package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Deposite extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Colors scheme - same as Dashboard and Transfer
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    
    private JTextField amountField;
    private JTextArea descArea;
    private int accountId;
    private String userName;
    private JLabel currentAccountLabel;
    private JLabel currentBalanceLabel;
    private JPanel sidebarPanel;
    private List<JButton> menuButtons = new ArrayList<>();
    private JRadioButton bankTransferRadio;
    private JRadioButton cardRadio;
    
    public Deposite() {
        // Set look and feel to be more modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Deposit Funds - Kurdish-O-Banking (KOB)");
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
        separator.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the separator
        sidebarPanel.add(separator);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Create a panel to hold the menu buttons and center them
        JPanel menuButtonsPanel = new JPanel();
        menuButtonsPanel.setLayout(new BoxLayout(menuButtonsPanel, BoxLayout.Y_AXIS));
        menuButtonsPanel.setOpaque(false);
        menuButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Menu items with icons - Same as Dashboard for consistency
        String[] menuItems = {"Dashboard", "Balance", "Accounts", "Deposit", "Withdraw", "Transfers", "Transactions", "QR Codes"};
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
            
            // Center align the button
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Create a wrapper panel to center the button
            JPanel buttonWrapper = new JPanel();
            buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.X_AXIS));
            buttonWrapper.setOpaque(false);
            buttonWrapper.setMaximumSize(new Dimension(220, 45));
            buttonWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add space before the button to center it
            buttonWrapper.add(Box.createHorizontalGlue());
            buttonWrapper.add(button);
            buttonWrapper.add(Box.createHorizontalGlue());
            
            menuButtonsPanel.add(buttonWrapper);
            menuButtonsPanel.add(Box.createVerticalStrut(5));
            menuButtons.add(button);
        }
        
        // Add the menu buttons panel to the sidebar
        sidebarPanel.add(menuButtonsPanel);
        
        // Set Deposit as initially selected
        updateSelectedButton("Deposit");
        
        // Add logout at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        JSeparator bottomSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        bottomSeparator.setForeground(new Color(70, 80, 120));
        bottomSeparator.setBackground(new Color(70, 80, 120));
        bottomSeparator.setMaximumSize(new Dimension(220, 1));
        bottomSeparator.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the separator
        sidebarPanel.add(bottomSeparator);
        
        // Create logout button centered
        JButton logoutButton = createMenuButton("Logout", "logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    Deposite.this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    // Open login page
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            LoginUI loginPage = new LoginUI();
                            loginPage.setVisible(true);
                        }
                    });
                }
            }
        });
        
        // Create wrapper for logout button to center it
        JPanel logoutWrapper = new JPanel();
        logoutWrapper.setLayout(new BoxLayout(logoutWrapper, BoxLayout.X_AXIS));
        logoutWrapper.setOpaque(false);
        logoutWrapper.setMaximumSize(new Dimension(220, 45));
        logoutWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoutWrapper.add(Box.createHorizontalGlue());
        logoutWrapper.add(logoutButton);
        logoutWrapper.add(Box.createHorizontalGlue());
        
        sidebarPanel.add(logoutWrapper);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private JButton createMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.CENTER); // Changed from LEFT to CENTER
        button.setIconTextGap(10);
        button.setMaximumSize(new Dimension(180, 45)); // Reduced width to center better
        button.setPreferredSize(new Dimension(180, 45)); // Reduced width to center better
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Equal padding on all sides
        
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
        
        // Title and description
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Deposit Funds");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel("Add money to your account securely");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Account info on the right
        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));
        accountPanel.setOpaque(false);
        
        currentAccountLabel = new JLabel("Account ID: ");
        currentAccountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        currentAccountLabel.setForeground(LIGHT_TEXT_COLOR);
        currentAccountLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        currentBalanceLabel = new JLabel("Available Balance: Loading...");
        currentBalanceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        currentBalanceLabel.setForeground(TEXT_COLOR);
        currentBalanceLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        accountPanel.add(currentAccountLabel);
        accountPanel.add(Box.createVerticalStrut(4));
        accountPanel.add(currentBalanceLabel);
        
        headerPanel.add(accountPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createMainContent() {
        // Main content panel
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new GridBagLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Deposit method section
        JPanel depositMethodPanel = createSectionPanel("Deposit Method");
        depositMethodPanel.setLayout(new BorderLayout());
        
        JPanel methodContent = new JPanel();
        methodContent.setLayout(new BoxLayout(methodContent, BoxLayout.X_AXIS));
        methodContent.setOpaque(false);
        methodContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        ButtonGroup methodGroup = new ButtonGroup();
        bankTransferRadio = new JRadioButton();
        cardRadio = new JRadioButton();
        methodGroup.add(bankTransferRadio);
        methodGroup.add(cardRadio);
        bankTransferRadio.setSelected(true); // Default selection
        
        JPanel bankTransferCard = createRadioCard("Bank Transfer", "2–3 business days", bankTransferRadio);
        JPanel cardDepositCard = createRadioCard("Credit/Debit Card", "Instant deposit", cardRadio);
        
        methodContent.add(bankTransferCard);
        methodContent.add(Box.createHorizontalStrut(20));
        methodContent.add(cardDepositCard);
        methodContent.add(Box.createHorizontalGlue());
        
        depositMethodPanel.add(methodContent, BorderLayout.CENTER);
        
        // Amount section
        JPanel amountPanel = createSectionPanel("Deposit Amount");
        amountPanel.setLayout(new BorderLayout());
        
        JPanel amountContent = new JPanel();
        amountContent.setLayout(new BoxLayout(amountContent, BoxLayout.Y_AXIS));
        amountContent.setOpaque(false);
        amountContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        amountLabel.setForeground(TEXT_COLOR);
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel amountInfoLabel = new JLabel("Enter the amount you want to deposit");
        amountInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        amountInfoLabel.setForeground(LIGHT_TEXT_COLOR);
        amountInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        amountField = new JTextField();
        amountField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        amountField.setMaximumSize(new Dimension(800, 40));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Quick amount buttons
        JLabel quickLabel = new JLabel("Quick amounts:");
        quickLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        quickLabel.setForeground(LIGHT_TEXT_COLOR);
        quickLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel quickButtons = new JPanel();
        quickButtons.setOpaque(false);
        quickButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        quickButtons.setLayout(new BoxLayout(quickButtons, BoxLayout.X_AXIS));
        
        String[] amounts = {"$100", "$500", "$1000"};
        for (String amount : amounts) {
            JButton quickButton = new JButton(amount);
            quickButton.setBackground(new Color(230, 240, 255));
            quickButton.setForeground(SECONDARY_COLOR);
            quickButton.setFocusPainted(false);
            quickButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
            quickButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 220, 250)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            
            final String amountValue = amount;
            quickButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    amountField.setText(amountValue.replace("$", ""));
                }
            });
            
            quickButtons.add(quickButton);
            if (!amount.equals(amounts[amounts.length-1])) {
                quickButtons.add(Box.createHorizontalStrut(10));
            }
        }
        
        amountContent.add(amountLabel);
        amountContent.add(Box.createVerticalStrut(5));
        amountContent.add(amountInfoLabel);
        amountContent.add(Box.createVerticalStrut(15));
        amountContent.add(amountField);
        amountContent.add(Box.createVerticalStrut(15));
        amountContent.add(quickLabel);
        amountContent.add(Box.createVerticalStrut(5));
        amountContent.add(quickButtons);
        
        amountPanel.add(amountContent, BorderLayout.CENTER);
        
        // Description section
        JPanel descriptionPanel = createSectionPanel("Description");
        descriptionPanel.setLayout(new BorderLayout());
        
        JPanel descContent = new JPanel();
        descContent.setLayout(new BoxLayout(descContent, BoxLayout.Y_AXIS));
        descContent.setOpaque(false);
        descContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel descLabel = new JLabel("Description (Optional)");
        descLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        descLabel.setForeground(TEXT_COLOR);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descInfoLabel = new JLabel("Add a note to your deposit for your records");
        descInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descInfoLabel.setForeground(LIGHT_TEXT_COLOR);
        descInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        descArea = new JTextArea(4, 30);
        descArea.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        descArea.setMaximumSize(new Dimension(800, 100));
        descArea.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Submit button
        JButton depositButton = new JButton("Deposit Funds");
        depositButton.setBackground(SECONDARY_COLOR);
        depositButton.setForeground(Color.WHITE);
        depositButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        depositButton.setFocusPainted(false);
        depositButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        depositButton.setMaximumSize(new Dimension(800, 50));
        depositButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        depositButton.setOpaque(true);  
        depositButton.setBorderPainted(false);
        
        // Add hover effect
        depositButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                depositButton.setBackground(new Color(25, 118, 210));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                depositButton.setBackground(SECONDARY_COLOR);
            }
        });
        
        depositButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleDepositSubmit();
            }
        });
        
        descContent.add(descLabel);
        descContent.add(Box.createVerticalStrut(5));
        descContent.add(descInfoLabel);
        descContent.add(Box.createVerticalStrut(15));
        descContent.add(descArea);
        descContent.add(Box.createVerticalStrut(30));
        descContent.add(depositButton);
        
        descriptionPanel.add(descContent, BorderLayout.CENTER);
        
        // Add panels to main content with GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        mainContent.add(depositMethodPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        mainContent.add(amountPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        mainContent.add(descriptionPanel, gbc);
        
        // Add main content to frame with scroll pane
        add(new JScrollPane(mainContent), BorderLayout.CENTER);
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
        JPanel titleBar = new JPanel();
        titleBar.setLayout(new BorderLayout());
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
    
    private JPanel createRadioCard(String title, String description, JRadioButton radio) {
        JPanel panel = new RoundedPanel(15, Color.WHITE);
        panel.setPreferredSize(new Dimension(220, 100));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        radio.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(LIGHT_TEXT_COLOR);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);
        
        panel.add(radio, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        
        // Add mouse listener to make the whole panel clickable
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radio.setSelected(true);
            }
        });
        
        return panel;
    }
    
    // Method to set user info
    public void setUserInfo(String userName, int accountId) {
        this.userName = userName;
        this.accountId = accountId;
        
        // Update account info
        currentAccountLabel.setText("Account ID: " + accountId);
        
        // Load balance if needed
        loadCurrentBalance();
        
        System.out.println("Deposit: Set user info - User: " + userName + ", Account ID: " + accountId);
    }
    
    // Method to load current balance
    private void loadCurrentBalance() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT balance FROM accounts WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, accountId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                currentBalanceLabel.setText(String.format("Available Balance: $%.2f", balance));
            } else {
                currentBalanceLabel.setText("Available Balance: Not found");
            }
            
        } catch (SQLException ex) {
            currentBalanceLabel.setText("Available Balance: Error loading");
            System.out.println("Error loading balance: " + ex.getMessage());
            ex.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    private void handleButtonClick(String buttonName) {
        System.out.println("Button clicked: " + buttonName);
        
        switch (buttonName) {
            case "Dashboard":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Dashbord dashboard = new Dashbord();
                        dashboard.setUserInfo(userName, accountId);
                        dashboard.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Balance":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        BalancePage balancePage = new BalancePage(userName, accountId);
                        balancePage.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Accounts":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        UserProfile userProfile = new UserProfile();
                        userProfile.setUserInfo(userName, accountId);
                        userProfile.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Deposit":
                // Already on Deposit page
                break;
            case "Withdraw":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Withdraw withdrawScreen = new Withdraw();
                        withdrawScreen.setUserInfo(userName, accountId);
                        withdrawScreen.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Transfers":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Transfer transferScreen = new Transfer();
                        transferScreen.setUserInfo(userName, accountId);
                        transferScreen.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Transactions":
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Transaction transactionScreen = new Transaction(accountId, userName);
                        transactionScreen.setVisible(true);
                        dispose();}
                    });
                    break;
                case "QR Codes":
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            Dashbord dashboard = new Dashbord();
                            dashboard.setUserInfo(userName, accountId);
                            dashboard.setVisible(true);
                            // Navigate to QR Codes tab
                            dispose();
                        }
                    });
                    break;
                    
                default:
                    break;
            }
        }
        
        private void handleDepositSubmit() {
            String amount = amountField.getText().trim();
            String description = descArea.getText().trim();
            
            if (amount.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter an amount to deposit", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            try {
                double amountValue = Double.parseDouble(amount);
                if (amountValue <= 0) {
                    JOptionPane.showMessageDialog(this, "Please enter a positive amount", "Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Confirm deposit with custom dialog
                String method = bankTransferRadio.isSelected() ? "Bank Transfer" : "Credit/Debit Card";
                String message = String.format(
                    "<html><div style='text-align: center;'>" +
                    "<h2>Confirm Deposit</h2>" +
                    "<p>You are about to deposit:</p>" +
                    "<h3 style='color: #1E90FF;'>$%.2f</h3>" +
                    "<p>Method: <b>%s</b></p>" +
                    "<p>Please confirm this transaction.</p>" +
                    "</div></html>",
                    amountValue, method
                );
                
                int confirmResult = JOptionPane.showConfirmDialog(
                    this,
                    message,
                    "Confirm Deposit",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (confirmResult == JOptionPane.YES_OPTION) {
                    boolean success = saveTransaction(amountValue, description);
                    
                    if (success) {
                        String successMessage = String.format(
                            "<html><div style='text-align: center;'>" +
                            "<h2 style='color: #28A745;'>Deposit Pending</h2>" +
                            "<p>Your deposit of <b>$%.2f</b> has been submitted</p>" +
                            "<p>Transaction ID: <b>%s</b></p>" +
                            "<p>Status: <b>Pending Approval</b></p>" +
                            "</div></html>",
                            amountValue, generateTransactionId()
                        );
                        
                        JOptionPane.showMessageDialog(
                            this,
                            successMessage,
                            "Deposit Submitted",
                            JOptionPane.INFORMATION_MESSAGE
                        );
                        
                        SwingUtilities.invokeLater(new Runnable() {
                            @Override
                            public void run() {
                                Dashbord dashboard = new Dashbord();
                                dashboard.setUserInfo(userName, accountId);
                                dashboard.setVisible(true);
                                dispose();
                            }
                        });
                    } else {
                        JOptionPane.showMessageDialog(this, 
                                "Failed to process deposit. Please try again later.", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
                
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
        
        // Generate a transaction ID
        private String generateTransactionId() {
            return "DEP" + System.currentTimeMillis() % 10000000;
        }
        
        private boolean saveTransaction(double amount, String description) {
            Connection conn = null;
            try {
                conn = DatabaseConnection.getConnection();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = sdf.format(new Date());
                
                System.out.println("Creating deposit transaction:");
                System.out.println("Account ID: " + accountId);
                System.out.println("Amount: $" + amount);
                System.out.println("Date: " + currentDate);
                System.out.println("Description: " + description);
    
                String sql = "INSERT INTO transactions (account_id, transaction_type, amount, transaction_date, description, status) " +
                         "VALUES (?, ?, ?, ?, ?, ?)";
    
                PreparedStatement pstmt = conn.prepareStatement(sql);
                pstmt.setInt(1, accountId);
                pstmt.setString(2, "DEPOSIT");
                pstmt.setDouble(3, amount);
                pstmt.setString(4, currentDate);
                pstmt.setString(5, description);
                pstmt.setString(6, TransactionStatus.PENDING);
    
                int rowsAffected = pstmt.executeUpdate();
                pstmt.close();
                
                System.out.println("Transaction saved, rows affected: " + rowsAffected);
                return rowsAffected > 0;
    
            } catch (SQLException e) {
                System.out.println("Error saving transaction: " + e.getMessage());
                e.printStackTrace();
                return false;
            } finally {
                if (conn != null) {
                    try {
                        conn.close();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        
        // Custom rounded panel class - same as in Dashboard
        class RoundedPanel extends JPanel {
            private int cornerRadius;
            private Color backgroundColor;
            
            public RoundedPanel(int radius, Color bgColor) {
                super();
                this.cornerRadius = radius;
                this.backgroundColor = bgColor;
                setOpaque(false);
            }
            
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(backgroundColor);
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
                g2d.dispose();
            }
        }
    
        public static void main(String[] args) {
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    Deposite deposit = new Deposite();
                    deposit.setUserInfo("John Doe", 12345);
                    deposit.setVisible(true);
                }
            });
        }
    }