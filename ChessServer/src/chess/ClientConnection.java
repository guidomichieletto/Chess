package chess;

import java.awt.Point;
import java.io.*;
import java.net.Socket;

public class ClientConnection extends Thread {
    private Socket socket;
    private Game game;
    private Color player;

    private BufferedReader in;
    private PrintWriter out;

    public ClientConnection(Socket socket, Game game, Color player) {
        this.socket = socket;
        this.game = game;
        this.player = player;
    }

    @Override
    public void run() {
        if(!initStreams()) {
            try {
                socket.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }

            return;
        }

        while(!game.isStarted()) {}

        out.println("STARTED," + player.symbol);

        try {
            while(true) {
                String line = in.readLine();
                String[] params = line.split(",");

                switch(params[0]) {
                    case "GETBOARD" -> getBoard();
                    case "AVAILABLEMOVES" -> availableMoves(new Point(Integer.parseInt(params[1]), Integer.parseInt(params[2])));
                    case "MOVE" -> move(new Point(Integer.parseInt(params[1]), Integer.parseInt(params[2])), new Point(Integer.parseInt(params[3]), Integer.parseInt(params[4])));
                }
            }

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }

    private boolean initStreams() {
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
        } catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }

        return true;
    }

    private void getBoard() {
        out.println(game.toCSV());
    }

    private void availableMoves(Point start) {
        for(Point p : game.getPiece(start.x, start.y).availableMoves(start)) {
            out.print(p.x + "," + p.y + ";");
        }
        out.println();
    }

    private void move(Point start, Point end) {
        if(game.getCurrentPlayer() != player) {
            out.println("NOTYOURTURN");
            return;
        }

        if(game.getPiece(start.x, start.y).getColor() != game.getCurrentPlayer()) {
            out.println("NOTYOURPIECE");
            return;
        }

        try {
            game.movePiece(start, end);
            out.println("OK");
        } catch (Exception ex) {
            out.println("UNALLOWEDMOVE");
        }
    }
}
