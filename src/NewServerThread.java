import java.io.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
/**
 * Initially created by Roman Gaev
 * 26.02.2018
 * Server thread for every single client
 * <p>
 * May the force be with you.
 */
public class NewServerThread extends Thread {
    private Socket client;
    private Statement statement;
    private PrintWriter out;
    private BufferedReader in;
    private User currentUser;

    //constructor with database connection and client's socket
    public NewServerThread(Socket client, Statement statement) throws IOException {
        super("NewServerThread");
        this.client = client;
        this.statement = statement;
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));

    }

    //communication of the thread with one particular client
    public void run() {
        try {
            //creating protocol for communication
            Protocol protocol = new Protocol(this);
            String serverSays = "Greetings, motherfuckers! Welcome to server! 0 to sign in. 1 to sign up. 2 to get a surprise. 3 to quit the program:";
            //communication with the client using the protocol
            out.println(serverSays);
            String userSays;
            while ((userSays = in.readLine()) != null) {
                serverSays = protocol.processInput(userSays);
                out.println(serverSays);
                if (serverSays.equals("See you in a bit!"))
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //sign in of client
    public boolean signIn() {
        String username;
        String password;

        try {
            out.println("Username:");
            username = in.readLine();
            out.println("Password:");
            password = in.readLine();

            statement.executeQuery("SELECT\n" +
                    "    table_schema || '.' || table_name\n" +
                    "FROM\n" +
                    "    information_schema.tables\n" +
                    "WHERE\n" +
                    "    table_type = 'BASE TABLE'\n" +
                    "AND\n" +
                    "    table_schema NOT IN ('pg_catalog', 'information_schema');");
            ResultSet rs = statement.executeQuery("SELECT * FROM users;");

            while (rs.next()) {
                if (rs.getString(2).equals(username) && rs.getString(3).equals(password)) {
                    currentUser = new User(rs.getString(2), rs.getString(3), rs.getString(4));
                    return true;
                }
            }

            return false;
        } catch (IOException e) {
            System.out.println("Something is wrong with IO!");
            e.printStackTrace();
            return false;
        } catch (SQLException e) {
            System.out.println("Something is wrong with SQL query!");
            e.printStackTrace();
            return false;
        }

    }

    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public User getCurrentUser() {
        return currentUser;
    }
}
