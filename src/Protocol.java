import java.io.IOException;

/**
 * Initially created by Roman Gaev
 * 26.02.2018
 * Protocol for communication
 * <p>
 * May the force be with you.
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


    public Protocol(NewServerThread thread) {
        this.thread = thread;
    }

    public void processInput(Message message) throws IOException {
        int command = message.getCommand();
        switch (command) {
            case LOGIN:
                thread.login(message.getContent()[0], message.getContent()[1]);
                break;

            case  REGISTER:
                thread.register(message.getContent()[0], message.getContent()[1], message.getContent()[2]);
                break;

            case MESSAGE:
                thread.sendMessage(message);
                break;
            case HISTORY:
                thread.sendHistory(message.getContent()[0]);
                break;
            case CREATE_GROUP:
                thread.createGroup();
                break;
            case LEAVE_GROUP:
                thread.leaveGroup(message);
                break;
        }


    }

}
