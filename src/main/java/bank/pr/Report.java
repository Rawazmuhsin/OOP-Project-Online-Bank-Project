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
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileWriter;
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
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
public class Report extends JFrame {
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    
    private String userName;
    private int accountId;
    private boolean isAdmin = true; // Set to true for admin version
    
    // Report data
    private double totalDeposits = 0;
    private double totalWithdrawals = 0;
    private double netBalance = 0;
    private int pendingTransactions = 0;
    private List<TransactionData> transactionList = new ArrayList<>();
    
    // UI Components
    private JComboBox<String> reportTypeComboBox;
    private JComboBox<String> dateRangeComboBox;
    private JLabel totalDepositsLabel;
    private JLabel totalWithdrawalsLabel;
    private JLabel netBalanceLabel;
    private JLabel pendingTransactionsLabel;
    private JTable transactionsTable;
    private DefaultTableModel tableModel;
    
    // Date range options
    private final String[] DATE_RANGES = {
        "Last 7 Days", "Last 30 Days", "Last 90 Days", "Year to Date", "Custom Range"
    };
    
    // Report type options
    private final String[] REPORT_TYPES = {
        "Transaction Summary", "Account Activity", "Pending Approvals", "User Activity", "System Overview"
    };
    
    // Class to hold transaction data
    private class TransactionData {
        int transactionId;
        int accountId;
        String userName;
        String transactionType;
        double amount;
        String date;
        String status;
        String description;
        
        TransactionData(int transactionId, int accountId, String userName, String transactionType, 
                       double amount, String date, String status, String description) {
            this.transactionId = transactionId;
            this.accountId = accountId;
            this.userName = userName;
            this.transactionType = transactionType;
            this.amount = amount;
            this.date = date;
            this.status = status;
            this.description = description;
        }
    }
    
    // Constructor
    public Report() {
        this("Admin", 0); // Default constructor with admin values
    }
    
    public Report(String userName, int accountId) {
        this.userName = userName;
        this.accountId = accountId;
        
        initializeUI();
        loadReportData(); // Load initial data
    }
    
    private void initializeUI() {
        setTitle("KOB Admin - Reports Dashboard");
        setSize(1100, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);
        
        // Create main content panel
        JPanel mainContentPanel = createMainContentPanel();
        JScrollPane scrollPane = new JScrollPane(mainContentPanel);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);
        
        setVisible(true);
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
        
       // Add logo image
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
        addMenuItem(menuPanel, "Dashboard", false);
        addMenuItem(menuPanel, "Customer Accounts", false);
        addMenuItem(menuPanel, "Transaction Oversight", false);
        addMenuItem(menuPanel, "Approval Queue", false);
        addMenuItem(menuPanel, "Reports", true);
        addMenuItem(menuPanel, "Audit Logs", false);
        
        sidebar.add(menuPanel);
        sidebar.add(Box.createVerticalGlue());
        
        // Add admin profile at bottom of sidebar
        JPanel profilePanel = new RoundedPanel(15, new Color(40, 50, 90));
        profilePanel.setLayout(new BoxLayout(profilePanel, BoxLayout.Y_AXIS));
        profilePanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        profilePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        profilePanel.setMaximumSize(new Dimension(250, 120));
        
