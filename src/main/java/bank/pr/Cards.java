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
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Cards extends JFrame {

    private static final long serialVersionUID = 1L;
    
    // Colors scheme
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color CARD_COLOR = new Color(255, 255, 255);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);
    
    // Card brand colors
    private static final Color ONLINE_CARD_COLOR = new Color(46, 139, 87); // Green
    private static final Color PHONE_CARD_COLOR = new Color(178, 34, 34);  // Red
    private static final Color INTERNET_CARD_COLOR = new Color(72, 61, 139); // Purple
    
    // Components
    private JLabel greeting;
    private String userName;
    private int accountId; // Changed from userId to accountId
    private JPanel mainContentPanel;
    private List<JButton> menuButtons = new ArrayList<>();
    private JPanel cardsPanel;
    private JTabbedPane cardsTabbedPane;
    private JPanel myCardsPanel;
    private JPanel buyCardsPanel;
    
    // Card data
    private final String[] ONLINE_CARDS = {"iTunes", "Google Play", "PlayStation", "Xbox"};
    private final String[] PHONE_CARDS = {"Korek", "Asiacell", "Zain"};
    private final String[] INTERNET_CARDS = {"FTTH", "Fastlink", "Newroz", "Tishknet"};
    
    // Current selection
    private String currentCardType = "";
    
    public Cards() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Kurdish-O-Banking (KOB) - Cards Management");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create components
        createSidebar();
        createHeaderPanel();
        createMainContent();
        
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
    }
    
   
    private void createSidebar() {
        JPanel sidebarPanel = new JPanel() {
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
        sidebarPanel.add(separator);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Menu items with icons
        String[] menuItems = {"Dashboard", "Balance", "Accounts", "Deposit", "Withdraw", "Transfers", "Transactions", "Cards", "QR Codes"};
        String[] iconNames = {"dashboard", "balance", "accounts", "deposit", "withdraw", "transfers", "transactions", "cards", "qrcode"};
        
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
            
            sidebarPanel.add(button);
            menuButtons.add(button);
            sidebarPanel.add(Box.createVerticalStrut(5));
        }
        
        // Set Cards as initially selected
        updateSelectedButton("Cards");
        
        // Add logout at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        JSeparator bottomSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        bottomSeparator.setForeground(new Color(70, 80, 120));
        bottomSeparator.setBackground(new Color(70, 80, 120));
        bottomSeparator.setMaximumSize(new Dimension(220, 1));
        sidebarPanel.add(bottomSeparator);
        
        JButton logoutButton = createMenuButton("Logout", "logout");
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
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        add(sidebarPanel, BorderLayout.WEST);
    }

    private JButton createMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.CENTER);
        button.setIconTextGap(10);
        button.setMaximumSize(new Dimension(220, 45));
        button.setPreferredSize(new Dimension(220, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        try {
            ImageIcon icon = new ImageIcon("icons/" + iconName + ".png");
            java.awt.Image image = icon.getImage().getScaledInstance(16, 16, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            button.setIcon(icon);
            button.setHorizontalTextPosition(SwingConstants.RIGHT);
            button.setVerticalTextPosition(SwingConstants.CENTER);
        } catch (Exception e) {
            // If icon not found, use text only
            System.err.println("Icon not found: " + iconName);
        }
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (button.getClientProperty("selected") == null || 
                    !((Boolean)button.getClientProperty("selected"))) {
                    button.setBackground(new Color(45, 55, 95));
                    button.setContentAreaFilled(true);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (button.getClientProperty("selected") == null || 
                    !((Boolean)button.getClientProperty("selected"))) {
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
        
        greeting = new JLabel("Welcome back");
        greeting.setFont(new Font("SansSerif", Font.BOLD, 16));
        greeting.setForeground(TEXT_COLOR);
        
        JLabel titleLabel = new JLabel("Cards Management");
        titleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        titleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        greetingPanel.add(greeting, BorderLayout.NORTH);
        greetingPanel.add(titleLabel, BorderLayout.SOUTH);
        
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
        mainContentPanel.setLayout(new BorderLayout());
        mainContentPanel.setBackground(BACKGROUND_COLOR);
        mainContentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create tabbed pane for "My Cards" and "Buy Cards"
        cardsTabbedPane = new JTabbedPane(JTabbedPane.TOP);
        cardsTabbedPane.setFont(new Font("SansSerif", Font.BOLD, 14));
        cardsTabbedPane.setBackground(BACKGROUND_COLOR);
        
        // Create "My Cards" panel
        createMyCardsPanel();
        
        // Create "Buy Cards" panel with category selection
        createBuyCardsPanel();
        
        // Add panels to tabbed pane
        cardsTabbedPane.addTab("My Cards", myCardsPanel);
        cardsTabbedPane.addTab("Buy Cards", buyCardsPanel);
        
        mainContentPanel.add(cardsTabbedPane, BorderLayout.CENTER);
        add(mainContentPanel, BorderLayout.CENTER);
    }
    
    private void createMyCardsPanel() {
        myCardsPanel = new JPanel(new BorderLayout());
        myCardsPanel.setBackground(BACKGROUND_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CARD_COLOR);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Your Cards");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionPanel.setOpaque(false);
        
        JButton addCardButton = new JButton("Buy New Card");
        addCardButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        addCardButton.setBackground(SECONDARY_COLOR);
        addCardButton.setForeground(Color.WHITE);
        addCardButton.setFocusPainted(false);
        addCardButton.setBorderPainted(false);
        addCardButton.addActionListener(e -> cardsTabbedPane.setSelectedIndex(1)); // Switch to Buy Cards tab
        
        actionPanel.add(addCardButton);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(actionPanel, BorderLayout.EAST);
        
        // Create cards panel (will be populated in loadCards method)
        cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        cardsPanel.setBackground(BACKGROUND_COLOR);
        
        JScrollPane scrollPane = new JScrollPane(cardsPanel);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        
        myCardsPanel.add(titlePanel, BorderLayout.NORTH);
        myCardsPanel.add(scrollPane, BorderLayout.CENTER);
    }
    
    private void createBuyCardsPanel() {
        buyCardsPanel = new JPanel(new BorderLayout());
        buyCardsPanel.setBackground(BACKGROUND_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CARD_COLOR);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel("Select Card Type");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        
        // Create card type selection content
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new GridLayout(1, 3, 20, 0));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        contentPanel.setBackground(BACKGROUND_COLOR);
        
        // Create card type options
        JPanel onlineCardPanel = createCardTypeOption("Online Cards", ONLINE_CARD_COLOR, 
                "iTunes, Google Play, PlayStation, Xbox");
        JPanel phoneCardPanel = createCardTypeOption("Phone Cards", PHONE_CARD_COLOR, 
                "Korek, Asiacell, Zain");
        JPanel internetCardPanel = createCardTypeOption("Internet Cards", INTERNET_CARD_COLOR, 
                "FTTH, Fastlink, Newroz, Tishknet");
        
        contentPanel.add(onlineCardPanel);
        contentPanel.add(phoneCardPanel);
        contentPanel.add(internetCardPanel);
        
        // Add click handlers to each panel
        onlineCardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCardSelection("ONLINE", ONLINE_CARDS, ONLINE_CARD_COLOR);
            }
        });
        
        phoneCardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCardSelection("PHONE", PHONE_CARDS, PHONE_CARD_COLOR);
            }
        });
        
        internetCardPanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showCardSelection("INTERNET", INTERNET_CARDS, INTERNET_CARD_COLOR);
            }
        });
        
        buyCardsPanel.add(titlePanel, BorderLayout.NORTH);
        buyCardsPanel.add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createCardTypeOption(String title, Color color, String description) {
        RoundedPanel panel = new RoundedPanel(15, Color.WHITE);
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        panel.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
        
        // Color bar at top
        JPanel colorBar = new JPanel();
        colorBar.setBackground(color);
        colorBar.setPreferredSize(new Dimension(0, 8));
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 22));
        titleLabel.setForeground(color);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Description
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(LIGHT_TEXT_COLOR);
        descLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Icon or image (using a placeholder colored panel for now)
        JPanel iconPanel = new JPanel();
        iconPanel.setBackground(new Color(color.getRed(), color.getGreen(), color.getBlue(), 30));
        iconPanel.setPreferredSize(new Dimension(0, 100));
        
        try {
            // Try to load category icon if available
            String iconPath = "icons/" + title.toLowerCase().replace(" ", "_") + ".png";
            ImageIcon icon = new ImageIcon(iconPath);
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setHorizontalAlignment(SwingConstants.CENTER);
            iconPanel.setLayout(new BorderLayout());
            iconPanel.add(iconLabel, BorderLayout.CENTER);
        } catch (Exception e) {
            // If icon not found, use colored panel
            System.err.println("Icon not found for " + title);
        }
        
        // "Select" button
        JButton selectButton = new JButton("Select");
        selectButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        selectButton.setBackground(color);
        selectButton.setForeground(Color.WHITE);
        selectButton.setFocusPainted(false);
        selectButton.setBorderPainted(false);
        
        // Center panel for title and description
        JPanel centerPanel = new JPanel();
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.setOpaque(false);
        centerPanel.add(Box.createVerticalGlue());
        centerPanel.add(titleLabel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(descLabel);
        centerPanel.add(Box.createVerticalGlue());
        
        // Add all components
        panel.add(colorBar, BorderLayout.NORTH);
        panel.add(iconPanel, BorderLayout.CENTER);
        panel.add(centerPanel, BorderLayout.SOUTH);
        
        // Add hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                panel.setBackground(new Color(245, 245, 245));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setBackground(Color.WHITE);
            }
        });
        
        return panel;
    }
    
    private void showCardSelection(String cardType, String[] cardOptions, Color cardColor) {
        // Save current card type
        currentCardType = cardType;
        
        // Create a new panel to replace the buyCardsPanel
        JPanel cardSelectionPanel = new JPanel(new BorderLayout());
        cardSelectionPanel.setBackground(BACKGROUND_COLOR);
        
        // Create title panel
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(CARD_COLOR);
        titlePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel(cardType.charAt(0) + cardType.substring(1).toLowerCase() + " Cards");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        
        // Back button
        JButton backButton = new JButton("← Back to Categories");
        backButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        backButton.setForeground(SECONDARY_COLOR);
        backButton.setBackground(null);
        backButton.setBorder(null);
        backButton.setFocusPainted(false);
        backButton.setContentAreaFilled(false);
        backButton.addActionListener(e -> {
            // Restore original Buy Cards panel
            cardsTabbedPane.setComponentAt(1, buyCardsPanel);
        });
        
        titlePanel.add(titleLabel, BorderLayout.WEST);
        titlePanel.add(backButton, BorderLayout.EAST);
        
        // Create card options panel
        JPanel cardsContainer = new JPanel();
        cardsContainer.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        cardsContainer.setBackground(BACKGROUND_COLOR);
        
        // Add card options
        for (String cardName : cardOptions) {
            JPanel cardOption = createCardPurchasePanel(cardName, cardType, cardColor);
            cardsContainer.add(cardOption);
        }
        
        JScrollPane scrollPane = new JScrollPane(cardsContainer);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(BACKGROUND_COLOR);
        
        cardSelectionPanel.add(titlePanel, BorderLayout.NORTH);
        cardSelectionPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Replace the buy cards panel with the card selection panel
        cardsTabbedPane.setComponentAt(1, cardSelectionPanel);
    }
    
    
    private JPanel createCardPurchasePanel(String cardName, String cardType, Color cardColor) {
        RoundedPanel panel = new RoundedPanel(15, Color.WHITE);
        panel.setPreferredSize(new Dimension(300, 400));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Card logo/image at top
        JPanel imagePanel = new JPanel();
        imagePanel.setPreferredSize(new Dimension(0, 120));
        imagePanel.setBackground(cardColor);
        
        // Use card name as logo text for now
        JLabel logoLabel = new JLabel(cardName);
        logoLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(SwingConstants.CENTER);
        imagePanel.setLayout(new BorderLayout());
        imagePanel.add(logoLabel, BorderLayout.CENTER);
        
        // Card details
        JPanel detailsPanel = new JPanel();
        detailsPanel.setLayout(new BoxLayout(detailsPanel, BoxLayout.Y_AXIS));
        detailsPanel.setOpaque(false);
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        
        JLabel nameLabel = new JLabel(cardName + " Card");
        nameLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        nameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel typeLabel = new JLabel(cardType.charAt(0) + cardType.substring(1).toLowerCase() + " Card");
        typeLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        typeLabel.setForeground(LIGHT_TEXT_COLOR);
        typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Denomination selection
        JLabel denomLabel = new JLabel("Select Amount:");
        denomLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        denomLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        denomLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        
        JPanel radioPanel = new JPanel(new GridLayout(4, 1, 0, 10));
        radioPanel.setOpaque(false);
        
        ButtonGroup group = new ButtonGroup();
        
        // Create amount options based on card type
        int[] amounts;
        if (cardType.equals("ONLINE")) {
            amounts = new int[]{10, 25, 50, 100};
        } else if (cardType.equals("PHONE")) {
            amounts = new int[]{5, 10, 15, 25};
        } else { // INTERNET
            amounts = new int[]{20, 30, 50, 100};
        }
        
        for (int amount : amounts) {
            JRadioButton radio = new JRadioButton("$" + amount);
            radio.setActionCommand(String.valueOf(amount));
            radio.setFont(new Font("SansSerif", Font.BOLD, 16));
            radio.setHorizontalAlignment(SwingConstants.CENTER);
            radio.setOpaque(false);
            
            // Select first option by default
            if (amount == amounts[0]) {
                radio.setSelected(true);
            }
            
            group.add(radio);
            radioPanel.add(radio);
        }
        
      // Buy button
      JButton buyButton = new JButton("Buy Now");
      buyButton.setFont(new Font("SansSerif", Font.BOLD, 16));
      buyButton.setBackground(cardColor);
      buyButton.setForeground(Color.WHITE);
      buyButton.setFocusPainted(false);
      buyButton.setBorderPainted(false);
      buyButton.addActionListener(e -> {
          String selectedAmount = group.getSelection().getActionCommand();
          purchaseCard(cardName, cardType, Double.parseDouble(selectedAmount));
      });
      
      detailsPanel.add(nameLabel);
      detailsPanel.add(Box.createVerticalStrut(5));
      detailsPanel.add(typeLabel);
      detailsPanel.add(Box.createVerticalStrut(10));
      detailsPanel.add(denomLabel);
      detailsPanel.add(radioPanel);
      
      panel.add(imagePanel, BorderLayout.NORTH);
      panel.add(detailsPanel, BorderLayout.CENTER);
      panel.add(buyButton, BorderLayout.SOUTH);
      
      return panel;
  }
 
  private void purchaseCard(String cardName, String cardType, double amount) {
      // Show confirmation dialog
      int confirm = JOptionPane.showConfirmDialog(this,
          "Confirm purchase of " + cardName + " Card for $" + amount + "?",
          "Confirm Purchase",
          JOptionPane.YES_NO_OPTION);
      
      if (confirm != JOptionPane.YES_OPTION) {
          return;
      }
      
      // Check account balance and process purchase
      try (Connection conn = DatabaseConnection.getConnection()) {
          // Get account balance and ID
          String accountQuery = "SELECT account_id, balance FROM accounts WHERE username = ? AND account_type = 'Checking'";
          PreparedStatement accountStmt = conn.prepareStatement(accountQuery);
          accountStmt.setString(1, userName);
          
          ResultSet accountRs = accountStmt.executeQuery();
          
          if (accountRs.next()) {
              int accountId = accountRs.getInt("account_id");
              double balance = accountRs.getDouble("balance");
              
              if (balance < amount) {
                  JOptionPane.showMessageDialog(this,
                      "Insufficient funds in your checking account",
                      "Purchase Failed",
                      JOptionPane.ERROR_MESSAGE);
                  return;
              }
              
              // Proceed with purchase if enough funds
              // 1. Update account balance
              String updateBalanceQuery = "UPDATE accounts SET balance = balance - ? " +
                                        "WHERE account_id = ?";
              PreparedStatement updateStmt = conn.prepareStatement(updateBalanceQuery);
              updateStmt.setDouble(1, amount);
              updateStmt.setInt(2, accountId);
              updateStmt.executeUpdate();
              
              // 2. Add transaction record with account_id
              String transactionQuery = "INSERT INTO transactions (account_id, transaction_type, amount, description) " +
                                       "VALUES (?, 'purchase', ?, ?)";
              PreparedStatement transactionStmt = conn.prepareStatement(transactionQuery);
              transactionStmt.setInt(1, accountId);
              transactionStmt.setDouble(2, amount);
              transactionStmt.setString(3, "Purchase of " + cardName + " Card");
              transactionStmt.executeUpdate();
              
              // 3. Generate PIN and create card record
              String cardPin = generateCardPin();
              
              String cardQuery = "INSERT INTO cards (account_id, card_number, card_type, card_usage_type, " +
                               "card_holder_name, expiry_date, cvv, pin_code, card_status, daily_limit, " +
                               "card_balance, card_name) " +
                               "VALUES (?, ?, 'PREPAID', ?, ?, ?, ?, ?, 'ACTIVE', ?, ?, ?)";
              
              PreparedStatement cardStmt = conn.prepareStatement(cardQuery);
              cardStmt.setInt(1, accountId); // Use account_id instead of user_id
              cardStmt.setString(2, generateCardNumber());
              cardStmt.setString(3, cardType);
              cardStmt.setString(4, userName);
              
              // Set 1 year expiry
              LocalDate expiryDate = LocalDate.now().plusYears(1);
              cardStmt.setDate(5, java.sql.Date.valueOf(expiryDate));
              
              cardStmt.setString(6, generateCVV());
              cardStmt.setString(7, cardPin);
              
              // Set dailyLimit same as card value for prepaid cards
              cardStmt.setDouble(8, amount);
              
              // Set card balance same as purchase amount
              cardStmt.setDouble(9, amount);
              
              // Store card name (provider)
              cardStmt.setString(10, cardName);
              
              cardStmt.executeUpdate();
              
              // Show success message with PIN
              JOptionPane.showMessageDialog(this,
                  "Card purchased successfully!\n\n" +
                  "Card: " + cardName + "\n" +
                  "Amount: $" + amount + "\n" +
                  "PIN Code: " + cardPin + "\n\n" +
                  "Please keep this PIN safe. You will need it to use your card.",
                  "Purchase Successful",
                  JOptionPane.INFORMATION_MESSAGE);
              
              // Switch to My Cards tab and refresh
              cardsTabbedPane.setSelectedIndex(0);
              loadCards();
          } else {
              JOptionPane.showMessageDialog(this,
                  "No checking account found",
                  "Purchase Failed",
                  JOptionPane.ERROR_MESSAGE);
          }
          
      } catch (SQLException ex) {
          JOptionPane.showMessageDialog(this,
              "Error processing purchase: " + ex.getMessage(),
              "Database Error",
              JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
      }
  } 
 
  private String generateCardPin() {
      // Generate a 10-digit PIN
      Random random = new Random();
      StringBuilder sb = new StringBuilder(10);
      for (int i = 0; i < 10; i++) {
          sb.append(random.nextInt(10));
      }
      return sb.toString();
  }
  
  private String generateCardNumber() {
      // Generate 16-digit card number
      StringBuilder sb = new StringBuilder();
      Random random = new Random();
      for (int i = 0; i < 16; i++) {
          sb.append(random.nextInt(10));
      }
      return sb.toString();
  }
  
  private String generateCVV() {
      // Generate 3-digit CVV
      Random random = new Random();
      return String.format("%03d", random.nextInt(1000));
  }
  
  // Helper method to darken a color
  private Color darkenColor(Color color, float factor) {
      float[] hsb = Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null);
      return Color.getHSBColor(hsb[0], hsb[1], Math.max(0, hsb[2] - factor));
  }
  
  // Custom rounded panel class
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
  
  // Load user's cards
  private void loadCards() {
      // Clear existing cards
      cardsPanel.removeAll();
      
      try (Connection conn = DatabaseConnection.getConnection()) {
          String query = "SELECT * FROM cards WHERE account_id = ?";
          PreparedStatement stmt = conn.prepareStatement(query);
          stmt.setInt(1, accountId);
          
          ResultSet rs = stmt.executeQuery();
          
          boolean hasCards = false;
          
          while (rs.next()) {
              hasCards = true;
              int cardId = rs.getInt("card_id");
              String cardNumber = rs.getString("card_number");
              String cardType = rs.getString("card_type");
              String cardUsageType = rs.getString("card_usage_type");
              String cardHolderName = rs.getString("card_holder_name");
              java.sql.Date expiryDate = rs.getDate("expiry_date");
              String cvv = rs.getString("cvv");
              String cardStatus = rs.getString("card_status");
              double dailyLimit = rs.getDouble("daily_limit");
              double cardBalance = rs.getDouble("card_balance");
              String cardName = rs.getString("card_name");
              
              // Create a card panel based on card type
              JPanel cardPanel;
              if ("PREPAID".equals(cardType)) {
                  cardPanel = createPrepaidCardPanel(cardId, cardName, cardUsageType, 
                                                  cardHolderName, expiryDate, cardStatus, cardBalance);
              } else {
                  cardPanel = createBankCardPanel(cardId, cardNumber, cardType, cardUsageType, 
                                                cardHolderName, expiryDate, cvv, cardStatus, dailyLimit);
              }
              
              cardsPanel.add(cardPanel);
          }
          
          if (!hasCards) {
              // Display message if no cards found
              JPanel noCardsPanel = new JPanel();
              noCardsPanel.setBackground(CARD_COLOR);
              noCardsPanel.setPreferredSize(new Dimension(600, 200));
              noCardsPanel.setBorder(BorderFactory.createLineBorder(new Color(230, 230, 230), 1));
              noCardsPanel.setLayout(new GridBagLayout());
              
              GridBagConstraints gbc = new GridBagConstraints();
              gbc.gridwidth = GridBagConstraints.REMAINDER;
              gbc.anchor = GridBagConstraints.CENTER;
              
              JLabel noCardsLabel = new JLabel("You don't have any cards yet");
              noCardsLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
              noCardsLabel.setForeground(TEXT_COLOR);
              
              JLabel subLabel = new JLabel("Go to 'Buy Cards' tab to purchase your first card");
              subLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
              subLabel.setForeground(LIGHT_TEXT_COLOR);
              
              gbc.insets = new Insets(0, 0, 10, 0);
              noCardsPanel.add(noCardsLabel, gbc);
              gbc.insets = new Insets(0, 0, 15, 0);
              noCardsPanel.add(subLabel, gbc);
              
              JButton buyCardButton = new JButton("Buy Cards");
              buyCardButton.setFont(new Font("SansSerif", Font.BOLD, 14));
              buyCardButton.setBackground(SECONDARY_COLOR);
              buyCardButton.setForeground(Color.WHITE);
              buyCardButton.setFocusPainted(false);
              buyCardButton.setBorderPainted(false);
              buyCardButton.addActionListener(e -> cardsTabbedPane.setSelectedIndex(1));
              
              noCardsPanel.add(buyCardButton, gbc);
              
              cardsPanel.add(noCardsPanel);
          }
          
      } catch (SQLException ex) {
          JOptionPane.showMessageDialog(this,
              "Error loading cards: " + ex.getMessage(),
              "Database Error",
              JOptionPane.ERROR_MESSAGE);
          ex.printStackTrace();
      }
      
      // Refresh panel
      cardsPanel.revalidate();
      cardsPanel.repaint();
  }
  
  private JPanel createBankCardPanel(int cardId, String cardNumber, String cardType, String cardUsageType,
                                   String cardHolderName, java.sql.Date expiryDate, String cvv, 
                                   String cardStatus, double dailyLimit) {
      // Determine card color based on card type
      Color cardColor;
      if ("DEBIT".equals(cardType)) {
          cardColor = SECONDARY_COLOR;
      } else if ("CREDIT".equals(cardType)) {
          cardColor = new Color(72, 61, 139);
      } else {
          cardColor = SECONDARY_COLOR;
      }
      
      // Create the card panel
      RoundedPanel cardPanel = new RoundedPanel(15, cardColor);
      cardPanel.setPreferredSize(new Dimension(350, 200));
      cardPanel.setLayout(new BorderLayout());
      cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));
      
      // Card header (bank logo & type)
      JPanel headerPanel = new JPanel(new BorderLayout());
      headerPanel.setOpaque(false);
      
      JLabel bankLabel = new JLabel("Kurdish-O-Banking");
      bankLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
      bankLabel.setForeground(Color.WHITE);
      
      JLabel cardTypeLabel = new JLabel(cardType + " - " + cardUsageType);
      cardTypeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
      cardTypeLabel.setForeground(new Color(240, 240, 240));
      cardTypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);
      
      headerPanel.add(bankLabel, BorderLayout.WEST);
      headerPanel.add(cardTypeLabel, BorderLayout.EAST);
      
      // Card number
      JPanel centerPanel = new JPanel(new BorderLayout());
      centerPanel.setOpaque(false);
      centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
      
      // Format card number with spaces
      String formattedNumber = formatCardNumber(cardNumber);
      JLabel numberLabel = new JLabel(formattedNumber);
      numberLabel.setFont(new Font("Monospaced", Font.BOLD, 18));
      numberLabel.setForeground(Color.WHITE);
      numberLabel.setHorizontalAlignment(SwingConstants.CENTER);
      
      centerPanel.add(numberLabel, BorderLayout.CENTER);
      
      // Card footer (cardholder name, expiry, status)
      JPanel footerPanel = new JPanel(new BorderLayout(15, 0));
      footerPanel.setOpaque(false);
      
      JPanel namePanel = new JPanel();
      namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
      namePanel.setOpaque(false);
      
      JLabel nameTitle = new JLabel("CARD HOLDER");
      nameTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
      nameTitle.setForeground(new Color(240, 240, 240));
      
      JLabel nameValue = new JLabel(cardHolderName.toUpperCase());
      nameValue.setFont(new Font("SansSerif", Font.BOLD, 14));
      nameValue.setForeground(Color.WHITE);
      
      namePanel.add(nameTitle);
      namePanel.add(Box.createVerticalStrut(3));
      namePanel.add(nameValue);
      
      JPanel expiryPanel = new JPanel();
      expiryPanel.setLayout(new BoxLayout(expiryPanel, BoxLayout.Y_AXIS));
      expiryPanel.setOpaque(false);
      
      JLabel expiryTitle = new JLabel("EXPIRES");
      expiryTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
      expiryTitle.setForeground(new Color(240, 240, 240));
      
      // Format date as MM/YY
      String formattedExpiry = formatExpiryDate(expiryDate);
      JLabel expiryValue = new JLabel(formattedExpiry);
      expiryValue.setFont(new Font("SansSerif", Font.BOLD, 14));
      expiryValue.setForeground(Color.WHITE);
      
      expiryPanel.add(expiryTitle);
      expiryPanel.add(Box.createVerticalStrut(3));
      expiryPanel.add(expiryValue);
      
      JPanel statusPanel = new JPanel();
      statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
      statusPanel.setOpaque(false);
      
      JLabel statusTitle = new JLabel("STATUS");
      statusTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
      statusTitle.setForeground(new Color(240, 240, 240));
      
      JLabel statusValue = new JLabel(cardStatus);
      statusValue.setFont(new Font("SansSerif", Font.BOLD, 14));
      statusValue.setForeground(Color.WHITE);
      
      statusPanel.add(statusTitle);
      statusPanel.add(Box.createVerticalStrut(3));
      statusPanel.add(statusValue);
      
      JPanel leftFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
      leftFooter.setOpaque(false);
      leftFooter.add(namePanel);
      leftFooter.add(expiryPanel);
      
      footerPanel.add(leftFooter, BorderLayout.WEST);
      footerPanel.add(statusPanel, BorderLayout.EAST);
      
      // Add all components to card panel
      cardPanel.add(headerPanel, BorderLayout.NORTH);
      cardPanel.add(centerPanel, BorderLayout.CENTER);
      cardPanel.add(footerPanel, BorderLayout.SOUTH);
      
      // Add popup menu on right-click
      cardPanel.addMouseListener(new MouseAdapter() {
          @Override
          public void mouseClicked(MouseEvent e) {
              if (e.getButton() == MouseEvent.BUTTON3) { // Right click
                  showCardOptionsDialog(cardId, cardStatus);
              }
          }
      });
      
      // Add tooltip
      cardPanel.setToolTipText("Right-click for card options");
      
      return cardPanel;
  }
  
  private JPanel createPrepaidCardPanel(int cardId, String cardName, String cardUsageType,
  String cardHolderName, java.sql.Date expiryDate, 
  String cardStatus, double cardBalance) {
// Determine card color based on usage type
Color cardColor;
if ("ONLINE".equals(cardUsageType)) {
cardColor = ONLINE_CARD_COLOR;
} else if ("PHONE".equals(cardUsageType)) {
cardColor = PHONE_CARD_COLOR;
} else if ("INTERNET".equals(cardUsageType)) {
cardColor = INTERNET_CARD_COLOR;
} else {
cardColor = SECONDARY_COLOR;
}

// Create the card panel
RoundedPanel cardPanel = new RoundedPanel(15, cardColor);
cardPanel.setPreferredSize(new Dimension(350, 200));
cardPanel.setLayout(new BorderLayout());
cardPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

// Card header (provider logo & type)
JPanel headerPanel = new JPanel(new BorderLayout());
headerPanel.setOpaque(false);

JLabel providerLabel = new JLabel(cardName);
providerLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
providerLabel.setForeground(Color.WHITE);

JLabel cardTypeLabel = new JLabel("PREPAID - " + cardUsageType);
cardTypeLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
cardTypeLabel.setForeground(new Color(240, 240, 240));
cardTypeLabel.setHorizontalAlignment(SwingConstants.RIGHT);

headerPanel.add(providerLabel, BorderLayout.WEST);
headerPanel.add(cardTypeLabel, BorderLayout.EAST);

// Balance in center
JPanel centerPanel = new JPanel(new BorderLayout());
centerPanel.setOpaque(false);
centerPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

JLabel balanceLabel = new JLabel("$" + String.format("%.2f", cardBalance));
balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
balanceLabel.setForeground(Color.WHITE);
balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);

