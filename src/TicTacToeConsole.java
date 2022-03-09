import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class TicTacToeConsole extends JFrame {

    ChatIF clientUI;
    TicTacToe game;
    private JButton b1 = new JButton("-");
    private JButton b2 = new JButton("-");
    private JButton b3 = new JButton("-");
    private JButton b4 = new JButton("-");
    private JButton b5 = new JButton("-");
    private JButton b6 = new JButton("-");
    private JButton b7 = new JButton("-");
    private JButton b8 = new JButton("-");
    private JButton b9 = new JButton("-");

    public TicTacToeConsole(ChatIF clientUI) {
        super("Tic Tac Toe");
        this.setLayout(new GridLayout(3, 3, 3, 3));
        add(b1);
        add(b2);
        add(b3);
        add(b4);
        add(b5);
        add(b6);
        add(b7);
        add(b8);
        add(b9);
        setSize(300, 300);
        setVisible(true);

        addActionListener();
        this.clientUI = clientUI;
    }


    public static void main(String[] args) {
        TicTacToeConsole ttt = new TicTacToeConsole(null);
    }

    public void updateBoard(char[][] board) {
        b1.setText(board[0][0] + "");
        b2.setText(board[0][1] + "");
        b3.setText(board[0][2] + "");
        b4.setText(board[1][0] + "");
        b5.setText(board[1][1] + "");
        b6.setText(board[1][2] + "");
        b7.setText(board[2][0] + "");
        b8.setText(board[2][1] + "");
        b9.setText(board[2][2] + "");
    }

//    public boolean checkWin(char[][] board) {
//        return (board[0][0] != '\u0000' && board[0][0] == board[0][1] && board[0][0] == board[0][2]) //1-2-3
//                || (board[1][0] != '\u0000' && board[1][0] == board[1][1] && board[1][0] == board[1][2]) //4-5-6
//                || (board[2][0] != '\u0000' && board[2][0] == board[2][1] && board[2][0] == board[2][2]) //7-8-9
//                || (board[0][0] != '\u0000' && board[0][0] == board[1][0] && board[0][0] == board[2][0]) //1-4-7
//                || (board[0][1] != '\u0000' && board[0][1] == board[1][1] && board[0][1] == board[2][1]) //2-5-8
//                || (board[0][2] != '\u0000' && board[0][2] == board[1][2] && board[0][2] == board[2][2]) //3-6-9
//                || (board[0][0] != '\u0000' && board[0][0] == board[1][1] && board[0][0] == board[2][2]) //1-5-9
//                || (board[0][2] != '\u0000' && board[0][2] == board[1][1] && board[0][2] == board[2][0]); //3-5-7
//        //for example
//    }

    private void addActionListener() {
        b1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMove(1);
            }
        });
        b2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMove(2);
            }
        });
        b3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMove(3);
            }
        });
        b4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMove(4);
            }
        });
        b5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMove(5);
            }
        });
        b6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMove(6);
            }
        });
        b7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMove(7);
            }
        });
        b8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMove(8);
            }
        });
        b9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                processMove(9);
            }
        });
    }

    private void processMove(int move) {
        game.updateBoard(move); //buttons[i].setText("X");
        updateBoard(game.getBoard());
        game.checkWin();
        // isWinner != null means we have a winner
        if (game.getWinner() != 0) {
            game.setGameState(4);
            sendGameToServer();
        } else if (game.checkDraw()) {
            game.setGameState(5);
            sendGameToServer();
        } else sendGameToServer();
    }

    public void sendGameToServer() {
        ChatClient client = ((GUIConsole) clientUI).client;
        try {
            client.sendToServer(new Envelope("ttt", "", game));
        } catch (IOException e) {
            clientUI.display("Failed to acquire user list.");
        }
    }
}
