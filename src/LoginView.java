import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.*;

/**
 * @author Ioana Avirvarei GUI for login, register and chat page
 */
public class LoginView extends JFrame {

    private ClientModel client;
    JTextField loginField;
    JPasswordField passwordField;
    public static JButton confirmButton;
    private JLabel stateLabel = new JLabel("Loading...");
    private JPanel cardsPanel;

    Font titleFont = new Font("Script MT Bold", Font.BOLD, 30); // font for the title


    public boolean checkPassword(char[] password) {

        if(!(password.length > 5 && password.length < 13)) {
            JOptionPane.showMessageDialog(new JFrame(), "Password should be between 6-12 characters.", "Password Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }


        int numCount = 0;
        int alphCount = 0;
        for(char c : password) {
            if(Character.isAlphabetic(c)) {
                alphCount++;
            }
            if(Character.isDigit(c)) {
                numCount++;
            }
            if(!Character.isLetterOrDigit(c)) {
                JOptionPane.showMessageDialog(new JFrame(), "Password should not include any special characters", "Password Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        if(!(alphCount >= 1 && numCount >= 1)) {
            JOptionPane.showMessageDialog(new JFrame(), "Your password should include at least 1 numeric and 1 alphabetic letter.", "Password Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;

    }

    // Method for checking restrictions in name
    public boolean checkName(String name) {

        // The name should be 2-40 characters long
        if(!(name.length() < 40 && name.length() > 2)) {
            JOptionPane.showMessageDialog(new JFrame(), "Name should be between 2-40 characters long.", "Name Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // The name should not contain numbers
        char[] nameArray = name.toCharArray();
        for(char c : nameArray) {
            if(Character.isDigit(c)) {
                JOptionPane.showMessageDialog(new JFrame(), "Name should not include any numbers.", "Name Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // The name should not contain any special characters, whitespace is allowed
            if(!Character.isWhitespace(c)){
                if(!Character.isLetterOrDigit(c)) {
                    JOptionPane.showMessageDialog(new JFrame(), "Name should not include any special characters", "Name Error", JOptionPane.WARNING_MESSAGE);
                    return false;
                }
            }
        }
        return true;
    }

    // Array to check if every single character of the username is a letter
    public boolean checkForLetter(char[] charArray) {

        for (char c : charArray) {
            if (!Character.isAlphabetic(c)) {
                return false;
            }
        }
        return true;
    }

    public LoginView() {
        super("Login/Sign up");

        //Set up client
        client = new ClientModel("localhost", 6006);
        if (!client.connect()) stateLabel.setText("Error: unable to connect!");
        else stateLabel.setText("Connection is established");

        //Set up GUI
        cardsPanel = new JPanel(new CardLayout());
        cardsPanel.add(buildLogPanel());
        cardsPanel.add(buildRegPanel());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        mainPanel.add(cardsPanel);
        mainPanel.add(stateLabel);
        add(mainPanel);
        setSize(500, 400);
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }


    public JPanel buildLogPanel() {
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));

        JLabel welcomeLabel = new JLabel("<html>Welcome to<br> Messenger!</html>", SwingConstants.CENTER);
        welcomeLabel.setFont(titleFont); // set the font
        welcomeLabel.setForeground(Color.GRAY); // set the color
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);


        loginField = new JTextField();
        loginField.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = new JButton("   Login   ");
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerButton = new JButton("Register");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton connectButton = new JButton("Connect");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        logPanel.add(welcomeLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 50)));
        logPanel.add(loginField);
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        logPanel.add(passwordField);
        logPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        logPanel.add(loginButton);
        logPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        logPanel.add(registerButton);
        logPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        logPanel.add(connectButton);
        logPanel.add(Box.createRigidArea(new Dimension(0, 50)));


        loginButton.addActionListener(new loginActionListener());
        registerButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                CardLayout cl = (CardLayout) (cardsPanel.getLayout());
                cl.next(cardsPanel);
            }
        });
        connectButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                client = new ClientModel("localhost", 6006);
                if (!client.connect()) stateLabel.setText("Error: still unable to connect!");
                else stateLabel.setText("Connection is established");
            }
        });


