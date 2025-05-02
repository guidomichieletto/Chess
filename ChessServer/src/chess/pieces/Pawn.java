package chess.pieces;

import chess.Color;
import java.awt.*;
import java.util.ArrayList;

public class Pawn extends Piece {
    public Pawn(Color color) {
        super(color, 'P');
    }

    public ArrayList<Point> availableMoves(Point start) {
        ArrayList<Point> availableMoves = new ArrayList<>();
        if (getColor() == Color.WHITE) {
            if (start.y - 1 >= 0) availableMoves.add(new Point(start.x, start.y - 1));
        } else {
            if (start.y + 1 < 8) availableMoves.add(new Point(start.x, start.y + 1));
        }
        return availableMoves;
    }
}
