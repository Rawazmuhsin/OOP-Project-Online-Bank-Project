package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

public class BalancePage extends JFrame {

    public BalancePage() {
        setTitle("Account Balance -  Kurdish - O - Banking (KOB)");
        setSize(950, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Sidebar
        JPanel sidebar = new JPanel();
        sidebar.setBackground(new Color(20, 25, 45));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setPreferredSize(new Dimension(200, getHeight()));

        JLabel titleLabel = new JLabel(" Kurdish - O - Banking");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(20, 10, 20, 10));
        sidebar.add(titleLabel);

        String[] menuItems = {"Balance", "Dashboard", "Accounts", "Deposit", "Transfer", "Withdraw"};
        for (String item : menuItems) {
            JButton button = new JButton(item);
            button.setAlignmentX(Component.LEFT_ALIGNMENT);
            button.setMaximumSize(new Dimension(180, 40));
            button.setBackground(item.equals("Balance") ? new Color(40, 45, 65) : new Color(20, 25, 45));
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setFont(new Font("SansSerif", Font.PLAIN, 14));
            button.setBorder(new EmptyBorder(10, 20, 10, 10));
            sidebar.add(button);
        }

        // Main content panel
        JPanel content = new JPanel();
        content.setLayout(null);
        content.setBackground(new Color(245, 247, 251));
        content.setBorder(new EmptyBorder(30, 30, 30, 30));

        JLabel pageTitle = new JLabel("Account Balance");
        pageTitle.setFont(new Font("SansSerif", Font.BOLD, 22));
        pageTitle.setBounds(20, 20, 300, 30);
        content.add(pageTitle);

        JLabel updatedTime = new JLabel("Last updated: Today, 10:15 AM");
        updatedTime.setFont(new Font("SansSerif", Font.PLAIN, 13));
        updatedTime.setForeground(Color.GRAY);
        updatedTime.setBounds(20, 55, 300, 20);
        content.add(updatedTime);

        // Account dropdown placeholder
        JPanel accountSelectPanel = new JPanel(new BorderLayout());
        accountSelectPanel.setBounds(20, 90, 500, 45);
        accountSelectPanel.setBackground(new Color(250, 250, 250));
        accountSelectPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(10, 10, 10, 10)
        ));
        JLabel selectedAccount = new JLabel("Select Account");
        selectedAccount.setFont(new Font("SansSerif", Font.PLAIN, 14));
        accountSelectPanel.add(selectedAccount, BorderLayout.WEST);
        content.add(accountSelectPanel);

        // Current Balance box - dynamic placeholder
        JPanel currentBalancePanel = createBalanceBox(
                "CURRENT BALANCE", "$0.00", "Includes pending transactions",
                new Color(230, 245, 255), new Color(30, 90, 140)
        );
        currentBalancePanel.setBounds(20, 150, 500, 140);
        content.add(currentBalancePanel);

        // Available Balance box - dynamic placeholder
        JPanel availableBalancePanel = createBalanceBox(
                "AVAILABLE BALANCE", "$0.00", "Immediately accessible funds",
                new Color(235, 255, 240), new Color(0, 110, 40)
        );
        availableBalancePanel.setBounds(20, 300, 500, 140);
        content.add(availableBalancePanel);

        // Quick Actions
        JLabel quickActionLabel = new JLabel("Quick Actions");
        quickActionLabel.setFont(new Font("SansSerif", Font.PLAIN, 14));
        quickActionLabel.setBounds(20, 460, 150, 20);
        content.add(quickActionLabel);

        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        actionsPanel.setOpaque(false);
        actionsPanel.setBounds(20, 490, 600, 40);
        String[] actionLabels = {"Details", "Statement", "History", "Export"};
        for (String label : actionLabels) {
            JButton actionBtn = new JButton(label);
            actionBtn.setBackground(new Color(240, 245, 255));
            actionBtn.setForeground(new Color(40, 80, 200));
            actionBtn.setFocusPainted(false);
            actionBtn.setFont(new Font("SansSerif", Font.PLAIN, 13));
            actionBtn.setPreferredSize(new Dimension(100, 35));
            actionsPanel.add(actionBtn);
        }
        content.add(actionsPanel);

        add(sidebar, BorderLayout.WEST);
        add(content, BorderLayout.CENTER);
        setVisible(true);
    }

    private JPanel createBalanceBox(String title, String amount, String subtext, Color bg, Color textColor) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(bg);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(230, 230, 230)),
                new EmptyBorder(20, 20, 20, 20)
        ));

        JLabel label = new JLabel(title);
        label.setFont(new Font("SansSerif", Font.BOLD, 14));
        label.setForeground(textColor);

        JLabel balance = new JLabel(amount); // Placeholder "$0.00"
        balance.setFont(new Font("SansSerif", Font.BOLD, 32));
        balance.setForeground(textColor);

        JLabel sub = new JLabel(subtext);
        sub.setFont(new Font("SansSerif", Font.PLAIN, 13));
        sub.setForeground(Color.GRAY);

        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        panel.add(balance);
        panel.add(Box.createVerticalStrut(5));
        panel.add(sub);
        return panel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(BalancePage::new);
    }
}
