import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Observable;

/**
 * @author Nabeel, Ali, Roman
 *
 * Simple client model
 */
public class ClientModel {
    private String serverName;
    private int serverPort;
    private Socket serverSocket;
    private MainChatView view;
    private String login;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;
    private Map<Integer,Conversation> allUsers;


    public ClientModel(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;

    }

    public boolean connect() {
        try {
            serverSocket = new Socket(serverName, serverPort);
            oos = new ObjectOutputStream(serverSocket.getOutputStream());
            ois = new ObjectInputStream(serverSocket.getInputStream());
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String login, String password, String legalName) {
        try {
        oos.writeObject(new Message(Protocol.REGISTER, new String[]{login,password,legalName}));

            if (((Message) ois.readObject()).getCommand()==Protocol.TRUE) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }


    public boolean login(String username, String password) {
        try {
            oos.writeObject(new Message(Protocol.LOGIN, new String[]{username,password}));
            if (((Message) ois.readObject()).getCommand()==Protocol.TRUE){
                login = username;
                allUsers= new HashMap<>();
                allUsers=(Map) ois.readObject();
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    public void startReadingThread() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Message userMessage;
                    boolean running=true;
                    while ((running&& (userMessage =(Message) ois.readObject()) != null)) {
                        String[] tokens = userMessage.getContent();
                            switch(userMessage.getCommand()){
                                case Protocol.REGISTER:
                                    if(view!=null){
                                        Conversation c = (Conversation)ois.readObject();
                                        allUsers.put(Integer.valueOf(tokens[0]),c);
                                        view.updateRegister(Integer.valueOf(tokens[0]), tokens[1]);

                                    }
                                    System.out.println("client got info about new user created");
                                    break;
                                case Protocol.ONLINE:
                                    if(view!=null){view.updateOnline(tokens[0]);}
                                    System.out.println("client got online");
                                    break;
                                case Protocol.OFFLINE:
                                    if(view!=null){view.updateOffline(tokens[0]);}
                                    System.out.println("client got offline");
                                    System.out.println("client remove"+tokens[0]);
                                    break;
                                case Protocol.MESSAGE:
                                    System.out.println("client got message");
                                    String loginToPrint=tokens[0];
                                   // if(loginToPrint.equals(login)) loginToPrint="You";
                                    if(view!=null){view.updateMessages(loginToPrint,allUsers.get(Integer.valueOf(tokens[1])).getName(),tokens[2]);}
                                    break;
                                case Protocol.HISTORY:
                                    ArrayList<String> messages =(ArrayList<String>)ois.readObject();
                                    allUsers.get(Integer.valueOf(tokens[0])).getMessages().addAll(messages);
                                    view.updateHistory(messages);
                                    break;
                                case Protocol.CREATE_GROUP:
                                    Integer id =(Integer) ois.readObject();
                                    Conversation conversation = (Conversation) ois.readObject();

                                    allUsers.put(id,conversation);
                                    view.updateGroups(conversation.getName(),id);
                                    break;
                                case Protocol.LEAVE_GROUP:

                                    String deletedLogin = userMessage.getContent()[0];
                                    int groupId = Integer.parseInt(userMessage.getContent()[1]);
                                if(login.equals(deletedLogin)) {
                                    view.deleteGroup(allUsers.get(groupId).getName());
                                    allUsers.remove(groupId);
                                    System.out.println("leaveGroup2");
                                }
                                else
                                    allUsers.get(groupId).getParticipants().remove(deletedLogin);
                                break;
                                case Protocol.EXIT:
                                    running=false;
                                    break;
                                default:
                                    break;
                            }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    System.exit(1);
                }
            }
        }.start();

    }


    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        System.out.println("client finalize");
        oos.writeObject(new Message(Protocol.EXIT));
        try {
            oos.close();
            ois.close();
            serverSocket.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            System.exit(1);
        }
    }


    public void sendMessage(int groupId, String text) {
            try{
            oos.writeObject(new Message(Protocol.MESSAGE,new String[]{login,String.valueOf(groupId), text}));
            allUsers.get(groupId).getMessages().add(login+": "+text);
            }catch (Exception e){e.printStackTrace();}

    }


    public void setView(MainChatView view) {
        this.view=view;
    }

    public String getLogin() {
        return login;
    }

    public ObjectOutputStream getOut() {
        return oos;
    }

    public Map<Integer, Conversation> getAllUsers() {
        return allUsers;
    }

    public void getHistory(int groupId) throws IOException {
        //ArrayList<String> local =allUsers.get(groupId).getMessages();
       // if(allUsers.get(groupId).getMessages().isEmpty())
            oos.writeObject(new Message(Protocol.HISTORY, new String[] {String.valueOf(groupId)}));
        //else view.updateHistory(local);
    }

    public void createGroup(String name, ArrayList<String> participants){

        try {
            oos.writeObject(new Message(Protocol.CREATE_GROUP));
            oos.writeObject(new Conversation(name,participants));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void leaveGroup(int id) {
        try {
            oos.writeObject(new Message(Protocol.LEAVE_GROUP,new String[]{login, String.valueOf(id)}));
            System.out.println("leaveGroup");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
