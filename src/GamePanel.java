import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;

public class GamePanel extends JPanel implements ActionListener, KeyListener {

    private static final int WIDTH = 800;
    private static final int HEIGHT = 600;

    private static final int INTERSECTION_SIZE = 120;
    private static final int ROAD_WIDTH = 120;

    private static final int TIMER_DELAY = 30;
    private static final int GAME_DURATION = 60_000;

    private static final int MAX_QUEUE = 6;   // spustis
    private static final int SAFE_GAP = 22;   // min atstumas tarp masinu

    private Timer timer;
    private long startTime;

    private final List<Car> cars = new ArrayList<>();


    private final TrafficLight horizontalLight;
    private final TrafficLight verticalLight;

    private final CarSpawner spawner;
    private final TrafficController controller;

    private boolean gameOver = false;
    private boolean gameWon = false;
    private String endMessage = "";

    private boolean paused = false;
    private long pauseStart;


    private int level = 1;
    private double carSpeed = 3.0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(new Color(25, 25, 25));

        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;


        verticalLight = new TrafficLight(
                centerX + ROAD_WIDTH / 2 + 30,
                centerY - INTERSECTION_SIZE / 2 - 105
        );

        horizontalLight = new TrafficLight(
                centerX - ROAD_WIDTH / 2 - 60,
                centerY + INTERSECTION_SIZE / 2 + 20
        );


        spawner = new CarSpawner(WIDTH, HEIGHT);
        controller = new TrafficController(
                WIDTH, HEIGHT, INTERSECTION_SIZE, ROAD_WIDTH,
                SAFE_GAP, MAX_QUEUE
        );

        setFocusable(true);
        addKeyListener(this);

