package bank.pr;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ApproveTransaction extends JFrame {

    public ApproveTransaction() {
        setTitle("Bank Manager - Approval Queue");
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

        setVisible(true);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, 800));
        sidebarPanel.setBackground(new Color(26, 32, 44)); // #1a202c
        sidebarPanel.setLayout(null);

        // Sidebar title
        JLabel titleLabel = new JLabel("Bank Manager");
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
        JLabel notificationLabel = new JLabel("3 transactions awaiting approval");
        notificationLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        notificationLabel.setForeground(new Color(220, 53, 69)); // #dc3545
        notificationLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notificationLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        contentPanel.add(notificationLabel);

        // Table
        String[] columnNames = {"DATE", "CUSTOMER", "ACCOUNT", "AMOUNT", "TYPE", "ACTIONS"};
        Object[][] data = {
            {"01/03/2025", "Alice Brown", "****7890", "$300.00", "Transfer", "Approve/Reject"},
            {"01/04/2025", "Bob Green", "****1234", "$450.00", "Withdrawal", "Approve/Reject"},
            {"01/05/2025", "Carol White", "****5678", "$1,200.00", "Deposit", "Approve/Reject"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only make Actions column editable
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(50); // Taller rows for additional info
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Custom renderers for different columns
        table.getColumnModel().getColumn(3).setCellRenderer(new AmountRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new TransactionTypeRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new ActionRenderer());
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(200);

        // Style table header
        JTableHeader header = table.getTableHeader();
        header.setBackground(new Color(248, 250, 252)); // #f8fafc
        header.setForeground(new Color(52, 58, 64)); // #343a40
        header.setFont(new Font("Arial", Font.PLAIN, 14));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        // Add additional info to rows
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JPanel panel = new JPanel(new BorderLayout());
                JLabel label = new JLabel(value.toString());
                label.setFont(new Font("Arial", Font.PLAIN, 14));
                label.setForeground(new Color(52, 58, 64)); // #343a40
                panel.add(label, BorderLayout.NORTH);
                
                // Add additional info for each row
                if (column == 1) {
                    String[] additionalInfo = {
                        "To: ****4567 | Memo: \"Rent payment\"",
                        "ATM Withdrawal | Location: Main St Branch",
                        "Check Deposit | Check #1024"
                    };
                    JLabel infoLabel = new JLabel(additionalInfo[row]);
                    infoLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                    infoLabel.setForeground(new Color(108, 117, 125)); // #6c757d
                    panel.add(infoLabel, BorderLayout.SOUTH);
                }
                
                return panel;
            }
        });

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane);

        // Bulk Action Buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 20));
        actionPanel.setBackground(Color.WHITE);
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton approveAllButton = new JButton("Approve All");
        approveAllButton.setFont(new Font("Arial", Font.PLAIN, 14));
        approveAllButton.setBackground(new Color(230, 255, 250)); // #e6fffa
        approveAllButton.setForeground(new Color(13, 110, 253)); // #0d6efd
        approveAllButton.setBorder(BorderFactory.createLineBorder(new Color(230, 255, 250)));
        approveAllButton.setFocusPainted(false);
        approveAllButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "All transactions approved!"));
        actionPanel.add(approveAllButton);

        JButton rejectAllButton = new JButton("Reject All");
        rejectAllButton.setFont(new Font("Arial", Font.PLAIN, 14));
        rejectAllButton.setBackground(new Color(255, 236, 236)); // #ffecec
        rejectAllButton.setForeground(new Color(220, 53, 69)); // #dc3545
        rejectAllButton.setBorder(BorderFactory.createLineBorder(new Color(255, 236, 236)));
        rejectAllButton.setFocusPainted(false);
        rejectAllButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "All transactions rejected!"));
        actionPanel.add(rejectAllButton);

        contentPanel.add(actionPanel);

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
           
        });
        contentPanel.add(dashboardButton);

        mainPanel.add(contentPanel, BorderLayout.NORTH);
        return mainPanel;
    }

    // Custom renderer for Amount column
    private static class AmountRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            
            // Color amounts based on transaction type (simplified logic)
            String type = table.getValueAt(row, 4).toString();
            if (type.equals("Deposit")) {
                label.setForeground(new Color(0, 128, 0)); // Green for deposits
            } else {
                label.setForeground(new Color(220, 53, 69)); // Red for withdrawals/transfers
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

            if (value.equals("Deposit")) {
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

    // Custom renderer for Action column
    private static class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 0));
            panel.setOpaque(true);
            panel.setBackground(table.getBackground());

            JButton approveButton = new JButton("Approve");
            approveButton.setFont(new Font("Arial", Font.PLAIN, 12));
            approveButton.setBackground(new Color(230, 255, 250)); // #e6fffa
            approveButton.setForeground(new Color(13, 110, 253)); // #0d6efd
            approveButton.setBorder(BorderFactory.createLineBorder(new Color(230, 255, 250)));
            approveButton.setFocusPainted(false);
            approveButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(table, "Transaction approved!");
                // Here you would update the table/model to remove the approved row
            });

            JButton rejectButton = new JButton("Reject");
            rejectButton.setFont(new Font("Arial", Font.PLAIN, 12));
            rejectButton.setBackground(new Color(255, 236, 236)); // #ffecec
            rejectButton.setForeground(new Color(220, 53, 69)); // #dc3545
            rejectButton.setBorder(BorderFactory.createLineBorder(new Color(255, 236, 236)));
            rejectButton.setFocusPainted(false);
            rejectButton.addActionListener(e -> {
                JOptionPane.showMessageDialog(table, "Transaction rejected!");
                // Here you would update the table/model to remove the rejected row
            });

            panel.add(approveButton);
            panel.add(rejectButton);
            return panel;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ApproveTransaction();
        });
    }
}