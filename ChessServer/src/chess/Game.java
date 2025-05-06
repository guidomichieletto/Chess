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

        // Pawn enpassant
        if (board[start.x][start.y] instanceof Pawn) {
            Pawn movingPawn = (Pawn) board[start.x][start.y];
            movingPawn.enpassantable = (Math.abs(start.y - end.y) == 2);

            if (start.x != end.x && board[end.x][end.y] == null) {
                int capturedY = (movingPawn.getColor() == Color.WHITE) ? end.y + 1 : end.y - 1;
                board[end.x][capturedY] = null;
            }
        }

        board[end.x][end.y] = board[start.x][start.y];
        board[start.x][start.y] = null;

        checkWin();
    }

    private void checkWin() {
        boolean whiteKing = false;
        boolean blackKing = false;

        for(int y = 0; y < board.length; y++) {
            for(int x = 0; x < board.length; x++) {
                if(board[x][y] != null) {
                    if(board[x][y].getClass() == King.class) {
                        if(board[x][y].getColor() == Color.WHITE) whiteKing = true;
                        else blackKing = true;
                    }
                }
            }
        }

        if(!whiteKing) {
            black.sendTrigger("WIN");
            white.sendTrigger("LOSE");
        } else if(!blackKing) {
            white.sendTrigger("WIN");
            black.sendTrigger("LOSE");
        }
    }

    public void nextTurn() {

        if(currentPlayer == Color.WHITE) {
            currentPlayer = Color.BLACK;
            black.sendTrigger("YOURTURN");
        }
        else {
            currentPlayer = Color.WHITE;
            white.sendTrigger("YOURTURN");
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
