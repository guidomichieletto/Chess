import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MenuGUI extends JFrame {
    public MenuGUI() {
        setTitle("Menu");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.yellow);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridx = 0;


        JLabel titolo = new JLabel("SCACCHI");
        titolo.setFont(new Font("Roboto", Font.BOLD, 50));
        add(titolo, gbc);

        JButton gioca = new JButton("GIOCA");
        gioca.setBackground(Color.pink);
        gioca.setPreferredSize(new Dimension(200, 50));
        add(gioca, gbc);
        gioca.addActionListener(e -> {
            ClientTCP client = new ClientTCP();
            boolean connected = client.connect("localhost", 3030);

            if (connected) {
                WaitingScreen waitingScreen = new WaitingScreen();
                waitingScreen.setVisible(true);

                new Thread(() -> {
                    try {
                        String response;
                        while ((response = client.receiveMessage()) != null) {
                            if (response.startsWith("STARTED")) {
                                String[] parts = response.split(",");
                                boolean areYouWhite = "W".equals(parts[1]);

                                SwingUtilities.invokeLater(() -> {
                                    waitingScreen.dispose();
                                    ChessBoardGUI chessBoardGUI = new ChessBoardGUI(client, areYouWhite);
                                    chessBoardGUI.setVisible(true);
                                });
                                break;
                            }
                        }
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }).start();

                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Impossibile connettersi al server.", "Errore", JOptionPane.ERROR_MESSAGE);
            }
        });


        JButton esci = new JButton("ESCI");
        esci.setBackground(Color.ORANGE);
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
