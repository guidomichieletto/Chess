import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;

public class Main {
    public static final int PORT = 3030;
    public static ArrayList<Game> games = new ArrayList<>();

    public static void main(String[] args) {

    }

    public static void startServer() {
        try {
            ServerSocket ss = new ServerSocket(PORT);

        } catch (IOException ex) {
            System.out.println(ex.getMessage());
        }
    }
}