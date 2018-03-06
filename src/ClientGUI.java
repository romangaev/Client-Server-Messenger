package trying;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * 
 * @author Ali Oztas
 *
 */
public class ClientGUI extends JFrame implements ActionListener {

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 3622985772063112589L;
	private JLabel label; // this will first say Username: then Enter message#
	private JTextField textField; // first username and again then the messages
	private JTextField textServer,textPort; // holding address and port number
	private JButton login, logout, onlineUsers; // For necessary buttons
	private JTextArea textArea;
	private boolean connected;
	private Client client;
	private int defaultPort;
	private String defaultHost;

	public ClientGUI(String host, int port) {
		super("Chat Client");
		defaultPort = port;
		defaultHost = host;
		
		JPanel northPanel = new JPanel(new GridLayout(3,1));
		JPanel serverAndPort = new JPanel(new GridLayout(1,5, 1, 3));
		textServer = new JTextField(host);
		textPort = new JTextField(""+ port);
		textPort.setHorizontalAlignment(SwingConstants.RIGHT);
		serverAndPort.add(new JLabel("Host:  "));
		serverAndPort.add(textServer);
		serverAndPort.add(new JLabel("Port:  "+port));
		serverAndPort.add(textPort);
		serverAndPort.add(new JLabel(""));
		northPanel.add(serverAndPort);
		//Label and TextField
		
		label = new JLabel("Enter your username please",SwingConstants.CENTER);
		northPanel.add(label);
		textField = new JTextField("Random");
		textField.setBackground(Color.WHITE);
		northPanel.add(textField);
		add(northPanel, BorderLayout.NORTH);
		
		//Center panel Chat Room
		textArea = new JTextArea("Welcome to Mumbai Chat Room\n",80, 80);
		JPanel centerPanel = new JPanel(new GridLayout(1,1));
		centerPanel.add(new JScrollPane(textArea));
		textArea.setEditable(false);
		add(centerPanel, BorderLayout.CENTER);
		
		login = new JButton("Login");
		login.addActionListener(this);
		logout = new JButton("Logout");
		logout.addActionListener(this);
		logout.setEnabled(false); //After login this will be set to true
		
		onlineUsers = new JButton("Online Users");
		onlineUsers.addActionListener(this);
		onlineUsers.setEnabled(false); // And again after login this will be set to true
		
		JPanel southPanel = new JPanel();
		southPanel.add(login);
		southPanel.add(logout);
		southPanel.add(onlineUsers);
		add(southPanel, BorderLayout.SOUTH);
		
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setSize(600, 600);
		setVisible(true);
		//call textField.requestFocus()
			
		}
	//Called by client to append text
	void append(String str) {
		textArea.append(str);
		textArea.setCaretPosition(textArea.getText().length() - 1);
	}
	
	void connectionFailed() {
		login.setEnabled(true);
		logout.setEnabled(false);
		onlineUsers.setEnabled(false);
		label.setText("Enter your username please");
		textPort.setText("" + defaultPort);
		textServer.setText(defaultHost);
		textPort.setEditable(true);
		textServer.setEditable(true);
		textField.removeActionListener(this);
		connected = false;
	}
	
	public void actionPerformed(ActionEvent e) {
		Object o = e.getSource();
		if(o == logout) {
			client.sendMessage(new ChatMessage(ChatMessage.MSG, ""));
			return;
		}
		if(o == onlineUsers) {
			client.sendMessage(new ChatMessage(ChatMessage.WHO, ""));
		}
		if(connected) {
			client.sendMessage(new ChatMessage(ChatMessage.MSG, textField.getText()));
			textField.setText("");
			return;
		}
		if(o == login) {
			String username = textField.getText().trim();
			String server = textServer.getText().trim();
			String portNum = textPort.getText().trim();
			if(username.length() == 0 || server.length() == 0 || portNum.length() == 0) {
				return;
			}
			int port = 0;
			try {
				port = Integer.parseInt(portNum);
			}
			catch(Exception e1) {
				e1.printStackTrace();
				return;
			}
			
			client = new Client(server, port, username, this);
			//if you can start it or not
			if(!client.start()) {
				return;
			}
			textField.setText("");
			label.setText("Enter your username please");
			connected = true;
			
			//disable login,port,server and enable others
			textServer.setEditable(false);
			textServer.setEnabled(true);
			login.setEnabled(false);
			logout.setEnabled(true);
			onlineUsers.setEnabled(true);
			textField.addActionListener(this);
			
			
		}
	}
	
	
	public static void main(String[] args) {
		new ClientGUI("localhost", 51000);
	}
	
	
	
	
	

}
