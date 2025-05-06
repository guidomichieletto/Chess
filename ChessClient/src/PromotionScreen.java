import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class PromotionScreen extends JFrame {

    private static final int BOARD_SIZE = 2;
    private String pieceToPromotion;

    public String getPieceToPromotion() {
        return pieceToPromotion;
    }

    public PromotionScreen(boolean areYouWhite) {
        setTitle("Promozione");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel panel = new JPanel(new GridLayout(2,2));
        for (int i = 0; i < BOARD_SIZE * BOARD_SIZE; i++) {
            JPanel cell = new JPanel(new BorderLayout());
            cell.setBorder(new LineBorder(Color.BLACK, 2));
            panel.add(cell);
        }

        if (areYouWhite) {
            addPieceToSquare(panel, 0, 0, "Queen_white");
            addPieceToSquare(panel, 0, 1, "Knight_white");
            addPieceToSquare(panel, 1, 0, "Bishop_white");
            addPieceToSquare(panel, 1, 1, "Rook_white");
        }else {
            addPieceToSquare(panel, 0, 0, "Queen_black");
            addPieceToSquare(panel, 0, 1, "Knight_black");
            addPieceToSquare(panel, 1, 0, "Bishop_black");
            addPieceToSquare(panel, 1, 1, "Rook_black");
        }
        add(panel, BorderLayout.CENTER);
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

        Image image = icon.getImage().getScaledInstance(50, 50, Image.SCALE_SMOOTH);
        icon = new ImageIcon(image);

        JLabel piece = new JLabel(icon);
        piece.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                pieceToPromotion = pieceName;
                dispose();
            }
            public void mouseEntered(MouseEvent e) {
                piece.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }
        });

        square.add(piece, BorderLayout.CENTER);
    }


    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PromotionScreen promotionScreen = new PromotionScreen(true);
            promotionScreen.setVisible(true);
        });
    }
}