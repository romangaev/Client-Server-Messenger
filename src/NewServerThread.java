import java.io.*;
import java.net.Socket;
import java.sql.*;
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
    public Socket client;
    private Connection connection;
    private Statement statement;
    private User currentUser;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    //constructor with database connection and client's socket
    public NewServerThread(ServerModel server, Socket client, Connection connection) throws IOException, SQLException {
        super("NewServerThread");
        this.server = server;
        this.client = client;
        this.connection= connection;
        this.statement = connection.createStatement();
        oos = new ObjectOutputStream(client.getOutputStream());
        ois = new ObjectInputStream(client.getInputStream());

    }


    //communication of the thread with one particular client
    public void run() {
        System.out.println("new thread");
        try {
            //creating protocol for communication
            Protocol protocol = new Protocol(this);

            //communication with the client using the protocol
            Message userMessage;

            while ((userMessage =(Message) ois.readObject()) != null) {
                if (userMessage.getCommand()==Protocol.EXIT) {
                    System.out.println("server got exit");
                    oos.writeObject(new Message(Protocol.EXIT));
                    logoff();
                    break;
                }
                protocol.processInput(userMessage);
            }


        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                ois.close();
                oos.close();
                client.close();
            } catch (IOException io) {
                System.err.println("Couldn't close server socket" +
                        io.getMessage());
            }
        }
    }

    //Sending message as an object and storing it in the database
    public void sendMessage(Message message) {
        try {
            String from = message.getContent()[0];
            int to = Integer.valueOf(message.getContent()[1]);
            String content = message.getContent()[2];

            //Sending message to everyone in corresponding group
            executePreStatement();
            ResultSet rs = statement.executeQuery("SELECT username FROM groups where id="+to);
            while(rs.next()){
                String userToSend=rs.getString(1);
                List<NewServerThread> pool = server.getThreadPool();
                pool.forEach(x -> {
                    String otherUser = x.getCurrentUser().getLogin();
                    //   if(otherUser!=null&&!otherUser.getLogin().equals(currentUser.getLogin())) x.out.println(Protocol.MESSAGE +" "+currentUser.getLogin()+" "+ msg);
                    if (otherUser.equals(userToSend)) {
                        try {
                            x.getOut().writeObject(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

            //Inserting to the database
            executePreStatement();
            ResultSet rs1 = statement.executeQuery("SELECT MAX(id) FROM users");
            rs1.next();
            int nextId = rs1.getInt(1) + 1;
            PreparedStatement ps = connection.prepareStatement("INSERT INTO messages VALUES (?, ?, ?, ?, ?)");
            ps.setInt(1,nextId);
            ps.setInt(2,to);
            ps.setString(3,from);
            ps.setString(4,content);
            ps.setTimestamp(5,new Timestamp(System.currentTimeMillis()));
            File file = message.getFile();
            if(file!=null){
                ps.setBytes(6, ObjectConverter.getByteArrayObject(file));
            }
            ps.executeUpdate();
            ps.close();
        }catch (Exception e){e.printStackTrace();}
    }


    public void register(String username, String password, String legalname) throws IOException {
        try {
            //Creating private conversation group with every user in the database
            executePreStatement();
            ResultSet rs1 = statement.executeQuery("SELECT * FROM users");
            while(rs1.next()) {
                ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM groups");
                rs.next();
                int nextGroupId = rs.getInt(1) + 1;
                statement.executeUpdate("INSERT INTO groups VALUES ('" + nextGroupId + "','private','" + rs1.getString(2) + "')");
                statement.executeUpdate("INSERT INTO groups VALUES ('" + nextGroupId + "','private','" + username + "')");
            }

            //Creating user in users table
            ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM users");
            rs.next();
            int nextId = rs.getInt(1) + 1;
            statement.executeUpdate("INSERT INTO users VALUES ('" + nextId + "','" + username + "','" + password + "','" + legalname + "')");

            oos.writeObject(new Message(Protocol.TRUE));
        } catch (SQLException e) {
            e.printStackTrace();
                oos.writeObject(new Message(Protocol.FALSE));
        }
    }



    public void login(String username, String password) throws IOException {
        try {
            //checking user existence and password
            executePreStatement();
            ResultSet rs = statement.executeQuery("SELECT username, password,name FROM users WHERE username = '" + username + "'");
                if (rs.next()&&rs.getString(1).equals(username) && rs.getString(2).equals(password)) {
                    currentUser = new User(rs.getString(1), rs.getString(2), rs.getString(3));
                    oos.writeObject(new Message(Protocol.TRUE));

                    ArrayList<NewServerThread> pool = server.getThreadPool();
                    // send current user all other online logins
                    pool.forEach(x -> {
                                User threadUser = x.getCurrentUser();
                                if (threadUser != null && !threadUser.getLogin().equals(getCurrentUser().getLogin())) {
                                    try {
                                        oos.writeObject(new Message(Protocol.ONLINE,new String[]{threadUser.getLogin()}));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    );

                    // send other online users current user's status
                    String login = getCurrentUser().getLogin();
                    pool.forEach(x -> {
                                if (x.getCurrentUser() != null && !login.equals(x.getCurrentUser().getLogin())) {
                                    try {
                                        x.getOut().writeObject(new Message(Protocol.ONLINE,new String[]{login}));
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                    );
                } else{
                    oos.writeObject(new Message(Protocol.FALSE));
                }
        } catch (SQLException e) {
            e.printStackTrace();
            oos.writeObject(new Message(Protocol.FALSE));
        }

    }


    private void logoff() throws IOException {
        System.out.println("logoff");
        server.getThreadPool().remove(this);
        ArrayList<NewServerThread> pool = server.getThreadPool();
        pool.forEach(x -> {
                    User threadUser = x.getCurrentUser();
                    if (threadUser != null) {
                        try {
                            x.getOut().writeObject(new Message(Protocol.OFFLINE,new String[]{currentUser.getLogin()}));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
        );
    }


    public ObjectOutputStream getOut() {
        return oos;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void executePreStatement() throws SQLException {
        statement.executeQuery("SELECT\n" +
                "    table_schema || '.' || table_name\n" +
                "FROM\n" +
                "    information_schema.tables\n" +
                "WHERE\n" +
                "    table_type = 'BASE TABLE'\n" +
                "AND\n" +
                "    table_schema NOT IN ('pg_catalog', 'information_schema');");
    }


}
