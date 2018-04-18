package client;

import supplementary.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Arrays;
import javax.swing.*;

/**
 * @author Ioana Avirvarei, Roman Gaev
 *
 * client.LoginView class represents initial GUI View with Login and Registration pages allocated in one frame by CardLayout.
 * Once successfully logged in it proceeds user to a client.MainChatView.
 *
 * version 18.04.2018
 */
public class LoginView extends JFrame {

    private ClientModel client;
    JTextField loginField;
    JPasswordField passwordField;
    public static JButton confirmButton;
    //stateLabel - label to present current application state(on the bottom of the frame)
    private JLabel stateLabel = new JLabel("Loading...");
    private JPanel cardsPanel;

    String ip = "localhost";
    int port = 6000;

    //Constructor with kicking off methods and panels
    public LoginView() {
        super("Login/Sign up");

        //Trying to establish pretty UI if possible
        UIManager.put("nimbusBase", Color.DARK_GRAY);
        UIManager.put("nimbusBlueGrey", Color.LIGHT_GRAY);
        UIManager.put("control", Color.DARK_GRAY);
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            System.out.println("client.LoginView: Unable to set up Nimbus as Look and Feel!");
            e.printStackTrace();
        }

        //Set up client
        client = new ClientModel(ip, port);
        if (!client.connect()) stateLabel.setText("Error: unable to connect!");
        else stateLabel.setText("Connection is established");

