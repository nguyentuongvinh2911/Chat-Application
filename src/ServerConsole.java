
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

import static java.lang.System.exit;

public class ServerConsole extends AbstractServer {

    //Class variables *************************************************
    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;
    public static int port; //Port to listen on
    public static Scanner scanner = new Scanner(System.in);

    //Constructors ****************************************************

    /*
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public ServerConsole(int port) {
        super(port);
        try {
            this.listen(); //Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
        }
    }

    //Instance methods ************************************************

    /*
     * This method is responsible for the creation of
     * the server instance (there is no UI in this phase).
     *
     * @param args[0] The port number to listen on.  Defaults to 5555
     *          if no argument is entered.
     */
    public static void main(String[] args) {
        String command;
        while (true) {
            command = scanner.nextLine();
            if (command.contains("#setPort")) {
                try {
                    port = (Integer.parseInt(command.substring(8).trim()));
                } catch (ArrayIndexOutOfBoundsException oob) {
                    port = DEFAULT_PORT;
                }
            } else if (command.equals("#start")) {
                ServerConsole sv = new ServerConsole(port);
                String serverCommand;
                while (true) {
                    serverCommand = scanner.nextLine();
                    sv.handleServerCommand(serverCommand);
                }
            }

        }
    }

    public void handleServerCommand(String command) {
        if (command.contains("<ADMIN>")) {
            sendToAllClients(command);
        } else if (command.equals("#stop")) {
            try {
                close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else if (command.equals("#quit")) {
            exit(0);
        } else if (command.contains("#ison")) {
            String userName = command.substring(5).trim();
            Thread[] clientThreadList = getClientConnections();
            for (Thread thread : clientThreadList) {
                ConnectionToClient target = (ConnectionToClient) thread;
                if (target.getInfo("userid").equals(userName)) {
                    String roomName = target.getInfo("room").toString();
                    System.out.println(userName + " is on in room " + roomName);
                    return;
                }
            }
            System.out.println(userName + " is not logged in.");
        } else if (command.equals("#userstatus")) {
            Thread[] clientThreadList = getClientConnections();
            for (Thread thread : clientThreadList) {
                ConnectionToClient target = (ConnectionToClient) thread;
                String userName = target.getInfo("userid").toString();
                String roomName = target.getInfo("room").toString();
                System.out.println(userName + " - " + roomName);
            }
        }
    }

    /*
     * This method handles any messages received from the client.
     *
     * @param msg    The message received from the client.
     * @param client The connection from which the message originated.
     */
    public void handleMessageFromClient(Object msg, ConnectionToClient client) {
        if (msg instanceof Envelope) {
            Envelope env = (Envelope) msg;
            handleCommandFromClient(env, client);
        } else {
            System.out.println("Message received: " + msg + " from " + client);
            String userID = client.getInfo("userid").toString();
            this.sendToAllClientsInRoom(msg, client);
        }
    }

    public void handleCommandFromClient(Envelope env, ConnectionToClient client) {
        if (env.getId().equals("login")) {
            String userID = env.getContent().toString();
            client.setInfo("userid", userID);
            client.setInfo("room", "lobby");
        } else if (env.getId().equals("join")) {
            // Set info "room" = roomName
            String roomName = env.getContent().toString();
            client.setInfo("room", roomName);
        } else if (env.getId().equals("pm")) {
            String message = env.getContent().toString();
            String target = env.getArg();
            sendToAClient(message, target, client);
        } else if (env.getId().equals("yell")) {
            String message = env.getContent().toString();
            String userID = client.getInfo("userid").toString();
            sendToAllClients(userID + " yells: " + message);
        } else if (env.getId().equals("who")) {
            this.sendRoomListToClient(client);
        }
    }

    public void sendRoomListToClient(ConnectionToClient client) {
        Envelope e = new Envelope();
        e.setId("who");
        ArrayList<String> userList = new ArrayList<String>();
        String room = client.getInfo("room").toString();
        Thread[] clientThreadList = getClientConnections();
        for (Thread thread : clientThreadList) {
            ConnectionToClient target = (ConnectionToClient) thread;
            if (target.getInfo("room").equals(room)) {
                userList.add(target.getInfo("userid").toString());
            }
        }
        e.setArg(room);
        e.setContent(userList);
        try {
            client.sendToClient(e);
        } catch (Exception ex) {
            System.out.println("Failed to send userList to client.");
        }
    }

    public void sendToAClient(Object msg, String pmTarget, ConnectionToClient client) {
        Thread[] clientThreadList = getClientConnections();
        for (Thread thread : clientThreadList) {
            ConnectionToClient target = (ConnectionToClient) thread;
            if (target.getInfo("userid").equals(pmTarget)) {
                try {
                    target.sendToClient(client.getInfo("userid").toString() + ": " + msg);
                } catch (Exception e) {
                    System.out.println("Failed to send to client.");
                }
            }
        }
    }

    public void sendToAllClientsInRoom(Object msg, ConnectionToClient client) {
        Thread[] clientThreadList = getClientConnections();
        String roomName = client.getInfo("room").toString();

        for (Thread thread : clientThreadList) {
            ConnectionToClient target = (ConnectionToClient) thread;
            if (target.getInfo("room").equals(roomName)) {
                try {
                    target.sendToClient(client.getInfo("userid").toString() + ": " + msg);
                } catch (Exception e) {
                    System.out.println("Failed to send to client.");
                }
            }
        }
    }

//    public void sendToAllClients(Object msg) {
//        Thread[] clientThreadList = getClientConnections();
//
//        for (Thread thread : clientThreadList) {
//            ConnectionToClient target = (ConnectionToClient) thread;
//            try {
//                target.sendToClient(msg);
//            } catch (Exception e) {
//                System.out.println("Failed to send to all clients.");
//            }
//        }
//    }
    /*
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted() {
        System.out.println("Server listening for connections on port " + getPort());
    }

    //Class methods ***************************************************

    /*
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        System.out.println("Server has stopped listening for connections.");
    }

    protected void clientConnected(ConnectionToClient client) {
        System.out.println("<Client Connected:" + client + ">");
    }

    protected void clientDisconnected(ConnectionToClient client) {
        System.out.println("<Client Disconnected: " + client + ">");
    }

    synchronized protected void clientException(ConnectionToClient client, Throwable exception) {
        System.out.println("Client shut down.");
    }
}
//End of EchoServer class
