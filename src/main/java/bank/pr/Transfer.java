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
    private int accountId = 10; // Default source account ID (Rawaz's account)
    private String userName = "Rawaz.muhsin"; // Default user name

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
        internal.setSelected(true);

        transferTypePanel.add(wrapRadioPanel("Between My Accounts", "Instant transfer", internal));
        transferTypePanel.add(wrapRadioPanel("To Another Person", "1–2 business days", external));

        content.add(transferTypePanel);
        content.add(Box.createVerticalStrut(30));

        // From Account
        JLabel fromLabel = new JLabel("From Account");
        fromLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        fromAccountField = new JTextField("Checking - Account ID: " + accountId);
        fromAccountField.setPreferredSize(new Dimension(300, 30));
        fromAccountField.setEditable(false);
        fromAccountField.setBackground(Color.WHITE);

        content.add(fromLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(fromAccountField);
        content.add(Box.createVerticalStrut(20));

        // To Account
        JLabel toLabel = new JLabel("To Account (Enter recipient's account ID)");
        toLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        toAccountField = new JTextField();
        toAccountField.setPreferredSize(new Dimension(300, 30));

        content.add(toLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(toAccountField);
        content.add(Box.createVerticalStrut(20));

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
        
        // Update from account field with the account ID
        if (fromAccountField != null) {
            fromAccountField.setText("Checking - Account ID: " + accountId);
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
            
            boolean success = createPendingTransfer(transferAmount, destinationAccountId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                        "Transfer of $" + transferAmount + " to account ID " + destinationAccountId + " has been submitted and is pending approval.",
                        "Transfer Pending", 
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
                        "Failed to process transfer. Please verify the destination account ID and try again.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for the amount", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Method to create a pending transfer in the transactions table
    private boolean createPendingTransfer(double amount, int destinationAccountId) {
        Connection conn = null;
        
        try {
            System.out.println("==== Starting transfer operation ====");
            System.out.println("Source account ID: " + accountId);
            System.out.println("Destination account ID: " + destinationAccountId);
            System.out.println("Transfer amount: $" + amount);
            
            // Make sure we don't transfer to the same account
            if (accountId == destinationAccountId) {
                System.out.println("ERROR: Cannot transfer to the same account");
                JOptionPane.showMessageDialog(this, "Cannot transfer to the same account", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            conn = DatabaseConnection.getConnection();
            System.out.println("Connected to database");
            
            // Verify that both accounts exist
            String checkAccountsQuery = "SELECT account_id FROM accounts WHERE account_id = ?";
            PreparedStatement checkSourceStmt = conn.prepareStatement(checkAccountsQuery);
            checkSourceStmt.setInt(1, accountId);
            ResultSet sourceRs = checkSourceStmt.executeQuery();
            
            if (!sourceRs.next()) {
                System.out.println("ERROR: Source account not found");
                return false;
            }
            
            PreparedStatement checkDestStmt = conn.prepareStatement(checkAccountsQuery);
            checkDestStmt.setInt(1, destinationAccountId);
            ResultSet destRs = checkDestStmt.executeQuery();
            
            if (!destRs.next()) {
                System.out.println("ERROR: Destination account not found");
                return false;
            }
            
            // Check if current account has sufficient funds
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
            }
            
            // Create a pending transaction
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String currentDate = sdf.format(new Date());
            
            // Insert into transactions table
            String transactionSql = "INSERT INTO transactions (account_id, transaction_type, amount, transaction_date, description, status) " +
                               "VALUES (?, ?, ?, ?, ?, ?)";
                               
            PreparedStatement transactionStmt = conn.prepareStatement(transactionSql);
            transactionStmt.setInt(1, accountId);
            transactionStmt.setString(2, "TRANSFER");
            transactionStmt.setDouble(3, amount);
            transactionStmt.setString(4, currentDate);
            transactionStmt.setString(5, "Transfer to account ID " + destinationAccountId);
            transactionStmt.setString(6, TransactionStatus.PENDING);
            
            int rowsAffected = transactionStmt.executeUpdate();
            System.out.println("Transaction inserted, rows affected: " + rowsAffected);
            
            // Store destination account ID in the description
            // This way the ApproveTransaction class can extract it when needed
            
            if (rowsAffected > 0) {
                return true;
            } else {
                System.out.println("ERROR: Failed to insert transaction");
                return false;
            }
            
        } catch (SQLException e) {
            System.out.println("ERROR: Database exception: " + e.getMessage());
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
        SwingUtilities.invokeLater(Transfer::new);
    }
}