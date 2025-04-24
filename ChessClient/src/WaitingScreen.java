import javax.swing.*;
import java.awt.*;

public class WaitingScreen extends JFrame {
    public WaitingScreen() {
        setTitle("Attesa");
        setSize(400, 200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Impedisce la chiusura accidentale
        setLayout(new BorderLayout());

        JLabel message = new JLabel("In attesa di un avversario...", SwingConstants.CENTER);
        message.setFont(new Font("Roboto", Font.BOLD, 20));
        add(message, BorderLayout.CENTER);
    }
}
