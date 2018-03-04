


import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.UIManager;

public class LoginPage {

    static LoginPage LoginPage;
    static JFrame chat = new JFrame("Chat"); //chat
	static Font titleFont = new Font("Script MT Bold", Font.BOLD, 30); // font for the title
    static Font textingFont = new Font("French Script MT", Font.ITALIC, 30); //font for the SEND button
   static sendButtonListener sendListener;
   public static JButton send;
   public static JTextField messageBox;
   public static JTextArea chatBox;
   public static JTextField userText;
   static JPasswordField passwordText;
   private static LoginDet logindet;
    static JFrame window; //window
    private static LoginDB logDB;
    public static void main(String[] args) {
        LoginPage LoginPage = new LoginPage();
        LoginPage.loginPage();
    }


    public void loginPage() {
        chat.setVisible(false);
        window = new JFrame("Login/Register");
        
		window.setSize(500, 750); // size of the window
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		JPanel prePanel = new JPanel();
		window.add(prePanel);

        userText = new JTextField();

        prePanel.setLayout(null);
		JLabel welcomeLabel = new JLabel("Welcome to Messenger!");
		welcomeLabel.setBounds(100, 100, 600, 250); // coordinates for the title
		prePanel.add(welcomeLabel, BorderLayout.NORTH);
		welcomeLabel.setFont(titleFont); // set the font
		welcomeLabel.setForeground(Color.GRAY); // set the color
        
        prePanel.add(userText);
      //for user
      		JLabel userLabel = new JLabel("User"); //chooseusernameLabel
      		userLabel.setBounds(80, 300, 80, 25); // coordinates for the user field
      		prePanel.add(userLabel);

      	    userText = new JTextField();
      		userText.setBounds(130, 300, 300, 25); // coordinates for the user field
      		prePanel.add(userText);

      		// for password
      		JLabel passwordLabel = new JLabel("Password");
      		passwordLabel.setBounds(50, 340, 80, 25);
      		prePanel.add(passwordLabel);

      		passwordText = new JPasswordField(20);
      		passwordText.setBounds(130, 335, 300, 25); // coordinates for the password text
      		prePanel.add(passwordText);

      		//login button
      		JButton loginButton = new JButton("Login");
      		loginButton.setBounds(200, 400, 100, 35); // coordinates for the login button
      		prePanel.add(loginButton);

      		//register button
      		JButton registerButton = new JButton("Register");
      		registerButton.setBounds(200, 450, 100, 35); // coordinates for the register button
      		prePanel.add(registerButton);
      		window.setVisible(true);
        
//        window.add(BorderLayout.CENTER, prePanel);
//        window.add(BorderLayout.SOUTH, login);
//        window.setVisible(true);
//        window.setSize(300, 300);

        loginButton.addActionListener(new loginButtonListener());
        registerButton.addActionListener(new loginButtonListener());

    }

    public static void chatPage() {
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

        chatBox.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createTitledBorder("")
                , BorderFactory.createEmptyBorder(10, 10, 10, 10)));
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
        sendListener = new sendButtonListener();
        send.addActionListener(sendListener);
        chat.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    }

    static class sendButtonListener implements ActionListener {
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

    static String username;
    static char[] password;
    static class loginButtonListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            username = userText.getText();
            password = passwordText.getPassword();
            logindet = new LoginDet(username, password);
            logDB.accessUser(logindet);
            
            if (username.length() < 1) {System.out.println("No!"); }
            else {
            window.setVisible(false);
            chatPage();
            }
        }

    }
}