package chess.pieces;

import chess.Color;
import chess.Game;

import java.awt.Point;
import java.util.ArrayList;

public class Rook extends Piece {
    public Rook(Game game, Color color) {
        super(game, color, 'R');
    }

    public ArrayList<Point> availableMoves(Point start) {
        ArrayList<Point> availableMoves = new ArrayList<>();

        // Vertical moves
        for (int y = 0; y < Game.ROWS; y++) {
            if (y != start.y) {
                availableMoves.add(new Point(start.x, y));
            }
        }

        // Horizontal moves
        for (int x = 0; x < Game.COLS; x++) {
            if (x != start.x) {
                availableMoves.add(new Point(x, start.y));
            }
        }

        return availableMoves;
    }
}
