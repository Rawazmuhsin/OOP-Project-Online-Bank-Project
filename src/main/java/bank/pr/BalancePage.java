package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
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

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

public class BalancePage extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    private static final Color POSITIVE_COLOR = new Color(0, 150, 50);
    private static final Color NEGATIVE_COLOR = new Color(220, 50, 50);
    
    private JLabel balanceLabel;
    private JLabel availableBalanceLabel;
    private JComboBox<Account> accountComboBox;
    private List<Account> userAccounts;
    private String userName;
    private int userId;
    private JPanel sidebarPanel;
    private List<JButton> menuButtons = new ArrayList<>();
    private JLabel lastUpdateLabel;
    private JPanel transactionListPanel;
    private int  maxVisibleTransactions = 3; // Maximum number of transactions to show

    // Default constructor
    public BalancePage() {
        initializeUI();
    }
    
    // Constructor with user information
    public BalancePage(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        initializeUI();
    }
    
    // Initialize UI components
    private void initializeUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Kurdish-O-Banking (KOB) - Account Balance");
        setSize(1024, 650);  // Optimized size to fit most screens without scrolling
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(0, 0));  // No gap between components
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create sidebar panel (narrower to save space)
        createSidebar();
        
        // Create header panel (smaller to save space)
        createHeaderPanel();
        
        // Create main content (optimized for no scrolling)
        createCompactMainContent();
        
        // Make the window visible
        setVisible(true);
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
        
        // Make sidebar narrower
        sidebarPanel.setPreferredSize(new Dimension(180, getHeight()));
        sidebarPanel.setLayout(new BoxLayout(sidebarPanel, BoxLayout.Y_AXIS));
        
        // Logo panel - more compact
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BorderLayout());
        logoPanel.setBorder(BorderFactory.createEmptyBorder(15, 10, 15, 10));
        
        JLabel bankName = new JLabel("Kurdish-O-Banking");
        bankName.setForeground(Color.WHITE);
        bankName.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        JLabel tagline = new JLabel("Your Future, Your Bank");
        tagline.setForeground(new Color(200, 200, 200));
        tagline.setFont(new Font("SansSerif", Font.ITALIC, 10));
        
        JPanel namePanel = new JPanel();
        namePanel.setOpaque(false);
        namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
        namePanel.add(bankName);
        namePanel.add(Box.createVerticalStrut(2));
        namePanel.add(tagline);
        
        logoPanel.add(namePanel, BorderLayout.CENTER);
        
        // Try to add logo image if available
        try {
            ImageIcon logoIcon = new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png");
            java.awt.Image image = logoIcon.getImage().getScaledInstance(30, 30, java.awt.Image.SCALE_SMOOTH);
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
        separator.setMaximumSize(new Dimension(180, 1));
        sidebarPanel.add(separator);
        sidebarPanel.add(Box.createVerticalStrut(10));
        
        // Menu items with icons - same as dashboard but more compact
        String[] menuItems = {"Dashboard", "Balance", "Accounts", "Deposit", "Withdraw", "Transfers", "Transactions", "Cards", "QR Codes"};
        String[] iconNames = {"dashboard", "balance", "accounts", "deposit", "withdraw", "transfers", "transactions", "cards", "qrcode"};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton button = createCompactMenuButton(menuItems[i], iconNames[i]);
            
            final String item = menuItems[i];
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleButtonClick(item);
                }
            });
            
            sidebarPanel.add(button);
            menuButtons.add(button);
            sidebarPanel.add(Box.createVerticalStrut(2));
        }
        
        // Set Balance as initially selected
        updateSelectedButton("Balance");
        
        // Add logout at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        JSeparator bottomSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        bottomSeparator.setForeground(new Color(70, 80, 120));
        bottomSeparator.setBackground(new Color(70, 80, 120));
        bottomSeparator.setMaximumSize(new Dimension(180, 1));
        sidebarPanel.add(bottomSeparator);
        
        JButton logoutButton = createCompactMenuButton("Logout", "logout");
        logoutButton.addActionListener(e -> {
            int choice = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to logout?",
                "Logout Confirmation",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (choice == JOptionPane.YES_OPTION) {
                dispose();
                // Open login page
                SwingUtilities.invokeLater(() -> {
                    LoginUI loginPage = new LoginUI();
                    loginPage.setVisible(true);
                });
            }
        });
        
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(10));
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private JButton createCompactMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(8);
        button.setMaximumSize(new Dimension(180, 35));
        button.setPreferredSize(new Dimension(180, 35));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 5));
        
        // Try to load icon if available
        try {
            ImageIcon icon = new ImageIcon("icons/" + iconName + ".png");
            java.awt.Image image = icon.getImage().getScaledInstance(14, 14, java.awt.Image.SCALE_SMOOTH);
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
    
    private void updateSelectedButton(String selectedItem) {
        for (JButton button : menuButtons) {
            if (button.getText().equals(selectedItem)) {
                button.setBackground(SECONDARY_COLOR);
                button.setContentAreaFilled(true);
                button.setFont(new Font("SansSerif", Font.BOLD, 13));
                button.putClientProperty("selected", true);
            } else {
                button.setContentAreaFilled(false);
                button.setFont(new Font("SansSerif", Font.PLAIN, 13));
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
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("Account Balance");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel("Monitor and manage your balances");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        titlePanel.add(titleLabel, BorderLayout.NORTH);
        titlePanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Current date/time panel
        JPanel dateTimePanel = new JPanel(new BorderLayout());
        dateTimePanel.setOpaque(false);
        
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy");
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm a");
        
        JLabel dateLabel = new JLabel(now.format(dateFormatter));
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        dateLabel.setForeground(TEXT_COLOR);
        dateLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        JLabel timeLabel = new JLabel(now.format(timeFormatter));
        timeLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        timeLabel.setForeground(LIGHT_TEXT_COLOR);
        timeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        dateTimePanel.add(dateLabel, BorderLayout.NORTH);
        dateTimePanel.add(timeLabel, BorderLayout.SOUTH);
        
        headerPanel.add(dateTimePanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createCompactMainContent() {
        // Main container with GridBagLayout for precise control
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.BOTH;
        
        // ===== Account Selection Row =====
        JPanel accountSelectionPanel = new JPanel(new BorderLayout(10, 0));
        accountSelectionPanel.setBackground(CARD_COLOR);
        accountSelectionPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Account selector
        userAccounts = fetchUserAccounts();
        accountComboBox = new JComboBox<>();
        
        DefaultComboBoxModel<Account> accountModel = new DefaultComboBoxModel<>();
        if (!userAccounts.isEmpty()) {
            for (Account account : userAccounts) {
                accountModel.addElement(account);
            }
        } else {
            accountModel.addElement(new Account(0, "No accounts found", "Unknown", 0.0));
        }
        
        accountComboBox.setModel(accountModel);
        accountComboBox.setFont(new Font("SansSerif", Font.PLAIN, 13));
        
        // Last update label
        lastUpdateLabel = new JLabel("Last updated: " + 
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss")));
        lastUpdateLabel.setFont(new Font("SansSerif", Font.ITALIC, 11));
        lastUpdateLabel.setForeground(LIGHT_TEXT_COLOR);
        
        // Refresh button
        JButton refreshButton = new JButton("↻");
        refreshButton.setFocusPainted(false);
        refreshButton.setBackground(SECONDARY_COLOR);
        refreshButton.setForeground(Color.WHITE);
        refreshButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        refreshButton.setPreferredSize(new Dimension(30, 30));
        refreshButton.addActionListener(e -> {
            userAccounts = fetchUserAccounts();
            DefaultComboBoxModel<Account> newModel = new DefaultComboBoxModel<>();
            
            if (!userAccounts.isEmpty()) {
                for (Account account : userAccounts) {
                    newModel.addElement(account);
                }
                accountComboBox.setModel(newModel);
                updateBalanceDisplay();
                lastUpdateLabel.setText("Last updated: " + 
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss")));
            } else {
                newModel.addElement(new Account(0, "No accounts found", "Unknown", 0.0));
                accountComboBox.setModel(newModel);
            }
        });
        
        accountComboBox.addActionListener(e -> {
            updateBalanceDisplay();
            lastUpdateLabel.setText("Last updated: " + 
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm:ss")));
        });
        
        JPanel comboAndRefreshPanel = new JPanel(new BorderLayout(5, 0));
        comboAndRefreshPanel.setOpaque(false);
        comboAndRefreshPanel.add(accountComboBox, BorderLayout.CENTER);
        comboAndRefreshPanel.add(refreshButton, BorderLayout.EAST);
        
        accountSelectionPanel.add(comboAndRefreshPanel, BorderLayout.CENTER);
        accountSelectionPanel.add(lastUpdateLabel, BorderLayout.SOUTH);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.05;
        mainPanel.add(accountSelectionPanel, gbc);
        
        // ===== Balance Cards Row =====
        JPanel balanceCardsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        balanceCardsPanel.setOpaque(false);
        
        // Current Balance Card
        JPanel currentBalanceCard = createCompactBalanceCard("Current Balance", "$0.00", SECONDARY_COLOR,
                "Includes all transactions, including pending ones");
        
        // Available Balance Card
        JPanel availableBalanceCard = createCompactBalanceCard("Available Balance", "$0.00", ACCENT_COLOR,
                "Funds immediately available for withdrawal");
        
        balanceLabel = findBalanceLabelInCard(currentBalanceCard);
        availableBalanceLabel = findBalanceLabelInCard(availableBalanceCard);
        
        balanceCardsPanel.add(currentBalanceCard);
        balanceCardsPanel.add(availableBalanceCard);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.25;
        mainPanel.add(balanceCardsPanel, gbc);
        
        // ===== Action and Transaction Row =====
        // Create a container for both transactions and actions
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        bottomPanel.setOpaque(false);
        
        // Recent Transactions (Left side)
        JPanel transactionsPanel = createCompactSectionPanel("Recent Transactions");
        transactionsPanel.setLayout(new BorderLayout());
        
        // Fixed height transaction panel
        transactionListPanel = new JPanel();
        transactionListPanel.setLayout(new BoxLayout(transactionListPanel, BoxLayout.Y_AXIS));
        transactionListPanel.setOpaque(false);
        
        // Add placeholder initially
        JLabel placeholderLabel = new JLabel("Select an account to view transactions");
        placeholderLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        placeholderLabel.setForeground(LIGHT_TEXT_COLOR);
        placeholderLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        transactionListPanel.add(placeholderLabel);
        
        // No scroll pane - we'll just show a fixed number of transactions
        JPanel transactionContentPanel = new JPanel(new BorderLayout());
        transactionContentPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        transactionContentPanel.setOpaque(false);
        transactionContentPanel.add(transactionListPanel, BorderLayout.CENTER);
        
        transactionsPanel.add(transactionContentPanel, BorderLayout.CENTER);
        
        // Quick Actions (Right side)
        JPanel actionsPanel = createCompactSectionPanel("Quick Actions");
        actionsPanel.setLayout(new GridLayout(2, 2, 8, 8));
        actionsPanel.setBorder(BorderFactory.createCompoundBorder(
            actionsPanel.getBorder(),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        // Create compact action buttons
        JButton statementButton = createCompactActionButton("Statement", "Generate account statement");
        JButton exportButton = createCompactActionButton("Export", "Save balance details");
        JButton historyButton = createCompactActionButton("History", "View all transactions");
        JButton transferButton = createCompactActionButton("Transfer", "Move funds between accounts");
        
        statementButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Statement feature coming soon!"));
        exportButton.addActionListener(e -> exportToPDF());
        historyButton.addActionListener(e -> handleButtonClick("Transactions"));
        transferButton.addActionListener(e -> handleButtonClick("Transfers"));
        
        actionsPanel.add(statementButton);
        actionsPanel.add(exportButton);
        actionsPanel.add(historyButton);
        actionsPanel.add(transferButton);
        
        // Add both panels to the bottom container
        bottomPanel.add(transactionsPanel);
        bottomPanel.add(actionsPanel);
        
        // Add bottom panel to main layout
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.weighty = 0.7; // Make this section taller
        mainPanel.add(bottomPanel, gbc);
        
        // Update display if we have accounts
        if (!userAccounts.isEmpty()) {
            updateBalanceDisplay();
        }
        
        // Add main panel to frame without scrolling
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createCompactSectionPanel(String title) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(3, 0, 5, 0)
        ));
        
        // Title bar
        JPanel titleBar = new JPanel(new BorderLayout());
        titleBar.setBackground(CARD_COLOR);
        titleBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        
        titleBar.add(titleLabel, BorderLayout.WEST);
        
        panel.add(titleBar, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createCompactBalanceCard(String title, String amount, Color accentColor, String description) {
        // Create panel with rounded corners
        JPanel card = new RoundedPanel(12, accentColor);
        card.setLayout(new BorderLayout());
        card.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setOpaque(false);
        
        // Title Label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Balance Panel
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.X_AXIS));
        balancePanel.setOpaque(false);
        balancePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel balanceLabel = new JLabel(amount);
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setName("balanceValue"); // Add name to make it easier to find
        
        balancePanel.add(balanceLabel);
        
        // Description Label
        JLabel descriptionLabel = new JLabel(description);
        descriptionLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descriptionLabel.setForeground(new Color(240, 240, 240));
        descriptionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Add components to header panel
        headerPanel.add(titleLabel);
        headerPanel.add(Box.createVerticalStrut(10));
        headerPanel.add(balancePanel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(descriptionLabel);
        
        card.add(headerPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JButton createCompactActionButton(String title, String description) {
        JButton button = new JButton();
        button.setLayout(new BorderLayout(8, 3));
        button.setBackground(CARD_COLOR);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        descLabel.setForeground(LIGHT_TEXT_COLOR);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(2));
        textPanel.add(descLabel);
        
        button.add(textPanel, BorderLayout.CENTER);
        
        // Try to add icon if available
        try {
            String iconName = title.toLowerCase().replace(" ", "_");
            ImageIcon icon = new ImageIcon("icons/" + iconName + ".png");
            java.awt.Image image = icon.getImage().getScaledInstance(20, 20, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 5));
            button.add(iconLabel, BorderLayout.WEST);
        } catch (Exception e) {
            // If icon not found, use text only
            System.err.println("Icon not found for action button: " + title);
        }
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(250, 250, 250));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(CARD_COLOR);
            }
        });
        
        return button;
    }
    
    // Helper method to find the balance label in a card
    private JLabel findBalanceLabelInCard(JPanel card) {
        try {
            // Get the header panel (first component)
            Component headerComp = card.getComponent(0);
            if (!(headerComp instanceof JPanel)) return new JLabel("$0.00");
            
            JPanel headerPanel = (JPanel)headerComp;
            
            // First try to find by name
            for (Component comp : headerPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel)comp;
                    for (Component c : panel.getComponents()) {
                        if (c instanceof JLabel && "balanceValue".equals(c.getName())) {
                            return (JLabel)c;
                        }
                    }
                }
            }
            
            // If not found by name, search in all panels
            for (Component comp : headerPanel.getComponents()) {
                if (comp instanceof JPanel) {
                    JPanel panel = (JPanel)comp;
                    if (panel.getComponentCount() > 0 && panel.getComponent(0) instanceof JLabel) {
                        return (JLabel)panel.getComponent(0);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error finding balance label: " + e.getMessage());
        }
        
        // Fallback
        return new JLabel("$0.00");
    }
    
    private List<Account> fetchUserAccounts() {
        List<Account> accounts = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query;
            PreparedStatement stmt;
            
            if (userId > 0 && userName != null) {
                // If user info is provided, fetch only that user's accounts
                query = "SELECT account_id, username, account_type, balance FROM accounts WHERE account_id = ? OR username = ?";
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, userId);
                stmt.setString(2, userName);
            } else {
                // Otherwise fetch all accounts
                query = "SELECT account_id, username, account_type, balance FROM accounts";
                stmt = conn.prepareStatement(query);
            }
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                int accountId = rs.getInt("account_id");
                String username = rs.getString("username");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                
                accounts.add(new Account(accountId, username, accountType, balance));
            }
        } catch (SQLException e) {
            System.err.println("Database error: " + e.getMessage());
            e.printStackTrace();
        }
        
        return accounts;
    }
    
    private void updateBalanceDisplay() {
        if (accountComboBox.getSelectedItem() != null) {
            Account selectedAccount = (Account) accountComboBox.getSelectedItem();
            
            // Update both balance displays
            String formattedBalance = String.format("$%.2f", selectedAccount.getBalance());
            balanceLabel.setText(formattedBalance);
            availableBalanceLabel.setText(formattedBalance); // Assuming available balance is the same as current balance
            
            // Update user ID and username based on the selected account
            userId = selectedAccount.getAccountId();
            userName = selectedAccount.getUsername();
            
            // Update transaction list
            updateTransactionList();
        }
    }
    
    private void updateTransactionList() {
        // Clear existing transactions
        transactionListPanel.removeAll();
        
        // Get recent transactions
        List<TransactionItem> transactions = fetchTransactionHistory();
        
        if (transactions.isEmpty() || (transactions.size() == 1 && transactions.get(0).getDescription().contains("No transactions"))) {
            // No transactions found
            JLabel noTransactionsLabel = new JLabel("No recent transactions found");
            noTransactionsLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
            noTransactionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
            noTransactionsLabel.setForeground(LIGHT_TEXT_COLOR);
            noTransactionsLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            transactionListPanel.add(Box.createVerticalStrut(10));
            transactionListPanel.add(noTransactionsLabel);
        } else {
            // Add transactions to the panel - only show max number of transactions
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            int count = 0;
            
            for (TransactionItem transaction : transactions) {
                if (count >= maxVisibleTransactions) break;
                
                JPanel transactionPanel = createCompactTransactionPanel(
                    transaction.getCategory(),
                    transaction.getDescription(),
                    transaction.getAmount(),
                    dateFormat.format(transaction.getDate())
                );
                
                transactionListPanel.add(transactionPanel);
                transactionListPanel.add(Box.createVerticalStrut(5));
                count++;
            }
            
            // Add "View All" button if there are more than maxVisibleTransactions
            if (transactions.size() > maxVisibleTransactions) {
                JButton viewAllButton = new JButton("View All Transactions");
                viewAllButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                viewAllButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
                viewAllButton.setForeground(SECONDARY_COLOR);
                viewAllButton.setFocusPainted(false);
                viewAllButton.setBorderPainted(false);
                viewAllButton.setContentAreaFilled(false);
                viewAllButton.addActionListener(e -> handleButtonClick("Transactions"));
                
                transactionListPanel.add(Box.createVerticalStrut(5));
                transactionListPanel.add(viewAllButton);
            }
        }
        
        // Refresh panel
        transactionListPanel.revalidate();
        transactionListPanel.repaint();
    }
    
    private JPanel createCompactTransactionPanel(String type, String description, double amount, String date) {
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(8, 0));
        panel.setBackground(CARD_COLOR);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(240, 240, 240), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        // Icon/Type on left
        JPanel typePanel = new JPanel(new BorderLayout());
        typePanel.setOpaque(false);
        typePanel.setPreferredSize(new Dimension(20, 20));
        
        // Try to add icon if available
        try {
            String iconName = type.toLowerCase();
            ImageIcon icon = new ImageIcon("icons/" + iconName + ".png");
            java.awt.Image image = icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            JLabel iconLabel = new JLabel(icon);
            typePanel.add(iconLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            // If icon not found, use text only
            JLabel typeLabel = new JLabel(type.substring(0, 1).toUpperCase());
            typeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
            typeLabel.setForeground(TEXT_COLOR);
            typePanel.add(typeLabel, BorderLayout.CENTER);
        }
        
        // Description and date in center
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 13));
        descLabel.setForeground(TEXT_COLOR);
        
        JLabel dateLabel = new JLabel(date);
        dateLabel.setFont(new Font("SansSerif", Font.PLAIN, 11));
        dateLabel.setForeground(LIGHT_TEXT_COLOR);
        
        detailsPanel.add(descLabel);
        detailsPanel.add(Box.createVerticalStrut(2));
        detailsPanel.add(dateLabel);
        
        // Amount on right
        JLabel amountLabel = new JLabel(String.format("$%.2f", amount));
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        amountLabel.setHorizontalAlignment(SwingConstants.RIGHT);
        
        // Set color based on transaction type
        if (type.equalsIgnoreCase("deposit") || type.equalsIgnoreCase("credit")) {
            amountLabel.setForeground(POSITIVE_COLOR);
        } else {
            amountLabel.setForeground(NEGATIVE_COLOR);
        }
        
        panel.add(typePanel, BorderLayout.WEST);
        panel.add(detailsPanel, BorderLayout.CENTER);
        panel.add(amountLabel, BorderLayout.EAST);
        
        return panel;
    }
    
    // Method to export balance and transaction history to PDF
    private void exportToPDF() {
        if (accountComboBox.getSelectedItem() == null) {
            JOptionPane.showMessageDialog(this, 
                    "Please select an account first", 
                    "Export Error", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Show file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("PDF Files", "pdf"));
        
        if (fileChooser.showSaveDialog(this) == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".pdf")) {
                filePath += ".pdf";
            }
            
            try {
                // Create PDF document
                Document document = new Document();
                PdfWriter.getInstance(document, new FileOutputStream(filePath));
                document.open();
                
                // Add title
                com.itextpdf.text.Font titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
                Paragraph title = new Paragraph("Account Statement", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph(" ")); // Add space
                
                // Add account info
                Account selectedAccount = (Account) accountComboBox.getSelectedItem();
                com.itextpdf.text.Font normalFont = FontFactory.getFont(FontFactory.HELVETICA, 12);
                document.add(new Paragraph("Account Information:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                document.add(new Paragraph("Account Holder: " + selectedAccount.getUsername(), normalFont));
                document.add(new Paragraph("Account Type: " + selectedAccount.getAccountType(), normalFont));
                document.add(new Paragraph("Account Number: " + selectedAccount.getAccountId(), normalFont));
                document.add(new Paragraph("Current Balance: $" + String.format("%.2f", selectedAccount.getBalance()), normalFont));
                document.add(new Paragraph("Statement Date: " + new SimpleDateFormat("MMMM dd, yyyy").format(new Date()), normalFont));
                document.add(new Paragraph(" ")); // Add space
                
                // Add transaction history
                document.add(new Paragraph("Recent Transactions:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
                document.add(new Paragraph(" ")); // Add space
                
                // Create transaction table
                PdfPTable table = new PdfPTable(5); // 5 columns
                table.setWidthPercentage(100);
                
                // Set column widths
                float[] columnWidths = {2f, 4f, 2f, 2f, 2f};
                table.setWidths(columnWidths);
                
                // Add table headers
                String[] headers = {"Date", "Description", "Type", "Amount", "Balance"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    cell.setHorizontalAlignment(Element.ALIGN_LEFT);
                    cell.setPadding(5);
                    table.addCell(cell);
                }
                
                // Add transaction data from database
                List<TransactionItem> transactions = fetchTransactionHistory();
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
                
                for (TransactionItem transaction : transactions) {
                    table.addCell(dateFormat.format(transaction.getDate()));
                    table.addCell(transaction.getDescription());
                    table.addCell(transaction.getCategory());
                    table.addCell(String.format("$%.2f", transaction.getAmount()));
                    table.addCell(String.format("$%.2f", transaction.getBalance()));
                }
                
                document.add(table);
                
                // Add footer
                document.add(new Paragraph(" ")); // Add space
                Paragraph footer = new Paragraph("Kurdish - O - Banking (KOB)", normalFont);
                footer.setAlignment(Element.ALIGN_CENTER);
                document.add(footer);
                
                document.close();
                
                JOptionPane.showMessageDialog(this, 
                        "PDF report has been saved successfully!", 
                        "Export Successful", 
                        JOptionPane.INFORMATION_MESSAGE);
                
            } catch (DocumentException | java.io.IOException e) {
                JOptionPane.showMessageDialog(this, 
                        "Error creating PDF: " + e.getMessage(), 
                        "Export Error", 
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    // Class to store transaction data for PDF export
    private class TransactionItem {
        private Date date;
        private String description;
        private String category;
        private double amount;
        private double balance;
        
        public TransactionItem(Date date, String description, String category, double amount, double balance) {
            this.date = date;
            this.description = description;
            this.category = category;
            this.amount = amount;
            this.balance = balance;
        }
        
        public Date getDate() {
            return date;
        }
        
        public String getDescription() {
            return description;
        }
        
        public String getCategory() {
            return category;
        }
        
        public double getAmount() {
            return amount;
        }
        
        public double getBalance() {
            return balance;
        }
    }
    
    // Fetch transaction history from database
    private List<TransactionItem> fetchTransactionHistory() {
        List<TransactionItem> transactions = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT t.transaction_date, t.description, t.transaction_type as category, " +
                           "t.amount, a.balance " +
                           "FROM transactions t " +
                           "JOIN accounts a ON t.account_id = a.account_id " +
                           "WHERE a.username = ? OR a.account_id = ? " +
                           "ORDER BY t.transaction_date DESC LIMIT 20"; // Get most recent 20 transactions
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userName);
            stmt.setInt(2, userId);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Date date = rs.getDate("transaction_date");
                String description = rs.getString("description");
                String category = rs.getString("category");
                double amount = rs.getDouble("amount");
                double balance = rs.getDouble("balance");
                
                transactions.add(new TransactionItem(date, description, category, amount, balance));
            }
        } catch (SQLException e) {
            System.err.println("Database error loading transactions: " + e.getMessage());
            e.printStackTrace();
            
            // Add a placeholder transaction if there's an error
            transactions.add(new TransactionItem(new Date(), "Error loading transactions", "Error", 0.0, 0.0));
        }
        
        // If no transactions found, add a placeholder
        if (transactions.isEmpty()) {
            transactions.add(new TransactionItem(new Date(), "No transactions found", "Info", 0.0, 0.0));
        }
        
        return transactions;
    }
    
    // Method to handle sidebar button clicks
    private void handleButtonClick(String buttonName) {
        System.out.println("Button clicked: " + buttonName);
        
        switch (buttonName) {
            case "Dashboard":
                // Go to Dashboard
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    if (userName != null && userId > 0) {
                        dashboard.setUserInfo(userName, userId);
                    }
                    dashboard.setVisible(true);
                    this.dispose();
                });
                break;
            case "Balance":
                // We're already here
                break;
            case "Accounts":
                // Go to User Profile page
                SwingUtilities.invokeLater(() -> {
                    UserProfile userProfile = new UserProfile();
                    if (userName != null && userId > 0) {
                        userProfile.setUserInfo(userName, userId);
                    }
                    userProfile.setVisible(true);
                    this.dispose();
                });
                break;
            case "Deposit":
                // Go to Deposit page
                SwingUtilities.invokeLater(() -> {
                    Deposite deposit = new Deposite();
                    if (userName != null && userId > 0) {
                        deposit.setUserInfo(userName, userId);
                    }
                    deposit.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transfers":
                // Go to Transfer page
                SwingUtilities.invokeLater(() -> {
                    Transfer transfer = new Transfer();
                    if (userName != null && userId > 0) {
                        transfer.setUserInfo(userName, userId);
                    }
                    transfer.setVisible(true);
                    this.dispose();
                });
                break;
            case "Withdraw":
                // Go to Withdraw page
                SwingUtilities.invokeLater(() -> {
                    Withdraw withdraw = new Withdraw();
                    if (userName != null && userId > 0) {
                        withdraw.setUserInfo(userName, userId);
                    }
                    withdraw.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transactions":
                // Go to transactions page
                SwingUtilities.invokeLater(() -> {
                    Transaction transactionScreen = new Transaction(userId, userName);
                    transactionScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Cards":
                // Go to cards page
                JOptionPane.showMessageDialog(this, "Cards feature coming soon!");
                break;
            case "QR Codes":
                // Go to Dashboard and select QR Codes tab
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    if (userName != null && userId > 0) {
                        dashboard.setUserInfo(userName, userId);
                    }
                    dashboard.setVisible(true);
                    this.dispose();
                });
                break;
            default:
                JOptionPane.showMessageDialog(this, buttonName + " page coming soon!");
                break;
        }
    }
    
    // RoundedPanel class for balance cards - matching Dashboard
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
    
    // Inner class to represent an account
    class Account {
        private int accountId;
        private String username;
        private String accountType;
        private double balance;
        
        public Account(int accountId, String username, String accountType, double balance) {
            this.accountId = accountId;
            this.username = username;
            this.accountType = accountType;
            this.balance = balance;
        }
        
        public int getAccountId() {
            return accountId;
        }
        
        public String getUsername() {
            return username;
        }
        
        public String getAccountType() {
            return accountType;
        }
        
        public double getBalance() {
            return balance;
        }
        
        @Override
        public String toString() {
            return username + " - " + accountType + " (#" + accountId + ")";
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new BalancePage();
        });
    }
}