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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import javax.swing.border.EmptyBorder;
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

    // Add a reference to the notification label
    private JLabel notificationLabel;

    public ApproveTransaction() {
        setTitle("KOB Manager - Approval Queue");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Set background color
        getContentPane().setBackground(new Color(245, 247, 251));

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
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 200, 30);
        sidebarPanel.add(titleLabel);

        // Active sidebar button (Approval Queue)
        JButton approvalButton = createSidebarButton("Approval Queue", true);
        approvalButton.setBounds(20, 100, 210, 40);
        sidebarPanel.add(approvalButton);

        // Other sidebar buttons
        String[] menuItems = {"Dashboard", "Customer Accounts", "Transaction Mgmt", "Reports", "Audit Logs"};
        int yPos = 180;
        for (String item : menuItems) {
            JButton menuButton = createSidebarButton(item, false);
            menuButton.setBounds(20, yPos, 210, 30);

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

    private JButton createSidebarButton(String text, boolean active) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        if (active) {
            button.setBackground(new Color(52, 58, 64));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.BOLD, 16));
        } else {
            button.setBackground(new Color(26, 32, 44));
            button.setForeground(Color.WHITE);
            button.setFont(new Font("Arial", Font.PLAIN, 14));

            // Hover effect
            button.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    button.setBackground(new Color(52, 58, 64));
                }

                public void mouseExited(MouseEvent e) {
                    button.setBackground(new Color(26, 32, 44));
                }
            });
        }

        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
        return button;
    }

    private JPanel createApprovalContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(245, 247, 251));
        mainPanel.setLayout(new BorderLayout());

        // Content container with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                BorderFactory.createEmptyBorder(25, 30, 25, 30)
        ));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Approval Queue Header
        JLabel titleLabel = new JLabel("Approve Pending Transactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(new Color(52, 58, 64));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        // Notification Label
        notificationLabel = new JLabel("Transactions awaiting approval");
        notificationLabel.setFont(new Font("Arial", Font.PLAIN, 15));
        notificationLabel.setForeground(new Color(220, 53, 69));
        notificationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 25, 0));
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
        table.setRowHeight(55);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
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
        header.setForeground(new Color(52, 58, 64));
        header.setFont(new Font("Arial", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        header.setReorderingAllowed(false);
        header.setPreferredSize(new Dimension(header.getWidth(), 40));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        contentPanel.add(scrollPane);

        // Bulk Action Buttons
        JPanel actionnPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 25));
        actionnPanel.setBackground(Color.WHITE);
        actionnPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton approveAllButton = createActionButton("Approve All", true);
        approveAllButton.addActionListener(e -> approveAllTransactions());
        actionnPanel.add(approveAllButton);

        JButton rejectAllButton = createActionButton("Reject All", false);
        rejectAllButton.addActionListener(e -> rejectAllTransactions());
        actionnPanel.add(rejectAllButton);

        contentPanel.add(actionnPanel);

        // Dashboard Button
        JButton dashboardButton = new JButton("← Dashboard");
        dashboardButton.setFont(new Font("Arial", Font.PLAIN, 14));
        dashboardButton.setBackground(new Color(248, 250, 252));
        dashboardButton.setForeground(new Color(52, 58, 64));
        dashboardButton.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        dashboardButton.setFocusPainted(false);
        dashboardButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        dashboardButton.setPreferredSize(new Dimension(150, 40));
        dashboardButton.setAlignmentX(Component.LEFT_ALIGNMENT);

        dashboardButton.addActionListener(e -> {
            dispose();
            // Navigate to Dashboard (add implementation)
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

        // Create vertical spacing without using Box class
        JPanel spacerPanel = new JPanel();
        spacerPanel.setPreferredSize(new Dimension(1, 10));
        spacerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 10));
        spacerPanel.setOpaque(false);
        contentPanel.add(spacerPanel);
        contentPanel.add(dashboardButton);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
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

        button.setFont(new Font("Arial", Font.BOLD, 14));
        if (isApprove) {
            button.setForeground(new Color(13, 110, 253));
        } else {
            button.setForeground(new Color(220, 53, 69));
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
            }

            // Update notification label with count
            int pendingCount = tableModel.getRowCount();
            notificationLabel.setText(pendingCount + " transaction" + (pendingCount != 1 ? "s" : "") + " awaiting approval");

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading pending transactions: " + e.getMessage(), "Database Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // Method to extract destination account ID from description
    private int extractDestinationAccountId(String description) {
        // The description format is "Transfer to account ID X"
        Pattern pattern = Pattern.compile("Transfer to account ID (\\d+)");
        Matcher matcher = pattern.matcher(description);
        if (matcher.find()) {
            try {
                return Integer.parseInt(matcher.group(1));
            } catch (NumberFormatException e) {
                System.out.println("Failed to parse account ID: " + matcher.group(1));
                e.printStackTrace();
                return -1;
            }
        }
        System.out.println("No account ID found in description: " + description);
        return -1; // Return -1 if no destination account ID found
    }

    // Approve a specific transaction
    public void approveTransaction(int transactionId) {
        Connection conn = null;
        try {
            conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);

            // First, get the transaction details
            String transactionQuery = "SELECT t.account_id, t.amount, t.transaction_type, t.description, a.balance " +
                    "FROM transactions t JOIN accounts a ON t.account_id = a.account_id " +
                    "WHERE t.transaction_id = ?";
            PreparedStatement transactionStmt = conn.prepareStatement(transactionQuery);
            transactionStmt.setInt(1, transactionId);
            ResultSet rs = transactionStmt.executeQuery();

            if (rs.next()) {
                int accountId = rs.getInt("account_id");
                double amount = rs.getDouble("amount");
                String type = rs.getString("transaction_type");
                String description = rs.getString("description");
                double currentBalance = rs.getDouble("balance");
                double newBalance = currentBalance;

                System.out.println("Approving transaction: " + transactionId);
                System.out.println("Type: " + type);
                System.out.println("Amount: $" + amount);
                System.out.println("Source Account ID: " + accountId);
                System.out.println("Current Source Balance: $" + currentBalance);

                // Start a transaction to ensure all updates happen atomically
                conn.setAutoCommit(false);
                try {
                    // Update balance based on transaction type
                    if (type.equals("DEPOSIT")) {
                        newBalance = currentBalance + amount;
                        System.out.println("Deposit - New Balance: $" + newBalance);
                    } else if (type.equals("WITHDRAW")) {
                        newBalance = currentBalance - amount;
                        System.out.println("Withdraw - New Balance: $" + newBalance);
                    } else if (type.equals("TRANSFER")) {
                        // For transfers, we need to handle both accounts
                        newBalance = currentBalance - amount;
                        System.out.println("Transfer - New Source Balance: $" + newBalance);

                        // Extract destination account ID from description
                        int destAccountId = extractDestinationAccountId(description);
                        System.out.println("Destination Account ID: " + destAccountId);

                        if (destAccountId > 0) {
                            // Get destination account balance
                            String destBalanceQuery = "SELECT balance FROM accounts WHERE account_id = ?";
                            PreparedStatement destBalanceStmt = conn.prepareStatement(destBalanceQuery);
                            destBalanceStmt.setInt(1, destAccountId);
                            ResultSet destBalanceRs = destBalanceStmt.executeQuery();

                            if (destBalanceRs.next()) {
                                double destBalance = destBalanceRs.getDouble("balance");
                                double newDestBalance = destBalance + amount;
                                System.out.println("Current Destination Balance: $" + destBalance);
                                System.out.println("New Destination Balance: $" + newDestBalance);

                                // Update destination account balance
                                String updateDestQuery = "UPDATE accounts SET balance = ? WHERE account_id = ?";
                                PreparedStatement updateDestStmt = conn.prepareStatement(updateDestQuery);
                                updateDestStmt.setDouble(1, newDestBalance);
                                updateDestStmt.setInt(2, destAccountId);
                                int destRowsAffected = updateDestStmt.executeUpdate();
                                System.out.println("Destination update rows affected: " + destRowsAffected);
                            } else {
                                throw new SQLException("Destination account not found: " + destAccountId);
                            }
                        } else {
                            throw new SQLException("Could not determine destination account from description: " + description);
                        }
                    }

                    // Update source account balance
                    String updateBalanceQuery = "UPDATE accounts SET balance = ? WHERE account_id = ?";
                    PreparedStatement updateBalanceStmt = conn.prepareStatement(updateBalanceQuery);
                    updateBalanceStmt.setDouble(1, newBalance);
                    updateBalanceStmt.setInt(2, accountId);
                    int sourceRowsAffected = updateBalanceStmt.executeUpdate();
                    System.out.println("Source update rows affected: " + sourceRowsAffected);

                    // Update transaction status
                    String updateStatusQuery = "UPDATE transactions SET status = 'APPROVED', approval_date = NOW() WHERE transaction_id = ?";
                    PreparedStatement updateStatusStmt = conn.prepareStatement(updateStatusQuery);
                    updateStatusStmt.setInt(1, transactionId);
                    int statusRowsAffected = updateStatusStmt.executeUpdate();
                    System.out.println("Status update rows affected: " + statusRowsAffected);

                    // Commit the transaction
                    conn.commit();
                    System.out.println("Transaction committed successfully");

                    JOptionPane.showMessageDialog(this,
                            "Transaction approved successfully",
                            "Approved",
                            JOptionPane.INFORMATION_MESSAGE);
                } catch (SQLException e) {
                    // If anything goes wrong, roll back
                    System.out.println("ERROR: " + e.getMessage());
                    e.printStackTrace();
                    try {
                        conn.rollback();
                        System.out.println("Transaction rolled back");
                    } catch (SQLException rollbackEx) {
                        System.out.println("Rollback failed: " + rollbackEx.getMessage());
                        rollbackEx.printStackTrace();
                    }
                    JOptionPane.showMessageDialog(this,
                            "Error approving transaction: " + e.getMessage(),
                            "Database Error",
                            JOptionPane.ERROR_MESSAGE);
                } finally {
                    // Restore auto-commit
                    try {
                        conn.setAutoCommit(true);
                    } catch (SQLException autoCommitEx) {
                        System.out.println("Failed to restore auto-commit: " + autoCommitEx.getMessage());
                        autoCommitEx.printStackTrace();
                    }
                }
            }

            loadPendingTransactions(); // Refresh the list

        } catch (SQLException e) {
            System.out.println("Database error: " + e.getMessage());
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
                } catch (SQLException e) {
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
                    label.setForeground(new Color(40, 167, 69)); // Green for deposits
                } else {
                    label.setForeground(new Color(220, 53, 69)); // Red for withdrawals/transfers
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
            label.setFont(new Font("Arial", Font.BOLD, 12));

            if (value.equals("DEPOSIT")) {
                label.setBackground(new Color(230, 255, 250)); // #e6fffa
                label.setForeground(new Color(13, 110, 253)); // #0d6efd
                label.setText("DEPOSIT");
            } else if (value.equals("WITHDRAW")) {
                label.setBackground(new Color(255, 236, 236)); // #ffecec
                label.setForeground(new Color(220, 53, 69)); // #dc3545
                label.setText("WITHDRAW");
            } else if (value.equals("TRANSFER")) {
                label.setBackground(new Color(255, 243, 205)); // Light yellow
                label.setForeground(new Color(255, 153, 0)); // Orange
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
                    g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                    g2.dispose();
                    super.paintComponent(g);
                }
            };
            approveButton.setFont(new Font("Arial", Font.BOLD, 12));
            approveButton.setForeground(new Color(13, 110, 253));
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
            rejectButton.setFont(new Font("Arial", Font.BOLD, 12));
            rejectButton.setForeground(new Color(220, 53, 69));
            rejectButton.setContentAreaFilled(false);
            rejectButton.setBorderPainted(false);
            rejectButton.setFocusPainted(false);
            rejectButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
            rejectButton.setPreferredSize(new Dimension(85, 30));

            // Add action listeners
            approveButton.addActionListener(e -> {
                parent.approveTransaction(currentTransactionId);
                fireEditingStopped();
            });

            rejectButton.addActionListener(e -> {
                parent.rejectTransaction(currentTransactionId);
                fireEditingStopped();
            });

            // Add hover effects
            approveButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    approveButton.setForeground(new Color(13, 110, 253).darker());
                }

                public void mouseExited(MouseEvent e) {
                    approveButton.setForeground(new Color(13, 110, 253));
                }
            });

            rejectButton.addMouseListener(new MouseAdapter() {
                public void mouseEntered(MouseEvent e) {
                    rejectButton.setForeground(new Color(220, 53, 69).darker());
                }

                public void mouseExited(MouseEvent e) {
                    rejectButton.setForeground(new Color(220, 53, 69));
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ApproveTransaction();
        });
    }
}