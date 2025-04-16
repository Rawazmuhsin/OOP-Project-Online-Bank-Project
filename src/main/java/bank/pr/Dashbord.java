package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Dashbord extends JFrame {

    public Dashbord() {
        setTitle("Online Banking");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar Panel
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(20, 25, 45));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel titleLabel = new JLabel("  Online Banking");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));

        String[] menuItems = {"Dashboard", "Accounts", "Transactions", "Transfers", "Cards"};

        sidebar.add(titleLabel);
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(item.equals("Dashboard") ? new Color(40, 45, 65) : new Color(20, 25, 45));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 10));
            sidebar.add(button);
        }

        // Main Content
        JPanel content = new JPanel();
        content.setLayout(new BorderLayout());
        content.setBackground(new Color(245, 247, 251));

        JLabel greeting = new JLabel("Hello, [User]! Last login: [Time]");
        greeting.setBorder(BorderFactory.createEmptyBorder(20, 20, 10, 20));
        greeting.setFont(new Font("SansSerif", Font.PLAIN, 14));
        content.add(greeting, BorderLayout.NORTH);

        JPanel cardsPanel = new JPanel();
        cardsPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 20));
        cardsPanel.setBackground(new Color(245, 247, 251));

        // Placeholder cards
        cardsPanel.add(createAccountCard("Checking - ****1234"));
        cardsPanel.add(createAccountCard("Savings - ****5678"));

        content.add(cardsPanel, BorderLayout.CENTER);

        // Add to frame
        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createAccountCard(String title) {
        JPanel card = new JPanel();
        card.setPreferredSize(new Dimension(300, 120));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(220, 220, 220)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        card.setLayout(new BorderLayout());

        JLabel accountLabel = new JLabel(title);
        accountLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JLabel placeholderLabel = new JLabel("[Available Balance]");
        placeholderLabel.setFont(new Font("SansSerif", Font.ITALIC, 16));
        placeholderLabel.setForeground(new Color(120, 120, 120));

        card.add(accountLabel, BorderLayout.NORTH);
        card.add(placeholderLabel, BorderLayout.SOUTH);

        return card;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Dashbord::new);
    }
}