        return logPanel;
    }


    public JPanel buildRegPanel() {
        JPanel regPanel = new JPanel();
        regPanel.setLayout(new BoxLayout(regPanel, BoxLayout.Y_AXIS));

        JLabel registerLabel = new JLabel("<html>Enter your registration<br>details: </html>");
        registerLabel.setFont(titleFont); // set the font
        registerLabel.setForeground(Color.GRAY); // set the color
        regPanel.add(registerLabel, BorderLayout.NORTH);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // for username
        JLabel userLabel = new JLabel("Username"); // chooseusernameLabel
        regPanel.add(userLabel);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextField loginField = new JTextField();
        regPanel.add(loginField);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // for password
        JLabel passwordLabel = new JLabel("Password");
        regPanel.add(passwordLabel);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPasswordField pwField = new JPasswordField(20);
        regPanel.add(pwField);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // for confirm password
        JLabel confirmPasswordLabel = new JLabel("Confirm password");
        regPanel.add(confirmPasswordLabel);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPasswordField pwConfirm = new JPasswordField(20);
        regPanel.add(pwConfirm);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // for name
        JLabel nameLabel = new JLabel("Name");
        regPanel.add(nameLabel);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextField nameField = new JTextField(20);
        regPanel.add(nameField);
        regPanel.add(Box.createRigidArea(new Dimension(0, 10)));


        // register button
        confirmButton = new JButton("Confirm");
        regPanel.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
/**
 if(!String.valueOf(pwField.getPassword()).equals(String.valueOf(pwConfirm.getPassword()))) {
 stateLabel.setText("Password and confirmation are not equal! Check again!");
 return;
 }
 if(pwField.getPassword().length==0 && pwConfirm.getPassword().length==0 && nameField.getText().equals("")&& loginField.getText().equals("")) {
 stateLabel.setText("You should fill all fields in the form!");
 return;
 }
 */
                String username = loginField.getText();
                char[] password = pwField.getPassword();
                String legalName = nameField.getText();
                char[] cfPass = pwConfirm.getPassword();
                char[] userChars = username.toCharArray();
                boolean checkRest = true;
                boolean checkPass = true;

                // Checking if the two password fields are the same
                if(!Arrays.equals(password, cfPass)){
                    checkPass = false;
                    JOptionPane.showMessageDialog(new JFrame(), "Password fields should be the same!", "Confirmation Error", JOptionPane.WARNING_MESSAGE);
                }

                    // Throwing a pane, warning the user that the username should be at least 5-10 characters long
                    if (!checkForLetter(userChars) || username.length() < 5 || username.length() > 11) {
                        JOptionPane.showMessageDialog(new JFrame(), "Your username should not be empty. \nThe length should be 5-10 characters " +
                                "long. \nYour username should only contain letters!", "Username Error", JOptionPane.WARNING_MESSAGE);
                        checkRest = false;
                        
                        // Checking if the password and the legalName field are valid
                    } else if (!checkPassword(password) || !checkName(legalName)) {
                        checkRest = false;

                    }


                    if (checkPass && checkRest && (client.register(loginField.getText(), String.valueOf(pwField.getPassword()), nameField.getText()))) {
                        stateLabel.setText("Successfully registered! You can log in now.");
                    } else stateLabel.setText("Couldn't register. Try again.");
                    CardLayout cl = (CardLayout) (cardsPanel.getLayout());
                    cl.next(cardsPanel);

            }
        });
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JButton backButton = new JButton("Back");
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                CardLayout cl = (CardLayout) (cardsPanel.getLayout());
                cl.next(cardsPanel);
            }
        });
        regPanel.add(backButton);

        return regPanel;
    }


    private class loginActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String username = loginField.getText();
            String password = String.valueOf(passwordField.getPassword());
            if (client.login(username, password)) {
                MainChatView panel = new MainChatView(client);
                JFrame frame = new JFrame("Messenger" + " - " + client.getLogin());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                frame.pack();
                frame.setResizable(true);
                frame.setVisible(true);
                frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                frame.addWindowListener(new java.awt.event.WindowAdapter() {
                    @Override
                    public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                        if (JOptionPane.showConfirmDialog(frame,
                                "Are you sure to close this window?", "Really Closing?",
                                JOptionPane.YES_NO_OPTION,
                                JOptionPane.QUESTION_MESSAGE) == JOptionPane.YES_OPTION) {
                            try {
                                client.getOut().writeObject(new Message(Protocol.EXIT));
                                System.exit(0);
                            } catch (Throwable throwable) {
                                throwable.printStackTrace();
                            }
                        }
                    }
                });

                client.startReadingThread();
                setVisible(false);
                dispose();
            } else {
                // show error message
                stateLabel.setText("Couldn't login! Try again.");

            }

        }

    }


    public static void main(String[] args) {
        new LoginView();
    }


}