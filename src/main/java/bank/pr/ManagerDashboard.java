package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.border.EmptyBorder;

public class ManagerDashboard extends JFrame {
    
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 220, 120);
    
    private int pendingApprovals = 0;
    private int flaggedTransactions = 0;
    private int highRiskAccounts = 0;
    private int newCustomers = 0;
    
    // Previous values to track changes
    private Map<String, Integer> previousValues = new HashMap<>();
    
    private String adminName = "";
    private String adminLastLogin = "";
    
    // UI Components that need to be updated
    private JLabel pendingApprovalsLabel;
    private JLabel flaggedTransactionsLabel;
    private JLabel highRiskAccountsLabel;
    private JLabel newCustomersLabel;
    private JLabel subHeaderLabel;
    
    // Card panels to highlight changes
    private JPanel pendingApprovalsCard;
    private JPanel flaggedTransactionsCard;
    private JPanel highRiskAccountsCard;
    private JPanel newCustomersCard;
    
    // Admin ID for logged in admin
    private int adminId = 0;

    public ManagerDashboard() {
        initialize();
    }
    
    public ManagerDashboard(int adminId) {
        this.adminId = adminId;
        initialize();
    }
    
    private void initialize() {
        setTitle("KOB Manager Dashboard");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Load admin information
        loadAdminInfo();
        
        // Load metrics from database
        loadMetricsFromDatabase();
        
        // Store initial values
        previousValues.put("pendingApprovals", pendingApprovals);
        previousValues.put("flaggedTransactions", flaggedTransactions);
        previousValues.put("highRiskAccounts", highRiskAccounts);
        previousValues.put("newCustomers", newCustomers);

        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        JPanel mainContentPanel = createMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Try to set icon
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
        
        setVisible(true);
    }
    
    /**
     * Loads admin information from the database
     */
    private void loadAdminInfo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query;
            
            if (adminId > 0) {
                // If admin ID is provided, get specific admin info
                query = "SELECT first_name, last_name, last_login FROM admin WHERE admin_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, adminId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    adminName = rs.getString("first_name") + " " + rs.getString("last_name");
                    
                    // Format the last login time if it exists
                    java.sql.Timestamp lastLogin = rs.getTimestamp("last_login");
                    if (lastLogin != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm a");
                        adminLastLogin = dateFormat.format(new Date(lastLogin.getTime()));
                    } else {
                        adminLastLogin = "First login";
                    }
                }
            } else {
                // If no specific admin ID, get the first admin in the database (fallback)
                query = "SELECT first_name, last_name, last_login FROM admin LIMIT 1";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    adminName = rs.getString("first_name") + " " + rs.getString("last_name");
                    
                    // Format the last login time if it exists
                    java.sql.Timestamp lastLogin = rs.getTimestamp("last_login");
                    if (lastLogin != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm a");
                        adminLastLogin = dateFormat.format(new Date(lastLogin.getTime()));
                    } else {
                        adminLastLogin = "First login";
                    }
                } else {
                    // If no admin found at all, use default values
                    adminName = "Administrator";
                    adminLastLogin = "Today";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading admin information: " + e.getMessage());
            adminName = "Administrator";
            adminLastLogin = "Today";
        }
    }
    
    /**
     * Load metrics from database
     */
    private void loadMetricsFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get pending approvals count (transactions with PENDING status)
            String pendingApprovalsQuery = "SELECT COUNT(*) FROM transactions WHERE status = 'PENDING'";
            try (PreparedStatement stmt = conn.prepareStatement(pendingApprovalsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pendingApprovals = rs.getInt(1);
                }
            }
            
            // Get flagged transactions count (any transactions that need attention)
            // Looking for either FLAGGED status or large amounts (over $1000) as potential flags
            String flaggedTransactionsQuery = "SELECT COUNT(*) FROM transactions WHERE status = 'FLAGGED' OR amount > 1000";
            try (PreparedStatement stmt = conn.prepareStatement(flaggedTransactionsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    flaggedTransactions = rs.getInt(1);
                }
            }
            
            // Get high-risk accounts count (accounts with balance > 10000)
            String highRiskAccountsQuery = "SELECT COUNT(*) FROM accounts WHERE balance > 10000";
            try (PreparedStatement stmt = conn.prepareStatement(highRiskAccountsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    highRiskAccounts = rs.getInt(1);
                }
            }
            
            // Get new customers count (accounts created in the last 7 days)
            String newCustomersQuery = "SELECT COUNT(*) FROM accounts WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
            try (PreparedStatement stmt = conn.prepareStatement(newCustomersQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    newCustomers = rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading metrics: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel() {
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
        
        sidebar.setPreferredSize(new Dimension(260, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(30, 25, 30, 25));
        
        JLabel logoLabel = new JLabel();
        try {

            ImageIcon logoIcon = new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png");
            java.awt.Image image = logoIcon.getImage().getScaledInstance(120, 80, java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(image);
            logoLabel.setIcon(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
        
        // Bank name 
        JLabel bankNameLabel = new JLabel("KOB Manager Portal");
        bankNameLabel.setForeground(Color.WHITE);
        bankNameLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        bankNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Admin panel label
        JLabel adminLabel = new JLabel("Administration Panel");
        adminLabel.setForeground(new Color(180, 180, 180));
        adminLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        adminLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add sidebar header
        sidebar.add(logoLabel);
        sidebar.add(bankNameLabel);
        sidebar.add(Box.createVerticalStrut(5));
        sidebar.add(adminLabel);
        sidebar.add(Box.createVerticalStrut(40));
        
        // Menu items panel
        JPanel menuPanel = new JPanel();
        menuPanel.setOpaque(false);
        menuPanel.setLayout(new BoxLayout(menuPanel, BoxLayout.Y_AXIS));
        menuPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add menu items
        addMenuItem(menuPanel, "Dashboard", true);
        addMenuItem(menuPanel, "Customer Accounts", false);
        addMenuItem(menuPanel, "Transaction Oversight", false);
        addMenuItem(menuPanel, "Approval Queue", false);
        addMenuItem(menuPanel, "Reports", false);
        addMenuItem(menuPanel, "Audit Logs", false);
        
        sidebar.add(menuPanel);
        sidebar.add(Box.createVerticalGlue());
        
        // Add admin profile at bottom of sidebar
        JPanel profilePanel = new RoundedPanel(15, new Color(40, 50, 90));
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        profilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.setMaximumSize(new Dimension(250, 120));
        
        JLabel nameLabel = new JLabel(adminName);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel roleLabel = new JLabel("Manager");
        roleLabel.setForeground(new Color(200, 200, 200));
        roleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        roleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setBackground(new Color(255, 100, 100));
        logoutButton.setForeground(Color.WHITE);
        logoutButton.setFocusPainted(false);
        logoutButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        logoutButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        logoutButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        logoutButton.setOpaque(true); 
        logoutButton.setBorderPainted(false);
        
        // Add action listener to logout button
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int response = JOptionPane.showConfirmDialog(
                    ManagerDashboard.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                    SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
                }
            }
        });
        
        profilePanel.add(nameLabel);
        profilePanel.add(Box.createVerticalStrut(5));
        profilePanel.add(roleLabel);
        profilePanel.add(Box.createVerticalStrut(15));
        profilePanel.add(logoutButton);
        
        sidebar.add(profilePanel);
        
        return sidebar;
    }
    
    /**
     * Helper method to add menu items
     */
    private void addMenuItem(JPanel menuPanel, String text, boolean isActive) {
        JPanel menuItem = new JPanel();
        menuItem.setLayout(new BorderLayout());
        menuItem.setOpaque(false);
        menuItem.setMaximumSize(new Dimension(250, 50));
        menuItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add highlight for active item
        if (isActive) {
            menuItem.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 3, 0, 0, ACCENT_COLOR),
                BorderFactory.createEmptyBorder(10, 15, 10, 10)
            ));
        } else {
            menuItem.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        }
        
        JLabel label = new JLabel(text);
        label.setForeground(isActive ? Color.WHITE : new Color(200, 200, 200));
        label.setFont(new Font("SansSerif", isActive ? Font.BOLD : Font.PLAIN, 16));
        
        menuItem.add(label, BorderLayout.CENTER);
        
        // Add hover effect
        menuItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isActive) {
                    menuItem.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(100, 100, 100)),
                        BorderFactory.createEmptyBorder(10, 15, 10, 10)
                    ));
                    label.setForeground(new Color(230, 230, 230));
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isActive) {
                    menuItem.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
                    label.setForeground(new Color(200, 200, 200));
                }
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToScreen(text);
            }
        });
        
        menuPanel.add(menuItem);
        menuPanel.add(Box.createVerticalStrut(5));
    }
    
    /**
     * Handles navigation to different screens
     */
    private void navigateToScreen(String screenName) {
        if (screenName.equals("Dashboard")) {
            // Already on dashboard, just refresh
            loadMetricsFromDatabase();
            updateMetricLabels();
            return;
        }
        
        dispose(); // Close the current window
        
        switch (screenName) {
            case "Customer Accounts":
                SwingUtilities.invokeLater(() -> new CustomerAccounts().setVisible(true));
                break;
            case "Transaction Oversight":
                SwingUtilities.invokeLater(() -> new ManageTransaction().setVisible(true));
                break;
            case "Reports":
                SwingUtilities.invokeLater(() -> new Report().setVisible(true));
                break;
            case "Approval Queue":
                SwingUtilities.invokeLater(() -> new ApproveTransaction(adminId).setVisible(true));
                break;
            case "Audit Logs":
                JOptionPane.showMessageDialog(null, 
                    "Audit Logs screen is under development.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
                break;
            default:
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
        }
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        
        JLabel headerLabel = new JLabel("Manager Dashboard");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerLabel.setForeground(TEXT_COLOR);
        
        // Last login info
        subHeaderLabel = new JLabel("Welcome back, " + adminName + " | Last login: " + adminLastLogin);
        subHeaderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subHeaderLabel.setForeground(LIGHT_TEXT_COLOR);
        
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(subHeaderLabel, BorderLayout.SOUTH);
        
        // Create a refresh button
        JButton refreshButton = new JButton("Refresh Dashboard");
        refreshButton.setBackground(SECONDARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        
        // Add hover effects
        refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                refreshButton.setBackground(new Color(25, 118, 210));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                refreshButton.setBackground(SECONDARY_COLOR);
            }
        });
        
        refreshButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadMetricsFromDatabase();
                updateMetricLabels();
                JOptionPane.showMessageDialog(
                    ManagerDashboard.this,
                    "Dashboard data refreshed successfully.",
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        // Add refresh button to a panel to position it properly
        JPanel refreshPanel = new JPanel();
        refreshPanel.setOpaque(false);
        refreshPanel.setLayout(new BorderLayout());
        refreshPanel.add(refreshButton, BorderLayout.EAST);
        
        // Add header and refresh button to top panel
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.WEST);
        topPanel.add(refreshPanel, BorderLayout.EAST);
        
        // Dashboard content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Overview section
        JLabel overviewLabel = new JLabel("TODAY'S OVERVIEW");
        overviewLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        overviewLabel.setForeground(TEXT_COLOR);
        overviewLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        overviewLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Metrics Panel
        JPanel metricsPanel = new JPanel();
        metricsPanel.setOpaque(false);
        metricsPanel.setLayout(new GridLayout(1, 4, 20, 0));
        metricsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        metricsPanel.setMaximumSize(new Dimension(1000, 120));
        
        // Create the metric labels
        pendingApprovalsLabel = new JLabel(String.valueOf(pendingApprovals));
        flaggedTransactionsLabel = new JLabel(String.valueOf(flaggedTransactions));
        highRiskAccountsLabel = new JLabel(String.valueOf(highRiskAccounts));
        newCustomersLabel = new JLabel(String.valueOf(newCustomers));
        
        // Add metric cards
        metricsPanel.add(createMetricCard("Pending Approvals", pendingApprovalsLabel, SECONDARY_COLOR));
        metricsPanel.add(createMetricCard("Flagged Transactions", flaggedTransactionsLabel, new Color(76, 175, 80)));
        metricsPanel.add(createMetricCard("High-Risk Accounts", highRiskAccountsLabel, new Color(220, 53, 69)));
        metricsPanel.add(createMetricCard("New Customers", newCustomersLabel, ACCENT_COLOR));
        
        // Tools section
        JLabel toolsLabel = new JLabel("MANAGEMENT TOOLS");
        toolsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        toolsLabel.setForeground(TEXT_COLOR);
        toolsLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 15, 0));
        toolsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Tools panel
        JPanel toolsPanel = new JPanel();
        toolsPanel.setOpaque(false);
        toolsPanel.setLayout(new GridLayout(2, 2, 20, 20));
        toolsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create tool cards
        JPanel accountsCard = createToolCard(
            "Customer Accounts", 
            "Search and manage all customer accounts", 
            "Customer Accounts",
            SECONDARY_COLOR
        );
        
        JPanel transactionsCard = createToolCard(
            "Transaction Management", 
            "Review and modify transaction records", 
            "Transaction Oversight",
            new Color(76, 175, 80)
        );
        
        JPanel approvalsCard = createToolCard(
            "Approval Queue", 
            "Authorize pending transactions", 
            "Approval Queue",
            new Color(220, 53, 69)
        );
        
        JPanel reportsCard = createToolCard(
            "Reports & Analytics", 
            "Generate financial and audit reports", 
            "Reports",
            ACCENT_COLOR
        );
        
        // Add tool cards to panel
        toolsPanel.add(accountsCard);
        toolsPanel.add(transactionsCard);
        toolsPanel.add(approvalsCard);
        toolsPanel.add(reportsCard);
        
        // Add everything to the content panel
        contentPanel.add(overviewLabel);
        contentPanel.add(metricsPanel);
        contentPanel.add(toolsLabel);
        contentPanel.add(toolsPanel);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        JLabel versionLabel = new JLabel("Kurdish-O-Banking Manager Portal v1.0 | © 2025 KOB. All rights reserved.");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(LIGHT_TEXT_COLOR);
        
        footerPanel.add(versionLabel, BorderLayout.WEST);
        
        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    /**
     * Creates a metric card with visual feedback for changes
     */
    private JPanel createMetricCard(String title, JLabel valueLabel, Color accentColor) {
        RoundedPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        // Store the card reference in a class variable for highlighting later
        switch (title) {
            case "Pending Approvals":
                pendingApprovalsCard = card;
                break;
            case "Flagged Transactions":
                flaggedTransactionsCard = card;
                break;
            case "High-Risk Accounts":
                highRiskAccountsCard = card;
                break;
            case "New Customers":
                newCustomersCard = card;
                break;
        }
        
        // Top colored bar
        JPanel colorBar = new JPanel();
        colorBar.setBackground(accentColor);
        colorBar.setPreferredSize(new Dimension(50, 5));
        
        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Format value label
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        valueLabel.setForeground(accentColor);
        valueLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Add components to card
        card.add(colorBar, BorderLayout.NORTH);
        card.add(titleLabel, BorderLayout.WEST);
        card.add(valueLabel, BorderLayout.CENTER);
        
        // Add shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 5, 5),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        return card;
    }
    
    /**
     * Creates a tool card
     */
    private JPanel createToolCard(String title, String description, String navigateTo, Color accentColor) {
        RoundedPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Title with colored accent
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.X_AXIS));
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel colorBar = new JPanel();
        colorBar.setBackground(accentColor);
        colorBar.setPreferredSize(new Dimension(5, 24));
        colorBar.setMaximumSize(new Dimension(5, 24));
        
        JLabel titleLabel = new JLabel(" " + title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        
        titlePanel.add(colorBar);
        titlePanel.add(titleLabel);
        
        // Description
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(LIGHT_TEXT_COLOR);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton actionButton = new JButton("Access");
        actionButton.setBackground(accentColor);
        actionButton.setForeground(Color.WHITE);
        actionButton.setFocusPainted(false);
        actionButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        actionButton.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        actionButton.setOpaque(true);
        actionButton.setBorderPainted(false);
        
        // Add hover effects
        actionButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                actionButton.setBackground(accentColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                actionButton.setBackground(accentColor);
            }
        });
        
        // Add action listener
        actionButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                navigateToScreen(navigateTo);
            }
        });
        
        // Add components to content panel
        contentPanel.add(titlePanel);
        contentPanel.add(descLabel);
        
        // Add content and button to card
        card.add(contentPanel, BorderLayout.CENTER);
        card.add(actionButton, BorderLayout.SOUTH);
        
        // Add click listener to the entire card
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToScreen(navigateTo);
            }
        });
        
        // Add shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 5, 5),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        return card;
    }

    /**
     * Updates the metric labels with the latest data and applies highlight effects for changes
     */
    private void updateMetricLabels() {
        // Store current values to detect changes
        Map<String, Integer> currentValues = new HashMap<>();
        currentValues.put("pendingApprovals", pendingApprovals);
        currentValues.put("flaggedTransactions", flaggedTransactions);
        currentValues.put("highRiskAccounts", highRiskAccounts);
        currentValues.put("newCustomers", newCustomers);
        
        // Update labels with new values and highlight changes
        pendingApprovalsLabel.setText(String.valueOf(pendingApprovals));
        flaggedTransactionsLabel.setText(String.valueOf(flaggedTransactions));
        highRiskAccountsLabel.setText(String.valueOf(highRiskAccounts));
        newCustomersLabel.setText(String.valueOf(newCustomers));
        
        // Check for changes and highlight them
        if (previousValues.containsKey("pendingApprovals") && 
            previousValues.get("pendingApprovals") != pendingApprovals) {
            highlightMetricChange(pendingApprovalsCard, pendingApprovalsLabel, 
                                previousValues.get("pendingApprovals"), pendingApprovals);
        }
        
        if (previousValues.containsKey("flaggedTransactions") && 
            previousValues.get("flaggedTransactions") != flaggedTransactions) {
            highlightMetricChange(flaggedTransactionsCard, flaggedTransactionsLabel, 
                                previousValues.get("flaggedTransactions"), flaggedTransactions);
        }
        
        if (previousValues.containsKey("highRiskAccounts") && 
            previousValues.get("highRiskAccounts") != highRiskAccounts) {
            highlightMetricChange(highRiskAccountsCard, highRiskAccountsLabel, 
                                previousValues.get("highRiskAccounts"), highRiskAccounts);
        }
        
        if (previousValues.containsKey("newCustomers") && 
            previousValues.get("newCustomers") != newCustomers) {
            highlightMetricChange(newCustomersCard, newCustomersLabel, 
                                previousValues.get("newCustomers"), newCustomers);
        }
        
        // Store current values as previous for next update
        previousValues = new HashMap<>(currentValues);
    }
    
    /**
     * Highlights a metric card when its value changes with a visual effect
     */
    private void highlightMetricChange(JPanel card, JLabel valueLabel, int oldValue, int newValue) {
        // Change background color temporarily
        Color originalColor = card.getBackground();
        card.setBackground(HIGHLIGHT_COLOR);
        
        // Increase font size temporarily
        Font originalFont = valueLabel.getFont();
        valueLabel.setFont(new Font(originalFont.getName(), Font.BOLD, 42));
        
        // Add + or - indicator based on change direction
        String changeIndicator = (newValue > oldValue) ? "+" : "";
        if (newValue != oldValue) {
            int difference = newValue - oldValue;
            valueLabel.setText(String.valueOf(newValue) + " (" + changeIndicator + difference + ")");
        }
        
        // Create timer to revert the changes after 3 seconds
        Timer timer = new Timer(3000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                card.setBackground(originalColor);
                valueLabel.setFont(originalFont);
                valueLabel.setText(String.valueOf(newValue));
            }
        });
        timer.setRepeats(false);
        timer.start();
    }
    
    /**
     * Custom rounded panel with background color
     */
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
        SwingUtilities.invokeLater(() -> {
            try {
                // Test database connection
                Connection conn = DatabaseConnection.getConnection();
                conn.close();
                
                // If connection successful, show dashboard
                new ManagerDashboard().setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to database: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}