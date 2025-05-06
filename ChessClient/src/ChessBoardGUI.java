import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.util.Arrays;
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

        Arrays.fill(arrayMovimento, -1);

        setTitle("Scacchiera");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                String[] options = {"Sì", "No", "Pareggio"};
                int result = JOptionPane.showOptionDialog(
                        ChessBoardGUI.this,
                        "Sei sicuro di voler uscire?",
                        "Conferma uscita",
                        JOptionPane.YES_NO_CANCEL_OPTION,
                        JOptionPane.QUESTION_MESSAGE,
                        null,
                        options,
                        options[2]
                );

                if (result == 0) {
                    client.sendMessage("RESIGN");
                } else if (result == 2) {
                    client.sendMessage("DRAW_OFFER");
                }
            }
        });
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
        if(message.equals("DRAW_OFFER")) {
            int response = JOptionPane.showConfirmDialog(this, "Il tuo avversario ha proposto un pareggio. Vuoi accettare?", "Proposta di pareggio", JOptionPane.YES_NO_OPTION);
            if (response == JOptionPane.YES_OPTION) {
                client.sendMessage("DRAW_ACCEPT");
            } else {
                client.sendMessage("DRAW_REJECT");
            }
            return;
        }
        if (message.equals("DRAW_ACCEPT")) {
            JOptionPane.showMessageDialog(this, "Tu e il tuo avversario avete pareggiato!", "Pareggio accettato", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            return;
        }
        if (message.equals("DRAW_REJECT")) {
            JOptionPane.showMessageDialog(this, "Il tuo avversario ha rifiutato il pareggio!", "Pareggio rifiutato", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        if (message.equals("WIN")) {
            JOptionPane.showMessageDialog(this, "HAI VINTO!", "Vittoria", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            return;
        }

        if (message.equals("LOSE")) {
            JOptionPane.showMessageDialog(this, "HAI PERSO!", "Sconfitta", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
            return;
        }

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
        if (message.equals("PROMOTION")){
            PromotionScreen promotionScreen = new PromotionScreen(areYouWhite);
            promotionScreen.setVisible(true);
            String piece = promotionScreen.getPieceToPromotion();
            if (piece != null) {
                client.sendMessage("PROM," + getPieceCodeFromImageName(piece));
            }
        }
        System.out.println("Messaggio sconosciuto: " + message);
    }

    private void movePiece(int startX, int startY, int endX, int endY) {
        client.sendMessage("MOVE," + startX + "," + startY + "," + endX + "," + endY);
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
                        System.out.println(convertCoordinate(finalCol) + "," + convertCoordinate(finalRow));
                        if (arrayMovimento[0] != -1 && arrayMovimento[1] != -1) {
                            int startX = convertCoordinate(arrayMovimento[0]);
                            int startY = convertCoordinate(arrayMovimento[1]);

                            movePiece(startX, startY, convertCoordinate(finalCol), convertCoordinate(finalRow));

                            arrayMovimento[0] = -1;
                            arrayMovimento[1] = -1;

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
    private String getPieceCodeFromImageName(String imageName) {
        return switch (imageName) {
            case "Pawn_black" -> "B,P";
            case "Pawn_white" -> "W,P";
            case "Rook_black" -> "B,R";
            case "Rook_white" -> "W,R";
            case "Knight_black" -> "B,N";
            case "Knight_white" -> "W,N";
            case "Bishop_black" -> "B,B";
            case "Bishop_white" -> "W,B";
            case "Queen_black" -> "B,Q";
            case "Queen_white" -> "W,Q";
            case "King_black" -> "B,K";
            case "King_white" -> "W,K";
            default -> throw new IllegalArgumentException("Nome immagine non valido: " + imageName);
        };
    }

    private void placePieces(JPanel boardPanel) {
        for (int col = 0; col < BOARD_SIZE; col++) {
            addPieceToSquare(boardPanel, convertCoordinate(1), convertCoordinate(col), "Pawn_black");
            addPieceToSquare(boardPanel, convertCoordinate(6), convertCoordinate(col), "Pawn_white");
        }
        addPieceToSquare(boardPanel, convertCoordinate(0), convertCoordinate(0), "Rook_black");
        addPieceToSquare(boardPanel, convertCoordinate(0), convertCoordinate(7), "Rook_black");
        addPieceToSquare(boardPanel, convertCoordinate(7), convertCoordinate(0), "Rook_white");
        addPieceToSquare(boardPanel, convertCoordinate(7), convertCoordinate(7), "Rook_white");
        addPieceToSquare(boardPanel, convertCoordinate(0), convertCoordinate(1), "Knight_black");
        addPieceToSquare(boardPanel, convertCoordinate(0), convertCoordinate(6), "Knight_black");
        addPieceToSquare(boardPanel, convertCoordinate(7), convertCoordinate(1), "Knight_white");
        addPieceToSquare(boardPanel, convertCoordinate(7), convertCoordinate(6), "Knight_white");
        addPieceToSquare(boardPanel, convertCoordinate(0), convertCoordinate(2), "Bishop_black");
        addPieceToSquare(boardPanel, convertCoordinate(0), convertCoordinate(5), "Bishop_black");
        addPieceToSquare(boardPanel, convertCoordinate(7), convertCoordinate(2), "Bishop_white");
        addPieceToSquare(boardPanel, convertCoordinate(7), convertCoordinate(5), "Bishop_white");
        addPieceToSquare(boardPanel, convertCoordinate(0), convertCoordinate(3), "Queen_black");
        addPieceToSquare(boardPanel, convertCoordinate(7), convertCoordinate(3), "Queen_white");
        addPieceToSquare(boardPanel, convertCoordinate(0), convertCoordinate(4), "King_black");
        addPieceToSquare(boardPanel, convertCoordinate(7), convertCoordinate(4), "King_white");

    }
    private int convertCoordinate(int coord) {
        return areYouWhite ? coord : BOARD_SIZE - 1 - coord;
    }

    private void addPieceToSquare(JPanel boardPanel, int row, int col, String pieceName) {
        int index = row * BOARD_SIZE + col;
        JPanel square = (JPanel) boardPanel.getComponent(index);

        square.removeAll();
        square.revalidate();

        ImageIcon icon = new ImageIcon(getClass().getResource("img/" + pieceName + ".png"));

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
                        square.setBackground(Color.YELLOW);
                        client.sendMessage("AVAILABLEMOVES," + convertCoordinate(col) + "," + convertCoordinate(row));
                        selectedSquare = null;
                        selectedName = null;
                        arrayMovimento[0] = -1;
                        arrayMovimento[1] = -1;
                        return;
                    }
                    selectedSquare = square;
                    originalColor = square.getBackground();
                    selectedName = pieceName;
                    arrayMovimento[0] = col;
                    arrayMovimento[1] = row;
                    square.setBackground(Color.YELLOW);
                    client.sendMessage("AVAILABLEMOVES," + convertCoordinate(arrayMovimento[0]) + "," + convertCoordinate(arrayMovimento[1]));
                    return;
                }

                if (selectedSquare == square) {
                    selectedSquare.setBackground(originalColor);
                    selectedSquare = null;
                    selectedName = null;
                    arrayMovimento[0] = -1;
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
                    arrayMovimento[0] = -1;
                    arrayMovimento[1] = -1;
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