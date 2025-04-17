package bank.pr;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

public class ManagerDashboard extends JFrame {

    public ManagerDashboard() {
        setTitle("Bank Manager Dashboard");
        setSize(1000, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel sidebarPanel = createSidebarPanel();
        add(sidebarPanel, BorderLayout.WEST);

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

        JLabel titleLabel = new JLabel("Bank Manager");
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 18));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(60, 40, 150, 30);
        sidebarPanel.add(titleLabel);

        RoundedPanel dashboardBtn = new RoundedPanel(8);
        dashboardBtn.setBackground(new Color(51, 51, 51));
        dashboardBtn.setBounds(20, 120, 200, 40);
        dashboardBtn.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));

        JLabel dashboardLabel = new JLabel("Dashboard");
        dashboardLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        dashboardLabel.setForeground(Color.WHITE);
        dashboardBtn.add(dashboardLabel);
        sidebarPanel.add(dashboardBtn);

        String[] menuItems = {
            "Customer Accounts", 
            "Transaction Oversight", 
            "Approval Queue", 
            "Reports", 
            "Audit Logs"
        };

        int yPos = 200;
        for (String item : menuItems) {
            JLabel menuLabel = new JLabel(item);
            menuLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            menuLabel.setForeground(Color.WHITE);
            menuLabel.setBounds(40, yPos, 200, 20);
            menuLabel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            menuLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent evt) {
                    JOptionPane.showMessageDialog(null, "Navigating to " + item);
                }

                public void mouseEntered(MouseEvent evt) {
                    menuLabel.setForeground(new Color(200, 200, 200));
                }

                public void mouseExited(MouseEvent evt) {
                    menuLabel.setForeground(Color.WHITE);
                }
            });
            sidebarPanel.add(menuLabel);
            yPos += 40;
        }

        return sidebarPanel;
    }

    private JPanel createMainContentPanel() {
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(249, 249, 249));
                g.fillRect(0, 0, getWidth(), getHeight());
            }
        };
        mainPanel.setLayout(null);

        RoundedPanel contentPanel = new RoundedPanel(16);
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBounds(20, 20, 720, 740);
        contentPanel.setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
        contentPanel.setLayout(null);
        mainPanel.add(contentPanel);

        JLabel headerLabel = new JLabel("Manager Dashboard");
        headerLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerLabel.setForeground(new Color(51, 51, 51));
        headerLabel.setBounds(60, 50, 250, 30);
        contentPanel.add(headerLabel);

        JLabel subHeaderLabel = new JLabel("Hello, Sarah Johnson | Last login: Today 09:42 AM");
        subHeaderLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        subHeaderLabel.setForeground(new Color(102, 102, 102));
        subHeaderLabel.setBounds(60, 80, 400, 20);
        contentPanel.add(subHeaderLabel);

        JLabel overviewLabel = new JLabel("TODAY'S OVERVIEW");
        overviewLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        overviewLabel.setForeground(new Color(102, 102, 102));
        overviewLabel.setBounds(60, 130, 150, 15);
        contentPanel.add(overviewLabel);

        MetricCard[] metricCards = {
            new MetricCard("Pending Approvals", "12", new Color(0, 123, 255), new Color(230, 247, 255)),
            new MetricCard("Flagged Transactions", "3", new Color(40, 167, 69), new Color(230, 255, 234)),
            new MetricCard("High-Risk Accounts", "2", new Color(220, 53, 69), new Color(255, 230, 230)),
            new MetricCard("New Customers", "5", new Color(111, 66, 193), new Color(230, 230, 255))
        };

        int xPos = 10;
        for (MetricCard card : metricCards) {
            card.setBounds(xPos, 150, 130, 80);
            contentPanel.add(card);
            xPos += 150;
        }

        JLabel toolsLabel = new JLabel("Management Tools");
        toolsLabel.setFont(new Font("Arial", Font.BOLD, 18));
        toolsLabel.setForeground(new Color(51, 51, 51));
        toolsLabel.setBounds(60, 270, 200, 20);
        contentPanel.add(toolsLabel);

        ToolCard[] toolCards = {
            new ToolCard("View Customer Accounts", "Search and manage all customer accounts", 
                        "Access", new Color(0, 123, 255), new Color(230, 247, 255)),
            new ToolCard("Manage Transactions", "Review and modify transaction records", 
                        "Access", new Color(40, 167, 69), new Color(230, 255, 234)),
            new ToolCard("Approve Transactions", "Authorize pending transactions", 
                        "Review", new Color(220, 53, 69), new Color(255, 230, 230)),
            new ToolCard("Generate Reports", "Create financial and audit reports", 
                        "Generate", new Color(111, 66, 193), new Color(230, 230, 255))
        };

        toolCards[0].setBounds(60, 310, 240, 140);
        toolCards[1].setBounds(340, 310, 240, 140);
        toolCards[2].setBounds(60, 470, 240, 140);
        toolCards[3].setBounds(340, 470, 240, 140);

        for (ToolCard card : toolCards) {
            contentPanel.add(card);
        }

        RoundedPanel logoutBtn = new RoundedPanel(8);
        logoutBtn.setBackground(new Color(255, 230, 230));
        logoutBtn.setBounds(60, 660, 120, 40);
        logoutBtn.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 10));

        JLabel logoutLabel = new JLabel("Logout");
        logoutLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutLabel.setForeground(new Color(220, 53, 69));
        logoutBtn.add(logoutLabel);
        contentPanel.add(logoutBtn);

        return mainPanel;
    }

    static class MetricCard extends RoundedPanel {
        public MetricCard(String title, String value, Color textColor, Color bgColor) {
            super(8);
            setBackground(bgColor);
            setLayout(null);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(textColor);
            titleLabel.setBounds(10, 10, 120, 15);
            add(titleLabel);

            JLabel valueLabel = new JLabel(value);
            valueLabel.setFont(new Font("Arial", Font.BOLD, 32));
            valueLabel.setForeground(textColor);
            valueLabel.setBounds(40, 30, 60, 40);
            add(valueLabel);
        }
    }

    static class ToolCard extends RoundedPanel {
        public ToolCard(String title, String description, String buttonText, Color textColor, Color bgColor) {
            super(8);
            setBackground(Color.WHITE);
            setBorder(BorderFactory.createLineBorder(new Color(224, 224, 224), 1));
            setLayout(null);

            RoundedPanel icon = new RoundedPanel(8);
            icon.setBackground(bgColor);
            icon.setBounds(20, 30, 40, 40);
            add(icon);

            JLabel titleLabel = new JLabel(title);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
            titleLabel.setForeground(textColor);
            titleLabel.setBounds(70, 30, 200, 20);
            add(titleLabel);

            JLabel descLabel = new JLabel(description);
            descLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            descLabel.setForeground(new Color(102, 102, 102));
            descLabel.setBounds(20, 80, 200, 15);
            add(descLabel);

            RoundedPanel button = new RoundedPanel(8);
            button.setBackground(bgColor);
            button.setBounds(20, 110, 80, 30);
            button.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 5));

            JLabel buttonLabel = new JLabel(buttonText);
            buttonLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            buttonLabel.setForeground(textColor);
            button.add(buttonLabel);
            add(button);
        }
    }

    static class RoundedPanel extends JPanel {
        private final int cornerRadius;

        public RoundedPanel(int cornerRadius) {
            super();
            this.cornerRadius = cornerRadius;
            setOpaque(false);
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Shape round = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
            g2.setColor(getBackground());
            g2.fill(round);
            g2.dispose();
            super.paintComponent(g);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ManagerDashboard().setVisible(true));
    }
}
