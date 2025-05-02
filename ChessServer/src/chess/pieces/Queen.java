package chess.pieces;

import chess.Color;
import chess.Game;

import java.awt.Point;
import java.util.ArrayList;

public class Queen extends Piece {
    public Queen(Game game, Color color) {
        super(game, color, 'Q');
    }

    public ArrayList<Point> availableMoves(Point start) {
        ArrayList<Point> availableMoves = new ArrayList<>();

        // Horizontal
        for(int x = 0; x < Game.COLS; x++) {
            if(x != start.x) availableMoves.add(new Point(x, start.y));
        }

        // Vertical
        for(int y = 0; y < Game.ROWS; y++) {
            if(y != start.y) availableMoves.add(new Point(start.x, y));
        }

        // Diagonal
        for(int i = 1; i < Game.COLS; i++) {
            if(start.x + i < Game.COLS && start.y + i < Game.ROWS) availableMoves.add(new Point(start.x + i, start.y + i));
            if(start.x - i >= 0 && start.y - i >= 0) availableMoves.add(new Point(start.x - i, start.y - i));
            if(start.x + i < Game.COLS && start.y - i >= 0) availableMoves.add(new Point(start.x + i, start.y - i));
            if(start.x - i >= 0 && start.y + i < Game.ROWS) availableMoves.add(new Point(start.x - i, start.y + i));
        }

        return availableMoves;
    }
}
