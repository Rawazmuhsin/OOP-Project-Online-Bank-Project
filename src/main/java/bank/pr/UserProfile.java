package bank.pr;

import java.awt.*;
import javax.swing.*;

public class UserProfile extends JFrame {

    private static final Color OFF_WHITE = new Color(245, 245, 245);
    private static final Color DARK_BLUE = new Color(33, 64, 95);
    private static final Color BRIGHT_RED = new Color(220, 53, 69);

    public UserProfile() {
        setTitle("User Profile");
        setSize(600, 520); // Adjusted for more space to fit all components
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(OFF_WHITE);

      
        JPanel titlePanel = new JPanel();
        titlePanel.setBounds(0, 20, 600, 50);
        titlePanel.setBackground(DARK_BLUE);
        JLabel titleLabel = new JLabel("User Profile");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 26));
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel);

       
        JPanel profileContainer = new JPanel();
        profileContainer.setLayout(null);
        profileContainer.setBounds(50, 90, 500, 300);
        profileContainer.setBackground(OFF_WHITE);
        profileContainer.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 2));
        mainPanel.add(profileContainer);

        
        JLabel profilePhoto = new JLabel("Photo", SwingConstants.CENTER);
        profilePhoto.setBounds(200, 20, 100, 100);
        profilePhoto.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 2));
        profilePhoto.setOpaque(true);
        profilePhoto.setBackground(Color.WHITE);
        profileContainer.add(profilePhoto);

        
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(30, 140, 100, 25);
        nameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        nameLabel.setForeground(DARK_BLUE);
        profileContainer.add(nameLabel);

        JTextField nameField = new JTextField("Alex Johnson");
        nameField.setBounds(120, 140, 350, 30);
        nameField.setFont(new Font("Arial", Font.PLAIN, 14));
        nameField.setBackground(Color.WHITE);
        nameField.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 2));
        profileContainer.add(nameField);

      
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(30, 180, 100, 25);
        emailLabel.setFont(new Font("Arial", Font.BOLD, 14));
        emailLabel.setForeground(DARK_BLUE);
        profileContainer.add(emailLabel);

        JTextField emailField = new JTextField("alex.johnson@example.com");
        emailField.setBounds(120, 180, 350, 30);
        emailField.setFont(new Font("Arial", Font.PLAIN, 14));
        emailField.setBackground(Color.WHITE);
        emailField.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 2));
        profileContainer.add(emailField);

        
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setBounds(30, 220, 100, 25);
        phoneLabel.setFont(new Font("Arial", Font.BOLD, 14));
        phoneLabel.setForeground(DARK_BLUE);
        profileContainer.add(phoneLabel);

        JTextField phoneField = new JTextField("(555) 123-4567");
        phoneField.setBounds(120, 220, 350, 30);
        phoneField.setFont(new Font("Arial", Font.PLAIN, 14));
        phoneField.setBackground(Color.WHITE);
        phoneField.setBorder(BorderFactory.createLineBorder(DARK_BLUE, 2));
        profileContainer.add(phoneField);

       
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(50, 400, 500, 50);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(OFF_WHITE);

        JButton editProfileButton = new JButton("Edit Profile");
        editProfileButton.setPreferredSize(new Dimension(150, 40));
        editProfileButton.setBackground(DARK_BLUE);
        editProfileButton.setForeground(Color.WHITE);
        editProfileButton.setFont(new Font("Arial", Font.BOLD, 14));
        editProfileButton.setFocusPainted(false);
        buttonPanel.add(editProfileButton);

        JButton changePasswordButton = new JButton("Change Password");
        changePasswordButton.setPreferredSize(new Dimension(150, 40));
        changePasswordButton.setBackground(DARK_BLUE);
        changePasswordButton.setForeground(Color.WHITE);
        changePasswordButton.setFont(new Font("Arial", Font.BOLD, 14));
        changePasswordButton.setFocusPainted(false);
        buttonPanel.add(changePasswordButton);

        mainPanel.add(buttonPanel);

      
        JButton deleteAccountButton = new JButton("Delete Account");
        deleteAccountButton.setBounds(225, 460, 150, 40); // Positioned at the bottom center
        deleteAccountButton.setBackground(BRIGHT_RED);
        deleteAccountButton.setForeground(Color.WHITE);
        deleteAccountButton.setFont(new Font("Arial", Font.BOLD, 14));
        deleteAccountButton.setFocusPainted(false);
        deleteAccountButton.setBorderPainted(false);
        mainPanel.add(deleteAccountButton);

        add(mainPanel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new UserProfile().setVisible(true);
        });
    }
}
