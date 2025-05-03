
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
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Transfer extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Colors scheme - same as Dashbord
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
    private JLabel greeting;
    private JTextField amountField;
    private JTextField toAccountField;
    private JTextField fromAccountField;
    private JButton verifyButton;
    private JButton scanQRButton;
    private JLabel verificationStatusLabel;
    private ButtonGroup transferTypeGroup;
    private JRadioButton internalTransferRadio;
    private JRadioButton externalTransferRadio;
    private boolean isVerified = false;
    private String verifiedRecipientName = "";
    private int userId = 0;
    private String userName = "";
    private JPanel sidebarPanel;
    private List<JButton> menuButtons = new ArrayList<>();
    private JButton transferButton;
    
    public Transfer() {
        // Set look and feel to be more modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Transfer Funds - Kurdish-O-Banking (KOB)");
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
        sidebarPanel.add(separator);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        // Menu items with icons - Same as Dashboard for consistency
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
        
        // Set Transfers as initially selected
        updateSelectedButton("Transfers");
        
        // Add logout at bottom
        sidebarPanel.add(Box.createVerticalGlue());
        
        JSeparator bottomSeparator = new JSeparator(SwingConstants.HORIZONTAL);
        bottomSeparator.setForeground(new Color(70, 80, 120));
        bottomSeparator.setBackground(new Color(70, 80, 120));
        bottomSeparator.setMaximumSize(new Dimension(220, 1));
        sidebarPanel.add(bottomSeparator);
        
        JButton logoutButton = createMenuButton("Logout", "logout");
        logoutButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int choice = JOptionPane.showConfirmDialog(
                    Transfer.this,
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
        
        sidebarPanel.add(logoutButton);
        sidebarPanel.add(Box.createVerticalStrut(20));
        
        add(sidebarPanel, BorderLayout.WEST);
    }
    
    private JButton createMenuButton(String text, String iconName) {
        JButton button = new JButton(text);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setIconTextGap(10);
        button.setMaximumSize(new Dimension(220, 45));
        button.setPreferredSize(new Dimension(220, 45));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 14));
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(10, 25, 10, 10));
        
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
        
        greeting = new JLabel("Transfer Funds");
        greeting.setFont(new Font("SansSerif", Font.BOLD, 20));
        greeting.setForeground(TEXT_COLOR);
        
        JLabel subtitleLabel = new JLabel("Send money safely to other accounts");
        subtitleLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitleLabel.setForeground(LIGHT_TEXT_COLOR);
        
        greetingPanel.add(greeting, BorderLayout.NORTH);
        greetingPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        headerPanel.add(greetingPanel, BorderLayout.WEST);
        
        // Account info on the right
        JPanel accountPanel = new JPanel();
        accountPanel.setLayout(new BoxLayout(accountPanel, BoxLayout.Y_AXIS));
        accountPanel.setOpaque(false);
        
        JLabel accountLabel = new JLabel("From Account");
        accountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        accountLabel.setForeground(LIGHT_TEXT_COLOR);
        accountLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        
        fromAccountField = new JTextField();
        fromAccountField.setEditable(false);
        fromAccountField.setBackground(CARD_COLOR);
        fromAccountField.setBorder(null);
        fromAccountField.setFont(new Font("SansSerif", Font.BOLD, 14));
        fromAccountField.setForeground(TEXT_COLOR);
        fromAccountField.setHorizontalAlignment(JTextField.RIGHT);
        fromAccountField.setAlignmentX(Component.RIGHT_ALIGNMENT);
        fromAccountField.setMaximumSize(new Dimension(300, 30));
        
        accountPanel.add(accountLabel);
        accountPanel.add(fromAccountField);
        
        headerPanel.add(accountPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
    }
    
    private void createMainContent() {
        // Main content panel
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new GridBagLayout());
        mainContent.setBackground(BACKGROUND_COLOR);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(15, 15, 15, 15);
        gbc.fill = GridBagConstraints.BOTH;
        
        // Transfer options section
        JPanel transferOptionsPanel = createSectionPanel("Transfer Type");
        transferOptionsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        
        transferTypeGroup = new ButtonGroup();
        internalTransferRadio = new JRadioButton();
        externalTransferRadio = new JRadioButton();
        transferTypeGroup.add(internalTransferRadio);
        transferTypeGroup.add(externalTransferRadio);
        externalTransferRadio.setSelected(true);
        
        JPanel internalCard = createRadioCard("Between My Accounts", "Instant transfer with no fees", internalTransferRadio);
        JPanel externalCard = createRadioCard("To Another Person", "1-2 business days processing", externalTransferRadio);
        
        transferOptionsPanel.add(internalCard);
        transferOptionsPanel.add(externalCard);
        
        // Recipient section
        JPanel recipientPanel = createSectionPanel("Recipient Information");
        recipientPanel.setLayout(new BorderLayout());
        
        JPanel recipientContent = new JPanel();
        recipientContent.setLayout(new BoxLayout(recipientContent, BoxLayout.Y_AXIS));
        recipientContent.setOpaque(false);
        recipientContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel recipientLabel = new JLabel("Recipient Account ID");
        recipientLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        recipientLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel recipientInfoLabel = new JLabel("Enter account ID or scan QR code to identify the recipient");
        recipientInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        recipientInfoLabel.setForeground(LIGHT_TEXT_COLOR);
        recipientInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel accountInputPanel = new JPanel(new BorderLayout(10, 0));
        accountInputPanel.setOpaque(false);
        accountInputPanel.setMaximumSize(new Dimension(800, 40));
        accountInputPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        toAccountField = new JTextField();
        toAccountField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        toAccountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        buttonPanel.setOpaque(false);
        
        verifyButton = createActionButton("Verify", SECONDARY_COLOR);
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyRecipientAccount();
            }
        });
        
        scanQRButton = createActionButton("Scan QR", ACCENT_COLOR);
        scanQRButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openQRCodeScanner();
            }
        });
        
        buttonPanel.add(verifyButton);
        buttonPanel.add(scanQRButton);
        
        accountInputPanel.add(toAccountField, BorderLayout.CENTER);
        accountInputPanel.add(buttonPanel, BorderLayout.EAST);
        
        verificationStatusLabel = new JLabel("");
        verificationStatusLabel.setFont(new Font("SansSerif", Font.ITALIC, 13));
        verificationStatusLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        recipientContent.add(recipientLabel);
        recipientContent.add(Box.createVerticalStrut(5));
        recipientContent.add(recipientInfoLabel);
        recipientContent.add(Box.createVerticalStrut(15));
        recipientContent.add(accountInputPanel);
        recipientContent.add(Box.createVerticalStrut(10));
        recipientContent.add(verificationStatusLabel);
        
        recipientPanel.add(recipientContent, BorderLayout.CENTER);
        
        // Amount section
        JPanel amountPanel = createSectionPanel("Transfer Amount");
        amountPanel.setLayout(new BorderLayout());
        
        JPanel amountContent = new JPanel();
        amountContent.setLayout(new BoxLayout(amountContent, BoxLayout.Y_AXIS));
        amountContent.setOpaque(false);
        amountContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        amountLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel amountInfoLabel = new JLabel("Enter the amount you want to transfer");
        amountInfoLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        amountInfoLabel.setForeground(LIGHT_TEXT_COLOR);
        amountInfoLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        amountField = new JTextField();
        amountField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        amountField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        amountField.setMaximumSize(new Dimension(800, 40));
        amountField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Quick amount buttons
        JPanel quickAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        quickAmountPanel.setOpaque(false);
        quickAmountPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] amounts = {"$100", "$250", "$500", "$1000"};
        for (String amount : amounts) {
            JButton quickButton = new JButton(amount);
            quickButton.setBackground(new Color(240, 240, 245));
            quickButton.setForeground(TEXT_COLOR);
            quickButton.setFocusPainted(false);
            quickButton.setFont(new Font("SansSerif", Font.PLAIN, 12));
            quickButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
            ));
            
            final String amountValue = amount;
            quickButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    amountField.setText(amountValue.replace("$", ""));
                }
            });
            
            quickAmountPanel.add(quickButton);
        }
        
        // Submit button
        transferButton = new JButton("Transfer Funds");
        transferButton.setBackground(SECONDARY_COLOR);
        transferButton.setForeground(Color.WHITE);
        transferButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        transferButton.setFocusPainted(false);
        transferButton.setBorder(BorderFactory.createEmptyBorder(12, 20, 12, 20));
        transferButton.setMaximumSize(new Dimension(800, 50));
        transferButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        transferButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTransferSubmit();
            }
        });
        
        amountContent.add(amountLabel);
        amountContent.add(Box.createVerticalStrut(5));
        amountContent.add(amountInfoLabel);
        amountContent.add(Box.createVerticalStrut(15));
        amountContent.add(amountField);
        amountContent.add(Box.createVerticalStrut(10));
        amountContent.add(quickAmountPanel);
        amountContent.add(Box.createVerticalStrut(30));
        amountContent.add(transferButton);
        
        amountPanel.add(amountContent, BorderLayout.CENTER);
        
        // Transfer info panel
        JPanel infoPanel = createSectionPanel("Transfer Information");
        infoPanel.setLayout(new BorderLayout());
        
        JPanel infoContent = new JPanel();
        infoContent.setLayout(new BoxLayout(infoContent, BoxLayout.Y_AXIS));
        infoContent.setOpaque(false);
        infoContent.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Add transfer info items
        infoContent.add(createInfoItem("Secure Transfer", "All transfers are encrypted and secured with industry-standard protocols.", "security"));
        infoContent.add(Box.createVerticalStrut(20));
        infoContent.add(createInfoItem("Processing Time", "Internal transfers: Immediate\nExternal transfers: 1-2 business days", "time"));
        infoContent.add(Box.createVerticalStrut(20));
        infoContent.add(createInfoItem("Transfer Limits", "Daily limit: $10,000\nMonthly limit: $50,000", "limits"));
        infoContent.add(Box.createVerticalStrut(20));
        infoContent.add(createInfoItem("Need Help?", "Contact our support team at support@kob.com\nor call us at 1-800-KOB-BANK", "help"));
        
        infoPanel.add(infoContent, BorderLayout.CENTER);
        
        // Add panels to main content with GridBagLayout
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.3;
        mainContent.add(transferOptionsPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.7;
        gbc.weighty = 0.35;
        mainContent.add(recipientPanel, gbc);
        
        gbc.gridx = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;
        mainContent.add(infoPanel, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        gbc.weightx = 1.0;
        gbc.weighty = 0.35;
        mainContent.add(amountPanel, gbc);
        
        // Add main content to frame with scroll pane
        add(new JScrollPane(mainContent), BorderLayout.CENTER);
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
    
    private JPanel createRadioCard(String title, String description, JRadioButton radio) {
        // Use same RoundedPanel as in Dashboard for consistency
        JPanel panel = new RoundedPanel(15, Color.WHITE);
        panel.setPreferredSize(new Dimension(250, 100));
        panel.setLayout(new BorderLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        radio.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setForeground(TEXT_COLOR);
        
        JLabel descLabel = new JLabel(description);
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        descLabel.setForeground(LIGHT_TEXT_COLOR);
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(descLabel);
        
        panel.add(radio, BorderLayout.WEST);
        panel.add(textPanel, BorderLayout.CENTER);
        
        // Add mouse listener to make the whole panel clickable
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                radio.setSelected(true);
            }
        });
        
        return panel;
    }
    
    private JButton createActionButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        
        return button;
    }
    
    private JPanel createInfoItem(String title, String content, String iconName) {
        JPanel panel = new JPanel(new BorderLayout(15, 0));
        panel.setOpaque(false);
        panel.setMaximumSize(new Dimension(300, 80));
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Try to load icon
        try {
            ImageIcon icon = new ImageIcon("icons/" + iconName + ".png");
            java.awt.Image image = icon.getImage().getScaledInstance(24, 24, java.awt.Image.SCALE_SMOOTH);
            icon = new ImageIcon(image);
            JLabel iconLabel = new JLabel(icon);
            iconLabel.setVerticalAlignment(JLabel.TOP);
            iconLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
            panel.add(iconLabel, BorderLayout.WEST);
        }
        // Add these methods to your Transfer class

        catch (Exception e) {
            System.err.println("Icon not found: " + iconName);
        }
        
        JPanel textPanel = new JPanel();
        textPanel.setLayout(new BoxLayout(textPanel, BoxLayout.Y_AXIS));
        textPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel contentLabel = new JLabel("<html>" + content.replace("\n", "<br>") + "</html>");
        contentLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        contentLabel.setForeground(LIGHT_TEXT_COLOR);
        contentLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        textPanel.add(titleLabel);
        textPanel.add(Box.createVerticalStrut(5));
        textPanel.add(contentLabel);
        
        panel.add(textPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Method to set user info
    public void setUserInfo(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        
        // Update window title and greeting
        setTitle("Transfer Funds - " + userName + " - Kurdish-O-Banking (KOB)");
        greeting.setText("Transfer Funds - " + userName);
        
        // Update account field
        if (fromAccountField != null) {
            fromAccountField.setText("Loading account information...");
            
            // Load actual account details in the background
            SwingUtilities.invokeLater(new Runnable() {
                @Override
                public void run() {
                    updateAccountInfo();
                }
            });
        }
    }
    
    // Method to update account information
    private void updateAccountInfo() {
        if (userId <= 0) {
            fromAccountField.setText("Invalid account information");
            return;
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT username, account_type, balance FROM accounts WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Get account details
                String username = rs.getString("username");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                
                // Update UI with account information
                String accountInfo = accountType + " Account - ID: " + userId + " - Balance: $" + String.format("%.2f", balance);
                fromAccountField.setText(accountInfo);
            } else {
                // Just show the account ID if details can't be loaded
                fromAccountField.setText("Account ID: " + userId);
            }
        } catch (SQLException e) {
            // On error, just show the account ID
            fromAccountField.setText("Account ID: " + userId);
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    /**
     * Open the QR code scanner window
     */
    private void openQRCodeScanner() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                QRCodeTransfer qrScanner = new QRCodeTransfer(Transfer.this, userName, userId);
                qrScanner.setVisible(true);
            }
        });
    }
    
    /**
     * Method called by QRCodeTransfer to set recipient info from QR code
     * @param recipientId The scanned account ID
     * @param recipientName The scanned username
     */
    public void setRecipientFromQRCode(int recipientId, String recipientName) {
        // Set the account field
        toAccountField.setText(String.valueOf(recipientId));
        
        // Set verification info
        verifiedRecipientName = recipientName;
        verificationStatusLabel.setText("✓ Verified from QR code: " + recipientName);
        verificationStatusLabel.setForeground(SUCCESS_COLOR);
        isVerified = true;
    }

    // Method to verify recipient account
    private void verifyRecipientAccount() {
        String toAccount = toAccountField.getText().trim();
        
        if (toAccount.isEmpty()) {
            verificationStatusLabel.setText("Please enter a destination account ID");
            verificationStatusLabel.setForeground(ERROR_COLOR);
            isVerified = false;
            return;
        }
        
        // Validate that the account ID is a number
        int destinationAccountId;
        try {
            destinationAccountId = Integer.parseInt(toAccount);
        } catch (NumberFormatException e) {
            verificationStatusLabel.setText("Invalid account ID (should be a number)");
            verificationStatusLabel.setForeground(ERROR_COLOR);
            isVerified = false;
            return;
        }
        
        // Make sure we don't transfer to the same account
        if (userId == destinationAccountId) {
            verificationStatusLabel.setText("Cannot transfer to your own account");
            verificationStatusLabel.setForeground(ERROR_COLOR);
            isVerified = false;
            return;
        }
        
        // Check if the destination account exists
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            
            String checkAccountQuery = "SELECT username FROM accounts WHERE account_id = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkAccountQuery);
            checkStmt.setInt(1, destinationAccountId);
            ResultSet rs = checkStmt.executeQuery();
            
            if (rs.next()) {
                String recipientName = rs.getString("username");
                verifiedRecipientName = recipientName;
                verificationStatusLabel.setText("✓ Verified: " + recipientName);
                verificationStatusLabel.setForeground(SUCCESS_COLOR);
                isVerified = true;
            } else {
                verificationStatusLabel.setText("❌ Account not found");
                verificationStatusLabel.setForeground(ERROR_COLOR);
                isVerified = false;
            }
            
        } catch (SQLException e) {
            verificationStatusLabel.setText("Error verifying account: " + e.getMessage());
            verificationStatusLabel.setForeground(ERROR_COLOR);
            isVerified = false;
            e.printStackTrace();
        } finally {
            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Method to handle button clicks
    private void handleButtonClick(String buttonName) {
        System.out.println("Button clicked: " + buttonName);
        
        switch (buttonName) {
            case "Dashboard":
                // Go to Dashboard page
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Dashbord dashboard = new Dashbord();
                        dashboard.setUserInfo(userName, userId);
                        dashboard.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Balance":
                // Go to Balance page
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        BalancePage balancePage = new BalancePage(userName, userId);
                        balancePage.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Accounts":
                // Go to User Profile page
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        UserProfile userProfile = new UserProfile();
                        userProfile.setUserInfo(userName, userId);
                        userProfile.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Deposit":
                // Go to Deposit page
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Deposite depositScreen = new Deposite();
                        depositScreen.setUserInfo(userName, userId);
                        depositScreen.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Withdraw":
                // Go to Withdraw page
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Withdraw withdrawScreen = new Withdraw();
                        withdrawScreen.setUserInfo(userName, userId);
                        withdrawScreen.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Transactions":
                // Go to transactions page
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Transaction transactionScreen = new Transaction(userId, userName);
                        transactionScreen.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "Transfers":
                // Refresh current page
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Transfer transferScreen = new Transfer();
                        transferScreen.setUserInfo(userName, userId);
                        transferScreen.setVisible(true);
                        dispose();
                    }
                });
                break;
            case "QR Codes":
                // Show QR Codes tab on dashboard
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Dashbord dashboard = new Dashbord();
                        dashboard.setUserInfo(userName, userId);
                        dashboard.setVisible(true);
                        // Switch to QR Codes tab
                        // Assuming mainTabbedPane is accessible and has QR Codes at index 1
                        dispose();
                    }
                });
                break;
            case "Cards":
                // Cards feature not implemented yet
                JOptionPane.showMessageDialog(this, "Cards feature coming soon!", "Information", JOptionPane.INFORMATION_MESSAGE);
                break;
            default:
                break;
        }
    }

    // Method to handle transfer submission
    private void handleTransferSubmit() {
        String amount = amountField.getText().trim();
        String toAccount = toAccountField.getText().trim();
        
        if (amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount to transfer", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (toAccount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a destination account ID", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (!isVerified) {
            JOptionPane.showMessageDialog(this, 
                    "Please verify the recipient account first by clicking the 'Verify' button or using QR code", 
                    "Verification Required", 
                    JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Validate that the account ID is a number
        int destinationAccountId;
        try {
            destinationAccountId = Integer.parseInt(toAccount);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, 
                    "Please enter a valid account ID (should be a number)", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double transferAmount = Double.parseDouble(amount);
            
            if (transferAmount <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Show transfer confirmation dialog
            showTransferConfirmation(transferAmount, destinationAccountId, verifiedRecipientName);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Show custom transfer confirmation dialog
    private void showTransferConfirmation(double amount, int destinationAccountId, String recipientName) {
        // Create a visually appealing confirmation dialog
        String message = String.format(
            "<html><div style='text-align: center;'>" +
            "<h2>Confirm Transfer</h2>" +
            "<p>You are about to transfer:</p>" +
            "<h3 style='color: #1E90FF;'>$%.2f</h3>" +
            "<p>To: <b>%s</b><br>Account ID: <b>%d</b></p>" +
            "<p>Please confirm this transaction.</p>" +
            "</div></html>",
            amount, recipientName, destinationAccountId
        );
        
        int choice = JOptionPane.showConfirmDialog(
            this,
            message,
            "Confirm Transfer",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (choice == JOptionPane.YES_OPTION) {
            boolean success = processDirectTransfer(amount, destinationAccountId);
            
            if (success) {
                String successMessage = String.format(
                    "<html><div style='text-align: center;'>" +
                    "<h2 style='color: #28A745;'>Transfer Successful!</h2>" +
                    "<p>You have transferred <b>$%.2f</b> to <b>%s</b></p>" +
                    "<p>Transaction ID: <b>%s</b></p>" +
                    "<p>A receipt has been sent to your email.</p>" +
                    "</div></html>",
                    amount, recipientName, generateTransactionId()
                );
                
                JOptionPane.showMessageDialog(
                    this,
                    successMessage,
                    "Transfer Complete",
                    JOptionPane.INFORMATION_MESSAGE
                );
                
                // Navigate back to dashboard
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        Dashbord dashboard = new Dashbord();
                        dashboard.setUserInfo(userName, userId);
                        dashboard.setVisible(true);
                        dispose();
                    }
                });
            }
        }
    }
    
    // Generate a random transaction ID
    private String generateTransactionId() {
        return "TRX" + System.currentTimeMillis() % 10000000;
    }
    
    // Method to process a direct transfer (not pending)
    private boolean processDirectTransfer(double amount, int destinationAccountId) {
        Connection conn = null;
        
        try {
            System.out.println("==== Starting direct transfer operation ====");
            System.out.println("Source account ID: " + userId);
            System.out.println("Destination account ID: " + destinationAccountId);
            System.out.println("Transfer amount: $" + amount);
            
            conn = DatabaseConnection.getConnection();
            // Begin transaction
            conn.setAutoCommit(false);
            
            // Check source account balance
            String balanceQuery = "SELECT balance FROM accounts WHERE account_id = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
            balanceStmt.setInt(1, userId);
            ResultSet balanceRs = balanceStmt.executeQuery();
            
            if (balanceRs.next()) {
                double currentBalance = balanceRs.getDouble("balance");
                System.out.println("Current balance: $" + currentBalance);
                
                if (currentBalance < amount) {
                    System.out.println("ERROR: Insufficient funds");
                    JOptionPane.showMessageDialog(this, "Insufficient funds. Current balance: $" + String.format("%.2f", currentBalance), "Error", JOptionPane.ERROR_MESSAGE);
                    conn.rollback();
                    return false;
                }
            } else {
                System.out.println("ERROR: Source account not found");
                conn.rollback();
                return false;
            }
            
            // Deduct amount from source account
            String deductQuery = "UPDATE accounts SET balance = balance - ? WHERE account_id = ?";
            PreparedStatement deductStmt = conn.prepareStatement(deductQuery);
            deductStmt.setDouble(1, amount);
            deductStmt.setInt(2, userId);
            int deductResult = deductStmt.executeUpdate();
            
            if (deductResult != 1) {
                System.out.println("ERROR: Failed to deduct from source account");
                conn.rollback();
                return false;
            }
            
            // Add amount to destination account
            String addQuery = "UPDATE accounts SET balance = balance + ? WHERE account_id = ?";
            PreparedStatement addStmt = conn.prepareStatement(addQuery);
            addStmt.setDouble(1, amount);
            addStmt.setInt(2, destinationAccountId);
            int addResult = addStmt.executeUpdate();
            
            if (addResult != 1) {
                System.out.println("ERROR: Failed to add to destination account");
                conn.rollback();
                return false;
            }
            
            // Record transaction for source account (negative amount)
            recordTransaction(conn, userId, "Transfer", -amount, 
                    "Transfer to account ID " + destinationAccountId, "APPROVED");
            
            // Record transaction for destination account (positive amount)
            recordTransaction(conn, destinationAccountId, "Transfer", amount, 
                    "Transfer from account ID " + userId, "APPROVED");
            
            // Commit transaction
            conn.commit();
            System.out.println("Transfer completed successfully");
            return true;
            
        } catch (SQLException e) {
            System.out.println("ERROR: Database exception: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    
    // Helper method to record a transaction
    private void recordTransaction(Connection conn, int accountId, String type, double amount, 
                                  String description, String status) throws SQLException {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String currentDate = sdf.format(new Date());
        
        String sql = "INSERT INTO transactions (account_id, transaction_type, amount, transaction_date, description, status) " +
                    "VALUES (?, ?, ?, ?, ?, ?)";
        
        PreparedStatement stmt = conn.prepareStatement(sql);
        stmt.setInt(1, accountId);
        stmt.setString(2, type);
        stmt.setDouble(3, amount);
        stmt.setString(4, currentDate);
        stmt.setString(5, description);
        stmt.setString(6, status);
        
        stmt.executeUpdate();
    }
    
    // Custom rounded panel class for card-like components - same as in Dashbord
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
    
    // Main method for testing
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                Transfer transfer = new Transfer();
                transfer.setUserInfo("John Doe", 12345);
                transfer.setVisible(true);
            }
        });
    }}