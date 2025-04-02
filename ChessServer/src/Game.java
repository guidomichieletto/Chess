public class Game {
    private final int COLS = 8;
    private final int ROWS = 8;

    private Color currentPlayer = Color.WHITE;
    private Piece[][] board = new Piece[COLS][ROWS];

    public Game() {
        // Game init
        initFirstRow(0, Color.BLACK);
        for(int i = 0; i < COLS; i++) board[i][1] = new Piece(PieceType.PAWN, Color.BLACK);
        initFirstRow(7, Color.WHITE);
        for(int i = 0; i < COLS; i++) board[i][6] = new Piece(PieceType.PAWN, Color.WHITE);
    }

    private void initFirstRow(int row, Color color) {
        board[0][row] = new Piece(PieceType.ROOK, color);
        board[1][row] = new Piece(PieceType.KNIGHT, color);
        board[2][row] = new Piece(PieceType.BISHOP, color);
        board[3][row] = new Piece(PieceType.QUEEN, color);
        board[4][row] = new Piece(PieceType.KING, color);
        board[5][row] = new Piece(PieceType.BISHOP, color);
        board[6][row] = new Piece(PieceType.KNIGHT, color);
        board[7][row] = new Piece(PieceType.ROOK, color);
    }

    public void movePiece(int startX, int startY, int endX, int endY) throws Exception {
        if(board[startX][startY] == null) throw new Exception("Nessuna pedina sulla cella selezionata");

        // Check if the piace can reach the end cell
        PieceType pieceType = board[startX][startY].getType();
        if(pieceType == PieceType.PAWN) {

        } else if(pieceType == PieceType.ROOK) {
            if(startX != endX && startY != endY) throw new Exception("Movimento torre non ammesso");
        } else if(pieceType == PieceType.KING) {
            if()
        }

    }

    private void nextTurn() {
        if(currentPlayer == Color.WHITE) currentPlayer = Color.BLACK;
        else currentPlayer = Color.WHITE;
    }
}
