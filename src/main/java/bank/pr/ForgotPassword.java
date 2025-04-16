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

public class ForgotPassword extends JFrame {

    public ForgotPassword() {
        setTitle("Forgot Password");
        setSize(500, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);
        setLayout(null);
        getContentPane().setBackground(new Color(230, 233, 239)); // Light gray background

        // Main white card panel
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(75, 50, 350, 480);
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createLineBorder(new Color(220, 220, 220)));
        add(panel);

        // Blue header panel
        JPanel header = new JPanel();
        header.setBackground(new Color(66, 103, 244)); // Blue header
        header.setBounds(0, 0, 350, 60);
        header.setLayout(null);
        panel.add(header);

        JLabel headerLabel = new JLabel("Forgot Password");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 16));
        headerLabel.setForeground(Color.WHITE);
        headerLabel.setHorizontalAlignment(SwingConstants.CENTER);
        headerLabel.setBounds(0, 15, 350, 30);
        header.add(headerLabel);

        // Instructions
        JLabel instructionLabel = new JLabel("Please enter your email or username.");
        instructionLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        instructionLabel.setForeground(new Color(120, 120, 120));
        instructionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        instructionLabel.setBounds(25, 75, 300, 20);
        panel.add(instructionLabel);

        // Fields
        String[] labels = {
            "Email or Username",
            "Authentication Code",
            "New Password",
            "Confirm New Password"
        };

        JTextField[] fields = new JTextField[labels.length];

        for (int i = 0; i < labels.length; i++) {
            JLabel fieldLabel = new JLabel(labels[i]);
            fieldLabel.setFont(new Font("Arial", Font.PLAIN, 13));
            fieldLabel.setBounds(25, 110 + i * 70, 300, 20);
            panel.add(fieldLabel);

            fields[i] = labels[i].toLowerCase().contains("password") ? new JPasswordField() : new JTextField();
            fields[i].setBounds(25, 135 + i * 70, 300, 35);
            fields[i].setFont(new Font("Arial", Font.PLAIN, 14));
            fields[i].setBackground(new Color(250, 252, 255));
            fields[i].setBorder(BorderFactory.createLineBorder(new Color(210, 210, 210)));
            panel.add(fields[i]);
        }

        // Reset button
        JButton resetButton = new JButton("Reset Password");
        resetButton.setFont(new Font("Arial", Font.BOLD, 14));
        resetButton.setBackground(new Color(66, 103, 244));
        resetButton.setForeground(Color.WHITE);
        resetButton.setBounds(25, 415, 300, 40);
        resetButton.setFocusPainted(false);
        resetButton.setBorderPainted(false);
        panel.add(resetButton);

        // Footer note
        JLabel noteLabel = new JLabel("For security, you may be asked to verify additional info.");
        noteLabel.setFont(new Font("Arial", Font.PLAIN, 11));
        noteLabel.setForeground(new Color(130, 130, 130));
        noteLabel.setHorizontalAlignment(SwingConstants.CENTER);
        noteLabel.setBounds(25, 500, 450, 30);
        add(noteLabel);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ForgotPassword().setVisible(true));
    }
}
