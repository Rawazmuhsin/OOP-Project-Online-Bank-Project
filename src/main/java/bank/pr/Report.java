package bank.pr;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

public class Report extends JFrame {
    public Report() {
        setTitle("KOB Manager Dashboard");
        setSize(800, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Main panel with custom painting
        JPanel mainPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Background
                g2d.setColor(new Color(240, 244, 248));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                
                // Sidebar Background
                g2d.setColor(new Color(26, 32, 44));
                g2d.fillRect(0, 0, 250, getHeight());
                
                // Header: Bank Manager
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Arial", Font.PLAIN, 18));
                g2d.drawString("KOB Manager", 60, 70);
                
                // Sidebar Links
                g2d.setColor(new Color(52, 58, 64));
                g2d.fillRoundRect(20, 120, 210, 40, 5, 5);
                g2d.setColor(Color.WHITE);
                g2d.drawString("Reports", 80, 145);
                
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.drawString("Dashboard", 60, 220);
                g2d.drawString("Customer Accounts", 60, 260);
                g2d.drawString("Transactions", 60, 300);
                g2d.drawString("Approvals", 60, 340);
                g2d.drawString("Audit Logs", 60, 380);
                
                // Main Content Area
                g2d.setColor(Color.WHITE);
                g2d.fillRoundRect(250, 30, 530, 730, 10, 10); // Adjusted width to fit better
            }
        };
        
        // Content panel that will hold all the main content components
        JPanel contentPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Financial Reports Header
                g2d.setColor(new Color(52, 58, 64));
                g2d.setFont(new Font("Arial", Font.PLAIN, 24));
                g2d.drawString("Financial Reports", 70, 40);
                
                g2d.setColor(new Color(108, 117, 125));
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.drawString("Last generated: Today, 10:15 AM", 70, 80);
                
                // Create New Report Section
                g2d.setColor(new Color(248, 250, 252));
                g2d.fillRoundRect(70, 130, 450, 30, 5, 5);
                
                g2d.setColor(new Color(52, 58, 64));
                g2d.drawString("Create New Report", 90, 150);
                
                // Date Range
                g2d.setColor(new Color(52, 58, 64));
                g2d.drawString("Date Range", 70, 330);
                
