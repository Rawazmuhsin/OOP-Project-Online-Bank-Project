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
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Withdraw extends JFrame {
    private JTextField amountField;
    private JTextArea descArea;
    private JPanel quickButtons;
    private int accountId; // Remove default - will be set from Dashboard
    private String userName; // Remove default - will be set from Dashboard
    private JLabel currentAccountLabel; // Added to show current account
    private JLabel currentBalanceLabel; // Added to show current balance

    public Withdraw() {
        setTitle("Withdraw Funds -  Kurdish - O - Banking (KOB)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(20, 25, 45));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel titleLabel = new JLabel("  Kurdish - O - Banking");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] menuItems = {"Withdraw", "Dashboard", "Accounts", "Deposit", "Transfer", "Transactions"};

        sidebar.add(titleLabel);
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(item.equals("Withdraw") ? new Color(40, 45, 65) : new Color(20, 25, 45));
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

        // Main content
        JPanel content = new JPanel();
        content.setBackground(new Color(245, 247, 251));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Withdraw Funds");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Withdraw money from your account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        content.add(title);
        content.add(Box.createVerticalStrut(5));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(15));
        
        // Add current account ID and balance display
        currentAccountLabel = new JLabel("Current Account ID: " + accountId);
        currentAccountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        currentAccountLabel.setForeground(new Color(44, 62, 80));
        content.add(currentAccountLabel);
        
        currentBalanceLabel = new JLabel("Available Balance: Loading...");
        currentBalanceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        currentBalanceLabel.setForeground(new Color(44, 62, 80));
        content.add(currentBalanceLabel);
        content.add(Box.createVerticalStrut(15));

        // Withdraw Method
        JPanel methodPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        methodPanel.setOpaque(false);

        JRadioButton bankTransfer = new JRadioButton();
        JRadioButton card = new JRadioButton();

        ButtonGroup methodGroup = new ButtonGroup();
        methodGroup.add(bankTransfer);
        methodGroup.add(card);
        bankTransfer.setSelected(true);

        methodPanel.add(wrapRadioPanel("Bank Account", "2–3 business days", bankTransfer));
        methodPanel.add(wrapRadioPanel("Debit Card", "Instant withdrawal", card));

        content.add(methodPanel);
        content.add(Box.createVerticalStrut(30));

        // Amount Input
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(200, 30));

        content.add(amountLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(amountField);
        content.add(Box.createVerticalStrut(10));

        // Quick Amount Buttons
        JLabel quickLabel = new JLabel("Quick amounts:");
        quickLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        quickButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quickButtons.setOpaque(false);

        String[] quicks = {"$100", "$250", "$500", "$1000"};
        for (String amt : quicks) {
            JButton btn = new JButton(amt);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(220, 255, 230));
            btn.setForeground(new Color(0, 128, 0));
            
            // Add action listener to quick amount buttons
            btn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    amountField.setText(amt.replace("$", ""));
                }
            });
            
            quickButtons.add(btn);
        }

        content.add(quickLabel);
        content.add(quickButtons);
        content.add(Box.createVerticalStrut(20));

        // Description
        JLabel descLabel = new JLabel("Description (Optional)");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descArea = new JTextArea(3, 30);
        descArea.setLineWrap(true);
        descArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        content.add(descLabel);
        content.add(descArea);
        content.add(Box.createVerticalStrut(30));

        // Submit Button
        JButton submitBtn = new JButton("Withdraw Funds");
        submitBtn.setBackground(new Color(60, 130, 255));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setPreferredSize(new Dimension(300, 40));
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Add action listener to submit button
        submitBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleWithdrawSubmit();
            }
        });

        content.add(submitBtn);

        // Add to Frame
        add(sidebar, BorderLayout.WEST);
        add(new JScrollPane(content), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel wrapRadioPanel(String title, String subtitle, JRadioButton button) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 255)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);
        panel.setPreferredSize(new Dimension(200, 70));

        button.setText("<html><b>" + title + "</b><br><span style='font-size:10px;color:gray'>" + subtitle + "</span></html>");
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setFont(new Font("SansSerif", Font.PLAIN, 13));
        panel.add(button, BorderLayout.CENTER);
        return panel;
    }
    
    // Method to set user info
    public void setUserInfo(String userName, int accountId) {
        this.userName = userName;
        this.accountId = accountId;
        
        // Update the account ID label when user info is set
        if (currentAccountLabel != null) {
            currentAccountLabel.setText("Current Account ID: " + accountId);
        }
        
        // Load and display current balance
        loadCurrentBalance();
        
        System.out.println("Withdraw: Set user info - User: " + userName + ", Account ID: " + accountId);
    }
    
    // Method to load current balance
    private void loadCurrentBalance() {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT balance FROM accounts WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, accountId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                double balance = rs.getDouble("balance");
                currentBalanceLabel.setText(String.format("Available Balance: $%.2f", balance));
            } else {
                currentBalanceLabel.setText("Available Balance: Not found");
            }
            
            rs.close();
            stmt.close();
            
        } catch (SQLException ex) {
            currentBalanceLabel.setText("Available Balance: Error loading");
            System.out.println("Error loading balance: " + ex.getMessage());
            ex.printStackTrace();
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
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, accountId);
                    dashboard.setVisible(true);
                    this.dispose();
                });
                break;
            case "Deposit":
                SwingUtilities.invokeLater(() -> {
                    Deposite depositScreen = new Deposite();
                    depositScreen.setUserInfo(userName, accountId);
                    depositScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Withdraw":
                // Stay on withdraw page
                break;
            case "Transfer":
                SwingUtilities.invokeLater(() -> {
                    Transfer transferScreen = new Transfer();
                    transferScreen.setUserInfo(userName, accountId);
                    transferScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transactions":
                SwingUtilities.invokeLater(() -> {
                    Transaction transactionScreen = new Transaction(accountId, userName);
                    transactionScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Accounts":
                SwingUtilities.invokeLater(() -> {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setUserInfo(userName, accountId);
                    userProfile.setVisible(true);
                    this.dispose();
                });
                break;
            default:
                break;
        }
    }
    
    // Method to handle withdraw submission
    private void handleWithdrawSubmit() {
        String amount = amountField.getText();
        String description = descArea.getText();
        
        if (amount.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter an amount to withdraw", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double amountValue = Double.parseDouble(amount);
            if (amountValue <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive amount", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            boolean success = saveTransaction(amountValue, description);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Withdrawal of $" + amountValue + " has been submitted and is pending approval.", 
                        "Withdrawal Pending", 
                        JOptionPane.INFORMATION_MESSAGE);
                
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, accountId);
                    dashboard.setVisible(true);
                    this.dispose();
                });
            } else {
                JOptionPane.showMessageDialog(this, 
                        "Failed to process withdrawal. Please try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to save transaction
    private boolean saveTransaction(double amount, String description) {
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = sdf.format(new Date());
            
            System.out.println("Creating withdrawal transaction:");
            System.out.println("Account ID: " + accountId);
            System.out.println("Amount: $" + amount);
            System.out.println("Date: " + currentDate);
            System.out.println("Description: " + description);

            // Check if account has sufficient funds
            String balanceQuery = "SELECT balance FROM accounts WHERE account_id = ?";
            PreparedStatement balanceStmt = conn.prepareStatement(balanceQuery);
            balanceStmt.setInt(1, accountId);
            ResultSet balanceRs = balanceStmt.executeQuery();
            
            if (balanceRs.next()) {
                double currentBalance = balanceRs.getDouble("balance");
                System.out.println("Current balance: $" + currentBalance);
                
                if (currentBalance < amount) {
                    System.out.println("ERROR: Insufficient funds");
                    JOptionPane.showMessageDialog(this, "Insufficient funds. Current balance: $" + currentBalance, "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            } else {
                System.out.println("ERROR: Account not found");
                return false;
            }

            // Insert the transaction with pending status
            String sql = "INSERT INTO transactions (account_id, transaction_type, amount, transaction_date, description, status) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, accountId);
            pstmt.setString(2, "Withdrawal");
            pstmt.setDouble(3, amount);
            pstmt.setString(4, currentDate);
            pstmt.setString(5, description);
            pstmt.setString(6, TransactionStatus.PENDING);

            int rowsAffected = pstmt.executeUpdate();
            pstmt.close();
            
            System.out.println("Transaction saved, rows affected: " + rowsAffected);
            return rowsAffected > 0;

        } catch (SQLException e) {
            System.out.println("Error saving transaction: " + e.getMessage());
            e.printStackTrace();
            return false;
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Withdraw::new);
    }
}