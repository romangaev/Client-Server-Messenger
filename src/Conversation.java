import java.io.Serializable;
import java.util.ArrayList;

 public class Conversation implements Serializable{
    private String name;
    private ArrayList<String> participants;
    private ArrayList<String> messages= new ArrayList<>();

     public Conversation(String name,  String member) {
         this.name = name;
         this.participants = new ArrayList<>();
         participants.add(member);
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
