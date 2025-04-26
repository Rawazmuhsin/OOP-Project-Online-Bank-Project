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
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.table.TableCellRenderer;

public class CustomerAccounts extends JFrame {
    
    private DefaultTableModel tableModel;
    private JTable accountsTable;
    private int adminId = 0;
    private List<Integer> accountIds = new ArrayList<>(); // Store account IDs for removal

    public CustomerAccounts() {
        initialize();
    }
    
    public CustomerAccounts(int adminId) {
        this.adminId = adminId;
        initialize();
    }
    
    private void initialize() {
        setTitle("KOB Manager - Customer Accounts");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createAccountsContentPanel();
        add(new JScrollPane(mainContentPanel), BorderLayout.CENTER);

        setVisible(true);
    }

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

        // Active sidebar button (Customer Accounts)
        JButton accountsButton = new JButton("Customer Accounts");
        accountsButton.setBackground(new Color(52, 58, 64)); // #343a40
        accountsButton.setForeground(Color.WHITE);
        accountsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        accountsButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        accountsButton.setBounds(20, 100, 210, 40);
        accountsButton.setHorizontalAlignment(SwingConstants.LEFT);
        accountsButton.setFocusPainted(false);
        sidebarPanel.add(accountsButton);

        // Other sidebar buttons
        String[] menuItems = {"Dashboard", "Transaction Oversight", "Approval Queue", "Reports", "Audit Logs"};
        int yPos = 180;
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

