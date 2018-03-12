import java.io.Serializable;

public class Message implements Serializable{
    int command;
    String[] content;
    public Message(int command){
        this.command=command;
    }
    public Message(int command, String[]content){
        this.command=command;
        this.content= content;
    }

    public int getCommand() {
        return command;
    }

    public String[] getContent() {
        return content;
    }
}
