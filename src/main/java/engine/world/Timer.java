package engine.world;

/**
 * this class provides timer inside the game.
 */
public class Timer {

    private static final int DAY_LENGTH = 86400;
    private double timeTick = 0;
    private long lastTime;
    private final double gameSpeed;

    public Timer(double gameSpeed) {
        this.gameSpeed = gameSpeed;
    }

    public void init() {
        timeTick = 0;
        lastTime = System.currentTimeMillis();
    }

    public void update() {
        long now = System.currentTimeMillis();
        timeTick += (int) ((now - lastTime) * gameSpeed);
        if (timeTick > DAY_LENGTH) timeTick -= DAY_LENGTH;
        lastTime = now;
    }

    public double getTimeTick() {
        return timeTick;
    }

    public double getTimeRatio() {
        return timeTick / DAY_LENGTH;
    }

}
