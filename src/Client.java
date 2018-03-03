import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
/**
 * 
 * @author Ali Oztas
 *
 */
public class Client implements Runnable {

	// cl soc
	private static Socket clientSocket = null;
	private static PrintStream outputStream = null;
	private static DataInputStream inputStream = null;
	private static BufferedReader inputLine = null;
	private static boolean closed = false;

	public static void main(String[] args) {

		
		int port = 52000;
		String host = "localhost";
		// IF THERE ARE NO INPUTS default vals will be used, if there are they will be
		// set.
		if (args.length < 2) {
			System.out.println("Usage <host> <portNumber>\n" + "Now using host = " + host + ", portNumber = " + port);
		} else {
			host = args[0];
			port = Integer.valueOf(args[1]).intValue();
		}

		try {
			clientSocket = new Socket(host, port);
			inputLine = new BufferedReader(new InputStreamReader(System.in));
			outputStream = new PrintStream(clientSocket.getOutputStream());
			inputStream = new DataInputStream(clientSocket.getInputStream());
			
		} catch (UnknownHostException e) {
			System.err.println("Dunno host");
		} catch (IOException e) {
			System.err.println("I/O error");
		}

		// If everything is ok now write data

		if (clientSocket != null && outputStream != null && inputStream != null) {
			try {
				new Thread(new Client()).start();

				while (!closed) {
					outputStream.println(inputLine.readLine());
				}
				// Closing everything
				outputStream.close();
				inputStream.close();
				clientSocket.close();
			} catch (IOException e) {
				System.err.println("I/O ");
			}
		}

	}

	@Override
	public void run() {

		// Reading socket until a certain line is received WILL BE CHANGED IN GUI

		String command;
		try {
			while ((command = inputLine.readLine()) != null) {

				System.out.println(command);
				if (command.indexOf("*** quit") != -1) {
					break;
				}
			}
			closed = true;
		} catch (IOException e) {
			System.err.println("I/O");
		}
	}
}
