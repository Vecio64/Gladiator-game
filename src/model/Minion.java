package model;

import java.awt.image.BufferedImage;

/**
 * Minion Class
 * Abstract class representing basic non-boss enemies.
 * Used to group Harpies, Golems, etc., and manage shared logic like scoring.
 */
public abstract class Minion extends HostileEntity {
    public Minion(int x, int y, int w, int h, BufferedImage image, int hp, int scorePoints, GameModel model) {
        super(x, y, w, h, image, hp, scorePoints, model);
    }
}