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
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Dashbord extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // Colors scheme
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    
    // Components
    private JLabel greeting;
    private String userName;
    private int userId;
    private JPanel dashboardContent;
    private JLabel checkingBalanceLabel;
    private JLabel savingsBalanceLabel;
    private JLabel checkingAccountIdLabel;
    private JLabel savingsAccountIdLabel;
    private JPanel qrCodePanel;
    private JTabbedPane mainTabbedPane;
    private JLabel transactionCountLabel;
    private JLabel lastTransactionLabel;
    private JPanel sidebarPanel;
    private List<JButton> menuButtons = new ArrayList<>();

    public Dashbord() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Kurdish-O-Banking (KOB)");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create components
        createSidebar();
        createHeaderPanel();
        createMainContent();
        
        try {
            setIconImage(ImageIO.read(new File("Logo/o1iwr2s2kskm9zqn7qr.png")));
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
        String[] menuItems = {"Dashboard", "Balance", "Accounts", "Deposit", "Withdraw", "Transfers", "Transactions", "Cards", "QR Codes"};
        String[] iconNames = {"dashboard", "balance", "accounts", "deposit", "withdraw", "transfers", "transactions", "cards", "qrcode"};
        
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
        
        // Set Dashboard as initially selected
        updateSelectedButton("Dashboard");
        
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
        
        greeting = new JLabel("Welcome back");
        greeting.setFont(new Font("SansSerif", Font.BOLD, 16));
        greeting.setForeground(TEXT_COLOR);
        
        JLabel usernameLabel = new JLabel("User");
        usernameLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        usernameLabel.setForeground(LIGHT_TEXT_COLOR);
        
        greetingPanel.add(greeting, BorderLayout.NORTH);
        greetingPanel.add(usernameLabel, BorderLayout.SOUTH);
        
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
        // Main content with tabbed pane
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BorderLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        
        // Create tabbed pane with custom styling
        mainTabbedPane = new JTabbedPane() {
            @Override
            public void updateUI() {
                super.updateUI();
                setFont(new Font("SansSerif", Font.PLAIN, 14));
                setBackground(BACKGROUND_COLOR);
                setForeground(TEXT_COLOR);
            }
        };
        mainTabbedPane.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create dashboard content
        createDashboardPanel();
        
        // Create QR code panel
        createQRCodePanel();
        
        // Add tabs
        mainTabbedPane.addTab("Dashboard", dashboardContent);
        mainTabbedPane.addTab("QR Codes", new JScrollPane(qrCodePanel));
        
        mainContent.add(mainTabbedPane, BorderLayout.CENTER);
        add(mainContent, BorderLayout.CENTER);
    }
    
    private void createDashboardPanel() {
        dashboardContent = new JPanel();
        dashboardContent.setLayout(new GridBagLayout());
        dashboardContent.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Account summary section
        JPanel accountSummaryPanel = createSectionPanel("Account Summary");
        accountSummaryPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        
        // Create fancy account cards
        JPanel checkingCard = createAccountCard("Checking Account", "****1234", SECONDARY_COLOR);
        JPanel savingsCard = createAccountCard("Savings Account", "****5678", ACCENT_COLOR);
        
        accountSummaryPanel.add(checkingCard);
        accountSummaryPanel.add(savingsCard);
        
        // Add account summary panel to dashboard
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.4;
        dashboardContent.add(accountSummaryPanel, gbc);
        
        // Recent activity section
        JPanel recentActivityPanel = createSectionPanel("Recent Activity");
        recentActivityPanel.setLayout(new BorderLayout(10, 10));
        
        // Create a panel to hold transaction information
        JPanel transactionPanel = new JPanel();
        transactionPanel.setLayout(new BoxLayout(transactionPanel, BoxLayout.Y_AXIS));
        transactionPanel.setOpaque(false);
        transactionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Transaction statistics
        JPanel statsPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        statsPanel.setOpaque(false);
        
        // Transaction count panel
        JPanel countPanel = new TransparentPanel();
        countPanel.setLayout(new BoxLayout(countPanel, BoxLayout.Y_AXIS));
        JLabel countTitle = new JLabel("Recent Transactions");
        countTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        countTitle.setForeground(LIGHT_TEXT_COLOR);
        countTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        transactionCountLabel = new JLabel("0");
        transactionCountLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        transactionCountLabel.setForeground(TEXT_COLOR);
        transactionCountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        countPanel.add(countTitle);
        countPanel.add(Box.createVerticalStrut(5));
        countPanel.add(transactionCountLabel);
        
        // Last transaction panel
        JPanel lastPanel = new TransparentPanel();
        lastPanel.setLayout(new BoxLayout(lastPanel, BoxLayout.Y_AXIS));
        JLabel lastTitle = new JLabel("Last Transaction");
        lastTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        lastTitle.setForeground(LIGHT_TEXT_COLOR);
        lastTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lastTransactionLabel = new JLabel("No recent transactions");
        lastTransactionLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        lastTransactionLabel.setForeground(TEXT_COLOR);
        lastTransactionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        lastPanel.add(lastTitle);
        lastPanel.add(Box.createVerticalStrut(5));
        lastPanel.add(lastTransactionLabel);
        
        statsPanel.add(countPanel);
        statsPanel.add(lastPanel);
        
        // Add view all transactions button
        JButton viewAllButton = new JButton("View All Transactions");
        viewAllButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        viewAllButton.setForeground(Color.WHITE);
        viewAllButton.setBackground(SECONDARY_COLOR);
        viewAllButton.setFocusPainted(false);
        viewAllButton.addActionListener(e -> handleButtonClick("Transactions"));
        viewAllButton.setOpaque(true);  
        viewAllButton.setBorderPainted(false);  
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);
        buttonPanel.add(viewAllButton);
        
        transactionPanel.add(statsPanel);
        transactionPanel.add(Box.createVerticalStrut(20));
        transactionPanel.add(buttonPanel);
        
        recentActivityPanel.add(transactionPanel, BorderLayout.CENTER);
        
        // Quick actions section
        JPanel quickActionsPanel = createSectionPanel("Quick Actions");
        quickActionsPanel.setLayout(new GridLayout(2, 2, 15, 15));
        quickActionsPanel.setBorder(BorderFactory.createCompoundBorder(
            quickActionsPanel.getBorder(),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Create action buttons
        JButton depositButton = createActionButton("Deposit", "Quickly deposit funds");
        JButton withdrawButton = createActionButton("Withdraw", "Withdraw your money");
        JButton transferButton = createActionButton("Transfer", "Transfer between accounts");
        JButton qrButton = createActionButton("QR Codes", "View your account QR codes");
        
        depositButton.addActionListener(e -> handleButtonClick("Deposit"));
        withdrawButton.addActionListener(e -> handleButtonClick("Withdraw"));
        transferButton.addActionListener(e -> handleButtonClick("Transfers"));
        qrButton.addActionListener(e -> mainTabbedPane.setSelectedIndex(1));
        
        quickActionsPanel.add(depositButton);
        quickActionsPanel.add(withdrawButton);
        quickActionsPanel.add(transferButton);
        quickActionsPanel.add(qrButton);
        
        // Add panels to dashboard
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.6;
        gbc.weighty = 0.6;
        dashboardContent.add(recentActivityPanel, gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.4;
        gbc.weighty = 0.6;
        dashboardContent.add(quickActionsPanel, gbc);
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
    
    private JPanel createAccountCard(String accountType, String accountNumber, Color accentColor) {
        // Create a panel with rounded corners
        JPanel card = new RoundedPanel(15, accentColor);
        card.setPreferredSize(new Dimension(320, 180));
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Account type and number
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        
        JLabel typeLabel = new JLabel(accountType);
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        typeLabel.setForeground(Color.WHITE);
        
        JLabel numberLabel = new JLabel(accountNumber);
        numberLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        numberLabel.setForeground(new Color(240, 240, 240));
        
        headerPanel.add(typeLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(numberLabel);
        
        // Balance section
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));
        balancePanel.setOpaque(false);
        
        JLabel balanceTitle = new JLabel("Current Balance");
        balanceTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        balanceTitle.setForeground(new Color(240, 240, 240));
        
        JLabel balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        balanceLabel.setForeground(Color.WHITE);
        
        if (accountType.startsWith("Checking")) {
            checkingBalanceLabel = balanceLabel;
        } else if (accountType.startsWith("Savings")) {
            savingsBalanceLabel = balanceLabel;
        }
        
        balancePanel.add(balanceTitle);
        balancePanel.add(Box.createVerticalStrut(5));
        balancePanel.add(balanceLabel);
        
        // Account ID section
        JPanel accountIdPanel = new JPanel();
        accountIdPanel.setLayout(new BoxLayout(accountIdPanel, BoxLayout.Y_AXIS));
        accountIdPanel.setOpaque(false);
        
        JLabel accountIdTitle = new JLabel("Account ID");
        accountIdTitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        accountIdTitle.setForeground(new Color(240, 240, 240));
        
        JLabel accountIdLabel = new JLabel("--");
        accountIdLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        accountIdLabel.setForeground(Color.WHITE);
        
        if (accountType.startsWith("Checking")) {
            checkingAccountIdLabel = accountIdLabel;
        } else if (accountType.startsWith("Savings")) {
            savingsAccountIdLabel = accountIdLabel;
        }
        
        accountIdPanel.add(accountIdTitle);
        accountIdPanel.add(Box.createVerticalStrut(5));
        accountIdPanel.add(accountIdLabel);
        
        // Footer with action buttons
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        footerPanel.setOpaque(false);
        
        JButton depositButton = new TransparentButton("Deposit");
        depositButton.setForeground(Color.WHITE);
        depositButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        depositButton.addActionListener(e -> handleButtonClick("Deposit"));
        
        JButton withdrawButton = new TransparentButton("Withdraw");
        withdrawButton.setForeground(Color.WHITE);
        withdrawButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
        withdrawButton.addActionListener(e -> handleButtonClick("Withdraw"));
        
        footerPanel.add(depositButton);
        footerPanel.add(withdrawButton);
        
        // Add components to card
        card.add(headerPanel, BorderLayout.NORTH);
        
        JPanel centerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        centerPanel.setOpaque(false);
        centerPanel.add(balancePanel);
        centerPanel.add(accountIdPanel);
        
        card.add(centerPanel, BorderLayout.CENTER);
        card.add(footerPanel, BorderLayout.SOUTH);
        
        return card;
    }
    
    private JButton createActionButton(String title, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(10, 5));
        button.setBackground(CARD_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
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
        textPanel.add(Box.createVerticalStrut(3));
        textPanel.add(descLabel);
        
        button.add(textPanel, BorderLayout.CENTER);
        
        // Try to add icon if available
        try {
            String iconName = title.toLowerCase().replace(" ", "_");
            ImageIcon icon = new ImageIcon("icons/" + iconName + ".png");
            java.awt.Image image = icon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 10));
            button.add(iconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            // If icon not found, use text only
            System.err.println("Icon not found for action button: " + title);
        }
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(250, 250, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(CARD_COLOR);
            }
        });
        
        return button;
    }
    
    private void createQRCodePanel() {
        qrCodePanel = new JPanel();
        qrCodePanel.setLayout(new BorderLayout());
        qrCodePanel.setBackground(BACKGROUND_COLOR);
        
        // Header for QR code section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Your Account QR Codes");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel("Use these QR codes to quickly transfer money between accounts");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setOpaque(false);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(5));
        titlePanel.add(subtitleLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Container for QR codes
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        qrCodePanel.add(headerPanel, BorderLayout.NORTH);
        qrCodePanel.add(new JScrollPane(contentPanel), BorderLayout.CENTER);
    }
    
    public void setUserInfo(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        
        // Update greeting with user name and current time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy 'at' HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        
        greeting.setText("Welcome back, " + userName + "!");
        
        // Load and display account balances and account IDs
        loadAccountInfo();
        
        // Load transaction info
        loadTransactionInfo();
        
        // Generate and display QR codes
        loadQRCodes();
        
        // Debug information
        System.out.println("Dashboard: Set user info - User: " + userName + ", Account ID: " + userId);
    }
    
    private void loadAccountInfo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT account_id, account_type, balance FROM accounts WHERE account_id = ? OR username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, userName);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                int accountId = rs.getInt("account_id");
                
                if ("Checking".equals(accountType)) {
                    checkingBalanceLabel.setText(String.format("$%.2f", balance));
                    checkingAccountIdLabel.setText(String.valueOf(accountId));
                } else if ("Savings".equals(accountType)) {
                    savingsBalanceLabel.setText(String.format("$%.2f", balance));
                    savingsAccountIdLabel.setText(String.valueOf(accountId));
                }
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading account information: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void loadTransactionInfo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Count recent transactions (last 30 days)
            String countQuery = "SELECT COUNT(*) FROM transactions WHERE user_id = ? " +
                               "AND transaction_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
            PreparedStatement countStmt = conn.prepareStatement(countQuery);
            countStmt.setInt(1, userId);
            ResultSet countRs = countStmt.executeQuery();
            
            if (countRs.next()) {
                int count = countRs.getInt(1);
                transactionCountLabel.setText(String.valueOf(count));
            }
            
            // Get most recent transaction
            String recentQuery = "SELECT transaction_type, amount, transaction_date " +
                                "FROM transactions WHERE user_id = ? " +
                                "ORDER BY transaction_date DESC LIMIT 1";
            PreparedStatement recentStmt = conn.prepareStatement(recentQuery);
            recentStmt.setInt(1, userId);
            ResultSet recentRs = recentStmt.executeQuery();
            
            if (recentRs.next()) {
                String type = recentRs.getString("transaction_type");
                double amount = recentRs.getDouble("amount");
                java.sql.Timestamp date = recentRs.getTimestamp("transaction_date");
                
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d, yyyy");
                String formattedDate = date.toLocalDateTime().format(formatter);
                
                lastTransactionLabel.setText(String.format("%s: $%.2f (%s)", 
                    capitalizeFirstLetter(type), amount, formattedDate));
            } else {
                lastTransactionLabel.setText("No recent transactions");
            }
            
        } catch (SQLException ex) {
            System.err.println("Error loading transaction info: " + ex.getMessage());
            // Don't show error dialog, just use defaults
        }
    }
    
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1).toLowerCase();
    }
    
    private void loadQRCodes() {
        // Get the content panel from the QR code panel's scroll pane
        JScrollPane scrollPane = (JScrollPane) qrCodePanel.getComponent(1);
        JPanel contentPanel = (JPanel) scrollPane.getViewport().getView();
        if (contentPanel == null) {
            contentPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
            contentPanel.setBackground(BACKGROUND_COLOR);
            scrollPane.setViewportView(contentPanel);
        } else {
            contentPanel.removeAll();
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get user accounts
            String query = "SELECT account_id, account_type, balance FROM accounts WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userName);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int accountId = rs.getInt("account_id");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                
                // Create QR code card for each account
                JPanel qrCard = createQRCodeCard(accountId, accountType, balance);
                contentPanel.add(qrCard);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading QR codes: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
        
        // Refresh the panel
        qrCodePanel.revalidate();
        qrCodePanel.repaint();
    }
    
    private JPanel createQRCodeCard(int accountId, String accountType, double balance) {
        JPanel card = new RoundedPanel(15, Color.WHITE);
        card.setPreferredSize(new Dimension(300, 450));
        card.setLayout(new BorderLayout(0, 15));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Header with account info
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        JLabel typeLabel = new JLabel(accountType + " Account");
        typeLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        typeLabel.setForeground(TEXT_COLOR);
        typeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel idLabel = new JLabel("Account ID: " + accountId);
        idLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        idLabel.setForeground(LIGHT_TEXT_COLOR);
        idLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel balanceLabel = new JLabel(String.format("Balance: $%.2f", balance));
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        balanceLabel.setForeground(LIGHT_TEXT_COLOR);
        balanceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        headerPanel.add(typeLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(idLabel);
        headerPanel.add(Box.createVerticalStrut(3));
        headerPanel.add(balanceLabel);
        
        // QR code panel
        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setOpaque(false);
        qrPanel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        
        // Generate QR code silently (no dialog)
        BufferedImage qrImage = BankQRGenerator.generateQRCodeSilent(userId, accountId, accountType);
        if (qrImage != null) {
            JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
            qrLabel.setHorizontalAlignment(JLabel.CENTER);
            qrPanel.add(qrLabel, BorderLayout.CENTER);
        } else {
            JLabel errorLabel = new JLabel("Could not generate QR code");
            errorLabel.setHorizontalAlignment(JLabel.CENTER);
            qrPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        // Export button with improved styling
        JButton exportButton = new JButton("Export QR Code");
        exportButton.setBackground(SECONDARY_COLOR);
        exportButton.setForeground(Color.WHITE);
        exportButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> {
            if (qrImage != null) {
                saveQRCodeToFile(qrImage, userName + "_" + accountType + "_" + accountId);
            }
        });
        
        // Add components to card
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(qrPanel, BorderLayout.CENTER);
        card.add(exportButton, BorderLayout.SOUTH);
        
        return card;
    }
    
    private void saveQRCodeToFile(BufferedImage qrImage, String baseFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save QR Code");
        fileChooser.setSelectedFile(new File(baseFileName + ".png"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure filename has .png extension
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }
            
            try {
                ImageIO.write(qrImage, "PNG", fileToSave);
                JOptionPane.showMessageDialog(this, 
                    "QR Code saved successfully to: " + fileToSave.getAbsolutePath(),
                    "Save Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Error saving QR code: " + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void handleButtonClick(String buttonName) {
        System.out.println("Button clicked: " + buttonName);
        
        switch (buttonName) {
            case "Dashboard":
                // Refresh dashboard
                loadAccountInfo();
                loadTransactionInfo();
                mainTabbedPane.setSelectedIndex(0);
                updateSelectedButton("Dashboard");
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
                // Go to User Profile page
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
            case "Transactions":
                // Go to transactions page
                SwingUtilities.invokeLater(() -> {
                    Transaction transactionScreen = new Transaction(userId, userName);
                    transactionScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transfers":
                // Go to transfers page with correct account ID
                SwingUtilities.invokeLater(() -> {
                    Transfer transferScreen = new Transfer();
                    transferScreen.setUserInfo(userName, userId);
                    transferScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Cards":
                // Go to cards page
                JOptionPane.showMessageDialog(this, "Cards feature coming soon!");
                break;
            case "QR Codes":
                // Show QR Codes tab
                mainTabbedPane.setSelectedIndex(1);
                updateSelectedButton("QR Codes");
                break;
            default:
                break;
        }
    }
    
    // Custom rounded panel class
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
    
    // Transparent panel for overlays
    class TransparentPanel extends JPanel {
        public TransparentPanel() {
            setOpaque(false);
        }
    }
    
    // Transparent button for account cards
    class TransparentButton extends JButton {
        public TransparentButton(String text) {
            super(text);
            setOpaque(false);
            setContentAreaFilled(false);
            setBorderPainted(false);
            setFocusPainted(false);
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashbord dashboard = new Dashbord();
            dashboard.setUserInfo("John Doe", 12345);
            dashboard.setVisible(true);
        });
    }
}