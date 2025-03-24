package bank.pr;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.border.LineBorder;

public class HomePage extends JFrame {
    private String userEmail;

    public HomePage(String email) {
        this.userEmail = email;
        setTitle("Banking App - Home");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(30, 30, 30));

        // Top Panel - Balance Display
        JPanel balancePanel = new JPanel();
        balancePanel.setBackground(new Color(40, 40, 40));
        balancePanel.setPreferredSize(new Dimension(400, 150));
        balancePanel.setLayout(new BorderLayout());

        JLabel balanceLabel = new JLabel("Account Balance", SwingConstants.CENTER);
        balanceLabel.setForeground(Color.WHITE);
        balanceLabel.setFont(new Font("Arial", Font.BOLD, 20));

        JLabel balanceAmount = new JLabel(getAccountBalance(userEmail) + " IQD", SwingConstants.CENTER);
        balanceAmount.setForeground(Color.ORANGE);
        balanceAmount.setFont(new Font("Arial", Font.BOLD, 32));

        balancePanel.add(balanceLabel, BorderLayout.NORTH);
        balancePanel.add(balanceAmount, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = new JPanel(new GridLayout(2, 2, 15, 15));
        buttonPanel.setBackground(new Color(30, 30, 30));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));

        buttonPanel.add(createStyledButton("Deposit", "⬆", e -> deposit()));// actionakan jare drust nakrawn
        buttonPanel.add(createStyledButton("Withdraw", "⬇", e -> withdraw()));
        buttonPanel.add(createStyledButton("Transfer", "⇄", e -> transfer()));
        buttonPanel.add(createStyledButton("Transaction History", "📜", e -> viewTransactionHistory(userEmail)));

        // Main Layout
        add(balancePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    private JButton createStyledButton(String text, String icon, ActionListener action) {
        JButton button = new JButton(icon + " " + text);
        button.setFont(new Font("Arial", Font.BOLD, 16));
        button.setBackground(Color.ORANGE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(false);
        button.setPreferredSize(new Dimension(120, 50));
        button.setBorder(new LineBorder(Color.BLACK, 2, true));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setOpaque(true);
        button.setBorderPainted(false);
        button.setToolTipText(text);

        button.addActionListener(action);

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 180, 0));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.ORANGE);
            }
        });

        return button;
    }

    private void deposit() {
        JOptionPane.showMessageDialog(this, "Deposit function clicked", "Deposit", JOptionPane.INFORMATION_MESSAGE);
    }

    private void withdraw() {
        JOptionPane.showMessageDialog(this, "Withdraw function clicked", "Withdraw", JOptionPane.INFORMATION_MESSAGE);
    }

    private void transfer() {
        JOptionPane.showMessageDialog(this, "Transfer function clicked", "Transfer", JOptionPane.INFORMATION_MESSAGE);
    }

    private void viewTransactionHistory(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT * FROM transactions WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();

            StringBuilder transactions = new StringBuilder("Transaction History:\n");
            while (resultSet.next()) {
                String type = resultSet.getString("type");
                double amount = resultSet.getDouble("amount");
                transactions.append(type).append(": $").append(amount).append("\n");
            }

            JOptionPane.showMessageDialog(this, transactions.toString(), "Transaction History", JOptionPane.INFORMATION_MESSAGE);
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Error fetching transaction history: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String getAccountBalance(String email) {
        try (Connection conn = DatabaseConnection.getConnection()) {
            String query = "SELECT balance FROM accounts WHERE email = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, email);
            ResultSet resultSet = stmt.executeQuery();
            if (resultSet.next()) {
                return String.valueOf(resultSet.getDouble("balance"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "0";
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HomePage("user@example.com").setVisible(true));
    }
}
