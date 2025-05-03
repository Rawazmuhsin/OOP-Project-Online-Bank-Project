package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;

public class ManageTransaction extends JFrame {
    
    private DefaultTableModel tableModel;
    private JTable transactionsTable;
    private int adminId = 0;

    public ManageTransaction() {
        initialize();
    }
    
    public ManageTransaction(int adminId) {
        this.adminId = adminId;
        initialize();
    }
    
    private void initialize() {
        setTitle("KOB Manager - Transaction Management");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createTransactionContentPanel();
        add(new JScrollPane(mainContentPanel), BorderLayout.CENTER);

        setVisible(true);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel();
        sidebarPanel.setPreferredSize(new Dimension(250, 800));
        sidebarPanel.setBackground(new Color(26, 32, 44)); // #1a202c
        sidebarPanel.setLayout(null);

        // Sidebar title
        JLabel titleLabel = new JLabel("KOB Manager");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 200, 30);
        sidebarPanel.add(titleLabel);

        // Active sidebar button (Transaction Mgmt)
        JButton transactionButton = new JButton("Transaction Mgmt");
        transactionButton.setBackground(new Color(52, 58, 64)); // #343a40
        transactionButton.setForeground(Color.WHITE);
        transactionButton.setFont(new Font("Arial", Font.PLAIN, 16));
        transactionButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        transactionButton.setBounds(20, 100, 210, 40);
        transactionButton.setHorizontalAlignment(SwingConstants.LEFT);
        transactionButton.setFocusPainted(false);
        sidebarPanel.add(transactionButton);

