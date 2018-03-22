import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Ioana, Ali, Nabeel
 *
 * The View for the main chat window
 */

public class MainChatView extends JPanel implements ActionListener {

    private ClientModel client;
    private DefaultListModel<String> userListModel;
    private JList<String> userListUI;
    private ListSelectionModel listSelectionModel;
    private DefaultListModel<String> msgModel;
    private JList<String> msgList;
    private JTextArea inputField;
    private Map<String,Integer> idNameGroups;
    private JButton groupButton;
    private JLabel conversationInfo = new JLabel("LastMinuteMessenger");
    JPopupMenu popup;
    private JButton sendButton;

    /**
     * Constructor for the Chat View.
     * @param client Client that is logged in.
     */
    public MainChatView(ClientModel client) {
        // Initializing client for view and sending reference for that view to client
        this.client = client;
        client.setView(this);


        //initializing User lists (left side) and WEST panel

        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);
        JPanel west= new JPanel();
        west.setLayout(new BorderLayout());
        west.add(new JScrollPane(userListUI),BorderLayout.CENTER);


        groupButton = new JButton("create");
        groupButton.addActionListener(this);
        west.add(groupButton,BorderLayout.SOUTH);


        //CREATING  A SPECIAL MAP CHATROOM ID - NAME
        idNameGroups = new HashMap<>();
        client.getAllUsers().forEach(
                (key, value) -> {
                    if (value.getName().equals("private")) {

                        for(String member : value.getParticipants()){
                            if(!member.equals(client.getLogin())){
                                idNameGroups.put(member,key);
                                userListModel.addElement(member + " offline");
                                break;
                            }
                        }
                    } else {
                        idNameGroups.put(value.getName(),key);
                        userListModel.addElement(value.getName());
                    }
                }
        );

        listSelectionModel = userListUI.getSelectionModel();
        listSelectionModel.addListSelectionListener(
                new SharedListSelectionHandler());
        listSelectionModel.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        userListUI.setSelectedIndex(0);




        // Initializing message list and list renderers stuff
        msgModel = new DefaultListModel<>();
        msgList = new JList<String>(msgModel) {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }
        };
        msgList.setCellRenderer(new MyCellRenderer());
        ComponentListener l = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {

                // Next line possible if list is of type JXList
                // list.invalidateCellSizeCache();
                // for core: force cache invalidation by temporarily setting fixed height
                msgList.setFixedCellHeight(10);
                msgList.setFixedCellHeight(-1);
            }
        };
        msgList.addComponentListener(l);
        JPanel east = new JPanel();
        east.setLayout(new BorderLayout());
        east.add(new JScrollPane(msgList), BorderLayout.CENTER);




        // initializing GUI for EAST panel
        JPanel eastsouth = new JPanel();
        eastsouth.setLayout(new BorderLayout());
        eastsouth.setBackground(Color.LIGHT_GRAY);

        JPanel inner = new JPanel();
        inner.setLayout(new BorderLayout());
        inner.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputField = new JTextArea();
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);

        sendButton = new JButton("Send");
        sendButton.addActionListener(this);

        inner.add(inputField, BorderLayout.CENTER);
        inner.add(sendButton, BorderLayout.EAST);
        eastsouth.add(inner, BorderLayout.CENTER);



        east.add(eastsouth, BorderLayout.SOUTH);
        east.add(conversationInfo, BorderLayout.NORTH);

        //adding to main panel
        setLayout(new BorderLayout());
        add(east, BorderLayout.CENTER);
        add(new JScrollPane(west), BorderLayout.WEST);

        createPopupMenu();

    }
    //    public void delayMessage() {
