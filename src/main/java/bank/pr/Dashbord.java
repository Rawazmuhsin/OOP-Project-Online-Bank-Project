package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Dashbord extends JFrame {

    private JLabel greeting;
    private String userName;
    private int userId;
    private JLabel checkingBalanceLabel;
    private JLabel savingsBalanceLabel;
    private JLabel checkingAccountIdLabel;
    private JLabel savingsAccountIdLabel;

    public Dashbord() {
        setTitle(" Kurdish - O - Banking (KOB)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(20, 25, 45));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel titleLabel = new JLabel("  Kurdish - O - Banking");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        // Added "Balance" to the menu items
        String[] menuItems = {"Dashboard", "Balance", "Accounts", "Transactions", "Transfers", "Cards"};

        sidebar.add(titleLabel);
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(item.equals("Dashboard") ? new Color(40, 45, 65) : new Color(20, 25, 45));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
            
            // Add action listener to each button
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleButtonClick(item);
                }
            });
            
            sidebar.add(button);
        }

        // Main Content
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(245, 247, 251));

        // Default greeting (will be updated when setUserInfo is called)
        greeting = new JLabel("Hello, User! Last login: Now");
        greeting.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        greeting.setFont(new Font("SansSerif", Font.PLAIN, 14));
        content.add(greeting, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setBackground(new Color(245, 247, 251));

        // Create account cards with updated layout to include account ID
        JPanel checkingCard = createAccountCard("Checking - ****1234");
        checkingBalanceLabel = (JLabel) ((JPanel)checkingCard.getComponent(1)).getComponent(1);
        checkingAccountIdLabel = (JLabel) ((JPanel)checkingCard.getComponent(1)).getComponent(3);
        
        JPanel savingsCard = createAccountCard("Savings - ****5678");
        savingsBalanceLabel = (JLabel) ((JPanel)savingsCard.getComponent(1)).getComponent(1);
        savingsAccountIdLabel = (JLabel) ((JPanel)savingsCard.getComponent(1)).getComponent(3);

        cardsPanel.add(checkingCard);
        cardsPanel.add(savingsCard);

        content.add(cardsPanel, BorderLayout.CENTER);

        // Add to frame
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
    }

    private JPanel createAccountCard(String title) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(300, 150));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setLayout(new BorderLayout());

        JLabel accountLabel = new JLabel(title);
        accountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Create info panel for balance and account ID
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new GridLayout(2, 2, 5, 10));
        infoPanel.setBackground(Color.WHITE);
        
        // Balance section
        JLabel balanceTextLabel = new JLabel("Balance:");
        balanceTextLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel balanceLabel = new JLabel("$0.00");
        balanceLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        balanceLabel.setForeground(new Color(44, 62, 80));
        
        // Account ID section
        JLabel accountIdTextLabel = new JLabel("Account-ID:");
        accountIdTextLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JLabel accountIdLabel = new JLabel("?");
        accountIdLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        accountIdLabel.setForeground(new Color(44, 62, 80));
        
        // Add all to info panel
        infoPanel.add(balanceTextLabel);
        infoPanel.add(balanceLabel);
        infoPanel.add(accountIdTextLabel);
        infoPanel.add(accountIdLabel);

        card.add(accountLabel, BorderLayout.NORTH);
        card.add(infoPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    public void setUserInfo(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
        
        // Update greeting with user name and current time
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
        
        greeting.setText("Hello, " + userName + "! Last login: " + formattedDateTime);
        
        // Load and display account balances and account IDs
        loadAccountInfo();
    }
    
    private void loadAccountInfo() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT account_id, account_type, balance FROM accounts WHERE account_id = ? OR username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            stmt.setString(2, userName);
            
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                int accountId = rs.getInt("account_id");
                
                if ("Checking".equals(accountType)) {
                    checkingBalanceLabel.setText(String.format("$%.2f", balance));
                    checkingAccountIdLabel.setText(String.valueOf(accountId));
                } else if ("Savings".equals(accountType)) {
                    savingsBalanceLabel.setText(String.format("$%.2f", balance));
                    savingsAccountIdLabel.setText(String.valueOf(accountId));
                }
            }
            
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this,
                "Error loading account information: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // Method to handle button clicks
    private void handleButtonClick(String buttonName) {
        System.out.println("Button clicked: " + buttonName);
        
        switch (buttonName) {
            case "Dashboard":
                // Refresh dashboard
                loadAccountInfo();
                break;
            case "Balance":
                // Go to Balance page
                SwingUtilities.invokeLater(() -> {
                    BalancePage balancePage = new BalancePage();
                    balancePage.setVisible(true);
                    this.dispose(); // Close the current Dashboard window
                });
                break;
            case "Accounts":
                // Go to User Profile page
                SwingUtilities.invokeLater(() -> {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setUserInfo(userName, userId); // Pass user info to UserProfile
                    userProfile.setVisible(true);
                    this.dispose(); // Close the current Dashboard window
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
            case "Transfers":
                // Go to transfers page
                SwingUtilities.invokeLater(() -> {
                    Transfer transferScreen = new Transfer();
                    transferScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Cards":
                // Go to cards page
                JOptionPane.showMessageDialog(this, "Cards page coming soon!");
                break;
            default:
                break;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Dashbord dashboard = new Dashbord();
            dashboard.setUserInfo("John Doe", 12345);
            dashboard.setVisible(true);
        });
    }
}