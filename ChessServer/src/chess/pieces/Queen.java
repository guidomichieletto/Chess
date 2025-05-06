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

        // Horizontal right
        for(int x = start.x + 1; x < Game.COLS; x++) {
            if(!checkCellColor(x, start.y)) break;
            availableMoves.add(new Point(x, start.y));
            if(!checkCell(x, start.y)) break;
        }

        // Horizontal left
        for(int x = start.x - 1; x >= 0; x--) {
            if(!checkCellColor(x, start.y)) break;
            availableMoves.add(new Point(x, start.y));
            if(!checkCell(x, start.y)) break;
        }

        // Vertical down
        for(int y = start.y + 1; y < Game.ROWS; y++) {
            if(!checkCellColor(start.x, y)) break;
            availableMoves.add(new Point(start.x, y));
            if(!checkCell(start.x, y)) break;
        }

        // Vertical up
        for(int y = start.y - 1; y >= 0; y--) {
            if(!checkCellColor(start.x, y)) break;
            availableMoves.add(new Point(start.x, y));
            if(!checkCell(start.x, y)) break;
        }

        // Diagonal
        for (int i = 1; start.x + i < Game.COLS && start.y + i < Game.ROWS; i++) {
            if(!checkCellColor(start.x + i, start.y + i)) break;
            availableMoves.add(new Point(start.x + i, start.y + i));
            if(!checkCell(start.x + i, start.y + i)) break;
        }
        for (int i = 1; start.x - i >= 0 && start.y + i < Game.ROWS; i++) {
            if(!checkCellColor(start.x - i, start.y + i)) break;
            availableMoves.add(new Point(start.x - i, start.y + i));
            if(!checkCell(start.x - i, start.y + i)) break;
        }
        for (int i = 1; start.x + i < Game.COLS && start.y - i >= 0; i++) {
            if(!checkCellColor(start.x + i, start.y - i)) break;
            availableMoves.add(new Point(start.x + i, start.y - i));
            if(!checkCell(start.x + i, start.y - i)) break;
        }
        for (int i = 1; start.x - i >= 0 && start.y - i >= 0; i++) {
            if(!checkCellColor(start.x - i, start.y - i)) break;
            availableMoves.add(new Point(start.x - i, start.y - i));
            if(!checkCell(start.x - i, start.y - i)) break;
        }

        return availableMoves;
    }
}
