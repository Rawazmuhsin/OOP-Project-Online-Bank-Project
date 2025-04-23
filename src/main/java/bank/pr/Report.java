package bank.pr;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;

public class Report extends JFrame {
    private String userName;
    private int accountId;
    private boolean isAdmin = true; // Set to true for admin version
    
    // Report data
    private double totalDeposits = 0;
    private double totalWithdrawals = 0;
    private double netBalance = 0;
    private int pendingTransactions = 0;
    private List<TransactionData> transactionList = new ArrayList<>();
    
    // UI Components
    private JComboBox<String> reportTypeComboBox;
    private JComboBox<String> dateRangeComboBox;
    private JLabel totalDepositsLabel;
    private JLabel totalWithdrawalsLabel;
    private JLabel netBalanceLabel;
    private JLabel pendingTransactionsLabel;
    
    // Date range options
    private final String[] DATE_RANGES = {
        "Last 7 Days", "Last 30 Days", "Last 90 Days", "Year to Date", "Custom Range"
    };
    
    // Report type options
    private final String[] REPORT_TYPES = {
        "Transaction Summary", "Account Activity", "Pending Approvals", "User Activity", "System Overview"
    };
    
    // Class to hold transaction data
    private class TransactionData {
        int transactionId;
        int accountId;
        String userName;
        String transactionType;
        double amount;
        String date;
        String status;
        String description;
        
        TransactionData(int transactionId, int accountId, String userName, String transactionType, 
                       double amount, String date, String status, String description) {
            this.transactionId = transactionId;
            this.accountId = accountId;
            this.userName = userName;
            this.transactionType = transactionType;
            this.amount = amount;
            this.date = date;
            this.status = status;
            this.description = description;
        }
    }
    
    // Constructor
    public Report() {
        this("Admin", 0); // Default constructor with admin values
    }
    
    public Report(String userName, int accountId) {
        this.userName = userName;
        this.accountId = accountId;
        
        initializeUI();
        loadReportData(); // Load initial data
    }
    
    private void initializeUI() {
        setTitle("KOB Admin - Reports Dashboard");
        setSize(900, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with custom painting
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2d.setColor(new Color(240, 244, 248));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Sidebar Background
                g2d.setColor(new Color(26, 32, 44));
                g2d.fillRect(0, 0, 250, getHeight());
                
                // Header: Bank Admin
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.BOLD, 18));
                g2d.drawString("KOB Admin Panel", 60, 70);
                
                // Admin badge
                g2d.setColor(new Color(255, 193, 7));
                g2d.fillRoundRect(60, 80, 60, 20, 10, 10);
                g2d.setColor(Color.BLACK);
                g2d.setFont(new Font("Arial", Font.BOLD, 12));
                g2d.drawString("ADMIN", 70, 94);
                
                // Sidebar Links
                g2d.setColor(new Color(52, 58, 64));
                g2d.fillRoundRect(20, 120, 210, 40, 5, 5);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Reports", 80, 145);
                
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.setColor(Color.WHITE);
                g2d.drawString("Dashboard", 60, 220);
                g2d.drawString("Customer Accounts", 60, 260);
                g2d.drawString("Transactions", 60, 300);
                g2d.drawString("Approvals", 60, 340);
                g2d.drawString("Audit Logs", 60, 380);
                g2d.drawString("User Management", 60, 420);
                g2d.drawString("System Settings", 60, 460);
                
