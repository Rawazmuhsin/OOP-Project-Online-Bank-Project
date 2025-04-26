package bank.pr;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

/**
 * Utility class to generate default avatar images
 * This creates a simple colored circle with initials when no photo is available
 */
public class DefaultAvatarGenerator {
    
    /**
     * Generate a default avatar with the user's initials
     * @param username The username to get initials from
     * @param width The width of the avatar image
     * @param height The height of the avatar image
     * @return A BufferedImage containing the generated avatar
     */
    public static BufferedImage generateDefaultAvatar(String username, int width, int height) {
        BufferedImage avatar = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = avatar.createGraphics();
        
        // Enable anti-aliasing
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Generate a color based on the hash of the username
        Color backgroundColor = getColorFromUsername(username);
        
        // Fill background
        g2.setColor(backgroundColor);
        g2.fillOval(0, 0, width, height);
        
        // Get initials from username
        String initials = getInitials(username);
        
        // Draw initials
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, width / 2));
        
        // Center text
        int textWidth = g2.getFontMetrics().stringWidth(initials);
        int textHeight = g2.getFontMetrics().getHeight();
        g2.drawString(initials, (width - textWidth) / 2, 
                      height / 2 + textHeight / 4); // Approximately center vertically
        
        g2.dispose();
        return avatar;
    }
    
    /**
     * Extract initials from a username
     * @param username The input username
     * @return Initials (up to 2 characters)
     */
    private static String getInitials(String username) {
        if (username == null || username.isEmpty()) {
            return "?";
        }
        
        // Get first character
        StringBuilder initials = new StringBuilder();
        initials.append(Character.toUpperCase(username.charAt(0)));
        
        // Try to find a second initial if username has multiple parts
        if (username.contains(" ")) {
            String[] parts = username.split(" ");
            if (parts.length > 1 && parts[1].length() > 0) {
                initials.append(Character.toUpperCase(parts[1].charAt(0)));
            }
        } else if (username.length() > 1) {
            // If no space, use second character
            initials.append(Character.toLowerCase(username.charAt(1)));
        }
        
        return initials.toString();
    }
    
    /**
     * Generate a consistent color based on a username
     * @param username The input username
     * @return A Color object
     */
    private static Color getColorFromUsername(String username) {
        if (username == null || username.isEmpty()) {
            return new Color(150, 150, 150);
        }
        
        // Generate a simple hash from the username
        int hash = username.hashCode();
        
        // Use the hash to generate HSB color components
        // Hue: 0-360 degrees (mapped to 0-1.0)
        float hue = Math.abs(hash % 360) / 360.0f;
        
        // Saturation: 60-90%
        float saturation = 0.6f + (Math.abs((hash / 360) % 30) / 100.0f);
        
        // Brightness: 60-90%
        float brightness = 0.6f + (Math.abs((hash / 10800) % 30) / 100.0f);
        
        return Color.getHSBColor(hue, saturation, brightness);
    }
}