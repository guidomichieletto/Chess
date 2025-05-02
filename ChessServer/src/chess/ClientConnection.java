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
    public void sendReadyMessage() {
        out.println("READY");
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

        try {
            game.getStartSemaphore().acquire();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        out.println("STARTED," + player.symbol);
        System.out.println("Started" + player.symbol);
        if (player == Color.WHITE) {
            sendTrigger();
        }

        try {
            while(true) {
                String line = in.readLine();
                String[] params = line.split(",");
                System.out.println(params[0]);

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
        String board = game.toCSV().replace("\n", "|");
        out.println("BOARD|" + board);

        ClientConnection other = (player == Color.WHITE) ? game.getBlack() : game.getWhite();
        if (other != null) {
            other.out.println("BOARD|" + board);
        }
    }

    private void availableMoves(Point start) {
        StringBuilder sb = new StringBuilder("MOVES|");
        for(Point p : game.getPiece(start.x, start.y).availableMoves(start)) {
            sb.append(p.x).append(",").append(p.y).append(";");
        }
        out.println(sb.toString());
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
            game.nextTurn();
            out.println("OK");
        } catch (Exception ex) {
            out.println("UNALLOWEDMOVE");
        }
    }

    public void sendTrigger() {
        out.println("YOURTURN");
    }
}
