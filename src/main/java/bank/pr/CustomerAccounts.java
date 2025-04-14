package bank.pr;

import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomerAccounts extends JFrame {

    public CustomerAccounts() {
        setTitle("Bank Manager - Customer Accounts");
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
        JLabel titleLabel = new JLabel("Bank Manager");
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
        contentPanel.add(filterPanel);

        // Table - Corrected implementation
        String[] columnNames = {"CUSTOMER NAME", "ACCOUNT NUMBER", "ACCOUNT TYPE", "BALANCE", "STATUS"};
        Object[][] data = {
            {"John Doe", "****1234", "Premium Checking", "$5,000.00", "Active"},
            {"Jane Smith", "****5678", "Savings Plus", "$7,200.00", "Active"},
            {"Robert Johnson", "****9012", "Business Checking", "$12,450.00", "Flagged"}
        };

        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        JTable table = new JTable(model);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Custom renderer for status column - Fixed implementation
        table.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
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

        contentPanel.add(paginationPanel);
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        return mainPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CustomerAccounts();
        });
    }
}