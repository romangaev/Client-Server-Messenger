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
                thread.login(message.getContent()[0],message.getContent()[1]);
                break;

            case  REGISTER:
                thread.register(message.getContent()[0],message.getContent()[1], message.getContent()[2]);
                break;

            case MESSAGE:
                thread.sendMessage(message);
                break;

        }


    }

}
