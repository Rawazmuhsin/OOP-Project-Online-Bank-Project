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

// ZXing imports for QR code reading
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.LuminanceSource;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.Result;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import java.util.HashMap;
import java.util.Map;

public class QRCodeTransfer extends JFrame {
    
    private String userName;
    private int userId;
    private JLabel statusLabel;
    private JPanel imagePanel;
    private JButton scanButton;
    private JButton confirmButton;
    private Transfer parentTransfer; // Reference to the parent Transfer object
    
    /**
     * Constructor for QRCodeTransfer
     * @param parent The parent Transfer object to return results to
     * @param userName Current user's name
     * @param userId Current user's ID
     */
    public QRCodeTransfer(Transfer parent, String userName, int userId) {
        this.parentTransfer = parent;
        this.userName = userName;
        this.userId = userId;
        
        initializeUI();
    }
    
    /**
     * Set up the UI components
     */
    private void initializeUI() {
        setTitle("Scan QR Code - Kurdish-O-Banking");
        setSize(600, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        
        // Header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(20, 25, 45));
        headerPanel.setPreferredSize(new Dimension(getWidth(), 70));
        headerPanel.setLayout(new BorderLayout());
        
        JLabel titleLabel = new JLabel("Scan QR Code for Transfer", SwingConstants.CENTER);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout(20, 20));
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        contentPanel.setBackground(new Color(245, 247, 251));
        
        // Instructions
        JLabel instructionsLabel = new JLabel(
            "<html><p>Scan a QR code to automatically fill in the recipient's account details.</p>" +
            "<p>This is a secure way to transfer money without typing account numbers manually.</p></html>");
        instructionsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        // Image panel for QR code preview
        imagePanel = new JPanel();
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setBackground(Color.WHITE);
        imagePanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        imagePanel.setPreferredSize(new Dimension(300, 300));
        
        JLabel placeholderLabel = new JLabel("QR Code Preview", SwingConstants.CENTER);
        placeholderLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        placeholderLabel.setForeground(Color.GRAY);
        imagePanel.add(placeholderLabel, BorderLayout.CENTER);
        
        // Status label
        statusLabel = new JLabel("Select a QR code to scan");
        statusLabel.setFont(new Font("SansSerif", Font.ITALIC, 14));
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);
        
