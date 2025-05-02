package chess.pieces;

import chess.Color;
import chess.Game;

import java.awt.*;
import java.util.ArrayList;

public class Bishop extends Piece {
    public Bishop(Color color) {
        super(color, 'B');
    }

    @Override
    public ArrayList<Point> availableMoves(Point start) {
        ArrayList<Point> availableMoves = new ArrayList<>();

        for (int i = 1; start.x + i < Game.COLS && start.y + i < Game.ROWS; i++) {
            availableMoves.add(new Point(start.x + i, start.y + i));
        }
        for (int i = 1; start.x - i >= 0 && start.y + i < Game.ROWS; i++) {
            availableMoves.add(new Point(start.x - i, start.y + i));
        }
        for (int i = 1; start.x + i < Game.COLS && start.y - i >= 0; i++) {
            availableMoves.add(new Point(start.x + i, start.y - i));
        }
        for (int i = 1; start.x - i >= 0 && start.y - i >= 0; i++) {
            availableMoves.add(new Point(start.x - i, start.y - i));
        }

        return availableMoves;
    }
}
