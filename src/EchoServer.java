import java.io.IOException;
import java.util.ArrayList;

public class EchoServer extends AbstractServer {
    //Class variables *************************************************

    /**
     * The default port to listen on.
     */
    final public static int DEFAULT_PORT = 5555;

    String player1Id = "";
    String player2Id = "";

    //Constructors ****************************************************

    /*
     * Constructs an instance of the echo server.
     *
     * @param port The port number to connect on.
     */
    public EchoServer(int port) {
        super(port);
//        try {
//            this.listen(); //Start listening for connections
//        } catch (Exception ex) {
//            System.out.println("ERROR - Could not listen for clients!");
//        }
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
        int port; //Port to listen on
        try {
            port = Integer.parseInt(args[0]); //Set port to 5556
        } catch (ArrayIndexOutOfBoundsException oob) {
            port = DEFAULT_PORT;
        }
        EchoServer sv = new EchoServer(port);
        try {
            sv.listen(); //Start listening for connections
        } catch (Exception ex) {
            System.out.println("ERROR - Could not listen for clients!");
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
        } else if (env.getId().equals("ttt")) {
            TicTacToe ttt = (TicTacToe) env.getContent();
            this.processTicTacToe(ttt);
        } else if (env.getId().equals("tttDecline")) {
            Thread[] clientThreadList = getClientConnections();
            for (Thread thread : clientThreadList) {
                ConnectionToClient target = (ConnectionToClient) thread;
                if (target.getInfo("userid").equals(player1Id)) {
                    TicTacToe ttt = (TicTacToe) target.getInfo("ttt");
                    ttt.setGameState(2);
//                    processTicTacToe(ttt);
                    try {
                        target.sendToClient(new Envelope("ttt", "", ttt));
                    } catch (IOException e) {
                        System.out.println("Fail to sent #tttAccept.");
                    }
                }
            }
        } else if (env.getId().equals("tttAccept")) {
            Thread[] clientThreadList = getClientConnections();
            for (Thread thread : clientThreadList) {
                ConnectionToClient target = (ConnectionToClient) thread;
                if (target.getInfo("userid").equals(player1Id)) {
                    TicTacToe ttt = (TicTacToe) target.getInfo("ttt");
                    ttt.setGameState(3);
                    try {
                        target.sendToClient(new Envelope("ttt", "", ttt));
                    } catch (IOException e) {
                        System.out.println("Fail to sent #tttAccept.");
                    }
                }
            }
        }
    }

    private void processTicTacToe(TicTacToe ttt) {
        int gameState = ttt.getGameState();
        player1Id = ttt.getPlayer1();
        player2Id = ttt.getPlayer2();
        // Get player1, player2 ConnectionToClient for reuse
        ConnectionToClient player1 = null;
        ConnectionToClient player2 = null;
        Thread[] clientThreadList = getClientConnections();
        for (Thread thread : clientThreadList) {
            ConnectionToClient target = (ConnectionToClient) thread;
            if (target.getInfo("userid").equals(player1Id)) {
                player1 = target;
            }
            if (target.getInfo("userid").equals(player2Id)) {
                player2 = target;
            }
        }
        try {
            switch (gameState) {
                // invite
                case 1 -> {
                    player1.setInfo("ttt", ttt);
                    player2.setInfo("ttt", ttt);
                    player2.sendToClient(new Envelope("ttt", "", ttt));
                }
                // decline
                case 2 -> player1.sendToClient(new Envelope("ttt", "", ttt));
                // play
                case 3 -> {
                    player1.setInfo("ttt", ttt);
                    player2.setInfo("ttt", ttt);
                    if (ttt.getActivePlayer() == 1) {
                        ttt.setActivePlayer(2);
                        player2.sendToClient(new Envelope("ttt", "", ttt));
                    } else if (ttt.getActivePlayer() == 2) {
                        ttt.setActivePlayer(1);
                        player1.sendToClient(new Envelope("ttt", "", ttt));
                    }
                }
                // has winner
                case 4 -> {
                    if (ttt.getActivePlayer() == 1) {
                        // player 1 win - Winner = 1;
                        player1.sendToClient(new Envelope("ttt", "", ttt));
                        ttt.setActivePlayer(2);
                        player2.sendToClient(new Envelope("ttt", "", ttt));
                    } else if (ttt.getActivePlayer() == 2) {
                        // player 2 win - Winner = 2;
                        player1.sendToClient(new Envelope("ttt", "", ttt));
                        ttt.setActivePlayer(1);
                        player2.sendToClient(new Envelope("ttt", "", ttt));
                    }
//                    player1.sendToClient(new Envelope("ttt", "", ttt));
//                    player2.sendToClient(new Envelope("ttt", "", ttt));
                }
                // tie
                case 5 -> {
                    player1.sendToClient(new Envelope("ttt", "", ttt));
                    player2.sendToClient(new Envelope("ttt", "", ttt));
                    break;
                }

            }
        } catch (Exception ignored) {
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

    /*
     * This method overrides the one in the superclass.  Called
     * when the server starts listening for connections.
     */
    protected void serverStarted() {
        System.out.println
                ("Server listening for connections on port " + getPort());
    }

    //Class methods ***************************************************

    /*
     * This method overrides the one in the superclass.  Called
     * when the server stops listening for connections.
     */
    protected void serverStopped() {
        System.out.println
                ("Server has stopped listening for connections.");
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
