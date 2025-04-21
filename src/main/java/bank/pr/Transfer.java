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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Transfer extends JFrame {
    private JTextField amountField;
    private JTextField toAccountField;
    private JTextField fromAccountField;
    private JButton verifyButton;
    private JLabel verificationStatusLabel;
    private boolean isVerified = false;
    private String verifiedRecipientName = "";
    private int accountId = 0; // Will be set by setUserInfo
    private String userName = ""; // Will be set by setUserInfo

    public Transfer() {
        setTitle("Transfer Funds -  Kurdish - O - Banking (KOB)");
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

        String[] menuItems = {"Transfer", "Dashboard", "Accounts", "Deposit", "Withdraw", "Transactions"};
        
        sidebar.add(titleLabel);
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(item.equals("Transfer") ? new Color(40, 45, 65) : new Color(20, 25, 45));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));

            // Add action listener for navigation
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleNavigation(item);
                }
            });

            sidebar.add(button);
        }

        // Main content
        JPanel content = new JPanel();
        content.setBackground(new Color(245, 247, 251));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Transfer Funds");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Transfer money safely between your accounts or to others.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        content.add(title);
        content.add(Box.createVerticalStrut(5));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));

        // Transfer Type
        JPanel transferTypePanel = new JPanel(new GridLayout(1, 2, 20, 10));
        transferTypePanel.setOpaque(false);

        JRadioButton internal = new JRadioButton();
        JRadioButton external = new JRadioButton();
        ButtonGroup group = new ButtonGroup();
        group.add(internal);
        group.add(external);
        external.setSelected(true); // Default to external transfer

        transferTypePanel.add(wrapRadioPanel("Between My Accounts", "Instant transfer", internal));
        transferTypePanel.add(wrapRadioPanel("To Another Person", "1–2 business days", external));

        content.add(transferTypePanel);
        content.add(Box.createVerticalStrut(30));

        // From Account
        JLabel fromLabel = new JLabel("From Account");
        fromLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        fromAccountField = new JTextField("");  // Will be set in setUserInfo
        fromAccountField.setPreferredSize(new Dimension(300, 30));
        fromAccountField.setEditable(false);
        fromAccountField.setBackground(Color.WHITE);

        content.add(fromLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(fromAccountField);
        content.add(Box.createVerticalStrut(20));

        // To Account with Verify Button
        JLabel toLabel = new JLabel("To Account (Enter recipient's account ID)");
        toLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        
        // Panel for account input and verify button
        JPanel accountInputPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        accountInputPanel.setOpaque(false);
        
        toAccountField = new JTextField();
        toAccountField.setPreferredSize(new Dimension(250, 30));
        
        verifyButton = new JButton("Verify");
        verifyButton.setBackground(new Color(60, 130, 180));
        verifyButton.setForeground(Color.WHITE);
        verifyButton.setFocusPainted(false);
        verifyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                verifyRecipientAccount();
            }
        });
        
        accountInputPanel.add(toAccountField);
        accountInputPanel.add(Box.createHorizontalStrut(10));
        accountInputPanel.add(verifyButton);
        
        // Verification status label
        verificationStatusLabel = new JLabel("");
        verificationStatusLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        
        content.add(toLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(accountInputPanel);
        content.add(Box.createVerticalStrut(5));
        content.add(verificationStatusLabel);
        content.add(Box.createVerticalStrut(15));

        // Amount Section
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(300, 30));

        content.add(amountLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(amountField);

        JPanel quickAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quickAmountPanel.setOpaque(false);
        String[] amounts = {"$100", "$250", "$500", "$1000"};

        for (String amount : amounts) {
            JButton quickBtn = new JButton(amount);
            quickBtn.setFocusPainted(false);
            quickBtn.setBackground(new Color(255, 230, 230));
            quickBtn.setForeground(new Color(180, 0, 0));

            // Add action listener for quick amount buttons
            quickBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    amountField.setText(amount.replace("$", ""));
                }
            });

            quickAmountPanel.add(quickBtn);
        }

        content.add(Box.createVerticalStrut(10));
        content.add(quickAmountPanel);
        content.add(Box.createVerticalStrut(30));

        // Submit Button
        JButton transferBtn = new JButton("Transfer Funds");
        transferBtn.setBackground(new Color(60, 130, 255));
        transferBtn.setForeground(Color.WHITE);
        transferBtn.setFocusPainted(false);
        transferBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        transferBtn.setPreferredSize(new Dimension(300, 40));

        // Add action listener for transfer button
        transferBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                handleTransferSubmit();
            }
        });

        content.add(transferBtn);

        // Frame Layout
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
        
        // Immediately update the from account field with the account ID
        if (fromAccountField != null) {
            fromAccountField.setText("Account ID: " + accountId);
            
            // Load actual account details in the background
            SwingUtilities.invokeLater(() -> {
                updateAccountInfo();
            });
        }
    }
    
    // Method to update account information
    private void updateAccountInfo() {
        if (accountId <= 0) {
            fromAccountField.setText("Invalid account information");
            return;
        }
        
        Connection conn = null;
        try {
            conn = DatabaseConnection.getConnection();
            String query = "SELECT username, account_type, balance FROM accounts WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, accountId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                // Get account details
                String username = rs.getString("username");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                
                // Update UI with account information
                String accountInfo = accountType + " - Account ID: " + accountId + " - Balance: $" + balance;
                fromAccountField.setText(accountInfo);
                
                // Update window title with username
                setTitle("Transfer Funds - " + username + " - Kurdish - O - Banking (KOB)");
            } else {
                // Just show the account ID if details can't be loaded
                fromAccountField.setText("Account ID: " + accountId);
            }
        } catch (SQLException e) {
            // On error, just show the account ID
            fromAccountField.setText("Account ID: " + accountId);
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

    // Method to verify recipient account
    private void verifyRecipientAccount() {
        String toAccount = toAccountField.getText().trim();
        
        if (toAccount.isEmpty()) {
            verificationStatusLabel.setText("Please enter a destination account ID");
            verificationStatusLabel.setForeground(Color.RED);
            isVerified = false;
            return;
        }
        
        // Validate that the account ID is a number
        int destinationAccountId;
        try {
            destinationAccountId = Integer.parseInt(toAccount);
        } catch (NumberFormatException e) {
            verificationStatusLabel.setText("Invalid account ID (should be a number)");
            verificationStatusLabel.setForeground(Color.RED);
            isVerified = false;
            return;
        }
        
        // Make sure we don't transfer to the same account
        if (accountId == destinationAccountId) {
            verificationStatusLabel.setText("Cannot transfer to your own account");
            verificationStatusLabel.setForeground(Color.RED);
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
                verificationStatusLabel.setForeground(new Color(0, 150, 0));
                isVerified = true;
            } else {
                verificationStatusLabel.setText("❌ Account not found");
                verificationStatusLabel.setForeground(Color.RED);
                isVerified = false;
            }
            
        } catch (SQLException e) {
            verificationStatusLabel.setText("Error verifying account: " + e.getMessage());
            verificationStatusLabel.setForeground(Color.RED);
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

    // Method to handle navigation
    private void handleNavigation(String destination) {
        this.dispose(); // Close current window

        // Open the selected page
        switch (destination) {
            case "Dashboard":
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, accountId);
                    dashboard.setVisible(true);
                });
                break;
            case "Deposit":
                SwingUtilities.invokeLater(() -> {
                    Deposite deposit = new Deposite();
                    deposit.setUserInfo(userName, accountId);
                    deposit.setVisible(true);
                });
                break;
            case "Withdraw":
                SwingUtilities.invokeLater(() -> {
                    Withdraw withdraw = new Withdraw();
                    withdraw.setUserInfo(userName, accountId);
                    withdraw.setVisible(true);
                });
                break;
            case "Transfer":
                // Already on this page, do nothing or refresh
                SwingUtilities.invokeLater(() -> {
                    Transfer transfer = new Transfer();
                    transfer.setUserInfo(userName, accountId);
                    transfer.setVisible(true);
                });
                break;
            case "Transactions":
                SwingUtilities.invokeLater(() -> {
                    Transaction transaction = new Transaction(accountId, userName);
                    transaction.setVisible(true);
                });
                break;
            case "Accounts":
                SwingUtilities.invokeLater(() -> {
                    UserProfile userProfile = new UserProfile();
                    userProfile.setUserInfo(userName, accountId);
                    userProfile.setVisible(true);
                });
                break;
            default:
                System.out.println("Navigation to " + destination + " not implemented");
                break;
        }
    }

    // Method to handle transfer submission
    private void handleTransferSubmit() {
        // Don't check for accountId <= 0, just use the ID that was passed in
        String amount = amountField.getText();
        String toAccount = toAccountField.getText();
        
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
                    "Please verify the recipient account first by clicking the 'Verify' button", 
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
            
            // Confirm transfer
            int confirmResult = JOptionPane.showConfirmDialog(this,
                    "Are you sure you want to transfer $" + transferAmount + " to " + verifiedRecipientName + 
                    " (Account ID: " + destinationAccountId + ")?",
                    "Confirm Transfer",
                    JOptionPane.YES_NO_OPTION);
            
            if (confirmResult == JOptionPane.YES_OPTION) {
                boolean success = processDirectTransfer(transferAmount, destinationAccountId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, 
                            "Transfer of $" + transferAmount + " to " + verifiedRecipientName + 
                            " (Account ID: " + destinationAccountId + ") completed successfully.",
                            "Transfer Complete", 
                            JOptionPane.INFORMATION_MESSAGE);
                    
                    // Navigate back to dashboard
                    SwingUtilities.invokeLater(() -> {
                        Dashbord dashboard = new Dashbord();
                        dashboard.setUserInfo(userName, accountId);
                        dashboard.setVisible(true);
                        this.dispose();
                    });
                } else {
                    JOptionPane.showMessageDialog(this, 
                            "Failed to process transfer. Please try again later.", 
                            "Error", 
                            JOptionPane.ERROR_MESSAGE);
                }
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to process a direct transfer (not pending)
    private boolean processDirectTransfer(double amount, int destinationAccountId) {
        Connection conn = null;
        
        try {
            System.out.println("==== Starting direct transfer operation ====");
            System.out.println("Source account ID: " + accountId);
            System.out.println("Destination account ID: " + destinationAccountId);
            System.out.println("Transfer amount: $" + amount);
            
            conn = DatabaseConnection.getConnection();
            // Begin transaction
            conn.setAutoCommit(false);
            
            // Check source account balance
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
            deductStmt.setInt(2, accountId);
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
            recordTransaction(conn, accountId, "Transfer", -amount, 
                    "Transfer to account ID " + destinationAccountId, "APPROVED");
            
            // Record transaction for destination account (positive amount)
            recordTransaction(conn, destinationAccountId, "Transfer", amount, 
                    "Transfer from account ID " + accountId, "APPROVED");
            
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Transfer::new);
    }
}