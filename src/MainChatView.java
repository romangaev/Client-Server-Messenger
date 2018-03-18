
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainChatView extends JPanel implements ActionListener {

    private ClientModel client;
    private DefaultListModel<String> userListModel;
    private JList<String> userListUI;
    public ListSelectionModel listSelectionModel;
    private DefaultListModel<String> msgModel;
    private JList<String> msgList;
    private JTextArea inputField;
    private Map<String,Integer> idNameGroups;

    public MainChatView(ClientModel client) {
        //initializing client for view and sending reference for that view to client
        this.client = client;
        client.setView(this);



        //initializing User lists (left side)
        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);




        //CREATING  A SPECIAL MAP CHATROOM ID - NAME
        idNameGroups = new HashMap<>();
       client.getAllUsers().forEach(
               (key, value) -> {
                   if (value.getName().equals("private")) {

                       for(String member : value.getParticipants()){
                           if(!member.equals(client.getLogin())){
                               idNameGroups.put(member,key);
                               break;
                           }
                       }


                   } else {
                       idNameGroups.put(value.getName(),key);
                   }
               }
       );

        for(String every : idNameGroups.keySet()){
            userListModel.addElement(every+" offline");
        }

        listSelectionModel = userListUI.getSelectionModel();
        listSelectionModel.addListSelectionListener(
                new SharedListSelectionHandler());
        listSelectionModel.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);




        // initializing message list and list renderers stuff
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
                // next line possible if list is of type JXList
                // list.invalidateCellSizeCache();
                // for core: force cache invalidation by temporarily setting fixed height
                msgList.setFixedCellHeight(10);
                msgList.setFixedCellHeight(-1);
            }
        };
        msgList.addComponentListener(l);



        // initializing GUI for south panel
        JPanel south = new JPanel();
        south.setLayout(new BorderLayout());
        south.setBackground(Color.LIGHT_GRAY);

        JPanel inner = new JPanel();
        inner.setLayout(new BorderLayout());
        inner.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        inputField = new JTextArea();
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);

        JButton sendButton = new JButton("Send");
        sendButton.addActionListener(this);

        inner.add(inputField, BorderLayout.CENTER);
        inner.add(sendButton, BorderLayout.EAST);
        south.add(inner, BorderLayout.CENTER);




        //adding to main panel
        setLayout(new BorderLayout());
        add(new JScrollPane(msgList), BorderLayout.CENTER);
        add(new JScrollPane(userListUI), BorderLayout.WEST);
        add(south, BorderLayout.SOUTH);


    }

    //Action listener for Send Button
    public void actionPerformed(ActionEvent e) {
        String text = inputField.getText();
        int groupId = idNameGroups.get(userListUI.getSelectedValue().split(" ",2)[0]);

        if (!text.equals("") && !text.equals(" ")) {
            client.sendMessage(groupId,text);
            msgModel.addElement(client.getLogin()+": " + text);
            inputField.setText("");
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "You should write something!", "Error", JOptionPane.WARNING_MESSAGE);
        }
    }

    //methods called by client model to update lists
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


    //render class to render appereance of lists
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
            // this is just to lure the ta's internal sizing mechanism into action
            if (width > 0)
                ta.setSize(width, Short.MAX_VALUE);
            return p;

        }
    }



    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {
            try {
                if( e.getValueIsAdjusting() ) return;
                if(userListUI.getSelectedValue()==null) userListUI.setSelectedIndex(userListModel.getSize()-1);
                int groupId = idNameGroups.get(userListUI.getSelectedValue().split(" ",2)[0]);
                client.getHistory(groupId);

            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }
    }


}