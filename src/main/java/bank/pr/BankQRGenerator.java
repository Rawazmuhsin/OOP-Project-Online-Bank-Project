package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

public class BankQRGenerator {
    
    // Encryption key for securing QR code data
    private static final String ENCRYPTION_KEY = "KurdishOBank2024"; // Exactly 16 characters    
    
    /**
     * Generates a QR code for a specific user account without showing dialog messages
     * @param userId The user ID
     * @param accountId The account ID 
     * @param accountType The type of account (Checking/Savings)
     * @return BufferedImage containing the QR code
     */
    public static BufferedImage generateQRCodeSilent(int userId, int accountId, String accountType) {
        try {
            // Create the data string
            String data = String.format("BANK:KB;UID:%d;AID:%d;TYPE:%s", 
                    userId, accountId, accountType);
            
            // Encrypt the data for security
            String encryptedData = encryptData(data);
            
            // Log the data for debugging purposes only
            System.out.println("Generated QR code for " + accountType + 
                    " account (ID: " + accountId + ") with encrypted data: " + encryptedData);
            
            // Generate QR code using ZXing library
            return createQRCodeImage(encryptedData, 200, 200);
            
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Create a standard QR code image using ZXing library
     * @param data The data to encode in the QR code
     * @param width The width of the QR code image
     * @param height The height of the QR code image
     * @return BufferedImage containing the QR code
     */
    private static BufferedImage createQRCodeImage(String data, int width, int height) {
        try {
            // Use ZXing to generate a standard QR code that can be read by scanners
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, width, height);
            
            // Convert to BufferedImage
            BufferedImage qrCode = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    qrCode.setRGB(x, y, bitMatrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
                }
            }
            return qrCode;
        } catch (WriterException e) {
            e.printStackTrace();
            // Fall back to our custom implementation if ZXing fails
            return createCustomQRCodeImage(data, width, height);
        }
    }
    
    /**
     * Create a custom QR code image as fallback if ZXing fails
     */
    private static BufferedImage createCustomQRCodeImage(String data, int width, int height) {
        // Create a black and white image
        BufferedImage qrCode = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D graphics = qrCode.createGraphics();
        
        // Fill background with white
        graphics.setColor(Color.WHITE);
        graphics.fillRect(0, 0, width, height);
        
        // Calculate a hash of the data to create a visual pattern
        int hash = data.hashCode();
        
        // Use the hash to create a deterministic pattern
        graphics.setColor(Color.BLACK);
        
        // Draw position detection patterns (corners)
        int patternSize = width / 7;
        drawPositionDetectionPattern(graphics, patternSize, patternSize, patternSize);
        drawPositionDetectionPattern(graphics, width - (patternSize * 2), patternSize, patternSize);
        drawPositionDetectionPattern(graphics, patternSize, height - (patternSize * 2), patternSize);
        
        // Draw data pattern based on the hash
        int blockSize = Math.max(3, width / 30);
        int margin = Math.max(patternSize * 3, width / 4);
        int cols = (width - 2 * margin) / blockSize;
        int rows = (height - 2 * margin) / blockSize;
        
        // Convert the string to bytes and use those for the pattern
        byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
        
        for (int i = 0; i < rows && i < bytes.length; i++) {
            for (int j = 0; j < cols && (i * cols + j) < bytes.length; j++) {
                // Use the byte value to determine if we draw a block
                int byteValue = bytes[i * cols + j] & 0xFF;
                if (byteValue % 2 == 0) {
                    graphics.fillRect(margin + j * blockSize, margin + i * blockSize, 
                                     blockSize, blockSize);
                }
            }
        }
        
        graphics.dispose();
        return qrCode;
    }
    
    /**
     * Draw a position detection pattern for the QR code
     */
    private static void drawPositionDetectionPattern(Graphics2D g, int x, int y, int size) {
        // Outer square
        g.setColor(Color.BLACK);
        g.fillRect(x, y, size, size);
        
        // Middle white square
        g.setColor(Color.WHITE);
        g.fillRect(x + size/6, y + size/6, size - size/3, size - size/3);
        
        // Inner black square
        g.setColor(Color.BLACK);
        g.fillRect(x + size/3, y + size/3, size/3, size/3);
    }
    
