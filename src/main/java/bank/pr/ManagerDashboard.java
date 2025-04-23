package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class ManagerDashboard extends JFrame {
    
    // Dashboard metrics
    private int pendingApprovals = 0;
    private int flaggedTransactions = 0;
    private int highRiskAccounts = 0;
    private int newCustomers = 0;
    
    // Admin information
    private String adminName = "";
    private String adminLastLogin = "";
    
    // UI Components that need to be updated
    private JLabel pendingApprovalsLabel;
    private JLabel flaggedTransactionsLabel;
    private JLabel highRiskAccountsLabel;
    private JLabel newCustomersLabel;
    private JLabel subHeaderLabel;
    
    // Admin ID for logged in admin
    private int adminId = 0;

    public ManagerDashboard() {
        initialize();
    }
    
    public ManagerDashboard(int adminId) {
        this.adminId = adminId;
        initialize();
    }
    
    private void initialize() {
        setTitle("KOB Manager Dashboard");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Load admin information
        loadAdminInfo();
        
        // Load metrics from database
        loadMetricsFromDatabase();

        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        JPanel mainContentPanel = createMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
        
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
                query = "SELECT first_name, last_name, last_login FROM admin WHERE admin_id = ?";
                PreparedStatement stmt = conn.prepareStatement(query);
                stmt.setInt(1, adminId);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    adminName = rs.getString("first_name") + " " + rs.getString("last_name");
                    
                    // Format the last login time if it exists
                    java.sql.Timestamp lastLogin = rs.getTimestamp("last_login");
                    if (lastLogin != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm a");
                        adminLastLogin = dateFormat.format(new Date(lastLogin.getTime()));
                    } else {
                        adminLastLogin = "First login";
                    }
                }
            } else {
                // If no specific admin ID, get the first admin in the database (fallback)
                query = "SELECT first_name, last_name, last_login FROM admin LIMIT 1";
                PreparedStatement stmt = conn.prepareStatement(query);
                ResultSet rs = stmt.executeQuery();
                
                if (rs.next()) {
                    adminName = rs.getString("first_name") + " " + rs.getString("last_name");
                    
                    // Format the last login time if it exists
                    java.sql.Timestamp lastLogin = rs.getTimestamp("last_login");
                    if (lastLogin != null) {
                        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd yyyy hh:mm a");
                        adminLastLogin = dateFormat.format(new Date(lastLogin.getTime()));
                    } else {
                        adminLastLogin = "First login";
                    }
                } else {
                    // If no admin found at all, use default values
                    adminName = "Administrator";
                    adminLastLogin = "Today";
                }
            }
        } catch (SQLException e) {
            System.err.println("Error loading admin information: " + e.getMessage());
            adminName = "Administrator";
            adminLastLogin = "Today";
        }
    }
    
    /**
show up info     */
    private void loadMetricsFromDatabase() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Get pending approvals count (transactions with PENDING status)
            String pendingApprovalsQuery = "SELECT COUNT(*) FROM transactions WHERE status = 'PENDING'";
            try (PreparedStatement stmt = conn.prepareStatement(pendingApprovalsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    pendingApprovals = rs.getInt(1);
                }
            }
            
            // Get flagged transactions count (any transactions that need attention)
            // Looking for either FLAGGED status or large amounts (over $1000) as potential flags
            String flaggedTransactionsQuery = "SELECT COUNT(*) FROM transactions WHERE status = 'FLAGGED' OR amount > 1000";
            try (PreparedStatement stmt = conn.prepareStatement(flaggedTransactionsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    flaggedTransactions = rs.getInt(1);
                }
            }
            
            // Get high-risk accounts count (accounts with balance > 10000)
            String highRiskAccountsQuery = "SELECT COUNT(*) FROM accounts WHERE balance > 10000";
            try (PreparedStatement stmt = conn.prepareStatement(highRiskAccountsQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    highRiskAccounts = rs.getInt(1);
                }
            }
            
            // Get new customers count (accounts created in the last 7 days)
            String newCustomersQuery = "SELECT COUNT(*) FROM accounts WHERE created_at >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
            try (PreparedStatement stmt = conn.prepareStatement(newCustomersQuery);
                 ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    newCustomers = rs.getInt(1);
                }
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Error loading metrics: " + e.getMessage(), 
                                         "Database Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(26, 26, 26));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(240, 800));
        sidebarPanel.setLayout(null);

        JLabel titleLabel = new JLabel("KOB Manager");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 150, 30);
        sidebarPanel.add(titleLabel);

        RoundedPanel dashboardBtn = new RoundedPanel(8);
        dashboardBtn.setBackground(new Color(51, 51, 51));
        dashboardBtn.setBounds(20, 120, 200, 40);
        dashboardBtn.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        dashboardBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Add action listener to dashboard button
        dashboardBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // Already on dashboard, no need to navigate
            }
        });

        JLabel dashboardLabel = new JLabel("Dashboard");
        dashboardLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dashboardLabel.setForeground(Color.WHITE);
        dashboardBtn.add(dashboardLabel);
        sidebarPanel.add(dashboardBtn);

        String[] menuItems = {
            "Customer Accounts", 
            "Transaction Oversight", 
            "Approval Queue", 
            "Reports", 
            "Audit Logs"
        };

        int yPos = 200;
        for (String item : menuItems) {
            JLabel menuLabel = new JLabel(item);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setBounds(40, yPos, 200, 20);
            menuLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            menuLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    navigateToScreen(item);
                }

                public void mouseEntered(MouseEvent evt) {
                    menuLabel.setForeground(new Color(200, 200, 200));
                }

                public void mouseExited(MouseEvent evt) {
                    menuLabel.setForeground(Color.WHITE);
                }
            });
            sidebarPanel.add(menuLabel);
            yPos += 40;
        }

        return sidebarPanel;
    }
    
    /**
     * Handles navigation to different screens
     */
   // In ManagerDashboard.java, update the navigateToScreen method
