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

        GridBagConstraints left = new GridBagConstraints();
        left.anchor = GridBagConstraints.LINE_START;
        left.fill = GridBagConstraints.HORIZONTAL;
        left.weightx = 512.0D;
        left.weighty = 1.0D;

        GridBagConstraints right = new GridBagConstraints();
        right.insets = new Insets(0, 10, 0, 0);
        right.anchor = GridBagConstraints.LINE_END;
        right.fill = GridBagConstraints.NONE;
        right.weightx = 1.0D;
        right.weighty = 1.0D;
        
        JPanel south = new JPanel();
        south.setBackground(Color.LIGHT_GRAY);
        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(this);
        south.add(inputField, right);
        south.add(sendButton, left);
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
