package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.EventObject;
import java.util.Vector;

import javax.swing.AbstractCellEditor;
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
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;

public class ApproveTransaction extends JFrame {
    private DefaultTableModel tableModel;
    private JTable table;
    private final String DB_URL = "jdbc:mysql://localhost:3306/user_management";
    private final String DB_USER = "root";
    private final String DB_PASSWORD = "Makwan2004111";

    
    public ApproveTransaction() {
        setTitle("KOB Manager - Approval Queue");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel
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

        // Active sidebar button (Approval Queue)
        JButton approvalButton = new JButton("Approval Queue");
        approvalButton.setBackground(new Color(52, 58, 64)); // #343a40
        approvalButton.setForeground(Color.WHITE);
        approvalButton.setFont(new Font("Arial", Font.PLAIN, 16));
        approvalButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        approvalButton.setBounds(20, 100, 210, 40);
        approvalButton.setHorizontalAlignment(SwingConstants.LEFT);
        approvalButton.setFocusPainted(false);
        sidebarPanel.add(approvalButton);

        // Other sidebar buttons
        String[] menuItems = {"Dashboard", "Customer Accounts", "Transaction Mgmt", "Reports", "Audit Logs"};
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
            
            // Add action listener for the Transaction Mgmt button
            if (item.equals("Transaction Mgmt")) {
                menuButton.addActionListener(e -> {
                    SwingUtilities.invokeLater(() -> {
                        new ManageTransaction();
                        dispose();
                    });
                });
            }
            
            sidebarPanel.add(menuButton);
            yPos += 40;
        }

