
/**
 * Initially created by Roman Gaev
 * 26.02.2018
 * Protocol for communication
 * <p>
 * May the force be with you.
 */
public class Protocol {
    public static final int LOGIN=0;









    NewServerThread thread;
    private static final int AUTH = 0;
    private static final int PROFILE = 1;

    private int state = AUTH;

    public Protocol(NewServerThread thread) {
        this.thread = thread;
    }

    public String processInput(String input) {
        int command = Integer.parseInt(input);
        String generatedOutput;


        if (state == AUTH) {
            switch (command) {
                case 0:
                    if (thread.signIn()) {
                        generatedOutput = "Successfully logged in!. 0 for your private user information (I mean no private naked photos). 1 to log out to main menu";
                        state = PROFILE;
                    } else
                        generatedOutput = "Couldn't log in! Something is wrong either with your login or your password or your hands...";
                    break;
                case 1:
                    generatedOutput = "Sorry! This function is unavailable so far! I need to stop drinking Guinness so much and code more...";
                    break;
                case 2:
                    generatedOutput = "Did you really think you deserve a surprise? Oh, come on. I would never spend my time on you.";
                    break;
                default:
                    generatedOutput = "See you in a bit!";
                    break;
            }
        } else if (state == PROFILE) {
            switch (command) {
                case 0:
                    generatedOutput = thread.getCurrentUser().toString() + ". 0 for your private user information (I mean no private naked photos). 1 to log out to main menu";
                    break;
                default:
                    thread.setCurrentUser(null);
                    generatedOutput = "Greetings, motherfucker! Welcome to server! 0 to sign in. 1 to sign up. 2 to get a surprise. 3 to quit the program:";
                    state = AUTH;
                    break;
            }
        } else {
            generatedOutput = "Bye.";
            state = AUTH;
        }

        return generatedOutput;
    }


}
