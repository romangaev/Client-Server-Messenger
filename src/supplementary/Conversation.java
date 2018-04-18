package supplementary;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * @author Roman Gaev
 * supplementary.Conversation class represents conversation with participants: either private 1-1 or a group chat
 * <p>
 * version 18.04.2018
 */
public class Conversation implements Serializable {
    private String name;
    private ArrayList<String> participants;
    private ArrayList<String> messages = new ArrayList<>();

    public Conversation(String name, String member) {
        this.name = name;
        this.participants = new ArrayList<>();
        participants.add(member);
    }

    public Conversation(String name, ArrayList<String> participants) {
        this.name = name;
        this.participants = participants;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<String> getParticipants() {
        return participants;
    }

    public ArrayList<String> getMessages() {
        return messages;
    }
}