JLabel balanceTitle = new JLabel("AVAILABLE BALANCE");
balanceTitle.setFont(new Font("SansSerif", Font.PLAIN, 12));
balanceTitle.setForeground(new Color(240, 240, 240));
balanceTitle.setHorizontalAlignment(SwingConstants.CENTER);

JPanel balancePanel = new JPanel();
balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.Y_AXIS));
balancePanel.setOpaque(false);
balancePanel.add(balanceLabel);
balancePanel.add(balanceTitle);

centerPanel.add(balancePanel, BorderLayout.CENTER);

// PIN Button in center (below balance)
JButton showPinButton = new JButton("Show PIN");
showPinButton.setFont(new Font("SansSerif", Font.BOLD, 12));
showPinButton.setForeground(Color.WHITE);
showPinButton.setBackground(new Color(255, 255, 255, 80)); // Semi-transparent white
showPinButton.setBorderPainted(false);
showPinButton.setFocusPainted(false);
showPinButton.setOpaque(false);
showPinButton.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
showPinButton.addActionListener(e -> showPrepaidCardPin(cardId));

JPanel pinButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
pinButtonPanel.setOpaque(false);
pinButtonPanel.add(showPinButton);

centerPanel.add(pinButtonPanel, BorderLayout.SOUTH);

