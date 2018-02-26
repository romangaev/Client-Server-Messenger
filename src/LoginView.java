import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
/**
 * Just an example login no controls or anything
 * @author Ali Oztas
 *
 */
public class LoginView extends JFrame{


	public static void main(String[] args) {
		JFrame frame = new JFrame("Just random login");
		frame.setSize(500, 250); // Set the size however you want
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel panel = new JPanel();
		frame.add(panel);
		insertStuff(panel);
		frame.setVisible(true);
		
		
	}

	public static void insertStuff(JPanel panel) {
		
		panel.setLayout(null);
		
		JTextField userText = new JTextField(20);
		userText.setBounds(150,20,250,50); //for every textfield and button bounds are kinda important 
		panel.add(userText);
		// What you write in the new JLabel("STRING") that STRING gets written in the button 
		JLabel userLabel = new JLabel("User");
		userLabel.setBounds(20,20,120,50);
		panel.add(userLabel);
		
		JLabel passwordLabel = new JLabel("Password");
		passwordLabel.setBounds(20,80,120,50);
		panel.add(passwordLabel);
		
		JPasswordField passwordText = new JPasswordField(20);
		passwordText.setBounds(150, 80, 250, 50);
		panel.add(passwordText);
		
		JButton loginButton = new JButton("login");
		loginButton.setBounds(40,140,140,50);
		panel.add(loginButton);
		
		JButton registerButton = new JButton("register");
		registerButton.setBounds(300, 140,140,50);
		panel.add(registerButton);
		
//		/*
//		 * There are two versions to do this first is this one just lamb
//		 */
//		ActionListener loginListener = new ActionListener() {
//			
//			 
//			@Override
//			public void actionPerformed(ActionEvent e) {
//				JOptionPane.showMessageDialog(null, "NO CONTROLLZ");
//			}
//		};
		//The other one is creating another class for it
		ActionListener loginListener = new LoginListener();
		loginButton.addActionListener(loginListener);
		
	}
	
	
}
