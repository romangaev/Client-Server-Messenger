package messengerProject;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.Statement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.UIManager;

/**
 * @author Ioana Avirvarei GUI for login, register and chat page
 *
 */
public class LoginPage {

	LoginPage LoginPage;
	JFrame chat = new JFrame("Chat"); // chat
	Font titleFont = new Font("Script MT Bold", Font.BOLD, 35); // font for the title
	Font textingFont = new Font("French Script MT", Font.ITALIC, 30); // font for the SEND button

	JButton send;
	JButton loginButton;
	JButton registerButton;
	JTextField messageBox;
	JTextArea chatBox;
	JTextField userText;
	JPasswordField passwordText;
	JFrame window; // window
	JFrame register; // frame for register page
	GridBagConstraints gbc;
            
	public static void main(String[] args) {
		// try {
		// UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		LoginPage LoginPage = new LoginPage();
		LoginPage.setupLoginPage();

	}


	public void setupLoginPage() {

		chat.setVisible(false);
		window = new JFrame("Login/Register");
		window.setSize(450, 730); // size of the window
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel prePanel = new JPanel(new GridBagLayout());
		window.add(prePanel);
		prePanel.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.insets = new Insets(4, 4, 4, 4);

		JLabel welcomeLabel = new JLabel("<html>Welcome to<br> Messenger!</html>", SwingConstants.CENTER);
		gbc.fill = GridBagConstraints.HORIZONTAL;

		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.ipady = 40; // increase height of the title
		gbc.weightx = 0.5;
		gbc.gridwidth = 4;
		gbc.gridx = 0;
		gbc.gridy = 0;

		prePanel.add(welcomeLabel, gbc);

		welcomeLabel.setFont(titleFont); // set the font
		welcomeLabel.setForeground(Color.GRAY); // set the color

		// for user
		JLabel userLabel = new JLabel("User", SwingConstants.CENTER);
		gbc.ipady = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.gridwidth = 2;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(10, 80, 0, 80);

		prePanel.add(userLabel, gbc);

		userText = new JTextField();
		gbc.ipady = 0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		gbc.insets = new Insets(10, 80, 0, 80);

		prePanel.add(userText, gbc);

		// for password
		JLabel passwordLabel = new JLabel("Password", SwingConstants.CENTER);
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.5;
		prePanel.add(passwordLabel, gbc);

		JPasswordField passwordText = new JPasswordField(20);
		gbc.ipady = 0;
		gbc.gridx = 0;
		gbc.gridy = 2;
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.weightx = 0.5;
		prePanel.add(passwordText, gbc);

		// login button
		JButton loginButton = new JButton("Login");
		gbc.ipady = 0;
		// gbc.weighty = 1;
		gbc.insets = new Insets(100, 150, 0, 150);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.gridy = 4;
		prePanel.add(loginButton, gbc);

		// register button
		JButton registerButton = new JButton("Register");
		// gbc.ipady = 1;
		// gbc.weighty = 1;
		gbc.insets = new Insets(10, 150, 0, 150);
		gbc.gridx = 1;
		gbc.gridwidth = 2;
		gbc.gridy = 5;

		prePanel.add(registerButton, gbc);

		window.setVisible(true);

		loginButton.addActionListener(new loginButtonListener());
		registerButton.addActionListener(new registerButtonListener());

	}

