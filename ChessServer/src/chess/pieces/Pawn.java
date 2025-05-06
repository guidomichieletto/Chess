package chess.pieces;

import chess.Color;
import chess.Game;

import java.awt.*;
import java.util.ArrayList;

public class Pawn extends Piece {
    public boolean enpassantable = false;

    public Pawn(Game game, Color color) {
        super(game, color, 'P');
    }

    public ArrayList<Point> availableMoves(Point start) {
        ArrayList<Point> availableMoves = new ArrayList<>();

        // White
        if (this.getColor() == Color.WHITE) {
            if (start.y == 6 && this.getGame().getPiece(start.x, start.y - 2) == null && this.getGame().getPiece(start.x, start.y - 1) == null)
                availableMoves.add(new Point(start.x, start.y - 2));
            if (start.y - 1 >= 0 && this.getGame().getPiece(start.x, start.y - 1) == null)
                availableMoves.add(new Point(start.x, start.y - 1));

            if (start.y - 1 >= 0 && start.x + 1 < Game.COLS) {
                Piece p = this.getGame().getPiece(start.x + 1, start.y - 1);
                if (p != null && p.getColor() != this.getColor())
                    availableMoves.add(new Point(start.x + 1, start.y - 1));
            }
            if (start.y - 1 >= 0 && start.x - 1 >= 0) {
                Piece p = this.getGame().getPiece(start.x - 1, start.y - 1);
                if (p != null && p.getColor() != this.getColor())
                    availableMoves.add(new Point(start.x - 1, start.y - 1));
            }

            if (start.x + 1 < Game.COLS) {
                Piece right = this.getGame().getPiece(start.x + 1, start.y);
                if (right instanceof Pawn && right.getColor() == Color.BLACK && ((Pawn) right).enpassantable)
                    availableMoves.add(new Point(start.x + 1, start.y - 1));
            }
            if (start.x - 1 >= 0) {
                Piece left = this.getGame().getPiece(start.x - 1, start.y);
                if (left instanceof Pawn && left.getColor() == Color.BLACK && ((Pawn) left).enpassantable)
                    availableMoves.add(new Point(start.x - 1, start.y - 1));
            }
        }

        // Black
        if (this.getColor() == Color.BLACK) {
            if (start.y == 1 && this.getGame().getPiece(start.x, start.y + 2) == null && this.getGame().getPiece(start.x, start.y + 1) == null)
                availableMoves.add(new Point(start.x, start.y + 2));
            if (start.y + 1 < Game.ROWS && this.getGame().getPiece(start.x, start.y + 1) == null)
                availableMoves.add(new Point(start.x, start.y + 1));

            if (start.y + 1 < Game.ROWS && start.x + 1 < Game.COLS) {
                Piece p = this.getGame().getPiece(start.x + 1, start.y + 1);
                if (p != null && p.getColor() != this.getColor())
                    availableMoves.add(new Point(start.x + 1, start.y + 1));
            }
            if (start.y + 1 < Game.ROWS && start.x - 1 >= 0) {
                Piece p = this.getGame().getPiece(start.x - 1, start.y + 1);
                if (p != null && p.getColor() != this.getColor())
                    availableMoves.add(new Point(start.x - 1, start.y + 1));
            }

            if (start.x + 1 < Game.COLS) {
                Piece right = this.getGame().getPiece(start.x + 1, start.y);
                if (right instanceof Pawn && right.getColor() == Color.WHITE && ((Pawn) right).enpassantable)
                    availableMoves.add(new Point(start.x + 1, start.y + 1));
            }
            if (start.x - 1 >= 0) {
                Piece left = this.getGame().getPiece(start.x - 1, start.y);
                if (left instanceof Pawn && left.getColor() == Color.WHITE && ((Pawn) left).enpassantable)
                    availableMoves.add(new Point(start.x - 1, start.y + 1));
            }
        }

        return availableMoves;
    }
}
