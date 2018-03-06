package trying;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * After server this will be started
 * @author Ali Oztas
 *
 */
public class Client {

	private ObjectInputStream in;
	private ObjectOutputStream out;
	private Socket socket;
	
	private ClientGUI clientGUI;
	private String server, username;
	private int port;
	
	/*
	 * This one is for console
	 */
	Client(String server, int port, String username){
		this(server,port,username,null);
	}
	public Client(String server, int port, String username, ClientGUI clientGUI){
		this.server = server;
		this.port = port;
		this.username = username;
		this.clientGUI = clientGUI;
	}
	
	public boolean start() {
		try {
			socket = new Socket(server, port);
		}
		catch(Exception e) {
			display("Error connecting to server: " + e);
			return false;
			}
	String msg = "Connection accepted " + socket.getInetAddress() + ":" + socket.getPort();
	display(msg);
	
	try {
		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());
	}
	catch(IOException e) {
		display("Exception creating new I/O streams: "+e);
		return false;
	}
	//creating thread to listen from serer
	
	new ListenFromServer().start();
	//Send the username to server, this is the only one which will be as a string
	try{
		out.writeObject(username);
	}
	catch(IOException e) {
		display("Exception while login: "+e);
		disconnect();
		return false;
	}
	return true;
	}
	private void display(String msg) {
		if(clientGUI == null) {
			System.out.println(msg);
		}
		else{
			clientGUI.append(msg + "\n"); // append to TextArea
		}
	}
	
	void sendMessage(ChatMessage msg) {
		try {
			out.writeObject(msg);
		}
		catch(IOException e) {
			display("Exception while writing: "+e);
		}
	}
	//If something goes wrong disconnect
	private void disconnect() {
		try {
			if(in != null) {
				in.close();
			}
		}
			catch(Exception e) {
				e.printStackTrace();
			}
		try {
			if(out != null) {
				out.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		try {
			if(socket != null) {
				socket.close();
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		if(clientGUI != null) {
			clientGUI.connectionFailed();
		}
	}
	
	public static void main(String[] args) {
		
		int port = 51000;
		String host = "localhost";
		String username = "Trying";
		//Allowing usage from cmd
		//depending on number of args provided
		switch(args.length) {
		case 3:
			host = args[2];
		case 2:
			try {
				port = Integer.parseInt(args[1]);
			}
			catch(Exception e) {
				System.out.println("Invalid port number");
				return;
			}
		case 1:
			username = args[0];
		case  0:
			break;
			
		default:
			System.out.println("Usage is > java Client [username] [port] [host]");
			return;
		}
		Client client = new Client(host, port, username);
		
		if(!client.start()) {
			return;
		}
		Scanner scan = new Scanner(System.in);
		while(true) {
			System.out.println("> "); // read msg from user
			String msg = scan.nextLine();
			if(msg.equalsIgnoreCase("OFF")) {
				client.sendMessage(new ChatMessage(ChatMessage.OFF, ""));
				break; //for disconnect
			}
			else if(msg.equalsIgnoreCase("WHO")) {
				client.sendMessage(new ChatMessage(ChatMessage.WHO,""));
			}
			else {
				client.sendMessage(new ChatMessage(ChatMessage.MSG, msg));
			}
		}
		client.disconnect();
	}
	//This class will wait for message from server and will add it to TextArea
	class ListenFromServer extends Thread{
		@Override
		public void run() {
			while(true) {
				try {
					String msg = (String) in.readObject();
					if(clientGUI == null) {
						System.out.println(msg);
						System.out.print("> ");
					}
					else {
						clientGUI.append(msg);
					}
				}
				catch(IOException e) {
					display("Server has close the connection: " + e);
					if(clientGUI != null) {
						clientGUI.connectionFailed();
					}
					break;
				}
				catch(ClassNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
