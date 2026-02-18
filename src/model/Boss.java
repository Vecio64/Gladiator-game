package model;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Boss Class (Abstract)
 *
 * Represents a major enemy entity (Boss).
 * Extends HostileEntity
 *
 * Key Feature:
 * - Notifies the GameModel when defeated via `bossDefeated()`.
 */
public abstract class Boss extends HostileEntity {

    /**
     * Constructor for Boss.
     * Passes all parameters up to HostileEntity.
     */
    public Boss(int x, int y, int w, int h, BufferedImage image, int hp, int scorePoints, GameModel model) {
        super(x, y, w, h, image, hp, scorePoints, model);
    }

    /**
     * Handles receiving damage.
     * Checks if the Boss has died and triggers the level progression logic.
     */
    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);

        // If HP <= 0, notify the model to proceed to the next stage
        if (this.isDead){
            model.bossDefeated();
        }
    }
}