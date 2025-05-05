package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
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
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class ApproveTransaction extends JFrame {
    // Main color palette - matching ManagerDashboard
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    
    // Status colors
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color WARNING_COLOR = new Color(255, 153, 0);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    
    private DefaultTableModel tableModel;
    private JTable table;
    private final String DB_URL = "jdbc:mysql://localhost:3306/user_management";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "Makwan2004111";

    // Add a reference to the notification label
    private JLabel notificationLabel;
    
    // Add adminId field to track the current admin
    private int adminId = 0;

    public ApproveTransaction() {
        this(0); // Default constructor with default admin ID
    }

    public ApproveTransaction(int adminId) {
        this.adminId = adminId;
        
        setTitle("KOB Manager - Approval Queue");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set background color
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create sidebar panel using gradient background like ManagerDashboard
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createApprovalContentPanel();
        add(new JScrollPane(mainContentPanel), BorderLayout.CENTER);

        // Load pending transactions
        loadPendingTransactions();

        setVisible(true);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background like ManagerDashboard
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
        
        // Add logo
        JLabel logoLabel = new JLabel();
        try {
            // Path to your saved logo
            ImageIcon logoIcon = new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png");
            // Resize the image
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
        addMenuItem(menuPanel, "Approval Queue", true); // Active menu
        addMenuItem(menuPanel, "Reports", false);
        addMenuItem(menuPanel, "Audit Logs", false);
        
        sidebar.add(menuPanel);
        sidebar.add(Box.createVerticalGlue());
        
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
        dispose(); // Close the current window
        
        switch (screenName) {
            case "Dashboard":
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
                break;
            case "Customer Accounts":
                SwingUtilities.invokeLater(() -> new CustomerAccounts(adminId).setVisible(true));
                break;
            case "Transaction Oversight":
                SwingUtilities.invokeLater(() -> new ManageTransaction(adminId).setVisible(true));
                break;
            case "Reports":
                SwingUtilities.invokeLater(() -> new Report().setVisible(true));
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

    private JPanel createApprovalContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        // Content container with rounded corners - matching Dashboard card style
        RoundedPanel contentPanel = new RoundedPanel(15, Color.WHITE);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 25, 30));

        // Header Panel with title and subtitle
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Approval Queue Header
        JLabel titleLabel = new JLabel("Approve Pending Transactions");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Notification Label
        notificationLabel = new JLabel("Transactions awaiting approval");
        notificationLabel.setFont(new Font("SansSerif", Font.PLAIN, 15));
        notificationLabel.setForeground(DANGER_COLOR);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        headerPanel.add(titleLabel, BorderLayout.NORTH);
        headerPanel.add(notificationLabel, BorderLayout.SOUTH);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        contentPanel.add(headerPanel);

        // Table with rounded corners and improved styling
        String[] columnNames = {"ID", "DATE", "CUSTOMER", "ACCOUNT", "AMOUNT", "TYPE", "ACTIONS"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only make Actions column editable
            }
        };
        table = new JTable(tableModel);
        table.setRowHeight(55);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("SansSerif", Font.PLAIN, 14));
        table.setSelectionBackground(new Color(240, 245, 255));
        table.setIntercellSpacing(new Dimension(0, 5));

        // Custom renderers for different columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new TransactionTypeRenderer());

        // Set both renderer and editor for the Actions column
        ActionButtonsPanel actionPanel = new ActionButtonsPanel(this);
        table.getColumnModel().getColumn(6).setCellRenderer(actionPanel);
        table.getColumnModel().getColumn(6).setCellEditor(actionPanel);

        // Hide the ID column (used for database operations)
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);

        // Set column widths
        table.getColumnModel().getColumn(1).setPreferredWidth(120);
        table.getColumnModel().getColumn(2).setPreferredWidth(170);
        table.getColumnModel().getColumn(3).setPreferredWidth(120);
        table.getColumnModel().getColumn(4).setPreferredWidth(120);
        table.getColumnModel().getColumn(5).setPreferredWidth(120);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        // Wrap table in a scroll pane with custom border
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(scrollPane);

        // Bulk Action Buttons
        JPanel bulkActionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 25));
        bulkActionPanel.setBackground(Color.WHITE);
        bulkActionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton approveAllButton = createActionButton("Approve All", true);
        approveAllButton.addActionListener(e -> approveAllTransactions());
        bulkActionPanel.add(approveAllButton);

        JButton rejectAllButton = createActionButton("Reject All", false);
        rejectAllButton.addActionListener(e -> rejectAllTransactions());
        bulkActionPanel.add(rejectAllButton);

        contentPanel.add(bulkActionPanel);
        
        // Add spacing
        contentPanel.add(Box.createVerticalStrut(15));

        // Dashboard Button styled like ManagerDashboard
        JButton dashboardButton = new JButton("← Dashboard");
        dashboardButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dashboardButton.setBackground(new Color(248, 250, 252));
        dashboardButton.setForeground(TEXT_COLOR);
        dashboardButton.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        dashboardButton.setFocusPainted(false);
        dashboardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dashboardButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardButton.setMaximumSize(new Dimension(150, 40));
        dashboardButton.setPreferredSize(new Dimension(150, 40));

        // Update the dashboard button to navigate back with the admin ID
        dashboardButton.addActionListener(e -> {
            dispose();
            // Navigate to Dashboard with the admin ID
            SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
        });

        // Add hover effect to dashboard button
        dashboardButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                dashboardButton.setBackground(new Color(240, 242, 245));
            }

            public void mouseExited(MouseEvent e) {
                dashboardButton.setBackground(new Color(248, 250, 252));
            }
        });

        contentPanel.add(dashboardButton);

        // Add footer with version info - like ManagerDashboard
        JPanel footerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        footerPanel.setOpaque(false);
        footerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        footerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel versionLabel = new JLabel("Kurdish-O-Banking Manager Portal v1.0 | © 2025 KOB. All rights reserved.");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(LIGHT_TEXT_COLOR);
        footerPanel.add(versionLabel);
        
        contentPanel.add(footerPanel);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        return mainPanel;
    }

    private JButton createActionButton(String text, boolean isApprove) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isApprove) {
                    g2.setPaint(new GradientPaint(0, 0, new Color(230, 255, 250), 0, getHeight(), new Color(210, 255, 240)));
                } else {
                    g2.setPaint(new GradientPaint(0, 0, new Color(255, 236, 236), 0, getHeight(), new Color(255, 220, 220)));
                }
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        if (isApprove) {
            button.setForeground(SECONDARY_COLOR);
        } else {
            button.setForeground(DANGER_COLOR);
        }
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setPreferredSize(new Dimension(120, 40));
        return button;
    }

    // Load pending transactions from database
    private void loadPendingTransactions() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT t.transaction_id, t.transaction_date, a.username, " +
                    "a.account_id, t.amount, t.transaction_type, t.description " +
                    "FROM transactions t " +
                    "JOIN accounts a ON t.account_id = a.account_id " +
                    "WHERE t.status = 'PENDING' " +
                    "ORDER BY t.transaction_date DESC";
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();

            // Clear existing data
            tableModel.setRowCount(0);

            // Add new data
            int rowCount = 0;
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("transaction_id"));
                row.add(rs.getDate("transaction_date"));
                row.add(rs.getString("username"));
                // Display account ID
                String accountId = rs.getString("account_id");
                row.add("Account ID: " + accountId);
                row.add(rs.getDouble("amount"));
                row.add(rs.getString("transaction_type"));
                row.add("Pending"); // This will be replaced by the ActionRenderer
                tableModel.addRow(row);
                rowCount++;
            }

            // Update notification label with count
            int pendingCount = tableModel.getRowCount();
            notificationLabel.setText(pendingCount + " transaction" + (pendingCount != 1 ? "s" : "") + " awaiting approval");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading pending transactions: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public void approveTransaction(int transactionId) {
        Connection conn = null;
        try {
            System.out.println("\n========== TRANSACTION APPROVAL LOG ==========");
            System.out.println("Starting approval process for Transaction ID: " + transactionId);
            
            // Get a connection
            System.out.println("[1] Opening database connection...");
            try {
                conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                System.out.println("    ✓ Connection established successfully");
            } catch (SQLException e) {
                System.out.println("    ✗ FAILED to establish database connection");
                System.out.println("    Error message: " + e.getMessage());
                throw e;
            }
            
            // Get transaction details
            System.out.println("[2] Retrieving transaction details...");
            String transactionQuery = "SELECT transaction_id, account_id, amount, transaction_type, description, status " +
                    "FROM transactions WHERE transaction_id = ?";
            System.out.println("    SQL Query: " + transactionQuery);
            System.out.println("    Parameters: [" + transactionId + "]");
            
            PreparedStatement transactionStmt = null;
            ResultSet rs = null;
            try {
                transactionStmt = conn.prepareStatement(transactionQuery);
                transactionStmt.setInt(1, transactionId);
                rs = transactionStmt.executeQuery();
                System.out.println("    ✓ Query executed successfully");
            } catch (SQLException e) {
                System.out.println("    ✗ FAILED to execute transaction query");
                System.out.println("    Error message: " + e.getMessage());
                throw e;
            }
    
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                double amount = rs.getDouble("amount");
                String type = rs.getString("transaction_type");
                String description = rs.getString("description");
                String status = rs.getString("status");
    
                System.out.println("    ✓ Transaction found:");
                System.out.println("      - Account ID: " + accountId);
                System.out.println("      - Amount: $" + amount);
                System.out.println("      - Type: " + type);
                System.out.println("      - Status: " + status);
                System.out.println("      - Description: " + (description != null ? description : "N/A"));
                
                // IMPORTANT FIX: Convert transaction type to uppercase for comparison
                // This fixes the case-sensitivity issue when comparing transaction types
                type = type.toUpperCase();
                System.out.println("      - Normalized Type: " + type);
    
                // Verify transaction is pending
                if (!status.equals("PENDING")) {
                    System.out.println("    ✗ Transaction is not in PENDING state, current status: " + status);
                    JOptionPane.showMessageDialog(this, 
                        "This transaction has already been processed: " + status, 
                        "Warning", 
                        JOptionPane.WARNING_MESSAGE);
                    return;
                }
                System.out.println("    ✓ Confirmed transaction is in PENDING state");
    
                // Get account information
                System.out.println("[3] Retrieving account balance information...");
                String accountQuery = "SELECT balance FROM accounts WHERE account_id = ?";
                System.out.println("    SQL Query: " + accountQuery);
                System.out.println("    Parameters: [" + accountId + "]");
                
                PreparedStatement accountStmt = null;
                ResultSet accountRs = null;
                try {
                    accountStmt = conn.prepareStatement(accountQuery);
                    accountStmt.setInt(1, accountId);
                    accountRs = accountStmt.executeQuery();
                    System.out.println("    ✓ Account query executed successfully");
                } catch (SQLException e) {
                    System.out.println("    ✗ FAILED to execute account query");
                    System.out.println("    Error message: " + e.getMessage());
                    throw e;
                }
                
                if (!accountRs.next()) {
                    System.out.println("    ✗ Account ID " + accountId + " not found in database");
                    JOptionPane.showMessageDialog(this, 
                        "Account ID " + accountId + " not found in the database.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                double currentBalance = accountRs.getDouble("balance");
                System.out.println("    ✓ Current account balance: $" + currentBalance);
                
                // Start database transaction
                System.out.println("[4] Beginning database transaction...");
                try {
                    conn.setAutoCommit(false);
                    System.out.println("    ✓ Auto-commit disabled, transaction started");
                } catch (SQLException e) {
                    System.out.println("    ✗ FAILED to disable auto-commit");
                    System.out.println("    Error message: " + e.getMessage());
                    throw e;
                }
                
                try {
                    double newBalance = currentBalance;
                    
                    // Calculate new balance based on transaction type
                    System.out.println("[5] Calculating new balance based on transaction type: '" + type + "'...");
                    if (type.equals("DEPOSIT")) {
                        newBalance = currentBalance + amount;
                        System.out.println("    ✓ Deposit transaction - New Balance: $" + newBalance);
                        System.out.println("      (Current: $" + currentBalance + " + Deposit: $" + amount + ")");
                    } else  {
                        newBalance = currentBalance - amount;
                        System.out.println("    ✓ Withdrawal transaction - New Balance: $" + newBalance);
                        System.out.println("      (Current: $" + currentBalance + " - Withdrawal: $" + amount + ")");
                        
                        // Check for sufficient funds
                        if (newBalance < 0) {
                            System.out.println("    ✗ INSUFFICIENT FUNDS for withdrawal");
                            System.out.println("      Withdrawal amount: $" + amount + ", Current balance: $" + currentBalance);
                            throw new SQLException("Insufficient funds for withdrawal. Current balance: $" + currentBalance);
                        }
                    }
                    
                    // Update the account balance
                    System.out.println("[6] Updating account balance...");
                    String updateBalanceQuery = "UPDATE accounts SET balance = ? WHERE account_id = ?";
                    System.out.println("    SQL Query: " + updateBalanceQuery);
                    System.out.println("    Parameters: [" + newBalance + ", " + accountId + "]");
                    
                    PreparedStatement updateStmt = null;
                    int rowsAffected = 0;
                    try {
                        updateStmt = conn.prepareStatement(updateBalanceQuery);
                        updateStmt.setDouble(1, newBalance);
                        updateStmt.setInt(2, accountId);
                        rowsAffected = updateStmt.executeUpdate();
                        System.out.println("    ✓ Account balance update executed successfully");
                        System.out.println("      Rows affected: " + rowsAffected);
                    } catch (SQLException e) {
                        System.out.println("    ✗ FAILED to update account balance");
                        System.out.println("    Error message: " + e.getMessage());
                        throw e;
                    }
                    
                    if (rowsAffected <= 0) {
                        System.out.println("    ✗ No rows affected by balance update. Account ID might be incorrect.");
                        throw new SQLException("Failed to update account balance. No rows affected.");
                    }
                    
                    // Update transaction status
                    System.out.println("[7] Updating transaction status to APPROVED...");
                    String updateStatusQuery = "UPDATE transactions SET status = ?, approval_date = NOW() WHERE transaction_id = ?";
                    System.out.println("    SQL Query: " + updateStatusQuery);
                    System.out.println("    Parameters: [APPROVED, " + transactionId + "]");
                    
                    PreparedStatement updateStatusStmt = null;
                    int statusRowsAffected = 0;
                    try {
                        updateStatusStmt = conn.prepareStatement(updateStatusQuery);
                        updateStatusStmt.setString(1, "APPROVED");
                        updateStatusStmt.setInt(2, transactionId);
                        statusRowsAffected = updateStatusStmt.executeUpdate();
                        System.out.println("    ✓ Transaction status update executed successfully");
                        System.out.println("      Rows affected: " + statusRowsAffected);
                    } catch (SQLException e) {
                        System.out.println("    ✗ FAILED to update transaction status");
                        System.out.println("    Error message: " + e.getMessage());
                        throw e;
                    }
                    
                    if (statusRowsAffected <= 0) {
                        System.out.println("    ✗ No rows affected by status update. Transaction ID might be incorrect.");
                        throw new SQLException("Failed to update transaction status. No rows affected.");
                    }
                    
                    // Verify the balance update
                    System.out.println("[8] Verifying account balance update...");
                    PreparedStatement verifyStmt = null;
                    ResultSet verifyRs = null;
                    try {
                        verifyStmt = conn.prepareStatement(accountQuery);
                        verifyStmt.setInt(1, accountId);
                        verifyRs = verifyStmt.executeQuery();
                        System.out.println("    ✓ Verification query executed successfully");
                    } catch (SQLException e) {
                        System.out.println("    ✗ FAILED to execute verification query");
                        System.out.println("    Error message: " + e.getMessage());
                        throw e;
                    }
                    
                    if (verifyRs.next()) {
                        double verifiedBalance = verifyRs.getDouble("balance");
                        System.out.println("    ✓ Verified current balance: $" + verifiedBalance);
                        System.out.println("      Expected balance: $" + newBalance);
                        
                        if (Math.abs(verifiedBalance - newBalance) > 0.01) {
                            System.out.println("    ✗ BALANCE VERIFICATION FAILED!");
                            System.out.println("      Expected: $" + newBalance + ", Actual: $" + verifiedBalance);
                            throw new SQLException("Balance verification failed! Expected: $" + newBalance + ", Actual: $" + verifiedBalance);
                        } else {
                            System.out.println("    ✓ Balance verification successful!");
                        }
                    } else {
                        System.out.println("    ✗ Account not found during verification!");
                        throw new SQLException("Account not found during verification!");
                    }
                    
                    // Commit all changes
                    System.out.println("[9] Committing transaction...");
                    try {
                        conn.commit();
                        System.out.println("    ✓ Transaction committed successfully");
                    } catch (SQLException e) {
                        System.out.println("    ✗ FAILED to commit transaction");
                        System.out.println("    Error message: " + e.getMessage());
                        throw e;
                    }
                    
                    System.out.println("✓✓✓ TRANSACTION APPROVED SUCCESSFULLY ✓✓✓");
                    
                    // Show success message with styled dialog
                    JOptionPane.showMessageDialog(this,
                        "Transaction approved successfully",
                        "Approved",
                        JOptionPane.INFORMATION_MESSAGE);
                    
                } catch (SQLException e) {
                    // Roll back in case of any error
                    System.out.println("[!] ERROR OCCURRED: " + e.getMessage());
                    e.printStackTrace();
                    
                    System.out.println("[!] Attempting to roll back transaction...");
                    try {
                        conn.rollback();
                        System.out.println("    ✓ Transaction rolled back successfully");
                    } catch (SQLException rollbackEx) {
                        System.out.println("    ✗ FAILED to roll back transaction");
                        System.out.println("    Error message: " + rollbackEx.getMessage());
                        rollbackEx.printStackTrace();
                    }
                    
                    // Show error message with styled dialog
                    JOptionPane.showMessageDialog(this,
                        "Error approving transaction: " + e.getMessage(),
                        "Database Error",
                        JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Restore auto-commit
                    System.out.println("[10] Restoring auto-commit...");
                    try {
                        conn.setAutoCommit(true);
                        System.out.println("    ✓ Auto-commit restored successfully");
                    } catch (SQLException autoCommitEx) {
                        System.out.println("    ✗ FAILED to restore auto-commit");
                        System.out.println("    Error message: " + autoCommitEx.getMessage());
                        autoCommitEx.printStackTrace();
                    }
                }
            } else {
                System.out.println("    ✗ Transaction ID " + transactionId + " not found");
                JOptionPane.showMessageDialog(this,
                    "Transaction ID " + transactionId + " not found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
            
            // Refresh the list of pending transactions
            System.out.println("[11] Refreshing pending transactions list...");
            try {
                loadPendingTransactions();
                System.out.println("    ✓ Pending transactions list refreshed");
            } catch (Exception e) {
                System.out.println("    ✗ Error refreshing transaction list: " + e.getMessage());
                e.printStackTrace();
            }
            System.out.println("============ END OF TRANSACTION LOG ============\n");
            
        } catch (SQLException e) {
            System.out.println("[!] CRITICAL DATABASE ERROR: " + e.getMessage());
            e.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error approving transaction: " + e.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        } finally {
            // Close connection
            if (conn != null) {
                try {
                    conn.close();
                    System.out.println("[12] Database connection closed");
                } catch (SQLException e) {
                    System.out.println("[!] Error closing database connection: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        }
    }
    
    
    // Reject a specific transaction
    public void rejectTransaction(int transactionId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = "UPDATE transactions SET status = 'REJECTED', approval_date = NOW() WHERE transaction_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, transactionId);
            updateStmt.executeUpdate();

            JOptionPane.showMessageDialog(this,
                    "Transaction rejected",
                    "Rejected",
                    JOptionPane.INFORMATION_MESSAGE);

            loadPendingTransactions(); // Refresh the list

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this,
                    "Error rejecting transaction: " + e.getMessage(),
                    "Database Error",
                    JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Approve all pending transactions
    private void approveAllTransactions() {
        int pendingCount = tableModel.getRowCount();
        if (pendingCount == 0) {
            JOptionPane.showMessageDialog(this,
                    "No pending transactions to approve",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to approve all " + pendingCount + " pending transactions?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int transactionId = (int) tableModel.getValueAt(i, 0);
                approveTransaction(transactionId);
            }
        }
    }

    // Reject all pending transactions
    private void rejectAllTransactions() {
        int pendingCount = tableModel.getRowCount();
        if (pendingCount == 0) {
            JOptionPane.showMessageDialog(this,
                    "No pending transactions to reject",
                    "Information",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to reject all " + pendingCount + " pending transactions?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE);

        if (response == JOptionPane.YES_OPTION) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int transactionId = (int) tableModel.getValueAt(i, 0);
                rejectTransaction(transactionId);
            }
        }
    }

    // Custom renderer for Amount column
    private static class AmountRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            label.setBorder(new EmptyBorder(0, 0, 0, 15));

            // Format amount with dollar sign and two decimal places
            if (value instanceof Double) {
                double amount = (Double) value;
                String formattedAmount = String.format("$%.2f", amount);
                label.setText(formattedAmount);

                // Color amounts based on transaction type
                String type = table.getValueAt(row, 5).toString();
                if (type.equals("DEPOSIT")) {
                    label.setForeground(SUCCESS_COLOR); // Green for deposits
                } else {
                    label.setForeground(DANGER_COLOR); // Red for withdrawals/transfers
                }

                if (isSelected) {
                    label.setForeground(label.getForeground().darker());
                }
            }
            return label;
        }
    }

    // Custom renderer for Transaction Type column
    private static class TransactionTypeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            // Set font
            label.setFont(new Font("SansSerif", Font.BOLD, 12));

            if (value.equals("DEPOSIT")) {
                label.setBackground(new Color(230, 255, 250)); // #e6fffa
                label.setForeground(SECONDARY_COLOR); // #0d6efd
                label.setText("DEPOSIT");
            } else if (value.equals("WITHDRAW")) {
                label.setBackground(new Color(255, 236, 236)); // #ffecec
                label.setForeground(DANGER_COLOR); // #dc3545
                label.setText("WITHDRAW");
            } else if (value.equals("TRANSFER")) {
                label.setBackground(new Color(255, 243, 205)); // Light yellow
                label.setForeground(ACCENT_COLOR); // Orange
                label.setText("TRANSFER");
            }

            // Rounded corners
            label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

            if (isSelected) {
                label.setBackground(label.getBackground().darker());
            }

            return label;
        }
    }

    // Custom combined renderer and editor for Action column with buttons
    private static class ActionButtonsPanel extends AbstractCellEditor implements TableCellRenderer, TableCellEditor {
        private JPanel panel;
        private JButton approveButton;
        private JButton rejectButton;
        private ApproveTransaction parent;
        private int currentTransactionId;

        public ActionButtonsPanel(ApproveTransaction parent) {
            this.parent = parent;
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 5));
            panel.setOpaque(true);

            approveButton = new JButton("Approve") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setPaint(new GradientPaint(0, 0, new Color(230, 255, 250), 0, getHeight(), new Color(210, 255, 240)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);g2.dispose();
                    super.paintComponent(g);
                }
            };
            approveButton.setFont(new Font("SansSerif", Font.BOLD, 12));
            approveButton.setForeground(SECONDARY_COLOR);
            approveButton.setContentAreaFilled(false);
            approveButton.setBorderPainted(false);
            approveButton.setFocusPainted(false);
            approveButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            approveButton.setPreferredSize(new Dimension(85, 30));

            rejectButton = new JButton("Reject") {
                @Override
                protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setPaint(new GradientPaint(0, 0, new Color(255, 236, 236), 0, getHeight(), new Color(255, 220, 220)));
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            rejectButton.setFont(new Font("SansSerif", Font.BOLD, 12));
            rejectButton.setForeground(DANGER_COLOR);
            rejectButton.setContentAreaFilled(false);
            rejectButton.setBorderPainted(false);
            rejectButton.setFocusPainted(false);
            rejectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rejectButton.setPreferredSize(new Dimension(85, 30));

            // Add action listeners
            approveButton.addActionListener(e -> {
                try {
                    parent.approveTransaction(currentTransactionId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, "Error approving transaction: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    cancelCellEditing();
                }
            });

            rejectButton.addActionListener(e -> {
                try {
                    parent.rejectTransaction(currentTransactionId);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(parent, "Error rejecting transaction: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                } finally {
                    cancelCellEditing();
                }
            });

            // Add hover effects
            approveButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    approveButton.setForeground(SECONDARY_COLOR.darker());
                }

                public void mouseExited(MouseEvent e) {
                    approveButton.setForeground(SECONDARY_COLOR);
                }
            });

            rejectButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    rejectButton.setForeground(DANGER_COLOR.darker());
                }

                public void mouseExited(MouseEvent e) {
                    rejectButton.setForeground(DANGER_COLOR);
                }
            });

            panel.add(approveButton);
            panel.add(rejectButton);
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int column) {
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                                                      boolean isSelected, int row, int column) {
            // Store the transaction ID for the action buttons
            currentTransactionId = (int) table.getValueAt(row, 0);
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }

        @Override
        public Object getCellEditorValue() {
            return "Pending";
        }

        @Override
        public boolean isCellEditable(EventObject e) {
            return true;
        }
    }
    
    // Custom rounded panel with background color
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
                // Test database connection first
                Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/user_management", "root", "Makwan2004111");
                conn.close();
                
                // If connection successful, show the UI
                new ApproveTransaction().setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to database: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}