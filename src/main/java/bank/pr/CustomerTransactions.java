package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Desktop;
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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

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

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.Element;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Chunk;
import com.itextpdf.text.Image;

import java.io.FileOutputStream;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 * This class displays all transactions for a specific customer account
 * Updated to match the ManagerDashboard design style
 */
public class CustomerTransactions extends JFrame {
    
    // Colors matching the ManagerDashboard
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color HIGHLIGHT_COLOR = new Color(255, 220, 120);
    private static final Color SUCCESS_COLOR = new Color(25, 135, 84);
    private static final Color WARNING_COLOR = new Color(255, 193, 7);
    private static final Color DANGER_COLOR = new Color(220, 53, 69);
    
    private DefaultTableModel tableModel;
    private JTable transactionsTable;
    private int adminId;
    private int accountId;
    private String customerName;
    private String accountType;
    private double accountBalance;
    private String adminName = "Administrator"; // Default name
    
    /**
     * Constructor that takes account ID and admin ID
     * @param accountId The customer account ID to show transactions for
     * @param adminId The admin ID for return navigation
     */
    public CustomerTransactions(int accountId, int adminId) {
        this.accountId = accountId;
        this.adminId = adminId;
        
        // Load admin information
        loadAdminInfo();
        
        // Load customer information first
        if (loadCustomerInfo()) {
            initialize();
            applyTransactionFilter("All");        } else {
            JOptionPane.showMessageDialog(this,
                "Could not find account with ID: " + accountId,
                "Account Not Found",
                JOptionPane.ERROR_MESSAGE);
            dispose();
            SwingUtilities.invokeLater(() -> new CustomerAccounts(adminId).setVisible(true));
        }
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
        setTitle("KOB Manager - Transaction History");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        // Create sidebar panel with gradient and rounded components
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel with shadowed cards
        JPanel mainContentPanel = createTransactionsContentPanel();
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
        
        // Back to Customer Accounts
        JPanel backItem = new JPanel();
        backItem.setLayout(new BorderLayout());
        backItem.setOpaque(false);
        backItem.setMaximumSize(new Dimension(250, 50));
        backItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backItem.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
        
        JLabel backLabel = new JLabel("← Back to Accounts");
        backLabel.setForeground(new Color(200, 200, 200));
        backLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        
        backItem.add(backLabel, BorderLayout.CENTER);
        
        // Add hover effect
        backItem.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                backItem.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 3, 0, 0, new Color(100, 100, 100)),
                    BorderFactory.createEmptyBorder(10, 15, 10, 10)
                ));
                backLabel.setForeground(new Color(230, 230, 230));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                backItem.setBorder(BorderFactory.createEmptyBorder(10, 18, 10, 10));
                backLabel.setForeground(new Color(200, 200, 200));
            }
            
            @Override
            public void mouseClicked(MouseEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> new CustomerAccounts(adminId).setVisible(true));
            }
        });
        
        menuPanel.add(backItem);
        menuPanel.add(Box.createVerticalStrut(5));
        
        // Active Transaction History
        JPanel activeItem = new JPanel();
        activeItem.setLayout(new BorderLayout());
        activeItem.setOpaque(false);
        activeItem.setMaximumSize(new Dimension(250, 50));
        activeItem.setCursor(new Cursor(Cursor.HAND_CURSOR));
        activeItem.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 3, 0, 0, ACCENT_COLOR),
            BorderFactory.createEmptyBorder(10, 15, 10, 10)
        ));
        
        JLabel activeLabel = new JLabel("Transaction History");
        activeLabel.setForeground(Color.WHITE);
        activeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        
        activeItem.add(activeLabel, BorderLayout.CENTER);
        menuPanel.add(activeItem);
        menuPanel.add(Box.createVerticalStrut(5));
        
        // Add other menu items
        addMenuItem(menuPanel, "Dashboard", false);
        addMenuItem(menuPanel, "Customer Accounts", false);
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
                    CustomerTransactions.this,
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
     * Creates the main content panel for transactions
     */
    private JPanel createTransactionsContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(30, 30, 30, 30));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setOpaque(false);
        headerPanel.setLayout(new BorderLayout());
        
        JLabel headerLabel = new JLabel("Transaction History");
        headerLabel.setFont(new Font("SansSerif", Font.BOLD, 26));
        headerLabel.setForeground(TEXT_COLOR);
        
        // Subheader
        JLabel subHeaderLabel = new JLabel("Account: " + customerName + " (#" + accountId + ")");
        subHeaderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subHeaderLabel.setForeground(LIGHT_TEXT_COLOR);
        
        headerPanel.add(headerLabel, BorderLayout.NORTH);
        headerPanel.add(subHeaderLabel, BorderLayout.SOUTH);
        
        // Action buttons panel
        JPanel actionPanel = new JPanel();
        actionPanel.setOpaque(false);
        actionPanel.setLayout(new BoxLayout(actionPanel, BoxLayout.X_AXIS));
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setBackground(SECONDARY_COLOR);
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
                refreshButton.setBackground(new Color(25, 118, 210));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                refreshButton.setBackground(SECONDARY_COLOR);
            }
        });
        
        refreshButton.addActionListener(e -> {
            applyTransactionFilter("All");
            showSuccessMessage("Transaction data refreshed successfully");
        });
        
        // Export button
        JButton exportButton = new JButton("Export");
        exportButton.setBackground(SUCCESS_COLOR);
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        exportButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        exportButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        exportButton.setOpaque(true);
        exportButton.setBorderPainted(false);
        
        // Add hover effects
        exportButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                exportButton.setBackground(new Color(21, 115, 71));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                exportButton.setBackground(SUCCESS_COLOR);
            }
        });
        
        // Update export button action to use the new PDF export method
        exportButton.addActionListener(e -> {
            exportTransactionsToPDF();
        });
        
        // Add buttons to action panel
        actionPanel.add(refreshButton);
        actionPanel.add(Box.createHorizontalStrut(15));
        actionPanel.add(exportButton);
        
        // Add header and action panels to top panel
        JPanel topPanel = new JPanel();
        topPanel.setOpaque(false);
        topPanel.setLayout(new BorderLayout());
        topPanel.add(headerPanel, BorderLayout.WEST);
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        // Account summary panel
        RoundedPanel accountPanel = new RoundedPanel(15, Color.WHITE);
        accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));
        accountPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Account information layout
        JPanel accountInfoPanel = new JPanel(new GridLayout(2, 2, 20, 15));
        accountInfoPanel.setOpaque(false);
        
        // Create info cards
        JPanel nameCard = createInfoCard("Account Holder", customerName, SECONDARY_COLOR);
        JPanel idCard = createInfoCard("Account ID", "#" + accountId, ACCENT_COLOR);
        JPanel typeCard = createInfoCard("Account Type", accountType, new Color(140, 90, 210));
        
        // Format balance
        String balanceStr = new DecimalFormat("$#,##0.00").format(accountBalance);
        Color balanceColor = accountBalance > 0 ? SUCCESS_COLOR : DANGER_COLOR;
        JPanel balanceCard = createInfoCard("Current Balance", balanceStr, balanceColor);
        
        accountInfoPanel.add(nameCard);
        accountInfoPanel.add(typeCard);
        accountInfoPanel.add(idCard);
        accountInfoPanel.add(balanceCard);
        
        accountPanel.add(accountInfoPanel);
        
        // Transactions table panel
        RoundedPanel tablePanel = new RoundedPanel(15, Color.WHITE);
        tablePanel.setLayout(new BorderLayout());
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Transactions header
        JLabel transactionsLabel = new JLabel("Transaction History");
        transactionsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        transactionsLabel.setForeground(TEXT_COLOR);
        transactionsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
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
        transactionsTable.setRowHeight(40);
        transactionsTable.setShowGrid(false);
        transactionsTable.setIntercellSpacing(new Dimension(0, 0));
        transactionsTable.setFont(new Font("SansSerif", Font.PLAIN, 14));
        transactionsTable.setBackground(Color.WHITE);
        transactionsTable.setSelectionBackground(new Color(232, 240, 254));
        
        // Custom renderer for status column with rounded corners
        transactionsTable.getColumnModel().getColumn(5).setCellRenderer(new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                // Create a custom panel with rounded corners
                RoundedPanel panel = new RoundedPanel(10, Color.WHITE);
                panel.setLayout(new BorderLayout());
                
                JLabel label = new JLabel(value.toString());
                label.setHorizontalAlignment(SwingConstants.CENTER);
                label.setFont(new Font("SansSerif", Font.BOLD, 14));
                
                if ("APPROVED".equals(value)) {
                    panel.setBackground(new Color(230, 255, 243));
                    label.setForeground(SUCCESS_COLOR);
                } else if ("PENDING".equals(value)) {
                    panel.setBackground(new Color(255, 250, 230));
                    label.setForeground(WARNING_COLOR);
                } else {
                    panel.setBackground(new Color(255, 236, 236));
                    label.setForeground(DANGER_COLOR);
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
        
        // Custom renderer for amount column to right-align and format currency
        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(new DefaultTableCellRenderer() {
            private final DecimalFormat formatter = new DecimalFormat("$#,##0.00");
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                label.setHorizontalAlignment(SwingConstants.RIGHT);
                label.setFont(new Font("SansSerif", Font.BOLD, 14));
                
                // Format amount as currency
                if (value != null) {
                    try {
                        double amount = Double.parseDouble(value.toString());
                        // Get transaction type to color-code amounts
                        String type = (String)table.getValueAt(row, 1);
                        if (type.equals("Withdrawal") || 
                            type.contains("Transfer") && 
                            !type.contains("from")) {
                            // Negative for withdrawals and outgoing transfers
                            label.setText(formatter.format(-amount));
                            label.setForeground(DANGER_COLOR);
                        } else {
                            // Positive for deposits and incoming transfers
                            label.setText(formatter.format(amount));
                            label.setForeground(SUCCESS_COLOR);
                        }
                    } catch (Exception e) {
                        // Keep original text if parsing fails
                    }
                }
                
                return label;
            }
        });
        
        // Custom date format renderer
        transactionsTable.getColumnModel().getColumn(3).setCellRenderer(new DefaultTableCellRenderer() {
            private final SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Try to parse and reformat the date for better display
                if (value != null && !value.equals("N/A")) {
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = inputFormat.parse(value.toString());
                        label.setText(displayFormat.format(date));
                    } catch (Exception e) {
                        // Keep original format if parsing fails
                    }
                }
                
                return label;
            }
        });
        
        // Same for approval date
        transactionsTable.getColumnModel().getColumn(6).setCellRenderer(new DefaultTableCellRenderer() {
            private final SimpleDateFormat displayFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = (JLabel) super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                
                // Try to parse and reformat the date for better display
                if (value != null && !value.equals("N/A")){
                    try {
                        SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        Date date = inputFormat.parse(value.toString());
                        label.setText(displayFormat.format(date));
                    } catch (Exception e) {
                        // Keep original format if parsing fails
                    }
                }
                
                return label;
            }
        });

        // Style table header
        JTableHeader header = transactionsTable.getTableHeader();
        header.setBackground(new Color(248, 250, 252));
        header.setForeground(TEXT_COLOR);
        header.setFont(new Font("SansSerif", Font.BOLD, 14));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        // Add table components to table panel
        tablePanel.add(transactionsLabel, BorderLayout.NORTH);
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        // Filter options panel at the bottom of the table
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setOpaque(false);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel filterLabel = new JLabel("Filter by: ");
        filterLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        filterPanel.add(filterLabel);
        
        // Create a ButtonGroup to ensure only one filter can be selected
        javax.swing.ButtonGroup filterGroup = new javax.swing.ButtonGroup();
        String[] filterOptions = {"All", "Deposits", "Withdrawals", "Transfers", "Last 30 Days"};
        
        // Store the filter buttons to reference later
        java.util.Map<String, javax.swing.JToggleButton> filterButtons = new java.util.HashMap<>();
        
        for (String option : filterOptions) {
            // Use JToggleButton to create radio-button-like behavior
            javax.swing.JToggleButton filterButton = new javax.swing.JToggleButton(option);
            filterButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
            
            // Set the first option (All) as selected by default
            if (option.equals("All")) {
                filterButton.setSelected(true);
                filterButton.setBackground(SECONDARY_COLOR);
                filterButton.setForeground(Color.WHITE);
            } else {
                filterButton.setBackground(new Color(248, 250, 252));
                filterButton.setForeground(TEXT_COLOR);
            }
            
            filterButton.setBorder(BorderFactory.createLineBorder(option.equals("All") ? 
                SECONDARY_COLOR : new Color(235, 237, 239)));
            filterButton.setFocusPainted(false);
            
            // Add to button group to create radio button effect
            filterGroup.add(filterButton);
            
            // Add hover effects
            final String buttonText = option;
            filterButton.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    if (!filterButton.isSelected()) {
                        filterButton.setBackground(new Color(240, 242, 245));
                    }
                }
                
                @Override
                public void mouseExited(MouseEvent e) {
                    if (filterButton.isSelected()) {
                        filterButton.setBackground(SECONDARY_COLOR);
                    } else {
                        filterButton.setBackground(new Color(248, 250, 252));
                    }
                }
            });
            
            // Implement actual filtering functionality
            filterButton.addActionListener(e -> {
                // Update button appearance when selected
                for (javax.swing.JToggleButton btn : filterButtons.values()) {
                    if (btn.isSelected()) {
                        btn.setBackground(SECONDARY_COLOR);
                        btn.setForeground(Color.WHITE);
                    } else {
                        btn.setBackground(new Color(248, 250, 252));
                        btn.setForeground(TEXT_COLOR);
                    }
                }
                
                // Apply the actual filter
                applyTransactionFilter(buttonText);
            });
            
            // Store button reference
            filterButtons.put(option, filterButton);
            filterPanel.add(filterButton);
        }
        
        tablePanel.add(filterPanel, BorderLayout.SOUTH);
        
        // Create content panel to hold all components
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Add account panel
        contentPanel.add(accountPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        contentPanel.add(tablePanel);
        
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
     * Create info card for account information
     */
    private JPanel createInfoCard(String title, String value, Color accentColor) {
        RoundedPanel card = new RoundedPanel(10, Color.WHITE);
        card.setLayout(new BorderLayout(0, 10));
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 0, 3, accentColor),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        valueLabel.setForeground(TEXT_COLOR);
        
        card.add(titleLabel, BorderLayout.NORTH);
        card.add(valueLabel, BorderLayout.CENTER);
        
        // Add shadow effect
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createEmptyBorder(0, 0, 5, 5),
            BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 0, 3, accentColor),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            )
        ));
        
        return card;
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
     * Apply transaction filter based on selection
     * @param filterType The type of filter to apply
     */
    private void applyTransactionFilter(String filterType) {
        // Clear existing data
        tableModel.setRowCount(0);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Base query - will be modified based on filter
            String query = "SELECT * FROM transactions WHERE account_id = ?";
            
            // Add filter conditions
            switch (filterType) {
                case "Deposits":
                    query += " AND (transaction_type = 'Deposit' OR transaction_type LIKE '%Transfer from%')";
                    break;
                case "Withdrawals":
                    query += " AND transaction_type = 'Withdrawal'";
                    break;
                case "Transfers":
                    query += " AND transaction_type LIKE '%Transfer%'";
                    break;
                case "Last 30 Days":
                    query += " AND transaction_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
                    break;
                case "All":
                default:
                    // No additional filter for "All"
                    break;
            }
            
            // Add sorting to show newest transactions first
            query += " ORDER BY transaction_date DESC";
            
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
            
            // If no transactions match the filter, display a message
            if (tableModel.getRowCount() == 0) {
                String message = "No " + (filterType.equals("All") ? "" : filterType.toLowerCase() + " ") +
                                "transactions found for this account";
                tableModel.addRow(new Object[]{message, "", "", "", "", "", ""});
            }
            
            // Show a success message for the filter
            showFilterAppliedMessage("Showing " + filterType + " transactions");
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error applying filter: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    /**
     * Shows a stylized filter applied message
     */
    private void showFilterAppliedMessage(String message) {
        // Create a temporary message panel
        JPanel messagePanel = new RoundedPanel(15, SECONDARY_COLOR);
        messagePanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.X_AXIS));
        
        JLabel filterIcon = new JLabel("🔍");
        filterIcon.setFont(new Font("SansSerif", Font.BOLD, 18));
        filterIcon.setForeground(Color.WHITE);
        
        JLabel messageLabel = new JLabel(" " + message);
        messageLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        messageLabel.setForeground(Color.WHITE);
        
        messagePanel.add(filterIcon);
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
            javax.swing.Timer timer = new javax.swing.Timer(2000, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    frame.getLayeredPane().remove(messagePanel);
                    frame.repaint();
                }
            });
            timer.setRepeats(false);
            timer.start();
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
            case "Approval Queue":
                SwingUtilities.invokeLater(() -> new ApproveTransaction(adminId).setVisible(true));
                break;
            case "Reports":
                SwingUtilities.invokeLater(() -> new Report().setVisible(true));
                break;
            case "Audit Logs":
                // For now, just show a message and return to dashboard
                JOptionPane.showMessageDialog(null, 
                    "Audit Logs screen is under development.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
                break;
            default:
                SwingUtilities.invokeLater(() -> new CustomerAccounts(adminId).setVisible(true));
        }
    }
   