        //Set up GUI
        cardsPanel = new JPanel(new CardLayout());
        cardsPanel.add(buildLogPanel());
        cardsPanel.add(buildRegPanel());

        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBackground(Color.DARK_GRAY);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 50, 30));
        mainPanel.add(cardsPanel);
        setLayout(new BorderLayout());
        add(mainPanel, BorderLayout.CENTER);
        add(stateLabel, BorderLayout.SOUTH);
        pack();
        setResizable(false);
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setVisible(true);
    }


    //Build one of the panels for CardLayout - Log in page
    public JPanel buildLogPanel() {
        JPanel logPanel = new JPanel();
        logPanel.setLayout(new BoxLayout(logPanel, BoxLayout.Y_AXIS));
        logPanel.setBackground(Color.DARK_GRAY);

        //JLabel welcomeLabel = new JLabel("<html>Welcome to<br> Messenger!</html>", SwingConstants.CENTER);
        JLabel welcomeLabel = new JLabel(new ImageIcon(LoginView.class.getProtectionDomain().getCodeSource().getLocation().getPath() + "/client/logo2.png"));
        //welcomeLabel.setFont(titleFont); // set the font
        //welcomeLabel.setForeground(Color.GRAY); // set the color
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        loginField = new JTextField();
        loginField.setAlignmentX(Component.CENTER_ALIGNMENT);

        passwordField = new JPasswordField();
        passwordField.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton loginButton = new JButton("   Log in   ");
        loginButton.setFont(new Font("Rockwell", Font.PLAIN, 15));
        loginButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton registerButton = new JButton(" Register ");
        registerButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        registerButton.setFont(new Font("Rockwell", Font.PLAIN, 15));

        JButton connectButton = new JButton(" Connect ");
        connectButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        connectButton.setFont(new Font("Rockwell", Font.PLAIN, 15));

        logPanel.add(welcomeLabel);
        logPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        logPanel.add(loginField);
        logPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        logPanel.add(passwordField);
        logPanel.add(Box.createRigidArea(new Dimension(0, 20)));
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

                client = new ClientModel(ip, port);

                if (!client.connect()) stateLabel.setText("Error: still unable to connect!");
                else stateLabel.setText("Connection is established");
            }
        });

        return logPanel;
    }

    //Build one of the panels for CardLayout - registration page
    public JPanel buildRegPanel() {
        JPanel regPanel = new JPanel();
        regPanel.setLayout(new BoxLayout(regPanel, BoxLayout.Y_AXIS));
        regPanel.setBackground(Color.DARK_GRAY);

        JLabel registerLabel = new JLabel("<html>Enter your registration<br>details: </html>");
        registerLabel.setFont(new Font("Rockwell", Font.PLAIN, 30)); // set the font
        registerLabel.setForeground(Color.WHITE); // set the color
        regPanel.add(registerLabel, BorderLayout.NORTH);
        regPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // for username
        JLabel userLabel = new JLabel("Username"); // chooseusernameLabel
        userLabel.setForeground(Color.LIGHT_GRAY);
        regPanel.add(userLabel);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextField loginField = new JTextField();
        regPanel.add(loginField);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // for password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setForeground(Color.LIGHT_GRAY);
        regPanel.add(passwordLabel);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPasswordField pwField = new JPasswordField(20);
        regPanel.add(pwField);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // for confirm password
        JLabel confirmPasswordLabel = new JLabel("Confirm password");
        confirmPasswordLabel.setForeground(Color.LIGHT_GRAY);
        regPanel.add(confirmPasswordLabel);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JPasswordField pwConfirm = new JPasswordField(20);
        regPanel.add(pwConfirm);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        // for name
        JLabel nameLabel = new JLabel("Name");
        nameLabel.setForeground(Color.LIGHT_GRAY);
        regPanel.add(nameLabel);
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));

        JTextField nameField = new JTextField(20);
        regPanel.add(nameField);
        regPanel.add(Box.createRigidArea(new Dimension(0, 20)));

        // register button
        confirmButton = new JButton("Confirm");
        regPanel.add(confirmButton);
        confirmButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                String username = loginField.getText();
                char[] password = pwField.getPassword();
                String legalName = nameField.getText();
                char[] cfPass = pwConfirm.getPassword();
                char[] userChars = username.toCharArray();
                boolean checkRest = true;
                boolean checkPass = true;
                boolean checkFields = true;

                if (username.isEmpty() || password.length == 0 || cfPass.length == 0 || nameField.getText().isEmpty()) {
                    checkFields = false;
                    JOptionPane.showMessageDialog(new JFrame(), "You should fill all the fields", "Field Error", JOptionPane.WARNING_MESSAGE);
                }

                // Checking if the two password fields are the same
                if (!Arrays.equals(password, cfPass)) {
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

                // If the restrictions are satisfied client gets registered.
                if (checkFields && checkPass && checkRest && (client.register(loginField.getText(), String.valueOf(pwField.getPassword()), nameField.getText()))) {
                    stateLabel.setText("Successfully registered! You can log in now.");
                    loginField.setText("");
                    pwField.setText("");
                    pwConfirm.setText("");
                    nameField.setText("");
                } else stateLabel.setText("Couldn't register. Try again.");

                CardLayout cl = (CardLayout) (cardsPanel.getLayout());
                cl.next(cardsPanel);
            }
        });
        regPanel.add(Box.createRigidArea(new Dimension(0, 5)));
        JButton backButton = new JButton("   Back   ");

        //Action listener for back button
        backButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                CardLayout cl = (CardLayout) (cardsPanel.getLayout());
                cl.next(cardsPanel);
            }
        });
        regPanel.add(backButton);

        return regPanel;
    }

    //Login button's action listener is done in a nested class.
    private class loginActionListener implements ActionListener {
        public void actionPerformed(ActionEvent event) {
            String username = loginField.getText();
            String password = String.valueOf(passwordField.getPassword());
            if (client.login(username, password)) {
                MainChatView panel = new MainChatView(client);
                JFrame frame = new JFrame("Messenger" + " - " + client.getLogin());
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(panel);
                frame.setSize(700, 500);
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


    /**
     * SOME SUPPLEMENTARY METHODS FOR CHECKS
     */
    //checkPassword is a supplementary method to validate password in registration process
    public boolean checkPassword(char[] password) {
        if (!(password.length > 5 && password.length < 13)) {
            JOptionPane.showMessageDialog(new JFrame(), "Password should be between 6-12 characters.", "Password Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        int numCount = 0;
        int alphCount = 0;
        for (char c : password) {
            if (Character.isAlphabetic(c)) alphCount++;
            if (Character.isDigit(c)) numCount++;
            if (!Character.isLetterOrDigit(c)) {
                JOptionPane.showMessageDialog(new JFrame(), "Password should not include any special characters", "Password Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }
        }
        if (!(alphCount >= 1 && numCount >= 1)) {
            JOptionPane.showMessageDialog(new JFrame(), "Your password should include at least 1 numeric and 1 alphabetic letter.", "Password Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }
        return true;
    }

    // Method for checking restrictions in name
    public boolean checkName(String name) {

        // The name should be 2-40 characters long
        if (!(name.length() < 40 && name.length() > 2)) {
            JOptionPane.showMessageDialog(new JFrame(), "Name should be between 2-40 characters long.", "Name Error", JOptionPane.WARNING_MESSAGE);
            return false;
        }

        // The name should not contain numbers
        char[] nameArray = name.toCharArray();
        for (char c : nameArray) {
            if (Character.isDigit(c)) {
                JOptionPane.showMessageDialog(new JFrame(), "Name should not include any numbers.", "Name Error", JOptionPane.WARNING_MESSAGE);
                return false;
            }

            // The name should not contain any special characters, whitespace is allowed
            if (!Character.isWhitespace(c)) {
                if (!Character.isLetterOrDigit(c)) {
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


    /**
     * main method to start the client side application
     */
    public static void main(String[] args) {
        new LoginView();
    }
}
