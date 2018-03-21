import java.io.*;
import javax.swing.*;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;


/**
 * @author Roman, Ali, Maurice, Nabeel, Ioana
 *
 *  Server thread for every single client
 */
public class NewServerThread extends Thread {
    private ServerModel server;
    public Socket client;
    private Statement statement;
    private User currentUser;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    // Constructor with database connection and client's socket
    public NewServerThread(ServerModel server, Socket client, Statement statement) throws IOException {
        super("NewServerThread");
        this.server = server;
        this.client = client;
        this.statement = statement;
        oos = new ObjectOutputStream(client.getOutputStream());
        ois = new ObjectInputStream(client.getInputStream());

    }

    // Using a Hash Set to get the usernames from the database
    // In order to crosscheck if a new user tries to register with an existing username
    public HashSet<String> getUsersFromDB() {

        HashSet<String> allUsers = new HashSet<String>();

        try {
            String getUsers = "SELECT username FROM users;";
            ResultSet rs = statement.executeQuery(getUsers);
            while (rs.next()) {
                allUsers.add(rs.getString("username"));
            }

        } catch (SQLException e) {
            e.printStackTrace();

        }


        return allUsers;
    }

    //communication of the thread with one particular client
    public void run() {
        System.out.println("new thread");
        try {
            //creating protocol for communication
            Protocol protocol = new Protocol(this);

            //communication with the client using the protocol
            Message userMessage;

            while ((userMessage = (Message) ois.readObject()) != null) {
                if (userMessage.getCommand() == Protocol.EXIT) {
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
            }

            // Does not work, there is a EOFException thrown
            catch (EOFException eof) {
                eof.printStackTrace();
            } catch (IOException io) {
                System.err.println("Couldn't close server socket" +
                        io.getMessage());
            }
        }
    }


    public void sendMessage(Message message) {
        try {
            List<NewServerThread> pool = server.getThreadPool();
            pool.forEach(x -> {
                User otherUser = x.getCurrentUser();
                //   if(otherUser!=null&&!otherUser.getLogin().equals(currentUser.getLogin())) x.out.println(Protocol.MESSAGE +" "+currentUser.getLogin()+" "+ msg);
                if (!x.equals(this) && otherUser != null) {
                    try {
                        x.getOut().writeObject(message);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Array to check if every single character of the username is a letter
    public boolean checkForLetter(char[] charArray) {

        for (char c : charArray) {
            if (!Character.isAlphabetic(c)) {
                return false;
            }
        }
        return true;
    }

    public void register(String username, String password, String legalname) throws IOException {

        // Creating the hashset
        HashSet<String> allUsers = getUsersFromDB();

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

            //Checking if the user who tries to sign up, picks a username different than the usernames in the database
            if (allUsers.contains(username)) {
                JOptionPane.showMessageDialog(new JFrame(), "Username already exists!", "Error",
                        JOptionPane.WARNING_MESSAGE);
                oos.writeObject(new Message(Protocol.FALSE));
            } else {
                statement.executeUpdate("INSERT INTO users VALUES ('" + nextId + "','" + username + "','" + password + "','" + legalname + "')");
                oos.writeObject(new Message(Protocol.TRUE));
            }
        } catch (SQLException e) {
            e.printStackTrace();
            oos.writeObject(new Message(Protocol.FALSE));
        }

    }


    // Checking if user is online
//    public int getOnline(String username){
//        int stat = 0;
//        try{
//
//            String getStat = "SELECT online FROM users WHERE username="+username+";";
//            ResultSet rs =  statement.executeQuery(getStat);
//            if(rs.next()){
//                stat = rs.getInt("online");
//            }
//        }
//        catch (SQLException e) {
//            e.printStackTrace();
//        }
//    }


    public void login(String username, String password) throws IOException {
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
            if (rs.next() && rs.getString(1).equals(username) && rs.getString(2).equals(password)) {
                currentUser = new User(rs.getString(1), rs.getString(2), rs.getString(3));
                oos.writeObject(new Message(Protocol.TRUE));

                ArrayList<NewServerThread> pool = server.getThreadPool();
                // send current user all other online logins
                pool.forEach(x -> {
                            User threadUser = x.getCurrentUser();
                            if (threadUser != null && !threadUser.getLogin().equals(getCurrentUser().getLogin())) {
                                try {
                                    oos.writeObject(new Message(Protocol.ONLINE, new String[]{threadUser.getLogin()}));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );

                // Send other online users current user's status
                String login = getCurrentUser().getLogin();
                pool.forEach(x -> {
                            if (x.getCurrentUser() != null && !login.equals(x.getCurrentUser().getLogin())) {
                                try {
                                    x.getOut().writeObject(new Message(Protocol.ONLINE, new String[]{login}));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            } else {
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
                            x.getOut().writeObject(new Message(Protocol.OFFLINE, new String[]{currentUser.getLogin()}));
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


}