                // Total Deposits Card
                g2d.setColor(new Color(204, 229, 255));
                g2d.fillRoundRect(70, 440, 170, 90, 5, 5);
                g2d.setColor(new Color(13, 110, 253));
                g2d.setFont(new Font("Arial", Font.PLAIN, 14));
                g2d.drawString("Total Deposits", 90, 465);
                g2d.setFont(new Font("Arial", Font.PLAIN, 24));
                g2d.drawString("$42,500", 90, 495);
            }
        };
        contentPanel.setLayout(null);
        contentPanel.setOpaque(false);
        contentPanel.setBounds(250, 30, 530, 730);
        
        // Report Template Dropdown (simulated)
        JPanel templatePanel = new JPanel();
        templatePanel.setBackground(new Color(248, 250, 252));
        templatePanel.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239)));
        templatePanel.setBounds(70, 220, 390, 30);
        JLabel templateLabel = new JLabel("Select Report Template");
        templateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        templateLabel.setForeground(new Color(52, 58, 64)); // #343a40
        templatePanel.add(templateLabel);
        contentPanel.add(templatePanel);
        
        // Customize Button
        JButton customizeButton = new JButton("Customize");
        customizeButton.setBounds(470, 220, 150, 30);
        customizeButton.setBackground(new Color(13, 110, 253));
        customizeButton.setForeground(Color.WHITE);
        customizeButton.setBorderPainted(false);
        customizeButton.setFocusPainted(false);
        customizeButton.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(customizeButton);
        
        // Date Input Fields (simulated)
        JPanel startDatePanel = new JPanel();
        startDatePanel.setBackground(new Color(248, 250, 252));
        startDatePanel.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239)));
        startDatePanel.setBounds(70, 350, 170, 30);
        JLabel startDateLabel = new JLabel("01 Jan 2025");
        startDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        startDateLabel.setForeground(new Color(52, 58, 64));
        startDatePanel.add(startDateLabel);
        contentPanel.add(startDatePanel);
        
        JLabel toLabel = new JLabel("to");
        toLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        toLabel.setForeground(new Color(52, 58, 64));
        toLabel.setBounds(250, 355, 20, 20);
        contentPanel.add(toLabel);
        
        JPanel endDatePanel = new JPanel();
        endDatePanel.setBackground(new Color(248, 250, 252));
        endDatePanel.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239)));
        endDatePanel.setBounds(280, 350, 170, 30);
        JLabel endDateLabel = new JLabel("31 Jan 2025");
        endDateLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        endDateLabel.setForeground(new Color(52, 58, 64));
        endDatePanel.add(endDateLabel);
        contentPanel.add(endDatePanel);
        
        // Action Buttons
        JButton generateReportButton = new JButton("Generate Report");
        generateReportButton.setBounds(220, 680, 150, 30);
        generateReportButton.setBackground(new Color(13, 110, 253));
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.setBorderPainted(false);
        generateReportButton.setFocusPainted(false);
        generateReportButton.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(generateReportButton);
        
        JButton exportOptionsButton = new JButton("Export Options");
        exportOptionsButton.setBounds(380, 680, 150, 30);
        exportOptionsButton.setBackground(new Color(248, 250, 252));
        exportOptionsButton.setForeground(new Color(52, 58, 64));
        exportOptionsButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239)));
        exportOptionsButton.setFocusPainted(false);
        exportOptionsButton.setFont(new Font("Arial", Font.PLAIN, 14));
        contentPanel.add(exportOptionsButton);
        
        // Add the back to dashboard button
        JButton backToDashboardButton = new JButton("Back to Dashboard");
        backToDashboardButton.setBounds(70, 680, 140, 30);
        backToDashboardButton.setBackground(new Color(248, 250, 252));
        backToDashboardButton.setForeground(new Color(52, 58, 64));
        backToDashboardButton.setBorder(BorderFactory.createLineBorder(new Color(235, 237, 239)));
        backToDashboardButton.setFocusPainted(false);
        backToDashboardButton.setFont(new Font("Arial", Font.PLAIN, 14));
        backToDashboardButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                SwingUtilities.invokeLater(() -> new ManagerDashboard().setVisible(true));
            }
        });
        contentPanel.add(backToDashboardButton);
        
        // Add navigation for sidebar menu items
        addSidebarNavigation(mainPanel);
        
        // Set layout to null for absolute positioning
        mainPanel.setLayout(null);
        mainPanel.add(contentPanel);
        add(mainPanel);
    }
    
    /**
     * Adds click listeners to the sidebar menu items for navigation
     */
    private void addSidebarNavigation(JPanel mainPanel) {
        // Create invisible buttons over the sidebar text for navigation
        String[] menuItems = {"Dashboard", "Customer Accounts", "Transactions", "Approvals", "Audit Logs"};
        int[] yPositions = {220, 260, 300, 340, 380};
        
        for (int i = 0; i < menuItems.length; i++) {
            JButton navButton = new JButton();
            navButton.setBounds(20, yPositions[i] - 15, 210, 30);
            navButton.setOpaque(false);
            navButton.setContentAreaFilled(false);
            navButton.setBorderPainted(false);
            
            final String item = menuItems[i];
            navButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    navigateToScreen(item);
                }
            });
            
            mainPanel.add(navButton);
        }
    }
    
    /**
     * Helper method to navigate between screens
     */
    private void navigateToScreen(String screenName) {
        dispose(); // Close the current window
        
        switch (screenName) {
            case "Dashboard":
                SwingUtilities.invokeLater(() -> new ManagerDashboard().setVisible(true));
                break;
            case "Customer Accounts":
                SwingUtilities.invokeLater(() -> new CustomerAccounts().setVisible(true));
                break;
            case "Transactions":
                SwingUtilities.invokeLater(() -> new ManageTransaction().setVisible(true));
                break;
            case "Approvals":
                // For now, just show a message and return to dashboard
                JOptionPane.showMessageDialog(null, 
                    "Approval Queue screen is under development.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> new ManagerDashboard().setVisible(true));
                break;
            case "Audit Logs":
                // For now, just show a message and return to dashboard
                JOptionPane.showMessageDialog(null, 
                    "Audit Logs screen is under development.", 
                    "Coming Soon", JOptionPane.INFORMATION_MESSAGE);
                SwingUtilities.invokeLater(() -> new ManagerDashboard().setVisible(true));
                break;
            default:
                SwingUtilities.invokeLater(() -> new ManagerDashboard().setVisible(true));
        }
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Report dashboard = new Report();
            dashboard.setVisible(true);
        });
    }
}