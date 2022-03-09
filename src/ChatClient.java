import java.io.IOException;
import java.util.ArrayList;

/**
 * This class overrides some of the methods defined in the abstract
 * superclass in order to give more functionality to the client.
 */
public class ChatClient extends AbstractClient {
    //Instance variables **********************************************

    /**
     * The interface type variable.  It allows the implementation of
     * the display method in the client.
     */
    ChatIF clientUI;
    TicTacToeConsole tttConsole;

    //Constructors ****************************************************

    /**
     * Constructs an instance of the chat client.
     *
     * @param host     The server to connect to.
     * @param port     The port number to connect on.
     * @param clientUI The interface type variable.
     */

    public ChatClient(String host, int port, ChatIF clientUI)
            throws IOException {
        super(host, port); //Call the superclass constructor
        this.clientUI = clientUI;
        //openConnection();
    }


    //Instance methods ************************************************

    /**
     * This method handles all data that comes in from the server.
     *
     * @param msg The message from the server.
     */
    public void handleMessageFromServer(Object msg) {
        if (msg instanceof Envelope) {
            Envelope env = (Envelope) msg;
            handleCommandFromServer(env);
        } else {
            clientUI.display(msg.toString());
        }
    }

    public void handleCommandFromServer(Envelope env) {
        if (env.getId().equals("who")) {
            ArrayList<String> userList = (ArrayList<String>) env.getContent();
            String roomName = env.getArg();
            clientUI.displayUserList(userList, roomName);
        } else if (env.getId().equals("ttt")) {
            TicTacToe ttt = (TicTacToe) env.getContent();
            this.processTicTacToe(ttt);
        }
    }

    /**
     * This method handles all data coming from the UI
     *
     * @param message The message from the UI.
     */
    public void handleMessageFromClientUI(String message) {

        if (message.charAt(0) == '#') {
            handleClientCommand(message);
        } else {
            try {
                sendToServer(message);
            } catch (IOException e) {
                clientUI.display
                        ("Could not send message to server.  Terminating client.......");
                quit();
            }
        }
    }

    /**
     * This method terminates the client.
     */
    public void quit() {
        try {
            closeConnection();
        } catch (IOException ignored) {
        }
        System.exit(0);
    }

    public void connectionClosed() {
        System.out.println("Connection closed");
    }


    protected void connectionException(Exception exception) {
        System.out.println("Server has shut down");
    }


    public void handleClientCommand(String message) {

        if (message.equals("#quit")) {
            clientUI.display("Shutting Down Client");
            quit();
        }

        if (message.equals("#logoff")) {
            clientUI.display("Disconnecting from server");
            try {
                closeConnection();
            } catch (IOException ignored) {
            }
            ;
        }

        if (message.contains("#setHost")) {
            if (isConnected())
                clientUI.display("Cannot change host while connected");
            else
                setHost(message.substring(8).trim());
        }

        if (message.contains("#setPort")) {
            if (isConnected())
                clientUI.display("Cannot change port while connected");
            else
                setPort(Integer.parseInt(message.substring(8).trim()));
        }

        if (message.contains("#login")) {
            //#login Vinh
            if (isConnected())
                clientUI.display("Already connected");
            else {
                try {
                    String username = message.substring(6).trim();
                    openConnection();
                    clientUI.display("Logging in as " + username);
                    Envelope env = new Envelope("login", "", username);
                    this.sendToServer(env);
                } catch (IOException e) {
                    clientUI.display("Failed to connect to server.");
                }
            }
        }

        if (message.contains("#join")) {
            // #join roomName
            // create an envelope
            try {
                String roomName = message.substring(5).trim();
                openConnection();
                Envelope env = new Envelope("join", "", roomName);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("Failed to join a room.");
            }
        }

        if (message.contains("#pm")) {
            //#pm user message
            try {
                String targetAndMessage = message.substring(3).trim();
                //user message
                String target = targetAndMessage.substring(0, targetAndMessage.indexOf(" ")).trim();
                String msg = targetAndMessage.substring(targetAndMessage.indexOf(" ")).trim();
                openConnection();
                Envelope env = new Envelope("pm", target, msg);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("Failed to send private message.");
            }
        }

        if (message.contains("#yell")) {
            //#yell message
            try {
                String msg = message.substring(5).trim();
                openConnection();
                Envelope env = new Envelope("yell", "", msg);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("Failed to yell.");
            }
        }

        if (message.contains("#who")) {
            //#who
            try {
                Envelope env = new Envelope("who", "", "");
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("Failed to acquire user list.");
            }
        }

        if (message.contains("#ttt") && !message.equals("#tttDecline") && !message.equals("#tttAccept")) {
            //#ttt player1 player2
            String players = message.substring(4).trim();
            String player1 = players.substring(0, players.indexOf(" ")).trim();
            String player2 = players.substring(players.indexOf(" ")).trim();
            tttConsole = new TicTacToeConsole(clientUI);
            tttConsole.game = new TicTacToe(player1, player2, 1, 1);
            try {
                Envelope env = new Envelope("ttt", "", tttConsole.game);
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("Failed to acquire user list.");
            }
        }

        if (message.equals("#tttDecline")) {
            try {
                Envelope env = new Envelope("tttDecline", "", "");
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("Failed to send #tttDecline.");
            }
        }

        if (message.equals("#tttAccept")) {
            tttConsole = new TicTacToeConsole(clientUI);
            try {
                Envelope env = new Envelope("tttAccept", "", "");
                this.sendToServer(env);
            } catch (IOException e) {
                clientUI.display("Failed to send #tttAccept.");
            }
        }
    }

    private void processTicTacToe(TicTacToe ttt) {
        int gameState = ttt.getGameState();
        switch (gameState) {
            // invite
            case 1 -> {
                clientUI.display("You have been invited to play TicTacToe with " + ttt.getPlayer1() + ". \n#tttAccept to accept, #tttDecline to decline");
            }
            // decline
            case 2 -> {
                clientUI.display("Your game was declined.");
                tttConsole.dispose();
            }
            // play
            case 3 -> {
                clientUI.display("Your turn to play TicTacToe.");
                tttConsole.game = ttt;
                tttConsole.updateBoard(ttt.getBoard());
            }
            // has winner
            case 4 -> {
                if (ttt.getActivePlayer() == 1) clientUI.display("You have won!");
                if (ttt.getActivePlayer() == 2) clientUI.display("You have lost!");
                tttConsole.dispose();
            }
            // tie
            case 5 -> {
                clientUI.display("Tie game! GG!");
                tttConsole.dispose();
            }
        }
    }
}
//End of ChatClient class