// Card footer (cardholder name, expiry, status)
JPanel footerPanel = new JPanel(new BorderLayout(15, 0));
footerPanel.setOpaque(false);

JPanel namePanel = new JPanel();
namePanel.setLayout(new BoxLayout(namePanel, BoxLayout.Y_AXIS));
namePanel.setOpaque(false);

JLabel nameTitle = new JLabel("CARD HOLDER");
nameTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
nameTitle.setForeground(new Color(240, 240, 240));

JLabel nameValue = new JLabel(cardHolderName.toUpperCase());
nameValue.setFont(new Font("SansSerif", Font.BOLD, 14));
nameValue.setForeground(Color.WHITE);

namePanel.add(nameTitle);
namePanel.add(Box.createVerticalStrut(3));
namePanel.add(nameValue);

JPanel expiryPanel = new JPanel();
expiryPanel.setLayout(new BoxLayout(expiryPanel, BoxLayout.Y_AXIS));
expiryPanel.setOpaque(false);

JLabel expiryTitle = new JLabel("EXPIRES");
expiryTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
expiryTitle.setForeground(new Color(240, 240, 240));

// Format date as MM/YY
String formattedExpiry = formatExpiryDate(expiryDate);
JLabel expiryValue = new JLabel(formattedExpiry);
expiryValue.setFont(new Font("SansSerif", Font.BOLD, 14));
expiryValue.setForeground(Color.WHITE);

