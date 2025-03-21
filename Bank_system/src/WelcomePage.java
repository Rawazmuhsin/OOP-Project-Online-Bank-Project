import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class WelcomePage extends JFrame implements ActionListener {
    
    private static final Color DARK_BLUE = new Color(25, 33, 43);
    private static final Color ORANGE = new Color(255, 140, 0);
    private static final Color LIGHT_GRAY = new Color(240, 240, 240);
    private static final Color WHITE = Color.WHITE;
    
    public WelcomePage() {
        setTitle("Online Banking System");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        
     
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(null);
        mainPanel.setBackground(DARK_BLUE);
        
       
        JPanel titlePanel = new JPanel();
        titlePanel.setBounds(0, 30, 600, 60);
        titlePanel.setBackground(DARK_BLUE);
        JLabel titleLabel = new JLabel("Welcome to Online Banking");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 32));
        titleLabel.setForeground(WHITE);
        titlePanel.add(titleLabel);
        mainPanel.add(titlePanel);
        
      
        JLabel subtitleLabel = new JLabel("Secure • Reliable • Convenient");
        subtitleLabel.setBounds(0, 90, 600, 30);
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        subtitleLabel.setForeground(ORANGE);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(subtitleLabel);
        
      
        JPanel featuresPanel = new JPanel();
        featuresPanel.setBounds(50, 140, 500, 50);
        featuresPanel.setLayout(new GridLayout(1, 1, 0, 0));
        featuresPanel.setBackground(DARK_BLUE);
        
        String[] features = {
            "Easy Money Transfer"
        };
        
        for (String feature : features) {
            JLabel featureLabel = new JLabel(feature);
            featureLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            featureLabel.setForeground(WHITE);
            featureLabel.setHorizontalAlignment(JLabel.CENTER);
            featuresPanel.add(featureLabel);
        }
        
        mainPanel.add(featuresPanel);
        
    
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBounds(50, 210, 500, 50);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setBackground(DARK_BLUE);
        
      
        JButton loginButton = new JButton("Login");
        loginButton.setPreferredSize(new Dimension(150, 40));
        loginButton.setBackground(ORANGE);
        loginButton.setForeground(WHITE);
        loginButton.setFont(new Font("Arial", Font.BOLD, 16));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.addActionListener(e -> {
            this.dispose();

        });
        buttonPanel.add(loginButton);
        
       
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setPreferredSize(new Dimension(150, 40));
        signUpButton.setBackground(LIGHT_GRAY);
        signUpButton.setForeground(DARK_BLUE);
        signUpButton.setFont(new Font("Arial", Font.BOLD, 16));
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        signUpButton.addActionListener(e -> {
            this.dispose();

        });
        buttonPanel.add(signUpButton);
        
        mainPanel.add(buttonPanel);
        
        // Security message
        JLabel securityLabel = new JLabel("Your security is our top priority");
        securityLabel.setBounds(0, 340, 600, 20);
        securityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        securityLabel.setForeground(LIGHT_GRAY);
        securityLabel.setHorizontalAlignment(JLabel.CENTER);
        mainPanel.add(securityLabel);
        
        add(mainPanel);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        // Action handling if needed
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new WelcomePage().setVisible(true);
        });
    }
}