private void navigateToScreen(String screenName) {
    dispose(); // Close the current window
    
    switch (screenName) {
        case "Customer Accounts":
            SwingUtilities.invokeLater(() -> new CustomerAccounts().setVisible(true));
            break;
        case "Transaction Oversight":
            SwingUtilities.invokeLater(() -> new ManageTransaction().setVisible(true));
            break;
        case "Reports":
            SwingUtilities.invokeLater(() -> new Report().setVisible(true));
            break;
        case "Approval Queue":
            // Instead of showing a message, navigate to ApproveTransaction
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

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(249, 249, 249));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        RoundedPanel contentPanel = new RoundedPanel(16);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(20, 20, 720, 740);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        contentPanel.setLayout(null);
        mainPanel.add(contentPanel);

        JLabel headerLabel = new JLabel("Manager Dashboard");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(51, 51, 51));
        headerLabel.setBounds(60, 50, 250, 30);
        contentPanel.add(headerLabel);

        // Use the admin name from database if available
        subHeaderLabel = new JLabel("Hello, " + adminName + " | Last login: " + adminLastLogin);
        subHeaderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subHeaderLabel.setForeground(new Color(102, 102, 102));
        subHeaderLabel.setBounds(60, 80, 400, 20);
        contentPanel.add(subHeaderLabel);

        JLabel overviewLabel = new JLabel("TODAY'S OVERVIEW");
        overviewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        overviewLabel.setForeground(new Color(102, 102, 102));
        overviewLabel.setBounds(60, 130, 150, 15);
        contentPanel.add(overviewLabel);

        // Create metric cards with data from database
        pendingApprovalsLabel = new JLabel(String.valueOf(pendingApprovals));
        flaggedTransactionsLabel = new JLabel(String.valueOf(flaggedTransactions));
        highRiskAccountsLabel = new JLabel(String.valueOf(highRiskAccounts));
        newCustomersLabel = new JLabel(String.valueOf(newCustomers));
        
        MetricCard[] metricCards = {
            new MetricCard("Pending Approvals", pendingApprovalsLabel, new Color(0, 123, 255), new Color(230, 247, 255)),
            new MetricCard("Flagged Transactions", flaggedTransactionsLabel, new Color(40, 167, 69), new Color(230, 255, 234)),
            new MetricCard("High-Risk Accounts", highRiskAccountsLabel, new Color(220, 53, 69), new Color(255, 230, 230)),
            new MetricCard("New Customers", newCustomersLabel, new Color(111, 66, 193), new Color(230, 230, 255))
        };

        int xPos = 60;
        for (MetricCard card : metricCards) {
            card.setBounds(xPos, 150, 130, 80);
            contentPanel.add(card);
            xPos += 150;
        }

        JLabel toolsLabel = new JLabel("Management Tools");
        toolsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        toolsLabel.setForeground(new Color(51, 51, 51));
        toolsLabel.setBounds(60, 270, 200, 20);
        contentPanel.add(toolsLabel);

        ToolCard[] toolCards = {
            new ToolCard("View Customer Accounts", "Search and manage all customer accounts", 
                        "Access", new Color(0, 123, 255), new Color(230, 247, 255)),
            new ToolCard("Manage Transactions", "Review and modify transaction records", 
                        "Access", new Color(40, 167, 69), new Color(230, 255, 234)),
            new ToolCard("Approve Transactions", "Authorize pending transactions", 
                        "Review", new Color(220, 53, 69), new Color(255, 230, 230)),
            new ToolCard("Generate Reports", "Create financial and audit reports", 
                        "Generate", new Color(111, 66, 193), new Color(230, 230, 255))
        };

        toolCards[0].setBounds(60, 310, 240, 140);
        toolCards[1].setBounds(340, 310, 240, 140);
        toolCards[2].setBounds(60, 470, 240, 140);
        toolCards[3].setBounds(340, 470, 240, 140);

        // Add action listeners to tool cards
        toolCards[0].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToScreen("Customer Accounts");
            }
        });
        
        toolCards[1].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToScreen("Transaction Oversight");
            }
        });
        
        toolCards[2].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToScreen("Approval Queue");
            }
        });
        
        toolCards[3].addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                navigateToScreen("Reports");
            }
        });

        for (ToolCard card : toolCards) {
            contentPanel.add(card);
        }

        RoundedPanel logoutBtn = new RoundedPanel(8);
        logoutBtn.setBackground(new Color(255, 230, 230));
        logoutBtn.setBounds(60, 660, 120, 40);
        logoutBtn.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        logoutBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Add action to logout button
        logoutBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int response = JOptionPane.showConfirmDialog(
                    ManagerDashboard.this,
                    "Are you sure you want to logout?",
                    "Confirm Logout",
                    JOptionPane.YES_NO_OPTION
                );
                
                if (response == JOptionPane.YES_OPTION) {
                    dispose();
                    // Show login screen
                    SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
                }
            }
        });

        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutLabel.setForeground(new Color(220, 53, 69));
        logoutBtn.add(logoutLabel);
        contentPanel.add(logoutBtn);

        // Add refresh button
        RoundedPanel refreshBtn = new RoundedPanel(8);
        refreshBtn.setBackground(new Color(230, 247, 255));
        refreshBtn.setBounds(190, 660, 120, 40);
        refreshBtn.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));
        refreshBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        refreshBtn.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                loadMetricsFromDatabase();
                updateMetricLabels();
                JOptionPane.showMessageDialog(
                    ManagerDashboard.this,
                    "Dashboard data refreshed successfully.",
                    "Refresh Complete",
                    JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        JLabel refreshLabel = new JLabel("Refresh");
        refreshLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshLabel.setForeground(new Color(0, 123, 255));
        refreshBtn.add(refreshLabel);
        contentPanel.add(refreshBtn);

        return mainPanel;
    }
    
    /**
     * Updates the metric labels with the latest data
     */
    private void updateMetricLabels() {
        pendingApprovalsLabel.setText(String.valueOf(pendingApprovals));
        flaggedTransactionsLabel.setText(String.valueOf(flaggedTransactions));
        highRiskAccountsLabel.setText(String.valueOf(highRiskAccounts));
        newCustomersLabel.setText(String.valueOf(newCustomers));
    }

    static class MetricCard extends RoundedPanel {
        public MetricCard(String title, JLabel valueLabel, Color textColor, Color bgColor) {
            super(8);
            setBackground(bgColor);
            setLayout(null);
            
            // Make the card clickable
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(textColor);
            titleLabel.setBounds(10, 10, 120, 15);
            add(titleLabel);

            valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
            valueLabel.setForeground(textColor);
            valueLabel.setBounds(50, 30, 60, 40);
            add(valueLabel);
        }
    }

    static class ToolCard extends RoundedPanel {
        public ToolCard(String title, String description, String buttonText, Color textColor, Color bgColor) {
            super(8);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
            setLayout(null);
            
            // Make the card clickable
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            RoundedPanel icon = new RoundedPanel(8);
            icon.setBackground(bgColor);
            icon.setBounds(20, 30, 40, 40);
            add(icon);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(textColor);
            titleLabel.setBounds(70, 30, 200, 20);
            add(titleLabel);

            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            descLabel.setForeground(new Color(102, 102, 102));
            descLabel.setBounds(20, 80, 200, 15);
            add(descLabel);

            RoundedPanel button = new RoundedPanel(8);
            button.setBackground(bgColor);
            button.setBounds(20, 110, 80, 30);
            button.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));
            button.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

            JLabel buttonLabel = new JLabel(buttonText);
            buttonLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            buttonLabel.setForeground(textColor);
            button.add(buttonLabel);
            add(button);
        }
    }

    static class RoundedPanel extends JPanel {
        private final int cornerRadius;

        public RoundedPanel(int cornerRadius) {
            super();
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.setColor(getBackground());
            g2.fill(round);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Test database connection
                Connection conn = DatabaseConnection.getConnection();
                conn.close();
                
                // If connection successful, show dashboard
                new ManagerDashboard().setVisible(true);
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, 
                    "Cannot connect to database: " + e.getMessage(), 
                    "Database Error", JOptionPane.ERROR_MESSAGE);
            }
        });
    }
}