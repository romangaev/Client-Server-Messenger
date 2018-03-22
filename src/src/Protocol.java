import java.io.IOException;

/**
 * @author Roman & Maurice
 *
 * Protocol for communication
 */
public class Protocol {
    public static final int LOGIN=0;
    public  static final int REGISTER=1;
    public static final int MESSAGE=3;
    public static final int ONLINE=4;
    public static final int OFFLINE=5;
    public static final int HISTORY=6;
    public static final int CREATE_GROUP=7;
    public static final int LEAVE_GROUP=8;



    public  static final int EXIT=101;
    public static final int TRUE=1;
    public static final int FALSE=0;
    NewServerThread thread;

    /**
     * Constructor of protocol for the particular Thread/Client.
     * @param thread
     */
    public Protocol(NewServerThread thread) {
        this.thread = thread;
    }

    /**
     * Method to process the input and distributing the tasks according to the command
     * @param message Message which contains the command
     * @throws IOException
     */
    public void processInput(Message message) throws IOException {
        int command = message.getCommand();
        //Depending on the command different methods in NewServerThread are being used.
        switch (command) {
            case LOGIN:
                thread.login(message.getContent()[0], message.getContent()[1]); // Login with username and password
                break;

            case  REGISTER:
                //Registering with username,password and legal name
                thread.register(message.getContent()[0], message.getContent()[1], message.getContent()[2]);
                break;

            case MESSAGE:
                //Sending the message
                thread.sendMessage(message);
                break;
            case HISTORY:
                //Sending the history
                thread.sendHistory(message.getContent()[0]);
                break;
            case CREATE_GROUP:
                //Creating the group
                thread.createGroup();
                break;
            case LEAVE_GROUP:
                //Leaving the group
                thread.leaveGroup(message);
                break;
        }


    }

}