package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.Box; 

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

/**
 * This class displays all transactions for a specific customer account
 */
public class CustomerTransactions extends JFrame {
    
    private DefaultTableModel tableModel;
    private JTable transactionsTable;
    private int adminId;
    private int accountId;
    private String customerName;
    private String accountType;
    private double accountBalance;
    
    /**
     * Constructor that takes account ID and admin ID
     * @param accountId The customer account ID to show transactions for
     * @param adminId The admin ID for return navigation
     */
    public CustomerTransactions(int accountId, int adminId) {
        this.accountId = accountId;
        this.adminId = adminId;
        
        // Load customer information first
        if (loadCustomerInfo()) {
            initialize();
            loadTransactionsData();
        } else {
            JOptionPane.showMessageDialog(this,
                "Could not find account with ID: " + accountId,
                "Account Not Found",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> new CustomerAccounts(adminId).setVisible(true));
        }
    }
    
    /**
     * Loads basic customer account information
     * @return true if account was found, false otherwise
     */
    private boolean loadCustomerInfo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT username, account_type, balance FROM accounts WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, accountId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                customerName = rs.getString("username");
                accountType = rs.getString("account_type");
                accountBalance = rs.getDouble("balance");
                return true;
            }
            
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        return false;
    }
    
    /**
     * Initialize the UI components
     */
    private void initialize() {
        setTitle("Transaction History for Account #" + accountId);
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createTransactionsContentPanel();
        add(new JScrollPane(mainContentPanel), BorderLayout.CENTER);

        setVisible(true);
    }
    
    /**
     * Creates the sidebar navigation panel
     */
    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, 800));
        sidebarPanel.setBackground(new Color(26, 32, 44)); // #1a202c
        sidebarPanel.setLayout(null);

        // Sidebar title
        JLabel titleLabel = new JLabel("KOB Manager");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 200, 30);
        sidebarPanel.add(titleLabel);

        // Back to Customer Accounts button
        JButton backButton = new JButton("← Back to Accounts");
        backButton.setBackground(new Color(52, 58, 64)); // #343a40
        backButton.setForeground(Color.WHITE);
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        backButton.setBounds(20, 100, 210, 40);
        backButton.setHorizontalAlignment(SwingConstants.LEFT);
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new CustomerAccounts(adminId).setVisible(true));
        });
        sidebarPanel.add(backButton);

        // Active sidebar button (Customer Transactions)
        JButton transactionsButton = new JButton("Account Transactions");
        transactionsButton.setBackground(new Color(13, 110, 253)); // #0d6efd
        transactionsButton.setForeground(Color.WHITE);
        transactionsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        transactionsButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        transactionsButton.setBounds(20, 150, 210, 40);
        transactionsButton.setHorizontalAlignment(SwingConstants.LEFT);
        transactionsButton.setFocusPainted(false);
        sidebarPanel.add(transactionsButton);

        // Other sidebar buttons
        String[] menuItems = {"Dashboard", "Customer Accounts", "Transaction Oversight", "Reports", "Audit Logs"};
        int yPos = 230;
        for (String item : menuItems) {
            JButton menuButton = new JButton(item);
            menuButton.setBackground(new Color(26, 32, 44)); // #1a202c
            menuButton.setForeground(Color.WHITE);
            menuButton.setFont(new Font("Arial", Font.PLAIN, 14));
            menuButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
            menuButton.setBounds(20, yPos, 210, 30);
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setFocusPainted(false);
            
            // Hover effect
            menuButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    menuButton.setBackground(new Color(52, 58, 64)); // #343a40
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    menuButton.setBackground(new Color(26, 32, 44)); // #1a202c
                }
            });
            
            // Add action listener for navigation
            menuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    navigateToScreen(item);
                }
            });
            
            sidebarPanel.add(menuButton);
            yPos += 40;
        }

        return sidebarPanel;
    }
    
    /**
     * Creates the main content panel for transactions
     */
    private JPanel createTransactionsContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 244, 248)); // #f0f4f8
        mainPanel.setLayout(new BorderLayout());

        // Content container with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Customer Info Panel
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        infoPanel.setBackground(new Color(248, 250, 252)); // #f8fafc
        infoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(235, 237, 239)), 
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel customerInfoLabel = new JLabel("<html><b>Customer:</b> " + customerName + 
            " | <b>Account ID:</b> " + accountId + 
            " | <b>Type:</b> " + accountType + 
            " | <b>Balance:</b> $" + new DecimalFormat("#,##0.00").format(accountBalance) + "</html>");
        customerInfoLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        infoPanel.add(customerInfoLabel);
        
        // Make the info panel full width
        infoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        infoPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, infoPanel.getPreferredSize().height));
        contentPanel.add(infoPanel);
        
        // Add some spacing
        contentPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        
        // Transaction History Header
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64)); // #343a40
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        // Create table with data from database
        String[] columnNames = {"TRANSACTION ID", "TYPE", "AMOUNT", "DATE", "DESCRIPTION", "STATUS", "APPROVAL DATE"};
        
        // Create table model for dynamic updates
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table non-editable
            }
        };

        transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(30);
        transactionsTable.setShowGrid(false);
        transactionsTable.setIntercellSpacing(new Dimension(0, 0));
        transactionsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Custom renderer for status column
        transactionsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);

                if (value.equals("APPROVED")) {
                    label.setBackground(new Color(230, 255, 250)); // #e6fffa
                    label.setForeground(new Color(13, 110, 253)); // #0d6efd
                    label.setBorder(BorderFactory.createLineBorder(new Color(230, 255, 250)));
                } else if (value.equals("PENDING")) {
                    label.setBackground(new Color(255, 250, 230)); // #fffae6
                    label.setForeground(new Color(255, 153, 0)); // #ff9900
                    label.setBorder(BorderFactory.createLineBorder(new Color(255, 250, 230)));
                } else {
                    label.setBackground(new Color(255, 236, 236)); // #ffecec
                    label.setForeground(new Color(220, 53, 69)); // #dc3545
                    label.setBorder(BorderFactory.createLineBorder(new Color(255, 236, 236)));
                }
                return label;
            }
        });
        
        // Custom renderer for amount column to right-align and format currency
        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private final DecimalFormat formatter = new DecimalFormat("$#,##0.00");
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                
                // Format amount as currency
                if (value != null) {
                    try {
                        double amount = Double.parseDouble(value.toString());
                        // Get transaction type to color-code amounts
                        String type = (String)table.getValueAt(row, 1);
                        if (type.equals("Withdrawal") || type.contains("Transfer") && !type.contains("from")) {
                            // Negative for withdrawals and outgoing transfers
                            label.setText(formatter.format(-amount));
                            label.setForeground(new Color(220, 53, 69)); // Red for negative
                        } else {
                            // Positive for deposits and incoming transfers
                            label.setText(formatter.format(amount));
                            label.setForeground(new Color(25, 135, 84)); // Green for positive
                        }
                    } catch (Exception e) {
                        // Keep original text if parsing fails
                    }
                }
                
                return label;
            }
        });

        // Style table header
        JTableHeader header = transactionsTable.getTableHeader();
        header.setBackground(new Color(248, 250, 252)); // #f8fafc
        header.setForeground(new Color(52, 58, 64)); // #343a40
        header.setFont(new Font("Arial", Font.PLAIN, 14));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(scrollPane);

        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton backButton = new JButton("Back to Customer Accounts");
        backButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backButton.setBackground(new Color(248, 250, 252)); // #f8fafc
        backButton.setForeground(new Color(52, 58, 64)); // #343a40
        backButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
        backButton.setFocusPainted(false);
        backButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new CustomerAccounts(adminId).setVisible(true));
        });
        actionPanel.add(backButton);
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(230, 247, 255));
        refreshButton.setForeground(new Color(13, 110, 253));
        refreshButton.setBorder(BorderFactory.createLineBorder(new Color(230, 247, 255)));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            loadTransactionsData();
            JOptionPane.showMessageDialog(this, 
                "Transaction data refreshed successfully.",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        actionPanel.add(refreshButton);
        
        JButton exportButton = new JButton("Export Transactions");
        exportButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exportButton.setBackground(new Color(230, 255, 243));
        exportButton.setForeground(new Color(25, 135, 84));
        exportButton.setBorder(BorderFactory.createLineBorder(new Color(230, 255, 243)));
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, 
                "Export functionality coming soon!",
                "Feature Coming Soon", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        actionPanel.add(exportButton);

        contentPanel.add(actionPanel);
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        return mainPanel;
    }
    
    /**
     * Loads transaction data from the database into the table
     */
    private void loadTransactionsData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query to get all transactions for this account
            String query = "SELECT * FROM transactions WHERE account_id = ? ORDER BY transaction_date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, accountId);
            
            ResultSet rs = stmt.executeQuery();
            
            // Date formatter for displaying dates nicely
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            
            while (rs.next()) {
                int transactionId = rs.getInt("transaction_id");
                String transactionType = rs.getString("transaction_type");
                double amount = rs.getDouble("amount");
                
                // Format dates from timestamp
                String transactionDate = "N/A";
                Date dateObj = rs.getTimestamp("transaction_date");
                if (dateObj != null) {
                    transactionDate = dateFormat.format(dateObj);
                }
                
                String description = rs.getString("description");
                String status = rs.getString("status");
                
                // Format approval date
                String approvalDate = "N/A";
                Date approvalObj = rs.getTimestamp("approval_date");
                if (approvalObj != null) {
                    approvalDate = dateFormat.format(approvalObj);
                }
                
                // Add new row
                tableModel.addRow(new Object[]{
                    transactionId,
                    transactionType,
                    amount,
                    transactionDate,
                    description,
                    status,
                    approvalDate
                });
            }
            
            // If no transactions were found, display a message
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"No transactions found for this account", "", "", "", "", "", ""});
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading transaction data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to navigate between screens
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
                SwingUtilities.invokeLater(() -> new ManageTransaction().setVisible(true));
                break;
            case "Reports":
                SwingUtilities.invokeLater(() -> new Report().setVisible(true));
                break;
            default:
                SwingUtilities.invokeLater(() -> new CustomerAccounts(adminId).setVisible(true));
        }
    }
    
    public static void main(String[] args) {
        // For testing only - normally would be called from CustomerAccounts
        SwingUtilities.invokeLater(() -> new CustomerTransactions(10, 1).setVisible(true));
    }
}