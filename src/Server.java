package trying;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;


public class Server {

	private static int uniqueID;
	private List<ClientThread> clients; // list of clients
	private ServerGUI serverGUI;
	public SimpleDateFormat simpleDateFormat;
	private int port;
	private boolean cont;

	public Server(int port) {
		this(port, null);	
	}
	public Server(int port, ServerGUI sg) {
		//GUI or no
		this.serverGUI = sg;
		this.port = port;
		simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
		clients = Collections.synchronizedList(new ArrayList<ClientThread>());
				
	}
	public void start() {
		cont = true;
		//Creating server and waiting for connections
		
		try {
			ServerSocket serverSocket = new ServerSocket(port);
			while(cont) {
				display("Waiting on port "+ port +".");
				
				Socket socket = serverSocket.accept();
				
				if(!cont) {
					break;
				}
				ClientThread client = new ClientThread(socket); // make a thread of this socket
				clients.add(client);
				
				client.start();
			}
		//Stop
			try {
			serverSocket.close();
			for(int i = 0; i < clients.size(); i++) {
				ClientThread client = clients.get(i);
				try {
					client.in.close();
					client.out.close();
					client.socket.close();
				}
				catch(IOException e) {
					e.printStackTrace();		
	}
	}
	}
			catch(Exception e) {
				display("Exception closing the server: "+e);
			}
	}
		catch(IOException e) {
			String msg = simpleDateFormat.format(new Date()) + " Exception on socket " + e + "\n";
			display(msg);
		}
	}
		//GUI to stop the server
		protected void stop() {
			cont = false;
			try {
				new Socket("localhost", port);
			}
			catch(Exception e) {
				e.printStackTrace();
			}
		}
		/*
		 * Displaying an event to GUI
		 */
		public void display(String msg) {
			String message = simpleDateFormat.format(new Date()) + " " + msg;
			if(serverGUI == null) {
				System.out.println(message);
			}
			else {
				serverGUI.appendEvent(message + "\n");
			}
		}
		
		public synchronized void broadcast (String message) {
			// adding date format to messages
			String time = simpleDateFormat.format(new Date());
			String messageWithTime = time + " " +message + "\n";
			
			if(serverGUI == null) {
				System.out.println(messageWithTime);
			}
			else {
				serverGUI.appendRoom(messageWithTime); // append in room window
			}
			//looping in reverse order in case we have to remove a client because they have disconnected
			
			for(int i = clients.size() -1 ; i == 0; i--) {
				ClientThread client = clients.get(i);
				if(!client.writeMsg(messageWithTime)) {
					clients.remove(i);
					display("Disconnected Client "+ client.username + " removed from the list");
				}
				
			}
		}
		private synchronized void remove(int id) {
			//scan the list
			for(int i = 0; i < clients.size(); i++) {
				ClientThread client = clients.get(i);
				if(client.id == id) {
					clients.remove(i);
					return;
				}
			}
		}
		
		public static void main(String[] args) {
			
			int port = 51000;
			switch(args.length) {
			case 1: 
				try {
					port = Integer.parseInt(args[0]);
				}
				catch(Exception e) {
					System.out.println("Invalid port number");
					return;
				}
			case 0: 
				break;
			default:
				System.out.println("Usage is > java Server [portNum]");
				return;
			}
			Server server = new Server(port);
			server.start();
		}
			
		
		
	class ClientThread extends Thread {
		private  Socket socket;
		private ObjectInputStream in;
		private ObjectOutputStream out;
		private int id;
		private String username;
		private ChatMessage msg;
		private String date;
		
		
		ClientThread(Socket socket){
			
			id = ++uniqueID;
			this.socket = socket;
			System.out.println("Trying to create I/O streams");
			try {
				out = new ObjectOutputStream(socket.getOutputStream());
				in = new ObjectInputStream(socket.getInputStream());
				username = (String) in.readObject();
				display(username + " HAS JUST CONNECTED!!");
			}
			catch(IOException e) {
				display("Exception while creating new I/O streams");
				e.printStackTrace();
				return;
			}
			catch(ClassNotFoundException e) {
				
			}
			date = new Date().toString() + "\n";
		}
	
		
		public void run() {
			// continue until OFF
			boolean cont = true;
			while(cont) {
				
				try {
					msg = (ChatMessage) in.readObject();
				}
				catch(IOException e) {
					display(username + " Exception while reading" + e);
					break;
				}
				catch(ClassNotFoundException e1) {
					break;
				}
				String message = msg.getMessage();
				
				switch(msg.getType()) {
				case ChatMessage.MSG:
					broadcast(username + ": "+ message);
					break;
				
				case ChatMessage.OFF:
					display(username + " disconnected!");
					cont = false;
					break;
					
				case ChatMessage.WHO:
					writeMsg("List of online users are " +simpleDateFormat.format(new Date()) + "\n");
				//scan online users
					//It can be done sth like
					// On a JLIST
					//Add the client.username to the JLIST when the user logs in
					for(int i = 0 ; i < clients.size(); ++i) {
						ClientThread client = clients.get(i);
						writeMsg((i+1) + ") " + client.username + " since "+ client.date);
					}
					break;	
			}	
			}
		remove(id);
		close();
		}

		
	
	private void close() {
		//try to close the connection
		try {
			if(out != null) {
				out.close();
			}
		}
		catch(Exception e) {}
		try {
			if(in != null) {
				in.close();
			}
		}
		catch(Exception e) {}
	try {
		if(socket != null) {
			socket.close();
		}
	}
	catch(Exception e) {}
	}

	private boolean writeMsg(String msg) {
		
		// If Client is coonnected send the message
		
		if(!socket.isConnected()) {
			close();
			return false;
		}
		try {
			out.writeObject(msg);
		}
		catch(IOException e) {
			display("Error while sending message to " + username);
			display(e.toString());
		}
		return true;
	}
		}
}
	