//
//        Timer timer = new Timer(400, new ActionListener() {
//            @Override
//            public void actionPerformed(ActionEvent arg0) {
//                sendButton.setEnabled(false);
//            }
//        });
//        timer.setRepeats(false);
//        timer.start();
//    }
//    public void actionPerformed(ActionEvent e) {
//
//
//        String text = inputField.getText();
//
//        // Checking if a line is empty after the first line
//        // text.trim allows for different messages to be written in different lines
//        text = text.trim();
//        if (!text.equals("") && !text.equals(" ")) {
//            client.sendMessage(text);
//            msgModel.addElement("You: " + text);
//            inputField.setText("");
//
//            // In case of an empty text entry, a popup message warns the user
//        } else {
//            JOptionPane.showMessageDialog(new JFrame(), "You should write something!", "Error", JOptionPane.WARNING_MESSAGE);
//        }
//        delayMessage();
//    }
    //Action listener for Send Button
    public void actionPerformed(ActionEvent e) {
        if(e.getSource()==groupButton){

            JFrame innerframe = new JFrame("Create group");
            JPanel main = new JPanel();
            DefaultListModel<String> model = new DefaultListModel();
            JList users = new JList(model);

            for(int i =0;i< userListModel.getSize();i++)
            {String s =userListModel.get(i);
                if(isPerson(s))
                    model.addElement(s);
            }

            main.setLayout(new BorderLayout());

            JPanel panel = new JPanel(new BorderLayout());
            Border border = BorderFactory.createTitledBorder("User List");
            panel.setBorder(border);
            JButton submitButton = new JButton("Create");
            JTextField nameGroup = new JTextField("Name your group");
            panel.add(nameGroup,BorderLayout.NORTH);
            panel.add(new JScrollPane(users),BorderLayout.CENTER);
            users.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);


            main.add(panel, BorderLayout.CENTER);
            main.add(nameGroup, BorderLayout.NORTH);
            main.add(submitButton, BorderLayout.SOUTH); // The listener of this will initiate the CreateGroup method
            submitButton.addActionListener(new ActionListener() {

                //If the UserList does not contain  the name get the selected values and create a group with the name and those values which are the users
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = nameGroup.getText();
                    if(!userListModel.contains(name));{
                        ArrayList<String> selected = new ArrayList<>() ;
                        users.getSelectedValuesList().forEach(x->{
                            String changeName = (String)x;
                            selected.add(changeName.split(" ")[0]);
                        });
                        selected.add(client.getLogin());

                        client.createGroup(name,selected);
                        innerframe.setVisible(false);}
                }
            });
            innerframe.getContentPane().add(main, BorderLayout.CENTER);
            innerframe.pack();
            innerframe.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            innerframe.setVisible(true); // size is gonna be rearranged and all that random GUI
        }
        else {
            String text = inputField.getText();
            int groupId = idNameGroups.get(userListUI.getSelectedValue().split(" ", 2)[0]);


            text = text.trim();
            // Checking if a line is empty after the first line
            // text.trim allows for different messages to be written in different lines

            if (!text.equals("") && !text.equals(" ")) {
                client.sendMessage(groupId, text);
                msgModel.addElement(client.getLogin() + ": " + text);
                inputField.setText("");
                // In case of an empty text entry, a popup message warns the user
            } else {
                JOptionPane.showMessageDialog(new JFrame(), "You should write something!", "Error", JOptionPane.WARNING_MESSAGE);
            }
        }

        // SPAM PREVENTION
