package bank.pr;

import javax.swing.*;
import java.awt.*;


public class Transaction extends JFrame {

    public Transaction() {
        setTitle("Online Banking - Transactions");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createTransactionsContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(26, 32, 44)); // #1a202c
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(250, 800));
        sidebarPanel.setLayout(null);

        // Sidebar title
        JLabel titleLabel = new JLabel("Online Banking");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 200, 30);
        sidebarPanel.add(titleLabel);

        // Active sidebar button (Transactions)
        RoundedButton transactionsButton = new RoundedButton("Transactions", 5);
        transactionsButton.setBackground(new Color(52, 58, 64)); // #343a40
        transactionsButton.setForeground(Color.WHITE);
        transactionsButton.setFont(new Font("Arial", Font.PLAIN, 16));
        transactionsButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        transactionsButton.setBounds(20, 100, 210, 40);
        transactionsButton.setHorizontalAlignment(SwingConstants.LEFT);
        sidebarPanel.add(transactionsButton);

        // Other sidebar buttons
        String[] menuItems = {"Dashboard", "Accounts", "Deposit", "Transfer", "Withdraw"};
        int yPos = 180;
        for (String item : menuItems) {
            JLabel menuLabel = new JLabel(item);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setBounds(60, yPos, 200, 30);
            
            // Make labels clickable like buttons
            menuLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            menuLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent evt) {
                    JOptionPane.showMessageDialog(null, item + " clicked!");
                }
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    menuLabel.setForeground(new Color(200, 200, 200));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    menuLabel.setForeground(Color.WHITE);
                }
            });
            
            sidebarPanel.add(menuLabel);
            yPos += 40;
        }

        return sidebarPanel;
    }

    private JPanel createTransactionsContentPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(240, 244, 248)); // #f0f4f8
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // Main content area (white rounded rectangle)
        RoundedPanel contentPanel = new RoundedPanel(10);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(20, 20, 530, 740);
        contentPanel.setLayout(null);
        mainPanel.add(contentPanel);

        // Transaction History Header
        JLabel titleLabel = new JLabel("Transaction History");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(new Color(52, 58, 64)); // #343a40
        titleLabel.setBounds(30, 30, 300, 30);
        contentPanel.add(titleLabel);

        // Create table headers
        RoundedPanel headerPanel = new RoundedPanel(5);
        headerPanel.setBackground(new Color(248, 250, 252)); // #f8fafc
        headerPanel.setBounds(20, 100, 490, 30);
        headerPanel.setLayout(new GridLayout(1, 5));
        contentPanel.add(headerPanel);

        String[] headers = {"DATE", "DESCRIPTION", "CATEGORY", "AMOUNT", "BALANCE"};
        for (String header : headers) {
            JLabel headerLabel = new JLabel(header);
            headerLabel.setFont(new Font("Arial", Font.BOLD, 14));
            headerLabel.setForeground(new Color(52, 58, 64)); // #343a40
            headerLabel.setHorizontalAlignment(SwingConstants.LEFT);
            headerPanel.add(headerLabel);
        }

        // Create table rows
        String[][] transactions = {
            {"Mar 15, 2023", "Grocery Store", "Food", "-$86.75", "$4,582.90"},
            {"Mar 1, 2023", "Salary Deposit", "Income", "+$3,500.00", "$4,669.65"},
            {"Feb 28, 2023", "Utility Bill", "Bills", "-$120.50", "$1,169.65"}
        };

        int yPos = 140;
        for (int i = 0; i < transactions.length; i++) {
            RoundedPanel rowPanel = new RoundedPanel(5);
            rowPanel.setBackground(i % 2 == 0 ? Color.WHITE : new Color(248, 250, 252)); // Alternate row colors
            rowPanel.setBounds(20, yPos, 490, 30);
            rowPanel.setLayout(new GridLayout(1, 5));
            contentPanel.add(rowPanel);

            for (int j = 0; j < transactions[i].length; j++) {
                JLabel cellLabel = new JLabel(transactions[i][j]);
                cellLabel.setFont(new Font("Arial", Font.PLAIN, 14));
                
                // Set color for amount column
                if (j == 3) {
                    cellLabel.setForeground(transactions[i][j].startsWith("+") ? 
                        new Color(0, 128, 0) : // Green for positive
                        new Color(255, 0, 0));  // Red for negative
                } else {
                    cellLabel.setForeground(new Color(52, 58, 64)); // #343a40
                }
                
                cellLabel.setHorizontalAlignment(SwingConstants.LEFT);
                rowPanel.add(cellLabel);
            }
            
            yPos += 40;
        }

        return mainPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Transaction gui = new Transaction();
            gui.setVisible(true);
        });
    }

    // Custom Rounded Panel class (same as your perfect example)
    static class RoundedPanel extends JPanel {
        private int cornerRadius;

        public RoundedPanel(int cornerRadius) {
            super();
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
        }
    }

    // Custom Rounded Button class (same as your perfect example)
    static class RoundedButton extends JButton {
        private int cornerRadius;

        public RoundedButton(String text, int cornerRadius) {
            super(text);
            this.cornerRadius = cornerRadius;
            setContentAreaFilled(false);
            setFocusPainted(false);
            setBorderPainted(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}