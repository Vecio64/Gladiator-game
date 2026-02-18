package model;

import java.util.Random;

/**
 * EnemySpawner Class
 *
 * Manages the automatic spawning of a specific type of enemy.
 * It uses a timer that resets with a random variance after each spawn,
 * ensuring enemies don't appear in a predictable, robotic pattern.
 */
public class EnemySpawner {

    private Class<? extends Minion> enemyType; // The class of the enemy to spawn (e.g., Harpy.class)
    private int baseInterval;   // Average frames between spawns
    private int variance;       // Random variation (+/- frames)
    private int timer;          // Current countdown timer
    private Random rand = new Random();

    /**
     * Constructor.
     * @param enemyType The class of the enemy to spawn.
     * @param baseInterval Base time between spawns.
     * @param variance Random time added/subtracted from base.
     */
    public EnemySpawner(Class<? extends Minion> enemyType, int baseInterval, int variance) {
        this.enemyType = enemyType;
        this.baseInterval = baseInterval;
        this.variance = variance;
        resetTimer(); // Start the timer immediately
    }

    /**
     * Decrements the timer.
     * @return true if the timer reached 0 (time to spawn), false otherwise.
     */
    public boolean update() {
        timer--;
        if (timer <= 0) {
            resetTimer(); // Prepare for the next spawn
            return true;
        }
        return false;
    }

    /**
     * Resets the spawn timer with a new random value.
     */
    private void resetTimer() {
        // Calculate next spawn time: base +/- random variance
        int var = (variance > 0) ? rand.nextInt(variance * 2 + 1) - variance : 0;
        this.timer = baseInterval + var;

        // Safety: Ensure timer is never too low (e.g., prevent instant spamming)
        if (this.timer < 30) this.timer = 30;
    }

    /**
     * Dynamically increases difficulty by reducing the spawn interval.
     * @param multiplier Factor to multiply interval (e.g., 0.8 reduces time by 20%).
     */
    public void increaseDifficulty(double multiplier) {
        this.baseInterval = (int)(this.baseInterval * multiplier);
        this.variance = (int)(this.variance * multiplier);

        // Cap the speed to prevent the game from becoming impossible
        if (this.baseInterval < 60) this.baseInterval = 60;
    }

    public Class<? extends Minion> getEnemyType() {
        return enemyType;
    }
}