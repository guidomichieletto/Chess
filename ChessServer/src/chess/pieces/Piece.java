package chess.pieces;

import chess.Color;
import chess.Game;

import java.awt.Point;
import java.util.ArrayList;

public abstract class Piece {
    private Game game;
    private Color color;
    private char symbol;

    public Piece(Game game, Color color, char symbol) {
        this.game = game;
        this.color = color;
        this.symbol = symbol;
    }

    public Game getGame() {
        return game;
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
