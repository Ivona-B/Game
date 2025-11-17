import javax.swing.*;

public class TrafficGameFrame extends JFrame {

    public TrafficGameFrame() {
        setTitle("Šviesoforų meistras");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        GamePanel panel = new GamePanel();
        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
