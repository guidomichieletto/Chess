package chess.pieces;

import chess.Color;
import chess.Game;

import java.awt.*;
import java.util.ArrayList;

public class Pawn extends Piece {
    public Pawn(Game game, Color color) {
        super(game, color, 'P');
    }

    public ArrayList<Point> availableMoves(Point start) {
        ArrayList<Point> availableMoves = new ArrayList<>();

        // White
        if(this.getColor() == Color.WHITE) {
            if(start.y == 6 && this.getGame().getPiece(start.x, start.y - 2) == null) availableMoves.add(new Point(start.x, start.y - 2));
            if(start.y - 1 >= 0 && this.getGame().getPiece(start.x, start.y - 1) == null) availableMoves.add(new Point(start.x, start.y - 1));

            if(start.y - 1 >= 0 && start.x + 1 < Game.COLS && this.getGame().getPiece(start.x + 1, start.y - 1) != null && this.getGame().getPiece(start.x + 1, start.y - 1).getColor() != this.getColor()) {
                availableMoves.add(new Point(start.x + 1, start.y - 1));
            }
            if(start.y - 1 >= 0 && start.x - 1 >= 0 && this.getGame().getPiece(start.x - 1, start.y - 1) != null && this.getGame().getPiece(start.x - 1, start.y - 1).getColor() != this.getColor()) {
                availableMoves.add(new Point(start.x - 1, start.y - 1));
            }
        }


        // Black
        if(this.getColor() == Color.BLACK) {
            if(start.y == 1 && this.getGame().getPiece(start.x, start.y + 2) == null) availableMoves.add(new Point(start.x, start.y + 2));
            if(start.y + 1 < Game.ROWS && this.getGame().getPiece(start.x, start.y + 1) == null) availableMoves.add(new Point(start.x, start.y + 1));

            if(start.y + 1 < Game.ROWS && start.x + 1 < Game.COLS && this.getGame().getPiece(start.x + 1, start.y + 1) != null && this.getGame().getPiece(start.x + 1, start.y + 1).getColor() != this.getColor()) {
                availableMoves.add(new Point(start.x + 1, start.y + 1));
            }
            if(start.y + 1 < Game.ROWS && start.x - 1 >= 0 && this.getGame().getPiece(start.x - 1, start.y + 1) != null && this.getGame().getPiece(start.x - 1, start.y + 1).getColor() != this.getColor()) {
                availableMoves.add(new Point(start.x - 1, start.y + 1));
            }
        }


        return availableMoves;
    }
}
