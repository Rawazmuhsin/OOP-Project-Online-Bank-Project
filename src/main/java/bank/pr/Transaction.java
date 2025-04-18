package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class Transaction extends JFrame {
    private int userId;
    private String userName;

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
        
        initializeUI();
    }

    private void initializeUI() {
        setTitle(" Kurdish - O - Banking (KOB) - Transactions");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createTransactionsContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(26, 32, 44)); // #1a202c
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(250, 800));
        sidebarPanel.setLayout(null);

        // Sidebar title
        JLabel titleLabel = new JLabel("Kurdish - O - Banking");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 200, 30);
        sidebarPanel.add(titleLabel);

        // Active sidebar button (Transactions)
        RoundedButton transactionsButton = new RoundedButton("Transactions", 5);
        transactionsButton.setBackground(new Color(52, 58, 64)); // #343a40
        transactionsButton.setForeground(Color.WHITE);
        transactionsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        transactionsButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        transactionsButton.setBounds(20, 100, 210, 40);
        transactionsButton.setHorizontalAlignment(SwingConstants.LEFT);
        sidebarPanel.add(transactionsButton);

        // Other sidebar buttons
        String[] menuItems = {"Dashboard", "Accounts", "Deposit", "Transfer", "Withdraw"};
        int yPos = 180;
        for (String item : menuItems) {
            JLabel menuLabel = new JLabel(item);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setBounds(60, yPos, 200, 30);
            
            // Make labels clickable like buttons
            menuLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            menuLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    handleMenuItemClick(item);
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

    private void handleMenuItemClick(String menuItem) {
        switch (menuItem) {
            case "Dashboard":
                SwingUtilities.invokeLater(() -> {
                    Dashbord dashboard = new Dashbord();
                    dashboard.setUserInfo(userName, userId);
                    dashboard.setVisible(true);
                    this.dispose();
                });
                break;
            case "Withdraw":
                SwingUtilities.invokeLater(() -> {
                    Withdraw withdrawScreen = new Withdraw();
                    withdrawScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Deposit":
                SwingUtilities.invokeLater(() -> {
                    Deposite depositScreen = new Deposite();
                    depositScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Transfer":
                SwingUtilities.invokeLater(() -> {
                    Transfer transferScreen = new Transfer();
                    transferScreen.setVisible(true);
                    this.dispose();
                });
                break;
            case "Accounts":
                JOptionPane.showMessageDialog(this, "Accounts functionality coming soon!");
                break;
            default:
                JOptionPane.showMessageDialog(this, menuItem + " functionality coming soon!");
                break;
        }
    }

    private JPanel createTransactionsContentPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(240, 244, 248)); // #f0f4f8
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // Main content area (white rounded rectangle)
        RoundedPanel contentPanel = new RoundedPanel(10);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(20, 20, 530, 740);
        contentPanel.setLayout(null);
        mainPanel.add(contentPanel);

        // Transaction History Header
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64)); // #343a40
        titleLabel.setBounds(30, 30, 300, 30);
        contentPanel.add(titleLabel);

        // Create table headers
        RoundedPanel headerPanel = new RoundedPanel(5);
        headerPanel.setBackground(new Color(248, 250, 252)); // #f8fafc
        headerPanel.setBounds(20, 100, 490, 30);
        headerPanel.setLayout(new GridLayout(1, 5));
        contentPanel.add(headerPanel);

        String[] headers = {"DATE", "DESCRIPTION", "CATEGORY", "AMOUNT", "BALANCE"};
        for (String header : headers) {
            JLabel headerLabel = new JLabel(header);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerLabel.setForeground(new Color(52, 58, 64)); // #343a40
            headerLabel.setHorizontalAlignment(SwingConstants.LEFT);
            headerPanel.add(headerLabel);
        }

        // Load transactions from database
        ArrayList<TransactionItem> transactions = loadTransactionsFromDatabase();
        
        // Display transactions
        displayTransactions(contentPanel, transactions);

        return mainPanel;
    }
    
    private ArrayList<TransactionItem> loadTransactionsFromDatabase() {
        ArrayList<TransactionItem> transactions = new ArrayList<>();
        
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
    
    private void displayTransactions(JPanel contentPanel, ArrayList<TransactionItem> transactions) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
        int yPos = 140;
        
        for (int i = 0; i < transactions.size(); i++) {
            TransactionItem transaction = transactions.get(i);
            
            RoundedPanel rowPanel = new RoundedPanel(5);
            rowPanel.setBackground(i % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)); // Alternate row colors
            rowPanel.setBounds(20, yPos, 490, 30);
            rowPanel.setLayout(new GridLayout(1, 5));
            contentPanel.add(rowPanel);

            // Format the transaction data
            String dateStr = dateFormat.format(transaction.date);
            String descriptionStr = transaction.description;
            String categoryStr = transaction.category;
            String amountStr = (transaction.amount >= 0 ? "+" : "") + String.format("$%.2f", transaction.amount);
            String balanceStr = String.format("$%.2f", transaction.balance);
            
            String[] rowData = {dateStr, descriptionStr, categoryStr, amountStr, balanceStr};
            
            for (int j = 0; j < rowData.length; j++) {
                JLabel cellLabel = new JLabel(rowData[j]);
                cellLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                
                // Set color for amount column
                if (j == 3) {
                    cellLabel.setForeground(transaction.amount >= 0 ? 
                        new Color(0, 128, 0) : // Green for positive
                        new Color(255, 0, 0));  // Red for negative
                } else {
                    cellLabel.setForeground(new Color(52, 58, 64)); // #343a40
                }
                
                cellLabel.setHorizontalAlignment(SwingConstants.LEFT);
                rowPanel.add(cellLabel);
            }
            
            yPos += 40;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Transaction gui = new Transaction(12345, "John Doe");
            gui.setVisible(true);
        });
    }

    // Custom Rounded Panel class
    static class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int cornerRadius) {
            super();
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    // Custom Rounded Button class
    static class RoundedButton extends JButton {
        private int cornerRadius;

        public RoundedButton(String text, int cornerRadius) {
            super(text);
            this.cornerRadius = cornerRadius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}