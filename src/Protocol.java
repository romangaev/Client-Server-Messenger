
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

    public  static final int EXIT=101;
    public static final int TRUE=1;
    public static final int FALSE=0;
    NewServerThread thread;


    public Protocol(NewServerThread thread) {
        this.thread = thread;
    }

    public void processInput(String input) {
        String [] tokens= input.split(" ");
        int command = Integer.parseInt(tokens[0]);
        System.out.println("3");
        switch (command) {
            case LOGIN:
                thread.login(tokens[1],tokens[2]);
                break;

            case  REGISTER:
                thread.register(tokens[1],tokens[2], tokens[3]);
                break;

            case MESSAGE:
                thread.sentMessage(input.split(" ",2)[1]);
                break;

        }


    }

}
