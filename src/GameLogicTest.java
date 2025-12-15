import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameLogicTest {

    // juda
    @Test
    void carMovesWhenNotStopped() {
        Car car = Car.createHorizontal(0, 0, 5);
        car.update(false);
        assertTrue(car.getX() > 0);
    }

    // sustoja
    @Test
    void carStopsWhenMustStop() {
        Car car = Car.createHorizontal(0, 0, 5);
        car.update(true);
        assertTrue(car.isStopped());
    }

    // auto nejuda
    @Test
    void stoppedCarDoesNotMove() {
        Car car = Car.createHorizontal(10, 0, 5);
        car.update(true);
        int x = car.getX();
        car.update(true);
        assertEquals(x, car.getX());
    }

    // sviesoforo busena
    @Test
    void trafficLightChangesState() {
        TrafficLight light = new TrafficLight(0, 0);
        light.setState(TrafficLight.State.RED);
        light.nextState();
        assertTrue(light.isGreen());
    }

    // spustis
    @Test
    void detectsTrafficJam() {
        TrafficController controller = new TrafficController(
                800, 600, 120, 120, 20, 3
        );

        List<Car> cars = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            Car car = Car.createHorizontal(0, 0, 2);
            car.setStopped(true);
            cars.add(car);
        }

        assertTrue(controller.checkTrafficJam(cars));
    }

    // kada spusties nera
    @Test
    void noTrafficJamWhenFewCarsStopped() {
        TrafficController controller = new TrafficController(
                800, 600, 120, 120, 20, 5
        );

        List<Car> cars = new ArrayList<>();
        Car car = Car.createHorizontal(0, 0, 2);
        car.setStopped(true);
        cars.add(car);

        assertFalse(controller.checkTrafficJam(cars));
    }

    //  susidurimas sankr.
    @Test
    void detectsCollisionInIntersection() {
        TrafficController controller = new TrafficController(
                800, 600, 120, 120, 20, 6
        );

        Car h = Car.createHorizontal(390, 290, 0);
        Car v = Car.createVertical(390, 290, 0);

        List<Car> cars = List.of(h, v);

        assertTrue(controller.checkCollisions(cars));
    }

    // objekto busena auto be avarijos
    @Test
    void noCollisionWhenCarsSeparated() {
        TrafficController controller = new TrafficController(
                800, 600, 120, 120, 20, 6
        );

        Car h = Car.createHorizontal(0, 0, 0);
        Car v = Car.createVertical(700, 500, 0);

        List<Car> cars = List.of(h, v);

        assertFalse(controller.checkCollisions(cars));
    }
}
