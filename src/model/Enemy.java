package model;

import view.ResourceManager; // Import the manager
import java.awt.*;
import java.awt.image.BufferedImage;

// --- Enemyクラス (敵) ---
class Enemy extends GameObject {

    private BufferedImage image;

    private int velX;
    private int velY;

    public Enemy(int x, int y) {
        super(x, y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT); // 30x30の四角
        this.image = ResourceManager.enemyImg;

        // --- SETUP MOVEMENT ---
        // Vertical speed: Slow
        this.velY = GameConstants.ENEMY_YSPEED;

        // Horizontal speed: Fast
        this.velX = GameConstants.ENEMY_XSPEED;

        // Randomize initial direction (50% chance left or right)
        // Math.random() returns 0.0 to 1.0
        if (Math.random() < 0.5) {
            this.velX = -this.velX;
        }
    }
    @Override
    public void move() {
        // Update positions
        x += velX;
        y += velY;

        // --- BOUNCE LOGIC ---

        // Check Left Border
        if (x < 0) {
            x = 0;          // Fix position to avoid sticking
            velX = -velX;   // Reverse direction (become positive)
        }

        // Check Right Border
        if (x > GameConstants.SCREEN_WIDTH - width) {
            x = GameConstants.SCREEN_WIDTH - width; // Fix position
            velX = -velX;   // Reverse direction (become negative)
        }

        // Check Left Border
        if (y < 0) {
            y = 0;          // Fix position to avoid sticking
            velY = -velY;   // Reverse direction (become positive)
        }

        // Check Right Border
        if (y > GameConstants.SCREEN_HEIGHT - height) {
            y = GameConstants.SCREEN_HEIGHT - height; // Fix position
            velY = -velY;   // Reverse direction (become negative)
        }





    }
    @Override
    public void draw(Graphics g) {
        if (image != null) {
            // Check direction to flip image
            if (velX < 0) {
                // Moving RIGHT: Draw normal
                // (Assuming the original sprite faces Right)
                g.drawImage(image, x, y, width, height, null);
            } else {
                // Moving LEFT: Draw flipped (Mirror effect)
                // We start at (x + width) and draw with negative width
                g.drawImage(image, x + width, y, -width, height, null);
            }
        } else {
            // Fallback (Red square)
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }
}



