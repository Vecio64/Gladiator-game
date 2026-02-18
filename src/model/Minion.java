package model;

import java.awt.image.BufferedImage;

/**
 * Minion (Abstract)
 *
 * Represents standard, non-boss enemies (e.g., Harpies, Cyclopes).
 * This class serves as a grouping layer between HostileEntity and specific enemy types.
 * It simplifies type checking (e.g., `if (obj instanceof Minion)`) and shared behaviors for common enemies.
 */
public abstract class Minion extends HostileEntity {

    /**
     * Constructor for a Minion.
     * Passes all parameters up to HostileEntity.
     */
    public Minion(int x, int y, int w, int h, BufferedImage image, int hp, int scorePoints, GameModel model) {
        super(x, y, w, h, image, hp, scorePoints, model);
    }
}