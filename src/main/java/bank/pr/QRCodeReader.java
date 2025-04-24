package bank.pr;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;


public class QRCodeReader extends JFrame {
    
    private String userName;
    private int userId;
    private JTextArea resultArea;
    
    public QRCodeReader() {
        setTitle("Kurdish-O-Banking QR Code Reader");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());
        
        // Create top panel for file selection
        JPanel topPanel = new JPanel();
        topPanel.setBackground(new Color(20, 25, 45));
        topPanel.setLayout(new FlowLayout());
        
        JButton selectButton = new JButton("Select QR Code Image");
        selectButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        selectButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectAndProcessQRCode();
            }
        });
        
        topPanel.add(selectButton);
        
        // Create result display area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setFont(new Font("Monospaced", Font.PLAIN, 14));
        resultArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JScrollPane scrollPane = new JScrollPane(resultArea);
        
        // Create bottom panel for navigation
        JPanel bottomPanel = new JPanel();
        bottomPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        
        JButton backButton = new JButton("Back to Dashboard");
        backButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                goToDashboard();
            }
        });
        
        bottomPanel.add(backButton);
        
        // Add components to frame
        add(topPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Set the user information for the QR Code Reader
     * @param userName The username
     * @param userId The user ID
     */
    public void setUserInfo(String userName, int userId) {
        this.userName = userName;
        this.userId = userId;
    }
    
    /**
     * Prompt user to select QR code image file and process it
     */
    private void selectAndProcessQRCode() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select QR Code Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processQRImage(selectedFile);
        }
    }
    
    /**
     * Process the selected QR code image
     * @param file The QR code image file
     */
    private void processQRImage(File file) {
        try {
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                displayError("Invalid image file");
                return;
            }
            
            // For this simplified version, we'll simulate reading a QR code
            // In a real implementation, you would use a QR code reading library
            // Since we've created a custom QR code format, we'll use a basic approach
            
            // Extract "data" from the image - in this case, we'll use a prompt 
            String userInput = JOptionPane.showInputDialog(this, 
                    "Enter the encrypted data from the QR code (simulated reading):",
                    "QR Code Data",
                    JOptionPane.QUESTION_MESSAGE);
            
            if (userInput == null || userInput.isEmpty()) {
                displayError("No QR code data provided");
                return;
            }
            
            // Try to process the QR code data
            processQRData(userInput);
            
        } catch (IOException e) {
            displayError("Error reading image file: " + e.getMessage());
        } catch (Exception e) {
            displayError("Error processing QR code: " + e.getMessage());
        }
    }
    
    /**
     * Process the encrypted QR code data
     * @param encryptedData The encrypted data from the QR code
     */
    private void processQRData(String encryptedData) {
        try {
            // First verify this is a valid QR code from our system
            if (!BankQRGenerator.verifyQRCode(encryptedData)) {
                displayError("Invalid QR code. This QR code was not generated by Kurdish-O-Banking system.");
                return;
            }
            
            // Decrypt the data
            String decryptedData = BankQRGenerator.decryptData(encryptedData);
            
            // Extract account information
            int[] accountInfo = BankQRGenerator.parseAccountInfo(decryptedData);
            
            if (accountInfo == null) {
                displayError("Could not parse account information from QR code.");
                return;
            }
            
            int qrUserId = accountInfo[0];
            int accountId = accountInfo[1];
            
            // Look up the account in the database
            lookupAccountDetails(qrUserId, accountId);
            
        } catch (Exception e) {
            displayError("Error processing QR code data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Look up account details in the database
     * @param qrUserId User ID from QR code
     * @param accountId Account ID from QR code
     */
    private void lookupAccountDetails(int qrUserId, int accountId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT a.account_id, a.username, a.account_type, a.balance " +
                          "FROM accounts a " +
                          "WHERE a.account_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, accountId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String username = rs.getString("username");
                double balance = rs.getDouble("balance");
                String accountType = rs.getString("account_type");
                
                // Display the account information
                StringBuilder result = new StringBuilder();
                result.append("QR CODE SUCCESSFULLY VERIFIED\n");
                result.append("===========================\n\n");
                result.append("Account Information:\n");
                result.append("-------------------\n");
                result.append("Account ID: ").append(accountId).append("\n");
                result.append("Account Type: ").append(accountType).append("\n");
                result.append("Balance: $").append(String.format("%.2f", balance)).append("\n\n");
                
                result.append("User Information:\n");
                result.append("--------------------\n");
                result.append("Username: ").append(username).append("\n");
                
                // Get additional user information if available
                String userQuery = "SELECT first_name, last_name, email, phone FROM users WHERE username = ?";
                PreparedStatement userStmt = conn.prepareStatement(userQuery);
                userStmt.setString(1, username);
                
                ResultSet userRs = userStmt.executeQuery();
                if (userRs.next()) {
                    String firstName = userRs.getString("first_name");
                    String lastName = userRs.getString("last_name");
                    String email = userRs.getString("email");
                    String phone = userRs.getString("phone");
                    
                    result.append("Name: ").append(firstName).append(" ").append(lastName).append("\n");
                    result.append("Email: ").append(email).append("\n");
                    result.append("Phone: ").append(phone).append("\n\n");
                }
                
                result.append("QR Code Scan Time: ").append(java.time.LocalDateTime.now()).append("\n");
                
                resultArea.setText(result.toString());
            } else {
                displayError("Account not found in the database. QR code may be invalid.");
            }
            
        } catch (SQLException e) {
            displayError("Database error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    /**
     * Display an error message in the result area
     * @param message The error message
     */
    private void displayError(String message) {
        resultArea.setText("ERROR: " + message);
    }
    
    /**
     * Return to the dashboard
     */
    private void goToDashboard() {
        SwingUtilities.invokeLater(() -> {
            Dashbord dashboard = new Dashbord();
            dashboard.setUserInfo(userName, userId);
            dashboard.setVisible(true);
            this.dispose();
        });
    }
    
    /**
     * Main method for testing the QR code reader
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            QRCodeReader reader = new QRCodeReader();
            reader.setUserInfo("test_user", 1);
            reader.setVisible(true);
        });
    }}