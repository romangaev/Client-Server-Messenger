import java.io.*;
import java.net.Socket;
import java.util.Observable;

/**
 * @author Nabeel, Ali, Roman
 *
 * Simple client model
 */
public class ClientModel extends Observable {
    private String serverName;
    private int serverPort;
    private Socket serverSocket;
    private MainChatView view;
    private String login;
    private ObjectOutputStream oos;
    private ObjectInputStream ois;

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
                                    if(tokens[0].equals(login)) loginToPrint="You";
                                    if(view!=null){view.updateMessages(loginToPrint+": "+ tokens[1]);}
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
                    try {
                        serverSocket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
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
    }


    public void sendMessage(String text) {
            try{
            oos.writeObject(new Message(Protocol.MESSAGE,new String[]{login,text}));
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
}