expiryPanel.add(expiryTitle);
expiryPanel.add(Box.createVerticalStrut(3));
expiryPanel.add(expiryValue);

JPanel statusPanel = new JPanel();
statusPanel.setLayout(new BoxLayout(statusPanel, BoxLayout.Y_AXIS));
statusPanel.setOpaque(false);

JLabel statusTitle = new JLabel("STATUS");
statusTitle.setFont(new Font("SansSerif", Font.PLAIN, 10));
statusTitle.setForeground(new Color(240, 240, 240));

JLabel statusValue = new JLabel(cardStatus);
statusValue.setFont(new Font("SansSerif", Font.BOLD, 14));
statusValue.setForeground(Color.WHITE);

statusPanel.add(statusTitle);
statusPanel.add(Box.createVerticalStrut(3));
statusPanel.add(statusValue);

JPanel leftFooter = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
leftFooter.setOpaque(false);
leftFooter.add(namePanel);
leftFooter.add(expiryPanel);

footerPanel.add(leftFooter, BorderLayout.WEST);
footerPanel.add(statusPanel, BorderLayout.EAST);

// Add all components to card panel
cardPanel.add(headerPanel, BorderLayout.NORTH);
cardPanel.add(centerPanel, BorderLayout.CENTER);
cardPanel.add(footerPanel, BorderLayout.SOUTH);

