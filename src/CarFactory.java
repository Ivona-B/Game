public class CarFactory{

    public static Car createHorizontal(int x, int y, double speed) {
        return new Car(x, y, Car.Direction.HORIZONTAL, speed);
    }

    public static Car createVertical(int x, int y, double speed) {
        return new Car(x, y, Car.Direction.VERTICAL, speed);
    }
}
