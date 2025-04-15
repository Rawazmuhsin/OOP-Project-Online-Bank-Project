package bank.pr;

import java.awt.Color;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

public class SignUp extends JFrame {

    public SignUp() {
        setTitle("Sign Up");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 233, 239)); // Light background

        // Card container
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(75, 50, 350, 480);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(panel);

        // Header
        JPanel header = new JPanel();
        header.setBackground(new Color(66, 103, 244));
        header.setBounds(0, 0, 350, 60);
        header.setLayout(null);
        panel.add(header);

        JLabel headerLabel = new JLabel("Sign Up");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBounds(0, 15, 350, 30);
        header.add(headerLabel);

        // Fields
        String[] labels = {
            "Username",
            "Email",
            "Password",
            "Confirm Password",
            "Phone Number"
        };

        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel fieldLabel = new JLabel(labels[i]);
            fieldLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            fieldLabel.setBounds(25, 75 + i * 70, 300, 20);
            panel.add(fieldLabel);

            boolean isPassword = labels[i].toLowerCase().contains("password");
            fields[i] = isPassword ? new JPasswordField() : new JTextField();
            fields[i].setBounds(25, 100 + i * 70, 300, 35);
            fields[i].setFont(new Font("Arial", Font.PLAIN, 14));
            fields[i].setBackground(new Color(250, 252, 255));
            fields[i].setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
            panel.add(fields[i]);
        }

        // Submit Button
        JButton signUpButton = new JButton("Sign Up");
        signUpButton.setFont(new Font("Arial", Font.BOLD, 14));
        signUpButton.setBackground(new Color(66, 103, 244));
        signUpButton.setForeground(Color.WHITE);
        signUpButton.setBounds(25, 430, 300, 40);
        signUpButton.setFocusPainted(false);
        signUpButton.setBorderPainted(false);
        panel.add(signUpButton);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SignUp().setVisible(true));
    }
}
