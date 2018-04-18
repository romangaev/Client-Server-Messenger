package client;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * @author Roman Gaev, Ioana, Ali, Nabeel
 *
 * client.MainChatView is the second view for presenting actual chatbox after successful login.
 *
 * version 18.04.2018
 */

public class MainChatView extends JPanel implements ActionListener {

    private ClientModel client;
    //Left side (west) list with users and groups
    private DefaultListModel<ListEntry> usrListModel = new DefaultListModel();
    private JList<ListEntry> userList = new JList(usrListModel);
    private ListSelectionModel listSelectionModel;
    //Right side (east) list with messages
    private DefaultListModel<String> msgModel;
    private JList<String> msgList;
    //idNameGroups - map to find specific group conversation id for particular nickname
    private Map<String, Integer> idNameGroups;
    private JTextArea inputField;
    private JButton groupButton;
    private JLabel conversationInfo = new JLabel(" ... ");
    private JButton sendButton;
    //matches list - for storing message search matches
    private ArrayList<Integer> matches;

    private ImageIcon offlineIcon =new ImageIcon(LoginView.class.getProtectionDomain().getCodeSource().getLocation().getPath()+ "/client/offline.png");
    private ImageIcon onlineIcon =new ImageIcon(LoginView.class.getProtectionDomain().getCodeSource().getLocation().getPath()+ "/client/online.png");
    private ImageIcon groupIcon =new ImageIcon(LoginView.class.getProtectionDomain().getCodeSource().getLocation().getPath()+ "/client/group.png");
    private ImageIcon noMessageIcon =new ImageIcon(LoginView.class.getProtectionDomain().getCodeSource().getLocation().getPath()+ "/client/nomessage.png");
    private ImageIcon messageIcon = new ImageIcon(LoginView.class.getProtectionDomain().getCodeSource().getLocation().getPath()+ "/client/newmessage.png");