// Add popup menu on right-click
cardPanel.addMouseListener(new MouseAdapter() {
@Override
public void mouseClicked(MouseEvent e) {
if (e.getButton() == MouseEvent.BUTTON3) { // Right click
showPrepaidCardOptionsDialog(cardId, cardStatus, cardName);
}
}
});

// Add tooltip
cardPanel.setToolTipText("Right-click for card options or click 'Show PIN' to view PIN code");

return cardPanel;
}
  private void showPrepaidCardOptionsDialog(int cardId, String currentStatus, String cardName) {
      JDialog optionsDialog = new JDialog(this, cardName + " Card Options", true);
      optionsDialog.setSize(350, 250);
      optionsDialog.setLocationRelativeTo(this);
      optionsDialog.setLayout(new BorderLayout());
      
      JPanel contentPanel = new JPanel();
      contentPanel.setLayout(new GridLayout(4, 1, 10, 10));
      contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      contentPanel.setBackground(CARD_COLOR);
      
      JLabel titleLabel = new JLabel("Card Actions");
      titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
      titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
      
      // Options buttons
      JButton viewDetailsButton = createActionButton("View Details");
      JButton viewPinButton = createActionButton("View PIN Code");
      JButton deleteCardButton = createActionButton("Delete Card");
      
      // Set button colors
      viewDetailsButton.setBackground(SECONDARY_COLOR);
      viewPinButton.setBackground(ACCENT_COLOR);
      deleteCardButton.setBackground(new Color(192, 0, 0));
      
      // Set actions
      viewDetailsButton.addActionListener(e -> {
          optionsDialog.dispose();
          showPrepaidCardDetails(cardId);
      });
      
      viewPinButton.addActionListener(e -> {
          optionsDialog.dispose();
          showPrepaidCardPin(cardId);
      });
      
      deleteCardButton.addActionListener(e -> {
          optionsDialog.dispose();
          confirmDeleteCard(cardId);
      });
      
      contentPanel.add(titleLabel);
      contentPanel.add(viewDetailsButton);
      contentPanel.add(viewPinButton);
      contentPanel.add(deleteCardButton);
      
      optionsDialog.add(contentPanel, BorderLayout.CENTER);
      optionsDialog.setVisible(true);
  }
  
  private void showCardOptionsDialog(int cardId, String currentStatus) {
      JDialog optionsDialog = new JDialog(this, "Card Options", true);
      optionsDialog.setSize(350, 300);
      optionsDialog.setLocationRelativeTo(this);
      optionsDialog.setLayout(new BorderLayout());
      
      JPanel contentPanel = new JPanel();
      contentPanel.setLayout(new GridLayout(5, 1, 10, 10));
      contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
      contentPanel.setBackground(CARD_COLOR);
      
      JLabel titleLabel = new JLabel("Card Actions");
      titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
      titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
      
      // Options buttons
      JButton viewDetailsButton = createActionButton("View Details");
      JButton changeStatusButton = createActionButton("ACTIVE".equals(currentStatus) ? "Block Card" : "Activate Card");
      JButton changeLimitButton = createActionButton("Change Daily Limit");
      JButton deleteCardButton = createActionButton("Delete Card");
      
      // Set button colors
      viewDetailsButton.setBackground(SECONDARY_COLOR);
      changeStatusButton.setBackground("ACTIVE".equals(currentStatus) ? new Color(192, 0, 0) : new Color(0, 128, 0));
      changeLimitButton.setBackground(ACCENT_COLOR);
      deleteCardButton.setBackground(new Color(192, 0, 0));
      
      // Set actions
      viewDetailsButton.addActionListener(e -> {
          optionsDialog.dispose();
          showCardDetails(cardId);
      });
      
      changeStatusButton.addActionListener(e -> {
          optionsDialog.dispose();
          updateCardStatus(cardId, "ACTIVE".equals(currentStatus) ? "BLOCKED" : "ACTIVE");
      });
      
      changeLimitButton.addActionListener(e -> {
          optionsDialog.dispose();
          showChangeLimitDialog(cardId);
      });
      
      deleteCardButton.addActionListener(e -> {
          optionsDialog.dispose();
          confirmDeleteCard(cardId);
      });
      
      contentPanel.add(titleLabel);
      contentPanel.add(viewDetailsButton);
      contentPanel.add(changeStatusButton);
      contentPanel.add(changeLimitButton);
      contentPanel.add(deleteCardButton);
      
      optionsDialog.add(contentPanel, BorderLayout.CENTER);
      optionsDialog.setVisible(true);
  }
  
  private JButton createActionButton(String text) {
      JButton button = new JButton(text);
      button.setFont(new Font("SansSerif", Font.BOLD, 14));
      button.setForeground(Color.WHITE);
      button.setFocusPainted(false);
      button.setBorderPainted(false);
      button.setContentAreaFilled(true);
      return button;
  }
  
  private String formatCardNumber(String cardNumber) {
      // Split the card number into groups of 4 digits
      StringBuilder formatted = new StringBuilder();
      for (int i = 0; i < cardNumber.length(); i++) {
          if (i > 0 && i % 4 == 0) {
              formatted.append(" ");
          }
          formatted.append(cardNumber.charAt(i));
      }
      return formatted.toString();
  }
  

  private String formatExpiryDate(java.sql.Date date) {
    if (date == null) {
        return "N/A";
    }
    
    LocalDate localDate = date.toLocalDate();
    return String.format("%02d/%02d", localDate.getMonthValue(), localDate.getYear() % 100);
}