//        long startTime = System.currentTimeMillis();
//        while(System.currentTimeMillis() - startTime < 401){
//            sendButton.setEnabled(false);
//        }
//        sendButton.setEnabled(true);


    }

    // Methods called by client model to update lists
    public void updateOnline(String s) {
        userListModel.removeElement(s+" offline");
        userListModel.addElement(s+" online");

    }

    public void updateOffline(String s) {
        userListModel.removeElement(s+" online");
        userListModel.addElement(s+" offline");

    }

    public void updateMessages(String s) {
        msgModel.addElement(s);
    }

    public void updateHistory (ArrayList<String> history) {
        msgModel.removeAllElements();
        for(String message : history){
            msgModel.addElement(message);
        }
    }
    public void updateGroups(String s,int i) {
        userListModel.addElement(s);
        idNameGroups.put(s,i);
    }
    public void deleteGroup(String groupName) {
        userListModel.removeElement(groupName);
        idNameGroups.remove(groupName);
    }


    // Render class to render appearance of lists
    public class MyCellRenderer implements ListCellRenderer {
        private JPanel p;
        private JPanel iconPanel;
        private JLabel l;
        private JTextArea ta;

        public MyCellRenderer() {
            p = new JPanel();
            p.setLayout(new BorderLayout());

            // icon
            iconPanel = new JPanel(new BorderLayout());
            l = new JLabel("icon"); // <-- this will be an icon instead of a
            // text
            iconPanel.add(l, BorderLayout.NORTH);
            p.add(iconPanel, BorderLayout.WEST);

            // text
            ta = new JTextArea();
            ta.setLineWrap(true);
            ta.setWrapStyleWord(true);
            p.add(ta, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(final JList list,
                                                      final Object value, final int index, final boolean isSelected,
                                                      final boolean hasFocus) {
            String[] tokens = ((String) value).split(" ", 2);
            l.setText(tokens[0]);
            ta.setText(tokens[1]);
            int width = list.getWidth();
            // This is just to lure the ta's internal sizing mechanism into action
            if (width > 0)
                ta.setSize(width, Short.MAX_VALUE);
            return p;

        }
    }
// The difference between groups and the particular users are whether they contain the "offline/online" in their displayed names.

    /**
     *
     * @param s The displayed username to be checked
     * @return Boolean statement whether the user is a Person or not.
     */
    public boolean isPerson(String s){
        return s.contains(" offline")||s.contains(" online");
    }

    /**
     * Class to handle the group selection list.
     */
    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            try {
                if( e.getValueIsAdjusting() ) return;
                if(userListUI.getSelectedValue()==null) userListUI.setSelectedIndex(userListModel.getSize()-1);
                int groupId = idNameGroups.get(userListUI.getSelectedValue().split(" ",2)[0]);
                client.getHistory(groupId);
                //Checking if the selected value is a user or not.
                String selectedValue = userListUI.getSelectedValue();
                if(isPerson(selectedValue)){
                    conversationInfo.setText("<html><div style='text-align: center;'>" +selectedValue+ "</div></html>");
                }else{
                    StringBuilder sb= new StringBuilder();
                    sb.append("<html><div style='text-align: center;'>" +selectedValue+": ");
                    client.getAllUsers().get(idNameGroups.get(selectedValue)).getParticipants().forEach(x-> sb.append(" "+x));
                    conversationInfo.setText(sb.toString()+ "</div></html>");
                }

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }


    /**
     * When a user right clicks an item on the list they'll have the chance to Leave the Group
     */
    private void createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem myMenuItem1 = new JMenuItem("Leave group");
        popup.add(myMenuItem1);
        myMenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = userListUI.getSelectedValue();
                //If the selected value is a person they cannot leave it.
                if(isPerson(s)){
                    JOptionPane.showMessageDialog(new JFrame(), "This is a person, not a group", "Error", JOptionPane.WARNING_MESSAGE);
                }else{
                    client.leaveGroup(idNameGroups.get(s));
                }
            }
        });
        MouseListener popupListener = new PopupListener(popup);
        userListUI.addMouseListener(popupListener);
        userListUI.setComponentPopupMenu(popup);
    }
    private class PopupListener extends MouseAdapter {

        private JPopupMenu popup;

        PopupListener(JPopupMenu popupMenu) {
            popup = popupMenu;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            maybeShowPopup(e);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            maybeShowPopup(e);
        }

        private void maybeShowPopup(MouseEvent e) {
            if (e.isPopupTrigger()) {
                popup.show(e.getComponent(), e.getX(), e.getY());
            }
        }
    }


}