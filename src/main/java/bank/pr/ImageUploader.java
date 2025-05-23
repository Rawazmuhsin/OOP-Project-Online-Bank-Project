package bank.pr;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

public class ImageUploader {
    
    private static final String IMAGE_DIRECTORY = "user_images";
    
    // Create directories if not exist
    static {
        File directory = new File(IMAGE_DIRECTORY);
        if (!directory.exists()) {
            directory.mkdirs();
        }
    }
    
   
    public static BufferedImage browseForImage(JFrame parent) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Select Profile Image");
        
        // Set file filter for images
        FileNameExtensionFilter filter = new FileNameExtensionFilter(
                "Image Files", "jpg", "jpeg", "png", "gif");
        fileChooser.setFileFilter(filter);
        
        if (fileChooser.showOpenDialog(parent) == JFileChooser.APPROVE_OPTION) {
            try {
                File selectedFile = fileChooser.getSelectedFile();
                BufferedImage originalImage = ImageIO.read(selectedFile);
                
                // Validate image size
                if (originalImage == null) {
                    JOptionPane.showMessageDialog(parent, 
                            "Selected file is not a valid image.",
                            "Invalid Image", 
                            JOptionPane.ERROR_MESSAGE);
                    return null;
                }
                
                // Resize image if needed (optional)
                return originalImage;
            } catch (IOException e) {
                JOptionPane.showMessageDialog(parent, 
                        "Error reading image: " + e.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        }
        return null;
    }
    
    
    public static boolean saveUserImage(int userId, BufferedImage image) {
        if (image == null) return false;
        
        try {
            // Create a filename based on user ID
            String fileName = userId + "_profile.png";
            File outputFile = new File(IMAGE_DIRECTORY, fileName);
            
            // Save the image
            ImageIO.write(image, "png", outputFile);
            
            // Update the database with the image path
            updateImagePathInDB(userId, outputFile.getPath());
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
 
    private static void updateImagePathInDB(int userId, String imagePath) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            // Check if we need to add a new column to the accounts table
            addImageColumnIfNotExists(conn);
            
            // Update the user's image path
            String updateQuery = "UPDATE accounts SET profile_image = ? WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(updateQuery);
            stmt.setString(1, imagePath);
            stmt.setInt(2, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    
   
    private static void addImageColumnIfNotExists(Connection conn) throws SQLException {
        try {
            // Check if column exists
            PreparedStatement checkStmt = conn.prepareStatement(
                    "SELECT * FROM INFORMATION_SCHEMA.COLUMNS WHERE " +
                    "TABLE_NAME = 'accounts' AND COLUMN_NAME = 'profile_image'");
            ResultSet rs = checkStmt.executeQuery();
            
            if (!rs.next()) {
                // Column doesn't exist, add it
                PreparedStatement alterStmt = conn.prepareStatement(
                        "ALTER TABLE accounts ADD COLUMN profile_image VARCHAR(255)");
                alterStmt.executeUpdate();
            }
        } catch (SQLException e) {
            // For databases that don't support INFORMATION_SCHEMA, try a different approach
            try {
                // Just try to add the column and catch the exception if it already exists
                PreparedStatement alterStmt = conn.prepareStatement(
                        "ALTER TABLE accounts ADD COLUMN profile_image VARCHAR(255)");
                alterStmt.executeUpdate();
            } catch (SQLException ex) {
                // Column likely already exists, ignore
            }
        }
    }
    
    public static BufferedImage loadUserImage(int userId) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT profile_image FROM accounts WHERE account_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                String imagePath = rs.getString("profile_image");
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        return ImageIO.read(imageFile);
                    }
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        
        return null;
    }
    
    
    public static BufferedImage resizeImage(BufferedImage originalImage, int targetWidth, int targetHeight) {
        Image resultingImage = originalImage.getScaledInstance(targetWidth, targetHeight, Image.SCALE_SMOOTH);
        BufferedImage resizedImage = new BufferedImage(targetWidth, targetHeight, BufferedImage.TYPE_INT_ARGB);
        
        resizedImage.getGraphics().drawImage(resultingImage, 0, 0, null);
        return resizedImage;
    }
}