// Methods to handle card options
private void showCardDetails(int cardId) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT * FROM cards WHERE card_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, cardId);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            String cardNumber = rs.getString("card_number");
            String cardType = rs.getString("card_type");
            String cardUsageType = rs.getString("card_usage_type");
            String cardHolderName = rs.getString("card_holder_name");
            java.sql.Date expiryDate = rs.getDate("expiry_date");
            String cvv = rs.getString("cvv");
            String pinCode = rs.getString("pin_code");
            String cardStatus = rs.getString("card_status");
            double dailyLimit = rs.getDouble("daily_limit");
            double cardBalance = rs.getDouble("card_balance");
            
            // Create details dialog
            JDialog detailsDialog = new JDialog(this, "Card Details", true);
            detailsDialog.setSize(400, 500);
            detailsDialog.setLocationRelativeTo(this);
            detailsDialog.setLayout(new BorderLayout());
            
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            contentPanel.setBackground(CARD_COLOR);
            
            JLabel titleLabel = new JLabel("Bank Card Details");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            
            // Create detail rows
            JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 15, 15));
            detailsPanel.setOpaque(false);
            
            addDetailRow(detailsPanel, "Card Number:", hideCardNumber(cardNumber));
            addDetailRow(detailsPanel, "Card Type:", cardType);
            addDetailRow(detailsPanel, "Usage Type:", cardUsageType);
            addDetailRow(detailsPanel, "Card Holder:", cardHolderName);
            addDetailRow(detailsPanel, "Expiry Date:", formatExpiryDate(expiryDate));
            addDetailRow(detailsPanel, "CVV:", hideData(cvv));
            addDetailRow(detailsPanel, "PIN Code:", hideData(pinCode));
            addDetailRow(detailsPanel, "Status:", cardStatus);
            addDetailRow(detailsPanel, "Daily Limit:", "$" + String.format("%.2f", dailyLimit));
            if (cardBalance > 0) {
                addDetailRow(detailsPanel, "Card Balance:", "$" + String.format("%.2f", cardBalance));
            }
            
            // Close button
            JButton closeButton = new JButton("Close");
            closeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            closeButton.setBackground(SECONDARY_COLOR);
            closeButton.setForeground(Color.WHITE);
            closeButton.setFocusPainted(false);
            closeButton.setBorderPainted(false);
            closeButton.addActionListener(e -> detailsDialog.dispose());
            closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeButton.setMaximumSize(new Dimension(150, 40));
            
            contentPanel.add(titleLabel);
            contentPanel.add(detailsPanel);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(closeButton);
            
            detailsDialog.add(contentPanel, BorderLayout.CENTER);
            detailsDialog.setVisible(true);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error loading card details: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void showPrepaidCardDetails(int cardId) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT * FROM cards WHERE card_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, cardId);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            String cardNumber = rs.getString("card_number");
            String cardName = rs.getString("card_name");
            String cardType = rs.getString("card_type");
            String cardUsageType = rs.getString("card_usage_type");
            String cardHolderName = rs.getString("card_holder_name");
            java.sql.Date expiryDate = rs.getDate("expiry_date");
            String pinCode = rs.getString("pin_code");
            String cardStatus = rs.getString("card_status");
            double cardBalance = rs.getDouble("card_balance");
            
            // Create details dialog
            JDialog detailsDialog = new JDialog(this, "Card Details", true);
            detailsDialog.setSize(400, 500);
            detailsDialog.setLocationRelativeTo(this);
            detailsDialog.setLayout(new BorderLayout());
            
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            contentPanel.setBackground(CARD_COLOR);
            
            JLabel titleLabel = new JLabel(cardName + " Card Details");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            
            // Create detail rows
            JPanel detailsPanel = new JPanel(new GridLayout(0, 2, 15, 15));
            detailsPanel.setOpaque(false);
            
            addDetailRow(detailsPanel, "Card Name:", cardName);
            addDetailRow(detailsPanel, "Card Type:", cardType);
            addDetailRow(detailsPanel, "Usage Type:", cardUsageType);
            addDetailRow(detailsPanel, "Card Holder:", cardHolderName);
            addDetailRow(detailsPanel, "Expiry Date:", formatExpiryDate(expiryDate));
            addDetailRow(detailsPanel, "PIN Code:", hideData(pinCode));
            addDetailRow(detailsPanel, "Status:", cardStatus);
            addDetailRow(detailsPanel, "Card Balance:", "$" + String.format("%.2f", cardBalance));
            
            // Close button
            JButton closeButton = new JButton("Close");
            closeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            closeButton.setBackground(SECONDARY_COLOR);
            closeButton.setForeground(Color.WHITE);
            closeButton.setFocusPainted(false);
            closeButton.setBorderPainted(false);
            closeButton.addActionListener(e -> detailsDialog.dispose());
            closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeButton.setMaximumSize(new Dimension(150, 40));
            
            contentPanel.add(titleLabel);
            contentPanel.add(detailsPanel);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(closeButton);
            
            detailsDialog.add(contentPanel, BorderLayout.CENTER);
            detailsDialog.setVisible(true);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error loading card details: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}
