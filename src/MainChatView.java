import javafx.beans.InvalidationListener;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import java.util.TreeSet;

public class MainChatView extends JPanel implements ActionListener{

    private ClientModel client;

    private DefaultListModel<String> userListModel= new DefaultListModel<>();
    private JList<String> userListUI=new JList<>(userListModel);

    private DefaultListModel<String> msgModel = new DefaultListModel<>();
    private JList<String> msgList = new JList<>(msgModel);
    private JTextField inputField= new JTextField(50); // Changed from JTextArea to JTextField



    public MainChatView(ClientModel client) {
        this.client=client;
        client.setView(this);

        setLayout(new BorderLayout());
        add(new JScrollPane(msgList),BorderLayout.CENTER);
        add(new JScrollPane(userListUI),BorderLayout.WEST);

        JPanel south = new JPanel();
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        south.add(inputField);
        south.add(sendButton);
        add(south, BorderLayout.SOUTH);
    }



    public void actionPerformed(ActionEvent e) {
        client.sendMessage(inputField.getText());


        msgModel.addElement("You: "+inputField.getText());
        inputField.setText("");
    }


    public void updateOnline(String s){userListModel.addElement(s);}
    public void updateOffline(String s){userListModel.removeElement(s);}
    public void updateMessages(String s){msgModel.addElement(s);}


}
