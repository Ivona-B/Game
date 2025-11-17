import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class TrafficController {

    private final int width;
    private final int height;
    private final int intersectionSize;
    private final int roadWidth;
    private final int safeGap;
    private final int maxQueue;

    public TrafficController(int width,
                             int height,
                             int intersectionSize,
                             int roadWidth,
                             int safeGap,
                             int maxQueue) {
        this.width = width;
        this.height = height;
        this.intersectionSize = intersectionSize;
        this.roadWidth = roadWidth;
        this.safeGap = safeGap;
        this.maxQueue = maxQueue;
    }


    //atstumai, judejimas
    public void updateCars(List<Car> cars,
                           TrafficLight horizontalLight,
                           TrafficLight verticalLight) {

        int centerX = width / 2;
        int centerY = height / 2;

        int stopX = centerX - intersectionSize / 2 - 10;
        int stopY = centerY - intersectionSize / 2 - 10;

        List<Car> horizontals = new ArrayList<>();
        List<Car> verticals = new ArrayList<>();
        for (Car c : cars) {
            if (c.getDirection() == Car.Direction.HORIZONTAL) {
                horizontals.add(c);
            } else {
                verticals.add(c);
            }
        }

        // horizontal masinos
        horizontals.sort(Comparator.comparingInt(Car::getX).reversed());
        Car front = null;

        for (Car car : horizontals) {
            boolean mustStop = false;

            boolean beforeIntersection =
                    car.getX() + car.getWidth() < centerX + intersectionSize / 2;
            boolean atStopLine =
                    car.getX() + car.getWidth() >= stopX && beforeIntersection;

            if (!horizontalLight.isGreen() && atStopLine) {
                mustStop = true;
            }

            if (front != null) {
                int gap = front.getX() - (car.getX() + car.getWidth());
                if (gap <= safeGap) {
                    mustStop = true;
                }
            }

            car.update(mustStop);

            if (front != null) {
                int gapAfter = front.getX() - (car.getX() + car.getWidth());
                if (gapAfter < safeGap) {
                    car.setX(front.getX() - safeGap - car.getWidth());
                    car.setStopped(true);
                }
            }

            front = car;
        }

        // vertical masinos
        verticals.sort(Comparator.comparingInt(Car::getY).reversed());
        front = null;

        for (Car car : verticals) {
            boolean mustStop = false;

            boolean beforeIntersection =
                    car.getY() + car.getHeight() < centerY + intersectionSize / 2;
            boolean atStopLine =
                    car.getY() + car.getHeight() >= stopY && beforeIntersection;

            if (!verticalLight.isGreen() && atStopLine) {
                mustStop = true;
            }

            if (front != null) {
                int gap = front.getY() - (car.getY() + car.getHeight());
                if (gap <= safeGap) {
                    mustStop = true;
                }
            }

            car.update(mustStop);

            if (front != null) {
                int gapAfter = front.getY() - (car.getY() + car.getHeight());
                if (gapAfter < safeGap) {
                    car.setY(front.getY() - safeGap - car.getHeight());
                    car.setStopped(true);
                }
            }

            front = car;
        }

        // remove nuvaziavusias
        Iterator<Car> it = cars.iterator();
        while (it.hasNext()) {
            Car car = it.next();
            if (car.getDirection() == Car.Direction.HORIZONTAL) {
                if (car.getX() > width + 200) {
                    it.remove();
                }
            } else {
                if (car.getY() > height + 200) {
                    it.remove();
                }
            }
        }
    }


    public boolean checkCollisions(List<Car> cars) {
        int centerX = width / 2;
        int centerY = height / 2;

        Rectangle intersectionArea = new Rectangle(
                centerX - intersectionSize / 2,
                centerY - intersectionSize / 2,
                intersectionSize,
                intersectionSize
        );

        for (int i = 0; i < cars.size(); i++) {
            Car a = cars.get(i);
            Rectangle aBounds = a.getBounds();

            if (!aBounds.intersects(intersectionArea)) {
                continue;
            }

            for (int j = i + 1; j < cars.size(); j++) {
                Car b = cars.get(j);

                if (a.getDirection() == b.getDirection()) {
                    continue;
                }

                Rectangle bBounds = b.getBounds();

                if (!bBounds.intersects(intersectionArea)) {
                    continue;
                }

                if (aBounds.intersects(bBounds)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean checkTrafficJam(List<Car> cars) {
        int stoppedH = 0;
        int stoppedV = 0;

        for (Car c : cars) {
            if (c.isStopped()) {
                if (c.getDirection() == Car.Direction.HORIZONTAL) {
                    stoppedH++;
                } else {
                    stoppedV++;
                }
            }
        }

        int totalStopped = stoppedH + stoppedV;
        return totalStopped >= maxQueue;
    }
}
