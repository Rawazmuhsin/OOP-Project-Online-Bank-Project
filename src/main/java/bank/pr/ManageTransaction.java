package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

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

public class ManageTransaction extends JFrame {

    public ManageTransaction() {
        setTitle("KOB Manager - Transaction Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createTransactionContentPanel();
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

        // Active sidebar button (Transaction Mgmt)
        JButton transactionButton = new JButton("Transaction Mgmt");
        transactionButton.setBackground(new Color(52, 58, 64)); // #343a40
        transactionButton.setForeground(Color.WHITE);
        transactionButton.setFont(new Font("Arial", Font.PLAIN, 16));
        transactionButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        transactionButton.setBounds(20, 100, 210, 40);
        transactionButton.setHorizontalAlignment(SwingConstants.LEFT);
        transactionButton.setFocusPainted(false);
        sidebarPanel.add(transactionButton);

        // Other sidebar buttons
        String[] menuItems = {"Dashboard", "Customer Accounts", "Approval Queue", "Reports", "Audit Logs"};
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

    private JPanel createTransactionContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 244, 248)); // #f0f4f8
        mainPanel.setLayout(new BorderLayout());

        // Content container with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Transaction Management Header
        JLabel titleLabel = new JLabel("Manage Transactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64)); // #343a40
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        // Filter Section - Row 1
        JPanel filterPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel1.setBackground(Color.WHITE);
        filterPanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel1.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        String[] filterOptions1 = {"Date Range", "Customer Name", "Transaction Type", "Status"};
        for (String option : filterOptions1) {
            JButton filterButton = new JButton(option);
            filterButton.setFont(new Font("Arial", Font.PLAIN, 14));
            filterButton.setBackground(new Color(248, 250, 252)); // #f8fafc
            filterButton.setForeground(new Color(52, 58, 64)); // #343a40
            filterButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
            filterButton.setFocusPainted(false);
            filterPanel1.add(filterButton);
        }

        JButton applyButton = new JButton("Apply Filters");
        applyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        applyButton.setBackground(new Color(13, 110, 253)); // #0d6efd
        applyButton.setForeground(Color.WHITE);
        applyButton.setBorder(BorderFactory.createLineBorder(new Color(13, 110, 253)));
        applyButton.setFocusPainted(false);
        filterPanel1.add(applyButton);

        contentPanel.add(filterPanel1);

        // Filter Section - Row 2
        JPanel filterPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel2.setBackground(Color.WHITE);
        filterPanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] filterOptions2 = {"Amount Range", "Account Number"};
        for (String option : filterOptions2) {
            JButton filterButton = new JButton(option);
            filterButton.setFont(new Font("Arial", Font.PLAIN, 14));
            filterButton.setBackground(new Color(248, 250, 252)); // #f8fafc
            filterButton.setForeground(new Color(52, 58, 64)); // #343a40
            filterButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
            filterButton.setFocusPainted(false);
            filterPanel2.add(filterButton);
        }

        contentPanel.add(filterPanel2);

        // Table
        String[] columnNames = {"DATE", "CUSTOMER", "TYPE", "ACCOUNT", "AMOUNT", "STATUS", "ACTIONS"};
        Object[][] data = {
            {"01/01/2025", "John Doe", "Deposit", "****1234", "+$1,000.00", "Completed", "Details"},
            {"01/02/2025", "Jane Smith", "Withdrawal", "****5678", "-$500.00", "Completed", "Details"},
            {"01/03/2025", "Robert Johnson", "Transfer", "****9012", "-$1,200.00", "Pending", "Review"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only make Actions column editable
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Custom renderers for different columns
        table.getColumnModel().getColumn(2).setCellRenderer(new TransactionTypeRenderer());
        table.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());
        table.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        table.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(100);
        table.getColumnModel().getColumn(1).setPreferredWidth(150);
        table.getColumnModel().getColumn(2).setPreferredWidth(100);
        table.getColumnModel().getColumn(3).setPreferredWidth(100);
        table.getColumnModel().getColumn(4).setPreferredWidth(100);
        table.getColumnModel().getColumn(5).setPreferredWidth(100);
        table.getColumnModel().getColumn(6).setPreferredWidth(100);

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
     
        });
        paginationPanel.add(prevButton);

        for (int i = 1; i <= 5; i++) {
            JButton pageButton = new JButton(String.valueOf(i));
            pageButton.setFont(new Font("Arial", Font.PLAIN, 14));
            if (i == 2) {
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

        JButton exportButton = new JButton("Export CSV");
        exportButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exportButton.setBackground(new Color(248, 250, 252)); // #f8fafc
        exportButton.setForeground(new Color(52, 58, 64)); // #343a40
        exportButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Exporting to CSV..."));
        paginationPanel.add(exportButton);

        contentPanel.add(paginationPanel);
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        return mainPanel;
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

    // Custom renderer for Amount column
    private static class AmountRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            
            if (value.toString().startsWith("+")) {
                label.setForeground(new Color(0, 128, 0)); // Green for positive
            } else {
                label.setForeground(new Color(220, 53, 69)); // Red for negative
            }
            return label;
        }
    }

    // Custom renderer for Status column
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            if (value.equals("Completed")) {
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
            JButton button = new JButton(value.toString());
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setBackground(new Color(230, 255, 250)); // #e6fffa
            button.setForeground(new Color(13, 110, 253)); // #0d6efd
            button.setBorder(BorderFactory.createLineBorder(new Color(230, 255, 250)));
            button.setFocusPainted(false);
            return button;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ManageTransaction();
        });
    }
}