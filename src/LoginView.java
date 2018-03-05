
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.*;

/**
 * @author Ioana Avirvarei GUI for login, register and chat page
 *
 */
public class LoginView extends JFrame {

	private final ClientModel client;
	JTextField loginField;
	JPasswordField passwordField;
	private JLabel stateLabel= new JLabel("Loading...");
	private JPanel cardsPanel;
	Font titleFont = new Font("Script MT Bold", Font.BOLD, 30); // font for the title


	public LoginView(){
			super("Login/Sign up");

			//Set up client
			client= new ClientModel("localhost",5000);
			if(!client.connect()) stateLabel.setText("Error: unable to connect!");
			//Set up GUI

		cardsPanel = new JPanel(new CardLayout());
		cardsPanel.add(buildLogPanel());
		cardsPanel.add(buildRegPanel());

		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel,BoxLayout.Y_AXIS));
		mainPanel.setBorder(BorderFactory.createEmptyBorder(10,10,0,10));
		mainPanel.add(cardsPanel);
		mainPanel.add(stateLabel);
		add(mainPanel);
		setSize(500,400);
		setResizable(false);
		setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
		setVisible(true);
	}



	public JPanel buildLogPanel(){
		JPanel logPanel = new JPanel();
		logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

		JLabel welcomeLabel = new JLabel("<html>Welcome to<br> Messenger!</html>", SwingConstants.CENTER);
		welcomeLabel.setFont(titleFont); // set the font
		welcomeLabel.setForeground(Color.GRAY); // set the color
		welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


		loginField = new JTextField();
		loginField.setAlignmentX(Component.CENTER_ALIGNMENT);

		passwordField = new JPasswordField();
		passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton loginButton = new JButton("Login");
		loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

		JButton registerButton = new JButton("Register");
		registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);


		logPanel.add(welcomeLabel);
		logPanel.add(Box.createRigidArea(new Dimension(0,50)));
		logPanel.add(loginField);
		logPanel.add(Box.createRigidArea(new Dimension(0,5)));
		logPanel.add(passwordField);
		logPanel.add(Box.createRigidArea(new Dimension(0,10)));
		logPanel.add(loginButton);
		logPanel.add(Box.createRigidArea(new Dimension(0,10)));
		logPanel.add(registerButton);
		logPanel.add(Box.createRigidArea(new Dimension(0,50)));


		loginButton.addActionListener(new loginActionListener());
		registerButton.addActionListener(new registerActionListener());
		return  logPanel;
	}




	public JPanel buildRegPanel(){
		JPanel regPanel= new JPanel();
		regPanel.setLayout(new BoxLayout(regPanel, BoxLayout.Y_AXIS));

		JLabel registerLabel = new JLabel("<html>Enter your registration<br>details: </html>");
		registerLabel.setFont(titleFont); // set the font
		registerLabel.setForeground(Color.GRAY); // set the color
		regPanel.add(registerLabel, BorderLayout.NORTH);
		regPanel.add(Box.createRigidArea(new Dimension(0,5)));

		// for username
		JLabel userLabel = new JLabel("Username"); // chooseusernameLabel
		regPanel.add(userLabel);
		regPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JTextField userText = new JTextField();
		regPanel.add(userText);
		regPanel.add(Box.createRigidArea(new Dimension(0,5)));

		// for password
		JLabel passwordLabel = new JLabel("Password");
		regPanel.add(passwordLabel);
		regPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JPasswordField passwordText = new JPasswordField(20);
		regPanel.add(passwordText);
		regPanel.add(Box.createRigidArea(new Dimension(0,5)));

		// for confirm password
		JLabel confirmPasswordLabel = new JLabel("Confirm password");
		regPanel.add(confirmPasswordLabel);
		regPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JPasswordField confirmPasswordText = new JPasswordField(20);
		regPanel.add(confirmPasswordText);
		regPanel.add(Box.createRigidArea(new Dimension(0,5)));

		// for name
		JLabel nameLabel = new JLabel("Name");
		regPanel.add(nameLabel);
		regPanel.add(Box.createRigidArea(new Dimension(0,5)));

		JTextField nameText = new JTextField(20);
		regPanel.add(nameText);
		regPanel.add(Box.createRigidArea(new Dimension(0,10)));


		// register button
		JButton confirmButton = new JButton("Register");
		regPanel.add(confirmButton);
		confirmButton.addActionListener(new registerActionListener());
		regPanel.add(Box.createRigidArea(new Dimension(0,5)));

		return regPanel;
	}









	class loginActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			String username = loginField.getText();
			String password = passwordField.getPassword().toString();
			/*if (client.login(username,password)) {
				// bring up the user list window


				//MainChatView panel =new MainChatView(client);

				JFrame frame = new JFrame("Messenger");
				frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				frame.setSize(400, 600);
				//frame.add(panel);
				frame.setVisible(true);
				setVisible(false);
			} else {
				// show error message
				//JOptionPane.showMessageDialog(this, "Invalid login/password.");

			}
			**/
		}

	}

	class registerActionListener implements ActionListener {
		public void actionPerformed(ActionEvent event) {
			CardLayout cl = (CardLayout)(cardsPanel.getLayout());
			cl.next(cardsPanel);
		}

	}

	public static void main(String[] args) {
		new LoginView();
	}



}