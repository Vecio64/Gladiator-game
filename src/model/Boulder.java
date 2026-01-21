package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * Boulder Class
 * A heavy projectile dropped by the StoneGolem.
 * It has Power Level 2, meaning it destroys Arrows/Feathers but is destroyed by the Sun.
 */
public class Boulder extends Projectile {

    private double preciseY;
    private double velY;
    private double gravity = GameConstants.BOULDER_GRAVITY; // Accelerates downwards

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
        this.velY = GameConstants.BOULDER_INITIAL_SPEED; // Initial throw speed
    }

    @Override
    public void move() {
        // Apply Gravity
        velY += gravity;
        preciseY += velY;
        y = (int) preciseY;

        // Despawn if off-screen
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            // Placeholder: Gray Rock
            g.setColor(Color.GRAY);
            g.fillOval(x, y, width, height);
        }
    }

    @Override
    public Shape getShape() {
        // set hitbox to a circle
        return new Ellipse2D.Float(x, y, width, height);
    }
}