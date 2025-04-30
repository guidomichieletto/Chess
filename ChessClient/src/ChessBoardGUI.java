import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ChessBoardGUI extends JFrame {
    private static final int BOARD_SIZE = 8;
    private final Map<String, JLabel> pieceImages = new HashMap<>();
    private boolean areYouWhite = true;
    JPanel boardPanel;
    JPanel mainPanel;

    private JPanel selectedSquare = null;
    private Color originalColor = null;
    private String selectedName = null;

    int[] arrayMovimento = new int[2];

    private ClientTCP client;
    public ChessBoardGUI(ClientTCP client) {
        this.client = client;
        for(int i = 0; i<arrayMovimento.length; i++){
            arrayMovimento[i] = 0;
        }

        setTitle("Scacchiera");
        setSize(800, 800); //dimensione da scegliere in base al dispositivo
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
    }


    private void movePiece(int startX, int startY, int endX, int endY, String namePiece) {
        new Thread(() -> {
            client.sendMessage("MOVE," + startX + "," + startY + "," + endX + "," + endY);

            try {
                String response = client.receiveMessage();
                SwingUtilities.invokeLater(() -> {
                    if ("OK".equals(response)) {
                        client.sendMessage("GETBOARD");
                        try {
                            String[] boardPieces = client.receiveMessage().split(";");
                            for (int i = 0; i < BOARD_SIZE; i++){
                                for (int j = 0; j < BOARD_SIZE; j++){
                                    JPanel square = (JPanel) boardPanel.getComponent(i * BOARD_SIZE + j);
                                    square.removeAll();
                                }
                            }
                            for (int i = 0; i < BOARD_SIZE; i++){
                                for (int j = 0; j < BOARD_SIZE; j++){
                                    JPanel square = (JPanel) boardPanel.getComponent(i * BOARD_SIZE + j);
                                    square.removeAll();
                                }
                            }
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "Mossa non valida: " + response, "Errore", JOptionPane.ERROR_MESSAGE);
                    }
                });
            } catch (IOException e) {
                SwingUtilities.invokeLater(() -> JOptionPane.showMessageDialog(this, "Errore nella comunicazione con il server.", "Errore", JOptionPane.ERROR_MESSAGE));
            }
        }).start();
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
                        if (arrayMovimento[0] != 0 && arrayMovimento[1] == 0) {
                            int startX = arrayMovimento[0];
                            int startY = arrayMovimento[1];

                            movePiece(startX, startY, finalCol, finalRow, selectedName);

                            arrayMovimento[0] = 0;
                            arrayMovimento[1] = 0;

                            selectedSquare.setBackground(originalColor);
                            selectedSquare = null;
                            selectedName = null;
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
                if (selectedSquare != null) {
                    if (selectedSquare == square) {
                        selectedSquare.setBackground(originalColor);
                        selectedSquare = null;
                        selectedName = null;
                        arrayMovimento[0] = 0;
                        return;
                    } else {
                        if ((selectedName.contains("_white") && pieceName.contains("_black")) ||
                                (selectedName.contains("_black") && pieceName.contains("_white"))) {

                            square.removeAll();
                            square.revalidate();
                            square.repaint();

                            int startX = arrayMovimento[0];
                            int startY = arrayMovimento[1];

                            movePiece(startX, startY, col, row, selectedName);

                            selectedSquare.setBackground(originalColor);
                            selectedName = null;
                            arrayMovimento[0] = 0;
                        } else {
                            selectedSquare.setBackground(originalColor);
                        }
                    }
                }

                selectedSquare = square;
                originalColor = square.getBackground();
                selectedName = pieceName;
                arrayMovimento[0] = col;
                arrayMovimento[1] = row;

                square.setBackground(Color.YELLOW);
            }

            public void mouseEntered(MouseEvent e) {
                piece.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        square.add(piece, BorderLayout.CENTER);

        pieceImages.put(row + "," + col, piece);
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
}