                // Main Content Area
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(270, 30, 610, 730, 10, 10);
            }
        };
        
        // Content panel that will hold all the main content components
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Financial Reports Header
                g2d.setColor(new Color(52, 58, 64));
                g2d.setFont(new Font("Arial", Font.BOLD, 24));
                g2d.drawString("Financial Reports", 20, 40);
                
                g2d.setColor(new Color(108, 117, 125));
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                
                // Current date for last generated
                SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                String currentDate = dateFormat.format(new Date());
                g2d.drawString("Last generated: " + currentDate, 20, 70);
                
                // Admin user info
                g2d.setColor(new Color(13, 110, 253));
                g2d.drawString("Generated by: " + userName + " (Admin)", 20, 95);
                
                // Section dividers
                g2d.setColor(new Color(222, 226, 230));
                g2d.fillRect(20, 110, 570, 2);
                g2d.fillRect(20, 210, 570, 2);
                g2d.fillRect(20, 360, 570, 2);
            }
        };
        contentPanel.setLayout(null);
        contentPanel.setOpaque(false);
        contentPanel.setBounds(270, 30, 610, 730);
        
        // Report Type Selector
        JLabel reportTypeLabel = new JLabel("Report Type:");
        reportTypeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        reportTypeLabel.setBounds(20, 130, 150, 20);
        contentPanel.add(reportTypeLabel);
        
        reportTypeComboBox = new JComboBox<>(REPORT_TYPES);
        reportTypeComboBox.setBounds(20, 155, 250, 30);
        reportTypeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadReportData(); // Reload data when report type changes
            }
        });
        contentPanel.add(reportTypeComboBox);
        
        // Date Range Selector
        JLabel dateRangeLabel = new JLabel("Date Range:");
        dateRangeLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateRangeLabel.setBounds(300, 130, 150, 20);
        contentPanel.add(dateRangeLabel);
        
        dateRangeComboBox = new JComboBox<>(DATE_RANGES);
        dateRangeComboBox.setBounds(300, 155, 250, 30);
        dateRangeComboBox.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                loadReportData(); // Reload data when date range changes
            }
        });
        contentPanel.add(dateRangeComboBox);
        
        // Summary Cards Section
        JLabel summaryLabel = new JLabel("Financial Summary");
        summaryLabel.setFont(new Font("Arial", Font.BOLD, 18));
        summaryLabel.setBounds(20, 230, 200, 25);
        contentPanel.add(summaryLabel);
        
        // Card 1: Total Deposits
        JPanel depositCard = createSummaryCard("Total Deposits", "$0.00", new Color(209, 231, 221), new Color(25, 135, 84));
        depositCard.setBounds(20, 265, 135, 80);
        contentPanel.add(depositCard);
        totalDepositsLabel = (JLabel) depositCard.getComponent(1);
        
        // Card 2: Total Withdrawals
        JPanel withdrawalCard = createSummaryCard("Total Withdrawals", "$0.00", new Color(248, 215, 218), new Color(220, 53, 69));
        withdrawalCard.setBounds(165, 265, 135, 80);
        contentPanel.add(withdrawalCard);
        totalWithdrawalsLabel = (JLabel) withdrawalCard.getComponent(1);
        
        // Card 3: Net Balance
        JPanel balanceCard = createSummaryCard("Net Balance", "$0.00", new Color(207, 226, 255), new Color(13, 110, 253));
        balanceCard.setBounds(310, 265, 135, 80);
        contentPanel.add(balanceCard);
        netBalanceLabel = (JLabel) balanceCard.getComponent(1);
        
        // Card 4: Pending Transactions
        JPanel pendingCard = createSummaryCard("Pending", "0", new Color(255, 243, 205), new Color(255, 193, 7));
        pendingCard.setBounds(455, 265, 135, 80);
        contentPanel.add(pendingCard);
        pendingTransactionsLabel = (JLabel) pendingCard.getComponent(1);
        
        // Report Actions Section
        JLabel actionsLabel = new JLabel("Report Actions");
        actionsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        actionsLabel.setBounds(20, 380, 200, 25);
        contentPanel.add(actionsLabel);
        
        // Generate Report Button
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.setBounds(20, 415, 150, 35);
        generateReportButton.setBackground(new Color(13, 110, 253));
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.setBorderPainted(false);
        generateReportButton.setFocusPainted(false);
        generateReportButton.setFont(new Font("Arial", Font.BOLD, 14));
        generateReportButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Report.this.generateReport();
            }
        });
        contentPanel.add(generateReportButton);
        
        // Export CSV Button
        JButton exportCsvButton = new JButton("Export as CSV");
        exportCsvButton.setBounds(180, 415, 150, 35);
        exportCsvButton.setBackground(new Color(25, 135, 84));
        exportCsvButton.setForeground(Color.WHITE);
        exportCsvButton.setBorderPainted(false);
        exportCsvButton.setFocusPainted(false);
        exportCsvButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportCsvButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Report.this.exportReportToCsv();
            }
        });
        contentPanel.add(exportCsvButton);
        
        // Export Text Button (renamed from PDF)
        JButton exportTextButton = new JButton("Export as Text");
        exportTextButton.setBounds(340, 415, 150, 35);
        exportTextButton.setBackground(new Color(220, 53, 69)); // Red color to distinguish from CSV
        exportTextButton.setForeground(Color.WHITE);
        exportTextButton.setBorderPainted(false);
        exportTextButton.setFocusPainted(false);
        exportTextButton.setFont(new Font("Arial", Font.BOLD, 14));
        exportTextButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Report.this.exportReportToPdf();
            }
        });
        contentPanel.add(exportTextButton);
        
        // Back to Dashboard button
        JButton backToDashboardButton = new JButton("Back to Dashboard");
        backToDashboardButton.setBounds(20, 680, 180, 35);
        backToDashboardButton.setBackground(new Color(108, 117, 125));
        backToDashboardButton.setForeground(Color.WHITE);
        backToDashboardButton.setBorderPainted(false);
        backToDashboardButton.setFocusPainted(false);
        backToDashboardButton.setFont(new Font("Arial", Font.BOLD, 14));
        backToDashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Report.this.navigateToDashboard();
            }
        });
        contentPanel.add(backToDashboardButton);
        
        // Add navigation for sidebar menu items
        addSidebarNavigation(mainPanel);
        
        // Set layout to null for absolute positioning
        mainPanel.setLayout(null);
        mainPanel.add(contentPanel);
        add(mainPanel);
    }
    
    /**
     * Creates a summary card panel with title and value
     */
    private JPanel createSummaryCard(String title, String value, Color bgColor, Color textColor) {
        JPanel card = new JPanel();
        card.setLayout(null);
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        titleLabel.setForeground(textColor);
        titleLabel.setBounds(10, 5, 115, 20);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 16));
        valueLabel.setForeground(textColor);
        valueLabel.setBounds(10, 30, 115, 25);
        
        card.add(titleLabel);
        card.add(valueLabel);
        
        return card;
    }
    
    /**
     * Set user information and update UI elements
     */
    public void setUserInfo(String userName, int accountId) {
        this.userName = userName;
        this.accountId = accountId;
        loadReportData();
        
        System.out.println("Report: Set user info - User: " + userName + ", Account ID: " + accountId);
        repaint(); // Refresh the UI to display the new user info
    }
    
    /**
     * Fetch and load report data based on current selection
     */
    private void loadReportData() {
        // If the combo box is not initialized yet, return
        if (reportTypeComboBox == null || dateRangeComboBox == null) {
            return;
        }
        
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        String dateRange = (String) dateRangeComboBox.getSelectedItem();
        
        // Reset values
        totalDeposits = 0;
        totalWithdrawals = 0;
        netBalance = 0;
        pendingTransactions = 0;
        transactionList.clear();
        
        System.out.println("Loading report data: " + reportType + ", " + dateRange);
        
        // Get date range in SQL format
        String dateFilter = getDateFilterSql(dateRange);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query;
            PreparedStatement stmt;
            ResultSet rs;
            
            // Load summary data - modified for admin to see all accounts
            if (isAdmin) {
                // Total deposits
                query = "SELECT SUM(amount) as total FROM transactions WHERE transaction_type = 'DEPOSIT' " + dateFilter;
                stmt = conn.prepareStatement(query);
                rs = stmt.executeQuery();
                if (rs.next() && rs.getObject("total") != null) {
                    totalDeposits = rs.getDouble("total");
                }
                
                // Total withdrawals
                query = "SELECT SUM(amount) as total FROM transactions WHERE transaction_type = 'Withdrawal' " + dateFilter;
                stmt = conn.prepareStatement(query);
                rs = stmt.executeQuery();
                if (rs.next() && rs.getObject("total") != null) {
                    totalWithdrawals = rs.getDouble("total");
                }
                
                // Pending transactions
                query = "SELECT COUNT(*) as count FROM transactions WHERE status = 'PENDING' " + dateFilter;
                stmt = conn.prepareStatement(query);
                rs = stmt.executeQuery();
                if (rs.next()) {
                    pendingTransactions = rs.getInt("count");
                }
                
                // Load transaction list
                query = "SELECT t.transaction_id, t.account_id, a.username, t.transaction_type, " +
                        "t.amount, t.transaction_date, t.status, t.description " +
                        "FROM transactions t " +
                        "JOIN accounts a ON t.account_id = a.account_id " +
                        "WHERE 1=1 " + dateFilter + " " +
                        "ORDER BY t.transaction_date DESC LIMIT 100";
                
                stmt = conn.prepareStatement(query);
                rs = stmt.executeQuery();
                
                while (rs.next()) {
                    transactionList.add(new TransactionData(
                        rs.getInt("transaction_id"),
                        rs.getInt("account_id"),
                        rs.getString("username"),
                        rs.getString("transaction_type"),
                        rs.getDouble("amount"),
                        rs.getString("transaction_date"),
                        rs.getString("status"),
                        rs.getString("description")
                    ));
                }
            } else {
                // For non-admin users, only show their transactions
                // Total deposits
                query = "SELECT SUM(amount) as total FROM transactions WHERE transaction_type = 'DEPOSIT' AND account_id = ? " + dateFilter;
                stmt = conn.prepareStatement(query);
                stmt.setInt(1, accountId);
                rs = stmt.executeQuery();
                if (rs.next() && rs.getObject("total") != null) {
                    totalDeposits = rs.getDouble("total");
                }
                
                // Similar queries for other data points would go here...
            }
            
            // Calculate net balance
            netBalance = totalDeposits - totalWithdrawals;
            
            // Update UI
            updateSummaryCards();
            
        } catch (SQLException ex) {
            System.out.println("Error loading report data: " + ex.getMessage());
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this,
                "Error loading report data: " + ex.getMessage(),
                "Database Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    /**
     * Update the summary cards with the loaded data
     */
    private void updateSummaryCards() {
        if (totalDepositsLabel != null) {
            totalDepositsLabel.setText(String.format("$%.2f", totalDeposits));
        }
        if (totalWithdrawalsLabel != null) {
            totalWithdrawalsLabel.setText(String.format("$%.2f", totalWithdrawals));
        }
        if (netBalanceLabel != null) {
            netBalanceLabel.setText(String.format("$%.2f", netBalance));
        }
        if (pendingTransactionsLabel != null) {
            pendingTransactionsLabel.setText(String.format("%d", pendingTransactions));
        }
    }
    
    /**
     * Convert date range selection to SQL WHERE clause
     */
    private String getDateFilterSql(String dateRange) {
        if (dateRange == null) {
            return "";
        }
        
        switch (dateRange) {
            case "Last 7 Days":
                return "AND transaction_date >= DATE_SUB(NOW(), INTERVAL 7 DAY)";
            case "Last 30 Days":
                return "AND transaction_date >= DATE_SUB(NOW(), INTERVAL 30 DAY)";
            case "Last 90 Days":
                return "AND transaction_date >= DATE_SUB(NOW(), INTERVAL 90 DAY)";
            case "Year to Date":
                return "AND YEAR(transaction_date) = YEAR(CURRENT_DATE())";
            case "Custom Range":
                // In a real app, you'd show a date picker here
                return "";
            default:
                return "";
        }
    }
    
    /**
     * Generate a report based on selected criteria
     */
    private void generateReport() {
        String reportType = (String) reportTypeComboBox.getSelectedItem();
        String dateRange = (String) dateRangeComboBox.getSelectedItem();
        
        // Refresh data
        loadReportData();
        
        JOptionPane.showMessageDialog(this,
            "Report Generated Successfully\n\n" +
            "Report Type: " + reportType + "\n" +
            "Date Range: " + dateRange + "\n\n" +
            "Summary:\n" +
            "- Total Deposits: $" + String.format("%.2f", totalDeposits) + "\n" +
            "- Total Withdrawals: $" + String.format("%.2f", totalWithdrawals) + "\n" +
            "- Net Balance: $" + String.format("%.2f", netBalance) + "\n" +
            "- Pending Transactions: " + pendingTransactions + "\n\n" +
            "Total Transactions: " + transactionList.size(),
            "Report Generation",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    /**
     * Export report data to CSV
     */
    private void exportReportToCsv() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save CSV Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("CSV files (*.csv)", "csv"));
        
        // Set default file name
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String defaultFileName = "KOB_Report_" + dateFormat.format(new Date()) + ".csv";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Add .csv extension if missing
            if (!fileToSave.getAbsolutePath().endsWith(".csv")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".csv");
            }
            
            try (FileWriter writer = new FileWriter(fileToSave)) {
                // Write report header
                String reportType = (String) reportTypeComboBox.getSelectedItem();
                String dateRange = (String) dateRangeComboBox.getSelectedItem();
                
                writer.write("Kurdish - O - Banking (KOB) Report\n");
                writer.write(reportType + " - " + dateRange + "\n");
                writer.write("Generated on: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()) + "\n");
                writer.write("Generated by: " + userName + " (Admin)\n\n");
                
                // Write financial summary
                writer.write("FINANCIAL SUMMARY\n");
                writer.write("Total Deposits,$" + String.format("%.2f", totalDeposits) + "\n");
                writer.write("Total Withdrawals,$" + String.format("%.2f", totalWithdrawals) + "\n");
                writer.write("Net Balance,$" + String.format("%.2f", netBalance) + "\n");
                writer.write("Pending Transactions," + pendingTransactions + "\n\n");
                
                // Write transaction details
                writer.write("TRANSACTION DETAILS\n");
                writer.write("Transaction ID,Account ID,User,Type,Amount,Date,Status,Description\n");
                
                for (TransactionData transaction : transactionList) {
                    writer.write(String.format("%d,%d,%s,%s,%.2f,%s,%s,%s\n",
                        transaction.transactionId,
                        transaction.accountId,
                        transaction.userName,
                        transaction.transactionType,
                        transaction.amount,
                        transaction.date,
                        transaction.status,
                        transaction.description != null ? transaction.description.replace(",", " ") : ""
                    ));
                }
                
                JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Export report data to a formatted text file
     */
    private void exportReportToPdf() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Formatted Report");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Text files (*.txt)", "txt"));
        
        // Set default file name
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
        String defaultFileName = "KOB_Report_" + dateFormat.format(new Date()) + ".txt";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Add .txt extension if missing
            if (!fileToSave.getAbsolutePath().endsWith(".txt")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".txt");
            }
            
            try (FileWriter writer = new FileWriter(fileToSave)) {
                String reportType = (String) reportTypeComboBox.getSelectedItem();
                String dateRange = (String) dateRangeComboBox.getSelectedItem();
                String currentDate = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
                
                // Create a nicely formatted text report
                
                // Title and metadata section
                writer.write("===============================================================\n");
                writer.write("             KURDISH - O - BANKING (KOB) REPORT                \n");
                writer.write("===============================================================\n\n");
                
                writer.write("REPORT INFORMATION:\n");
                writer.write("-------------------\n");
                writer.write(String.format("Report Type: %s\n", reportType));
                writer.write(String.format("Date Range: %s\n", dateRange));
                writer.write(String.format("Generated on: %s\n", currentDate));
                writer.write(String.format("Generated by: %s (Admin)\n\n", userName));
                
                // Financial summary section
                writer.write("FINANCIAL SUMMARY:\n");
                writer.write("------------------\n");
                writer.write(String.format("%-25s $%,.2f\n", "Total Deposits:", totalDeposits));
                writer.write(String.format("%-25s $%,.2f\n", "Total Withdrawals:", totalWithdrawals));
                writer.write(String.format("%-25s $%,.2f\n", "Net Balance:", netBalance));
                writer.write(String.format("%-25s %d\n\n", "Pending Transactions:", pendingTransactions));
                
                // Transaction details section
                if (!transactionList.isEmpty()) {
                    writer.write("TRANSACTION DETAILS:\n");
                    writer.write("--------------------\n");
                    
                    // Table header
                    writer.write(String.format("%-5s | %-8s | %-15s | %-10s | %-12s | %-19s | %-15s | %s\n",
                            "ID", "Acct ID", "User", "Type", "Amount", "Date", "Status", "Description"));
                    
                    // Table divider
                    StringBuilder divider = new StringBuilder();
                    for (int i = 0; i < 120; i++) {
                        divider.append("-");
                    }
                    writer.write(divider.toString() + "\n");
                    
                    // Table data
                    for (TransactionData transaction : transactionList) {
                        String description = transaction.description;
                        if (description == null) {
                            description = "";
                        }
                        
                        // Truncate or adjust description if too long
                        if (description.length() > 30) {
                            description = description.substring(0, 27) + "...";
                        }
                        
                        writer.write(String.format("%-5d | %-8d | %-15s | %-10s | $%-11.2f | %-19s | %-15s | %s\n",
                            transaction.transactionId,
                            transaction.accountId,
                            transaction.userName,
                            transaction.transactionType,
                            transaction.amount,
                            transaction.date,
                            transaction.status,
                            description));
                    }
                }
                
                // Footer
                writer.write("\n\n");
                writer.write("===============================================================\n");
                writer.write("                    END OF REPORT                              \n");
                writer.write("===============================================================\n");
                
                JOptionPane.showMessageDialog(this,
                    "Report exported successfully to:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this,
                    "Error exporting report: " + ex.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Navigate back to the appropriate dashboard based on user role
     */
    private void navigateToDashboard() {
        dispose();
        SwingUtilities.invokeLater(() -> {
            if (isAdmin) {
                // Navigate to ManagerDashboard for admin users
                ManagerDashboard managerDashboard = new ManagerDashboard();
                managerDashboard.setVisible(true);
            } else {
                // Navigate to normal user dashboard for non-admin users
                Dashbord dashboard = new Dashbord();
                dashboard.setUserInfo(userName, accountId);
                dashboard.setVisible(true);
            }
        });
    }
    
    /**
     * Adds click listeners to the sidebar menu items for navigation
     */
    private void addSidebarNavigation(JPanel mainPanel) {
        // Create invisible buttons over the sidebar text for navigation
        String[] menuItems = {"Dashboard", "Customer Accounts", "Transactions", "Approvals", "Audit Logs", "User Management", "System Settings"};
        int[] yPositions = {220, 260, 300, 340, 380, 420, 460};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton navButton = new JButton();
            navButton.setBounds(20, yPositions[i] - 15, 210, 30);
            navButton.setOpaque(false);
            navButton.setContentAreaFilled(false);
            navButton.setBorderPainted(false);
            
            final String item = menuItems[i];
            navButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    Report.this.navigateToScreen(item);
                }
            });
            
            mainPanel.add(navButton);
        }
    }
    
    /**
     * Helper method to navigate between admin screens
     */
    private void navigateToScreen(String screenName) {
        dispose(); // Close the current window
        
        switch (screenName) {
            case "Dashboard":
                SwingUtilities.invokeLater(() -> {
                    if (isAdmin) {
                        new ManagerDashboard().setVisible(true);
                    } else {
                        Dashbord dashboard = new Dashbord();
                        dashboard.setUserInfo(userName, accountId);
                        dashboard.setVisible(true);
                    }
                });
                break;
            case "Customer Accounts":
                SwingUtilities.invokeLater(() -> {
                    new CustomerAccounts().setVisible(true);
                });
                break;
            case "Transactions":
                SwingUtilities.invokeLater(() -> {
                    new ManageTransaction().setVisible(true);
                });
                break;
            case "Approvals":
            case "Audit Logs":
            case "User Management":
            case "System Settings":
                SwingUtilities.invokeLater(() -> {
                    // For now, just show a message
                    JOptionPane.showMessageDialog(null, 
                        screenName + " screen is under development.", 
                        "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                    navigateToDashboard();
                });
                break;
            default:
                navigateToDashboard();
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Report report = new Report("Admin", 0);
            report.setVisible(true);
        });
        }}