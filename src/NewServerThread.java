import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * Initially created by Roman Gaev
 * 26.02.2018
 * Server thread for every single client
 * <p>
 * May the force be with you.
 */
public class NewServerThread extends Thread {
    private ServerModel server;
    private Socket client;
    private Statement statement;
    private PrintWriter out;
    private BufferedReader in;
    private User currentUser;

    //constructor with database connection and client's socket
    public NewServerThread(ServerModel server, Socket client, Statement statement) throws IOException {
        super("NewServerThread");
        this.server = server;
        this.client = client;
        this.statement = statement;
        out = new PrintWriter(client.getOutputStream(), true);
        in = new BufferedReader(new InputStreamReader(client.getInputStream()));

    }


    //communication of the thread with one particular client
    public void run() {
        System.out.println("new thread");
        try {
            //creating protocol for communication
            Protocol protocol = new Protocol(this);

            //communication with the client using the protocol
            String userRequest;

            while ((userRequest = in.readLine()) != null) {
                if (userRequest.equals(Protocol.EXIT + "")) {
                    System.out.println("server got exit");
                    logoff();
                    break;
                }
                protocol.processInput(userRequest);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                client.close();
                in.close();
                out.close();
            } catch (IOException io) {
                System.err.println("Couldn't close server socket" +
                        io.getMessage());
            }
        }
    }


    public void sentMessage(String msg) {
        List<NewServerThread> pool = server.getThreadPool();
        pool.forEach(x-> {
            User otherUser = x.getCurrentUser();
                //if(otherUser!=null&&!otherUser.getLogin().equals(currentUser.getLogin())) x.out.println(Protocol.MESSAGE +" "+currentUser.getLogin()+" "+ msg);
                if(!x.equals(this)&&otherUser!=null)x.out.println(Protocol.MESSAGE +" "+currentUser.getLogin()+" "+ msg);
        });
    }


    public void register(String username, String password, String legalname) {
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
            int nextId = rs.getInt(1) + 1;
            statement.executeUpdate("INSERT INTO users VALUES ('" + nextId + "','" + username + "','" + password + "','" + legalname + "')");
            out.println(Protocol.TRUE);
        } catch (SQLException e) {
            e.printStackTrace();
            out.println(Protocol.FALSE);
        }
    }

    public void login(String username, String password) {
        try {
            statement.executeQuery("SELECT\n" +
                    "    table_schema || '.' || table_name\n" +
                    "FROM\n" +
                    "    information_schema.tables\n" +
                    "WHERE\n" +
                    "    table_type = 'BASE TABLE'\n" +
                    "AND\n" +
                    "    table_schema NOT IN ('pg_catalog', 'information_schema');");

            ResultSet rs = statement.executeQuery("SELECT username, password,name FROM users WHERE username = '" + username + "'");
            if (rs.next()) {
                if (rs.getString(1).equals(username) && rs.getString(2).equals(password)) {
                    currentUser = new User(rs.getString(1), rs.getString(2), rs.getString(3));
                    out.println(Protocol.TRUE);

                    ArrayList<NewServerThread> pool = server.getThreadPool();
                    // send current user all other online logins
                    pool.forEach(x -> {
                                User threadUser = x.getCurrentUser();
                                if (threadUser != null && !threadUser.getLogin().equals(getCurrentUser().getLogin()))
                                    out.println(Protocol.ONLINE + " " + threadUser.getLogin());
                            }
                    );

                    // send other online users current user's status
                    String login = getCurrentUser().getLogin();
                    pool.forEach(x -> {
                                if (x.getCurrentUser() != null && !login.equals(x.getCurrentUser().getLogin()))
                                    x.getOut().println(Protocol.ONLINE + " " + login);
                            }
                    );
                }
            } else {
                out.println(Protocol.FALSE);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            out.println(Protocol.FALSE);
        }

    }

    private void logoff() throws IOException {
        System.out.println("logoff");
        server.getThreadPool().remove(this);
        ArrayList<NewServerThread> pool = server.getThreadPool();
        pool.forEach(x -> {
                    User threadUser = x.getCurrentUser();
                    if (threadUser != null)
                        x.out.println(Protocol.OFFLINE + " " + currentUser.getLogin());
                }
        );
    }


    public void setCurrentUser(User currentUser) {
        this.currentUser = currentUser;
    }

    public PrintWriter getOut() {
        return out;
    }

    public User getCurrentUser() {
        return currentUser;
    }


}
