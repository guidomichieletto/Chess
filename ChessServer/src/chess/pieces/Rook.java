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

        // Vertical moves up
        for (int y = start.y + 1; y < Game.ROWS; y++) {
            if(!checkCellColor(start.x, y)) break;
            availableMoves.add(new Point(start.x, y));
            if(!checkCell(start.x, y)) break;
        }

        // Vertical moves down
        for (int y = start.y - 1; y >= 0; y--) {
            if(!checkCellColor(start.x, y)) break;
            availableMoves.add(new Point(start.x, y));
            if(!checkCell(start.x, y)) break;
        }

        // Horizontal moves right
        for (int x = start.x + 1; x < Game.COLS; x++) {
            if(!checkCellColor(x, start.y)) break;
            availableMoves.add(new Point(x, start.y));
            if(!checkCell(x, start.y)) break;
        }

        // Horizontal moves left
        for (int x = start.x - 1; x >= 0; x--) {
            if(!checkCellColor(x, start.y)) break;
            availableMoves.add(new Point(x, start.y));
            if(!checkCell(x, start.y)) break;
        }

        return availableMoves;
    }
}