        JLabel nameLabel = new JLabel(userName);
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
        logoutButton.addActionListener(e -> {
            int response = JOptionPane.showConfirmDialog(
                Report.this,
                "Are you sure you want to logout?",
                "Confirm Logout",
                JOptionPane.YES_NO_OPTION
            );
            
            if (response == JOptionPane.YES_OPTION) {
                dispose();
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
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
        if (screenName.equals("Reports")) {
            // Already on Reports, just refresh
            loadReportData();
            return;
        }
        
        dispose(); // Close the current window
        
        switch (screenName) {
            case "Dashboard":
                SwingUtilities.invokeLater(() -> new ManagerDashboard(accountId).setVisible(true));
                break;
            case "Customer Accounts":
                SwingUtilities.invokeLater(() -> new CustomerAccounts().setVisible(true));
                break;
            case "Transaction Oversight":
                SwingUtilities.invokeLater(() -> new ManageTransaction().setVisible(true));
                break;
            case "Approval Queue":
                SwingUtilities.invokeLater(() -> new ApproveTransaction(accountId).setVisible(true));
                break;
            case "Audit Logs":
                JOptionPane.showMessageDialog(null, 
                    "Audit Logs screen is under development.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> new ManagerDashboard(accountId).setVisible(true));
                break;
            default:
                SwingUtilities.invokeLater(() -> new ManagerDashboard(accountId).setVisible(true));
        }
    }
    
    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setLayout(null); // Use absolute positioning like the original design
        
        // Content panel with padding to match the design
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(0, 0, 610, 730);
        
        // Financial Reports Header
        JLabel titleLabel = new JLabel("Financial Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64));
        titleLabel.setBounds(20, 40, 250, 30);
        
        // Current date for last generated
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = dateFormat.format(new Date());
        JLabel dateLabel = new JLabel("Last generated: " + currentDate);
        dateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(108, 117, 125));
        dateLabel.setBounds(20, 70, 300, 20);
        
        // Admin user info
        JLabel adminLabel = new JLabel("Generated by: " + userName + " (Admin)");
        adminLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        adminLabel.setForeground(new Color(13, 110, 253));
        adminLabel.setBounds(20, 95, 300, 20);
        
        // Section dividers
        JSeparator topDivider = new JSeparator();
        topDivider.setForeground(new Color(222, 226, 230));
        topDivider.setBackground(new Color(222, 226, 230));
        topDivider.setBounds(20, 110, 570, 2);
        
        // Report Type Selector
        JLabel reportTypeLabel = new JLabel("Report Type:");
        reportTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        reportTypeLabel.setBounds(20, 130, 150, 20);
        
        reportTypeComboBox = new JComboBox<>(REPORT_TYPES);
        reportTypeComboBox.setBounds(20, 155, 250, 30);
        reportTypeComboBox.addActionListener(e -> loadReportData());
        
        // Date Range Selector
        JLabel dateRangeLabel = new JLabel("Date Range:");
        dateRangeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateRangeLabel.setBounds(300, 130, 150, 20);
        
        dateRangeComboBox = new JComboBox<>(DATE_RANGES);
        dateRangeComboBox.setBounds(300, 155, 250, 30);
        dateRangeComboBox.addActionListener(e -> loadReportData());
        
        // Middle divider
        JSeparator middleDivider = new JSeparator();
        middleDivider.setForeground(new Color(222, 226, 230));
        middleDivider.setBackground(new Color(222, 226, 230));
        middleDivider.setBounds(20, 210, 570, 2);
        
        // Financial Metrics
        JLabel financialSummaryLabel = new JLabel("Financial Summary");
        financialSummaryLabel.setFont(new Font("Arial", Font.BOLD, 18));
        financialSummaryLabel.setBounds(20, 230, 200, 25);
        
        // Create the summary cards
        JPanel depositCard = createSummaryCard("Total Deposits", "$0.00", new Color(209, 231, 221), new Color(25, 135, 84));
        depositCard.setBounds(20, 265, 135, 80);
        totalDepositsLabel = (JLabel) depositCard.getComponent(1);
        
        JPanel withdrawalCard = createSummaryCard("Total Withdrawals", "$0.00", new Color(248, 215, 218), new Color(220, 53, 69));
        withdrawalCard.setBounds(165, 265, 135, 80);
        totalWithdrawalsLabel = (JLabel) withdrawalCard.getComponent(1);
        
        JPanel balanceCard = createSummaryCard("Net Balance", "$0.00", new Color(207, 226, 255), new Color(13, 110, 253));
        balanceCard.setBounds(310, 265, 135, 80);
        netBalanceLabel = (JLabel) balanceCard.getComponent(1);
        
        JPanel pendingCard = createSummaryCard("Pending", "0", new Color(255, 243, 205), new Color(255, 193, 7));
        pendingCard.setBounds(455, 265, 135, 80);
        pendingTransactionsLabel = (JLabel) pendingCard.getComponent(1);
        
        // Bottom divider
        JSeparator bottomDivider = new JSeparator();
        bottomDivider.setForeground(new Color(222, 226, 230));
        bottomDivider.setBackground(new Color(222, 226, 230));
        bottomDivider.setBounds(20, 360, 570, 2);
        
        // Report Actions
        JLabel actionsLabel = new JLabel("Report Actions");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        actionsLabel.setBounds(20, 380, 200, 25);
        
        // Generate Report Button - PRESERVED FROM ORIGINAL
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.setBounds(20, 415, 150, 35);
        generateReportButton.setBackground(new Color(13, 110, 253));
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.setBorderPainted(false);
        generateReportButton.setFocusPainted(false);
        generateReportButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateReportButton.addActionListener(e -> generateReport());
        
        // Export CSV Button - PRESERVED FROM ORIGINAL
        JButton exportCsvButton = new JButton("Export as CSV");
        exportCsvButton.setBounds(180, 415, 150, 35);
        exportCsvButton.setBackground(new Color(25, 135, 84));
        exportCsvButton.setForeground(Color.WHITE);
        exportCsvButton.setBorderPainted(false);
        exportCsvButton.setFocusPainted(false);
        exportCsvButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportCsvButton.addActionListener(e -> exportReportToCsv());
        
        // Export Text Button - PRESERVED FROM ORIGINAL
        JButton exportTextButton = new JButton("Export as Text");
        exportTextButton.setBounds(340, 415, 150, 35);
        exportTextButton.setBackground(new Color(220, 53, 69));
        exportTextButton.setForeground(Color.WHITE);
        exportTextButton.setBorderPainted(false);
        exportTextButton.setFocusPainted(false);
        exportTextButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportTextButton.addActionListener(e -> exportReportToPdf());
        
        // Transaction table
        String[] columns = {"ID", "Account", "User", "Type", "Amount", "Date", "Status", "Description"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(25);
        transactionsTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        transactionsTable.setFont(new Font("Arial", Font.PLAIN, 12));
        
        // Custom cell renderers
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        
        DefaultTableCellRenderer amountRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                try {
                    String type = (String) table.getValueAt(row, 3); // Type column
                    if (type.equalsIgnoreCase("DEPOSIT")) {
                        comp.setForeground(new Color(25, 135, 84));
                    } else if (type.equalsIgnoreCase("WITHDRAW") || type.equalsIgnoreCase("WITHDRAWAL")) {
                        comp.setForeground(new Color(220, 53, 69));
                    } else {
                        comp.setForeground(Color.BLACK);
                    }
                } catch (Exception e) {
                    comp.setForeground(Color.BLACK);
                }
                return comp;
            }
        };
        amountRenderer.setHorizontalAlignment(JLabel.RIGHT);
        
        transactionsTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        transactionsTable.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        transactionsTable.getColumnModel().getColumn(4).setCellRenderer(amountRenderer);
        transactionsTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);
        
        JScrollPane tableScrollPane = new JScrollPane(transactionsTable);
        tableScrollPane.setBounds(20, 470, 570, 200);
        
        // Back to Dashboard Button
        JButton backToDashboardButton = new JButton("Back to Dashboard");
        backToDashboardButton.setBounds(20, 680, 180, 35);
        backToDashboardButton.setBackground(new Color(108, 117, 125));
        backToDashboardButton.setForeground(Color.WHITE);
        backToDashboardButton.setBorderPainted(false);
        backToDashboardButton.setFocusPainted(false);
        backToDashboardButton.setFont(new Font("Arial", Font.BOLD, 14));
        backToDashboardButton.addActionListener(e -> navigateToDashboard());
        
        // Add all components to the main panel
        mainPanel.add(titleLabel);
        mainPanel.add(dateLabel);
        mainPanel.add(adminLabel);
        mainPanel.add(topDivider);
        mainPanel.add(reportTypeLabel);
        mainPanel.add(reportTypeComboBox);
        mainPanel.add(dateRangeLabel);
        mainPanel.add(dateRangeComboBox);
        mainPanel.add(middleDivider);
        mainPanel.add(financialSummaryLabel);
        mainPanel.add(depositCard);
        mainPanel.add(withdrawalCard);
        mainPanel.add(balanceCard);
        mainPanel.add(pendingCard);
        mainPanel.add(bottomDivider);
        mainPanel.add(actionsLabel);
        mainPanel.add(generateReportButton);
        mainPanel.add(exportCsvButton);
        mainPanel.add(exportTextButton);
        mainPanel.add(tableScrollPane);
        mainPanel.add(backToDashboardButton);
        
        return mainPanel;
    }
    
