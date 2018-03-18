import java.io.*;
import javax.swing.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;


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
    private Statement statement;
    private User currentUser;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;

    //constructor with database connection and client's socket
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
//            oos.writeObject(new Message(Protocol.FALSE));
            e.printStackTrace();

        }


        return allUsers;
    }

    public boolean checkPassword(String password) {

        if(!(password.length() > 5 && password.length() < 13)) {
            JOptionPane.showMessageDialog(new JFrame(), "Password should be between 6-12 characters.", "Password Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        char[] pw = password.toCharArray();
        int numCount = 0;
        int alphCount = 0;
        for(char c : pw) {
            if(Character.isAlphabetic(c)) {
                alphCount++;
            }
            if(Character.isDigit(c)) {
                numCount++;
            }
            if(!Character.isLetterOrDigit(c)) {
                JOptionPane.showMessageDialog(new JFrame(), "Password should not include any special characters", "Password Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        if(!(alphCount >= 1 && numCount >= 1)) {
            JOptionPane.showMessageDialog(new JFrame(), "Your password should include at least 1 numeric and 1 alphabetic letter.", "Password Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
    return true;

    }

    // Method for checking restrictions in name
    public boolean checkName(String name) {

        // The name should be 2-40 characters long
        if(!(name.length() < 40 && name.length() > 2)) {
            JOptionPane.showMessageDialog(new JFrame(), "Name should be between 2-40 characters long.", "Name Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // The name should not contain numbers
        char[] nameArray = name.toCharArray();
        for(char c : nameArray) {
            if(Character.isDigit(c)) {
                JOptionPane.showMessageDialog(new JFrame(), "Name should not include any numbers.", "Name Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // The name should not contain any special characters, whitespace is allowed
            if(!Character.isWhitespace(c)){
                if(!Character.isLetterOrDigit(c)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Name should not include any special characters", "Name Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        }
        return true;
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

            // does not work, there is a EOFException thrown
            catch(EOFException eof){
                eof.printStackTrace();
            }
            catch (IOException io) {
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

//        do {
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

                char[] userChars = username.toCharArray();

                // Throwing a pane, warning the user that the username should be at least 5-10 characters long
                if (!checkForLetter(userChars) || username.length() < 5 || username.length() > 11) {
                    JOptionPane.showMessageDialog(new JFrame(), "Your username should not be empty. \nThe length should be 5-10 characters " +
                            "long. \nYour username should only contain letters!", "Username Error", JOptionPane.WARNING_MESSAGE);
                    oos.writeObject(new Message(Protocol.FALSE));

                    //Checking if the user who tries to sign up, picks a username different than the usernames in the database
                } else if (allUsers.contains(username)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Username already exists!", "Error",
                            JOptionPane.WARNING_MESSAGE);
                    oos.writeObject(new Message(Protocol.FALSE));

                } else if(!checkPassword(password) || !checkName(legalname)) {
                    oos.writeObject(new Message(Protocol.FALSE));

                } else {
                    statement.executeUpdate("INSERT INTO users VALUES ('" + nextId + "','" + username + "','" + password + "','" + legalname + "')");
                    oos.writeObject(new Message(Protocol.TRUE));
                }
            } catch (SQLException e) {
                e.printStackTrace();
                oos.writeObject(new Message(Protocol.FALSE));
            }

//        } while ();
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

                // send other online users current user's status
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