import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

public class Main {
    InetAddress indirizzo;
    int porta = 1050;
    public void main(String[]args){
        try {
            Socket s = new Socket(indirizzo, porta);
            OutputStreamWriter osw = new OutputStreamWriter(s.getOutputStream());
            BufferedWriter bw = new BufferedWriter(osw);
            PrintWriter out = new PrintWriter(bw, true);

            InputStreamReader isr = new InputStreamReader(s.getInputStream());
            BufferedReader in = new BufferedReader(isr);
            String str = in.readLine();

            //quando devo chiudere la comunicazione
            out.close(); in.close(); s.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}