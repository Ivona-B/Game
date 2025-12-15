import java.util.List;
import java.util.Random;

public class CarSpawner {

    private final int width;
    private final int height;
    private final Random random = new Random();

    private long nextHorizontalSpawnTime = 0;
    private long nextVerticalSpawnTime = 0;

    public CarSpawner(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void reset(long now, int level) {
        nextHorizontalSpawnTime = now + randomSpawnInterval(level);
        nextVerticalSpawnTime = now + randomSpawnInterval(level);
    }

    private long randomSpawnInterval(int level) {
        return switch (level) {
            case 1 -> 2000 + random.nextInt(4000);   // 2 – 6 sekundės
            case 2 -> 1200 + random.nextInt(3000);   // 1.2 – 4.2 sekundės
            case 3 -> 800 + random.nextInt(2000);    // 0.8 – 2.8 sekundės
            default -> 2000 + random.nextInt(4000);
        };
    }


    public void spawnCars(List<Car> cars, double carSpeed, int level, long now) {
        int horizontalCount = 0;
        int verticalCount = 0;

        for (Car c : cars) {
            if (c.getDirection() == Car.Direction.HORIZONTAL) {
                horizontalCount++;
            } else {
                verticalCount++;
            }
        }

        int maxPerDirection = 10;

        if (horizontalCount < maxPerDirection && now >= nextHorizontalSpawnTime) {
            cars.add(CarFactory.createHorizontal(-120, height / 2 - 25, carSpeed));
            nextHorizontalSpawnTime = now + randomSpawnInterval(level);
        }

        if (verticalCount < maxPerDirection && now >= nextVerticalSpawnTime) {
            cars.add(CarFactory.createVertical(width / 2 - 25, -120, carSpeed));
            nextVerticalSpawnTime = now + randomSpawnInterval(level);
        }
    }
}
