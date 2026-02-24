package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Arrow Class
 *
 * Represents the standard projectile fired by the Player.
 * It travels vertically upwards and is destroyed when it goes off-screen.
 */
public class Arrow extends Projectile {

    /**
     * Constructor.
     *
     * @param x Starting X coordinate (centered on Player).
     * @param y Starting Y coordinate (top of Player).
     * @param arrowDamage Damage this arrow inflicts on enemies.
     */
    public Arrow(int x, int y, int arrowDamage, BufferedImage image) {
        // Initialize Projectile properties:
        // - Image: Standard Arrow Image
        // - Alignment: PLAYER (Harms Enemies)
        // - Power Level: 1 (Weakest projectile type, destroyed by heavier objects)
        super(x, y, GameConstants.ARROW_WIDTH, GameConstants.ARROW_HEIGHT, image,
                Alignment.PLAYER, 1, arrowDamage);
    }

    /**
     * Moves the arrow upwards.
     * Marks itself as "dead" (to be removed) if it leaves the screen.
     */
    @Override
    public void move() {
        y -= GameConstants.ARROW_SPEED;

        // Remove if completely off-screen (top)
        if (y < -height) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (ResourceManager.arrowImg != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            // Fallback
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }
    }
}