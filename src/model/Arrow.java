package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Arrow Class
 * Projectile fired by the player. Moves upwards.
 */
public class Arrow extends Projectile {

    private BufferedImage image;

    public Arrow(int x, int y) {
        // We pass the VARIABLE damage from GameConstants (or Player stats in the future)
        super(x, y, GameConstants.ARROW_WIDTH, GameConstants.ARROW_HEIGHT, GameConstants.ARROW_DAMAGE);
        this.image = ResourceManager.arrowImg;
    }

    @Override
    public void move() {
        y -= GameConstants.ARROW_SPEED; // Moves Up

        // Use the common check from Projectile class
        if (isOutOfBounds()) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }
    }
}