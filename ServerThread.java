import java.io.IOException;
import java.net.Socket;

public class ServerThread extends Thread{
	
	private Socket socket;

	public ServerThread(Socket clientSocket) {
		this.socket = socket;
	}

	@Override
	public void run() {
		
		
		
	}
	public static void main(String[] args) {
		Socket socket = null;
		int port = 52000;
		
		try {
			socket = new Socket("localhost", port);
			Thread.sleep(1000);
			Thread server = new Thread(new ServerThread(socket));
			server.start();
		}
		catch(IOException e) {
			System.err.println("Fatal conn error");
			e.printStackTrace();
		}
		catch(InterruptedException e) {
			System.err.println("Interruption");
			e.printStackTrace();
		}
	}
	
}