    public MainChatView(ClientModel client) {

        this.client = client;
        client.setView(this);

        onlineIcon.setDescription("onlineIcon");
        offlineIcon.setDescription("offlineIcon");
        groupIcon.setDescription("GROUP");
        userList.setCellRenderer(new UserListCellRenderer());

        /**
         * WEST PANEL
         */
        JPanel west = new JPanel();
        west.setLayout(new BorderLayout());
        west.setBackground(Color.DARK_GRAY);
        west.add(new JScrollPane(userList), BorderLayout.CENTER);

        groupButton = new JButton("create group");
        groupButton.addActionListener(this);
        JPanel westsouth = new JPanel();
        westsouth.setLayout(new BorderLayout());
        westsouth.setBorder(BorderFactory.createEmptyBorder(8, 10, 8, 10));
        westsouth.add(groupButton, BorderLayout.CENTER);
        west.add(westsouth,BorderLayout.SOUTH);

        //CREATING  A SPECIAL MAP CHATROOM ID - NAME
        idNameGroups = new HashMap<>();
        client.getAllUsers().forEach(
                (key, value) -> {
                    if (value.getName().equals("private")) {
                        for (String member : value.getParticipants()) {
                            if (!member.equals(client.getLogin())) {
                                idNameGroups.put(member, key);
                                usrListModel.addElement(new ListEntry(member, offlineIcon,noMessageIcon));
                                //userListModel.addElement(member + " offlineIcon");
                                break;
                            }
                        }
                    } else {
                        idNameGroups.put(value.getName(), key);
                        usrListModel.addElement(new ListEntry(value.getName(), groupIcon,null));
                        //userListModel.addElement(value.getName());
                    }
                }
        );

        listSelectionModel = userList.getSelectionModel();
        listSelectionModel.addListSelectionListener(
                new SharedListSelectionHandler());
        listSelectionModel.setSelectionMode(
                ListSelectionModel.SINGLE_SELECTION);
        userList.setSelectedIndex(0);

        /**
         * EAST PANEL
         */
        msgModel = new DefaultListModel<>();
        msgList = new JList<String>(msgModel) {
            @Override
            public boolean getScrollableTracksViewportWidth() {
                return true;
            }
        };
       msgList.setCellRenderer(new MessageCellRenderer());
        ComponentListener l = new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                msgList.setFixedCellHeight(10);
                msgList.setFixedCellHeight(-1);
            }
        };
        msgList.addComponentListener(l);
        msgList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        JPanel east = new JPanel();
        east.setLayout(new BorderLayout());
        east.add(new JScrollPane(msgList), BorderLayout.CENTER);

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
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = inputField.getText();
                int groupId = idNameGroups.get(userList.getSelectedValue().getName());
                text = text.trim();
                if (!text.equals("") && !text.equals(" ")) {
                    client.sendMessage(groupId, text);
                    msgModel.addElement(client.getLogin() + ": " + text);
                    inputField.setText("");
                    // In case of an empty text entry, a popup message warns the user
                } else {
                    JOptionPane.showMessageDialog(new JFrame(), "You should write something!", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        inner.add(inputField, BorderLayout.CENTER);
        inner.add(sendButton, BorderLayout.EAST);
        eastsouth.add(inner, BorderLayout.CENTER);

        east.add(eastsouth, BorderLayout.SOUTH);
        JPanel eastnorth = new JPanel();
        eastnorth.setLayout(new BorderLayout());
        JTextField searchField = new JTextField();
        searchField.getDocument().addDocumentListener(new MyDocumentListener());
        JButton searchButton = new JButton("Search");
        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (matches == null || matches.isEmpty()) {
                    matches = new ArrayList<>();

                    for (int i = 0; i < msgModel.getSize(); i++) {
                        String s = msgModel.get(i);
                        String sf =searchField.getText();
                        if (s.matches("(?i:(?s).*" + sf + ".*)")) {
                            matches.add(i);
                        }
                    }
                    if(matches!=null)msgList.setSelectedIndex(matches.get(0));
                }else{
                    msgList.setSelectedIndex(matches.get((matches.indexOf(msgList.getSelectedIndex())+1)%matches.size()));
                }
            }
        });

        eastnorth.add(conversationInfo, BorderLayout.WEST);
        conversationInfo.setFont(new Font("Rockwell",Font.BOLD,15));
        conversationInfo.setForeground(Color.white);
        conversationInfo.setBorder(BorderFactory.createEmptyBorder(5, 20, 5, 20));
        eastnorth.add(searchField, BorderLayout.CENTER);
        eastnorth.add(searchButton, BorderLayout.EAST);
        east.add(eastnorth, BorderLayout.NORTH);

        /**
         * MAIN PANEL
         */
        setLayout(new BorderLayout());

        JPanel bigBorder = new JPanel();
        bigBorder.setLayout(new BorderLayout());
        bigBorder.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        bigBorder.add(east, BorderLayout.CENTER);
        bigBorder.add(new JScrollPane(west), BorderLayout.WEST);
        JLabel appTitle = new JLabel("  LMM");
        appTitle.setOpaque(true);
        appTitle.setBackground(Color.gray);
        appTitle.setForeground(Color.white);
        appTitle.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        appTitle.setFont(new Font("Rockwell", Font.BOLD, 20));
        add(appTitle,BorderLayout.NORTH);
        add(bigBorder,BorderLayout.CENTER);
        createPopupMenu();
        userList.setBackground(Color.DARK_GRAY);
        msgList.setBackground(Color.DARK_GRAY);
    }



    /**
     * LISTENER FOR GROUP CREATION BUTTON - CREATES NEW FRAME AND STUFF
     */
    public void actionPerformed(ActionEvent e) {

            JFrame innerframe = new JFrame("Create group");
            JPanel main = new JPanel();
            DefaultListModel<String> model = new DefaultListModel();
            JList users = new JList(model);

            for (int i = 0; i < usrListModel.getSize(); i++) {
                ListEntry entry = usrListModel.get(i);
                if (isPerson(entry.getStatus().getDescription()))
                model.addElement(entry.getName());
            }

            main.setLayout(new BorderLayout());

            JPanel panel = new JPanel(new BorderLayout());
            JButton submitButton = new JButton("Create");
            JTextField nameGroup = new JTextField("Name your group");
            panel.add(nameGroup, BorderLayout.NORTH);
            panel.add(new JScrollPane(users), BorderLayout.CENTER);
            users.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

            main.add(panel, BorderLayout.CENTER);
            main.add(nameGroup, BorderLayout.NORTH);
            main.add(submitButton, BorderLayout.SOUTH); // The listener of this will initiate the CreateGroup method
            submitButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String name = nameGroup.getText();
                    {
                        ArrayList<String> selected = new ArrayList<>();
                        users.getSelectedValuesList().forEach(x -> {
                            String changeName = (String) x;
                            selected.add(changeName.split(" ")[0]);
                        });
                        selected.add(client.getLogin());
                        client.createGroup(name, selected);
                        innerframe.setVisible(false);
                    }
                }
            });
            innerframe.getContentPane().add(main, BorderLayout.CENTER);
            innerframe.pack();
            innerframe.setDefaultCloseOperation(JFrame.HIDE_ON_CLOSE);
            innerframe.setVisible(true); // size is gonna be rearranged and all that random GUI

    }


    /**
     * MESSAGE LIST RENDERER (NEED TO CHANGE THEIR LOOK AND FEEL)
     */
    public class MessageCellRenderer implements ListCellRenderer {
        private JPanel p;
        private JPanel textPanel;
        private JLabel l;
        private JTextArea ta2;
        private JLabel ta;
        private JPanel labelPanel;

        public MessageCellRenderer() {
            p = new JPanel();
            p.setLayout(new BorderLayout());
            setOpaque(true);
            // icon
            l = new JLabel(messageIcon);

            labelPanel = new JPanel();
            labelPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
            labelPanel.add(l);
            // text
            p.add(labelPanel, BorderLayout.WEST);
            // text
            ta2 = new JTextArea();
            ta2.setLineWrap(true);
            ta2.setWrapStyleWord(true);
            ta = new JLabel();
            ta.setForeground(Color.LIGHT_GRAY);
            ta.setFont(new Font("Rockwell",Font.BOLD,15));
            ta2.setFont(new Font("Rockwell",Font.PLAIN,15));
            ta2.setForeground(Color.white);
            textPanel = new JPanel(new BorderLayout());
            textPanel.add(ta, BorderLayout.NORTH);
            textPanel.add(ta2,BorderLayout.SOUTH);
            p.add(textPanel, BorderLayout.CENTER);
        }

        @Override
        public Component getListCellRendererComponent(final JList list,
                                                      final Object value, final int index, final boolean isSelected,
                                                      final boolean hasFocus) {
            String[] msg = ((String) value).split(" ",2);
            ta.setText(msg[0]);
            ta2.setText(msg[1]);

            int width = list.getWidth();
            if (isSelected) {
                ta.setBackground(Color.GRAY);
                ta.setForeground(Color.white);
                ta2.setBackground(Color.GRAY);
                textPanel.setBackground(Color.GRAY);
                labelPanel.setBackground(Color.GRAY);
                p.setBackground(Color.GRAY);
            }
            else {
                ta.setBackground(Color.DARK_GRAY);
                ta.setForeground(Color.LIGHT_GRAY);
                ta2.setBackground(Color.DARK_GRAY);
                textPanel.setBackground(Color.DARK_GRAY);
                labelPanel.setBackground(Color.DARK_GRAY);
                p.setBackground(Color.DARK_GRAY);
            }
            // This is just to lure the ta's internal sizing mechanism into action
            if (width > 0)
                ta.setSize(width, Short.MAX_VALUE);
            return p;
        }
    }



    /**
     * USER LIST RENDERER (NEED TO CHANGE THEIR LOOK AND FEEL)
     */
    class UserListCellRenderer implements ListCellRenderer
    {
        private JPanel p;
        private JLabel messageLabel;
        private JTextArea name;
        private JLabel status;

        public UserListCellRenderer(){
            p=new JPanel();
            messageLabel= new JLabel();
            name=new JTextArea();
            status=new JLabel();
            p.setLayout(new BorderLayout());
            p.add(messageLabel,BorderLayout.EAST);
            p.add(name, BorderLayout.CENTER);
            p.add(status, BorderLayout.WEST);
            name.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
            p.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

            name.setFont(new Font("Rockwell", Font.BOLD, 12));
            name.setForeground(Color.white);
            name.setBackground(Color.DARK_GRAY);
        }

        public Component getListCellRendererComponent(JList list, Object value,
                                                      int index, boolean isSelected,
                                                      boolean cellHasFocus) {
            ListEntry entry = (ListEntry) value;

            name.setText(entry.getName());
            messageLabel.setIcon(entry.getIcon());
            status.setIcon(entry.getStatus());

            if (isSelected) {
                name.setBackground(Color.GRAY);
                p.setBackground(Color.GRAY);
                p.setForeground(Color.GRAY);
            }
            else {
                name.setBackground(Color.DARK_GRAY);
                p.setBackground(Color.DARK_GRAY);
                p.setForeground(Color.DARK_GRAY);
            }

            setEnabled(list.isEnabled());
            setFont(list.getFont());
            setOpaque(true);
            EventQueue.invokeLater(new Runnable()
            {
                public void run()
                {
                    repaint();
                }
            });

            return p;
        }
    }



    /**
     * USER AND GROUP SELECTION LISTENER
     */
    class SharedListSelectionHandler implements ListSelectionListener {
        public void valueChanged(ListSelectionEvent e) {

                        try {
                            if (e.getValueIsAdjusting()) return;
                            if (userList.getSelectedValue() == null) userList.setSelectedIndex(0);
                            ListEntry selectedValue = userList.getSelectedValue();
                            if (selectedValue.getIcon() != null) selectedValue.setIcon(noMessageIcon);
                            int groupId = idNameGroups.get(selectedValue.getName());

                            client.getHistory(groupId);

                            if(isPerson(selectedValue.getStatus().getDescription()))
                                conversationInfo.setText("<html><div style='text-align: center;'>" + selectedValue + "</div></html>");
                            else{
                                StringBuilder sb = new StringBuilder();
                                sb.append("<html><div style='text-align: center;'>" + selectedValue + ": ");
                                client.getAllUsers().get(idNameGroups.get(selectedValue.getName())).getParticipants().forEach(x -> sb.append(" " + x));
                                conversationInfo.setText(sb.toString() + "</div></html>");
                            }

                            matches = null;
                        }catch (IOException em){em.printStackTrace();}

        }
    }



    /**
     * ListEntry class for user list
     */
    class ListEntry implements Comparable<ListEntry>
    {
        private String name;
        private ImageIcon icon;
        private ImageIcon status;

        public ListEntry(String name,ImageIcon status, ImageIcon icon) {
            this.name = name;
            this.icon = icon;
            this.status=status;
        }

        public String getName() {
            return name;
        }

        public ImageIcon getStatus() {
            return status;
        }

        public ImageIcon getIcon() {
            return icon;
        }

        public void setStatus(ImageIcon status) {
            this.status = status;
        }

        public void setIcon(ImageIcon icon) {
            this.icon = icon;
        }

        @Override
        public String toString(){
            return name;
        }

        @Override
        public int compareTo(ListEntry o) {
            return name.compareTo(o.getName());
        }
        @Override
        public boolean equals(Object o){
            if(o instanceof ListEntry){
                ListEntry entry = (ListEntry) o;
                if(name.equals(entry.getName()))return true;
            }
            return false;
        }
    }

    /**
     * SUPPORTIVE METHODS
     */
    public void updateRegister(int i, String s) {
        usrListModel.addElement(new ListEntry(s, offlineIcon,null));
        idNameGroups.put(s,i);
    }

    public void updateOnline(String s) {
        // userListModel.set(userListModel.indexOf(s+" offlineIcon"),s+" onlineIcon");
        usrListModel.get(usrListModel.indexOf(new ListEntry(s,null,null))).setStatus(onlineIcon);
    }

    public void updateOffline(String s) {
        //userListModel.set(userListModel.indexOf(s+" onlineIcon"),s+" offlineIcon");
        usrListModel.get(usrListModel.indexOf(new ListEntry(s,null,null))).setStatus(offlineIcon);
    }

    public void updateMessages(String sender,String conversation, String text) {
        String chatBox;
        if(conversation.equals("private")) chatBox=sender;
        else chatBox=conversation;
        if(chatBox.equals(userList.getSelectedValue().getName())){
            msgModel.addElement(chatBox+": "+text);
            matches=null;
        } else {
            //userListModel.set(userListModel.indexOf(login+" onlineIcon"),login+" onlineIcon O");
            usrListModel.get(usrListModel.indexOf(new ListEntry(chatBox,null,null))).setIcon(messageIcon);
        }
    }

    public void updateHistory(ArrayList<String> history) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                msgModel.removeAllElements();
                for (String message : history) {
                    msgModel.addElement(message);
                }
            }
        });

    }

    public void updateGroups(String s, int i) {
        //  userListModel.addElement(s);
        usrListModel.addElement(new ListEntry(s, groupIcon,null));
        idNameGroups.put(s, i);
    }

    public void deleteGroup(String groupName) {
        // userListUI.setSelectedIndex((userListUI.getSelectedIndex()+1)%userListModel.getSize());
        //userListModel.removeElement(groupName);
        userList.setSelectedIndex((userList.getSelectedIndex()+1)% usrListModel.getSize());
        usrListModel.removeElement(new ListEntry(groupName,null,null));
        idNameGroups.remove(groupName);
    }

    public boolean isPerson(String s) {
        return !s.equals("GROUP");
    }

    /**
     * STUFF FOR POP UP MENU  (LEAVE GROUP, RIGHT CLICK)
     */
    private void createPopupMenu() {
        JPopupMenu popup = new JPopupMenu();
        JMenuItem myMenuItem1 = new JMenuItem("Leave group");
        popup.add(myMenuItem1);
        myMenuItem1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String s = userList.getSelectedValue().getStatus().getDescription();
                if (isPerson(s)) {
                    JOptionPane.showMessageDialog(new JFrame(), "This is a person, not a group", "Error", JOptionPane.WARNING_MESSAGE);
                } else {
                    client.leaveGroup(idNameGroups.get(s));
                }
            }
        });
        MouseListener popupListener = new PopupListener(popup);
        userList.addMouseListener(popupListener);
        userList.setComponentPopupMenu(popup);
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

    class MyDocumentListener implements DocumentListener {
        @Override
        public void insertUpdate(DocumentEvent e) {
            matches=null;
        }

        @Override
        public void removeUpdate(DocumentEvent e) {
            matches=null;
        }

        @Override
        public void changedUpdate(DocumentEvent e) {
            matches=null;
        }
    }


}