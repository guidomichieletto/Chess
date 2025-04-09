import javax.swing.*;
import java.awt.*;

public class MenuGUI extends JFrame {
    public MenuGUI() {
        setTitle("Menu");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;


        JLabel titolo = new JLabel("SCACCHI");
        titolo.setFont(new Font("Roboto", Font.BOLD, 50));
        add(titolo, gbc);

        JButton gioca = new JButton("GIOCA");
        gioca.setPreferredSize(new Dimension(200, 50));
        add(gioca, gbc);
        gioca.addActionListener(e -> {
            ChessBoardGUI chessBoardGUI = new ChessBoardGUI();
            chessBoardGUI.setVisible(true);
            dispose();
        });

        JButton server = new JButton("SERVER");
        server.setPreferredSize(new Dimension(200, 50));
        add(server, gbc);

        JButton esci = new JButton("ESCI");
        esci.setPreferredSize(new Dimension(200, 50));
        add(esci, gbc);
        esci.addActionListener(e -> {
            System.exit(0);
        });
    }
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MenuGUI menu = new MenuGUI();
            menu.setVisible(true);
        });
    }
}
