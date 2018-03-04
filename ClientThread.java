import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is our new clients which will be initialised
 * They can send private messages with the usage of "@" sign 
 * Usernames can't have a username which will start with "@"
 * 
 * @author Ali Oztas
 *
 */
public class ClientThread extends Thread implements Runnable {

	private String clientName;
	private ObjectInputStream inputStream = null;
	private PrintStream outputStream = null;
	private Socket clientSocket = null;
	private static BufferedReader inputLine = null;
	private int maxThread = 10;
	private List<ClientThread> threads = Collections.synchronizedList(new ArrayList<ClientThread>(maxThread));
	private static LoginPage logPage;

	public ClientThread(Socket clientSocket, List<ClientThread> threads) {
		this.clientSocket = clientSocket;
		this.threads = threads;
		
	}

	public void run() {
		int maxThread = this.maxThread;
		List<ClientThread> threads = this.threads;
		try {
		inputStream = new ObjectInputStream(clientSocket.getInputStream());
		outputStream = new PrintStream(clientSocket.getOutputStream());
		inputLine = new BufferedReader(new InputStreamReader(inputStream));
		
		String name;
		while(true) {
			outputStream.println("Enter your name please");
			
			name = inputLine.readLine().trim();
			// you can have any condition for name here
			if(name.indexOf('@') == -1) {
				break;
			}
			else {
				outputStream.println("@ character will be used by server so don't include it in username");
			}
		}
		// open the gui
	outputStream.print("Welcome " + name + " to the chat room. \n To leave write quit in a new line");
	//just the general message
	synchronized(this) {
		for(int i = 0; i < maxThread; i++) {
			if(threads.get(i) != null && threads.get(i) == this) {
				clientName = "@" + name;
				break;
			}
		}
		for(int i = 0; i < maxThread; i++) {
			if(threads.get(i) != null && threads.get(i) == this) {
				threads.get(i).outputStream.println(name+ " HAS ENTERED THE CHAT ROOM!!");
				
			}
		}
	}
//starting the convo
	while(true) {
		inputLine = new BufferedReader(new InputStreamReader(System.in));
		String line = inputLine.readLine().trim();
		if(line.startsWith("quit")) { break;
	}
		// PRIVATE MESSAGE
		if(line.startsWith("@")) {
			String[] words = line.split("\\s", 2);
			if(words.length > 1 && words[1] != null) {
				words[1] = words[1].trim();
				if(!words[1].isEmpty()) {
					synchronized(this) {
						//finding the user which we'll send the message to
						for(int i = 0; i < maxThread; i++) {
							if(threads.get(i) != null && threads.get(i) != this
							   && threads.get(i).clientName != null
							   && threads.get(i).clientName.equals(words[0])) {
								threads.get(i).outputStream.print("<"+name+"> "+words[1] );
								//msg is sent now let the client know about it
								// like chat history kinda thing
								this.outputStream.println(">"+name + "> "+words[1]);
								break;
							}
						}
					}
				}
			}
		}
			else {
				//IF THE MSG IS PUBLIC
				synchronized(this) {
					for(int i = 0; i < maxThread; i++) {
						if(threads.get(i) != null && threads.get(i).clientName != null) {
							threads.get(i).outputStream.println("<"+name+"> "+line);
						}
					}
				}
			}
	}
			
			//If the user leaves the chat room
			synchronized(this) {
				for(int i = 0; i < maxThread; i++) {
					if(threads.get(i) != null && threads.get(i) != this
							&& threads.get(i).clientName != null) {
						threads.get(i).outputStream.println(name + " is leaving the room");
					}
				}
			}
			outputStream.println("BYE "+name +" have a good one");
			//Cleaning up and setting the current thread to null so new client can be accepted
			
			synchronized(this) {
				for(int i = 0; i < maxThread; i++) {
					if(threads.get(i) == this) {
						threads.set(i, null);
					}
				}
			}
			inputStream.close();
			outputStream.close();
			clientSocket.close();
			
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	
	
	}}
