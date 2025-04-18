package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;


public class WelcomePage extends JFrame {

    public WelcomePage() {
        setTitle("Welcome - Kurdish - O - Banking (KOB)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(20, 25, 45));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel titleLabel = new JLabel("  Kurdish - O - Banking (KOB)");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        sidebar.add(titleLabel);

        // Main content panel
        JPanel content = new JPanel();
        content.setBackground(new Color(245, 247, 251));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(60, 40, 40, 40));

        JLabel welcomeLabel = new JLabel("Welcome to Kurdish - O - Banking");
        welcomeLabel.setFont(new Font("SansSerif", Font.BOLD, 28));
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel descLabel = new JLabel("<html>Securely manage your finances from anywhere.<br>" +
                "Track your transactions, deposit funds, and transfer money with ease.<br>" +
                "Join now and take control of your banking experience.</html>");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        descLabel.setForeground(Color.DARK_GRAY);
        descLabel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Buttons Panel
        JPanel buttonsPanel = new JPanel();
        buttonsPanel.setOpaque(false);
        buttonsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 0));

        JButton loginButton = new JButton("Log In");
        loginButton.setBackground(new Color(60, 130, 255));
        loginButton.setForeground(Color.WHITE);
        loginButton.setFocusPainted(false);
        loginButton.setPreferredSize(new Dimension(120, 40));
        loginButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        JButton signupButton = new JButton("Sign Up");
        signupButton.setBackground(new Color(230, 230, 230));
        signupButton.setForeground(Color.BLACK);
        signupButton.setFocusPainted(false);
        signupButton.setPreferredSize(new Dimension(120, 40));
        signupButton.setFont(new Font("SansSerif", Font.BOLD, 14));

        // Action listeners
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close WelcomePage
                SwingUtilities.invokeLater(() -> new LoginUI().setVisible(true));
            }
        });

        signupButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dispose(); // Close WelcomePage
                SwingUtilities.invokeLater(() -> new SignUp().setVisible(true));
            }
        });

        buttonsPanel.add(loginButton);
        buttonsPanel.add(signupButton);

        content.add(welcomeLabel);
        content.add(descLabel);
        content.add(buttonsPanel);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(WelcomePage::new);
    }
}