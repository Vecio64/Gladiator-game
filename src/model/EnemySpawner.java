package model;

import java.util.Random;

/**
 * EnemySpawner Class
 * Manages the spawn timing for a specific type of enemy.
 * Allows independent spawn rates for different enemies (e.g., Harpies vs Golems).
 */
public class EnemySpawner {

    private Class<? extends Minion> enemyType; // The class of the enemy to spawn
    private int baseInterval;   // Frames between spawns (average)
    private int variance;       // Random variation in frames
    private int timer;          // Current countdown timer
    private Random rand = new Random();

    public EnemySpawner(Class<? extends Minion> enemyType, int baseInterval, int variance) {
        this.enemyType = enemyType;
        this.baseInterval = baseInterval;
        this.variance = variance;
        resetTimer(); // Start the timer immediately
    }

    /**
     * Updates the timer.
     * @return true if it's time to spawn an enemy, false otherwise.
     */
    public boolean update() {
        timer--;
        if (timer <= 0) {
            resetTimer();
            return true;
        }
        return false;
    }

    private void resetTimer() {
        // Calculate next spawn time: base +/- random variance
        int var = (variance > 0) ? rand.nextInt(variance * 2 + 1) - variance : 0;
        this.timer = baseInterval + var;
        if (this.timer < 30) this.timer = 30; // Minimum safety limit (0.5 sec)
    }

    /**
     * Increases difficulty by reducing the spawn interval.
     * @param multiplier Factor to multiply interval (e.g., 0.8 for 20% faster).
     */
    public void increaseDifficulty(double multiplier) {
        this.baseInterval = (int)(this.baseInterval * multiplier);
        this.variance = (int)(this.variance * multiplier);
        // Prevent it from becoming too fast (e.g., limit to 60 frames)
        if (this.baseInterval < 60) this.baseInterval = 60;
    }

    public Class<? extends Minion> getEnemyType() {
        return enemyType;
    }
}