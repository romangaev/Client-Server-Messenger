import javax.swing.*;
import java.io.*;
import java.net.Socket;

/**
 * Initially created by Roman Gaev
 * 26.02.2018
 * Simple client model
 * <p>
 * May the force be with you.
 */
public class ClientModel {
    private String serverName;
    private int serverPort;
    private Socket serverSocket;
    private PrintWriter out;
    private BufferedReader in;

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
                startReadingThread();
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
                            String cmd = tokens[0];
                            if ("online".equalsIgnoreCase(cmd)) {
                                handleOnline(tokens);
                            } else if ("offline".equalsIgnoreCase(cmd)) {
                                handleOffline(tokens);
                            } else if ("msg".equalsIgnoreCase(cmd)) {
                                String[] tokensMsg = line.split(" ");
                                handleMessage(tokensMsg);
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


    private void handleMessage(String[] tokensMsg) {
        String login = tokensMsg[1];
        String msgBody = tokensMsg[2];


    }

    private void handleOffline(String[] tokens) {
        String login = tokens[1];

    }

    private void handleOnline(String[] tokens) {
        String login = tokens[1];

    }


}
