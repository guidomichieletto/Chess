import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class MenuGUI extends JFrame {
    JTextField ipField = new JTextField("localhost", 5);
    JTextField portField = new JTextField("3030", 5);
    JLabel ipLabel = new JLabel("IP:");
    JLabel portLabel = new JLabel("PORTA:");
    public MenuGUI() {
        setTitle("Menu");
        setSize(800, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridBagLayout());
        getContentPane().setBackground(Color.yellow);


        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(ipLabel, gbc);

        gbc.gridx = 1;
        add(ipField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        add(portLabel, gbc);

        gbc.gridx = 1;
        add(portField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 2;
        JLabel titolo = new JLabel("SCACCHI");
        titolo.setFont(new Font("Roboto", Font.BOLD, 50));
        add(titolo, gbc);

        gbc.gridy = 3;
        JButton gioca = new JButton("GIOCA");
        gioca.setBackground(Color.pink);
        gioca.setPreferredSize(new Dimension(200, 50));
        add(gioca, gbc);
        gioca.addActionListener(e -> {
            ClientTCP client = new ClientTCP();
            boolean connected = client.connect(ipField.getText(), Integer.parseInt(portField.getText()));

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

        gbc.gridy = 4;
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
