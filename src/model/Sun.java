package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Sun Class
 *
 * A BossProjectile used by Apollo (Boss) or the Player (Ability 1).
 * It moves in a calculated trajectory and bounces off walls.
 * Only for this object the x and y values are centerded in
 * the middle of the image, (instead of the typical top left position)
 * to make the shrinking effect
 *
 * Key Features:
 * - Power Level 3 (Ultimate).
 * - Penetrating: Does not disappear after hitting one target.
 * - Variable Size: If used by the Player, it shrinks as it takes damage (loses HP).
 */
public class Sun extends BossProjectile {

    private double preciseX, preciseY;
    private double velX, velY;
    private double initialSize; // Stored to calculate shrinking ratio

    /**
     * Constructor for Sun Projectile.
     * @param summonerX X position of the entity creating the sun.
     * @param summonerY Y position of the entity creating the sun.
     * @param ApolloVelX velocity of Apollo (only if Apollo is the summoner)
     * @param isSecondPhase If true, spawns a faster, red sun. (only if Apollo is the summoner)
     * @param friendly If true, belongs to Player; else belongs to Apollo.
     */
    public Sun(int summonerX, int summonerY, int ApolloVelX, boolean isSecondPhase, boolean friendly) {
        // Call parent constructor
        super(0, 0,
                GameConstants.SUN_WIDTH,
                GameConstants.SUN_HEIGHT,
                isSecondPhase ? ResourceManager.sunImg2 : ResourceManager.sunImg,
                friendly ? Alignment.PLAYER : Alignment.ENEMY,
                3, // Ultimate Power Level
                1);

        this.isPlayerProjectile = friendly;
        this.isPenetrating = true; // Sun cuts through enemies

        // If Player uses it, give it HP so it can be "worn down"
        if (isPlayerProjectile) {
            maxHP = GameConstants.SUN_HP;
            currentHP = maxHP;
        }

        this.initialSize = GameConstants.SUN_WIDTH;

        // --- Velocity Calculation ---
        velX = 1;
        velY = 1;
        // if second phase give higher speed
        double currentSpeed = isSecondPhase ? GameConstants.SUN_SPEED2 : GameConstants.SUN_SPEED1;
        // angle for calculating velX and velY
        double angleRadians;

        if (!friendly){
            // --- ENEMY LOGIC (APOLLO) ---
            // Spawn relative to Apollo's position and direction
            preciseX = (ApolloVelX > 0) ? (summonerX + GameConstants.APOLLO_WIDTH + width / 2) : (summonerX - width / 2);
            preciseY = summonerY + height;

            // Calculate random angle downwards (between 20 and 70 degrees)
            double minAngle = 20.0;
            double maxAngle = 70.0;
            angleRadians = Math.toRadians(minAngle + Math.random() * (maxAngle - minAngle));

        } else {
            // --- PLAYER LOGIC (ABILITY) ---
            // Spawn above player
            preciseX = summonerX;
            preciseY = summonerY - height / 2;

            // Clamp spawn position to screen bounds
            // left bound
            if (preciseX < width / 2) preciseX = width / 2;
            // right bound
            if (preciseX > GameConstants.WINDOW_WIDTH - width / 2) preciseX = GameConstants.WINDOW_WIDTH - width / 2;

            // Launch upwards at a 30-degree angle
            angleRadians = Math.toRadians(360 - 30);
            velX = (Math.random() < 0.5) ? 1 : -1; // Randomize left/right start
        }

        // Apply calculated velocity components
        velX *= currentSpeed * Math.cos(angleRadians);
        velY *= currentSpeed * Math.sin(angleRadians);

        // Flip X direction if APOLLO is moving left
        if (ApolloVelX < 0) {
            velX = -velX;
        }

        // Set initial integer positions
        x = (int) preciseX;
        y = (int) preciseY;
    }

    @Override
    public void move() {
        preciseX += velX;
        preciseY += velY;

        x = (int) preciseX;
        y = (int) preciseY;

        // Calculate current radius (it might shrink)
        double currentRadius = getCurrentSize() / 2.0;

        // --- Wall Bounce Logic ---
        // Left Wall
        if (x - currentRadius < 0) {
            x = (int)currentRadius;
            preciseX = x;
            velX = -velX; // Reverse X velocity
        }
        // Right Wall
        if (x + currentRadius > GameConstants.WINDOW_WIDTH) {
            x = (int)(GameConstants.WINDOW_WIDTH - currentRadius);
            preciseX = x;
            velX = -velX; // Reverse X velocity
        }

        // --- Despawn Logic ---
        // Remove if it goes off top or bottom of screen
        if (y - currentRadius > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT || // TOP
                y + currentRadius < GameConstants.HUD_HEIGHT) { // BOTTOM
            isDead = true;
        }
    }

    /**
     * Calculates the current diameter of the Sun.
     * If owned by the player, it shrinks proportionally to its remaining HP.
     */
    private double getCurrentSize() {
        if (!isPlayerProjectile) return initialSize;

        // Shrink ratio: currentHP / maxHP
        double ratio = (double) currentHP / maxHP;
        return initialSize * ratio;
    }

    @Override
    public void draw(Graphics g) {
        double size = getCurrentSize();
        double radius = size / 2.0;

        // Center the drawing coordinates based on the (potentially shrunk) size
        int drawX = (int) (x - radius);
        int drawY = (int) (y - radius);
        int drawSize = (int) size;

        if (image != null) {
            g.drawImage(image, drawX, drawY, drawSize, drawSize, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, width, height);
        }
    }

    @Override
    public Shape getShape() {
        double size = getCurrentSize();
        double radius = size / 2.0;
        // give x and y with the normal (top left) coordinates instead of the centered coordinates
        return new Ellipse2D.Float((float)(x - radius), (float)(y - radius), (float)size, (float)size);
    }
}