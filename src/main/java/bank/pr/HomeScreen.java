package bank.pr;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;

public class HomeScreen extends JFrame {

    public HomeScreen() {
        setTitle("Online Banking");
        setSize(800, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);
        getContentPane().setBackground(new Color(30, 30, 30));

        JLabel welcomeLabel = new JLabel("Welcome to Online Banking", JLabel.CENTER);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 24));
        welcomeLabel.setForeground(Color.WHITE);
        welcomeLabel.setBounds(200, 50, 400, 30);
        add(welcomeLabel);

        JLabel taglineLabel = new JLabel("Secure • Reliable • Convenient", JLabel.CENTER);
        taglineLabel.setFont(new Font("Arial", Font.BOLD, 16));
        taglineLabel.setForeground(Color.ORANGE);
        taglineLabel.setBounds(250, 100, 300, 20);
        add(taglineLabel);

        JLabel descriptionLabel = new JLabel("Easy Money Transfer", JLabel.CENTER);
        descriptionLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        descriptionLabel.setForeground(Color.WHITE);
        descriptionLabel.setBounds(300, 140, 200, 20);
        add(descriptionLabel);

        JButton loginButton = new JButton("Login");
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setBackground(Color.ORANGE);
        loginButton.setForeground(Color.BLACK);
        loginButton.setBounds(300, 200, 100, 40);
        loginButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new LoginApp();
                dispose();
            }
        });
        add(loginButton);

        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setBackground(Color.WHITE);
        signUpButton.setForeground(Color.BLACK);
        signUpButton.setBounds(420, 200, 100, 40);
        signUpButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                new SignupApp();
                dispose();
            }
        });
        add(signUpButton);

        JLabel securityLabel = new JLabel("Your security is our top priority", JLabel.CENTER);
        securityLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        securityLabel.setForeground(Color.WHITE);
        securityLabel.setBounds(250, 280, 300, 20);
        add(securityLabel);

        setVisible(true);
    }

    public static void main(String[] args) {
        new HomeScreen();
    }
}
