import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Ioana, Nabeel, Ali
 */

public class MainChatView extends JPanel implements ActionListener {

    private ClientModel client;
    private DefaultListModel<String> userListModel;
    private JList<String> userListUI;
    private DefaultListModel<String> msgModel;
    private JList<String> msgList;
    private JTextArea inputField;
    private JButton sendButton;

    public MainChatView(ClientModel client) {
        // Initializing client for view and sending reference for that view to client
        this.client = client;
        client.setView(this);

        // Initializing User lists (left side)
        userListModel = new DefaultListModel<>();
        userListUI = new JList<>(userListModel);

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

        sendButton = new JButton("Send");
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
        String text = inputField.getText();


        // Checking if a line is empty after the first line
        // text.trim allows for different messages to be written in different lines
        text = text.trim();
        if (!text.equals("") && !text.equals(" ")) {
            client.sendMessage(text);
            msgModel.addElement("You: " + text);
            inputField.setText("");

            // In case of an empty text entry, a popup message warns the user
        } else {
            JOptionPane.showMessageDialog(new JFrame(), "You should write something!", "Error", JOptionPane.WARNING_MESSAGE);
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
        userListModel.addElement(s);
    }

    public void updateOffline(String s) {
        userListModel.removeElement(s);
    }

    public void updateMessages(String s) {
        msgModel.addElement(s);
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
}
