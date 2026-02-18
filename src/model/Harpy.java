package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Harpy Class
 *
 * Behavior:
 * - Bounces around the screen borders like a DVD screensaver.
 * - Periodically drops "Feather" projectiles.
 * - Confined within the game field (cannot fly into the HUD).
 */
public class Harpy extends Minion {

    private int velX; // Horizontal velocity
    private int velY; // Vertical velocity
    private int fireTimer;
    private boolean isInScreen; // Flag to ensure it fully enters the screen before bouncing off the top

    public Harpy(int x, int y, GameModel model) {
        // Pass params to parent
        super(x, y,
                GameConstants.HARPY_WIDTH,
                GameConstants.HARPY_HEIGHT,
                ResourceManager.harpyImg,
                GameConstants.HARPY_HP,
                GameConstants.HARPY_SCORE_POINTS,
                model);

        // --- Movement Setup ---
        this.velY = GameConstants.HARPY_VELY;
        this.velX = GameConstants.HARPY_VELX;
        this.isInScreen = false;

        // Randomize initial horizontal direction (Left or Right)
        if (Math.random() < 0.5) {
            this.velX = -this.velX;
        }

        resetFireTimer();
    }

    @Override
    public void move() {
        super.move();

        // Update Position
        x += velX;
        y += velY;

        // --- Boundary Bounce Logic ---

        // Left Wall
        if (x < 0) {
            x = 0;
            velX = -velX;
        }
        // Right Wall
        if (x > GameConstants.WINDOW_WIDTH - width) {
            x = GameConstants.WINDOW_WIDTH - width;
            velX = -velX;
        }

        // Top Wall (Only bounce if we have fully entered the screen first)
        // This prevents Harpies from getting stuck at the top when spawning.
        if (y < GameConstants.HUD_HEIGHT && isInScreen) {
            y = GameConstants.HUD_HEIGHT;
            velY = -velY;
        }

        // Check if entered screen
        if (y > GameConstants.HUD_HEIGHT){
            isInScreen = true;
        }

        // Bottom Wall
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height) {
            y = GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height;
            velY = -velY;
        }

        // --- Shooting Logic ---
        if(fireTimer > 0) {
            fireTimer--;
        } else {
            throwFeather();
            resetFireTimer();
        }
    }

    private void throwFeather(){
        // Spawn feather centered horizontally below the Harpy
        Feather f = new Feather (x + (width - GameConstants.FEATHER_WIDTH)/2, y + height);
        model.spawnEnemyProjectile(f);
    }

    /**
     * Resets the shooting timer with random variance.
     */
    public void resetFireTimer() {
        int base = GameConstants.FEATHER_FIRE_INTERVAL;
        int variance = GameConstants.FEATHER_FIRE_VARIANCE;

        // Calculate random modifier: +/- variance
        int randomVariation = (int)(Math.random() * (variance * 2)) - variance;

        this.fireTimer = base + randomVariation;
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imgToDraw = (flashTimer > 0) ? ResourceManager.harpyHitImg : image;

        if (image != null) {
            // Flip sprite based on horizontal direction
            if (velX < 0) {
                // Moving Left
                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                // Moving Right (Mirror Image)
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {
            // Fallback
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }
}