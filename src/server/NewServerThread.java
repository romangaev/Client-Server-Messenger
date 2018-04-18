package server;

import supplementary.*;

import java.io.*;

import java.net.Socket;
import java.sql.*;
import java.util.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

/**
 * @author Roman, Ali, Maurice, Nabeel, Ioana
 * <p>
 * server.NewServerThread class handles operations with one particular client using supplementary.Protocol class
 * <p>
 * version 19.04.2018
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
        super("server.NewServerThread");
        this.server = server;
        this.client = client;
        this.connection = connection;
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
            System.out.println("server.NewServerThread: Exception in reading client's requests");
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

    //Sending message as an object and storing it in the database
    public void sendMessage(Message message) {
        try {
            String from = message.getContent()[0];
            int to = Integer.valueOf(message.getContent()[1]);
            String content = message.getContent()[2];

            //Sending message to everyone in corresponding group
            executePreStatement();
            ResultSet rs = statement.executeQuery("SELECT username FROM groups where group_id=" + to);
            while (rs.next()) {
                String userToSend = rs.getString(1);
                if (!userToSend.equals(currentUser.getLogin())) {
                    Set<NewServerThread> pool = server.getThreadPool();
                    pool.forEach(x -> {
                        String otherUser = x.getCurrentUser().getLogin();
                        //   if(otherUser!=null&&!otherUser.getLogin().equals(currentUser.getLogin())) x.out.println(supplementary.Protocol.MESSAGE +" "+currentUser.getLogin()+" "+ msg);
                        if (otherUser.equals(userToSend)) {
                            try {
                                x.getOut().writeObject(message);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
            }

            //Inserting to the database
            executePreStatement();
            ResultSet rs1 = statement.executeQuery("SELECT MAX(id) FROM messages");
            rs1.next();
            int nextId = rs1.getInt(1) + 1;
            PreparedStatement ps = connection.prepareStatement("INSERT INTO messages VALUES (?, ?, ?, ?, ?, ?)");
            ps.setInt(1, nextId);
            ps.setInt(2, to);
            ps.setString(3, from);
            ps.setString(4, content);
            ps.setTimestamp(5, new Timestamp(System.currentTimeMillis()));
            File file = message.getFile();
            if (file != null) {
                ps.setBytes(6, ObjectConverter.getByteArrayObject(file));
            } else ps.setBytes(6, null);
            ps.executeUpdate();
            ps.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void register(String username, String password, String legalName) throws IOException {
        // Creating the hashset
        try {
            //Creating private conversation group with every user in the database
            executePreStatement();
            ResultSet rscheck = statement.executeQuery("SELECT * FROM users WHERE username='" + username + "'");
            if (rscheck.next()) throw new IllegalArgumentException();
            oos.writeObject(new Message(Protocol.TRUE));
            ResultSet rsOuter = statement.executeQuery("SELECT * FROM users");
            while (rsOuter.next()) {
                Statement statement2 = connection.createStatement();
                ResultSet rsInner = statement2.executeQuery("SELECT MAX(group_id) FROM groups");
                rsInner.next();
                int nextGroupId = rsInner.getInt(1) + 1;
                String otherUser = rsOuter.getString(2);
                statement2.executeUpdate("INSERT INTO groups VALUES (" + nextGroupId + ",'private','" + otherUser + "')");
                statement2.executeUpdate("INSERT INTO groups VALUES (" + nextGroupId + ",'private','" + username + "')");

                //send all online users info about new user
                Set<NewServerThread> pool = server.getThreadPool();
                pool.forEach(x -> {
                            if (x.getCurrentUser() != null && x.getCurrentUser().getLogin().equals(otherUser)) {
                                try {
                                    x.getOut().writeObject(new Message(Protocol.REGISTER, new String[]{String.valueOf(nextGroupId), username}));
                                    x.getOut().writeObject(new Conversation("private", new ArrayList<String>(Arrays.asList(username, otherUser))));
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                );
            }

            //Creating user in users table
            ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM users");
            rs.next();
            int nextId = rs.getInt(1) + 1;
            statement.executeUpdate("INSERT INTO users VALUES (" + nextId + ",'" + username + "','" + password + "','" + legalName + "')");

        } catch (SQLException e) {
            e.printStackTrace();
            oos.writeObject(new Message(Protocol.FALSE));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
            System.out.println("Username already exists!");
            oos.writeObject(new Message(Protocol.FALSE));
        }

    }

    public void login(String username, String password) throws IOException {
        try {
            //checking user existence and password
            executePreStatement();
            ResultSet rs = statement.executeQuery("SELECT username, password,name FROM users WHERE username = '" + username + "'");
            if (rs.next() && rs.getString(1).equals(username) && rs.getString(2).equals(password)) {
                currentUser = new User(rs.getString(1), rs.getString(2), rs.getString(3));
                oos.writeObject(new Message(Protocol.TRUE));

                // send to user conversations information
                //creating a MAP where we will store conversation info and send it to client to show as contact list
                HashMap<Integer, Conversation> conversations = new HashMap<>();
                ResultSet ids = statement.executeQuery("SELECT group_id FROM groups WHERE username='" + username + "'");
                while (ids.next()) {
                    Statement statement2 = connection.createStatement();
                    ResultSet rs2 = statement2.executeQuery("SELECT * FROM groups WHERE group_id=" + ids.getInt(1));
                    while (rs2.next()) {
                        int id = rs2.getInt(1);
                        String user = rs2.getString(3);
                        String groupName = rs2.getString(2);

                        if (!conversations.containsKey(id))
                            conversations.put(id, new Conversation(groupName, user));
                        else conversations.get(id).getParticipants().add(user);
                    }
                }
                oos.writeObject(conversations);

                // send current user all other online logins
                Set<NewServerThread> pool = server.getThreadPool();
                pool.forEach(x -> {
                    User threadUser = x.getCurrentUser();
                    if (threadUser != null && !threadUser.getLogin().equals(getCurrentUser().getLogin())) {
                        try {
                            oos.writeObject(new Message(Protocol.ONLINE, new String[]{threadUser.getLogin()}));
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

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
        Set<NewServerThread> pool = server.getThreadPool();
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

    //executePreStatement - method for SQL prestatement because of database wild desires
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

    public void sendHistory(String s) {
        try {
            executePreStatement();
            ResultSet rs = statement.executeQuery("SELECT * FROM messages WHERE group_id = " + s + " ORDER BY timestamp ASC");
            ArrayList<String> messages = new ArrayList<>();
            while (rs.next()) {
                messages.add(rs.getString(3) + ": " + rs.getString(4));
            }
            oos.writeObject(new Message(Protocol.HISTORY, new String[]{s}));
            oos.writeObject(messages);
        } catch (SQLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void createGroup() {
        try {
            Conversation conversation = (Conversation) ois.readObject();
            String name = conversation.getName();
            executePreStatement();
            ResultSet rs = statement.executeQuery("SELECT MAX(group_id) FROM groups");
            rs.next();
            int nextGroupId = rs.getInt(1) + 1;
            conversation.getParticipants().forEach(x ->
                    {
                        try {
                            statement.executeUpdate("INSERT INTO groups VALUES (" + nextGroupId + ",'" + name + "','" + x + "')");
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
            );

            server.getThreadPool().forEach(x -> {
                        if (x.getCurrentUser() != null && conversation.getParticipants().contains(x.getCurrentUser().getLogin())) {
                            try {
                                x.getOut().writeObject(new Message(Protocol.CREATE_GROUP));
                                x.getOut().writeObject(nextGroupId);
                                x.getOut().writeObject(conversation);
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    }
            );

        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void leaveGroup(Message message) {
        try {
            String login = message.getContent()[0];
            int id = Integer.valueOf(message.getContent()[1]);
            executePreStatement();
            ResultSet rs = statement.executeQuery("SELECT username FROM groups where group_id=" + id);
            while (rs.next()) {
                String userToSend = rs.getString(1);
                Set<NewServerThread> pool = server.getThreadPool();
                pool.forEach(x -> {
                    String otherUser = x.getCurrentUser().getLogin();
                    //   if(otherUser!=null&&!otherUser.getLogin().equals(currentUser.getLogin())) x.out.println(supplementary.Protocol.MESSAGE +" "+currentUser.getLogin()+" "+ msg);
                    if (otherUser.equals(userToSend)) {
                        try {
                            x.getOut().writeObject(message);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            statement.executeUpdate("DELETE FROM groups WHERE group_id=" + id + " AND username='" + login + "'");
            System.out.println("leaveGroup");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
