import java.io.*;
import java.net.Socket;

/**
 * Initially created by Roman Gaev
 * 26.02.2018
 * Simple client model
 * <p>
 * May the force be with you.
 */
public class ClientModel {

    public static void main(String[] args){

        try {
            //connecting to local server and creating output and input writing and reading tools
            Socket serverSocket = new Socket("localhost", 5000);
            PrintWriter out = new PrintWriter(serverSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader( new InputStreamReader(serverSocket.getInputStream()));
            BufferedReader console = new BufferedReader(new InputStreamReader(System.in));

            //reading from server and sending it messages back from the console.
            String serverSays;
            while((serverSays=in.readLine())!=null){
                System.out.println(serverSays);
                if(serverSays.equals("See you in a bit!")) break;
                out.println(console.readLine());
            }



        }catch (IOException e){e.printStackTrace();}


    }
}
