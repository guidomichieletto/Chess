import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Objects;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessBoardGUI extends JFrame {
    private static final int BOARD_SIZE = 8;
    private boolean areYouWhite = true;
    JPanel boardPanel;
    JPanel mainPanel;

    private JPanel selectedSquare = null;
    private Color originalColor = null;
    private String selectedName = null;

    int[] arrayMovimento = new int[2];

    private ClientTCP client;
    public ChessBoardGUI(ClientTCP client, boolean areYouWhite) {
        this.client = client;
        this.areYouWhite = areYouWhite;

        for (int i = 0; i < arrayMovimento.length; i++) {
            arrayMovimento[i] = 0;
        }

        setTitle("Scacchiera");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        boardPanel = new JPanel(new GridLayout(BOARD_SIZE, BOARD_SIZE));
        initializeBoard(boardPanel);
        placePieces(boardPanel);

        mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(createRowNumbers(), BorderLayout.WEST);
        mainPanel.add(createColumnLetters(), BorderLayout.NORTH);
        mainPanel.add(boardPanel, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
        startListeningThread();
    }
    private void startListeningThread() {
        new Thread(() -> {
            try {
                while (true) {
                    String message = client.receiveMessage();
                    SwingUtilities.invokeLater(() -> handleServerMessage(message));
                }
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Connessione al server persa.", "Errore", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
    }
    private void updateBoardFromServer(String[] rows) {
        for (int i = 0; i < BOARD_SIZE; i++) {
            for (int j = 0; j < BOARD_SIZE; j++) {
                JPanel square = (JPanel) boardPanel.getComponent(convertCoordinate(i) * BOARD_SIZE + convertCoordinate(j));
                square.removeAll();
                square.revalidate();
                square.repaint();
            }
        }

        for (int i = 0; i < BOARD_SIZE && i < rows.length; i++) {
            String[] pieces = rows[i].split(";", -1);
            for (int j = 0; j < BOARD_SIZE && j < pieces.length; j++) {
                if (!pieces[j].isEmpty()) {
                    addPieceToSquare(boardPanel, convertCoordinate(i), convertCoordinate(j), getPieceImageName(pieces[j]));
                }
            }
        }
    }


    private void handleServerMessage(String message) {
        if (message == null) return;

        if (message.equals("YOURTURN")) {
            JOptionPane.showMessageDialog(this, "È il tuo turno!", "Turno", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        if (message.equals("OK")) {
            client.sendMessage("GETBOARD");
            return;
        }

        if (message.equals("NOTYOURTURN")) {
            JOptionPane.showMessageDialog(this, "Non è il tuo turno", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (message.equals("NOTYOURPIECE")) {
            JOptionPane.showMessageDialog(this, "Non è il tuo pezzo", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }
        if (message.equals("UNALLOWEDMOVE")) {
            JOptionPane.showMessageDialog(this, "Non è concesso questa mossa", "Errore", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (message.startsWith("BOARD|")) {
            String[] rows = message.substring(6).split("\\|");
            updateBoardFromServer(rows);
            return;
        }
        if (message.startsWith("MOVES|")) {
            String[] moves = message.substring(6).split(";");
            Color highlightColor = (selectedSquare == null) ? Color.ORANGE : Color.GREEN;
            for (String move : moves) {
                if (move.isEmpty()) continue;
                String[] coordinates = move.split(",");
                int x = convertCoordinate(Integer.parseInt(coordinates[0]));
                int y = convertCoordinate(Integer.parseInt(coordinates[1]));
                int i = y * BOARD_SIZE + x;
                JPanel targetSquare = (JPanel) boardPanel.getComponent(i);
                targetSquare.setBackground(highlightColor);
            }
            return;
        }

        System.out.println("Messaggio sconosciuto: " + message);
    }

    private void movePiece(int startX, int startY, int endX, int endY) {
        client.sendMessage("MOVE," + convertCoordinate(startX) + "," + convertCoordinate(startY) + "," + convertCoordinate(endX) + "," + convertCoordinate(endY));
    }


    public void setAreYouWhite(boolean areYouWhite) {
        this.areYouWhite = areYouWhite;
    }


    private void initializeBoard(JPanel boardPanel) {
        boolean isWhite = true;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JPanel square = new JPanel(new BorderLayout());
                if (isWhite) {
                    square.setBackground(Color.WHITE);
                } else {
                    square.setBackground(Color.LIGHT_GRAY);
                }
                int finalRow = row;
                int finalCol = col;
                square.addMouseListener(new MouseAdapter() {
                    @Override
                    public void mouseClicked(MouseEvent e) {
                        if (arrayMovimento[0] != 0 && arrayMovimento[1] != 0) {
                            int startX = arrayMovimento[0];
                            int startY = arrayMovimento[1];
                            System.out.println("Row: " + finalRow + ", Col: " + finalCol);

                            movePiece(startX, startY, finalCol, finalRow);

                            arrayMovimento[0] = 0;
                            arrayMovimento[1] = 0;

                            selectedSquare.setBackground(originalColor);
                            selectedSquare = null;
                            selectedName = null;
                            resetBoardColors();
                        }
                    }

                    @Override
                    public void mouseEntered(MouseEvent e) {
                        square.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
                    }
                });
                boardPanel.add(square);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
    }
    private String getPieceImageName(String pieceCode) {
        return switch (pieceCode) {
            case "B,P" -> "Pawn_black";
            case "W,P" -> "Pawn_white";
            case "B,R" -> "Rook_black";
            case "W,R" -> "Rook_white";
            case "B,N" -> "Knight_black";
            case "W,N" -> "Knight_white";
            case "B,B" -> "Bishop_black";
            case "W,B" -> "Bishop_white";
            case "B,Q" -> "Queen_black";
            case "W,Q" -> "Queen_white";
            case "B,K" -> "King_black";
            case "W,K" -> "King_white";
            default -> throw new IllegalArgumentException("Codice pezzo non valido: " + pieceCode);
        };
    }

    private void placePieces(JPanel boardPanel) {
        if (areYouWhite) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                addPieceToSquare(boardPanel, 1, col, "Pawn_black");
                addPieceToSquare(boardPanel, 6, col, "Pawn_white");
            }
            addPieceToSquare(boardPanel, 0, 0, "Rook_black");
            addPieceToSquare(boardPanel, 0, 7, "Rook_black");
            addPieceToSquare(boardPanel, 7, 0, "Rook_white");
            addPieceToSquare(boardPanel, 7, 7, "Rook_white");
            addPieceToSquare(boardPanel, 0, 1, "Knight_black");
            addPieceToSquare(boardPanel, 0, 6, "Knight_black");
            addPieceToSquare(boardPanel, 7, 1, "Knight_white");
            addPieceToSquare(boardPanel, 7, 6, "Knight_white");
            addPieceToSquare(boardPanel, 0, 2, "Bishop_black");
            addPieceToSquare(boardPanel, 0, 5, "Bishop_black");
            addPieceToSquare(boardPanel, 7, 2, "Bishop_white");
            addPieceToSquare(boardPanel, 7, 5, "Bishop_white");
            addPieceToSquare(boardPanel, 0, 3, "Queen_black");
            addPieceToSquare(boardPanel, 7, 3, "Queen_white");
            addPieceToSquare(boardPanel, 0, 4, "King_black");
            addPieceToSquare(boardPanel, 7, 4, "King_white");
        } else {
            for (int col = 0; col < BOARD_SIZE; col++) {
                addPieceToSquare(boardPanel, 1, col, "Pawn_white");
                addPieceToSquare(boardPanel, 6, col, "Pawn_black");
            }
            addPieceToSquare(boardPanel, 0, 0, "Rook_white");
            addPieceToSquare(boardPanel, 0, 7, "Rook_white");
            addPieceToSquare(boardPanel, 7, 0, "Rook_black");
            addPieceToSquare(boardPanel, 7, 7, "Rook_black");
            addPieceToSquare(boardPanel, 0, 1, "Knight_white");
            addPieceToSquare(boardPanel, 0, 6, "Knight_white");
            addPieceToSquare(boardPanel, 7, 1, "Knight_black");
            addPieceToSquare(boardPanel, 7, 6, "Knight_black");
            addPieceToSquare(boardPanel, 0, 2, "Bishop_white");
            addPieceToSquare(boardPanel, 0, 5, "Bishop_white");
            addPieceToSquare(boardPanel, 7, 2, "Bishop_black");
            addPieceToSquare(boardPanel, 7, 5, "Bishop_black");
            addPieceToSquare(boardPanel, 0, 4, "Queen_white");
            addPieceToSquare(boardPanel, 7, 4, "Queen_black");
            addPieceToSquare(boardPanel, 0, 3, "King_white");
            addPieceToSquare(boardPanel, 7, 3, "King_black");
        }
    }
    private int convertCoordinate(int coord) {
        return areYouWhite ? coord : BOARD_SIZE - 1 - coord;
    }

    /*private void movePiecee(int startX, int startY, int endX, int endY, String namePiece) {
        JPanel startSquare = (JPanel) boardPanel.getComponent(startY * BOARD_SIZE + startX);
        JPanel endSquare = (JPanel) boardPanel.getComponent(endY * BOARD_SIZE + endX);

        startSquare.removeAll();
        startSquare.revalidate();
        startSquare.repaint();

        endSquare.removeAll();
        endSquare.revalidate();
        endSquare.repaint();

        addPieceToSquare(boardPanel, endY, endX, namePiece);
    }*/

    private void addPieceToSquare(JPanel boardPanel, int row, int col, String pieceName) {
        int index = row * BOARD_SIZE + col;
        JPanel square = (JPanel) boardPanel.getComponent(index);

        square.removeAll();
        square.revalidate();

        ImageIcon icon = new ImageIcon(Objects.requireNonNull(getClass().getResource("img/" + pieceName + ".png")));

        if (icon.getIconWidth() == -1) {
            System.err.println("Immagine non trovata: " + "img/" + pieceName + ".png");
            return;
        }

        Image image = icon.getImage().getScaledInstance(100, 100, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);

        JLabel piece = new JLabel(icon);
        piece.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                resetBoardColors();
                boolean isMyPiece = (areYouWhite && pieceName.contains("_white")) ||
                        (!areYouWhite && pieceName.contains("_black"));

                if (selectedSquare == null) {
                    if (!isMyPiece) {
                        client.sendMessage("AVAILABLEMOVES," + convertCoordinate(col) + "," + convertCoordinate(row));
                        selectedSquare = null;
                        selectedName = null;
                        arrayMovimento[0] = 0;
                        arrayMovimento[1] = 0;
                        return;
                    }
                    selectedSquare = square;
                    originalColor = square.getBackground();
                    selectedName = pieceName;
                    arrayMovimento[0] = col;
                    arrayMovimento[1] = row;
                    square.setBackground(Color.YELLOW);
                    client.sendMessage("AVAILABLEMOVES," + convertCoordinate(col) + "," + convertCoordinate(row));
                    return;
                }

                if (selectedSquare != null) {
                    if (selectedSquare == square) {
                        selectedSquare.setBackground(originalColor);
                        selectedSquare = null;
                        selectedName = null;
                        arrayMovimento[0] = 0;
                        return;
                    }
                    if ((selectedName.contains("_white") && pieceName.contains("_black")) ||
                            (selectedName.contains("_black") && pieceName.contains("_white"))) {
                        int startX = convertCoordinate(arrayMovimento[0]);
                        int startY = convertCoordinate(arrayMovimento[1]);
                        movePiece(startX, startY, convertCoordinate(col), convertCoordinate(row));
                        selectedSquare.setBackground(originalColor);
                        selectedSquare = null;
                        selectedName = null;
                        arrayMovimento[0] = 0;
                        arrayMovimento[1] = 0;
                        resetBoardColors();
                    } else {
                        selectedSquare.setBackground(originalColor);
                        selectedSquare = square;
                        originalColor = square.getBackground();
                        selectedName = pieceName;
                        arrayMovimento[0] = col;
                        arrayMovimento[1] = row;
                        square.setBackground(Color.YELLOW);
                        client.sendMessage("AVAILABLEMOVES," + convertCoordinate(col) + "," + convertCoordinate(row));
                    }
                }
            }

            public void mouseEntered(MouseEvent e) {
                piece.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        square.add(piece, BorderLayout.CENTER);
    }

    private JPanel createRowNumbers() {
        JPanel rowLabels = new JPanel(new GridLayout(BOARD_SIZE, 1));
        rowLabels.setBackground(Color.getHSBColor(24f / 360f, 0.69f, 0.64f));
        for (int i = 0; i < BOARD_SIZE; i++) {
            JLabel label = new JLabel(String.valueOf(BOARD_SIZE - i), SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            rowLabels.add(label);
        }
        return rowLabels;
    }

    private JPanel createColumnLetters() {
        JPanel columnLabels = new JPanel(new GridLayout(1, BOARD_SIZE));
        columnLabels.setBackground(Color.getHSBColor(24f / 360f, 0.69f, 0.64f));
        for (char c = 'A'; c < 'A' + BOARD_SIZE; c++) {
            JLabel label = new JLabel(String.valueOf(c), SwingConstants.CENTER);
            label.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
            columnLabels.add(label);
        }
        return columnLabels;
    }
    private void resetBoardColors() {
        boolean isWhite = true;
        for (int row = 0; row < BOARD_SIZE; row++) {
            for (int col = 0; col < BOARD_SIZE; col++) {
                JPanel square = (JPanel) boardPanel.getComponent(row * BOARD_SIZE + col);
                square.setBackground(isWhite ? Color.WHITE : Color.LIGHT_GRAY);
                isWhite = !isWhite;
            }
            isWhite = !isWhite;
        }
    }

}