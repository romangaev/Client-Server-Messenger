package supplementary;

import java.io.File;

/**
 * @author Roman, Ali
 *
 * Implementing the Serializable interface, turns the message to binary
 */

import java.io.Serializable;
import java.sql.Timestamp;
/**
 * @author Roman Gaev
 * supplementary.Message class represents message for client-server exchange
 * <p>
 * version 18.04.2018
 */
public class Message implements Serializable{
    private int command;
    private String[] content;
    private File file= null;

    public Message(int command){
        this.command=command;
    }
    public Message(int command, String[]content){
        this.command=command;
        this.content= content;
    }
    public Message(int command, String[]content, File file){
        this.command=command;
        this.content= content;
        this.file=file;
    }

    public int getCommand() {
        return command;
    }

    public String[] getContent() {
        return content;
    }

    public File getFile() {
        return file;
    }
}
