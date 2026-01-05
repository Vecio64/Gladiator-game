package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Apollo Class
 * Represents the first Boss of the game.
 * It moves horizontally, bounces off walls, and shoots "Sun" projectiles.
 * It has two phases: Normal and Enraged (Red & Fast).
 */
public class Apollo extends Boss {

    private BufferedImage image;
    private int speedX = GameConstants.APOLLO_SPEED;
    private boolean secondPhase = false; // Flag to track if the boss is in "Rage Mode"

    public Apollo(GameModel model) {
        // Call the parent constructor (Boss -> HostileEntity)
        // Parameters: x, y, width, height, HP, Score Points
        super(
                (GameConstants.FIELD_WIDTH - GameConstants.APOLLO_WIDTH) / 2, // Start in the middle
                GameConstants.HUD_HEIGHT, // Start below HUD
                GameConstants.APOLLO_WIDTH,
                GameConstants.APOLLO_HEIGHT,
                GameConstants.APOLLO_HP,
                GameConstants.APOLLO_SCORE_POINTS, // Score awarded when defeated
                model
        );
        this.image = ResourceManager.apolloImg;
    }

    @Override
    public void move() {
        // Update horizontal position
        x += speedX;

        // Bounce logic: If it hits the screen edges
        if (x <= 0 || x >= GameConstants.FIELD_WIDTH - width) {
            speedX = -speedX; // Reverse direction

            // Trigger shooting mechanism via GameModel
            // We pass the current phase status to decide if the Sun should be Red/Fast
            model.shootSun(x, y, speedX, secondPhase);
        }

        // Decrease the flash timer (inherited from HostileEntity) for the hit effect
        if (flashTimer > 0) flashTimer--;
    }

    @Override
    public void takeDamage(int dmg) {
        // 1. Apply damage using the parent class logic (reduces HP, checks death, adds score)
        super.takeDamage(dmg);

        // 2. Specific Logic for Apollo: Check for Second Phase (Rage Mode)
        // If HP drops below 50% and we are not yet in the second phase...
        if (hp <= maxHp / 2 && !secondPhase) {
            this.image = ResourceManager.apolloImg2; // Change sprite to Red Apollo
            this.speedX *= 2; // Double the movement speed
            secondPhase = true; // Activate the flag
            System.out.println("Apollo entering Phase 2!");
        }
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imgToDraw = (flashTimer > 0) ? ResourceManager.apolloHitImg : image;

        if (image != null) {
            // Directional Flipping Logic
            if (speedX > 0) {
                // Moving RIGHT: Draw normally
                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                // Moving LEFT: Flip the image horizontally
                // We draw starting at (x + width) and use a negative width to flip
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {
            // Fallback: Draw an Orange rectangle if images fail to load
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);
        }
    }
}