    /**
     * Creates a panel containing the QR code and export button
     * Improved layout to better use available space
     * @param userId User ID
     * @param userName User name for display purposes
     * @return JPanel containing the QR code display
     */
    public static JPanel createQRCodePanel(int userId, String userName) {
        JPanel qrPanel = new JPanel();
        qrPanel.setLayout(new BorderLayout(10, 10));
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Add title at the top
        JLabel titleLabel = new JLabel("Your Account QR Codes");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        qrPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 20));
        contentPanel.setBackground(Color.WHITE);
        
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT account_id, account_type, balance FROM accounts WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userName);
            
            ResultSet rs = stmt.executeQuery();
            boolean hasAccounts = false;
            
            while (rs.next()) {
                hasAccounts = true;
                int accountId = rs.getInt("account_id");
                String accountType = rs.getString("account_type");
                double balance = rs.getDouble("balance");
                
                // Create a card for each account
                JPanel accountCard = createAccountQRCard(userId, accountId, accountType, balance, userName);
                contentPanel.add(accountCard);
            }
            
            if (!hasAccounts) {
                JLabel noAccountsLabel = new JLabel("No accounts found for this user.");
                noAccountsLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
                contentPanel.add(noAccountsLabel);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            JLabel errorLabel = new JLabel("Error loading account data: " + ex.getMessage());
            errorLabel.setForeground(Color.RED);
            contentPanel.add(errorLabel);
        }
        
        qrPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add instructions at the bottom
        JLabel instructionsLabel = new JLabel(
            "<html><p>Use these QR codes to quickly transfer money between accounts or share your account details securely.</p></html>");
        instructionsLabel.setFont(new Font("SansSerif", Font.ITALIC, 12));
        instructionsLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionsLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        qrPanel.add(instructionsLabel, BorderLayout.SOUTH);
        
        return qrPanel;
    }
    
    /**
     * Create a card for displaying a single account's QR code
     */
    private static JPanel createAccountQRCard(int userId, int accountId, String accountType, double balance, String userName) {
        JPanel card = new JPanel();
        card.setLayout(new BorderLayout(5, 5));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setPreferredSize(new Dimension(240, 340));
        
        // Account header panel
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new GridLayout(3, 1, 0, 2));
        headerPanel.setBackground(Color.WHITE);
        
        JLabel accountTypeLabel = new JLabel(accountType + " Account");
        accountTypeLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        accountTypeLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel accountIdLabel = new JLabel("Account ID: " + accountId);
        accountIdLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        accountIdLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JLabel balanceLabel = new JLabel(String.format("Balance: $%.2f", balance));
        balanceLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        balanceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        headerPanel.add(accountTypeLabel);
        headerPanel.add(accountIdLabel);
        headerPanel.add(balanceLabel);
        
        // QR code image
        BufferedImage qrImage = generateQRCodeSilent(userId, accountId, accountType);
        JPanel qrPanel = new JPanel(new BorderLayout());
        qrPanel.setBackground(Color.WHITE);
        
        if (qrImage != null) {
            JLabel qrLabel = new JLabel(new ImageIcon(qrImage));
            qrLabel.setHorizontalAlignment(SwingConstants.CENTER);
            qrPanel.add(qrLabel, BorderLayout.CENTER);
        } else {
            JLabel errorLabel = new JLabel("Error generating QR code");
            errorLabel.setHorizontalAlignment(SwingConstants.CENTER);
            errorLabel.setForeground(Color.RED);
            qrPanel.add(errorLabel, BorderLayout.CENTER);
        }
        
        // Export button
        JButton exportButton = new JButton("Export QR Code");
        exportButton.setFocusPainted(false);
        
        // Only enable if we have a valid QR code
        if (qrImage != null) {
            final BufferedImage finalQrImage = qrImage;  // Need final for lambda
            exportButton.addActionListener(e -> saveQRCodeToFile(finalQrImage, userName + "_" + accountType + "_" + accountId));
        } else {
            exportButton.setEnabled(false);
        }
        
        // Add all to card
        card.add(headerPanel, BorderLayout.NORTH);
        card.add(qrPanel, BorderLayout.CENTER);
        card.add(exportButton, BorderLayout.SOUTH);
        
        return card;
    }
    
    /**
     * Save QR code image to file
     * @param qrImage The QR code image
     * @param baseFileName Base name for the file
     */
    private static void saveQRCodeToFile(BufferedImage qrImage, String baseFileName) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save QR Code");
        fileChooser.setSelectedFile(new File(baseFileName + "_qrcode.png"));
        fileChooser.setFileFilter(new FileNameExtensionFilter("PNG Images", "png"));
        
        int userSelection = fileChooser.showSaveDialog(null);
        
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            
            // Ensure filename has .png extension
            if (!fileToSave.getName().toLowerCase().endsWith(".png")) {
                fileToSave = new File(fileToSave.getAbsolutePath() + ".png");
            }
            
            try {
                ImageIO.write(qrImage, "PNG", fileToSave);
                JOptionPane.showMessageDialog(null, 
                    "QR Code saved successfully to: " + fileToSave.getAbsolutePath(),
                    "Save Successful", 
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(null,
                    "Error saving QR code: " + ex.getMessage(),
                    "Save Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    /**
     * Encrypt the data for the QR code
     * @param data The data to encrypt
     * @return Encrypted data string
     */
    private static String encryptData(String data) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(
            ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        
        byte[] encryptedBytes = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedBytes);
    }
    
    /**
     * Decrypt QR code data
     * @param encryptedData The encrypted data from QR code
     * @return Original data string
     */
    public static String decryptData(String encryptedData) throws Exception {
        SecretKeySpec secretKey = new SecretKeySpec(
            ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8), "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        
        byte[] decodedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedBytes = cipher.doFinal(decodedBytes);
        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
    
    /**
     * Utility method to verify a QR code
     * @param encryptedData The encrypted data from the QR scanner
     * @return true if the QR code is valid and from this system
     */
    public static boolean verifyQRCode(String encryptedData) {
        try {
            String decryptedData = decryptData(encryptedData);
            // Check if data matches our format
            return decryptedData.startsWith("BANK:KB;");
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Parse account information from decrypted QR code data
     * @param decryptedData The decrypted data string
     * @return int array containing [userId, accountId] or null if invalid
     */
    public static int[] parseAccountInfo(String decryptedData) {
        try {
            // Parse the format BANK:KB;UID:123;AID:456;TYPE:Checking
            String[] parts = decryptedData.split(";");
            int userId = 0;
            int accountId = 0;
            
            for (String part : parts) {
                String[] keyValue = part.split(":");
                if (keyValue.length == 2) {
                    if (keyValue[0].equals("UID")) {
                        userId = Integer.parseInt(keyValue[1]);
                    } else if (keyValue[0].equals("AID")) {
                        accountId = Integer.parseInt(keyValue[1]);
                    }
                }
            }
            
            if (userId > 0 && accountId > 0) {
                return new int[]{userId, accountId};
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return null;
    }
}