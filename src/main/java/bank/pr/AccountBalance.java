package bank.pr;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class AccountBalance extends JFrame {

    public AccountBalance() {
        setTitle("Online Banking");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Create sidebar panel
        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

        // Create main content panel
        JPanel mainContentPanel = createMainContentPanel();
        add(mainContentPanel, BorderLayout.CENTER);
    }

    private JPanel createSidebarPanel() {
        JPanel sidebarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(26, 26, 26));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        sidebarPanel.setPreferredSize(new Dimension(240, 800));
        sidebarPanel.setLayout(null);

        // Sidebar title
        JLabel titleLabel = new JLabel("Online Banking");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 200, 30);
        sidebarPanel.add(titleLabel);

        // Sidebar menu buttons
        String[] menuItems = {"Balance", "Dashboard", "Accounts", "Deposit", "Transfer", "Withdraw"};
        int yPos = 120;
        for (String item : menuItems) {
            RoundedButton menuButton = new RoundedButton(item, 5);
            menuButton.setBackground(new Color(26, 26, 26));
            menuButton.setForeground(Color.WHITE);
            menuButton.setFont(new Font("Arial", Font.PLAIN, 16));
            menuButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            menuButton.setBounds(40, yPos, 160, 30);
            menuButton.setHorizontalAlignment(SwingConstants.LEFT);
            
            // Add hover effect
            menuButton.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    menuButton.setBackground(new Color(50, 50, 50));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    menuButton.setBackground(new Color(26, 26, 26));
                }
            });
            
            // Add action listener
            menuButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, item + " button clicked!");
                }
            });
            
            sidebarPanel.add(menuButton);
            yPos += 40;
        }

        return sidebarPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(240, 240, 240));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        // Main content area (white rounded rectangle)
        RoundedPanel contentPanel = new RoundedPanel(10);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(20, 40, 520, 720);
        contentPanel.setLayout(null);
        mainPanel.add(contentPanel);

        // Title: Account Balance
        JLabel titleLabel = new JLabel("Account Balance");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 24));
        titleLabel.setForeground(new Color(51, 51, 51));
        titleLabel.setBounds(80, 40, 200, 30);
        contentPanel.add(titleLabel);

        // Subtitle: Last Updated
        JLabel subtitleLabel = new JLabel("Last updated: Today, 10:15 AM");
        subtitleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subtitleLabel.setForeground(new Color(102, 102, 102));
        subtitleLabel.setBounds(80, 70, 200, 20);
        contentPanel.add(subtitleLabel);

        // Account Selector Button
        RoundedPanel accountSelector = new RoundedPanel(5);
        accountSelector.setBackground(Color.WHITE);
        accountSelector.setBounds(60, 110, 440, 60);
        accountSelector.setLayout(null);
        contentPanel.add(accountSelector);

        JLabel selectLabel = new JLabel("Select Account");
        selectLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        selectLabel.setForeground(new Color(102, 102, 102));
        selectLabel.setBounds(20, 10, 200, 20);
        accountSelector.add(selectLabel);

        RoundedButton accountButton = new RoundedButton("Checking - ****1234", 5);
        accountButton.setBackground(Color.WHITE);
        accountButton.setForeground(new Color(51, 51, 51));
        accountButton.setFont(new Font("Arial", Font.PLAIN, 16));
        accountButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        accountButton.setBounds(15, 25, 410, 30);
        accountButton.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Add hover effect
        accountButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                accountButton.setBackground(new Color(240, 240, 240));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                accountButton.setBackground(Color.WHITE);
            }
        });
        
        // Add action listener
        accountButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Account selection clicked!");
            }
        });
        
        accountSelector.add(accountButton);

        // Current Balance Card
        RoundedPanel currentBalanceCard = new RoundedPanel(5);
        currentBalanceCard.setBackground(new Color(230, 247, 255));
        currentBalanceCard.setBounds(60, 200, 440, 180);
        currentBalanceCard.setLayout(null);
        contentPanel.add(currentBalanceCard);

        JLabel currentBalanceLabel = new JLabel("CURRENT BALANCE");
        currentBalanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        currentBalanceLabel.setForeground(new Color(0, 123, 255));
        currentBalanceLabel.setBounds(20, 20, 200, 20);
        currentBalanceCard.add(currentBalanceLabel);

        RoundedButton balanceButton = new RoundedButton("$4,582.90", 5);
        balanceButton.setBackground(new Color(230, 247, 255));
        balanceButton.setForeground(new Color(0, 123, 255));
        balanceButton.setFont(new Font("Arial", Font.PLAIN, 36));
        balanceButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        balanceButton.setBounds(15, 40, 410, 50);
        balanceButton.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Add hover effect
        balanceButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                balanceButton.setBackground(new Color(210, 237, 255));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                balanceButton.setBackground(new Color(230, 247, 255));
            }
        });
        
        // Add action listener
        balanceButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Current balance clicked!");
            }
        });
        
        currentBalanceCard.add(balanceButton);

        JLabel pendingLabel = new JLabel("Includes pending transactions");
        pendingLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        pendingLabel.setForeground(new Color(0, 123, 255));
        pendingLabel.setBounds(20, 100, 200, 20);
        currentBalanceCard.add(pendingLabel);

        // Available Balance Card
        RoundedPanel availableBalanceCard = new RoundedPanel(5);
        availableBalanceCard.setBackground(new Color(230, 255, 230));
        availableBalanceCard.setBounds(60, 410, 440, 180);
        availableBalanceCard.setLayout(null);
        contentPanel.add(availableBalanceCard);

        JLabel availableBalanceLabel = new JLabel("AVAILABLE BALANCE");
        availableBalanceLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        availableBalanceLabel.setForeground(new Color(40, 167, 69));
        availableBalanceLabel.setBounds(20, 20, 200, 20);
        availableBalanceCard.add(availableBalanceLabel);

        RoundedButton availableButton = new RoundedButton("$4,500.00", 5);
        availableButton.setBackground(new Color(230, 255, 230));
        availableButton.setForeground(new Color(40, 167, 69));
        availableButton.setFont(new Font("Arial", Font.PLAIN, 36));
        availableButton.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        availableButton.setBounds(15, 40, 410, 50);
        availableButton.setHorizontalAlignment(SwingConstants.LEFT);
        
        // Add hover effect
        availableButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                availableButton.setBackground(new Color(210, 255, 210));
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                availableButton.setBackground(new Color(230, 255, 230));
            }
        });
        
        // Add action listener
        availableButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(null, "Available balance clicked!");
            }
        });
        
        availableBalanceCard.add(availableButton);

        JLabel fundsLabel = new JLabel("Immediately accessible funds");
        fundsLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        fundsLabel.setForeground(new Color(40, 167, 69));
        fundsLabel.setBounds(20, 100, 200, 20);
        availableBalanceCard.add(fundsLabel);

        // Quick Actions
        JLabel quickActionsLabel = new JLabel("Quick Actions");
        quickActionsLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        quickActionsLabel.setForeground(new Color(51, 51, 51));
        quickActionsLabel.setBounds(80, 620, 200, 20);
        contentPanel.add(quickActionsLabel);

        // Action Buttons
        String[] buttonLabels = {"Details", "Statement", "History", "Export"};
        int xPos = 60;
        for (String label : buttonLabels) {
            RoundedButton button = new RoundedButton(label, 5);
            button.setBackground(new Color(230, 247, 255));
            button.setForeground(new Color(0, 123, 255));
            button.setFont(new Font("Arial", Font.PLAIN, 14));
            button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
            button.setBounds(xPos, 660, 90, 40);
            
            // Add hover effect
            button.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(200, 230, 255));
                }
                public void mouseExited(java.awt.event.MouseEvent evt) {
                    button.setBackground(new Color(230, 247, 255));
                }
            });
            
            // Add action listener
            button.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    JOptionPane.showMessageDialog(null, label + " button clicked!");
                }
            });
            
            contentPanel.add(button);
            xPos += 110;
        }

        return mainPanel;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            AccountBalance gui = new AccountBalance();
            gui.setVisible(true);
        });
    }

    // Custom Rounded Panel class
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
            Dimension arcs = new Dimension(cornerRadius, cornerRadius);
            int width = getWidth();
            int height = getHeight();
            Graphics2D graphics = (Graphics2D) g;
            graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Draw the rounded panel with borders.
            graphics.setColor(getBackground());
            graphics.fillRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
            graphics.setColor(getBackground());
            graphics.drawRoundRect(0, 0, width-1, height-1, arcs.width, arcs.height);
        }
    }

    // Custom Rounded Button class
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
            
            // Paint the background
            g2.setColor(getBackground());
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            
            super.paintComponent(g2);
            g2.dispose();
        }
    }
}