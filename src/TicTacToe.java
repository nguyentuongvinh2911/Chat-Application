import java.io.Serializable;

public class TicTacToe implements Serializable {
    private String Player1;
    private String Player2;
    private int activePlayer;
    private int gameState;
    private int winner = 0;
    private char[][] board = new char[3][3];

    public TicTacToe() {
    }

    public TicTacToe(String player1, String player2, int activePlayer, int gameState) {
        this.Player1 = player1;
        this.Player2 = player2;
        this.activePlayer = activePlayer;
        this.gameState = gameState;
    }

    public String getPlayer1() {
        return Player1;
    }

    public void setPlayer1(String player1) {
        Player1 = player1;
    }

    public String getPlayer2() {
        return Player2;
    }

    public void setPlayer2(String player2) {
        Player2 = player2;
    }

    public int getActivePlayer() {
        return activePlayer;
    }

    public void setActivePlayer(int activePlayer) {
        this.activePlayer = activePlayer;
    }

    public int getWinner() {
        return winner;
    }

    public void setWinner(int winner) {
        this.winner = winner;
    }

    public int getGameState() {
        return gameState;
    }

    public void setGameState(int gameState) {
        this.gameState = gameState;
    }

    public char[][] getBoard() {
        return board;
    }

    public void setBoard(char[][] board) {
        this.board = board;
    }

    public void checkWin() {
        if ((board[0][0] != '\u0000' && board[0][0] == board[0][1] && board[0][0] == board[0][2]) //1-2-3
                || (board[1][0] != '\u0000' && board[1][0] == board[1][1] && board[1][0] == board[1][2]) //4-5-6
                || (board[2][0] != '\u0000' && board[2][0] == board[2][1] && board[2][0] == board[2][2]) //7-8-9
                || (board[0][0] != '\u0000' && board[0][0] == board[1][0] && board[0][0] == board[2][0]) //1-4-7
                || (board[0][1] != '\u0000' && board[0][1] == board[1][1] && board[0][1] == board[2][1]) //2-5-8
                || (board[0][2] != '\u0000' && board[0][2] == board[1][2] && board[0][2] == board[2][2]) //3-6-9
                || (board[0][0] != '\u0000' && board[0][0] == board[1][1] && board[0][0] == board[2][2]) //1-5-9
                || (board[0][2] != '\u0000' && board[0][2] == board[1][1] && board[0][2] == board[2][0])) //3-5-7)
        {
            winner = activePlayer;
        }
    }

    public boolean checkDraw() {
        // board is full
        return (board[0][0] != '\u0000' && board[0][1] != '\u0000' && board[0][2] != '\u0000'
                && board[1][0] != '\u0000' && board[1][1] != '\u0000' && board[1][2] != '\u0000'
                && board[2][0] != '\u0000' && board[2][1] != '\u0000' && board[2][2] != '\u0000'
                && winner == 0); // No winner yet
    }

    public void updateBoard(int move) {
        char text;
        if (activePlayer == 1) text = 'X';
        else text = 'O';
        switch (move) {
            case 1 -> {
                if (board[0][0] == '\u0000') board[0][0] = text;
            }
            case 2 -> {
                if (board[0][1] == '\u0000') board[0][1] = text;
            }
            case 3 -> {
                if (board[0][2] == '\u0000') board[0][2] = text;
            }
            case 4 -> {
                if (board[1][0] == '\u0000') board[1][0] = text;
            }
            case 5 -> {
                if (board[1][1] == '\u0000') board[1][1] = text;
            }
            case 6 -> {
                if (board[1][2] == '\u0000') board[1][2] = text;
            }
            case 7 -> {
                if (board[2][0] == '\u0000') board[2][0] = text;
            }
            case 8 -> {
                if (board[2][1] == '\u0000') board[2][1] = text;
            }
            case 9 -> {
                if (board[2][2] == '\u0000') board[2][2] = text;
            }
        }
    }
}
