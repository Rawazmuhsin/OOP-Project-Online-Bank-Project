package bank.pr;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
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
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.common.BitMatrix;

public class BankQRGenerator {
    
    // Encryption key for securing QR code data
    private static final String ENCRYPTION_KEY = "KurdishOBank2024"; // Exactly 16 characters    
    
    /**
     * Generates a QR code for a specific user account and shows the encrypted data
     * @param userId The user ID
     * @param accountId The account ID 
     * @param accountType The type of account (Checking/Savings)
     * @return BufferedImage containing the QR code
     */
    public static BufferedImage generateQRCode(int userId, int accountId, String accountType) {
        try {
            // Create the data string
            String data = String.format("BANK:KB;UID:%d;AID:%d;TYPE:%s", 
                    userId, accountId, accountType);
            
            // Encrypt the data for security
            String encryptedData = encryptData(data);
            
            // Display the encrypted data for later use in scanning
            System.out.println("======== QR CODE DATA ========");
            System.out.println("For account ID " + accountId + ":");
            System.out.println("Encrypted data: " + encryptedData);
            System.out.println("=============================");
            
            // Optionally show this to the user when generating a QR code
            JOptionPane.showMessageDialog(null,
                "Generated QR code for " + accountType + " account.\n\n" +
                "Encrypted data: " + encryptedData + "\n\n" +
                "Please save this data for testing QR code scanning.",
                "QR Code Generated",
                JOptionPane.INFORMATION_MESSAGE);
            
            // Generate QR code using ZXing library
            return createQRCodeImage(encryptedData, 300, 300);
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null,
                "Error processing account data: " + e.getMessage(),
                "System Error",
                JOptionPane.ERROR_MESSAGE);
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
     * This is a basic implementation with limited capacity and error correction
     * @param data The data to encode in the QR code
     * @param width The width of the QR code image
     * @param height The height of the QR code image
     * @return BufferedImage containing a custom QR-like code
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
        drawPositionDetectionPattern(graphics, 50, 50, 40);
        drawPositionDetectionPattern(graphics, width - 90, 50, 40);
        drawPositionDetectionPattern(graphics, 50, height - 90, 40);
        
        // Draw data pattern based on the hash
        int blockSize = 10;
        int margin = 100;
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
        
        // Draw a border with the account ID and type encoded
        String borderText = "ID:" + hash;
        graphics.drawString(borderText, width/2 - 20, height - 20);
        
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
     * @param userId User ID
     * @param userName User name for display purposes
     * @return JPanel containing the QR code display
     */
    public static JPanel createQRCodePanel(int userId, String userName) {
        JPanel qrPanel = new JPanel();
        qrPanel.setLayout(new BorderLayout(10, 10));
        qrPanel.setBackground(Color.WHITE);
        qrPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Get account details from database
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Create a tabbed pane to show QR codes for different accounts
            JTabbedPane tabbedPane = new JTabbedPane();
            
            String query = "SELECT account_id, account_type FROM accounts WHERE username = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, userName);
            
            ResultSet rs = stmt.executeQuery();
            boolean hasAccounts = false;
            
            while (rs.next()) {
                hasAccounts = true;
                int accountId = rs.getInt("account_id");
                String accountType = rs.getString("account_type");
                
                // Create panel for this account
                JPanel accountQRPanel = new JPanel();
                accountQRPanel.setLayout(new BorderLayout());
                accountQRPanel.setBackground(Color.WHITE);
                
                // Generate QR code
                BufferedImage qrImage = generateQRCode(userId, accountId, accountType);
                JLabel qrCodeLabel = new JLabel(new ImageIcon(qrImage));
                
                // Add account info label
                JLabel accountInfoLabel = new JLabel(
                    String.format("<html><b>%s Account</b><br>Account ID: %d<br>User ID: %d</html>", 
                    accountType, accountId, userId));
                accountInfoLabel.setHorizontalAlignment(SwingConstants.CENTER);
                accountInfoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
                
                // Create export button
                JButton exportButton = new JButton("Export QR Code");
                exportButton.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        saveQRCodeToFile(qrImage, userName + "_" + accountType);
                    }
                });
                
                // Add to panel
                accountQRPanel.add(accountInfoLabel, BorderLayout.NORTH);
                accountQRPanel.add(qrCodeLabel, BorderLayout.CENTER);
                accountQRPanel.add(exportButton, BorderLayout.SOUTH);
                
                // Add to tabbed pane
                tabbedPane.addTab(accountType, accountQRPanel);
            }
            
            if (hasAccounts) {
                qrPanel.add(tabbedPane, BorderLayout.CENTER);
            } else {
                qrPanel.add(new JLabel("No accounts found for this user."), BorderLayout.CENTER);
            }
            
        } catch (SQLException ex) {
            ex.printStackTrace();
            qrPanel.add(new JLabel("Error loading account data: " + ex.getMessage()), BorderLayout.CENTER);
        }
        
        JLabel titleLabel = new JLabel("Your Banking QR Codes");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        qrPanel.add(titleLabel, BorderLayout.NORTH);
        
        return qrPanel;
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