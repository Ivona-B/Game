import java.awt.*;

public class TrafficLight {

    enum State {
        RED,
        GREEN,
        YELLOW
    }

    private int x, y;
    private State state = State.RED;

    public TrafficLight(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void nextState() {
        switch (state) {
            case RED -> state = State.GREEN;
            case GREEN -> state = State.YELLOW;
            case YELLOW -> state = State.RED;
        }
    }

    public boolean isGreen() {
        return state == State.GREEN;
    }

    public void draw(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.fillRoundRect(x, y, 30, 90, 10, 10);

        drawLight(g2, Color.RED, y + 5, state == State.RED);
        drawLight(g2, Color.YELLOW, y + 30, state == State.YELLOW);
        drawLight(g2, Color.GREEN, y + 55, state == State.GREEN);
    }

    private void drawLight(Graphics2D g2, Color color, int cy, boolean on) {
        g2.setColor(on ? color : color.darker().darker());
        g2.fillOval(x + 5, cy, 20, 20);
    }

    public void setState(State state) {
        this.state = state;
    }
}
