package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Background Class
 *
 * Handles the infinite scrolling background effect.
 * Instead of requiring a seamless texture, this class uses a "Mirroring" technique:
 * it draws the image normally, then draws a vertically flipped copy above/below it.
 * This ensures the edges always match perfectly, creating a smooth loop.
 */
public class Background {
    private double y;      // Current vertical position (double for precise scrolling)
    private double speed;  // Pixels per frame to scroll
    private BufferedImage image;

    // Dimensions derived from GameConstants
    private final int WIDTH = GameConstants.WINDOW_WIDTH;
    private final int HEIGHT = GameConstants.FIELD_HEIGHT / 2; // Height of one "tile"

    public Background() {
        this.image = ResourceManager.stage1Img; // Default to Stage 1
        this.speed = 0; // Starts stationary until game begins
        this.y = GameConstants.HUD_HEIGHT;
    }

    public void setImage(BufferedImage newImage){
        this.image = newImage;
    }

    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
    }

    /**
     * Updates the background position.
     * Resets 'y' when a full cycle is completed to prevent coordinate overflow.
     */
    public void update() {
        y += speed;

        // Reset position after scrolling past two full tiles (Normal + Flipped)
        if (y >= HEIGHT * 2 + GameConstants.HUD_HEIGHT) {
            y = GameConstants.HUD_HEIGHT;
        }
    }

    /**
     * Draws the background tiles.
     * Checks if we are in "Stage 2" mode to apply specific tiling logic if needed.
     */
    public void draw(Graphics g) {
        if (image == null) return;

        int currentY = (int) y;

        // Stage 2 logic (assumed to use the mirroring technique)
        if (this.image == ResourceManager.stage2Img){
            // DRAWING STRATEGY:
            // We need to cover the entire screen height.
            // Since our tile is smaller, and the background moves,
            // we draw a chain of tiles: Normal -> Flipped -> Normal -> Flipped.

            // 1. Draw Normal Tile at current Y
            drawForceSize(g, currentY, false);

            // 2. Draw Flipped Tile BELOW it (y + HEIGHT)
            drawForceSize(g, currentY + HEIGHT, true);

            // 3. Draw Flipped Tile ABOVE it (y - HEIGHT)
            // Essential for when 'currentY' is near 0
            drawForceSize(g, currentY - HEIGHT, true);

            // 4. Draw Normal Tile ABOVE that (y - 2*HEIGHT)
            // Essential for the seamless loop wrap-around
            drawForceSize(g, currentY - (HEIGHT * 2), false);
        } else {
            // Default drawing (static background )
            g.drawImage(image, 0, GameConstants.HUD_HEIGHT, WIDTH, HEIGHT * 2, null);
        }
    }

    /**
     * Helper to draw a tile either normally or vertically flipped.
     *
     * @param g Graphics context
     * @param yPos The Y coordinate to draw at
     * @param isFlipped If true, mirrors the image vertically
     */
    private void drawForceSize(Graphics g, int yPos, boolean isFlipped) {
        if (!isFlipped) {
            // Normal Draw
            g.drawImage(image, 0, yPos, WIDTH, HEIGHT, null);
        } else {
            // Flipped Draw:
            // - Destination Y starts at the bottom of the target area (yPos + HEIGHT)
            // - Height is negative (-HEIGHT) to draw upwards, effectively flipping it
            g.drawImage(image, 0, yPos + HEIGHT, WIDTH, -HEIGHT, null);
        }
    }
}