private void showPrepaidCardPin(int cardId) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT pin_code, card_name, card_usage_type FROM cards WHERE card_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, cardId);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            String pinCode = rs.getString("pin_code");
            String cardName = rs.getString("card_name");
            String cardUsageType = rs.getString("card_usage_type");
            
            // Determine color based on card type
            Color dialogColor;
            if ("ONLINE".equals(cardUsageType)) {
                dialogColor = ONLINE_CARD_COLOR;
            } else if ("PHONE".equals(cardUsageType)) {
                dialogColor = PHONE_CARD_COLOR;
            } else if ("INTERNET".equals(cardUsageType)) {
                dialogColor = INTERNET_CARD_COLOR;
            } else {
                dialogColor = SECONDARY_COLOR;
            }
            
            // Create a custom styled dialog
            JDialog pinDialog = new JDialog(this, cardName + " Card PIN", true);
            pinDialog.setSize(350, 250);
            pinDialog.setLocationRelativeTo(this);
            pinDialog.setResizable(false);
            pinDialog.setLayout(new BorderLayout());
            
            // Create a panel with gradient background similar to the card
            // Use the existing class variable for the color darkening
            final Color darkerColor = darkenColor(dialogColor, 0.2f);
            JPanel contentPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    // Create gradient background
                    GradientPaint gradient = new GradientPaint(
                        0, 0, dialogColor, 
                        0, getHeight(), darkerColor
                    );
                    g2d.setPaint(gradient);
                    g2d.fillRect(0, 0, getWidth(), getHeight());
                    g2d.dispose();
                }
            };
            
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Add title
            JLabel titleLabel = new JLabel(cardName + " Card PIN");
            titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
            titleLabel.setForeground(Color.WHITE);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Add PIN with large, easy-to-read font
            JPanel pinPanel = new JPanel();
            pinPanel.setOpaque(false);
            pinPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
            ));
            
            // Format PIN with spaces every 4 digits for readability
            StringBuilder formattedPin = new StringBuilder(pinCode);
            if (pinCode.length() >= 8) {
                formattedPin.insert(4, " ");
                if (pinCode.length() >= 10) {
                    formattedPin.insert(9, " ");
                }
            }
            
            JLabel pinLabel = new JLabel(formattedPin.toString());
            pinLabel.setFont(new Font("Monospaced", Font.BOLD, 24));
            pinLabel.setForeground(Color.WHITE);
            
            pinPanel.add(pinLabel);
            pinPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
            pinPanel.setMaximumSize(new Dimension(300, 80));
            
            // Add warning message
            JLabel warningLabel = new JLabel("Keep this PIN confidential!");
            warningLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
            warningLabel.setForeground(new Color(255, 255, 200));
            warningLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Description of how to use PIN
            JLabel usageLabel = new JLabel("Use this PIN when making purchases with this card");
            usageLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
            usageLabel.setForeground(Color.WHITE);
            usageLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            
            // Close button
            JButton closeButton = new JButton("OK");
            closeButton.setFont(new Font("SansSerif", Font.BOLD, 14));
            closeButton.setBackground(new Color(255, 255, 255, 100));
            closeButton.setForeground(Color.WHITE);
            closeButton.setFocusPainted(false);
            closeButton.setBorderPainted(false);
            closeButton.addActionListener(e -> pinDialog.dispose());
            closeButton.setAlignmentX(Component.CENTER_ALIGNMENT);
            closeButton.setMaximumSize(new Dimension(120, 40));
            
            // Add all components to the panel
            contentPanel.add(titleLabel);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(pinPanel);
            contentPanel.add(Box.createVerticalStrut(15));
            contentPanel.add(usageLabel);
            contentPanel.add(Box.createVerticalStrut(5));
            contentPanel.add(warningLabel);
            contentPanel.add(Box.createVerticalStrut(20));
            contentPanel.add(closeButton);
            
            pinDialog.add(contentPanel, BorderLayout.CENTER);
            pinDialog.setVisible(true);
        } else {
            JOptionPane.showMessageDialog(this,
                "Card PIN information not found",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error retrieving card PIN: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
private void updateCardStatus(int cardId, String newStatus) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "UPDATE cards SET card_status = ? WHERE card_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setString(1, newStatus);
        stmt.setInt(2, cardId);
        
        int result = stmt.executeUpdate();
        
        if (result > 0) {
            JOptionPane.showMessageDialog(this,
                "Card status updated to: " + newStatus,
                "Status Updated",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Refresh cards display
            loadCards();
        } else {
            JOptionPane.showMessageDialog(this,
                "Failed to update card status",
                "Update Failed",
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error updating card status: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void showChangeLimitDialog(int cardId) {
    try (Connection conn = DatabaseConnection.getConnection()) {
        String query = "SELECT daily_limit FROM cards WHERE card_id = ?";
        PreparedStatement stmt = conn.prepareStatement(query);
        stmt.setInt(1, cardId);
        ResultSet rs = stmt.executeQuery();
        
        if (rs.next()) {
            double currentLimit = rs.getDouble("daily_limit");
            
            // Show input dialog
            String input = JOptionPane.showInputDialog(this,
                "Enter new daily limit:\nCurrent limit: $" + String.format("%.2f", currentLimit),
                "Change Daily Limit",
                JOptionPane.PLAIN_MESSAGE);
            
            if (input != null && !input.trim().isEmpty()) {
                try {
                    double newLimit = Double.parseDouble(input.trim());
                    
                    if (newLimit <= 0) {
                        JOptionPane.showMessageDialog(this,
                            "Please enter a positive amount",
                            "Invalid Input",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    // Update daily limit
                    String updateQuery = "UPDATE cards SET daily_limit = ? WHERE card_id = ?";
                    PreparedStatement updateStmt = conn.prepareStatement(updateQuery);
                    updateStmt.setDouble(1, newLimit);
                    updateStmt.setInt(2, cardId);
                    
                    int result = updateStmt.executeUpdate();
                    
                    if (result > 0) {
                        JOptionPane.showMessageDialog(this,
                            "Daily limit updated to: $" + String.format("%.2f", newLimit),
                            "Limit Updated",
                            JOptionPane.INFORMATION_MESSAGE);
                        
                        // Refresh cards display
                        loadCards();
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "Failed to update daily limit",
                            "Update Failed",
                            JOptionPane.ERROR_MESSAGE);
                    }
                    
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                        "Please enter a valid number",
                        "Invalid Input",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    } catch (SQLException ex) {
        JOptionPane.showMessageDialog(this,
            "Error updating daily limit: " + ex.getMessage(),
            "Database Error",
            JOptionPane.ERROR_MESSAGE);
    }
}

private void confirmDeleteCard(int cardId) {
    int confirm = JOptionPane.showConfirmDialog(this,
        "Are you sure you want to delete this card?\nThis action cannot be undone.",
        "Confirm Deletion",
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);
    
    if (confirm == JOptionPane.YES_OPTION) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "DELETE FROM cards WHERE card_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, cardId);
            
            int result = stmt.executeUpdate();
            
            if (result > 0) {
                JOptionPane.showMessageDialog(this,
                    "Card deleted successfully",
                    "Card Deleted",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Refresh cards display
                loadCards();
            } else {
                JOptionPane.showMessageDialog(this,
                    "Failed to delete card",
                    "Deletion Failed",
                    JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error deleting card: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
}

private void addDetailRow(JPanel panel, String label, String value) {
    JLabel labelField = new JLabel(label);
    labelField.setFont(new Font("SansSerif", Font.BOLD, 14));
    labelField.setForeground(TEXT_COLOR);
    
    JLabel valueField = new JLabel(value);
    valueField.setFont(new Font("SansSerif", Font.PLAIN, 14));
    valueField.setForeground(LIGHT_TEXT_COLOR);
    
    panel.add(labelField);
    panel.add(valueField);
}

private String hideCardNumber(String cardNumber) {
    if (cardNumber == null || cardNumber.length() < 10) {
        return cardNumber;
    }
    
    // Show only last 4 digits
    StringBuilder masked = new StringBuilder();
    for (int i = 0; i < cardNumber.length() - 4; i++) {
        if (i > 0 && i % 4 == 0) {
            masked.append(" ");
        }
        masked.append("*");
    }
    
    String lastFour = cardNumber.substring(cardNumber.length() - 4);
    masked.append(" ").append(lastFour);
    
    return masked.toString();
}

private String hideData(String data) {
    if (data == null) {
        return "";
    }
    
    return "••••••";
}

public void setUserInfo(String userName, int accountId) {  // Changed parameter name from userId to accountId
    this.userName = userName;
    this.accountId = accountId;  // Store as accountId instead of userId
    
    // Update greeting
    greeting.setText("Welcome back, " + userName + "!");
    
    // Load cards
    loadCards();
}

private void handleButtonClick(String buttonName) {
    System.out.println("Button clicked: " + buttonName);
    
    switch (buttonName) {
        case "Dashboard":
            // Go to Dashboard
            SwingUtilities.invokeLater(() -> {
                Dashbord dashboard = new Dashbord();
                dashboard.setUserInfo(userName, accountId);  // Pass accountId instead of userId
                dashboard.setVisible(true);
                this.dispose();
            });
            break;
        case "Balance":
            // Go to Balance page
            SwingUtilities.invokeLater(() -> {
                BalancePage balancePage = new BalancePage(userName, accountId);  // Pass accountId instead of userId
                balancePage.setVisible(true);
                this.dispose();
            });
            break;
        case "Accounts":
            // Go to User Profile page
            SwingUtilities.invokeLater(() -> {
                UserProfile userProfile = new UserProfile();
                userProfile.setUserInfo(userName, accountId);  // Pass accountId instead of userId
                userProfile.setVisible(true);
                this.dispose();
            });
            break;
        case "Deposit":
            // Go to Deposit page
            SwingUtilities.invokeLater(() -> {
                Deposite depositScreen = new Deposite();
                depositScreen.setUserInfo(userName, accountId);  // Pass accountId instead of userId
                depositScreen.setVisible(true);
                this.dispose();
            });
            break;
        case "Withdraw":
            // Go to Withdraw page
            SwingUtilities.invokeLater(() -> {
                Withdraw withdrawScreen = new Withdraw();
                withdrawScreen.setUserInfo(userName, accountId);  // Pass accountId instead of userId
                withdrawScreen.setVisible(true);
                this.dispose();
            });
            break;
        case "Transactions":
            // Go to transactions page
            SwingUtilities.invokeLater(() -> {
                Transaction transactionScreen = new Transaction(accountId, userName);  // Pass accountId instead of userId
                transactionScreen.setVisible(true);
                this.dispose();
            });
            break;
        case "Transfers":
            // Go to transfers page
            SwingUtilities.invokeLater(() -> {
                Transfer transferScreen = new Transfer();
                transferScreen.setUserInfo(userName, accountId);  // Pass accountId instead of userId
                transferScreen.setVisible(true);
                this.dispose();
            });
            break;
        case "Cards":
            // Refresh current Cards page
            loadCards();
            break;
        case "QR Codes":
            // Go to Dashboard and show QR tab
            SwingUtilities.invokeLater(() -> {
                Dashbord dashboard = new Dashbord();
                dashboard.setUserInfo(userName, accountId);  // Pass accountId instead of userId
                dashboard.setVisible(true);
                // Set the tab to QR Codes
                try {
                    java.lang.reflect.Field field = Dashbord.class.getDeclaredField("mainTabbedPane");
                    field.setAccessible(true);
                    JTabbedPane tabbedPane = (JTabbedPane) field.get(dashboard);
                    tabbedPane.setSelectedIndex(1); // QR Codes tab
                } catch (Exception e) {
                    e.printStackTrace();
                }
                this.dispose();
            });
            break;
            
        default:
            break;
    }
}

public static void main(String[] args) {
    SwingUtilities.invokeLater(() -> {
        Cards cardsPage = new Cards();
        cardsPage.setUserInfo("John Doe", 12345);  // Pass account_id instead of user_id
        cardsPage.setVisible(true);
    });
}
}