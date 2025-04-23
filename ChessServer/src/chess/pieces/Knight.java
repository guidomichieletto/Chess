package chess.pieces;

import chess.Color;

import java.awt.Point;
import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Color color) {
        super(color, 'N');
    }

    public ArrayList<Point> availableMoves(Point start) {
        ArrayList<Point> availableMoves = new ArrayList<>();

        // TODO: add available moves

        return availableMoves;
    }
}
