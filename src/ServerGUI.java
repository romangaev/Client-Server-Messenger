package trying;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class ServerGUI extends JFrame implements ActionListener, WindowListener {

	/**
	 * 
	 */
	private static final long serialVersionUID = -668900007633952638L;

	private JButton stopStart;
	private JTextArea chat, event;
	private JTextField tPort;
	private Server server;
	
	public ServerGUI(int port) {
		super("Chat Server");
		server = null; // In NorthPanel, the portNum and start/stop buttons
		JPanel north = new JPanel();
		north.add(new JLabel("Port number: "));
		tPort = new JTextField(" "+ port);
		north.add(tPort);
		add(north,BorderLayout.NORTH);
		//Event and Chat Room
		JPanel center = new JPanel(new GridLayout(2,1));
		chat = new JTextArea(80,80);
		chat.setEditable(false);
		appendRoom("Chat room.\n");
		center.add(new JScrollPane(chat));
		event = new JTextArea(80,80);
		event.setEditable(false);
		appendEvent("Events.\n");
		center.add(new JScrollPane(event));
		add(center);
		// If user clicks the close button
		addWindowListener(this);
		setSize(400,600);
		setVisible(true);
	}
	//Append message to two JTextArea
	void appendRoom(String str) {
		chat.append(str);
		chat.setCaretPosition(chat.getText().length() - 1);
	}
	void appendEvent(String str) {
		event.append(str);
		event.setCaretPosition(chat.getText().length() - 1);
	}
	//Start or stop
	
	public void actionPerformed(ActionEvent e) {
		
		//if server is running stop
		if(server != null) {
			server.stop();
			server = null;
			tPort.setEditable(true);
			stopStart.setText("Start");
			return;
		}
		//starting the server
		int port;
		try {
			port = Integer.parseInt(tPort.getText().trim());
		}
		catch(Exception e1) {
			appendEvent("Invalid port number");
			return;
		}
		server = new Server(port, this);
		//starting server and as a thread
		new ServerRunning().start();
		stopStart.setText("Stop");
		tPort.setEditable(false);
	}
	
	public void windowClosing(WindowEvent e) {
		if(server != null) {
			try {
				server.stop();
			}
			catch(Exception e1) {
				e1.printStackTrace();
			}
			server = null;
		}
		dispose();
		System.exit(0);
	}
	@Override
	public void windowActivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowClosed(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeactivated(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowDeiconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowIconified(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public void windowOpened(WindowEvent e) {
		// TODO Auto-generated method stub
		
	}
	
	class ServerRunning extends Thread {
		public void run() {
			server.start();
			stopStart.setText("Start");
			tPort.setEditable(true);
			appendEvent("Server crashed! \n");
			server = null;
		}
	}
	
	
	public static void main(String[] args) {
		new ServerGUI(51000);
	}

	
	
	
	
	
	

}
