package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Apollo Class
 *
 * Represents the first Boss of the game.
 * Apollo moves horizontally across the screen, bouncing off the edges.
 * He attacks by shooting "Sun" projectiles at the player.
 *
 * This boss implements a simple 2-Phase Logic:
 * 1. Normal Phase: Standard movement speed.
 * 2. Enraged Phase (HP < 50%): Image changes to red, movement speed doubles.
 */
public class Apollo extends Boss {

    private int velX = GameConstants.APOLLO_SPEED1; // Current horizontal speed
    private boolean secondPhase = false; // Flag to track if "Rage Mode" is active

    /**
     * Constructor for Apollo.
     * Passes all parameters up to Boss.
     */
    public Apollo(GameModel model) {
        super(
                (GameConstants.WINDOW_WIDTH - GameConstants.APOLLO_WIDTH) / 2, // Start centered horizontally
                GameConstants.HUD_HEIGHT, // Start just below the Top HUD
                GameConstants.APOLLO_WIDTH,
                GameConstants.APOLLO_HEIGHT,
                ResourceManager.apolloImg,
                GameConstants.APOLLO_HP,
                GameConstants.APOLLO_SCORE_POINTS,
                model
        );
    }

    /**
     * Updates Apollo's position and handles wall bouncing logic.
     */
    @Override
    public void move() {
        super.move(); // Handles basic update logic if any

        // Update horizontal position
        x += velX;

        // Bounce Logic: Check if Apollo hits the left or right screen edge
        if (x <= 0 || x >= GameConstants.WINDOW_WIDTH - width) {
            velX = -velX; // Reverse direction

            // Trigger Attack: Shoot a Sun projectile whenever bouncing off a wall
            shootSun();
        }
    }

    /**
     * Spawns a Sun projectile at Apollo's current position.
     * The projectile inherits Apollo's current phase state (Normal or Enraged).
     */
    private void shootSun(){
        // Spawn a Sun. 'secondPhase' determines if it is a fast/red sun.
        Sun s = new Sun(x, y, velX, secondPhase, false);
        model.spawnEnemyProjectile(s);
    }

    /**
     * Handles taking damage and triggers Phase 2 transition logic.
     *
     * @param dmg Amount of damage to take.
     */
    @Override
    public void takeDamage(int dmg) {
        // 1. Apply damage using standard HostileEntity logic (reduces HP, checks death)
        super.takeDamage(dmg);

        // 2. Phase Transition Logic
        // If HP drops below 50%
        if (hp <= maxHp / 2 && !secondPhase) {
            image = ResourceManager.apolloImg2; // Change sprite to "Red Apollo"

            // Double the current speed (preserving direction)
            velX = (velX > 0) ? GameConstants.APOLLO_SPEED2 : -GameConstants.APOLLO_SPEED2;

            secondPhase = true; // Lock this state so it doesn't trigger again
        }
    }

    /**
     * Draws Apollo to the screen.
     * Includes logic to flip the sprite horizontally based on movement direction.
     */
    @Override
    public void draw(Graphics g) {
        // Choose image: Flash white if hit recently, otherwise show normal/rage sprite
        BufferedImage imgToDraw = (flashTimer > 0) ? ResourceManager.apolloHitImg : image;

        if (imgToDraw != null) {
            // Directional Flipping Logic
            if (velX > 0) {
                // Moving RIGHT: Draw normally
                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                // Moving LEFT: Flip image horizontally
                // We draw at (x + width) with a negative width (-width) to mirror it
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {
            // Fallback: Draw rectangle if image loading failed
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);
        }
    }
}