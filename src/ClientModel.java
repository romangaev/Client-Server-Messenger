
import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Observable;
import java.util.TreeSet;

/**
 * Initially created by Roman Gaev
 * 26.02.2018
 * Simple client model
 * <p>
 * May the force be with you.
 */
public class ClientModel extends Observable {
    private String serverName;
    private int serverPort;
    private Socket serverSocket;
    public PrintWriter out;
    private BufferedReader in;
    private MainChatView view;
    private String login;

    public ClientModel(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;

    }

    public boolean connect() {
        try {
            serverSocket = new Socket(serverName, serverPort);
            out = new PrintWriter(serverSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean register(String login, String password, String legalName) {
        String cmd = Protocol.REGISTER + " " + login + " " + password + " " + legalName;
        out.println(cmd);
        try {
            if (Integer.valueOf(in.readLine())==Protocol.TRUE) {
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;

    }
    public boolean login(String username, String password) {
        String cmd = Protocol.LOGIN + " " + username + " " + password;
        out.println(cmd);
        try {
            if (Integer.valueOf(in.readLine())==Protocol.TRUE){
                login = username;
                return true;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }


    public void startReadingThread() {
        new Thread() {
            @Override
            public void run() {
                try {
                    String line;
                    while ((line = in.readLine()) != null) {
                        String[] tokens = line.split(" ");
                        if (tokens != null && tokens.length > 0) {
                            int cmd = Integer.valueOf(tokens[0]);
                            switch(cmd){
                                case Protocol.ONLINE:
                                    if(view!=null){view.updateOnline(tokens[1]);}
                                    System.out.println("client got online");
                                    break;
                                case Protocol.OFFLINE:
                                    if(view!=null){view.updateOffline(tokens[1]);}
                                    System.out.println("client got offline");
                                    System.out.println("client remove"+tokens[1]);
                                    break;
                                case Protocol.MESSAGE:
                                    System.out.println("client got message");
                                    String[] msgtokens = line.split(" ",3);

                                    String loginToPring=msgtokens[1];
                                    if(msgtokens[1].equals(login)) loginToPring="You";

                                    if(view!=null){view.updateMessages(loginToPring+": "+ msgtokens[2]);}
                                    break;
                                default:
                                    break;
                            }

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
        out.println(Protocol.EXIT+"");
        try {
            out.close();
            in.close();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void sendMessage(String text) {
        if(!text.equals("")&&!text.equals(" ")){
            String cmd = Protocol.MESSAGE + " " +text;
            out.println(cmd);
        }
    }


    public void setView(MainChatView view) {
        this.view=view;
    }

    public String getLogin() {
        return login;
    }
}
