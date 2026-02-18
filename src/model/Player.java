package model;

import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

/**
 * Player Class
 *
 * Represents the user-controlled character.
 * Handles movement, rendering, and defining a precise hitbox for collision detection.
 *
 * Key Features:
 * - Movement is constrained within the game field boundaries.
 * - Uses a reduced "Padding" hitbox to make dodging easier.
 */
public class Player extends GameObject {
    private int velX = 0; // Horizontal velocity
    private int velY = 0; // Vertical velocity
    private int speed;

    /**
     * Constructor for the Player.
     * Passes all parameters up to GameOjbect.
     * Starts the player centered horizontally at the bottom of the screen.
     *
     * @param image The sprite to use for the player.
     */
    public Player(BufferedImage image) {
        super((GameConstants.WINDOW_WIDTH - GameConstants.PLAYER_WIDTH) / 2, // Center X
                GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - GameConstants.PLAYER_HEIGHT, // Bottom Y
                GameConstants.PLAYER_WIDTH,
                GameConstants.PLAYER_HEIGHT,
                image);
        speed = GameConstants.PLAYER_SPEED;
    }

    /**
     * Updates the player's position based on velocity.
     * Contains logic to prevent the player from moving outside the screen boundaries.
     */
    @Override
    public void move() {
        x += velX;
        y += velY;

        // --- Boundary Checks ---
        // Left
        if (x < 0) x = 0;
        // Right
        if (x > GameConstants.WINDOW_WIDTH - width) x = GameConstants.WINDOW_WIDTH - width;
        // Top
        if (y < GameConstants.HUD_HEIGHT) y = GameConstants.HUD_HEIGHT;
        // Bottom
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height)
            y = GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height;
    }

    @Override
    public void draw(Graphics g) {
        if(image != null){
            g.drawImage(image, x, y, width, height, null);
        }
        else{
            // Fallback: Blue square if image fails
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }
    }

    /**
     * Override getShape to provide a smaller, precise hitbox.
     *
     * Instead of using the full sprite rectangle (which often includes transparent pixels),
     * we shrink the hitbox by 30% width and 20% height.
     * This "generous" hitbox makes the game feel fairer to the player.
     */
    @Override
    public Shape getShape() {
        float paddingX = width * 0.3f;  // Remove 30% from width
        float paddingY = height * 0.2f; // Remove 20% from height

        // Create a centered ellipse within the sprite bounds
        return new Ellipse2D.Float(
                x + paddingX / 2,     // Offset X inward
                y + paddingY / 2,     // Offset Y inward
                width - paddingX,     // Reduced width
                height - paddingY     // Reduced height
        );
    }

    // --- Input Handling Methods ---
    // Called by the Controller (GamePanel) to update velocity

    /**
     * Sets the horizontal velocity.
     * Accepts a normalized double (range -1.0 to 1.0) to handle diagonal precision.
     *
     * @param normalizedX The direction vector X component.
     */

    public void setVelX(double normalizedX) {
        // Multiply direction by speed and round to the nearest integer for pixel movement
        this.velX = (int) Math.round(normalizedX * speed);
    }

    /**
     * Sets the vertical velocity.
     * Accepts a normalized double (range -1.0 to 1.0) to handle diagonal precision.
     *
     * @param normalizedY The direction vector Y component.
     */
    public void setVelY(double normalizedY) {
        this.velY = (int) Math.round(normalizedY * speed);
    }
}