    /**
     * Creates a summary card with title and value
     */
    private JPanel createSummaryCard(String title, String value, Color bgColor, Color textColor) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(textColor);
        titleLabel.setBounds(10, 5, 115, 20);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(textColor);
        valueLabel.setBounds(10, 30, 115, 25);
        
        card.add(titleLabel);
        card.add(valueLabel);
        
        return card;
    }
    
    /**
     * Set user information and update UI elements
     */
    public void setUserInfo(String userName, int accountId) {
        this.userName = userName;
        this.accountId = accountId;
        loadReportData();
        
        System.out.println("Report: Set user info - User: " + userName + ", Account ID: " + accountId);
        repaint(); // Refresh the UI to display the new user info
    }
    
    /**
     * Fetch and load report data based on current selection
     */
    private void loadReportData() {
        // If the combo box is not initialized yet, return
        if (reportTypeComboBox == null || dateRangeComboBox == null) {
            return;
        }
        
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        String dateRange = (String) dateRangeComboBox.getSelectedItem();
        
        // Reset values
        totalDeposits = 0;
        totalWithdrawals = 0;
        netBalance = 0;
        pendingTransactions = 0;
        transactionList.clear();
        
        System.out.println("Loading report data: " + reportType + ", " + dateRange);
        
        // Get date range in SQL format
        String dateFilter = getDateFilterSql(dateRange);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query;
            PreparedStatement stmt;
            ResultSet rs;
            
            // Load summary data - modified for admin to see all accounts
            if (isAdmin) {
                // Total deposits
                query = "SELECT SUM(amount) as total FROM transactions WHERE transaction_type = 'DEPOSIT' " + dateFilter;
                stmt = conn.prepareStatement(query);
                rs = stmt.executeQuery();
                if (rs.next() && rs.getObject("total") != null) {
                    totalDeposits = rs.getDouble("total");
                }
                
                // Total withdrawals
                query = "SELECT SUM(amount) as total FROM transactions WHERE transaction_type = 'Withdrawal' " + dateFilter;
                stmt = conn.prepareStatement(query);
                rs = stmt.executeQuery();
                if (rs.next() && rs.getObject("total") != null) {
                    totalWithdrawals = rs.getDouble("total");
                }
                
                // Pending transactions
                query = "SELECT COUNT(*) as count FROM transactions WHERE status = 'PENDING' " + dateFilter;
                stmt = conn.prepareStatement(query);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    pendingTransactions = rs.getInt("count");
                }
                
                // Load transaction list
                query = "SELECT t.transaction_id, t.account_id, a.username, t.transaction_type, " +
                        "t.amount, t.transaction_date, t.status, t.description " +
                        "FROM transactions t " +
                        "JOIN accounts a ON t.account_id = a.account_id " +
                        "WHERE 1=1 " + dateFilter + " " +
                        "ORDER BY t.transaction_date DESC LIMIT 100";
                
                stmt = conn.prepareStatement(query);
                rs = stmt.executeQuery();
                
                while (rs.next()) {
                    transactionList.add(new TransactionData(
                        rs.getInt("transaction_id"),
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getString("transaction_date"),
                        rs.getString("status"),
                        rs.getString("description")
                    ));
                }
            } else {
                // For non-admin users, only show their transactions
                // Total deposits
                query = "SELECT SUM(amount) as total FROM transactions WHERE transaction_type = 'DEPOSIT' AND account_id = ? " + dateFilter;
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, accountId);
                rs = stmt.executeQuery();
                if (rs.next() && rs.getObject("total") != null) {
                    totalDeposits = rs.getDouble("total");
                }
                
                // Similar queries for other data points would go here...
            }
            
            // Calculate net balance
            netBalance = totalDeposits - totalWithdrawals;
            
            // Update UI
            updateSummaryCards();
            updateTransactionTable();
            
        } catch (SQLException ex) {
            System.out.println("Error loading report data: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading report data: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update the summary cards with the loaded data
     */
    private void updateSummaryCards() {
        if (totalDepositsLabel != null) {
            totalDepositsLabel.setText(String.format("$%.2f", totalDeposits));
        }
        if (totalWithdrawalsLabel != null) {
            totalWithdrawalsLabel.setText(String.format("$%.2f", totalWithdrawals));
        }
        if (netBalanceLabel != null) {
            netBalanceLabel.setText(String.format("$%.2f", netBalance));
        }
        if (pendingTransactionsLabel != null) {
            pendingTransactionsLabel.setText(String.format("%d", pendingTransactions));
        }
    }
    
    /**
     * Update the transaction table with loaded data
     */
    private void updateTransactionTable() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Add transaction data to table
        for (TransactionData transaction : transactionList) {
            Object[] row = {
                transaction.transactionId,
                transaction.accountId,
                transaction.userName,
                transaction.transactionType,
                String.format("$%.2f", transaction.amount),
                transaction.date,
                transaction.status,
                transaction.description
            };
            tableModel.addRow(row);
        }
    }
    
    /**
     * Convert date range selection to SQL WHERE clause
     */
    private String getDateFilterSql(String dateRange) {
        if (dateRange == null) {
            return "";
        }
        
        switch (dateRange) {
            case "Last 7 Days":
                return "AND transaction_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
            case "Last 30 Days":
                return "AND transaction_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
            case "Last 90 Days":
                return "AND transaction_date >= DATE_SUB(NOW(), INTERVAL 90 DAY)";
            case "Year to Date":
                return "AND YEAR(transaction_date) = YEAR(CURRENT_DATE())";
            case "Custom Range":
                // In a real app, you'd show a date picker here
                return "";
            default:
                return "";
        }
    }
    
    /**
     * Generate a report based on selected criteria
     */
    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        String dateRange = (String) dateRangeComboBox.getSelectedItem();
        
        // Refresh data
        loadReportData();
        
        JOptionPane.showMessageDialog(this,
            "Report Generated Successfully\n\n" +
            "Report Type: " + reportType + "\n" +
            "Date Range: " + dateRange + "\n\n" +
            "Summary:\n" +
            "- Total Deposits: $" + String.format("%.2f", totalDeposits) + "\n" +
            "- Total Withdrawals: $" + String.format("%.2f", totalWithdrawals) + "\n" +
            "- Net Balance: $" + String.format("%.2f", netBalance) + "\n" +
            "- Pending Transactions: " + pendingTransactions + "\n\n" +
            "Total Transactions: " + transactionList.size(),
            "Report Generation",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Export report data to CSV
     */
    private void exportReportToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
        
        // Set default file name
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String defaultFileName = "KOB_Report_" + dateFormat.format(new Date()) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Add .csv extension if missing
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write report header
                String reportType = (String) reportTypeComboBox.getSelectedItem();
                String dateRange = (String) dateRangeComboBox.getSelectedItem();
                
                writer.write("Kurdish - O - Banking (KOB) Report\n");
                writer.write(reportType + " - " + dateRange + "\n");
                writer.write("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                writer.write("Generated by: " + userName + " (Admin)\n\n");
                
                // Write financial summary
                writer.write("FINANCIAL SUMMARY\n");
                writer.write("Total Deposits,$" + String.format("%.2f", totalDeposits) + "\n");
                writer.write("Total Withdrawals,$" + String.format("%.2f", totalWithdrawals) + "\n");
                writer.write("Net Balance,$" + String.format("%.2f", netBalance) + "\n");
                writer.write("Pending Transactions," + pendingTransactions + "\n\n");
                
                // Write transaction details
                writer.write("TRANSACTION DETAILS\n");
                writer.write("Transaction ID,Account ID,User,Type,Amount,Date,Status,Description\n");
                
                for (TransactionData transaction : transactionList) {
                    writer.write(String.format("%d,%d,%s,%s,%.2f,%s,%s,%s\n",
                        transaction.transactionId,
                        transaction.accountId,
                        transaction.userName,
                        transaction.transactionType,
                        transaction.amount,
                        transaction.date,
                        transaction.status,
                        transaction.description != null ? transaction.description.replace(",", " ") : ""
                    ));
                }
                
                JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Export report data to a formatted text file
     */
    private void exportReportToPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Formatted Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        
        // Set default file name
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String defaultFileName = "KOB_Report_" + dateFormat.format(new Date()) + ".txt";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Add .txt extension if missing
            if (!fileToSave.getAbsolutePath().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }
            
            try (FileWriter writer = new FileWriter(fileToSave)) {
                String reportType = (String) reportTypeComboBox.getSelectedItem();
                String dateRange = (String) dateRangeComboBox.getSelectedItem();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                
                // Create a nicely formatted text report
                
                // Title and metadata section
                writer.write("===============================================================\n");
                writer.write("             KURDISH - O - BANKING (KOB) REPORT                \n");
                writer.write("===============================================================\n\n");
                
                writer.write("REPORT INFORMATION:\n");
                writer.write("-------------------\n");
                writer.write(String.format("Report Type: %s\n", reportType));
                writer.write(String.format("Date Range: %s\n", dateRange));
                writer.write(String.format("Generated on: %s\n", currentDate));
                writer.write(String.format("Generated by: %s (Admin)\n\n", userName));
                
                // Financial summary section
                writer.write("FINANCIAL SUMMARY:\n");
                writer.write("------------------\n");
                writer.write(String.format("%-25s $%,.2f\n", "Total Deposits:", totalDeposits));
                writer.write(String.format("%-25s $%,.2f\n", "Total Withdrawals:", totalWithdrawals));
                writer.write(String.format("%-25s $%,.2f\n", "Net Balance:", netBalance));
                writer.write(String.format("%-25s %d\n\n", "Pending Transactions:", pendingTransactions));
                
                // Transaction details section
                if (!transactionList.isEmpty()) {
                    writer.write("TRANSACTION DETAILS:\n");
                    writer.write("--------------------\n");
                    
                    // Table header
                    writer.write(String.format("%-5s | %-8s | %-15s | %-10s | %-12s | %-19s | %-15s | %s\n",
                            "ID", "Acct ID", "User", "Type", "Amount", "Date", "Status", "Description"));
                    
                    // Table divider
                    StringBuilder divider = new StringBuilder();
                    for (int i = 0; i < 120; i++) {
                        divider.append("-");
                    }
                    writer.write(divider.toString() + "\n");
                    
                    // Table data
                    for (TransactionData transaction : transactionList) {
                        String description = transaction.description;
                        if (description == null) {
                            description = "";
                        }
                        
                        // Truncate or adjust description if too long
                        if (description.length() > 30) {
                            description = description.substring(0, 27) + "...";
                        }
                        
                        writer.write(String.format("%-5d | %-8d | %-15s | %-10s | $%-11.2f | %-19s | %-15s | %s\n",
                            transaction.transactionId,
                            transaction.accountId,
                            transaction.userName,
                            transaction.transactionType,
                            transaction.amount,
                            transaction.date,
                            transaction.status,
                            description));
                    }
                }
                
                // Footer
                writer.write("\n\n");
                writer.write("===============================================================\n");
                writer.write("                    END OF REPORT                              \n");
                writer.write("===============================================================\n");
                
                JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Navigate back to the appropriate dashboard based on user role
     */
    private void navigateToDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if (isAdmin) {
                // Navigate to ManagerDashboard for admin users
                ManagerDashboard managerDashboard = new ManagerDashboard(accountId);
                managerDashboard.setVisible(true);
            } else {
                // Navigate to normal user dashboard for non-admin users
                Dashbord dashboard = new Dashbord();
                dashboard.setUserInfo(userName, accountId);
                dashboard.setVisible(true);
            }
        });
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
            Report report = new Report("Admin", 0);
            report.setVisible(true);
        });
    }
}