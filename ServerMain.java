import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
/**
 * ServerMain class in which everything will happen
 * The server will be opened and new threads will be started until the maxThread number
 * No new threads will be allowed when the max number is reached
 * @author Ali Oztas
 *
 */

public class ServerMain {

	private static ServerSocket serverSocket = null;
	private static Socket clientSocket = null;
	private static final int maxThread = 10;
	private static List<ClientThread> threads = Collections.synchronizedList(new ArrayList<ClientThread>());
	private static LoginPage logPage;
	
	public static void main(String[] args) {
		// This part allows the usage of cmd line or just default vals
		int port = 52000;
		if (args.length < 1) {
			System.out.println("Usage java <portNumber\n" + "Now use port " + port);
		} else {
			port = Integer.valueOf(args[0]).intValue();
		}

		try {
			serverSocket = new ServerSocket(port);
		} catch (IOException e) {
			e.printStackTrace();
		}

			
		//Acceptance of threads and creation of them are done here
		// second if is when we reach the max number no new threads
		while (true) {
			try {
				clientSocket = serverSocket.accept();
				int i;
				for (i = 0; i < maxThread; i++) {
					if (threads.get(i) == null) {
						threads.set(i, new ClientThread(clientSocket, threads));
						threads.get(i).start();
						logPage.loginPage();
					}
				}
				if (i == maxThread) {
					PrintStream outputStream = new PrintStream(clientSocket.getOutputStream());
					outputStream.println("Server too busy");
					outputStream.close();
					clientSocket.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}

	}
}