        // Other sidebar buttons
        String[] menuItems = {"Dashboard", "Customer Accounts", "Approval Queue", "Reports", "Audit Logs"};
        int yPos = 180;
        for (String item : menuItems) {
            JButton menuButton = new JButton(item);
            menuButton.setBackground(new Color(26, 32, 44)); // #1a202c
            menuButton.setForeground(Color.WHITE);
            menuButton.setFont(new Font("Arial", Font.PLAIN, 14));
            menuButton.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
            menuButton.setBounds(20, yPos, 210, 30);
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            menuButton.setFocusPainted(false);
            
            // Hover effect
            menuButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    menuButton.setBackground(new Color(52, 58, 64)); // #343a40
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    menuButton.setBackground(new Color(26, 32, 44)); // #1a202c
                }
            });
            
            // Add action listener for navigation
            menuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    navigateToScreen(item);
                }
            });
            
            sidebarPanel.add(menuButton);
            yPos += 40;
        }

        return sidebarPanel;
    }

    private JPanel createTransactionContentPanel() {
        JPanel mainPanel = new JPanel();
        mainPanel.setBackground(new Color(240, 244, 248)); // #f0f4f8
        mainPanel.setLayout(new BorderLayout());

        // Content container with padding
        JPanel contentPanel = new JPanel();
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        
        // Transaction Management Header
        JLabel titleLabel = new JLabel("Manage Transactions");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64)); // #343a40
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentPanel.add(titleLabel);

        // Filter Section - Row 1
        JPanel filterPanel1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel1.setBackground(Color.WHITE);
        filterPanel1.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel1.setBorder(BorderFactory.createEmptyBorder(20, 0, 10, 0));

        String[] filterOptions1 = {"Date Range", "Customer Name", "Transaction Type", "Status"};
        for (String option : filterOptions1) {
            JButton filterButton = new JButton(option);
            filterButton.setFont(new Font("Arial", Font.PLAIN, 14));
            filterButton.setBackground(new Color(248, 250, 252)); // #f8fafc
            filterButton.setForeground(new Color(52, 58, 64)); // #343a40
            filterButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
            filterButton.setFocusPainted(false);
            filterPanel1.add(filterButton);
        }

        JButton applyButton = new JButton("Apply Filters");
        applyButton.setFont(new Font("Arial", Font.PLAIN, 14));
        applyButton.setBackground(new Color(13, 110, 253)); // #0d6efd
        applyButton.setForeground(Color.WHITE);
        applyButton.setBorder(BorderFactory.createLineBorder(new Color(13, 110, 253)));
        applyButton.setFocusPainted(false);
        filterPanel1.add(applyButton);

        contentPanel.add(filterPanel1);

        // Filter Section - Row 2
        JPanel filterPanel2 = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel2.setBackground(Color.WHITE);
        filterPanel2.setAlignmentX(Component.LEFT_ALIGNMENT);
        filterPanel2.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        String[] filterOptions2 = {"Amount Range", "Account Number"};
        for (String option : filterOptions2) {
            JButton filterButton = new JButton(option);
            filterButton.setFont(new Font("Arial", Font.PLAIN, 14));
            filterButton.setBackground(new Color(248, 250, 252)); // #f8fafc
            filterButton.setForeground(new Color(52, 58, 64)); // #343a40
            filterButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
            filterButton.setFocusPainted(false);
            filterPanel2.add(filterButton);
        }

        contentPanel.add(filterPanel2);

        // Table with real data
        String[] columnNames = {"DATE", "CUSTOMER", "TYPE", "ACCOUNT", "AMOUNT", "STATUS", "ACTIONS"};
        
        // Create table model for dynamic updates
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 6; // Only make Actions column editable
            }
        };

        transactionsTable = new JTable(tableModel);
        transactionsTable.setRowHeight(30);
        transactionsTable.setShowGrid(false);
        transactionsTable.setIntercellSpacing(new Dimension(0, 0));
        transactionsTable.setFont(new Font("Arial", Font.PLAIN, 14));
        
        // Load transaction data from database
        loadTransactionData();
        
        // Custom renderers for different columns
        transactionsTable.getColumnModel().getColumn(2).setCellRenderer(new TransactionTypeRenderer());
        transactionsTable.getColumnModel().getColumn(4).setCellRenderer(new AmountRenderer());
        transactionsTable.getColumnModel().getColumn(5).setCellRenderer(new StatusRenderer());
        transactionsTable.getColumnModel().getColumn(6).setCellRenderer(new ActionRenderer());
        
        // Set column widths
        transactionsTable.getColumnModel().getColumn(0).setPreferredWidth(120);
        transactionsTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        transactionsTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        transactionsTable.getColumnModel().getColumn(6).setPreferredWidth(100);

        // Style table header
        JTableHeader header = transactionsTable.getTableHeader();
        header.setBackground(new Color(248, 250, 252)); // #f8fafc
        header.setForeground(new Color(52, 58, 64)); // #343a40
        header.setFont(new Font("Arial", Font.PLAIN, 14));
        header.setBorder(BorderFactory.createEmptyBorder());
        header.setReorderingAllowed(false);

        JScrollPane scrollPane = new JScrollPane(transactionsTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        contentPanel.add(scrollPane);

        // Pagination Controls
        JPanel paginationPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 20));
        paginationPanel.setBackground(Color.WHITE);
        paginationPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JButton prevButton = new JButton("← Dashboard");
        prevButton.setFont(new Font("Arial", Font.PLAIN, 14));
        prevButton.setBackground(new Color(248, 250, 252)); // #f8fafc
        prevButton.setForeground(new Color(52, 58, 64)); // #343a40
        prevButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
        prevButton.setFocusPainted(false);
        prevButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
        });
        paginationPanel.add(prevButton);

        for (int i = 1; i <= 5; i++) {
            JButton pageButton = new JButton(String.valueOf(i));
            pageButton.setFont(new Font("Arial", Font.PLAIN, 14));
            if (i == 1) { // First page is active by default
                pageButton.setBackground(new Color(13, 110, 253)); // #0d6efd
                pageButton.setForeground(Color.WHITE);
                pageButton.setBorder(BorderFactory.createLineBorder(new Color(13, 110, 253)));
            } else {
                pageButton.setBackground(new Color(248, 250, 252)); // #f8fafc
                pageButton.setForeground(new Color(52, 58, 64)); // #343a40
                pageButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
            }
            pageButton.setFocusPainted(false);
            paginationPanel.add(pageButton);
        }

        JButton nextButton = new JButton("Next →");
        nextButton.setFont(new Font("Arial", Font.PLAIN, 14));
        nextButton.setBackground(new Color(248, 250, 252)); // #f8fafc
        nextButton.setForeground(new Color(52, 58, 64)); // #343a40
        nextButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
        nextButton.setFocusPainted(false);
        paginationPanel.add(nextButton);

        JButton exportButton = new JButton("Export PDF");
        exportButton.setFont(new Font("Arial", Font.PLAIN, 14));
        exportButton.setBackground(new Color(248, 250, 252)); // #f8fafc
        exportButton.setForeground(new Color(52, 58, 64)); // #343a40
        exportButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239))); // #ebedef
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportToPDF());
        paginationPanel.add(exportButton);
        
        // Add refresh button
        JButton refreshButton = new JButton("Refresh Data");
        refreshButton.setFont(new Font("Arial", Font.PLAIN, 14));
        refreshButton.setBackground(new Color(230, 247, 255));
        refreshButton.setForeground(new Color(13, 110, 253));
        refreshButton.setBorder(BorderFactory.createLineBorder(new Color(230, 247, 255)));
        refreshButton.setFocusPainted(false);
        refreshButton.addActionListener(e -> {
            loadTransactionData();
            JOptionPane.showMessageDialog(this, 
                "Transaction data refreshed successfully.",
                "Refresh Complete", 
                JOptionPane.INFORMATION_MESSAGE);
        });
        paginationPanel.add(refreshButton);

        contentPanel.add(paginationPanel);
        mainPanel.add(contentPanel, BorderLayout.NORTH);
        return mainPanel;
    }
    
    /**
     * Export the current table data to a PDF file
     * Requires iText library: com.itextpdf (add to your dependencies)
     */
    private void exportToPDF() {
        // Check if there's data to export
        if (tableModel.getRowCount() == 0 || 
            (tableModel.getRowCount() == 1 && tableModel.getValueAt(0, 0).toString().contains("No transactions"))) {
            JOptionPane.showMessageDialog(this,
                "No data available to export.",
                "Export Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create file chooser dialog
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save PDF File");
        
        // Set default filename with timestamp
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
        String defaultFileName = "transactions_" + dateFormat.format(new Date()) + ".pdf";
        fileChooser.setSelectedFile(new File(defaultFileName));
        
        // Set file filter to only show PDF files
        FileNameExtensionFilter pdfFilter = new FileNameExtensionFilter("PDF Files (*.pdf)", "pdf");
        fileChooser.setFileFilter(pdfFilter);
        
        // Show save dialog
        int userSelection = fileChooser.showSaveDialog(this);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Add .pdf extension if not present
            if (!fileToSave.getName().toLowerCase().endsWith(".pdf")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".pdf");
            }
            
            // Check if file already exists and confirm overwrite
            if (fileToSave.exists()) {
                int response = JOptionPane.showConfirmDialog(this,
                    "The file " + fileToSave.getName() + " already exists. Do you want to overwrite it?",
                    "Confirm Overwrite",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE);
                
                if (response != JOptionPane.YES_OPTION) {
                    return;
                }
            }
            
            try {
                // Create PDF document
                com.itextpdf.text.Document document = new com.itextpdf.text.Document();
                com.itextpdf.text.pdf.PdfWriter.getInstance(document, new FileOutputStream(fileToSave));
                document.open();
                
                // Add title
                com.itextpdf.text.Font titleFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 16, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Paragraph title = new com.itextpdf.text.Paragraph("Transaction Report", titleFont);
                title.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                title.setSpacingAfter(10);
                document.add(title);
                
                // Add timestamp
                com.itextpdf.text.Font smallFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 8);
                com.itextpdf.text.Paragraph timestamp = new com.itextpdf.text.Paragraph(
                    "Report generated on: " + new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(new Date()), 
                    smallFont);
                timestamp.setAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                timestamp.setSpacingAfter(20);
                document.add(timestamp);
                
                // Create table
                com.itextpdf.text.pdf.PdfPTable pdfTable = new com.itextpdf.text.pdf.PdfPTable(tableModel.getColumnCount() - 1); // Skip ACTIONS column
                pdfTable.setWidthPercentage(100);
                
                // Set column widths (percentages)
                float[] columnWidths = {15f, 20f, 15f, 15f, 15f, 20f};
                pdfTable.setWidths(columnWidths);
                
                // Define fonts for headers and data
                com.itextpdf.text.Font headerFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 10, com.itextpdf.text.Font.BOLD);
                com.itextpdf.text.Font dataFont = new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9);
                
                // Add table headers
                for (int i = 0; i < tableModel.getColumnCount() - 1; i++) { // Skip ACTIONS column
                    com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                        new com.itextpdf.text.Phrase(tableModel.getColumnName(i), headerFont));
                    cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(248, 250, 252));
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }
                
                // Add data rows
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    if (tableModel.getValueAt(row, 0).toString().contains("No transactions")) {
                        continue; // Skip "No transactions found" row
                    }
                    
                    for (int col = 0; col < tableModel.getColumnCount() - 1; col++) { // Skip ACTIONS column
                        // Get cell value
                        Object value = tableModel.getValueAt(row, col);
                        String cellValue = (value == null) ? "" : value.toString();
                        
                        com.itextpdf.text.pdf.PdfPCell cell = new com.itextpdf.text.pdf.PdfPCell(
                            new com.itextpdf.text.Phrase(cellValue, dataFont));
                        
                        // Align different columns as needed
                        if (col == 4) { // AMOUNT column
                            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_RIGHT);
                            
                            // Set color based on positive/negative values
                            if (cellValue.startsWith("+")) {
                                cell.setPhrase(new com.itextpdf.text.Phrase(cellValue, 
                                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, 
                                        com.itextpdf.text.Font.NORMAL, new com.itextpdf.text.BaseColor(0, 128, 0))));
                            } else if (cellValue.startsWith("-")) {
                                cell.setPhrase(new com.itextpdf.text.Phrase(cellValue, 
                                    new com.itextpdf.text.Font(com.itextpdf.text.Font.FontFamily.HELVETICA, 9, 
                                        com.itextpdf.text.Font.NORMAL, new com.itextpdf.text.BaseColor(220, 53, 69))));
                            }
                        } else if (col == 2 || col == 5) { // TYPE and STATUS columns
                            cell.setHorizontalAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                            
                            // Set background colors for transaction type
                            if (col == 2) {
                                String type = cellValue.toLowerCase();
                                if (type.contains("deposit")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(230, 255, 250));
                                } else if (type.contains("withdrawal")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 236, 236));
                                } else {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 250, 230));
                                }
                            }
                            
                            // Set background colors for status
                            if (col == 5) {
                                String status = cellValue.toLowerCase();
                                if (status.contains("approved")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(230, 255, 250));
                                } else if (status.contains("pending")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 250, 230));
                                } else if (status.contains("rejected") || status.contains("failed")) {
                                    cell.setBackgroundColor(new com.itextpdf.text.BaseColor(255, 236, 236));
                                }
                            }
                        }
                        
                        cell.setPadding(5);
                        pdfTable.addCell(cell);
                    }
                }
                
                document.add(pdfTable);
                
                // Add footer
                document.add(new com.itextpdf.text.Paragraph("\n"));
                com.itextpdf.text.Paragraph footer = new com.itextpdf.text.Paragraph(
                    "This is an official report from KOB Manager. For any inquiries, please contact the bank.", 
                    smallFont);
                footer.setAlignment(com.itextpdf.text.Element.ALIGN_CENTER);
                document.add(footer);
                
                // Close document
                document.close();
                
                JOptionPane.showMessageDialog(this,
                    "Transaction data successfully exported to PDF:\n" + fileToSave.getAbsolutePath(),
                    "Export Successful",
                    JOptionPane.INFORMATION_MESSAGE);
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this,
                    "Error exporting data to PDF: " + e.getMessage(),
                    "Export Error",
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
    }
    
    /**
     * Loads transaction data from the database
     */
    private void loadTransactionData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        SimpleDateFormat displayFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        DecimalFormat currencyFormat = new DecimalFormat("$#,##0.00");
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Join with accounts table to get usernames
            String query = "SELECT t.transaction_id, t.account_id, t.transaction_type, t.amount, " +
                           "t.transaction_date, t.description, t.status, t.account_number, " +
                           "a.username, a.account_number as full_account_number " +
                           "FROM transactions t " +
                           "LEFT JOIN accounts a ON t.account_id = a.account_id " +
                           "ORDER BY t.transaction_date DESC";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                String transactionDate = "";
                try {
                    java.sql.Timestamp timestamp = rs.getTimestamp("transaction_date");
                    transactionDate = displayFormat.format(timestamp);
                } catch (Exception e) {
                    transactionDate = "N/A";
                }
                
                String username = rs.getString("username");
                if (username == null || username.isEmpty()) {
                    username = "User #" + rs.getInt("account_id");
                }
                
                String transactionType = rs.getString("transaction_type");
                
                String accountNumber = rs.getString("full_account_number");
                if (accountNumber == null || accountNumber.isEmpty()) {
                    accountNumber = "****" + rs.getInt("account_id");
                } else {
                    // Mask account number for privacy
                    accountNumber = "****" + accountNumber.substring(Math.max(0, accountNumber.length() - 4));
                }
                
                double amount = rs.getDouble("amount");
                String formattedAmount = "";
                
                // Format amount with sign
                if (transactionType.equalsIgnoreCase("Deposit")) {
                    formattedAmount = "+" + currencyFormat.format(Math.abs(amount));
                } else if (transactionType.equalsIgnoreCase("Withdrawal")) {
                    formattedAmount = "-" + currencyFormat.format(Math.abs(amount));
                } else {
                    // For transfers, negative amounts are outgoing, positive are incoming
                    if (amount < 0) {
                        formattedAmount = "-" + currencyFormat.format(Math.abs(amount));
                    } else {
                        formattedAmount = "+" + currencyFormat.format(amount);
                    }
                }
                
                String status = rs.getString("status");
                if (status == null || status.isEmpty()) {
                    status = "PENDING";
                }
                
                // Action button text based on status
                String actionText = status.equals("APPROVED") ? "Details" : "Review";
                
                // Add the row to the table
                tableModel.addRow(new Object[]{
                    transactionDate,
                    username,
                    transactionType,
                    accountNumber,
                    formattedAmount,
                    status,
                    actionText
                });
            }
            
            // If no transactions were found, display a message
            if (tableModel.getRowCount() == 0) {
                tableModel.addRow(new Object[]{"No transactions found", "", "", "", "", "", ""});
            }
            
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, 
                "Error loading transaction data: " + e.getMessage(),
                "Database Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    /**
     * Helper method to navigate between screens
     */
    private void navigateToScreen(String screenName) {
        dispose(); // Close the current window
        
        switch (screenName) {
            case "Dashboard":
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
                break;
            case "Customer Accounts":
                SwingUtilities.invokeLater(() -> new CustomerAccounts(adminId).setVisible(true));
                break;
            case "Reports":
                SwingUtilities.invokeLater(() -> new Report().setVisible(true));
                break;
            case "Approval Queue":
                // For now, just show a message and return to dashboard
                JOptionPane.showMessageDialog(null, 
                    "Approval Queue screen is under development.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
                break;
            case "Audit Logs":
                // For now, just show a message and return to dashboard
                JOptionPane.showMessageDialog(null, 
                    "Audit Logs screen is under development.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
                break;
            default:
                SwingUtilities.invokeLater(() -> new ManagerDashboard(adminId).setVisible(true));
        }
    }

    // Custom renderer for Transaction Type column
    private static class TransactionTypeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            if (value == null) {
                return label;
            }
            
            String type = value.toString();
            if (type.equalsIgnoreCase("Deposit")) {
                label.setBackground(new Color(230, 255, 250)); // #e6fffa
                label.setForeground(new Color(13, 110, 253)); // #0d6efd
            } else if (type.equalsIgnoreCase("Withdrawal")) {
                label.setBackground(new Color(255, 236, 236)); // #ffecec
                label.setForeground(new Color(220, 53, 69)); // #dc3545
            } else {
                // Transfer or other types
                label.setBackground(new Color(255, 250, 230));
                label.setForeground(new Color(255, 153, 0));
            }
            label.setBorder(BorderFactory.createLineBorder(label.getBackground()));
            return label;
        }
    }

    // Custom renderer for Amount column
    private static class AmountRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.RIGHT);
            
            if (value == null) {
                return label;
            }
            
            if (value.toString().startsWith("+")) {
                label.setForeground(new Color(0, 128, 0)); // Green for positive
            } else if (value.toString().startsWith("-")) {
                label.setForeground(new Color(220, 53, 69)); // Red for negative
            }
            return label;
        }
    }

    // Custom renderer for Status column
    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JLabel label = (JLabel) super.getTableCellRendererComponent(
                    table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);

            if (value == null) {
                return label;
            }
            
            String status = value.toString();
            if (status.equalsIgnoreCase("APPROVED")) {
                label.setBackground(new Color(230, 255, 250)); // #e6fffa
                label.setForeground(new Color(13, 110, 253)); // #0d6efd
            } else if (status.equalsIgnoreCase("PENDING")) {
                label.setBackground(new Color(255, 250, 230));
                label.setForeground(new Color(255, 153, 0));
            } else {
                label.setBackground(new Color(255, 236, 236)); // #ffecec
                label.setForeground(new Color(220, 53, 69)); // #dc3545
            }
            label.setBorder(BorderFactory.createLineBorder(label.getBackground()));
            return label;
        }
    }

    // Custom renderer for Action column
    private static class ActionRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            JButton button = new JButton(value.toString());
            button.setFont(new Font("Arial", Font.PLAIN, 12));
            button.setBackground(new Color(230, 255, 250)); // #e6fffa
            button.setForeground(new Color(13, 110, 253)); // #0d6efd
            button.setBorder(BorderFactory.createLineBorder(new Color(230, 255, 250)));
            button.setFocusPainted(false);
            return button;
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ManageTransaction();
        });
    }
}