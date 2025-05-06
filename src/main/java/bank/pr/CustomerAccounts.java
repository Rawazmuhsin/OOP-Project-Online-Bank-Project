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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

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
import javax.swing.table.TableCellRenderer;

public class CustomerAccounts extends JFrame {
    
    // Colors matching the ManagerDashboard
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 220, 120);
    
    private DefaultTableModel tableModel;
    private JTable accountsTable;
    private int adminId = 0;
    private List<Integer> accountIds = new ArrayList<>(); // Store account IDs for removal
    private String adminName = "Administrator"; // Default name

    public CustomerAccounts() {
        initialize();
    }
    
    public CustomerAccounts(int adminId) {
        this.adminId = adminId;
        loadAdminInfo(); // Load admin information
        initialize();
    }
    
    /**
     * Loads admin information from the database
     */
    private void loadAdminInfo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query;
            
            if (adminId > 0) {
                // If admin ID is provided, get specific admin info
                query = "SELECT first_name, last_name FROM admin WHERE admin_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, adminId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    adminName = rs.getString("first_name") + " " + rs.getString("last_name");
                }
            } else {
                // If no specific admin ID, get the first admin in the database (fallback)
                query = "SELECT first_name, last_name FROM admin LIMIT 1";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    adminName = rs.getString("first_name") + " " + rs.getString("last_name");
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading admin information: " + e.getMessage());
        }
    }
    
    private void initialize() {
        setTitle("KOB Manager - Customer Accounts");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create sidebar panel with gradient and rounded components
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel with shadowed cards
        JPanel mainContentPanel = createAccountsContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Try to set icon
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

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
        
        // Logo
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
        addMenuItem(menuPanel, "Customer Accounts", true); // Active item
        addMenuItem(menuPanel, "Transaction Oversight", false);
        addMenuItem(menuPanel, "Approval Queue", false);
        addMenuItem(menuPanel, "Reports", false);
        addMenuItem(menuPanel, "Audit Logs", false);
        
        sidebar.add(menuPanel);
        sidebar.add(Box.createVerticalGlue());
        
        // Add admin profile at bottom of sidebar
        RoundedPanel profilePanel = new RoundedPanel(15, new Color(40, 50, 90));
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
                    CustomerAccounts.this,
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

    private JPanel createAccountsContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        
        JLabel headerLabel = new JLabel("Customer Accounts");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerLabel.setForeground(TEXT_COLOR);
        
        // Subheader
        JLabel subHeaderLabel = new JLabel("Manage and monitor all customer accounts");
        subHeaderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subHeaderLabel.setForeground(LIGHT_TEXT_COLOR);
        
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(subHeaderLabel, BorderLayout.SOUTH);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        
        // Transactions button
        JButton viewTransactionsButton = new JButton("View Transactions");
        viewTransactionsButton.setBackground(SECONDARY_COLOR);
        viewTransactionsButton.setForeground(Color.WHITE);
        viewTransactionsButton.setFocusPainted(false);
        viewTransactionsButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        viewTransactionsButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        viewTransactionsButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        viewTransactionsButton.setOpaque(true);
        viewTransactionsButton.setBorderPainted(false);
        
        // Add hover effects
        viewTransactionsButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                viewTransactionsButton.setBackground(new Color(25, 118, 210));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                viewTransactionsButton.setBackground(SECONDARY_COLOR);
            }
        });
        
        viewTransactionsButton.addActionListener(e -> {
            promptForAccountId();
        });
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setBackground(new Color(76, 175, 80));
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
                refreshButton.setBackground(new Color(56, 142, 60));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                refreshButton.setBackground(new Color(76, 175, 80));
            }
        });
        
        refreshButton.addActionListener(e -> {
            loadAccountsData();
            JOptionPane.showMessageDialog(this, 
                "Account data refreshed successfully.",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Add buttons to action panel
        actionPanel.add(Box.createHorizontalGlue());
        actionPanel.add(viewTransactionsButton);
        actionPanel.add(Box.createHorizontalStrut(15));
        actionPanel.add(refreshButton);
        
        // Add header and action panels to top panel
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        // Filter panel
        RoundedPanel filterPanel = new RoundedPanel(15, Color.WHITE);
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.X_AXIS));
        filterPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        filterPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel.setMaximumSize(new Dimension(1000, 60));
        
        JLabel filterLabel = new JLabel("Filter by:");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        filterLabel.setForeground(TEXT_COLOR);
        
        // Filter buttons
        String[] filterOptions = {"Account Type", "Balance Range", "Status"};
        JPanel filterButtonsPanel = new JPanel();
        filterButtonsPanel.setOpaque(false);
        filterButtonsPanel.setLayout(new BoxLayout(filterButtonsPanel, BoxLayout.X_AXIS));
        
        for (String option : filterOptions) {
            JButton filterButton = new JButton(option);
            filterButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
            filterButton.setBackground(new Color(248, 250, 252));
            filterButton.setForeground(new Color(52, 58, 64));
            filterButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239)));
            filterButton.setFocusPainted(false);
            
            // Add hover effects
            filterButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    filterButton.setBackground(new Color(240, 242, 245));
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    filterButton.setBackground(new Color(248, 250, 252));
                }
            });
            
            filterButtonsPanel.add(filterButton);
            filterButtonsPanel.add(Box.createHorizontalStrut(10));
        }
        
        // Search field placeholder
        JButton searchButton = new JButton("Search Accounts");
        searchButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        searchButton.setBackground(BACKGROUND_COLOR);
        searchButton.setForeground(TEXT_COLOR);
        searchButton.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        searchButton.setFocusPainted(false);
        
        filterPanel.add(filterLabel);
        filterPanel.add(Box.createHorizontalStrut(15));
        filterPanel.add(filterButtonsPanel);
        filterPanel.add(Box.createHorizontalGlue());
        filterPanel.add(searchButton);
        
        // Table Panel
        RoundedPanel tablePanel = new RoundedPanel(15, Color.WHITE);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create table with data from database
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
        accountsTable.setRowHeight(40);
        accountsTable.setShowGrid(false);
        accountsTable.setIntercellSpacing(new Dimension(0, 0));
        accountsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        accountsTable.setBackground(Color.WHITE);
        accountsTable.setSelectionBackground(new Color(232, 240, 254));
        
        // Set custom renderer for the button columns
        accountsTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer("Remove", new Color(220, 53, 69)));
        accountsTable.getColumnModel().getColumn(6).setCellRenderer(new ButtonRenderer("View", SECONDARY_COLOR));
        
        // Set custom editor for the button columns
        accountsTable.getColumnModel().getColumn(5).setCellEditor(new ButtonEditor(new JButton("Remove"), this, ButtonType.REMOVE));
        accountsTable.getColumnModel().getColumn(6).setCellEditor(new ButtonEditor(new JButton("View"), this, ButtonType.TRANSACTIONS));
        
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
        
        // Custom renderer for status column with rounded corners
        accountsTable.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Create a custom panel with rounded corners
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                
                JLabel label = new JLabel(value.toString());
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.PLAIN, 14));
                
                if (value.equals("Active")) {
                    panel.setBackground(new Color(230, 255, 250));
                    label.setForeground(new Color(13, 110, 253));
                } else {
                    panel.setBackground(new Color(255, 236, 236));
                    label.setForeground(new Color(220, 53, 69));
                }
                
                panel.add(label, BorderLayout.CENTER);
                
                // Handle selection
                if (isSelected) {
                    panel.setBorder(BorderFactory.createLineBorder(table.getSelectionBackground(), 2));
                } else {
                    panel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
                }
                
                return panel;
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
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(accountsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Pagination panel
        RoundedPanel paginationPanel = new RoundedPanel(15, Color.WHITE);
        paginationPanel.setLayout(new BoxLayout(paginationPanel, BoxLayout.X_AXIS));
        paginationPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        
        JButton backToDashboardButton = new JButton("← Back to Dashboard");
        backToDashboardButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        backToDashboardButton.setBackground(new Color(248, 250, 252));
        backToDashboardButton.setForeground(TEXT_COLOR);
        backToDashboardButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239)));
        backToDashboardButton.setFocusPainted(false);
        backToDashboardButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
        });
        
        // Pagination buttons
        JPanel pageButtonsPanel = new JPanel();
        pageButtonsPanel.setOpaque(false);
        pageButtonsPanel.setLayout(new BoxLayout(pageButtonsPanel, BoxLayout.X_AXIS));
        
        for (int i = 1; i <= 5; i++) {
            JButton pageButton = new JButton(String.valueOf(i));
            pageButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
            if (i == 1) { // First page is active by default
                pageButton.setBackground(SECONDARY_COLOR);
                pageButton.setForeground(Color.WHITE);
                pageButton.setBorder(BorderFactory.createLineBorder(SECONDARY_COLOR));
            } else {
                pageButton.setBackground(new Color(248, 250, 252));
                pageButton.setForeground(TEXT_COLOR);
                pageButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239)));
            }
            pageButton.setFocusPainted(false);
            
            // Add hover effects
            final int pageNum = i;
            pageButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (pageNum != 1) { // Don't change active button
                        pageButton.setBackground(new Color(240, 242, 245));
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (pageNum != 1) { // Don't change active button
                        pageButton.setBackground(new Color(248, 250, 252));
                    }
                }
            });
            
            pageButtonsPanel.add(pageButton);
            pageButtonsPanel.add(Box.createHorizontalStrut(5));
        }
        
        JButton nextButton = new JButton("Next →");
        nextButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nextButton.setBackground(new Color(248, 250, 252));
        nextButton.setForeground(TEXT_COLOR);
        nextButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239)));
        nextButton.setFocusPainted(false);
        
        // Add components to pagination panel
        paginationPanel.add(backToDashboardButton);
        paginationPanel.add(Box.createHorizontalGlue());
        paginationPanel.add(pageButtonsPanel);
        paginationPanel.add(Box.createHorizontalStrut(10));
        paginationPanel.add(nextButton);
        
        // Create content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Add filter panel
        JPanel filterContainerPanel = new JPanel();
        filterContainerPanel.setOpaque(false);
        filterContainerPanel.setLayout(new BorderLayout());
        filterContainerPanel.add(filterPanel, BorderLayout.NORTH);
        
        contentPanel.add(filterContainerPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(tablePanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(paginationPanel);
        
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
        
        // Create a custom confirmation dialog
        JPanel confirmPanel = new JPanel();
        confirmPanel.setLayout(new BoxLayout(confirmPanel, BoxLayout.Y_AXIS));
        
        JLabel iconLabel = new JLabel();
        try {
            ImageIcon warningIcon = new ImageIcon("icons/warning.png");
            iconLabel.setIcon(warningIcon);
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        } catch (Exception e) {
            // If icon not found, use text instead
            iconLabel.setText("⚠️");
            iconLabel.setFont(new Font("SansSerif", Font.BOLD, 48));
            iconLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        }
        
        JLabel titleLabel = new JLabel("Confirm Account Removal");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel messageLabel = new JLabel("Are you sure you want to remove " + username + "'s account?");
        messageLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        messageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel warningLabel = new JLabel("This action cannot be undone.");
        warningLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        warningLabel.setForeground(Color.RED);
        warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        confirmPanel.add(Box.createVerticalStrut(10));
        confirmPanel.add(iconLabel);
        confirmPanel.add(Box.createVerticalStrut(10));
        confirmPanel.add(titleLabel);
        confirmPanel.add(Box.createVerticalStrut(10));
        confirmPanel.add(messageLabel);
        confirmPanel.add(Box.createVerticalStrut(5));
        confirmPanel.add(warningLabel);
        confirmPanel.add(Box.createVerticalStrut(10));
        
        // Confirm deletion
        int confirm = JOptionPane.showConfirmDialog(this,
            confirmPanel,
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
                
                // Show success message with animation
                showSuccessMessage("Account removed successfully");
                
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
     * Shows a stylized success message
     */
    private void showSuccessMessage(String message) {
        // Create a temporary success message panel
        JPanel messagePanel = new RoundedPanel(15, new Color(76, 175, 80));
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
        
        JLabel checkIcon = new JLabel("✓");
        checkIcon.setFont(new Font("SansSerif", Font.BOLD, 24));
        checkIcon.setForeground(Color.WHITE);
        
        JLabel messageLabel = new JLabel(" " + message);
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        messageLabel.setForeground(Color.WHITE);
        
        messagePanel.add(checkIcon);
        messagePanel.add(messageLabel);
        
        // Add to layered pane to overlay on top of content
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            // Use integer values directly (300 is equivalent to POPUP_LAYER in JLayeredPane)
            frame.getLayeredPane().add(messagePanel, 300); // 300 is the standard value for popup layer
            
            // Calculate position
            int x = (frame.getWidth() - messagePanel.getPreferredSize().width) / 2;
            int y = 100;
            messagePanel.setBounds(x, y, messagePanel.getPreferredSize().width, messagePanel.getPreferredSize().height);
            
            // Set up timer to remove the message
            javax.swing.Timer timer = new javax.swing.Timer(3000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.getLayeredPane().remove(messagePanel);
                    frame.repaint();
                }
            });
            timer.setRepeats(false);
            timer.start();
        } else {
            // Fallback to JOptionPane if layered pane not available
            JOptionPane.showMessageDialog(this,
                message,
                "Success",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * Shows a dialog to enter account ID and view transactions
     */
    private void promptForAccountId() {
        // Create a stylish dialog for account ID input
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));
        
        JLabel titleLabel = new JLabel("Enter Account ID");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel instructionLabel = new JLabel("Please enter an account ID to view transactions:");
        instructionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        instructionLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Create a list of available account IDs for selection
        JPanel accountListPanel = new JPanel();
        accountListPanel.setLayout(new GridLayout(0, 1, 5, 5));
        accountListPanel.setBorder(BorderFactory.createTitledBorder("Available Accounts"));
        
        javax.swing.ButtonGroup group = new javax.swing.ButtonGroup();
        boolean hasAccounts = false;
        
        for (int i = 0; i < accountIds.size(); i++) {
            hasAccounts = true;
            final int accountId = accountIds.get(i);
            final String username = (String) tableModel.getValueAt(i, 0);
            final String accountNumber = (String) tableModel.getValueAt(i, 1);
            
            javax.swing.JRadioButton accountButton = new javax.swing.JRadioButton(
                username + " - Account " + accountNumber + " (ID: " + accountId + ")");
            accountButton.setActionCommand(String.valueOf(accountId));
            
            if (i == 0) {
                accountButton.setSelected(true);
            }
            
            group.add(accountButton);
            accountListPanel.add(accountButton);
        }
        
        if (!hasAccounts) {
            JLabel noAccountsLabel = new JLabel("No accounts available");
            noAccountsLabel.setForeground(Color.RED);
            accountListPanel.add(noAccountsLabel);
        }
        
        inputPanel.add(titleLabel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(instructionLabel);
        inputPanel.add(Box.createVerticalStrut(10));
        inputPanel.add(accountListPanel);
        
        if (hasAccounts) {
            int result = JOptionPane.showConfirmDialog(this, 
                inputPanel,
                "View Customer Transactions", 
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE);
            
            if (result == JOptionPane.OK_OPTION) {
                String selectedAccountId = group.getSelection().getActionCommand();
                try {
                    int accountId = Integer.parseInt(selectedAccountId);
                    viewTransactions(accountId);
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this,
                        "Please select a valid account.",
                        "Invalid Selection",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this,
                inputPanel,
                "View Customer Transactions",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    /**
     * View transactions for a specific account ID
     * @param accountId The account ID to view transactions for
     */
    public void viewTransactions(int accountId) {
        // Show loading indicator
        JPanel loadingPanel = new RoundedPanel(15, new Color(0, 0, 0, 150));
        loadingPanel.setLayout(new BoxLayout(loadingPanel, BoxLayout.Y_AXIS));
        
        JLabel loadingLabel = new JLabel("Loading Transactions...");
        loadingLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        loadingLabel.setForeground(Color.WHITE);
        loadingLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel waitLabel = new JLabel("Please wait");
        waitLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        waitLabel.setForeground(Color.WHITE);
        waitLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        loadingPanel.add(Box.createVerticalGlue());
        loadingPanel.add(loadingLabel);
        loadingPanel.add(Box.createVerticalStrut(10));
        loadingPanel.add(waitLabel);
        loadingPanel.add(Box.createVerticalGlue());
        
        // Add loading panel to layered pane
        JFrame frame = (JFrame) SwingUtilities.getWindowAncestor(this);
        if (frame != null) {
            // Use integer values directly (200 is equivalent to MODAL_LAYER in JLayeredPane)
            frame.getLayeredPane().add(loadingPanel, 200); // 200 is the standard value for modal layer
            
            // Calculate position and size
            int width = 300;
            int height = 150;
            int x = (frame.getWidth() - width) / 2;
            int y = (frame.getHeight() - height) / 2;
            loadingPanel.setBounds(x, y, width, height);
            frame.repaint();
        }
        
        // Use a separate thread to navigate to avoid UI freeze
        new Thread(() -> {
            try {
                // Simulate loading
                Thread.sleep(500);
                
                // Remove loading panel and navigate
                SwingUtilities.invokeLater(() -> {
                    if (frame != null) {
                        frame.getLayeredPane().remove(loadingPanel);
                        frame.repaint();
                    }
                    dispose(); // Close current window
                    new CustomerTransactions(accountId, adminId).setVisible(true);
                });
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
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
                SwingUtilities.invokeLater(() -> new ApproveTransaction(adminId).setVisible(true));
                break;
            case "Audit Logs":
                // For now, just show a message and return to dashboard
                JOptionPane.showMessageDialog(null, 
                    "Audit Logs screen is under development.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
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
            setFont(new Font("SansSerif", Font.BOLD, 14));
            setCursor(new Cursor(Cursor.HAND_CURSOR));
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
                button.setForeground(Color.WHITE);
            } else {
                button.setBackground(SECONDARY_COLOR); // Blue for transactions
                button.setForeground(Color.WHITE);
            }
            
            button.setFont(new Font("SansSerif", Font.BOLD, 14));
            button.setBorderPainted(false);
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
            try {
                // Set system look and feel
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            new CustomerAccounts();
        });
    }
}