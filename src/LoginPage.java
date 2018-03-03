
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

/**
 * @author Ioana Avirvarei
 * GUI for login, register and chat page
 *
 */
public class LoginPage {

	LoginPage LoginPage;
	JFrame chat = new JFrame("Chat"); // chat
	Font titleFont = new Font("Script MT Bold", Font.BOLD, 30); // font for the title
	Font textingFont = new Font("French Script MT", Font.ITALIC, 30); // font for the SEND button

	JButton send;
	JTextField messageBox;
	JTextArea chatBox;
	JTextField userText;
	JFrame window; // window
	JFrame register; // frame for register page

	public static void main(String[] args) {
		LoginPage LoginPage = new LoginPage();
		LoginPage.loginPage();
	}

	public void loginPage() {
		chat.setVisible(false);
		window = new JFrame("Login/Register");

		window.setSize(450, 730); // size of the window
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel prePanel = new JPanel();
		window.add(prePanel);

		userText = new JTextField();

		prePanel.setLayout(null);
		JLabel welcomeLabel = new JLabel("<html>Welcome to<br> Messenger!</html>");
		welcomeLabel.setBounds(150, 100, 600, 250); // coordinates for the title
		prePanel.add(welcomeLabel, BorderLayout.NORTH);
		welcomeLabel.setFont(titleFont); // set the font
		welcomeLabel.setForeground(Color.GRAY); // set the color

		prePanel.add(userText);
		// for user
		JLabel userLabel = new JLabel("User"); // chooseusernameLabel
		userLabel.setBounds(80, 300, 80, 25); // coordinates for the user field
		prePanel.add(userLabel);

		userText = new JTextField();
		userText.setBounds(130, 300, 250, 25); // coordinates for the user field
		prePanel.add(userText);

		// for password
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(50, 340, 80, 25);
		prePanel.add(passwordLabel);

		JPasswordField passwordText = new JPasswordField(20);
		passwordText.setBounds(130, 335, 250, 25); // coordinates for the password text
		prePanel.add(passwordText);

		// login button
		JButton loginButton = new JButton("Login");
		loginButton.setBounds(170, 400, 100, 35); // coordinates for the login button
		prePanel.add(loginButton);

		// register button
		JButton registerButton = new JButton("Register");
		registerButton.setBounds(170, 450, 100, 35); // coordinates for the register button
		prePanel.add(registerButton);
		window.setVisible(true);

		loginButton.addActionListener(new loginButtonListener());
		registerButton.addActionListener(new registerButtonListener());

	}

	public void registerPage() {
		// window settings
		window.setVisible(false); // set visibility false for the first login page
		chat.setVisible(false); // set visibility false for the chat
		register = new JFrame("Register "); // create a new window

		register.setSize(450, 730); // size of the register
		register.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		JPanel registerPanel = new JPanel();
		register.add(registerPanel);

		// for title
		registerPanel.setLayout(null);
		JLabel registerLabel = new JLabel("<html>Enter your registration<br>details: </html>");
		registerLabel.setBounds(100, 100, 600, 250); // coordinates for the title
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

	public void chatPage() {
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

	String username;

	class loginButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			username = userText.getText();
			if (username.length() < 1) {
				System.out.println("No!");
			} else {
				window.setVisible(false);
				chatPage();
			}
		}

	}

	class registerButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			window.setVisible(false);
			registerPage();
		}

	}

	class register1ButtonListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			username = userText.getText();
			if (username.length() < 1) {
				System.out.println("No!");
			} else {
				loginPage();
				register.setVisible(false);
				chatBox.setVisible(false);
			}
		}

	}

}