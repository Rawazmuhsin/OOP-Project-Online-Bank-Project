package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class BalancePage extends JFrame {
    
    private JLabel balanceLabel;
    private JLabel availableBalanceLabel;
    private JComboBox<Account> accountComboBox;
    private List<Account> userAccounts;
    private String userName;
    private int userId;

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
        setTitle("Account Balance - Kurdish - O - Banking (KOB)");
        setSize(950, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = createSidebar();
        add(sidebar, BorderLayout.WEST);

        // Main content panel
        JPanel content = new JPanel();
        content.setLayout(null);
        content.setBackground(new Color(245, 247, 251));
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel pageTitle = new JLabel("Account Balance");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        pageTitle.setBounds(20, 20, 300, 30);
        content.add(pageTitle);

        JLabel updatedTime = new JLabel("Last updated: Today, 10:15 AM");
        updatedTime.setFont(new Font("SansSerif", Font.PLAIN, 13));
        updatedTime.setForeground(Color.GRAY);
        updatedTime.setBounds(20, 55, 300, 20);
        content.add(updatedTime);

        // Account dropdown - now with real data
        JPanel accountSelectPanel = new JPanel(new BorderLayout());
        accountSelectPanel.setBounds(20, 90, 500, 45);
        accountSelectPanel.setBackground(new Color(250, 250, 250));
        accountSelectPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        
        // Get accounts from database
        userAccounts = fetchUserAccounts();
        
        accountComboBox = new JComboBox<>();
        DefaultComboBoxModel<Account> accountModel = new DefaultComboBoxModel<>();
        
        if (!userAccounts.isEmpty()) {
            for (Account account : userAccounts) {
                accountModel.addElement(account);
            }
            accountComboBox.setModel(accountModel);
            accountComboBox.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    updateBalanceDisplay();
                }
            });
        } else {
            accountModel.addElement(new Account(0, "No accounts found", "Unknown", 0.0));
            accountComboBox.setModel(accountModel);
        }
        
        accountSelectPanel.add(accountComboBox, BorderLayout.CENTER);
        content.add(accountSelectPanel);
        
        // Current Balance box - now with real data
        JPanel currentBalancePanel = createBalanceBox(
                "CURRENT BALANCE", "$0.00", "Includes pending transactions",
                new Color(230, 245, 255), new Color(30, 90, 140)
        );
        currentBalancePanel.setBounds(20, 150, 500, 140);
        content.add(currentBalancePanel);
        balanceLabel = (JLabel) ((JPanel) currentBalancePanel.getComponent(2)).getComponent(0);

        // Available Balance box - now with real data
        JPanel availableBalancePanel = createBalanceBox(
                "AVAILABLE BALANCE", "$0.00", "Immediately accessible funds",
                new Color(235, 255, 240), new Color(0, 110, 40)
        );
        availableBalancePanel.setBounds(20, 300, 500, 140);
        content.add(availableBalancePanel);
        availableBalanceLabel = (JLabel) ((JPanel) availableBalancePanel.getComponent(2)).getComponent(0);

        // Update balance display if we have accounts
        if (!userAccounts.isEmpty()) {
            updateBalanceDisplay();
        }

        // Quick Actions
        JLabel quickActionLabel = new JLabel("Quick Actions");
        quickActionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        quickActionLabel.setBounds(20, 460, 150, 20);
        content.add(quickActionLabel);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setBounds(20, 490, 600, 40);
        String[] actionLabels = {"Details", "Statement", "History", "Export"};
        for (String label : actionLabels) {
            JButton actionBtn = new JButton(label);
            actionBtn.setBackground(new Color(240, 245, 255));
            actionBtn.setForeground(new Color(40, 80, 200));
            actionBtn.setFocusPainted(false);
            actionBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
            actionBtn.setPreferredSize(new Dimension(100, 35));
            actionsPanel.add(actionBtn);
        }
        content.add(actionsPanel);

        add(content, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(20, 25, 45));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel titleLabel = new JLabel(" Kurdish - O - Banking");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.add(titleLabel);

        String[] menuItems = {"Balance", "Dashboard", "Accounts", "Deposit", "Transfer", "Withdraw"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(item.equals("Balance") ? new Color(40, 45, 65) : new Color(20, 25, 45));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            button.setBorder(new EmptyBorder(10, 20, 10, 10));
            
            // Add action listener to handle navigation
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleButtonClick(item);
                }
            });
            
            sidebar.add(button);
        }
        
        return sidebar;
    }
    
    private JPanel createBalanceBox(String title, String amount, String subtext, Color bg, Color textColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(textColor);
        panel.add(label);
        
        panel.add(Box.createVerticalStrut(10));
        
        JPanel balancePanel = new JPanel();
        balancePanel.setLayout(new BoxLayout(balancePanel, BoxLayout.X_AXIS));
        balancePanel.setOpaque(false);
        
        JLabel balance = new JLabel(amount);
        balance.setFont(new Font("SansSerif", Font.BOLD, 32));
        balance.setForeground(textColor);
        balancePanel.add(balance);
        
        panel.add(balancePanel);
        panel.add(Box.createVerticalStrut(5));
        
        JLabel sub = new JLabel(subtext);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);
        panel.add(sub);
        
        return panel;
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
            JOptionPane.showMessageDialog(this, 
                    "Database error: " + e.getMessage(), 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
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
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(BalancePage::new);
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
            case "Transfer":
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
            default:
                JOptionPane.showMessageDialog(this, buttonName + " page coming soon!");
                break;
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
}