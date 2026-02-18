package model;

import view.ResourceManager;
import java.awt.*;

/**
 * Feather Class
 *
 * A light projectile fired by the Harpy enemy.
 * It travels vertically downwards at a constant speed.
 *
 * Combat Logic:
 * - Power Level: 1 (Light).
 * - Equivalent to the Player's Arrow. They will destroy each other on impact.
 */
public class Feather extends Projectile {

    /**
     * Constructor for the Feather projectile.
     *
     * @param x The starting X coordinate (aligned with the horizontal center of the Harpy).
     * @param y The starting Y coordinate (positioned just below the Harpy's feet).
     */

    public Feather(int x, int y) {
        // Initialize Projectile properties:
        // - Alignment: ENEMY (Targets Player)
        // - Power Level: 1 (Light)
        super(x, y, GameConstants.FEATHER_WIDTH, GameConstants.FEATHER_HEIGHT, ResourceManager.featherImg,
                Alignment.ENEMY, 1, GameConstants.FEATHER_DAMAGE);
    }

    @Override
    public void move() {
        y += GameConstants.FEATHER_SPEED;

        // Despawn if it leaves the bottom of the screen
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (ResourceManager.featherImg != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, width, height);
        }
    }
}