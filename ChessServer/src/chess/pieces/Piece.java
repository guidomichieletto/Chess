package chess.pieces;

import chess.Color;

import java.awt.Point;
import java.util.ArrayList;

public abstract class Piece {
    private Color color;
    private char symbol;

    public Piece(Color color, char symbol) {
        this.color = color;
        this.symbol = symbol;
    }

    public Color getColor() {
        return color;
    }

    public char getSymbol() {
        return symbol;
    }

    public abstract ArrayList<Point> availableMoves(Point start);

    public boolean legalMove(Point start, Point end) {
        return availableMoves(start).contains(end);
    }
}
