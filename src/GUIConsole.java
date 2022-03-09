import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;

public class GUIConsole extends JFrame implements ChatIF {
    final public static int DEFAULT_PORT = 5555;
    ChatClient client;

    private JButton closeB = new JButton("Logoff");
    private JButton openB = new JButton("Login");
    private JButton sendB = new JButton("Send");
    private JButton quitB = new JButton("Quit");
    private JButton whoB = new JButton("User List");
    private JButton pmB = new JButton("PM");
    private JButton tttB = new JButton("Tic Tac Toe");

    private JComboBox whoCB = new JComboBox();

    private JTextField portTxF = new JTextField("5555");
    private JTextField hostTxF = new JTextField("127.0.0.1");
    private JTextField messageTxF = new JTextField("");
    private JTextField userTxF = new JTextField("");

    private JLabel portLB = new JLabel("Port: ", JLabel.RIGHT);
    private JLabel hostLB = new JLabel("Host: ", JLabel.RIGHT);
    private JLabel messageLB = new JLabel("Message: ", JLabel.RIGHT);
    private JLabel userLB = new JLabel("User ID: ", JLabel.RIGHT);

    private JTextArea messageList = new JTextArea();

    public GUIConsole(String host, int port, String userID) {
        super("Simple Chat GUI");
        setSize(300, 400);

        setLayout(new BorderLayout(5, 5));
        JPanel bottom = new JPanel();
        add("Center", messageList);
        add("South", bottom);

        bottom.setLayout(new GridLayout(8, 2, 5, 5));
        bottom.add(hostLB);
        bottom.add(hostTxF);

        bottom.add(portLB);
        bottom.add(portTxF);

        bottom.add(userLB);
        bottom.add(userTxF);

        bottom.add(messageLB);
        bottom.add(messageTxF);

        bottom.add(whoB);
        bottom.add(whoCB);


        bottom.add(pmB);
        bottom.add(sendB);
        bottom.add(openB);
        bottom.add(closeB);
        bottom.add(tttB);
        bottom.add(quitB);

        portTxF.setText(String.valueOf(port));
        hostTxF.setText(host);
        userTxF.setText(userID);
        setVisible(true);

        sendB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send(messageTxF.getText());
                //display(messageTxF.getText() + "\n");
                messageTxF.setText("");
            }
        });

        openB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                login(hostTxF.getText(), portTxF.getText(), userID);
            }
        });

        closeB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                logoff();
            }
        });

        quitB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                quit();
            }
        });

        whoB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send("#who");
            }
        });

        pmB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send("#pm " + whoCB.getSelectedItem() + " " + messageTxF.getText());
            }
        });

        tttB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                send("#ttt " +  userID + " " + whoCB.getSelectedItem());
            }
        });

        try {
            client = new ChatClient(host, port, this);
        } catch (IOException exception) {
            System.out.println("Error: Can't setup connection!!!!"
                    + " Terminating client.");
            System.exit(1);
        }


    }

    public static void main(String[] args) {
        String host = "";
        int port = 0;  //The port number
        String userID = "";
        try {
            host = args[0];
            port = Integer.parseInt(args[1]);
        } catch (ArrayIndexOutOfBoundsException e) {
            host = "localhost";
            port = DEFAULT_PORT;
        }
        try {
            userID = args[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            userID = "guest";
        }

        GUIConsole console = new GUIConsole(host, port, userID);
    }

    public void display(String message) {
        messageList.insert(message + "\n", 0);
    }

    public void displayUserList(ArrayList<String> userList, String roomName) {
        whoCB.removeAllItems();
        for (String user : userList) {
            whoCB.addItem(user);
        }
    }

    public void send(String message) {
        client.handleMessageFromClientUI(message);
    }

    public void login(String host, String port, String userID) {
        client.handleMessageFromClientUI("#setHost " + host);
        client.handleMessageFromClientUI("#setPort " + port);
        client.handleMessageFromClientUI("#login " + userID);
    }

    public void logoff() {
        client.handleMessageFromClientUI("#logoff");
    }

    public void quit() {
        client.handleMessageFromClientUI("#quit");
    }
}
