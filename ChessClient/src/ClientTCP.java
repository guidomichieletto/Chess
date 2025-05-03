import java.io.*;
import java.net.Socket;

public class ClientTCP {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    public boolean connect(String host, int port) {
        try {
            socket = new Socket(host, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
            out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8")), true);
            return true;
        } catch (IOException e) {
            System.err.println("Errore di connessione: " + e.getMessage());
            return false;
        }
    }

    public void sendMessage(String message) {
        System.out.println("Sending: " + message + "\n__________________________________________");
        out.println(message);
    }
    
    public String receiveMessage() throws IOException {
        String message = in.readLine();
        System.out.println("Received: " + message + "\n__________________________________________");
        return message;
    }

    public void closeConnection() {
        try {
            in.close();
            out.close();
            socket.close();
        } catch (IOException e) {
            System.err.println("Errore durante la chiusura della connessione: " + e.getMessage());
        }
    }
}