        return sidebarPanel;
    }

    private JPanel createApprovalContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 244, 248)); // #f0f4f8
        mainPanel.setLayout(new BorderLayout());

        // Content container with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Approval Queue Header
        JLabel titleLabel = new JLabel("Approve Pending Transactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64)); // #343a40
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        // Notification
        JLabel notificationLabel = new JLabel("Transactions awaiting approval");
        notificationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        notificationLabel.setForeground(new Color(220, 53, 69)); // #dc3545
        notificationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        contentPanel.add(notificationLabel);

        // Table
        String[] columnNames = {"ID", "DATE", "CUSTOMER", "ACCOUNT", "AMOUNT", "TYPE", "ACTIONS"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only make Actions column editable
            }
        };

        table = new JTable(tableModel);
        table.setRowHeight(50); // Taller rows for additional info
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Custom renderers for different columns
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
        table.getColumnModel().getColumn(1).setPreferredWidth(100);
        table.getColumnModel().getColumn(2).setPreferredWidth(150);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(200);

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 250, 252)); // #f8fafc
        header.setForeground(new Color(52, 58, 64)); // #343a40
        header.setFont(new Font("Arial", Font.PLAIN, 14));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane);

        // Bulk Action Buttons
        JPanel actionnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        actionnPanel.setBackground(Color.WHITE);
        actionnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton approveAllButton = new JButton("Approve All");
        approveAllButton.setFont(new Font("Arial", Font.PLAIN, 14));
        approveAllButton.setBackground(new Color(230, 255, 250)); // #e6fffa
        approveAllButton.setForeground(new Color(13, 110, 253)); // #0d6efd
        approveAllButton.setBorder(BorderFactory.createLineBorder(new Color(230, 255, 250)));
        approveAllButton.setFocusPainted(false);
        approveAllButton.addActionListener(e -> approveAllTransactions());
        actionnPanel.add(approveAllButton);

        JButton rejectAllButton = new JButton("Reject All");
        rejectAllButton.setFont(new Font("Arial", Font.PLAIN, 14));
        rejectAllButton.setBackground(new Color(255, 236, 236)); // #ffecec
        rejectAllButton.setForeground(new Color(220, 53, 69)); // #dc3545
        rejectAllButton.setBorder(BorderFactory.createLineBorder(new Color(255, 236, 236)));
        rejectAllButton.setFocusPainted(false);
        rejectAllButton.addActionListener(e -> rejectAllTransactions());
        actionnPanel.add(rejectAllButton);

        contentPanel.add(actionnPanel);

        // Dashboard Button
        JButton dashboardButton = new JButton("← Dashboard");
        dashboardButton.setFont(new Font("Arial", Font.PLAIN, 14));
        dashboardButton.setBackground(new Color(248, 250, 252)); // #f8fafc
        dashboardButton.setForeground(new Color(52, 58, 64)); // #343a40
        dashboardButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
        dashboardButton.setFocusPainted(false);
        dashboardButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        dashboardButton.addActionListener(e -> {
            dispose();
            // Navigate to Dashboard (add implementation)
        });
        contentPanel.add(dashboardButton);

        mainPanel.add(contentPanel, BorderLayout.NORTH);
        return mainPanel;
    }

    // Load pending transactions from database
    private void loadPendingTransactions() {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String query = "SELECT t.transaction_id, t.transaction_date, u.username, " +
                          "a.account_number, t.amount, t.transaction_type, t.description " +
                          "FROM transactions t " +
                          "JOIN users u ON t.user_id = u.user_id " +
                          "JOIN accounts a ON t.account_id = a.account_id " +
                          "WHERE t.status = 'PENDING' " +
                          "ORDER BY t.transaction_date DESC";
            
            PreparedStatement pstmt = conn.prepareStatement(query);
            ResultSet rs = pstmt.executeQuery();
            
            // Clear existing data
            tableModel.setRowCount(0);
            
            // Add new data
            while (rs.next()) {
                Vector<Object> row = new Vector<>();
                row.add(rs.getInt("transaction_id"));
                row.add(rs.getDate("transaction_date"));
                row.add(rs.getString("username"));
                
                // Mask account number for security
                String accountNumber = rs.getString("account_number");
                String maskedAccount = "****" + accountNumber.substring(Math.max(0, accountNumber.length() - 4));
                row.add(maskedAccount);
                
                row.add(rs.getDouble("amount"));
                row.add(rs.getString("transaction_type"));
                row.add("Pending"); // This will be replaced by the ActionRenderer
                
                tableModel.addRow(row);
            }
            
            // Update notification label with count
            int pendingCount = tableModel.getRowCount();
            JLabel notificationLabel = (JLabel) ((JPanel) ((JScrollPane) getContentPane().getComponent(1)).getViewport().getView()).getComponent(1);
            notificationLabel.setText(pendingCount + " transactions awaiting approval");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading pending transactions: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Approve a specific transaction
    public void approveTransaction(int transactionId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            // First, update the transaction status
            String updateQuery = "UPDATE transactions SET status = 'APPROVED', approval_date = NOW() WHERE transaction_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, transactionId);
            updateStmt.executeUpdate();
            
            // Then, update the account balance based on the transaction type
            String accountQuery = "SELECT t.account_id, t.amount, t.transaction_type, a.balance " +
                                 "FROM transactions t JOIN accounts a ON t.account_id = a.account_id " +
                                 "WHERE t.transaction_id = ?";
            PreparedStatement accountStmt = conn.prepareStatement(accountQuery);
            accountStmt.setInt(1, transactionId);
            ResultSet rs = accountStmt.executeQuery();
            
            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                double amount = rs.getDouble("amount");
                String type = rs.getString("transaction_type");
                double currentBalance = rs.getDouble("balance");
                double newBalance = currentBalance;
                
                // Update balance based on transaction type
                if (type.equals("DEPOSIT")) {
                    newBalance = currentBalance + amount;
                } else if (type.equals("WITHDRAW")) {
                    newBalance = currentBalance - amount;
                } else if (type.equals("TRANSFER")) {
                    // For transfers, we need to handle both accounts
                    newBalance = currentBalance - amount;
                    
                    // Get the destination account and update its balance
                    String transferQuery = "SELECT destination_account_id FROM transfers WHERE transaction_id = ?";
                    PreparedStatement transferStmt = conn.prepareStatement(transferQuery);
                    transferStmt.setInt(1, transactionId);
                    ResultSet transferRs = transferStmt.executeQuery();
                    
                    if (transferRs.next()) {
                        int destAccountId = transferRs.getInt("destination_account_id");
                        
                        // Get destination account balance
                        String destBalanceQuery = "SELECT balance FROM accounts WHERE account_id = ?";
                        PreparedStatement destBalanceStmt = conn.prepareStatement(destBalanceQuery);
                        destBalanceStmt.setInt(1, destAccountId);
                        ResultSet destBalanceRs = destBalanceStmt.executeQuery();
                        
                        if (destBalanceRs.next()) {
                            double destBalance = destBalanceRs.getDouble("balance");
                            double newDestBalance = destBalance + amount;
                            
                            // Update destination account balance
                            String updateDestQuery = "UPDATE accounts SET balance = ? WHERE account_id = ?";
                            PreparedStatement updateDestStmt = conn.prepareStatement(updateDestQuery);
                            updateDestStmt.setDouble(1, newDestBalance);
                            updateDestStmt.setInt(2, destAccountId);
                            updateDestStmt.executeUpdate();
                        }
                    }
                }
                
                // Update source account balance
                String updateBalanceQuery = "UPDATE accounts SET balance = ? WHERE account_id = ?";
                PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
                updateBalanceStmt.setDouble(1, newBalance);
                updateBalanceStmt.setInt(2, accountId);
                updateBalanceStmt.executeUpdate();
            }
            
            JOptionPane.showMessageDialog(this, "Transaction approved successfully", "Approved", JOptionPane.INFORMATION_MESSAGE);
            loadPendingTransactions(); // Refresh the list
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error approving transaction: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Reject a specific transaction
    public void rejectTransaction(int transactionId) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String updateQuery = "UPDATE transactions SET status = 'REJECTED', approval_date = NOW() WHERE transaction_id = ?";
            PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
            updateStmt.setInt(1, transactionId);
            updateStmt.executeUpdate();
            
            JOptionPane.showMessageDialog(this, "Transaction rejected", "Rejected", JOptionPane.INFORMATION_MESSAGE);
            loadPendingTransactions(); // Refresh the list
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error rejecting transaction: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    // Approve all pending transactions
    private void approveAllTransactions() {
        int response = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to approve all pending transactions?", 
                "Confirm Approval", 
                JOptionPane.YES_NO_OPTION);
        
        if (response == JOptionPane.YES_OPTION) {
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                int transactionId = (int) tableModel.getValueAt(i, 0);
                approveTransaction(transactionId);
            }
        }
    }
    
    // Reject all pending transactions
    private void rejectAllTransactions() {
        int response = JOptionPane.showConfirmDialog(this, 
                "Are you sure you want to reject all pending transactions?", 
                "Confirm Rejection", 
                JOptionPane.YES_NO_OPTION);
        
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
            
            // Format amount with dollar sign and two decimal places
            if (value instanceof Double) {
                double amount = (Double) value;
                String formattedAmount = String.format("$%.2f", amount);
                label.setText(formattedAmount);
                
                // Color amounts based on transaction type
                String type = table.getValueAt(row, 5).toString();
                if (type.equals("DEPOSIT")) {
                    label.setForeground(new Color(0, 128, 0)); // Green for deposits
                } else {
                    label.setForeground(new Color(220, 53, 69)); // Red for withdrawals/transfers
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

            if (value.equals("DEPOSIT")) {
                label.setBackground(new Color(230, 255, 250)); // #e6fffa
                label.setForeground(new Color(13, 110, 253)); // #0d6efd
            } else {
                label.setBackground(new Color(255, 236, 236)); // #ffecec
                label.setForeground(new Color(220, 53, 69)); // #dc3545
            }
            label.setBorder(BorderFactory.createLineBorder(label.getBackground()));
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
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);
            
            approveButton = new JButton("Approve");
            approveButton.setFont(new Font("Arial", Font.PLAIN, 12));
            approveButton.setBackground(new Color(230, 255, 250)); // #e6fffa
            approveButton.setForeground(new Color(13, 110, 253)); // #0d6efd
            approveButton.setBorder(BorderFactory.createLineBorder(new Color(230, 255, 250)));
            approveButton.setFocusPainted(false);
            
            rejectButton = new JButton("Reject");
            rejectButton.setFont(new Font("Arial", Font.PLAIN, 12));
            rejectButton.setBackground(new Color(255, 236, 236)); // #ffecec
            rejectButton.setForeground(new Color(220, 53, 69)); // #dc3545
            rejectButton.setBorder(BorderFactory.createLineBorder(new Color(255, 236, 236)));
            rejectButton.setFocusPainted(false);
            
            // Add action listeners
            approveButton.addActionListener(e -> {
                parent.approveTransaction(currentTransactionId);
                fireEditingStopped();
            });
            
            rejectButton.addActionListener(e -> {
                parent.rejectTransaction(currentTransactionId);
                fireEditingStopped();
            });
            
            panel.add(approveButton);
            panel.add(rejectButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            panel.setBackground(table.getBackground());
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ApproveTransaction();
        });
    }
}