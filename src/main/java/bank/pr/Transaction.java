package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;


public class Transaction extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color SUCCESS_COLOR = new Color(40, 167, 69);
    private static final Color ERROR_COLOR = new Color(220, 53, 69);
    
    // Components
    private int userId;
    private String userName;
    private JLabel greeting;
    private List<JButton> menuButtons = new ArrayList<>();
    private JPanel sidebarPanel;
    private JPanel mainContentPanel;
    private ArrayList<TransactionItem> currentTransactions;
    
    // Transaction data model
    private class TransactionItem {
        Date date;
        String description;
        String category;
        double amount;
        double balance;

        public TransactionItem(Date date, String description, String category, double amount, double balance) {
            this.date = date;
            this.description = description;
            this.category = category;
            this.amount = amount;
            this.balance = balance;
        }
    }

    public Transaction() {
        this(-1, "User"); // Default constructor with placeholder values
    }

    public Transaction(int userId, String userName) {
        this.userId = userId;
        this.userName = userName;
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Kurdish-O-Banking (KOB) - Transactions");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create components
        createSidebar();
        createHeaderPanel();
        createMainContent();
        
        // Set icon if available
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
    }
    
    // Add this method to allow setting user info after initialization
    public void setUserInfo(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        
        // Update window title and greeting
        setTitle("Kurdish-O-Banking (KOB) - Transactions - " + userName);
        if (greeting != null) {
            greeting.setText("Transaction History - " + userName);
        }
        
        // Refresh transaction data
        refreshTransactionData();
    }
    
    // Method to refresh transaction data based on user info
    private void refreshTransactionData() {
        // Remove old content
        if (mainContentPanel != null) {
            getContentPane().remove(mainContentPanel);
        }
        
        // Create and add new content panel with updated data
        createMainContent();
        
        // Refresh UI
        revalidate();
        repaint();
    }
    
    private void createSidebar() {
        sidebarPanel = new JPanel() {
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
        
        sidebarPanel.setPreferredSize(new Dimension(240, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        
        // Logo panel
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBorder(BorderFactory.createEmptyBorder(25, 15, 25, 15));
        
        JLabel bankName = new JLabel("Kurdish-O-Banking");
        bankName.setForeground(Color.WHITE);
        bankName.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        JLabel tagline = new JLabel("Your Future, Your Bank");
        tagline.setForeground(new Color(200, 200, 200));
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 12));
        
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.add(bankName);
        namePanel.add(Box.createVerticalStrut(3));
        namePanel.add(tagline);
        
        logoPanel.add(namePanel, BorderLayout.CENTER);
        
        // Try to add logo image if available
        try {
            ImageIcon logoIcon = new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png");
            java.awt.Image image = logoIcon.getImage().getScaledInstance(40, 40, java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(image);
            JLabel logoLabel = new JLabel(logoIcon);
            logoPanel.add(logoLabel, BorderLayout.WEST);
        } catch (Exception e) {
            System.err.println("Error loading small logo: " + e.getMessage());
        }
        
        sidebarPanel.add(logoPanel);
        
        // Add separator
        JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
        separator.setForeground(new Color(70, 80, 120));
        separator.setBackground(new Color(70, 80, 120));
        separator.setMaximumSize(new Dimension(220, 1));
        separator.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the separator
        sidebarPanel.add(separator);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Create a panel to hold the menu buttons and center them
        JPanel menuButtonsPanel = new JPanel();
        menuButtonsPanel.setLayout(new BoxLayout(menuButtonsPanel, BoxLayout.Y_AXIS));
        menuButtonsPanel.setOpaque(false);
        menuButtonsPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Menu items with icons
        String[] menuItems = {"Dashboard", "Balance", "Accounts", "Deposit", "Withdraw", "Transfers", "Transactions",  "QR Codes"};
        String[] iconNames = {"dashboard", "balance", "accounts", "deposit", "withdraw", "transfers", "transactions",  "qrcode"};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton button = createMenuButton(menuItems[i], iconNames[i]);
            
            final String item = menuItems[i];
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleButtonClick(item);
                    updateSelectedButton(item);
                }
            });
            
            // Center align the button
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Create a wrapper panel to center the button
            JPanel buttonWrapper = new JPanel();
            buttonWrapper.setLayout(new BoxLayout(buttonWrapper, BoxLayout.X_AXIS));
            buttonWrapper.setOpaque(false);
            buttonWrapper.setMaximumSize(new Dimension(220, 45));
            buttonWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add space before the button to center it
            buttonWrapper.add(Box.createHorizontalGlue());
            buttonWrapper.add(button);
            buttonWrapper.add(Box.createHorizontalGlue());
            
            menuButtonsPanel.add(buttonWrapper);
            menuButtonsPanel.add(Box.createVerticalStrut(5));
            menuButtons.add(button);
        }
        
        // Add the menu buttons panel to the sidebar
        sidebarPanel.add(menuButtonsPanel);
        
        // Set Transactions as initially selected
        updateSelectedButton("Transactions");
        
        // Add logout at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        JSeparator bottomSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        bottomSeparator.setForeground(new Color(70, 80, 120));
        bottomSeparator.setBackground(new Color(70, 80, 120));
        bottomSeparator.setMaximumSize(new Dimension(220, 1));
        bottomSeparator.setAlignmentX(Component.CENTER_ALIGNMENT); // Center the separator
        sidebarPanel.add(bottomSeparator);
        
        // Create logout button centered
        JButton logoutButton = createMenuButton("Logout", "logout");
        logoutButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    Transaction.this,
                    "Are you sure you want to logout?",
                    "Logout Confirmation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.QUESTION_MESSAGE
                );
                
                if (choice == JOptionPane.YES_OPTION) {
                    dispose();
                    // Open login page
                    SwingUtilities.invokeLater(new Runnable() {
                        @Override
                        public void run() {
                            LoginUI loginPage = new LoginUI();
                            loginPage.setVisible(true);
                        }
                    });
                }
            }
        });
        
        // Create wrapper for logout button to center it
        JPanel logoutWrapper = new JPanel();
        logoutWrapper.setLayout(new BoxLayout(logoutWrapper, BoxLayout.X_AXIS));
        logoutWrapper.setOpaque(false);
        logoutWrapper.setMaximumSize(new Dimension(220, 45));
        logoutWrapper.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        logoutWrapper.add(Box.createHorizontalGlue());
        logoutWrapper.add(logoutButton);
        logoutWrapper.add(Box.createHorizontalGlue());
        
        sidebarPanel.add(logoutWrapper);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private JButton createMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.CENTER); // Changed from LEFT to CENTER
        button.setIconTextGap(10);
        button.setMaximumSize(new Dimension(180, 45)); // Reduced width to center better
        button.setPreferredSize(new Dimension(180, 45)); // Reduced width to center better
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Equal padding on all sides
        
        // Try to load icon if available
        try {
            ImageIcon icon = new ImageIcon("icons/" + iconName + ".png");
            java.awt.Image image = icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            button.setIcon(icon);
        } catch (Exception e) {
            // If icon not found, use text only
            System.err.println("Icon not found: " + iconName);
        }
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setBackground(new Color(45, 55, 95));
                    button.setContentAreaFilled(true);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setContentAreaFilled(false);
                }
            }
        });
        
        return button;
    }
    
    // The rest of the code remains unchanged
    
    // All other methods remain as they were...
    
    private void updateSelectedButton(String selectedItem) {
        for (JButton button : menuButtons) {
            if (button.getText().equals(selectedItem)) {
                button.setBackground(SECONDARY_COLOR);
                button.setContentAreaFilled(true);
                button.setFont(new Font("SansSerif", Font.BOLD, 14));
                button.putClientProperty("selected", true);
            } else {
                button.setContentAreaFilled(false);
                button.setFont(new Font("SansSerif", Font.PLAIN, 14));
                button.putClientProperty("selected", false);
            }
        }
    }
    
    private void createHeaderPanel() {
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BorderLayout());
        headerPanel.setBackground(CARD_COLOR);
        headerPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 25, 15, 25)
        ));
        
        // Greeting panel
        JPanel greetingPanel = new JPanel(new BorderLayout());
        greetingPanel.setOpaque(false);
        
        greeting = new JLabel("Transaction History");
        greeting.setFont(new Font("SansSerif", Font.BOLD, 20));
        greeting.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel("View your account activity");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        greetingPanel.add(greeting, BorderLayout.NORTH);
        greetingPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(greetingPanel, BorderLayout.WEST);
        
        // Current date/time panel
        JPanel dateTimePanel = new JPanel(new BorderLayout());
        dateTimePanel.setOpaque(false);
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        
        JLabel dateLabel = new JLabel(now.format(dateFormatter));
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel timeLabel = new JLabel(now.format(timeFormatter));
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        timeLabel.setForeground(LIGHT_TEXT_COLOR);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        dateTimePanel.add(dateLabel, BorderLayout.NORTH);
        dateTimePanel.add(timeLabel, BorderLayout.SOUTH);
        
        headerPanel.add(dateTimePanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createMainContent() {
        mainContentPanel = new JPanel();
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        mainContentPanel.setLayout(new GridBagLayout());
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Transaction history panel
        JPanel transactionPanel = createSectionPanel("Recent Transactions");
        
        // Add account info at the top
        JPanel accountInfoPanel = new JPanel(new BorderLayout());
        accountInfoPanel.setOpaque(false);
        accountInfoPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 15, 15));
        
        JLabel accountLabel = new JLabel("Account: " + userName + " (ID: " + userId + ")");
        accountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        accountLabel.setForeground(TEXT_COLOR);
        
        accountInfoPanel.add(accountLabel, BorderLayout.WEST);
        
        // Add action buttons to the top right
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actionPanel.setOpaque(false);
        
        // Export to PDF button
        JButton exportButton = new JButton("Export PDF");
        exportButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        exportButton.setBackground(ACCENT_COLOR);
        exportButton.setForeground(Color.WHITE);
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportTransactionsToPDF());
        exportButton.setOpaque(true);  
        exportButton.setBorderPainted(false); 
        
        // Refresh button
        JButton refreshButton = new JButton("Refresh");
        refreshButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        refreshButton.setBackground(SECONDARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> refreshTransactionData());
        refreshButton.setOpaque(true);  
        refreshButton.setBorderPainted(false);
        
        actionPanel.add(exportButton);
        actionPanel.add(refreshButton);
        accountInfoPanel.add(actionPanel, BorderLayout.EAST);
        
        // Create transactions content
        JPanel transactionsContent = new JPanel();
        transactionsContent.setLayout(new BorderLayout());
        transactionsContent.setOpaque(false);
        
        // Create table header
        JPanel headerPanel = new JPanel(new GridLayout(1, 5, 10, 0));
        headerPanel.setBackground(new Color(248, 250, 252));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        String[] headers = {"DATE", "DESCRIPTION", "CATEGORY", "AMOUNT", "BALANCE"};
        for (String header : headers) {
            JLabel headerLabel = new JLabel(header);
            headerLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            headerLabel.setForeground(TEXT_COLOR);
            headerPanel.add(headerLabel);
        }
        
        // Load transactions from database
        currentTransactions = loadTransactionsFromDatabase();
        
        // Create panel for transaction rows
        JPanel rowsPanel = new JPanel();
        rowsPanel.setLayout(new BoxLayout(rowsPanel, BoxLayout.Y_AXIS));
        rowsPanel.setOpaque(false);
        
        // Display transactions
        displayTransactions(rowsPanel, currentTransactions);
        
        // Add components to transaction content
        transactionsContent.add(headerPanel, BorderLayout.NORTH);
        JScrollPane scrollPane = new JScrollPane(rowsPanel);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        transactionsContent.add(scrollPane, BorderLayout.CENTER);
        
        // Add all to transaction panel
        transactionPanel.add(accountInfoPanel, BorderLayout.NORTH);
        transactionPanel.add(transactionsContent, BorderLayout.CENTER);
        
        // Transaction stats panel
        JPanel statsPanel = createSectionPanel("Transaction Summary");
        statsPanel.setLayout(new BorderLayout());
        
        JPanel statsContent = new JPanel();
        statsContent.setLayout(new BoxLayout(statsContent, BoxLayout.Y_AXIS));
        statsContent.setOpaque(false);
        statsContent.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Add summary information
        addTransactionSummary(statsContent, currentTransactions);
        
        statsPanel.add(statsContent, BorderLayout.CENTER);
        
        // Add panels to main content with proper weights
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7;
        mainContentPanel.add(transactionPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        mainContentPanel.add(statsPanel, gbc);
        
        add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(5, 0, 10, 0)
        ));
        
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(CARD_COLOR);
        titleBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        panel.add(titleBar, BorderLayout.NORTH);
        
        return panel;
    }
    
    private ArrayList<TransactionItem> loadTransactionsFromDatabase() {
        ArrayList<TransactionItem> transactions = new ArrayList<>();
        
        // Check if user ID is valid before querying
        if (userId <= 0) {
            System.out.println("Warning: Invalid user ID: " + userId);
            transactions.add(new TransactionItem(new Date(), "Please log in to view transactions", "Info", 0.0, 0.0));
            return transactions;
        }
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Query to get transactions for the current user
            String query = "SELECT t.transaction_date, t.description, t.transaction_type as category, " +
                           "t.amount, a.balance " +
                           "FROM transactions t " +
                           "JOIN accounts a ON t.account_id = a.account_id " +
                           "WHERE a.username = ? OR a.account_id = ? " +
                           "ORDER BY t.transaction_date DESC";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userName);
            stmt.setInt(2, userId);
            
            System.out.println("Executing query for username: " + userName + ", userId: " + userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Date date = rs.getDate("transaction_date");
                String description = rs.getString("description");
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                double balance = rs.getDouble("balance");
                transactions.add(new TransactionItem(date, description, category, amount, balance));
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading transaction information: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();

            // Add some placeholder data if database connection fails
            transactions.add(new TransactionItem(new Date(), "Database connection error", "Error", 0.0, 0.0));
        }

        // If no transactions found, add a placeholder
        if (transactions.isEmpty()) {
            transactions.add(new TransactionItem(new Date(), "No transactions found", "Info", 0.0, 0.0));
        }
        return transactions;
    }
    
    private void displayTransactions(JPanel container, ArrayList<TransactionItem> transactions) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        
        for (int i = 0; i < transactions.size(); i++) {
            TransactionItem transaction = transactions.get(i);
            
            JPanel rowPanel = new JPanel(new GridLayout(1, 5, 10, 0));
            rowPanel.setBackground(i % 2 == 0 ? CARD_COLOR : new Color(248, 250, 252)); // Alternate row colors
            rowPanel.setBorder(BorderFactory.createEmptyBorder(12, 15, 12, 15));
            rowPanel.setMaximumSize(new Dimension(Short.MAX_VALUE, 40));
            rowPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
            
            // Format the transaction data
            String dateStr = dateFormat.format(transaction.date);
            String descriptionStr = transaction.description;
            String categoryStr = transaction.category;
            
            // Determine if this is a purchase transaction by category or description
            boolean isPurchase = "purchase".equalsIgnoreCase(categoryStr) || 
                                descriptionStr.toLowerCase().contains("purchase");
            
            // Format amount string and determine color
            Color amountColor;
            String amountStr;
            
            if (isPurchase) {
                // Format purchase transactions like withdrawals (negative amount)
                amountStr = String.format("-$%.2f", Math.abs(transaction.amount));
                amountColor = ERROR_COLOR; // Use the same color as withdrawals/negative transactions
            } else {
                // Format other transactions normally
                amountStr = (transaction.amount >= 0 ? "+" : "") + String.format("$%.2f", transaction.amount);
                amountColor = transaction.amount >= 0 ? SUCCESS_COLOR : ERROR_COLOR;
            }
            
            String balanceStr = String.format("$%.2f", transaction.balance);
            
            // Create labels for each cell
            JLabel dateLabel = new JLabel(dateStr);
            dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            dateLabel.setForeground(TEXT_COLOR);
            
            JLabel descLabel = new JLabel(descriptionStr);
            descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            descLabel.setForeground(TEXT_COLOR);
            
            JLabel categoryLabel = new JLabel(categoryStr);
            categoryLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            categoryLabel.setForeground(TEXT_COLOR);
            
            JLabel amountLabel = new JLabel(amountStr);
            amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            amountLabel.setForeground(amountColor);
            
            JLabel balanceLabel = new JLabel(balanceStr);
            balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
            balanceLabel.setForeground(TEXT_COLOR);
            
            // Add labels to row
            rowPanel.add(dateLabel);
            rowPanel.add(descLabel);
            rowPanel.add(categoryLabel);
            rowPanel.add(amountLabel);
            rowPanel.add(balanceLabel);
            
            // Add row to container
            container.add(rowPanel);
            
            // Add separator except for the last row
            if (i < transactions.size() - 1) {
                JSeparator separator = new JSeparator(SwingConstants.HORIZONTAL);
                separator.setForeground(new Color(240, 240, 240));
                separator.setMaximumSize(new Dimension(Short.MAX_VALUE, 1));
                separator.setAlignmentX(Component.LEFT_ALIGNMENT);
                container.add(separator);
            }
        }
    }
    private void addTransactionSummary(JPanel container, ArrayList<TransactionItem> transactions) {
        // Calculate summary statistics
        int totalTransactions = transactions.size();
        double totalDeposits = 0;
        double totalWithdrawals = 0;
        double netAmount = 0;
        
        for (TransactionItem transaction : transactions) {
            if (transaction.amount > 0) {
                totalDeposits += transaction.amount;
            } else if (transaction.amount < 0) {
                totalWithdrawals += Math.abs(transaction.amount);
            }
            netAmount += transaction.amount;
        }
        
        // Current balance - use the most recent transaction's balance
        double currentBalance = transactions.isEmpty() ? 0.0 : transactions.get(0).balance;
        
        // Create summary panels
        JPanel statsPanelLayout = new JPanel(new GridLayout(2, 2, 20, 20));
        statsPanelLayout.setOpaque(false);
        statsPanelLayout.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add summary items
        statsPanelLayout.add(createStatItem("Total Transactions", String.valueOf(totalTransactions), ""));
        statsPanelLayout.add(createStatItem("Current Balance", String.format("$%.2f", currentBalance), ""));
        statsPanelLayout.add(createStatItem("Total Deposits", String.format("$%.2f", totalDeposits), SUCCESS_COLOR));
        statsPanelLayout.add(createStatItem("Total Withdrawals", String.format("$%.2f", totalWithdrawals), ERROR_COLOR));
        
        container.add(statsPanelLayout);
    }
    
    private JPanel createStatItem(String title, String value, Object colorOrIcon) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(0, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        
        // Set color based on the type
        if (colorOrIcon instanceof Color) {
            valueLabel.setForeground((Color) colorOrIcon);
        } else {
            valueLabel.setForeground(TEXT_COLOR);
        }
        
        panel.add(titleLabel, BorderLayout.NORTH);
        panel.add(valueLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void exportTransactionsToPDF() {
        if (currentTransactions == null || currentTransactions.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "No transactions to export.", 
                "Export Error", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Ask user to choose file location
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Transactions as PDF");
        fileChooser.setSelectedFile(new File(userName + "_transactions.pdf"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure the file has .pdf extension
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }
            
            // Create and show progress dialog
            JOptionPane.showMessageDialog(this, 
                "Preparing to export transactions to PDF...", 
                "Export Started", 
                JOptionPane.INFORMATION_MESSAGE);
          
            try {
                // Create document
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();
                
                // Add title
                Paragraph title = new Paragraph("Transaction History for " + userName,
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18, BaseColor.BLACK));
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                
                // Add date
                SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy");
                Paragraph dateStr = new Paragraph("Generated on " + dateFormat.format(new Date()),
                    FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.GRAY));
                dateStr.setAlignment(Element.ALIGN_CENTER);
                document.add(dateStr);
                document.add(new Paragraph(" ")); // Empty line
                
                // Create table
                PdfPTable table = new PdfPTable(5); // 5 columns
                table.setWidthPercentage(100);
                
                // Set column widths
                float[] columnWidths = {2f, 4f, 2f, 2f, 2f};
                table.setWidths(columnWidths);
                
                // Add table headers
                String[] headers = {"Date", "Description", "Category", "Amount", "Balance"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, 
                        FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    cell.setBackgroundColor(new BaseColor(240, 240, 240));
                    cell.setPadding(5);
                    table.addCell(cell);
                }
                
                // Add data rows
                SimpleDateFormat pdfDateFormat = new SimpleDateFormat("MM/dd/yyyy");
                for (TransactionItem transaction : currentTransactions) {
                    // Date
                    table.addCell(new Phrase(pdfDateFormat.format(transaction.date)));
                    
                    // Description
                    table.addCell(new Phrase(transaction.description));
                    
                    // Category
                    table.addCell(new Phrase(transaction.category));
                    
                    // Amount (with color)
                    boolean isPurchase = "purchase".equalsIgnoreCase(transaction.category) || 
                                       transaction.description.toLowerCase().contains("purchase");
                    
                    Phrase amountPhrase;
                    if (isPurchase) {
                        // Format purchase transactions like withdrawals
                        amountPhrase = new Phrase(
                            String.format("-$%.2f", Math.abs(transaction.amount)),
                            FontFactory.getFont(FontFactory.HELVETICA, 12, BaseColor.RED)
                        );
                    } else {
                        // Format other transactions normally
                        amountPhrase = new Phrase(
                            (transaction.amount >= 0 ? "+" : "") + String.format("$%.2f", transaction.amount),
                            FontFactory.getFont(FontFactory.HELVETICA, 12, 
                                transaction.amount >= 0 ? BaseColor.GREEN : BaseColor.RED)
                        );
                    }
                    table.addCell(amountPhrase);
                    
                    // Balance
                    table.addCell(new Phrase(String.format("$%.2f", transaction.balance)));
                }
                
                document.add(table);
                
                // Add summary section
                document.add(new Paragraph(" ")); // Empty line
                document.add(new Paragraph("Transaction Summary", 
                    FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                
                // Calculate summary info
                int totalTransactions = currentTransactions.size();
                double totalDeposits = 0;
                double totalWithdrawals = 0;
                double totalPurchases = 0;
                
                for (TransactionItem transaction : currentTransactions) {
                    boolean isPurchase = "purchase".equalsIgnoreCase(transaction.category) || 
                                       transaction.description.toLowerCase().contains("purchase");
                    
                    if (isPurchase) {
                        totalPurchases += Math.abs(transaction.amount);
                    } else if (transaction.amount > 0) {
                        totalDeposits += transaction.amount;
                    } else if (transaction.amount < 0) {
                        totalWithdrawals += Math.abs(transaction.amount);
                    }
                }
                
                // Add summary details
                document.add(new Paragraph("Total Transactions: " + totalTransactions));
                document.add(new Paragraph("Total Deposits: $" + String.format("%.2f", totalDeposits)));
                document.add(new Paragraph("Total Withdrawals: $" + String.format("%.2f", totalWithdrawals)));
                document.add(new Paragraph("Total Purchases: $" + String.format("%.2f", totalPurchases)));
                
                // Close document
                document.close();
                
                JOptionPane.showMessageDialog(this, 
                    "Transactions successfully exported to:\n" + fileToSave.getAbsolutePath(),
                    "Export Complete", 
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, 
                    "Error exporting transactions: " + e.getMessage(), 
                    "Export Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }

    private void handleButtonClick(String buttonName) {
        System.out.println("Button clicked: " + buttonName);
        
        switch (buttonName) {
            case "Dashboard":
                // Go to Dashboard page
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                    this.dispose();
                });
                break;
            case "Balance":
                // Go to Balance page
                SwingUtilities.invokeLater(() -> {
                    BalancePage balancePage = new BalancePage(userName, userId);
                    balancePage.setVisible(true);
                    this.dispose();
                });
                break;
            case "Accounts":
                // Go to User Profile page
                SwingUtilities.invokeLater(() -> {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setUserInfo(userName, userId);
                    userProfile.setVisible(true);
                    this.dispose();
                });
                break;
            case "Deposit":
                // Go to Deposit page
                SwingUtilities.invokeLater(() -> {
                    Deposite depositScreen = new Deposite();
                    depositScreen.setUserInfo(userName, userId);
                    depositScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Withdraw":
                // Go to Withdraw page
                SwingUtilities.invokeLater(() -> {
                    Withdraw withdrawScreen = new Withdraw();
                    withdrawScreen.setUserInfo(userName, userId);
                    withdrawScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transfers":
                // Go to transfers page
                SwingUtilities.invokeLater(() -> {
                    Transfer transferScreen = new Transfer();
                    transferScreen.setUserInfo(userName, userId);
                    transferScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transactions":
                // Refresh current page
                refreshTransactionData();
                break;
            case "Cards":
                // Cards feature not implemented yet
                JOptionPane.showMessageDialog(this, "Cards feature coming soon!", "Information", JOptionPane.INFORMATION_MESSAGE);
                break;
            case "QR Codes":
                // Show QR Codes tab on dashboard
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                    // Switch to QR Codes tab handled in dashboard
                    this.dispose();
                });
                break;
            default:
                break;
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Transaction gui = new Transaction(12345, "John Doe");
            gui.setVisible(true);
        });
    }
    
    // Custom gradient panel for backgrounds with rounded corners
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color gradientStart;
        private Color gradientEnd;
        
        public RoundedPanel(int radius, Color baseColor) {
            super();
            this.cornerRadius = radius;
            this.gradientStart = baseColor;
            this.gradientEnd = darkenColor(baseColor, 0.2f);
            setOpaque(false);
        }
        
        private Color darkenColor(Color color, float factor) {
            float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
            return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - factor));
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            GradientPaint gradient = new GradientPaint(
                0, 0, gradientStart,
                0, getHeight(), gradientEnd
            );
            g2d.setPaint(gradient);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2d.dispose();
        }
    }
}



