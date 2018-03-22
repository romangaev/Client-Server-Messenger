import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Roman Gaev
 * Conversation class for the name of the conversation and the participants for it.
 */
public class Conversation implements Serializable{
    private String name;
    private ArrayList<String> participants;
    private ArrayList<String> messages= new ArrayList<>();

    /**
     * @param name Name of the conversation
     * @param member Member to be added to the conversation
     */
    public Conversation(String name,  String member) {
        this.name = name;
        this.participants = new ArrayList<>();
        participants.add(member);
    }
    public Conversation(String name, ArrayList<String> participants) {
        this.name = name;
        this.participants=participants;
    }

    /**
     * Getter for name
     * @return Name of the conversation
     */
    public String getName() {
        return name;
    }

    /**
     * Setter for name
     * @param name Name which will be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter for participants of the conversation
     * @return Participants as an ArrayList.
     */
    public ArrayList<String> getParticipants() {
        return participants;
    }

    /**
     * Getter for messages.
     * @return Messages that has been sent in the conversation in an ArrayList.
     */
    public ArrayList<String> getMessages() {
        return messages;
    }
}