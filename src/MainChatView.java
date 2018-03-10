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
    private JList<String> msgList = new JList<String>(msgModel){

        /**
         * @inherited <p>
         */
        @Override
        public boolean getScrollableTracksViewportWidth() {
            return true;
        }


    };
    private JTextArea inputField= new JTextArea(3,50);



    public MainChatView(ClientModel client) {
        this.client=client;
        client.setView(this);


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
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        south.add(inputField);
        south.add(sendButton);
        add(south, BorderLayout.SOUTH);
    }



    public void actionPerformed(ActionEvent e) {
        String text = inputField.getText();
        if(!text.equals("")&&!text.equals(" ")){
            client.sendMessage(text);
            msgModel.addElement("You: "+text);
            inputField.setText("");
        }
    }


    public void updateOnline(String s){userListModel.addElement(s);}
    public void updateOffline(String s){userListModel.removeElement(s);}
    public void updateMessages(String s){msgModel.addElement(s);}






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
            String[] tokens =((String)value).split(" ",2);
            l.setText(tokens[0]);
            ta.setText(tokens[1]);
            int width = list.getWidth();
            // this is just to lure the ta's internal sizing mechanism into action
            if (width > 0)
                ta.setSize(width, Short.MAX_VALUE);
            return p;

        }
    }
}
