import java.awt.*;

public class Car {

    enum Direction {
        HORIZONTAL,
        VERTICAL
    }

    private int x, y;
    private final int width = 40;
    private final int height = 20;
    private final double maxSpeed;   // greitis pagal lygį
    private double speed;
    private final Direction direction;
    private boolean stopped = false;

    public Car(int x, int y, Direction direction, double maxSpeed) {
        this.x = x;
        this.y = y;
        this.direction = direction;
        this.maxSpeed = maxSpeed;
        this.speed = maxSpeed;
    }


    public void update(boolean mustStop) {
        if (mustStop) {
            speed = 0;
            stopped = true;
        } else {
            speed = maxSpeed;
            stopped = false;
        }

        if (direction == Direction.HORIZONTAL) {
            x += speed;
        } else {
            y += speed;
        }
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(0, 153, 255));

        if (direction == Direction.HORIZONTAL) {
            g2.fillRect(x, y, width, height);
        } else {
            g2.fillRect(x, y, height, width); // apverčiame
        }
    }

    public Rectangle getBounds() {
        if (direction == Direction.HORIZONTAL) {
            return new Rectangle(x, y, width, height);
        } else {
            return new Rectangle(x, y, height, width);
        }
    }

    public boolean isStopped() {
        return stopped;
    }

    public Direction getDirection() {
        return direction;
    }

    public int getX() { return x; }

    public int getY() { return y; }

    public int getWidth() {
        return direction == Direction.HORIZONTAL ? width : height;
    }

    public int getHeight() {
        return direction == Direction.HORIZONTAL ? height : width;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setStopped(boolean stopped) {
        this.stopped = stopped;
        if (stopped) {
            this.speed = 0;
        }
    }
}