    private JPanel createAccountsContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 244, 248)); // #f0f4f8
        mainPanel.setLayout(new BorderLayout());

        // Content container with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Customer Accounts Header
        JLabel titleLabel = new JLabel("Customer Accounts");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64)); // #343a40
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        // Filter Section
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        filterPanel.add(filterLabel);

        String[] filterOptions = {"Account Type", "Balance Range", "Status"};
        for (String option : filterOptions) {
            JButton filterButton = new JButton(option);
            filterButton.setFont(new Font("Arial", Font.PLAIN, 14));
            filterButton.setBackground(new Color(248, 250, 252)); // #f8fafc
            filterButton.setForeground(new Color(52, 58, 64)); // #343a40
            filterButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
            filterButton.setFocusPainted(false);
            filterPanel.add(filterButton);
        }
        
        // Add View Customer Transactions button
        JButton viewTransactionsButton = new JButton("View Customer Transactions");
        viewTransactionsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        viewTransactionsButton.setBackground(new Color(230, 247, 255));
        viewTransactionsButton.setForeground(new Color(13, 110, 253));
        viewTransactionsButton.setBorder(BorderFactory.createLineBorder(new Color(230, 247, 255)));
        viewTransactionsButton.setFocusPainted(false);
        viewTransactionsButton.addActionListener(e -> {
            promptForAccountId();
        });
        filterPanel.add(viewTransactionsButton);
        
        contentPanel.add(filterPanel);

        // Create table with data from database
        // Added "VIEW TRANSACTIONS" column
        String[] columnNames = {"CUSTOMER NAME", "ACCOUNT NUMBER", "ACCOUNT TYPE", "BALANCE", "STATUS", "ACTION", "VIEW TRANSACTIONS"};
        
        // Create table model for dynamic updates
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                // Make only the ACTION and VIEW TRANSACTIONS columns editable
                return column == 5 || column == 6;
            }
            
            @Override
            public Class<?> getColumnClass(int columnIndex) {
                // Return JButton class for the ACTION and VIEW TRANSACTIONS columns
                return (columnIndex == 5 || columnIndex == 6) ? JButton.class : Object.class;
            }
        };

        accountsTable = new JTable(tableModel);
        accountsTable.setRowHeight(30);
        accountsTable.setShowGrid(false);
        accountsTable.setIntercellSpacing(new Dimension(0, 0));
        accountsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Set custom renderer for the button columns
        accountsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("Remove", Color.RED));
        accountsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer("Transactions", Color.BLUE));
        
        // Set custom editor for the button columns
        accountsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JButton("Remove"), this, ButtonType.REMOVE));
        accountsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JButton("Transactions"), this, ButtonType.TRANSACTIONS));
        
        // Load data from database
        loadAccountsData();
        
        // Adjust column widths for better appearance
        accountsTable.getColumnModel().getColumn(0).setPreferredWidth(150); // Customer name
        accountsTable.getColumnModel().getColumn(1).setPreferredWidth(120); // Account number
        accountsTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Account type  
        accountsTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Balance
        accountsTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Status
        accountsTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Remove button
        accountsTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Transactions button
        
        // Custom renderer for status column
        accountsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setOpaque(true);

                if (value.equals("Active")) {
                    label.setBackground(new Color(230, 255, 250)); // #e6fffa
                    label.setForeground(new Color(13, 110, 253)); // #0d6efd
                    label.setBorder(BorderFactory.createLineBorder(new Color(230, 255, 250)));
                } else {
                    label.setBackground(new Color(255, 236, 236)); // #ffecec
                    label.setForeground(new Color(220, 53, 69)); // #dc3545
                    label.setBorder(BorderFactory.createLineBorder(new Color(255, 236, 236)));
                }
                return label;
            }
        });
        
        // Custom renderer for balance column to right-align and format currency
        accountsTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private final DecimalFormat formatter = new DecimalFormat("$#,##0.00");
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                
                // Format balance as currency if it's not already formatted
                if (value != null && !value.toString().startsWith("$")) {
                    try {
                        double amount = Double.parseDouble(value.toString());
                        label.setText(formatter.format(amount));
                    } catch (NumberFormatException e) {
                        // Keep original text if parsing fails
                    }
                }
                
                return label;
            }
        });

        // Style table header
        JTableHeader header = accountsTable.getTableHeader();
        header.setBackground(new Color(248, 250, 252)); // #f8fafc
        header.setForeground(new Color(52, 58, 64)); // #343a40
        header.setFont(new Font("Arial", Font.PLAIN, 14));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(accountsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane);

        // Pagination Controls
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 20));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton prevButton = new JButton("← Dashboard");
        prevButton.setFont(new Font("Arial", Font.PLAIN, 14));
        prevButton.setBackground(new Color(248, 250, 252)); // #f8fafc
        prevButton.setForeground(new Color(52, 58, 64)); // #343a40
        prevButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
        prevButton.setFocusPainted(false);
        prevButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
        });
        paginationPanel.add(prevButton);

        for (int i = 1; i <= 5; i++) {
            JButton pageButton = new JButton(String.valueOf(i));
            pageButton.setFont(new Font("Arial", Font.PLAIN, 14));
            if (i == 1) { // First page is active by default
                pageButton.setBackground(new Color(13, 110, 253)); // #0d6efd
                pageButton.setForeground(Color.WHITE);
                pageButton.setBorder(BorderFactory.createLineBorder(new Color(13, 110, 253)));
            } else {
                pageButton.setBackground(new Color(248, 250, 252)); // #f8fafc
                pageButton.setForeground(new Color(52, 58, 64)); // #343a40
                pageButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
            }
            pageButton.setFocusPainted(false);
            paginationPanel.add(pageButton);
        }

        JButton nextButton = new JButton("Next →");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 14));
        nextButton.setBackground(new Color(248, 250, 252)); // #f8fafc
        nextButton.setForeground(new Color(52, 58, 64)); // #343a40
        nextButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
        nextButton.setFocusPainted(false);
        paginationPanel.add(nextButton);
        
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(230, 247, 255));
        refreshButton.setForeground(new Color(13, 110, 253));
        refreshButton.setBorder(BorderFactory.createLineBorder(new Color(230, 247, 255)));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            loadAccountsData();
            JOptionPane.showMessageDialog(this, 
                "Account data refreshed successfully.",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        paginationPanel.add(refreshButton);

        contentPanel.add(paginationPanel);
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        return mainPanel;
    }
    
    /**
     * Loads account data from the database into the table
     */
    private void loadAccountsData() {
        // Clear existing data
        tableModel.setRowCount(0);
        accountIds.clear(); // Clear stored account IDs
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT account_id, username, account_type, balance, created_at, account_number FROM accounts";
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int accountId = rs.getInt("account_id");
                accountIds.add(accountId); // Store account ID for removal
                
                String username = rs.getString("username");
                String accountNumber = rs.getString("account_number");
                if (accountNumber == null || accountNumber.isEmpty()) {
                    accountNumber = "****" + String.valueOf(accountId);
                } else {
                    // Mask account number for privacy - show only last 4 digits
                    accountNumber = "****" + accountNumber.substring(Math.max(0, accountNumber.length() - 4));
                }
                
                String accountType = rs.getString("account_type");
                if (accountType == null || accountType.isEmpty()) {
                    accountType = "Checking"; // Default account type
                }
                
                double balance = rs.getDouble("balance");
                
                // Determine account status - for demo purposes, any account with balance <= 0 is "Inactive"
                String status = balance > 0 ? "Active" : "Inactive";
                
                // Add new row with both Remove and Transactions buttons
                tableModel.addRow(new Object[]{
                    username,
                    accountNumber,
                    accountType,
                    balance,
                    status,
                    "Remove",       // This will be replaced by the button renderer
                    "Transactions"  // This will be replaced by the button renderer
                });
            }
            
            // If no accounts were found, display a message
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"No accounts found", "", "", "", "", "", ""});
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading account data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Remove account from database by index
     * @param rowIndex The row index in the table
     */
    public void removeAccount(int rowIndex) {
        if (rowIndex < 0 || rowIndex >= accountIds.size()) {
            JOptionPane.showMessageDialog(this,
                "Invalid account selection.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        int accountId = accountIds.get(rowIndex);
        String username = (String) tableModel.getValueAt(rowIndex, 0);
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to remove " + username + "'s account?",
            "Confirm Account Removal",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
            
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Delete from database
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM accounts WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, accountId);
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                // Remove from table model
                tableModel.removeRow(rowIndex);
                // Remove from accountIds list
                accountIds.remove(rowIndex);
                
                JOptionPane.showMessageDialog(this,
                    "Account removed successfully.",
                    "Account Removed",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Reload data to ensure consistency
                loadAccountsData();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to remove account. Please try again.",
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
    
    /**
     * Shows a dialog to enter account ID and view transactions
     */
    private void promptForAccountId() {
        String input = JOptionPane.showInputDialog(this, 
            "Enter Account ID to view transactions:",
            "View Customer Transactions", 
            JOptionPane.QUESTION_MESSAGE);
        
        if (input != null && !input.isEmpty()) {
            try {
                int accountId = Integer.parseInt(input);
                viewTransactions(accountId);
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(this,
                    "Please enter a valid account ID (number only).",
                    "Invalid Input",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * View transactions for a specific account ID
     * @param accountId The account ID to view transactions for
     */
    public void viewTransactions(int accountId) {
        // Open a new window to show transactions for this account ID
        dispose(); // Close current window
        SwingUtilities.invokeLater(() -> new CustomerTransactions(accountId, adminId).setVisible(true));
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
            case "Transaction Oversight":
                SwingUtilities.invokeLater(() -> new ManageTransaction().setVisible(true));
                break;
            case "Reports":
                SwingUtilities.invokeLater(() -> new Report().setVisible(true));
                break;
            case "Approval Queue":
                // For now, just show a message and return to dashboard
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Approval Queue screen is under development.", 
                    "Coming Soon", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
                break;
            case "Audit Logs":
                // For now, just show a message and return to dashboard
                javax.swing.JOptionPane.showMessageDialog(null, 
                    "Audit Logs screen is under development.", 
                    "Coming Soon", javax.swing.JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
                break;
            default:
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
        }
    }
    
    // Enum for button types
    enum ButtonType {
        REMOVE,
        TRANSACTIONS
    }
    
    /**
     * Custom renderer for the button columns
     */
    class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer(String text, Color color) {
            setOpaque(true);
            setText(text);
            setBackground(color);
            setForeground(Color.WHITE);
            setFocusPainted(false);
            setBorderPainted(false);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            return this;
        }
    }
    
    /**
     * Custom editor for the button columns to handle click events
     */
    class ButtonEditor extends javax.swing.DefaultCellEditor {
        protected JButton button;
        private String label;
        private boolean isPushed;
        private CustomerAccounts parent;
        private ButtonType buttonType;
        
        public ButtonEditor(JButton button, CustomerAccounts parent, ButtonType buttonType) {
            super(new javax.swing.JCheckBox());
            this.button = button;
            this.parent = parent;
            this.buttonType = buttonType;
            button.setOpaque(true);
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    fireEditingStopped();
                }
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            if (value == null) {
                label = "";
            } else {
                label = value.toString();
            }
            button.setText(label);
            
            // Set different colors based on button type
            if (buttonType == ButtonType.REMOVE) {
                button.setBackground(new Color(220, 53, 69)); // Red for remove
            } else {
                button.setBackground(new Color(13, 110, 253)); // Blue for transactions
            }
            
            button.setForeground(Color.WHITE);
            isPushed = true;
            return button;
        }
        
        @Override
        public Object getCellEditorValue() {
            isPushed = false;
            return label;
        }
        
        @Override
        public boolean stopCellEditing() {
            isPushed = false;
            return super.stopCellEditing();
        }
        
        @Override
        public void fireEditingStopped() {
            super.fireEditingStopped();
            
            int row = accountsTable.getSelectedRow();
            if (row >= 0 && row < accountIds.size()) {
                // Perform action based on button type
                if (buttonType == ButtonType.REMOVE) {
                    parent.removeAccount(row);
                } else if (buttonType == ButtonType.TRANSACTIONS) {
                    int accountId = accountIds.get(row);
                    parent.viewTransactions(accountId);
                }
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomerAccounts();
        });
    }
}