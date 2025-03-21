import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class Gui extends JFrame implements ActionListener {

	private static final Color DARK_BLUE = new Color(25, 33, 43);
	private static final Color ORANGE = new Color(255, 140, 0);
	private static final Color LIGHT_GRAY = new Color(240, 240, 240);
	private static final Color WHITE = Color.WHITE;

	private JTextField userText;
	private JPasswordField passwordText;
	private JCheckBox showPassword;
	private JLabel success;

	public Gui() {

		setTitle("Login System");
		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setResizable(false);

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(null);
		mainPanel.setBackground(DARK_BLUE);

		JPanel titlePanel = new JPanel();
		titlePanel.setBounds(0, 20, 400, 40);
		titlePanel.setBackground(DARK_BLUE);
		JLabel titleLabel = new JLabel("Welcome ");
		titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
		titleLabel.setForeground(WHITE);
		titlePanel.add(titleLabel);
		mainPanel.add(titlePanel);

		JPanel loginPanel = new JPanel();
		loginPanel.setBounds(50, 70, 300, 150);
		loginPanel.setLayout(null);
		loginPanel.setBackground(DARK_BLUE);

		JLabel userLabel = new JLabel("Username");
		userLabel.setBounds(0, 0, 100, 25);
		userLabel.setForeground(WHITE);
		userLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		loginPanel.add(userLabel);

		userText = new JTextField();
		userText.setBounds(0, 25, 300, 35);
		userText.setFont(new Font("Arial", Font.PLAIN, 14));
		userText.setBackground(LIGHT_GRAY);
		userText.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ORANGE, 2),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		loginPanel.add(userText);

		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(0, 70, 100, 25);
		passwordLabel.setForeground(WHITE);
		passwordLabel.setFont(new Font("Arial", Font.PLAIN, 14));
		loginPanel.add(passwordLabel);

		passwordText = new JPasswordField();
		passwordText.setBounds(0, 95, 300, 35);
		passwordText.setFont(new Font("Arial", Font.PLAIN, 14));
		passwordText.setBackground(LIGHT_GRAY);
		passwordText.setBorder(BorderFactory.createCompoundBorder(new LineBorder(ORANGE, 2),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		passwordText.setEchoChar('●');
		loginPanel.add(passwordText);

		showPassword = new JCheckBox("Show Password");
		showPassword.setBounds(0, 130, 150, 25);
		showPassword.setForeground(WHITE);
		showPassword.setBackground(DARK_BLUE);
		showPassword.setFont(new Font("Arial", Font.PLAIN, 12));
		showPassword.addActionListener(this);
		loginPanel.add(showPassword);

		mainPanel.add(loginPanel);

		JPanel buttonPanel = new JPanel();
		buttonPanel.setBounds(50, 230, 300, 40);
		buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
		buttonPanel.setBackground(DARK_BLUE);

		JButton loginButton = new JButton("Login");
		loginButton.setPreferredSize(new Dimension(120, 35));
		loginButton.setBackground(ORANGE);
		loginButton.setForeground(WHITE);
		loginButton.setFont(new Font("Arial", Font.BOLD, 14));
		loginButton.setFocusPainted(false);
		loginButton.setBorderPainted(false);
		loginButton.addActionListener(this);
		buttonPanel.add(loginButton);

		JButton forgotButton = new JButton("Forgot Password");
		forgotButton.setPreferredSize(new Dimension(120, 35));
		forgotButton.setBackground(LIGHT_GRAY);
		forgotButton.setForeground(DARK_BLUE);
		forgotButton.setFont(new Font("Arial", Font.BOLD, 14));
		forgotButton.setFocusPainted(false);
		forgotButton.setBorderPainted(false);
		forgotButton.addActionListener(e -> {
			this.dispose();
			//new ForgotPassword().setVisible(true);
		});
		buttonPanel.add(forgotButton);

		mainPanel.add(buttonPanel);

		success = new JLabel("");
		success.setBounds(50, 270, 300, 20);
		success.setForeground(ORANGE);
		success.setFont(new Font("Arial", Font.PLAIN, 12));
		success.setHorizontalAlignment(JLabel.CENTER);
		mainPanel.add(success);

		add(mainPanel);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == showPassword) {
			if (showPassword.isSelected()) {
				passwordText.setEchoChar((char) 0);
			} else {
				passwordText.setEchoChar('●');
			}
		} else {
			String user = userText.getText();
			String password = new String(passwordText.getPassword());

			if (user.equals("user") && password.equals("123@epu")) {
				success.setText("Login successful!");
				success.setForeground(new Color(0, 150, 0));

				this.dispose();
				//new ForgotPassword().setVisible(true);
			} else {
				success.setText("Invalid login, please try again");
				success.setForeground(ORANGE);
			}
		}
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(() -> {
			new Gui().setVisible(true);
		});
	}
}