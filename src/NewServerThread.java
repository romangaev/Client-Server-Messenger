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

            //communication with the client using the protocol
            String userRequest;
            int serverResponse;
            while ((userRequest = in.readLine()) != null) {
                serverResponse = protocol.processInput(userRequest);
                out.println(serverResponse);
                if (serverResponse==Protocol.EXIT)
                    break;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            try {
                client.close();
                in.close();
                out.close();
            }
            catch (IOException io) {
                System.err.println("Couldn't close server socket" +
                        io.getMessage());
            }
        }
    }

    public boolean register(String username, String password, String legalname) {
        try {
            statement.executeQuery("SELECT\n" +
                    "    table_schema || '.' || table_name\n" +
                    "FROM\n" +
                    "    information_schema.tables\n" +
                    "WHERE\n" +
                    "    table_type = 'BASE TABLE'\n" +
                    "AND\n" +
                    "    table_schema NOT IN ('pg_catalog', 'information_schema');");
            ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM users");
            rs.next();
            int nextId= rs.getInt(1)+1;
            statement.executeUpdate("INSERT INTO users VALUES ('"+nextId+"','"+username+"','"+password+"','"+legalname+"')");
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean login(String username, String password) {
        try {
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
        } catch (SQLException e) {
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