/**
 * Export the current transaction data to a PDF file
 * Uses the same style and approach as ManageTransaction class
 */
private void exportTransactionsToPDF() {
    // Check if there's data to export
    if (tableModel.getRowCount() == 0 || 
        (tableModel.getRowCount() == 1 && tableModel.getValueAt(0, 0).toString().contains("No"))) {
        JOptionPane.showMessageDialog(this,
            "No data available to export.",
            "Export Error",
            JOptionPane.WARNING_MESSAGE);
        return;
    }
    
    // Create file chooser dialog
    JFileChooser fileChooser = new JFileChooser();
    fileChooser.setDialogTitle("Save PDF File");
    
    // Set default filename with timestamp and account ID
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
    String defaultFileName = "account_" + accountId + "_transactions_" + dateFormat.format(new Date()) + ".pdf";
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
                "Report generated on: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()) + 
                " by " + adminName, 
                smallFont);
            timestamp.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
            timestamp.setSpacingAfter(20);
            document.add(timestamp);
            
            // Add account information section
            com.itextpdf.text.Font sectionFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 12, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Paragraph accountSection = new com.itextpdf.text.Paragraph("Account Information", sectionFont);
            accountSection.setSpacingAfter(10);
            document.add(accountSection);
            
            // Create table for account information
            com.itextpdf.text.pdf.PdfPTable accountTable = new com.itextpdf.text.pdf.PdfPTable(2);
            accountTable.setWidthPercentage(100);
            accountTable.setSpacingAfter(20);
            
            // Add account information
            com.itextpdf.text.Font labelFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font valueFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);
            
            addAccountTableRow(accountTable, "Account Holder:", customerName, labelFont, valueFont);
            addAccountTableRow(accountTable, "Account ID:", String.valueOf(accountId), labelFont, valueFont);
            addAccountTableRow(accountTable, "Account Type:", accountType, labelFont, valueFont);
            addAccountTableRow(accountTable, "Current Balance:", new DecimalFormat("$#,##0.00").format(accountBalance), labelFont, valueFont);
            
            document.add(accountTable);
            
            // Add transactions section
            com.itextpdf.text.Paragraph transactionsSection = new com.itextpdf.text.Paragraph("Transaction History", sectionFont);
            transactionsSection.setSpacingAfter(10);
            document.add(transactionsSection);
            
            // Create table for transactions
            com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(tableModel.getColumnCount());
            pdfTable.setWidthPercentage(100);
            
            // Set column widths (percentages)
            float[] columnWidths = {1.0f, 1.0f, 1.2f, 1.5f, 2.5f, 1.2f, 1.5f};
            pdfTable.setWidths(columnWidths);
            
            // Define fonts for headers and data
            com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9);
            
            // Add table headers
            String[] columnNames = {"TRANSACTION ID", "TYPE", "AMOUNT", "DATE", "DESCRIPTION", "STATUS", "APPROVAL DATE"};
            for (String columnName : columnNames) {
                com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                    new com.itextpdf.text.Phrase(columnName, headerFont));
                cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                cell.setBackgroundColor(new com.itextpdf.text.BaseColor(20, 30, 70)); // PRIMARY_COLOR
                cell.setPadding(5);
                cell.setVerticalAlignment(com.itextpdf.text.Element.ALIGN_MIDDLE);
                // Set text color to white for readability against dark background
                cell.setPhrase(new com.itextpdf.text.Phrase(columnName, 
                    new com.itextpdf.text.Font(headerFont.getFamily(), headerFont.getSize(), 
                    headerFont.getStyle(), com.itextpdf.text.BaseColor.WHITE)));
                pdfTable.addCell(cell);
            }
            
            // Add data rows
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                // Skip "No transactions found" row
                if (tableModel.getValueAt(row, 0) instanceof String && 
                    ((String)tableModel.getValueAt(row, 0)).contains("No")) {
                    continue;
                }
                
                // Add transaction data to PDF table
                // Set alternating row colors for better readability
                com.itextpdf.text.BaseColor rowColor = (row % 2 == 0) 
                    ? new com.itextpdf.text.BaseColor(245, 247, 250) // Light gray
                    : new com.itextpdf.text.BaseColor(255, 255, 255); // White
                
                for (int col = 0; col < tableModel.getColumnCount(); col++) {
                    // Get value from table model
                    Object value = tableModel.getValueAt(row, col);
                    String cellText = value != null ? value.toString() : "";
                    
                    // Create cell with data
                    com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell();
                    cell.setBackgroundColor(rowColor);
                    cell.setPadding(5);
                    
                    // Format amount column
                    if (col == 2) { // Amount column
                        String type = tableModel.getValueAt(row, 1).toString();
                        double amount = 0;
                        try {
                            // Remove currency symbols for parsing
                            amount = Double.parseDouble(cellText.replace("$", "").replace(",", ""));
                        } catch (Exception e) {
                            // Use 0 if parsing fails
                        }
                        
                        // Set color based on transaction type
                        com.itextpdf.text.BaseColor amountColor;
                        if (type.equals("Withdrawal") || (type.contains("Transfer") && !type.contains("from"))) {
                            // Negative amount for withdrawals and outgoing transfers
                            cellText = new DecimalFormat("$#,##0.00").format(-Math.abs(amount));
                            amountColor = new com.itextpdf.text.BaseColor(220, 53, 69); // Red
                        } else {
                            // Positive for deposits and incoming transfers
                            cellText = new DecimalFormat("$#,##0.00").format(Math.abs(amount));
                            amountColor = new com.itextpdf.text.BaseColor(25, 135, 84); // Green
                        }
                        
                        // Create font with color
                        com.itextpdf.text.Font amountFont = new com.itextpdf.text.Font(dataFont);
                        amountFont.setColor(amountColor);
                        
                        cell.setPhrase(new com.itextpdf.text.Phrase(cellText, amountFont));
                        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                    }
                    // Format date columns
                    else if (col == 3 || col == 6) { // Date columns
                        if (!cellText.equals("N/A")) {
                            try {
                                SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                                Date date = inputFormat.parse(cellText);
                                cellText = new SimpleDateFormat("MMM dd, yyyy HH:mm").format(date);
                            } catch (Exception e) {
                                // Keep original if parsing fails
                            }
                        }
                        cell.setPhrase(new com.itextpdf.text.Phrase(cellText, dataFont));
                        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    }
                    // Format status column
                    else if (col == 5) { // Status column
                        com.itextpdf.text.Font statusFont = new com.itextpdf.text.Font(dataFont);
                        com.itextpdf.text.BaseColor statusColor;
                        
                        if ("APPROVED".equals(cellText)) {
                            statusColor = new com.itextpdf.text.BaseColor(25, 135, 84); // Green
                        } else if ("PENDING".equals(cellText)) {
                            statusColor = new com.itextpdf.text.BaseColor(255, 193, 7); // Yellow
                        } else {
                            statusColor = new com.itextpdf.text.BaseColor(220, 53, 69); // Red
                        }
                        
                        statusFont.setColor(statusColor);
                        cell.setPhrase(new com.itextpdf.text.Phrase(cellText, statusFont));
                        cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    }
                    // Default formatting for other columns
                    else {
                        cell.setPhrase(new com.itextpdf.text.Phrase(cellText, dataFont));
                        cell.setHorizontalAlignment(col == 0 ? com.itextpdf.text.Element.ALIGN_CENTER : 
                                                   com.itextpdf.text.Element.ALIGN_LEFT);
                    }
                    
                    pdfTable.addCell(cell);
                }
            }
            
            document.add(pdfTable);
            
            // Add transaction summary
            com.itextpdf.text.Paragraph summarySection = new com.itextpdf.text.Paragraph("Transaction Summary", sectionFont);
            summarySection.setSpacingBefore(20);
            summarySection.setSpacingAfter(10);
            document.add(summarySection);
            
            // Calculate summary statistics
            int totalTransactions = tableModel.getRowCount();
            double totalDeposits = 0;
            double totalWithdrawals = 0;
            
            for (int row = 0; row < tableModel.getRowCount(); row++) {
                // Skip "No transactions found" row
                if (tableModel.getValueAt(row, 0) instanceof String && 
                    ((String)tableModel.getValueAt(row, 0)).contains("No")) {
                    continue;
                }
                
                try {
                    String type = tableModel.getValueAt(row, 1).toString();
                    String amountStr = tableModel.getValueAt(row, 2).toString();
                    
                    // Parse amount (remove currency symbols)
                    double amount = Double.parseDouble(amountStr.replace("$", "")
                                                               .replace(",", "")
                                                               .replace("+", "")
                                                               .replace("-", ""));
                    
                    if (type.equals("Deposit") || type.contains("Transfer from")) {
                        totalDeposits += amount;
                    } else {
                        totalWithdrawals += amount;
                    }
                } catch (Exception e) {
                    // Skip if parsing fails
                }
            }
            
            // Create summary table
            com.itextpdf.text.pdf.PdfPTable summaryTable = new com.itextpdf.text.pdf.PdfPTable(2);
            summaryTable.setWidthPercentage(60);
            summaryTable.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
            summaryTable.setSpacingAfter(20);
            
            // Set column widths for summary table
            float[] summaryWidths = {1.5f, 1.0f};
            summaryTable.setWidths(summaryWidths);
            
            // Add summary rows
            com.itextpdf.text.Font summaryLabelFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD);
            com.itextpdf.text.Font summaryValueFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10);
            com.itextpdf.text.Font depositFont = new com.itextpdf.text.Font(summaryValueFont);
            depositFont.setColor(new com.itextpdf.text.BaseColor(25, 135, 84)); // Green
            com.itextpdf.text.Font withdrawalFont = new com.itextpdf.text.Font(summaryValueFont);
            withdrawalFont.setColor(new com.itextpdf.text.BaseColor(220, 53, 69)); // Red
            
            DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
            
            addAccountTableRow(summaryTable, "Total Transactions:", String.valueOf(totalTransactions), summaryLabelFont, summaryValueFont);
            addAccountTableRow(summaryTable, "Total Deposits:", currencyFormat.format(totalDeposits), summaryLabelFont, depositFont);
            addAccountTableRow(summaryTable, "Total Withdrawals:", currencyFormat.format(totalWithdrawals), summaryLabelFont, withdrawalFont);
            addAccountTableRow(summaryTable, "Net Change:", currencyFormat.format(totalDeposits - totalWithdrawals), summaryLabelFont, summaryValueFont);
            
            document.add(summaryTable);
            
            // Add footer
            document.add(new com.itextpdf.text.Paragraph("\n"));
            com.itextpdf.text.Paragraph footer = new com.itextpdf.text.Paragraph(
                "This is an official report from Kurdish-O-Banking. For any inquiries, please contact customer service.", 
                smallFont);
            footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
            document.add(footer);
            
            // Close document
            document.close();
            
            JOptionPane.showMessageDialog(this,
                "Transaction data successfully exported to PDF:\n" + fileToSave.getAbsolutePath(),
                "Export Successful",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Ask if user wants to open the PDF file
            int openOption = JOptionPane.showConfirmDialog(
                this,
                "Would you like to open the PDF file now?",
                "Open PDF",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (openOption == JOptionPane.YES_OPTION) {
                // Open the PDF file using the default application
                try {
                    java.awt.Desktop.getDesktop().open(fileToSave);
                } catch (Exception e) {
                    JOptionPane.showMessageDialog(
                        this,
                        "Could not open the PDF file. It has been saved to:\n" + fileToSave.getAbsolutePath(),
                        "Open File Error",
                        JOptionPane.ERROR_MESSAGE
                    );
                }
            }
            
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this,
                "Error exporting data to PDF: " + e.getMessage(),
                "Export Error",
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }}
    }

/**
 * Helper method to add a row to the account information table
 */
private void addAccountTableRow(com.itextpdf.text.pdf.PdfPTable table, String label, String value, 
                               com.itextpdf.text.Font labelFont, com.itextpdf.text.Font valueFont) {
    com.itextpdf.text.pdf.PdfPCell labelCell = new com.itextpdf.text.pdf.PdfPCell(
        new com.itextpdf.text.Phrase(label, labelFont));
    labelCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
    labelCell.setPadding(5);
    labelCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
    table.addCell(labelCell);
    
    com.itextpdf.text.pdf.PdfPCell valueCell = new com.itextpdf.text.pdf.PdfPCell(
        new com.itextpdf.text.Phrase(value, valueFont));
    valueCell.setBorder(com.itextpdf.text.Rectangle.NO_BORDER);
    valueCell.setPadding(5);
    valueCell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_LEFT);
    table.addCell(valueCell);
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
                // Set system look and feel
                javax.swing.UIManager.setLookAndFeel(
                    javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                e.printStackTrace();
            }
            
            // For testing only - normally would be called from CustomerAccounts
            new CustomerTransactions(10, 1);
        });
    }
}