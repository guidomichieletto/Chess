package chess.pieces;

import chess.Color;
import chess.Game;

import java.awt.Point;
import java.util.ArrayList;

public class King extends Piece {

    public King(Game game, Color color) {
        super(game, color, 'K');
    }

    public ArrayList<Point> availableMoves(Point start) {
        ArrayList<Point> availableMoves = new ArrayList<>();

        if(start.y - 1 >= 0) availableMoves.add(new Point(start.x, start.y - 1)); // Up
        if(start.y + 1 < Game.ROWS) availableMoves.add(new Point(start.x, start.y + 1)); // Down
        if(start.x - 1 >= 0) availableMoves.add(new Point(start.x - 1, start.y)); // Left
        if(start.x + 1 < Game.COLS) availableMoves.add(new Point(start.x + 1, start.y)); // Right
        if(start.x - 1 >= 0 && start.y - 1 >= 0) availableMoves.add(new Point(start.x - 1, start.y - 1)); // Up-Left
        if(start.x + 1 < Game.COLS && start.y - 1 >= 0) availableMoves.add(new Point(start.x + 1, start.y - 1)); // Up-Right
        if(start.x - 1 >= 0 && start.y + 1 < Game.ROWS) availableMoves.add(new Point(start.x - 1, start.y + 1)); // Down-Left
        if(start.x + 1 < Game.COLS && start.y + 1 < Game.ROWS) availableMoves.add(new Point(start.x + 1, start.y + 1)); // Down-Right

        return availableMoves;
    }
}
