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
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerDateModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ManageTransaction extends JFrame {
    
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 220, 120);
    
    private DefaultTableModel tableModel;
    private JTable transactionsTable;
    private int adminId = 0;
    private String adminName = "Administrator";
    
    // Filter panels with active state indicators
    private RoundedPanel dateRangeCard;
    private RoundedPanel transactionTypeCard;
    private RoundedPanel statusCard;
    
    // Labels for active filter indicators
    private JLabel dateRangeFilterLabel;
    private JLabel transactionTypeFilterLabel;
    private JLabel statusFilterLabel;
    
    // Store active filters
    private Map<String, Object> activeFilters = new HashMap<>();

    public ManageTransaction() {
        initialize();
    }
    
    public ManageTransaction(int adminId) {
        this.adminId = adminId;
        initialize();
    }
    
    private void initialize() {
        setTitle("KOB Manager - Transaction Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Load admin information
        loadAdminInfo();

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createTransactionContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
        
        // Try to set icon
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }

        setVisible(true);
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
            adminName = "Administrator";
        }
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
        
        // Add menu items - set Transaction Oversight as active
        addMenuItem(menuPanel, "Dashboard", false);
        addMenuItem(menuPanel, "Customer Accounts", false);
        addMenuItem(menuPanel, "Transaction Oversight", true);
        addMenuItem(menuPanel, "Approval Queue", false);
        addMenuItem(menuPanel, "Reports", false);
        addMenuItem(menuPanel, "Audit Logs", false);
        
        sidebar.add(menuPanel);
        sidebar.add(Box.createVerticalGlue());
        
        // Add admin profile at bottom of sidebar
        JPanel profilePanel = new RoundedPanel(15, new Color(40, 50, 90));
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
                    ManageTransaction.this,
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
    
    /**
     * Handles navigation to different screens
     */
    private void navigateToScreen(String screenName) {
        if (screenName.equals("Transaction Oversight")) {
            // Already on this screen, just refresh
            loadTransactionData();
            return;
        }
        
        dispose(); // Close the current window
        
        switch (screenName) {
            case "Dashboard":
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
                break;
            case "Customer Accounts":
                SwingUtilities.invokeLater(() -> new CustomerAccounts().setVisible(true));
                break;
            case "Reports":
                SwingUtilities.invokeLater(() -> new Report().setVisible(true));
                break;
            case "Approval Queue":
                SwingUtilities.invokeLater(() -> new ApproveTransaction(adminId).setVisible(true));
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

    private JPanel createTransactionContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        
        JLabel headerLabel = new JLabel("Transaction Management");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerLabel.setForeground(TEXT_COLOR);
        
        // Sub header info
        JLabel subHeaderLabel = new JLabel("View and manage all transaction records");
        subHeaderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subHeaderLabel.setForeground(LIGHT_TEXT_COLOR);
        
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(subHeaderLabel, BorderLayout.SOUTH);
        
        // Create buttons panel for top right
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        
        // Clear filters button
        JButton clearFiltersButton = new JButton("Clear Filters");
        clearFiltersButton.setBackground(new Color(220, 53, 69)); // Red color
        clearFiltersButton.setForeground(Color.WHITE);
        clearFiltersButton.setFocusPainted(false);
        clearFiltersButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        clearFiltersButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        clearFiltersButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        clearFiltersButton.setOpaque(true);
        clearFiltersButton.setBorderPainted(false);
        clearFiltersButton.addActionListener(e -> clearAllFilters());
        
        // Add hover effects
        clearFiltersButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                clearFiltersButton.setBackground(new Color(200, 35, 51));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                clearFiltersButton.setBackground(new Color(220, 53, 69));
            }
        });
        
        // Export button
        JButton exportButton = new JButton("Export PDF");
        exportButton.setBackground(ACCENT_COLOR);
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        exportButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        exportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportButton.setOpaque(true);
        exportButton.setBorderPainted(false);
        exportButton.addActionListener(e -> exportToPDF());
        
        // Add hover effects
        exportButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exportButton.setBackground(ACCENT_COLOR.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                exportButton.setBackground(ACCENT_COLOR);
            }
        });
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setBackground(SECONDARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        refreshButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        refreshButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        refreshButton.setOpaque(true);
        refreshButton.setBorderPainted(false);
        refreshButton.addActionListener(e -> {
            loadTransactionData();
            JOptionPane.showMessageDialog(ManageTransaction.this, 
                "Transaction data refreshed successfully.",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        // Add hover effects
        refreshButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                refreshButton.setBackground(new Color(25, 118, 210));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                refreshButton.setBackground(SECONDARY_COLOR);
            }
        });
        
        buttonsPanel.add(clearFiltersButton);
        buttonsPanel.add(refreshButton);
        buttonsPanel.add(exportButton);
        
        // Add header and buttons to top panel
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.WEST);
        topPanel.add(buttonsPanel, BorderLayout.EAST);
        
        // Dashboard content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Filter section title
        JLabel filterLabel = new JLabel("TRANSACTION FILTERS");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        filterLabel.setForeground(TEXT_COLOR);
        filterLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 15, 0));
        filterLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create filter cards panel
        JPanel filterCardsPanel = new JPanel();
        filterCardsPanel.setOpaque(false);
        filterCardsPanel.setLayout(new GridLayout(1, 3, 20, 0));
        filterCardsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterCardsPanel.setMaximumSize(new Dimension(1000, 120));
        
        // Initialize filter indicator labels
        dateRangeFilterLabel = new JLabel("");
        dateRangeFilterLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        dateRangeFilterLabel.setForeground(SECONDARY_COLOR);
        
        transactionTypeFilterLabel = new JLabel("");
        transactionTypeFilterLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        transactionTypeFilterLabel.setForeground(SECONDARY_COLOR);
        
        statusFilterLabel = new JLabel("");
        statusFilterLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        statusFilterLabel.setForeground(SECONDARY_COLOR);
        
        // Create filter cards
        dateRangeCard = createFilterCard("Date Range", "Select start and end dates", dateRangeFilterLabel);
        transactionTypeCard = createFilterCard("Transaction Type", "Filter by deposit, withdrawal, transfer", transactionTypeFilterLabel);
        statusCard = createFilterCard("Status", "Filter by transaction status", statusFilterLabel);
        
        filterCardsPanel.add(dateRangeCard);
        filterCardsPanel.add(transactionTypeCard);
        filterCardsPanel.add(statusCard);
        
        // Transaction list section title
        JLabel transactionsLabel = new JLabel("TRANSACTION LIST");
        transactionsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        transactionsLabel.setForeground(TEXT_COLOR);
        transactionsLabel.setBorder(BorderFactory.createEmptyBorder(30, 0, 15, 0));
        transactionsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create table with real data
        String[] columnNames = {"DATE", "CUSTOMER", "TYPE", "ACCOUNT", "AMOUNT", "STATUS", "ACTIONS"};
        
        // Create table model for dynamic updates
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only make Actions column editable
            }
        };

        transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(40);
        transactionsTable.setShowGrid(false);
        transactionsTable.setIntercellSpacing(new Dimension(0, 0));
        transactionsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        transactionsTable.setBackground(Color.WHITE);
        
        // Style table header
        JTableHeader header = transactionsTable.getTableHeader();
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(230, 230, 230)));
        header.setReorderingAllowed(false);
        
        // Load transaction data from database
        loadTransactionData();
        
        // Custom renderers for different columns
        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(new TransactionTypeRenderer());
        transactionsTable.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());
        transactionsTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        transactionsTable.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());
        
        // Set column widths
        transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        transactionsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(6).setPreferredWidth(100);
        
        // Add action listener for the action buttons
        transactionsTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = transactionsTable.rowAtPoint(e.getPoint());
                int col = transactionsTable.columnAtPoint(e.getPoint());
                
                if (col == 6 && row >= 0) {
                    // Get transaction details and show action dialog
                    String status = (String) tableModel.getValueAt(row, 5);
                    if (status.equals("APPROVED")) {
                        showTransactionDetails(row);
                    } else {
                        showTransactionReview(row);
                    }
                }
            }
        });
        
        // Create a rounded panel for the table
        RoundedPanel tablePanel = new RoundedPanel(15, Color.WHITE);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        // Create a scroll pane for the table
        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBackground(Color.WHITE);
        
        tablePanel.add(scrollPane);
        
        // Add box for padding around table
        Box tableBox = Box.createVerticalBox();
        tableBox.setOpaque(false);
        tableBox.add(tablePanel);
        tableBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Pagination panel
        JPanel paginationPanel = new JPanel();
        paginationPanel.setOpaque(false);
        paginationPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 10, 20));
        paginationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Previous page button
        JButton prevButton = new JButton("← Previous");
        prevButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        prevButton.setBackground(new Color(248, 250, 252));
        prevButton.setForeground(TEXT_COLOR);
        prevButton.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        prevButton.setFocusPainted(false);
        
        // Page number buttons
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
                pageButton.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
            }
            
            pageButton.setFocusPainted(false);
            paginationPanel.add(pageButton);
        }
        
        // Next page button
        JButton nextButton = new JButton("Next →");
        nextButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        nextButton.setBackground(new Color(248, 250, 252));
        nextButton.setForeground(TEXT_COLOR);
        nextButton.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230)));
        nextButton.setFocusPainted(false);
        
        paginationPanel.add(prevButton);
        paginationPanel.add(nextButton);
        
        // Footer panel
        JPanel footerPanel = new JPanel();
        footerPanel.setOpaque(false);
        footerPanel.setLayout(new BorderLayout());
        footerPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        JLabel versionLabel = new JLabel("Kurdish-O-Banking Manager Portal v1.0 | © 2025 KOB. All rights reserved.");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(LIGHT_TEXT_COLOR);
        
        footerPanel.add(versionLabel, BorderLayout.WEST);
        
        // Add components to content panel
        contentPanel.add(filterLabel);
        contentPanel.add(filterCardsPanel);
        contentPanel.add(transactionsLabel);
        contentPanel.add(tableBox);
        contentPanel.add(paginationPanel);
        
        // Add all panels to main panel
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        mainPanel.add(footerPanel, BorderLayout.SOUTH);
        
        return mainPanel;
    }
   /**
     * Creates a filter card with title, description, and filter indicator
     */
    private RoundedPanel createFilterCard(String title, String description, JLabel filterIndicator) {
        RoundedPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        card.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Top colored bar
        JPanel colorBar = new JPanel();
        colorBar.setBackground(SECONDARY_COLOR);
        colorBar.setPreferredSize(new Dimension(50, 5));
        
        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Description label
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(LIGHT_TEXT_COLOR);
        
        // Create a panel for the labels
        JPanel labelPanel = new JPanel();
        labelPanel.setOpaque(false);
        labelPanel.setLayout(new BoxLayout(labelPanel, BoxLayout.Y_AXIS));
        labelPanel.add(titleLabel);
        labelPanel.add(Box.createVerticalStrut(5));
        labelPanel.add(descLabel);
        labelPanel.add(Box.createVerticalStrut(3));
        labelPanel.add(filterIndicator);
        
        // Add components to card
        card.add(colorBar, BorderLayout.NORTH);
        card.add(labelPanel, BorderLayout.CENTER);
        
        // Add click handler
        card.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleFilterCardClick(title);
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                card.setBackground(new Color(250, 252, 255));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                card.setBackground(Color.WHITE);
            }
        });
        
        // Add shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 5, 5),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        return card;
    }
    
    /**
     * Handles filter card click events
     */
    private void handleFilterCardClick(String filterType) {
        switch (filterType) {
            case "Date Range":
                showDateRangeFilterDialog();
                break;
            case "Transaction Type":
                showTransactionTypeFilterDialog();
                break;
            case "Status":
                showStatusFilterDialog();
                break;
            default:
                // Do nothing
        }
    }
    
    /**
     * Shows date range filter dialog
     */
    private void showDateRangeFilterDialog() {
        // Create a custom dialog for date range selection
        JDialog dialog = new JDialog(this, "Select Date Range", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Start date
        JPanel startDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        startDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel startDateLabel = new JLabel("Start Date:");
        startDateLabel.setPreferredSize(new Dimension(80, 25));
        
        // Set default to 30 days ago
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, -30);
        Date thirtyDaysAgo = cal.getTime();
        
        // Use JSpinner with DateModel for date selection
        JSpinner startDateSpinner = new JSpinner(new SpinnerDateModel(thirtyDaysAgo, null, new Date(), Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor startDateEditor = new JSpinner.DateEditor(startDateSpinner, "yyyy-MM-dd");
        startDateSpinner.setEditor(startDateEditor);
        startDateSpinner.setPreferredSize(new Dimension(150, 25));
        
        startDatePanel.add(startDateLabel);
        startDatePanel.add(startDateSpinner);
        
        // End date
        JPanel endDatePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        endDatePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel endDateLabel = new JLabel("End Date:");
        endDateLabel.setPreferredSize(new Dimension(80, 25));
        
        JSpinner endDateSpinner = new JSpinner(new SpinnerDateModel(new Date(), null, null, Calendar.DAY_OF_MONTH));
        JSpinner.DateEditor endDateEditor = new JSpinner.DateEditor(endDateSpinner, "yyyy-MM-dd");
        endDateSpinner.setEditor(endDateEditor);
        endDateSpinner.setPreferredSize(new Dimension(150, 25));
        
        endDatePanel.add(endDateLabel);
        endDatePanel.add(endDateSpinner);
        
        // Quick date ranges panel
        JPanel quickRangesPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quickRangesPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel quickRangeLabel = new JLabel("Quick Select:");
        quickRangeLabel.setPreferredSize(new Dimension(80, 25));
        
        JButton todayButton = new JButton("Today");
        JButton weekButton = new JButton("Last 7 Days");
        JButton monthButton = new JButton("Last 30 Days");
        
        // Style the quick buttons
        Font quickButtonFont = new Font("SansSerif", Font.PLAIN, 12);
        todayButton.setFont(quickButtonFont);
        weekButton.setFont(quickButtonFont);
        monthButton.setFont(quickButtonFont);
        
        todayButton.addActionListener(e -> {
            Date today = new Date();
            startDateSpinner.setValue(today);
            endDateSpinner.setValue(today);
        });
        
        weekButton.addActionListener(e -> {
            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -7);
            startDateSpinner.setValue(calendar.getTime());
            endDateSpinner.setValue(today);
        });
        
        monthButton.addActionListener(e -> {
            Date today = new Date();
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.DAY_OF_MONTH, -30);
            startDateSpinner.setValue(calendar.getTime());
            endDateSpinner.setValue(today);
        });
        
        JPanel buttonsContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        buttonsContainer.add(todayButton);
        buttonsContainer.add(weekButton);
        buttonsContainer.add(monthButton);
        
        quickRangesPanel.add(quickRangeLabel);
        quickRangesPanel.add(buttonsContainer);
        
        // Add panels to main panel
        mainPanel.add(startDatePanel);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(endDatePanel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(quickRangesPanel);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton applyButton = new JButton("Apply Filter");
        applyButton.setBackground(SECONDARY_COLOR);
        applyButton.setForeground(Color.WHITE);
        applyButton.addActionListener(e -> {
            // Get selected dates
            Date startDate = (Date) startDateSpinner.getValue();
            Date endDate = (Date) endDateSpinner.getValue();
            
            // Apply date filter
            applyDateFilter(startDate, endDate);
            
            // Close dialog
            dialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);
        
        mainPanel.add(buttonPanel);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Shows transaction type filter dialog
     */
    private void showTransactionTypeFilterDialog() {
        JDialog dialog = new JDialog(this, "Select Transaction Types", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Select Transaction Types:");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create checkboxes for transaction types
        JCheckBox depositCheckBox = new JCheckBox("Deposit");
        depositCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        depositCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        depositCheckBox.setSelected(true);
        
        JCheckBox withdrawalCheckBox = new JCheckBox("Withdrawal");
        withdrawalCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        withdrawalCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        withdrawalCheckBox.setSelected(true);
        
        JCheckBox transferCheckBox = new JCheckBox("Transfer");
        transferCheckBox.setFont(new Font("SansSerif", Font.PLAIN, 14));
        transferCheckBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        transferCheckBox.setSelected(true);
        
        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(depositCheckBox);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(withdrawalCheckBox);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(transferCheckBox);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton applyButton = new JButton("Apply Filter");
        applyButton.setBackground(SECONDARY_COLOR);
        applyButton.setForeground(Color.WHITE);
        applyButton.addActionListener(e -> {
            // Create list of selected transaction types
            List<String> selectedTypes = new ArrayList<>();
            
            if (depositCheckBox.isSelected()) {
                selectedTypes.add("Deposit");
            }
            
            if (withdrawalCheckBox.isSelected()) {
                selectedTypes.add("Withdrawal");
            }
            
            if (transferCheckBox.isSelected()) {
                selectedTypes.add("Transfer");
            }
            
            // Apply transaction type filter
            applyTransactionTypeFilter(selectedTypes);
            
            // Close dialog
            dialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);
        
        mainPanel.add(buttonPanel);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Shows status filter dialog
     */
    private void showStatusFilterDialog() {
        JDialog dialog = new JDialog(this, "Select Transaction Status", true);
        dialog.setSize(350, 250);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Select Transaction Status:");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create radio buttons for status
        JRadioButton allRadio = new JRadioButton("All Statuses");
        allRadio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        allRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        allRadio.setSelected(true);
        
        JRadioButton approvedRadio = new JRadioButton("Approved Only");
        approvedRadio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        approvedRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JRadioButton pendingRadio = new JRadioButton("Pending Only");
        pendingRadio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        pendingRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JRadioButton rejectedRadio = new JRadioButton("Rejected/Failed Only");
        rejectedRadio.setFont(new Font("SansSerif", Font.PLAIN, 14));
        rejectedRadio.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Group radio buttons
        ButtonGroup statusGroup = new ButtonGroup();
        statusGroup.add(allRadio);
        statusGroup.add(approvedRadio);
        statusGroup.add(pendingRadio);
        statusGroup.add(rejectedRadio);
        
        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createVerticalStrut(15));
        mainPanel.add(allRadio);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(approvedRadio);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(pendingRadio);
        mainPanel.add(Box.createVerticalStrut(10));
        mainPanel.add(rejectedRadio);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton applyButton = new JButton("Apply Filter");
        applyButton.setBackground(SECONDARY_COLOR);
        applyButton.setForeground(Color.WHITE);
        applyButton.addActionListener(e -> {
            // Get selected status
            String selectedStatus = null;
            
            if (approvedRadio.isSelected()) {
                selectedStatus = "APPROVED";
            } else if (pendingRadio.isSelected()) {
                selectedStatus = "PENDING";
            } else if (rejectedRadio.isSelected()) {
                selectedStatus = "REJECTED";
            }
            
            // Apply status filter
            applyStatusFilter(selectedStatus);
            
            // Close dialog
            dialog.dispose();
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(applyButton);
        
        mainPanel.add(buttonPanel);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Shows transaction details dialog
     */
    private void showTransactionDetails(int row) {
        // Get transaction details from the table
        String date = (String) tableModel.getValueAt(row, 0);
        String customer = (String) tableModel.getValueAt(row, 1);
        String type = (String) tableModel.getValueAt(row, 2);
        String account = (String) tableModel.getValueAt(row, 3);
        String amount = (String) tableModel.getValueAt(row, 4);
        String status = (String) tableModel.getValueAt(row, 5);
        
        // Create transaction details dialog
        JDialog dialog = new JDialog(this, "Transaction Details", true);
        dialog.setSize(400, 350);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Transaction Details");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create details panels
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Add field rows
        addDetailRow(detailsPanel, "Date & Time:", date);
        addDetailRow(detailsPanel, "Customer:", customer);
        addDetailRow(detailsPanel, "Transaction Type:", type);
        addDetailRow(detailsPanel, "Account Number:", account);
        addDetailRow(detailsPanel, "Amount:", amount);
        addDetailRow(detailsPanel, "Status:", status);
        addDetailRow(detailsPanel, "Reference ID:", "TRX" + (1000 + row));
        
        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(detailsPanel);
        mainPanel.add(Box.createVerticalGlue());
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Shows transaction review dialog
     */
    private void showTransactionReview(int row) {
        // Get transaction details from the table
        String date = (String) tableModel.getValueAt(row, 0);
        String customer = (String) tableModel.getValueAt(row, 1);
        String type = (String) tableModel.getValueAt(row, 2);
        String account = (String) tableModel.getValueAt(row, 3);
        String amount = (String) tableModel.getValueAt(row, 4);
        String status = (String) tableModel.getValueAt(row, 5);
        
        // Create transaction review dialog
        JDialog dialog = new JDialog(this, "Transaction Review", true);
        dialog.setSize(450, 450);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel titleLabel = new JLabel("Transaction Review");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Create details panels
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        // Add field rows
        addDetailRow(detailsPanel, "Date & Time:", date);
        addDetailRow(detailsPanel, "Customer:", customer);
        addDetailRow(detailsPanel, "Transaction Type:", type);
        addDetailRow(detailsPanel, "Account Number:", account);
        addDetailRow(detailsPanel, "Amount:", amount);
        addDetailRow(detailsPanel, "Current Status:", status);
        addDetailRow(detailsPanel, "Reference ID:", "TRX" + (1000 + row));
        
        // Add notes field
        JLabel notesLabel = new JLabel("Review Notes:");
        notesLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        notesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        notesLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 5, 0));
        
        JTextField notesField = new JTextField();
        notesField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        notesField.setAlignmentX(Component.LEFT_ALIGNMENT);
        notesField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        
        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(detailsPanel);
        mainPanel.add(notesLabel);
        mainPanel.add(notesField);
        mainPanel.add(Box.createVerticalStrut(20));
        
        // Action buttons panel
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 0));
        actionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JButton approveButton = new JButton("Approve");
        approveButton.setBackground(new Color(40, 167, 69)); // Green
        approveButton.setForeground(Color.WHITE);
        approveButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        approveButton.addActionListener(e -> {
            // Update transaction status logic would go here
            JOptionPane.showMessageDialog(dialog,
                "Transaction approved successfully!",
                "Approval Successful",
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            
            // Refresh data after approval
            loadTransactionData();
        });
        
        JButton rejectButton = new JButton("Reject");
        rejectButton.setBackground(new Color(220, 53, 69)); // Red
        rejectButton.setForeground(Color.WHITE);
        rejectButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        rejectButton.addActionListener(e -> {
            // Update transaction status logic would go here
            JOptionPane.showMessageDialog(dialog,
                "Transaction rejected.",
                "Transaction Rejected",
                JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
            
            // Refresh data after rejection
            loadTransactionData();
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        actionPanel.add(approveButton);
        actionPanel.add(rejectButton);
        actionPanel.add(cancelButton);
        
        mainPanel.add(actionPanel);
        
        dialog.add(mainPanel, BorderLayout.CENTER);
        dialog.setVisible(true);
    }
    
    /**
     * Adds a detail row to the details panel
     */
    private void addDetailRow(JPanel panel, String label, String value) {
        JPanel rowPanel = new JPanel(new BorderLayout(10, 0));
        rowPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        rowPanel.add(labelComponent, BorderLayout.WEST);
        rowPanel.add(valueComponent, BorderLayout.CENTER);
        
        panel.add(rowPanel);
        panel.add(Box.createVerticalStrut(10));
    }
    
    /**
     * Applies date filter to transaction data
     */
    private void applyDateFilter(Date startDate, Date endDate) {
        // Store the date filter in active filters
        Map<String, Date> dateFilter = new HashMap<>();
        dateFilter.put("startDate", startDate);
        dateFilter.put("endDate", endDate);
        activeFilters.put("dateRange", dateFilter);
        
        // Update filter indicator
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy");
        dateRangeFilterLabel.setText("Filter: " + displayFormat.format(startDate) + " to " + displayFormat.format(endDate));
        
        // Apply all active filters
        applyAllFilters();
    }
    
    /**
     * Applies transaction type filter
     */
    private void applyTransactionTypeFilter(List<String> selectedTypes) {
        // Store the type filter in active filters
        activeFilters.put("transactionTypes", selectedTypes);
        
        // Update filter indicator
        if (selectedTypes.size() == 3 || selectedTypes.isEmpty()) {
            transactionTypeFilterLabel.setText("");
        } else {
            transactionTypeFilterLabel.setText("Filter: " + String.join(", ", selectedTypes));
        }
        
        // Apply all active filters
        applyAllFilters();
    }
    
    /**
     * Applies status filter
     */
    private void applyStatusFilter(String status) {
        // Store the status filter in active filters
        activeFilters.put("status", status);
        
        // Update filter indicator
        if (status == null) {
            statusFilterLabel.setText("");
        } else {
            statusFilterLabel.setText("Filter: " + status);
        }
        
        // Apply all active filters
        applyAllFilters();
    }
    
    /**
     * Clears all active filters
     */
    private void clearAllFilters() {
        // Clear all active filters
        activeFilters.clear();
        
        // Clear filter indicators
        dateRangeFilterLabel.setText("");
        transactionTypeFilterLabel.setText("");
        statusFilterLabel.setText("");
        
        // Reload all transaction data
        loadTransactionData();
    }
    
    /**
     * Applies all active filters to the transaction data
     */
    private void applyAllFilters() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Build the base query
            StringBuilder queryBuilder = new StringBuilder();
            queryBuilder.append("SELECT t.transaction_id, t.account_id, t.transaction_type, t.amount, ");
            queryBuilder.append("t.transaction_date, t.description, t.status, t.account_number, ");
            queryBuilder.append("a.username, a.account_number as full_account_number ");
            queryBuilder.append("FROM transactions t ");
            queryBuilder.append("LEFT JOIN accounts a ON t.account_id = a.account_id ");
            queryBuilder.append("WHERE 1=1 "); // Always true condition to make it easier to add AND clauses
            
            // Add conditions based on active filters
            List<Object> params = new ArrayList<>();
            
            // Date range filter
            if (activeFilters.containsKey("dateRange")) {
                @SuppressWarnings("unchecked")
                Map<String, Date> dateFilter = (Map<String, Date>) activeFilters.get("dateRange");
                Date startDate = dateFilter.get("startDate");
                Date endDate = dateFilter.get("endDate");
                
                // Add one day to end date to include the end date in results
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(endDate);
                calendar.add(Calendar.DAY_OF_MONTH, 1);
                Date endDatePlusOne = calendar.getTime();
                
                SimpleDateFormat sqlDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                queryBuilder.append("AND DATE(t.transaction_date) BETWEEN ? AND ? ");
                params.add(sqlDateFormat.format(startDate));
                params.add(sqlDateFormat.format(endDatePlusOne));
            }
            
            // Transaction type filter
            if (activeFilters.containsKey("transactionTypes")) {
                @SuppressWarnings("unchecked")
                List<String> types = (List<String>) activeFilters.get("transactionTypes");
                
                if (!types.isEmpty() && types.size() < 3) { // If not all types selected
                    queryBuilder.append("AND t.transaction_type IN (");
                    for (int i = 0; i < types.size(); i++) {
                        queryBuilder.append("?");
                        if (i < types.size() - 1) {
                            queryBuilder.append(",");
                        }
                    }
                    queryBuilder.append(") ");
                    
                    // Add each type as a parameter
                    params.addAll(types);
                }
            }
            
            // Status filter
            if (activeFilters.containsKey("status") && activeFilters.get("status") != null) {
                String status = (String) activeFilters.get("status");
                queryBuilder.append("AND t.status = ? ");
                params.add(status);
            }
            
            // Add order by clause
            queryBuilder.append("ORDER BY t.transaction_date DESC");
            
            // Prepare statement
            PreparedStatement stmt = conn.prepareStatement(queryBuilder.toString());
            
            // Set parameters
            for (int i = 0; i < params.size(); i++) {
                stmt.setObject(i + 1, params.get(i));
            }
            
            // Execute query
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String transactionDate = "";
                try {
                    java.sql.Timestamp timestamp = rs.getTimestamp("transaction_date");
                    transactionDate = displayFormat.format(timestamp);
                } catch (Exception e) {
                    transactionDate = "N/A";
                }
                
                String username = rs.getString("username");
                if (username == null || username.isEmpty()) {
                    username = "User #" + rs.getInt("account_id");
                }
                
                String transactionType = rs.getString("transaction_type");
                
                String accountNumber = rs.getString("full_account_number");
                if (accountNumber == null || accountNumber.isEmpty()) {
                    accountNumber = "****" + rs.getInt("account_id");
                } else {
                    // Mask account number for privacy
                    accountNumber = "****" + accountNumber.substring(Math.max(0, accountNumber.length() - 4));
                }
                
                double amount = rs.getDouble("amount");
                String formattedAmount = "";
                
                // Format amount with sign
                if (transactionType.equalsIgnoreCase("Deposit")) {
                    formattedAmount = "+" + currencyFormat.format(Math.abs(amount));
                } else if (transactionType.equalsIgnoreCase("Withdrawal")) {
                    formattedAmount = "-" + currencyFormat.format(Math.abs(amount));
                } else {
                    // For transfers, negative amounts are outgoing, positive are incoming
                    if (amount < 0) {
                        formattedAmount = "-" + currencyFormat.format(Math.abs(amount));
                    } else {
                        formattedAmount = "+" + currencyFormat.format(amount);
                    }
                }
                
                String status = rs.getString("status");
                if (status == null || status.isEmpty()) {
                    status = "PENDING";
                }
                
                // Action button text based on status
                String actionText = status.equals("APPROVED") ? "Details" : "Review";
                
                // Add the row to the table
                tableModel.addRow(new Object[]{
                    transactionDate,
                    username,
                    transactionType,
                    accountNumber,
                    formattedAmount,
                    status,
                    actionText
                });
            }
            
            // If no transactions were found, display a message
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"No transactions found matching filters", "", "", "", "", "", ""});
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error applying filters: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Loads transaction data from the database
     */
    private void loadTransactionData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Join with accounts table to get usernames
            String query = "SELECT t.transaction_id, t.account_id, t.transaction_type, t.amount, " +
                           "t.transaction_date, t.description, t.status, t.account_number, " +
                           "a.username, a.account_number as full_account_number " +
                           "FROM transactions t " +
                           "LEFT JOIN accounts a ON t.account_id = a.account_id " +
                           "ORDER BY t.transaction_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String transactionDate = "";
                try {
                    java.sql.Timestamp timestamp = rs.getTimestamp("transaction_date");
                    transactionDate = displayFormat.format(timestamp);
                } catch (Exception e) {
                    transactionDate = "N/A";
                }
                
                String username = rs.getString("username");
                if (username == null || username.isEmpty()) {
                    username = "User #" + rs.getInt("account_id");
                }
                
                String transactionType = rs.getString("transaction_type");
                
                String accountNumber = rs.getString("full_account_number");
                if (accountNumber == null || accountNumber.isEmpty()) {
                    accountNumber = "****" + rs.getInt("account_id");
                } else {
                    // Mask account number for privacy
                    accountNumber = "****" + accountNumber.substring(Math.max(0, accountNumber.length() - 4));
                }
                
                double amount = rs.getDouble("amount");
                String formattedAmount = "";
                
                // Format amount with sign
                if (transactionType.equalsIgnoreCase("Deposit")) {
                    formattedAmount = "+" + currencyFormat.format(Math.abs(amount));
                } else if (transactionType.equalsIgnoreCase("Withdrawal")) {
                    formattedAmount = "-" + currencyFormat.format(Math.abs(amount));
                } else {
                    // For transfers, negative amounts are outgoing, positive are incoming
                    if (amount < 0) {
                        formattedAmount = "-" + currencyFormat.format(Math.abs(amount));
                    } else {
                        formattedAmount = "+" + currencyFormat.format(amount);
                    }
                }
                
                String status = rs.getString("status");
                if (status == null || status.isEmpty()) {
                    status = "PENDING";
                }
                
                // Action button text based on status
                String actionText = status.equals("APPROVED") ? "Details" : "Review";
                
                // Add the row to the table
                tableModel.addRow(new Object[]{
                    transactionDate,
                    username,
                    transactionType,
                    accountNumber,
                    formattedAmount,
                    status,
                    actionText
                });
            }
            
            // If no transactions were found, display a message
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"No transactions found", "", "", "", "", "", ""});
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
     * Export the current table data to a PDF file
     * Requires iText library: com.itextpdf (add to your dependencies)
     */
    private void exportToPDF() {
        // Check if there's data to export
        if (tableModel.getRowCount() == 0 || 
            (tableModel.getRowCount() == 1 && tableModel.getValueAt(0, 0).toString().contains("No transactions"))) {
            JOptionPane.showMessageDialog(this,
                "No data available to export.",
                "Export Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF File");
        
        // Set default filename with timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFileName = "transactions_" + dateFormat.format(new Date()) + ".pdf";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        // Set file filter to only show PDF files
        FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
        fileChooser.setFileFilter(pdfFilter);
        
        // Show save dialog
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Add .pdf extension if not present
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }
            
            // Check if file already exists and confirm overwrite
            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(this,
                    "The file " + fileToSave.getName() + " already exists. Do you want to overwrite it?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            try {
                // Create PDF document
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();
                
                // Add title
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Transaction Report", titleFont);
                title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);
                
                // Add timestamp
                com.itextpdf.text.Font smallFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8);
                com.itextpdf.text.Paragraph timestamp = new com.itextpdf.text.Paragraph(
                    "Report generated on: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), 
                    smallFont);
                timestamp.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                timestamp.setSpacingAfter(20);
                document.add(timestamp);
                
                // Add filter information if any filters are active
                if (!activeFilters.isEmpty()) {
                    com.itextpdf.text.Font filterFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, com.itextpdf.text.Font.ITALIC);
                    
                    StringBuilder filterInfo = new StringBuilder("Filters applied: ");
                    boolean first = true;
                    
                    if (activeFilters.containsKey("dateRange")) {
                        @SuppressWarnings("unchecked")
                        Map<String, Date> dateFilter = (Map<String, Date>) activeFilters.get("dateRange");
                        SimpleDateFormat filterDateFormat = new SimpleDateFormat("dd/MM/yyyy");
                        filterInfo.append("Date Range (")
                            .append(filterDateFormat.format(dateFilter.get("startDate")))
                            .append(" to ")
                            .append(filterDateFormat.format(dateFilter.get("endDate")))
                            .append(")");
                        first = false;
                    }
                    
                    if (activeFilters.containsKey("transactionTypes")) {
                        @SuppressWarnings("unchecked")
                        List<String> types = (List<String>) activeFilters.get("transactionTypes");
                        if (!types.isEmpty() && types.size() < 3) {
                            if (!first) filterInfo.append(", ");
                            filterInfo.append("Transaction Types (").append(String.join(", ", types)).append(")");
                            first = false;
                        }
                    }
                    
                    if (activeFilters.containsKey("status") && activeFilters.get("status") != null) {
                        if (!first) filterInfo.append(", ");
                        filterInfo.append("Status (").append(activeFilters.get("status")).append(")");
                    }
                    
                    com.itextpdf.text.Paragraph filterParagraph = new com.itextpdf.text.Paragraph(filterInfo.toString(), filterFont);
                    filterParagraph.setAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
                    filterParagraph.setSpacingAfter(10);
                    document.add(filterParagraph);
                }
                
                // Create table
                com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(tableModel.getColumnCount() - 1); // Skip ACTIONS column
                pdfTable.setWidthPercentage(100);
                
                // Set column widths (percentages)
                float[] columnWidths = {15f, 20f, 15f, 15f, 15f, 20f};
                pdfTable.setWidths(columnWidths);
                
                // Define fonts for headers and data
                com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9);
                
                // Add table headers
                for (int i = 0; i < tableModel.getColumnCount() - 1; i++) { // Skip ACTIONS column
                    com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(tableModel.getColumnName(i), headerFont));
                    cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(248, 250, 252));
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }
                
                // Add data rows
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    if (tableModel.getValueAt(row, 0).toString().contains("No transactions")) {
                        continue; // Skip "No transactions found" row
                    }
                    
                    for (int col = 0; col < tableModel.getColumnCount() - 1; col++) { // Skip ACTIONS column
                        // Get cell value
                        Object value = tableModel.getValueAt(row, col);
                        String cellValue = (value == null) ? "" : value.toString();
                        
                        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                            new com.itextpdf.text.Phrase(cellValue, dataFont));
                        
                        // Align different columns as needed
                        if (col == 4) { // AMOUNT column
                            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                            
                            // Set color based on positive/negative values
                            if (cellValue.startsWith("+")) {
                                cell.setPhrase(new com.itextpdf.text.Phrase(cellValue, 
                                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, 
                                        com.itextpdf.text.Font.NORMAL, new com.itextpdf.text.BaseColor(0, 128, 0))));
                            } else if (cellValue.startsWith("-")) {
                                cell.setPhrase(new com.itextpdf.text.Phrase(cellValue, 
                                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, 
                                        com.itextpdf.text.Font.NORMAL, new com.itextpdf.text.BaseColor(220, 53, 69))));
                            }
                        } else if (col == 2 || col == 5) { // TYPE and STATUS columns
                            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                            
                            // Set background colors for transaction type
                            if (col == 2) {
                                String type = cellValue.toLowerCase();
                                if (type.contains("deposit")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(230, 255, 250));
                                } else if (type.contains("withdrawal")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 236, 236));
                                } else {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 250, 230));
                                }
                            }
                            
                            // Set background colors for status
                            if (col == 5) {
                                String status = cellValue.toLowerCase();
                                if (status.contains("approved")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(230, 255, 250));
                                } else if (status.contains("pending")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 250, 230));
                                } else if (status.contains("rejected") || status.contains("failed")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 236, 236));
                                }
                            }
                        }
                        
                        cell.setPadding(5);
                        pdfTable.addCell(cell);
                    }
                }
                
                document.add(pdfTable);
                
                // Add footer
                document.add(new com.itextpdf.text.Paragraph("\n"));
                com.itextpdf.text.Paragraph footer = new com.itextpdf.text.Paragraph(
                    "This is an official report from KOB Manager. For any inquiries, please contact the bank.", 
                    smallFont);
                footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(footer);
                
                // Close document
                document.close();
                
                JOptionPane.showMessageDialog(this,
                    "Transaction data successfully exported to PDF:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting data to PDF: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
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

            if (value == null) {
                return label;
            }
            
            String type = value.toString();
            if (type.equalsIgnoreCase("Deposit")) {
                label.setBackground(new Color(230, 255, 250)); // #e6fffa
                label.setForeground(new Color(13, 110, 253)); // #0d6efd
            } else if (type.equalsIgnoreCase("Withdrawal")) {
                label.setBackground(new Color(255, 236, 236)); // #ffecec
                label.setForeground(new Color(220, 53, 69)); // #dc3545
            } else {
                // Transfer or other types
                label.setBackground(new Color(255, 250, 230));
                label.setForeground(new Color(255, 153, 0));
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
            
            if (value == null) {
                return label;
            }
            
            if (value.toString().startsWith("+")) {
                label.setForeground(new Color(0, 128, 0)); // Green for positive
            } else if (value.toString().startsWith("-")) {
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

            if (value == null) {
                return label;
            }
            
            String status = value.toString();
            if (status.equalsIgnoreCase("APPROVED")) {
                label.setBackground(new Color(230, 255, 250)); // #e6fffa
                label.setForeground(new Color(13, 110, 253)); // #0d6efd
            } else if (status.equalsIgnoreCase("PENDING")) {
                label.setBackground(new Color(255, 250, 230));
                label.setForeground(new Color(255, 153, 0));
            } else {
                label.setBackground(new Color(255, 236, 236)); // #ffecec
                label.setForeground(new Color(220, 53, 69)); // #dc3545
            }
            label.setBorder(BorderFactory.createLineBorder(label.getBackground()));
            return label;
        }
    }

    // Custom renderer for Action column
    private class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = new JButton(value.toString());
            button.setFont(new Font("SansSerif", Font.PLAIN, 12));
            button.setBackground(SECONDARY_COLOR); // Use class constant instead of creating new color
            button.setForeground(Color.WHITE);
            button.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            button.setFocusPainted(false);
            return button;
        }
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
            try {
                // Test database connection
                Connection conn = DatabaseConnection.getConnection();
                conn.close();
                
                // If connection successful, show dashboard
                new ManageTransaction().setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to database: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}