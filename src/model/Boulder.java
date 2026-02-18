package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Boulder Class
 *
 * A heavy projectile dropped by the Cyclops enemy.
 * It simulates gravity by accelerating downwards.
 *
 * Combat Logic:
 * - Power Level: 2 (Heavy).
 * - It will destroy Level 1 projectiles (Arrows) on impact.
 * - It can be destroyed by Level 3 projectiles (Sun/Lighting).
 */
public class Boulder extends Projectile {

    private double preciseY; // Double for smooth gravity calculation
    private double velY;     // Vertical velocity
    private double gravity = GameConstants.BOULDER_GRAVITY; // Acceleration per frame

    /**
     * Constructor for the Boulder.
     *
     * @param x The starting X coordinate (aligned with the horizontal center of the Cyclops).
     * @param y The starting Y coordinate (positioned just below the Cyclops).
     */

    public Boulder(int x, int y) {
        super(x, y,
                GameConstants.BOULDER_WIDTH,
                GameConstants.BOULDER_HEIGHT,
                ResourceManager.boulderImg,
                Alignment.ENEMY,
                2,
                GameConstants.BOULDER_DAMAGE
        );

        this.preciseY = y;
        this.velY = GameConstants.BOULDER_INITIAL_SPEED;
    }

    /**
     * Updates the boulder's position applying gravity logic.
     */
    @Override
    public void move() {
        // Apply Gravity: Increase velocity, then update position
        velY += gravity;
        preciseY += velY;
        y = (int) preciseY;

        // Despawn if it falls off the bottom of the screen
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            // Fallback: Gray Circle
            g.setColor(Color.GRAY);
            g.fillOval(x, y, width, height);
        }
    }

    /**
     * Override getShape to provide a Circular Hitbox.
     * This makes collision detection more accurate for a round object.
     */
    @Override
    public Shape getShape() {
        return new Ellipse2D.Float(x, y, width, height);
    }
}