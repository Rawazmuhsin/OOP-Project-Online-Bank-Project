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
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

public class Transfer extends JFrame {

    public Transfer() {
        setTitle("Transfer Funds - Online Banking");
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

        String[] menuItems = {"Transfer", "Dashboard", "Accounts", "Deposit", "Withdraw"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(item.equals("Transfer") ? new Color(40, 45, 65) : new Color(20, 25, 45));
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

        JLabel title = new JLabel("Transfer Funds");
        title.setFont(new Font("SansSerif", Font.BOLD, 22));
        JLabel subtitle = new JLabel("Transfer money safely between your accounts or to others.");
        subtitle.setFont(new Font("SansSerif", Font.PLAIN, 14));
        subtitle.setForeground(Color.GRAY);

        content.add(title);
        content.add(Box.createVerticalStrut(5));
        content.add(subtitle);
        content.add(Box.createVerticalStrut(30));

        // Transfer Type
        JPanel transferTypePanel = new JPanel(new GridLayout(1, 2, 20, 10));
        transferTypePanel.setOpaque(false);

        JRadioButton internal = new JRadioButton("Between My Accounts");
        JRadioButton external = new JRadioButton("To Another Person");

        ButtonGroup group = new ButtonGroup();
        group.add(internal);
        group.add(external);
        internal.setSelected(true);

        transferTypePanel.add(wrapRadioPanel("Between My Accounts", "Instant transfer", internal));
        transferTypePanel.add(wrapRadioPanel("To Another Person", "1–2 business days", external));

        content.add(transferTypePanel);
        content.add(Box.createVerticalStrut(30));

        // From Account
        JLabel fromLabel = new JLabel("From Account");
        fromLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JTextField fromField = new JTextField("Checking - ****1234");
        fromField.setPreferredSize(new Dimension(300, 30));
        fromField.setEditable(false);
        fromField.setBackground(Color.WHITE);

        content.add(fromLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(fromField);
        content.add(Box.createVerticalStrut(20));

        // To Account
        JLabel toLabel = new JLabel("To Account");
        toLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JTextField toField = new JTextField();
        toField.setPreferredSize(new Dimension(300, 30));

        content.add(toLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(toField);
        content.add(Box.createVerticalStrut(20));

        // Amount Section
        JLabel amountLabel = new JLabel("Amount");
        amountLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        JTextField amountField = new JTextField();
        amountField.setPreferredSize(new Dimension(300, 30));

        content.add(amountLabel);
        content.add(Box.createVerticalStrut(5));
        content.add(amountField);

        JPanel quickAmountPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        quickAmountPanel.setOpaque(false);
        String[] amounts = {"$100", "$250", "$500", "$1000"};
        for (String amount : amounts) {
            JButton quickBtn = new JButton(amount);
            quickBtn.setFocusPainted(false);
            quickBtn.setBackground(new Color(255, 230, 230));
            quickBtn.setForeground(new Color(180, 0, 0));
            quickAmountPanel.add(quickBtn);
        }

        content.add(Box.createVerticalStrut(10));
        content.add(quickAmountPanel);
        content.add(Box.createVerticalStrut(30));

        // Submit Button
        JButton transferBtn = new JButton("Transfer Funds");
        transferBtn.setBackground(new Color(60, 130, 255));
        transferBtn.setForeground(Color.WHITE);
        transferBtn.setFocusPainted(false);
        transferBtn.setFont(new Font("SansSerif", Font.BOLD, 14));
        transferBtn.setPreferredSize(new Dimension(300, 40));
        content.add(transferBtn);

        // Frame Layout
        add(sidebar, BorderLayout.WEST);
        add(new JScrollPane(content), BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel wrapRadioPanel(String title, String subtitle, JRadioButton button) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 255)),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        panel.setBackground(Color.WHITE);
        button.setOpaque(false);

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));

        JLabel sub = new JLabel(subtitle);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 12));
        sub.setForeground(Color.GRAY);

        panel.add(button);
        panel.add(label);
        panel.add(sub);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Transfer::new);
    }
}
