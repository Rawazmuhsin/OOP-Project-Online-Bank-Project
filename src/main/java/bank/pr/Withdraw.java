package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Withdraw extends JFrame {

    public Withdraw() {
        setTitle("Withdraw Funds - Online Banking");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(20, 25, 45));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel titleLabel = new JLabel("  Online Banking");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] menuItems = {"Withdraw", "Dashboard", "Accounts", "Deposit", "Transfer"};

        sidebar.add(titleLabel);
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(item.equals("Withdraw") ? new Color(40, 45, 65) : new Color(20, 25, 45));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
            sidebar.add(button);
        }

        // Main content
        JPanel content = new JPanel();
        content.setBackground(new Color(245, 247, 251));
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        JLabel title = new JLabel("Withdraw Funds");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Withdraw money from your account");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        content.add(title);
        content.add(Box.createVerticalStrut(5));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));

        // Withdraw Method
        JPanel methodPanel = new JPanel(new GridLayout(1, 2, 20, 10));
        methodPanel.setOpaque(false);

        JRadioButton bankTransfer = new JRadioButton("Bank Account");
        JRadioButton card = new JRadioButton("Debit Card");

        ButtonGroup methodGroup = new ButtonGroup();
        methodGroup.add(bankTransfer);
        methodGroup.add(card);
        bankTransfer.setSelected(true);

        methodPanel.add(wrapRadioPanel("Bank Account", "2–3 business days", bankTransfer));
        methodPanel.add(wrapRadioPanel("Debit Card", "Instant withdrawal", card));

        content.add(methodPanel);
        content.add(Box.createVerticalStrut(30));

        // Amount Input
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(200, 30));

        content.add(amountLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(amountField);
        content.add(Box.createVerticalStrut(10));

        // Quick Amount Buttons
        JLabel quickLabel = new JLabel("Quick amounts:");
        quickLabel.setFont(new Font("SansSerif", Font.PLAIN, 12));
        JPanel quickButtons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quickButtons.setOpaque(false);

        String[] quicks = {"$100", "$250", "$500", "$1000"};
        for (String amt : quicks) {
            JButton btn = new JButton(amt);
            btn.setFocusPainted(false);
            btn.setBackground(new Color(220, 255, 230));
            btn.setForeground(new Color(0, 128, 0));
            quickButtons.add(btn);
        }

        content.add(quickLabel);
        content.add(quickButtons);
        content.add(Box.createVerticalStrut(20));

        // Description
        JLabel descLabel = new JLabel("Description (Optional)");
        descLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        JTextArea descArea = new JTextArea(3, 30);
        descArea.setLineWrap(true);
        descArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

        content.add(descLabel);
        content.add(descArea);
        content.add(Box.createVerticalStrut(30));

        // Submit Button
        JButton submitBtn = new JButton("Withdraw Funds");
        submitBtn.setBackground(new Color(60, 130, 255));
        submitBtn.setForeground(Color.WHITE);
        submitBtn.setFocusPainted(false);
        submitBtn.setPreferredSize(new Dimension(300, 40));
        submitBtn.setFont(new Font("SansSerif", Font.BOLD, 14));

        content.add(submitBtn);

        // Add to Frame
        add(sidebar, BorderLayout.WEST);
        add(new JScrollPane(content), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel wrapRadioPanel(String title, String subtitle, JRadioButton button) {
        JPanel panel = new JPanel();
        panel.setBackground(Color.WHITE);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(180, 180, 255)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        JLabel sub = new JLabel(subtitle);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);

        button.setOpaque(false);

        panel.add(button);
        panel.add(label);
        panel.add(sub);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Withdraw::new);
    }
}