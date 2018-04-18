package server;

/**
 * @author Roman Gaev
 *
 * Main method to start the server
 *
 * version 18.04.2018
 */

public class ServerStarter {
    public static void main(String[] args){
        ServerModel serverModel = new ServerModel();
        serverModel.start();
    }
}
