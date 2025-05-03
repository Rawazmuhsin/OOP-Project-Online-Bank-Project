package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class WelcomePage extends JFrame {
    
    private static final long serialVersionUID = 1L;
    
    // Colors scheme - same as Dashboard and Transfer
    private static final Color PRIMARY_COLOR = new Color(20, 30, 70);
    private static final Color SECONDARY_COLOR = new Color(30, 144, 255);
    private static final Color ACCENT_COLOR = new Color(255, 165, 0);
    private static final Color BACKGROUND_COLOR = new Color(245, 247, 250);
    private static final Color TEXT_COLOR = new Color(50, 50, 50);
    private static final Color LIGHT_TEXT_COLOR = new Color(120, 120, 120);

    public WelcomePage() {
        // Set look and feel to be more modern
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        setTitle("Welcome - Kurdish-O-Banking (KOB)");
        setSize(1100, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);
        
        // Create sidebar with gradient background
        JPanel sidebar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create gradient background
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR, 
                    0, getHeight(), new Color(10, 20, 50)
                );
                g2d.setPaint(gradient);
                g2d.fillRect(0, 0, getWidth(), getHeight());
                g2d.dispose();
            }
        };
        
        sidebar.setPreferredSize(new Dimension(300, getHeight()));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Add logo to sidebar
        JLabel logoLabel = new JLabel();
        try {
            // Path to your saved logo
            ImageIcon logoIcon = new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png");
            // Resize the image
            java.awt.Image image = logoIcon.getImage().getScaledInstance(150, 100, java.awt.Image.SCALE_SMOOTH);
            logoIcon = new ImageIcon(image);
            logoLabel.setIcon(logoIcon);
            logoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            logoLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        } catch (Exception e) {
            System.err.println("Error loading logo: " + e.getMessage());
        }
        
        // Bank name and tagline
        JLabel bankNameLabel = new JLabel("Kurdish-O-Banking");
        bankNameLabel.setForeground(Color.WHITE);
        bankNameLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        bankNameLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel taglineLabel = new JLabel("Your Future, Your Bank");
        taglineLabel.setForeground(new Color(200, 200, 200));
        taglineLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        taglineLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Add sidebar content
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(logoLabel);
        sidebar.add(Box.createVerticalStrut(20));
        sidebar.add(bankNameLabel);
        sidebar.add(Box.createVerticalStrut(10));
        sidebar.add(taglineLabel);
        sidebar.add(Box.createVerticalGlue());
        
        // Add testimonial or feature highlight at the bottom of sidebar
        JPanel testimonialPanel = new RoundedPanel(15, new Color(40, 50, 90));
        testimonialPanel.setLayout(new BoxLayout(testimonialPanel, BoxLayout.Y_AXIS));
        testimonialPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        testimonialPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        testimonialPanel.setMaximumSize(new Dimension(250, 200));
        
        JLabel quoteLabel = new JLabel("\u201cThe best banking experience.\u201d");
        quoteLabel.setForeground(Color.WHITE);
        quoteLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        quoteLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel authorLabel = new JLabel("- Satisfied Customer");
        authorLabel.setForeground(new Color(200, 200, 200));
        authorLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        authorLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        testimonialPanel.add(quoteLabel);
        testimonialPanel.add(Box.createVerticalStrut(10));
        testimonialPanel.add(authorLabel);
        
        sidebar.add(testimonialPanel);
        sidebar.add(Box.createVerticalGlue());
        
        // Main content panel
        JPanel content = new JPanel();
        content.setBackground(BACKGROUND_COLOR);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(80, 60, 60, 60));
        
        // Welcome section
        JLabel welcomeLabel = new JLabel("Welcome to KOB");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 36));
        welcomeLabel.setForeground(TEXT_COLOR);
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("<html>Experience a new way of banking with our secure, convenient,<br>" +
                "and user-friendly online platform. Manage your finances<br>" +
                "anywhere, anytime with ease and confidence.</html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 16));
        descLabel.setForeground(LIGHT_TEXT_COLOR);
        descLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 40, 0));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Feature cards
        JPanel featurePanel = new JPanel();
        featurePanel.setLayout(new GridLayout(1, 3, 20, 0));
        featurePanel.setOpaque(false);
        featurePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        featurePanel.setMaximumSize(new Dimension(800, 200));
        
        featurePanel.add(createFeatureCard("Secure Transactions", "Bank with confidence using our state-of-the-art security protocols", SECONDARY_COLOR));
        featurePanel.add(createFeatureCard("24/7 Access", "Access your accounts anytime, anywhere through our online platform", new Color(76, 175, 80)));
        featurePanel.add(createFeatureCard("Easy Transfers", "Transfer money instantly between accounts with just a few clicks", ACCENT_COLOR));
        
        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new BoxLayout(buttonsPanel, BoxLayout.X_AXIS));
        buttonsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        buttonsPanel.setBorder(BorderFactory.createEmptyBorder(40, 0, 0, 0));
        
        JButton loginButton = new JButton("Log In");
        loginButton.setBackground(SECONDARY_COLOR);
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        loginButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        JButton signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(240, 240, 240));
        signupButton.setForeground(TEXT_COLOR);
        signupButton.setFocusPainted(false);
        signupButton.setFont(new Font("SansSerif", Font.BOLD, 16));
        signupButton.setBorder(BorderFactory.createEmptyBorder(15, 30, 15, 30));
        
        // Add hover effects
        loginButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                loginButton.setBackground(new Color(25, 118, 210));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                loginButton.setBackground(SECONDARY_COLOR);
            }
        });
        
        signupButton.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                signupButton.setBackground(new Color(230, 230, 230));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                signupButton.setBackground(new Color(240, 240, 240));
            }
        });
        
        // Action listeners
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close WelcomePage
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new LoginUI().setVisible(true);
                    }
                });
            }
        });

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close WelcomePage
                SwingUtilities.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        new SignUp().setVisible(true);
                    }
                });
            }
        });
        
        buttonsPanel.add(loginButton);
        buttonsPanel.add(Box.createHorizontalStrut(20));
        buttonsPanel.add(signupButton);
        buttonsPanel.add(Box.createHorizontalGlue());
        
        // Add all content elements
        content.add(welcomeLabel);
        content.add(descLabel);
        content.add(featurePanel);
        content.add(buttonsPanel);
        content.add(Box.createVerticalGlue());
        
        // Add a footer with version and copyright
        JLabel versionLabel = new JLabel("Version 1.0 | © 2025 Kurdish-O-Banking. All rights reserved.");
        versionLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        versionLabel.setForeground(LIGHT_TEXT_COLOR);
        versionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(versionLabel);
        
        // Add panels to frame
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        
        // Try to set icon
        try {
            setIconImage(new ImageIcon("Logo/o1iwr2s2kskm9zqn7qr.png").getImage());
        } catch (Exception e) {
            System.err.println("Error loading icon: " + e.getMessage());
        }
    }
    
    // Helper method to create feature cards
    private JPanel createFeatureCard(String title, String description, Color accentColor) {
        JPanel card = new RoundedPanel(15, Color.WHITE);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Colored accent bar at top
        JPanel accentBar = new JPanel();
        accentBar.setBackground(accentColor);
        accentBar.setPreferredSize(new Dimension(50, 5));
        accentBar.setMaximumSize(new Dimension(Short.MAX_VALUE, 5));
        accentBar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        titleLabel.setForeground(TEXT_COLOR);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 10, 0));
        
        JLabel descLabel = new JLabel("<html>" + description + "</html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(LIGHT_TEXT_COLOR);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        card.add(accentBar);
        card.add(titleLabel);
        card.add(descLabel);
        
        return card;
    }
    
    // Custom rounded panel class - same as in Dashboard and Transfer
    class RoundedPanel extends JPanel {
        private int cornerRadius;
        private Color backgroundColor;
        
        public RoundedPanel(int radius, Color bgColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            setOpaque(false);
        }
        
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g.create();
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setColor(backgroundColor);
            g2d.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2d.dispose();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new WelcomePage().setVisible(true);
            }
        });
    }
}