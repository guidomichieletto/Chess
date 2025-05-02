package chess;

import chess.pieces.*;

import java.awt.Point;
import java.util.concurrent.Semaphore;

public class Game {
    public static final int COLS = 8;
    public static final int ROWS = 8;

    private Color currentPlayer = Color.WHITE;
    private Piece[][] board = new Piece[COLS][ROWS];
    private Semaphore started = new Semaphore(0);

    private ClientConnection white;
    private ClientConnection black;

    public Game() {
        // chess.Game init
        initFirstRow(0, Color.BLACK);
        for(int i = 0; i < COLS; i++) board[i][1] = new Pawn(this, Color.BLACK);
        initFirstRow(7, Color.WHITE);
        for(int i = 0; i < COLS; i++) board[i][6] = new Pawn(this, Color.WHITE);
    }

    private void initFirstRow(int row, Color color) {
        board[0][row] = new Rook(this, color);
        board[1][row] = new Knight(this, color);
        board[2][row] = new Bishop(this, color);
        board[3][row] = new Queen(this, color);
        board[4][row] = new King(this, color);
        board[5][row] = new Bishop(this, color);
        board[6][row] = new Knight(this, color);
        board[7][row] = new Rook(this, color);
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public ClientConnection getWhite() {
        return white;
    }

    public ClientConnection getBlack() {
        return black;
    }

    public void setWhite(ClientConnection white) {
        this.white = white;
    }

    public void setBlack(ClientConnection black) {
        this.black = black;
    }

    public void start() {
        started.release();
        started.release();
    }

    public Semaphore getStartSemaphore() {
        return started;
    }

    public Piece getPiece(int x, int y) {
        return board[x][y];
    }

    public void movePiece(Point start, Point end) throws Exception {
        if(board[start.x][start.y] == null) throw new Exception("Nessuna pedina sulla cella selezionata");

        // Check if the piace can reach the end cell
        if(!board[start.x][start.y].legalMove(start, end)) throw new Exception("Movimento non ammesso");

        board[end.x][end.y] = board[start.x][start.y];
        board[start.x][start.y] = null;
    }

    public void nextTurn() {

        if(currentPlayer == Color.WHITE) {
            currentPlayer = Color.BLACK;
            black.sendTrigger();
        }
        else {
            currentPlayer = Color.WHITE;
            white.sendTrigger();
        }
    }

    public String toCSV() {
        String str = "";

        for(int y = 0; y < board.length; y++) {
            for(int x = 0; x < board.length; x++) {
                if(board[x][y] != null) str += Character.toString(board[x][y].getColor().symbol) + "," + Character.toString(board[x][y].getSymbol()) + ";";
                else str += ";";
            }

            str += '\n';
        }

        return str;
    }
}