        updateSpeedForLevel();
        startGame();
    }


    private void updateSpeedForLevel() {
        switch (level) {
            case 1 -> carSpeed = 2.0; // lėčiausias
            case 2 -> carSpeed = 2.5; // vidutinis
            case 3 -> carSpeed = 3.0; // greičiausias
            default -> carSpeed = 2.0;
        }
    }

    private void startGame() {
        if (timer != null) {
            timer.stop();
        }

        cars.clear();
        gameOver = false;
        gameWon = false;
        endMessage = "";
        paused = false;

        horizontalLight.setState(TrafficLight.State.GREEN);
        verticalLight.setState(TrafficLight.State.GREEN);

        startTime = System.currentTimeMillis();

        long now = System.currentTimeMillis();
        spawner.reset(now, level); // paruošiam generavimą

        timer = new Timer(TIMER_DELAY, this);
        timer.start();
        requestFocusInWindow();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameOver || gameWon) {
            if (timer != null) {
                timer.stop();
            }
            repaint();
            return;
        }

        if (paused) {
            repaint();
            return;
        }

        long now = System.currentTimeMillis();
        if (now - startTime >= GAME_DURATION) {
            gameWon = true;
            endMessage = "LAIMĖJAI! Sėkmingai valdėte eismą 60 s.";
            repaint();
            return;
        }

        //  generate naujas masinas
        spawner.spawnCars(cars, carSpeed, level, now);

        //  atnaujina judejima, atstum.
        controller.updateCars(cars, horizontalLight, verticalLight);

        if (controller.checkCollisions(cars)) {
            gameOver = true;
            endMessage = "AVARIJA! Du automobiliai susidūrė.";
        } else if (controller.checkTrafficJam(cars)) {
            gameOver = true;
            endMessage = "SPŪSTIS! Susidarė per ilga eilė.";
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        drawRoads(g2);
        drawIntersection(g2);
        drawTrafficLights(g2);
        drawCars(g2);
        drawHud(g2);

        if (paused && !gameOver && !gameWon) {
            drawPauseOverlay(g2);
        }

        if (gameOver || gameWon) {
            drawEndScreen(g2);
        }
    }

    private void drawRoads(Graphics2D g2) {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        g2.setColor(new Color(60, 60, 60));

        g2.fillRect(0, centerY - ROAD_WIDTH / 2, WIDTH, ROAD_WIDTH);
        g2.fillRect(centerX - ROAD_WIDTH / 2, 0, ROAD_WIDTH, HEIGHT);
    }

    private void drawIntersection(Graphics2D g2) {
        int centerX = WIDTH / 2;
        int centerY = HEIGHT / 2;

        g2.setColor(new Color(70, 70, 70));
        g2.fillRect(centerX - INTERSECTION_SIZE / 2,
                centerY - INTERSECTION_SIZE / 2,
                INTERSECTION_SIZE,
                INTERSECTION_SIZE);
    }

    private void drawTrafficLights(Graphics2D g2) {
        verticalLight.draw(g2);
        horizontalLight.draw(g2);
    }

    private void drawCars(Graphics2D g2) {
        for (Car car : cars) {
            car.draw(g2);
        }
    }

    private void drawHud(Graphics2D g2) {
        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.PLAIN, 14));

        long timeLeft = Math.max(0, GAME_DURATION - (System.currentTimeMillis() - startTime));
        int secondsLeft = (int) (timeLeft / 1000);

        g2.drawString("Laikas iki pergalės: " + secondsLeft + " s", 10, 20);
        g2.drawString("Viršutinis (VERTIKALUS): V, apatinis (HORIZONTALUS): H", 10, 40);
        g2.drawString("Lygis: " + level + " (keisti: 1, 2, 3)", 10, 60);
        g2.drawString("Pauzė / tęsti: SPACE", 10, 80);
    }

    private void drawPauseOverlay(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 150));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        String txt = "PAUZĖ";
        FontMetrics fm = g2.getFontMetrics();
        int x = (WIDTH - fm.stringWidth(txt)) / 2;
        int y = HEIGHT / 2 - 20;
        g2.drawString(txt, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String info = "Paspausk SPACE, kad tęstum.";
        fm = g2.getFontMetrics();
        g2.drawString(info, (WIDTH - fm.stringWidth(info)) / 2, y + 40);
    }

    private void drawEndScreen(Graphics2D g2) {
        g2.setColor(new Color(0, 0, 0, 180));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        g2.setColor(Color.WHITE);
        g2.setFont(new Font("Arial", Font.BOLD, 28));
        FontMetrics fm = g2.getFontMetrics();

        int textWidth = fm.stringWidth(endMessage);
        int x = (WIDTH - textWidth) / 2;
        int y = HEIGHT / 2;

        g2.drawString(endMessage, x, y);

        g2.setFont(new Font("Arial", Font.PLAIN, 18));
        String info = "Paspausk SPACE, kad pradėtum iš naujo.";
        fm = g2.getFontMetrics();
        int infoWidth = fm.stringWidth(info);
        g2.drawString(info, (WIDTH - infoWidth) / 2, y + 40);
    }



    @Override
    public void keyPressed(KeyEvent e) {
        int code = e.getKeyCode();

        if (!gameOver && !gameWon) {
            if (code == KeyEvent.VK_H) {
                horizontalLight.nextState();
            } else if (code == KeyEvent.VK_V) {
                verticalLight.nextState();
            } else if (code == KeyEvent.VK_SPACE) {
                if (!paused) {
                    paused = true;
                    pauseStart = System.currentTimeMillis();
                    if (timer != null) {
                        timer.stop();
                    }
                } else {
                    paused = false;
                    long now = System.currentTimeMillis();
                    long pauseDuration = now - pauseStart;
                    startTime += pauseDuration;
                    if (timer != null) {
                        timer.start();
                    }
                }
            } else if (code == KeyEvent.VK_1 || code == KeyEvent.VK_2 || code == KeyEvent.VK_3) {
                if (code == KeyEvent.VK_1) level = 1;
                if (code == KeyEvent.VK_2) level = 2;
                if (code == KeyEvent.VK_3) level = 3;
                updateSpeedForLevel();
                startGame();
            }
        } else {
            if (code == KeyEvent.VK_SPACE) {
                startGame();
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void keyReleased(KeyEvent e) { }
}
