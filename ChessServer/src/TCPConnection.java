import java.net.Socket;

public class TCPConnection extends Thread {
    private Socket socket;

    public TCPConnection(Socket socket) {
        this.socket = socket;
    }

    @Override
    public void run() {

    }
}
