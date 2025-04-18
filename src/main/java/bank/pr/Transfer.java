package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

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
        setTitle("Transfer Funds -  Kurdish - O - Banking (KOB)");
        setSize(900, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(20, 25, 45));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel titleLabel = new JLabel("  Kurdish - O - Banking");
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

            // Add action listener for navigation
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    handleNavigation(item);
                }
            });

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

            // Add action listener for quick amount buttons
            quickBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    amountField.setText(amount.replace("$", ""));
                }
            });

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

        // Add action listener for transfer button
        transferBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processTransfer(fromField.getText(), toField.getText(), amountField.getText());
            }
        });

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

    // Method to handle navigation between pages
    private void handleNavigation(String destination) {
        this.dispose(); // Close current window

        // Open the selected page
        switch (destination) {
            case "Dashboard":
            SwingUtilities.invokeLater(() -> {
                Dashbord dashboard = new Dashbord();
                dashboard.setUserInfo("John Doe", 12345); // Optionally set user info if needed
                dashboard.setVisible(true); // Ensure the Dashboard is visible
            });
            break;
            case "Deposit":
                SwingUtilities.invokeLater(Deposite::new);
                break;
            case "Withdraw":
                SwingUtilities.invokeLater(Withdraw::new);
                break;
            case "Transfer":
                // Already on this page, do nothing or refresh
                SwingUtilities.invokeLater(Transfer::new);
                break;
                case "Accounts":
                // Go to User Profile page
                SwingUtilities.invokeLater(() -> {
                    UserProfile userProfile = new UserProfile();
                    // userProfile.setUserInfo(userName, userId); // Pass user info to UserProfile
                    userProfile.setVisible(true);
                    this.dispose(); // Close the current Dashboard window
                });
                break;
            default:
                System.out.println("Navigation to " + destination + " not implemented");
                break;
        }
    }

    // Method to process transfer
    private void processTransfer(String fromAccount, String toAccount, String amount) {
        try {
            double transferAmount = Double.parseDouble(amount);
            System.out.println("Transfer initiated:");
            System.out.println("From: " + fromAccount);
            System.out.println("To: " + toAccount);
            System.out.println("Amount: $" + transferAmount);

            // Show success message
            System.out.println("Transfer successful!");

            // Return to dashboard
            this.dispose();
            SwingUtilities.invokeLater(Dashbord::new);
        } catch (NumberFormatException e) {
            System.err.println("Invalid amount entered: " + amount);
            // In a real app, you would show an error dialog here
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Transfer::new);
    }
}