	public void setupRegisterPage() {
		// window settings
		window.setVisible(false); // set visibility false for the first login page
		chat.setVisible(false); // set visibility false for the chat
		register = new JFrame("Register "); // create a new window

		// register.setSize(450, 730); // size of the register
		register.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel registerPanel = new JPanel();
		register.add(registerPanel);

		// for title
		registerPanel.setLayout(null);
		JLabel registerLabel = new JLabel("<html>Enter your registration<br>details: </html>");
		// registerLabel.setBounds(100, 100, 600, 250); // coordinates for the title
		registerPanel.add(registerLabel, BorderLayout.NORTH);
		registerLabel.setFont(titleFont); // set the font
		registerLabel.setForeground(Color.GRAY); // set the color

		// for username
		JLabel userLabel = new JLabel("Username"); // chooseusernameLabel
		userLabel.setBounds(50, 300, 80, 30); // coordinates for the user field
		registerPanel.add(userLabel);

		userText = new JTextField();
		userText.setBounds(130, 300, 250, 25); // coordinates for the user field
		registerPanel.add(userText);

		// for password
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(50, 340, 80, 30);
		registerPanel.add(passwordLabel);

		JPasswordField passwordText = new JPasswordField(20);
		passwordText.setBounds(130, 340, 250, 25); // coordinates for the password text
		registerPanel.add(passwordText);

		// for confirm password
		JLabel confirmPasswordLabel = new JLabel("<html>Confirm<br>password</html>");
		confirmPasswordLabel.setBounds(50, 380, 80, 30);
		registerPanel.add(confirmPasswordLabel);

		JPasswordField confirmPasswordText = new JPasswordField(20);
		confirmPasswordText.setBounds(130, 380, 250, 25); // coordinates for the password text
		registerPanel.add(confirmPasswordText);

		// for name
		JLabel nameLabel = new JLabel("Name");
		nameLabel.setBounds(50, 420, 80, 30);
		registerPanel.add(nameLabel);

		JTextField nameText = new JTextField(20);
		nameText.setBounds(90, 420, 100, 25); // coordinates for the password text
		registerPanel.add(nameText);

		// for surname
		JLabel surnameLabel = new JLabel("Surname");
		surnameLabel.setBounds(200, 420, 80, 30);
		registerPanel.add(surnameLabel);

		JTextField surnameText = new JTextField(20);
		surnameText.setBounds(260, 420, 120, 25);
		registerPanel.add(surnameText);

		// for date of birth
		JLabel dateLabel = new JLabel("Date of Birth");
		dateLabel.setBounds(50, 460, 80, 30);
		registerPanel.add(dateLabel);

		JTextField dateText = new JTextField(20);
		dateText.setBounds(130, 460, 120, 25);
		registerPanel.add(dateText);

		// for gender
		JLabel genderLabel = new JLabel("Gender");
		genderLabel.setBounds(255, 460, 80, 30);
		registerPanel.add(genderLabel);

		JTextField genderText = new JTextField(20);
		genderText.setBounds(300, 460, 80, 25);
		registerPanel.add(genderText);

		// for Location
		JLabel locationLabel = new JLabel("Location");
		locationLabel.setBounds(50, 500, 80, 30);
		registerPanel.add(locationLabel);

		JTextField locationText = new JTextField(20);
		locationText.setBounds(130, 500, 120, 25);
		registerPanel.add(locationText);

		// for status
		JLabel statusLabel = new JLabel("Status");
		statusLabel.setBounds(255, 500, 80, 30);
		registerPanel.add(statusLabel);

		JTextField statusText = new JTextField(20);
		statusText.setBounds(300, 500, 80, 25);
		registerPanel.add(statusText);

		// register button
		JButton register1Button = new JButton("Register");
		register1Button.setBounds(170, 600, 100, 35); // coordinates for the register button
		registerPanel.add(register1Button);

		register1Button.addActionListener(new register1ButtonListener());

		register.setVisible(true); // set visibility for register page

	}

	public void setupChatPage() {
		
		chat.setVisible(true);
		chat.setSize(500, 750);

		JPanel textingPanel = new JPanel();
		textingPanel.setBackground(Color.lightGray);

		chat.add(BorderLayout.SOUTH, textingPanel);

		textingPanel.setLayout(new GridBagLayout());

		JLabel information = new JLabel("Enter message to be sent:");
		information.setBounds(20, 20, 260, 25);
		information.setFont(textingFont);
		chat.add(information);

		messageBox = new JTextField(30);
		messageBox.setPreferredSize(new Dimension(400, 30));
		send = new JButton("Send");
		send.setFont(titleFont);
		send.setBounds(330, 70, 80, 75);

		chatBox = new JTextArea();
		chatBox.setEditable(false);
		chatBox.setWrapStyleWord(true);
		chatBox.setLineWrap(true);

		chatBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createTitledBorder(""),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)));
		chatBox.requestFocusInWindow();

		chat.add(new JScrollPane(chatBox), BorderLayout.CENTER);

		GridBagConstraints left = new GridBagConstraints();
		left.anchor = GridBagConstraints.WEST;
		GridBagConstraints right = new GridBagConstraints();
		right.anchor = GridBagConstraints.EAST;
		right.weightx = 1.0;

		textingPanel.add(messageBox, left);
		textingPanel.add(send, right);

		chatBox.setFont(new Font("Serif", Font.PLAIN, 15));
		send.addActionListener(new sendButtonListener());
		chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

	}

	String username;
	
	class sendButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			if (messageBox.getText().length() < 1) {
				// do nothing
			} else if (messageBox.getText().equals(".clear")) {
				chatBox.setText("Cleared all messages\n");
				messageBox.setText("");
			} else {
				chatBox.append("[" + username + "]: " + messageBox.getText() + "\n");
				messageBox.setText("");
			}
		}
	}


	class loginButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			username = userText.getText();
//			password = new String(passwordText.getPassword());
			if (username.length() < 1) {
				System.out.println("No!");
			} else {
				window.setVisible(false);
				setupChatPage();
			}
		}

	}

	class registerButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			window.setVisible(false);
			setupRegisterPage();
		}

	}

	class register1ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			username = userText.getText();
			if (username.length() < 1) {
				System.out.println("No!");
			} else {
				setupLoginPage();
				register.setVisible(false);
				chatBox.setVisible(false);
			}
		}

	}

}