        scanButton = new JButton("Select QR Code");
        scanButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        scanButton.setBackground(new Color(60, 130, 180));
        scanButton.setForeground(Color.WHITE);
        scanButton.setFocusPainted(false);
        scanButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectQRCodeFile();
            }
        });
        
        confirmButton = new JButton("Use This Account");
        confirmButton.setFont(new Font("SansSerif", Font.BOLD, 14));
        confirmButton.setBackground(new Color(60, 180, 60));
        confirmButton.setForeground(Color.WHITE);
        confirmButton.setFocusPainted(false);
        confirmButton.setEnabled(false);
        confirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                returnToTransfer();
            }
        });
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("SansSerif", Font.PLAIN, 14));
        cancelButton.setFocusPainted(false);
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close window without action
            }
        });
        
        buttonPanel.add(scanButton);
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        
        // Add components to content panel
        contentPanel.add(instructionsLabel, BorderLayout.NORTH);
        contentPanel.add(imagePanel, BorderLayout.CENTER);
        
        // South panel for status and buttons
        JPanel southPanel = new JPanel(new BorderLayout(0, 10));
        southPanel.setOpaque(false);
        southPanel.add(statusLabel, BorderLayout.NORTH);
        southPanel.add(buttonPanel, BorderLayout.CENTER);
        
        contentPanel.add(southPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    /**
     * Handle selecting and processing a QR code file
     */
    private void selectQRCodeFile() {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select QR Code Image");
        fileChooser.setFileFilter(new FileNameExtensionFilter("Image Files", "jpg", "png", "gif", "jpeg"));
        
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            processQRCodeImage(selectedFile);
        }
    }
    
    // Current scanned account details
    private int scannedAccountId = -1;
    private String scannedAccountUsername = "";
    private String scannedAccountType = "";
    
    /**
     * Process the selected QR code image
     * @param file The QR code image file
     */
    private void processQRCodeImage(File file) {
        try {
            // Load and display the image
            BufferedImage image = ImageIO.read(file);
            if (image == null) {
                statusLabel.setText("Error: Invalid image file");
                return;
            }
            
            // Display the image
            ImageIcon icon = new ImageIcon(image.getScaledInstance(300, 300, Image.SCALE_SMOOTH));
            JLabel imageLabel = new JLabel(icon);
            imagePanel.removeAll();
            imagePanel.add(imageLabel, BorderLayout.CENTER);
            imagePanel.revalidate();
            imagePanel.repaint();
            
            // Now try to read the QR code using ZXing
            try {
                // Set up the QR code reader
                LuminanceSource source = new BufferedImageLuminanceSource(image);
                BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(source));
                
                // Try to decode the QR code
                Map<DecodeHintType, Object> hints = new HashMap<>();
                hints.put(DecodeHintType.TRY_HARDER, Boolean.TRUE);
                
                Result qrResult = new MultiFormatReader().decode(bitmap, hints);
                String qrData = qrResult.getText();
                
                statusLabel.setText("QR code read successfully!");
                statusLabel.setForeground(new Color(0, 120, 0));
                
                // Process the QR code data
                processQRData(qrData);
                
            } catch (Exception e) {
                // QR code reading failed
                statusLabel.setText("Could not read QR code: " + e.getMessage());
                statusLabel.setForeground(Color.RED);
                
                // Still allow manual entry as fallback
                String manualData = JOptionPane.showInputDialog(this, 
                        "QR code reading failed. Enter the encrypted data manually:",
                        "Manual QR Code Data Entry",
                        JOptionPane.QUESTION_MESSAGE);
                
                if (manualData != null && !manualData.isEmpty()) {
                    processQRData(manualData);
                }
            }
            
        } catch (IOException e) {
            statusLabel.setText("Error reading image: " + e.getMessage());
            confirmButton.setEnabled(false);
        }
    }
    
    /**
     * Process the data from the QR code
     * @param qrData The data read from the QR code
     */
    private void processQRData(String qrData) {
        try {
            // First verify this is a valid QR code from our system
            if (!BankQRGenerator.verifyQRCode(qrData)) {
                statusLabel.setText("Invalid QR code. Not generated by Kurdish-O-Banking system.");
                statusLabel.setForeground(Color.RED);
                confirmButton.setEnabled(false);
                return;
            }
            
            // Decrypt the data
            String decryptedData = BankQRGenerator.decryptData(qrData);
            
            // Parse account information
            int[] accountInfo = BankQRGenerator.parseAccountInfo(decryptedData);
            
            if (accountInfo == null) {
                statusLabel.setText("Error: Could not parse account information from QR code.");
                statusLabel.setForeground(Color.RED);
                confirmButton.setEnabled(false);
                return;
            }
            
            int qrUserId = accountInfo[0];
            int accountId = accountInfo[1];
            
            // Don't allow transfers to your own account
            if (qrUserId == userId) {
                statusLabel.setText("Error: Cannot transfer to your own account.");
                statusLabel.setForeground(Color.RED);
                confirmButton.setEnabled(false);
                return;
            }
            
            // Store scanned data
            scannedAccountId = accountId;
            
            // Look up account details in database
            lookupAccountDetails(accountId);
            
        } catch (Exception e) {
            statusLabel.setText("Error processing QR code: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            confirmButton.setEnabled(false);
        }
    }
    
    /**
     * Look up account details from the database
     * @param accountId The account ID to look up
     */
    private void lookupAccountDetails(int accountId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT a.username, a.account_type FROM accounts a WHERE a.account_id = ?";
            
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, accountId);
            
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                scannedAccountUsername = rs.getString("username");
                scannedAccountType = rs.getString("account_type");
                
                // Display success message
                statusLabel.setText("Account found: " + scannedAccountUsername + " - " + 
                                   scannedAccountType + " (ID: " + accountId + ")");
                statusLabel.setForeground(new Color(0, 120, 0));
                
                // Enable the confirm button
                confirmButton.setEnabled(true);
            } else {
                statusLabel.setText("Error: Account not found in the database.");
                statusLabel.setForeground(Color.RED);
                confirmButton.setEnabled(false);
            }
            
        } catch (SQLException e) {
            statusLabel.setText("Database error: " + e.getMessage());
            statusLabel.setForeground(Color.RED);
            confirmButton.setEnabled(false);
        }
    }
    
    /**
     * Return the scanned account details to the transfer page
     */
    private void returnToTransfer() {
        if (scannedAccountId > 0 && !scannedAccountUsername.isEmpty()) {
            // Call the method in Transfer to set the recipient details
            parentTransfer.setRecipientFromQRCode(scannedAccountId, scannedAccountUsername);
            
            // Close this window
            dispose();
        } else {
            JOptionPane.showMessageDialog(this,
                    "No valid account information found.",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
        }
    }
}