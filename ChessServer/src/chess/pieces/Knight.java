package chess.pieces;

import chess.Color;
import chess.Game;

import java.awt.Point;
import java.util.ArrayList;

public class Knight extends Piece {
    public Knight(Game game, Color color) {
        super(game, color, 'N');
    }

    public ArrayList<Point> availableMoves(Point start) {
        ArrayList<Point> availableMoves = new ArrayList<>();

        int[][] moves = {
                {2, 1}, {1, 2}, {-1, 2}, {-2, 1},
                {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
        };

        for (int[] move : moves) {
            int newX = start.x + move[0];
            int newY = start.y + move[1];

            if (newX >= 0 && newX < Game.COLS && newY >= 0 && newY < Game.ROWS) {
                availableMoves.add(new Point(newX, newY));
            }
        }

        return availableMoves;
    }
}
