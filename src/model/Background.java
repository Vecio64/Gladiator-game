package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Background Class
 * Handles the infinite scrolling background using a "Mirroring" technique.
 * It draws the original image and a vertically flipped copy above it
 * to create a seamless loop without needing a specific seamless texture.
 */
public class Background {
    private double y; // Vertical position (double for smooth movement)
    private double speed; // Scrolling speed
    private BufferedImage image;

    // Screen dimensions
    private final int WIDTH = GameConstants.WINDOW_WIDTH;
    private final int HEIGHT = GameConstants.FIELD_HEIGHT / 2;

    public Background() {
        this.image = ResourceManager.stage1Img;
        this.speed = 0;
        this.y = GameConstants.HUD_HEIGHT;
    }

    public void setImage(BufferedImage newImage){
        this.image = newImage;
    }

    public void setSpeed(double newSpeed) {
        this.speed = newSpeed;
    }

    public void update() {
        // Move the background downwards
        y += speed;

        // Reset position when a full cycle is completed to prevent overflow
        if (y >= HEIGHT * 2 + GameConstants.HUD_HEIGHT) {
            y = GameConstants.HUD_HEIGHT;
        }
    }

    public void draw(Graphics g) {
        if (image == null) return;

        int currentY = (int) y;

        if (isStage2Img()){
            // DRAWING STRATEGY:
            // The screen is 800px tall. Our "Tile" is 400px.
            // We need to cover the screen from Y=0 to Y=800.
            // Since 'currentY' moves down, we need to draw tiles above and below it.

            // We assume the pattern is: [Normal] [Flipped] [Normal] [Flipped] ...

            // 1. Draw Normal Tile at current Y
            // Covers: y to y+400  x
            drawForceSize(g, currentY, false);

            // 2. Draw Flipped Tile BELOW
            // Covers: y+400 to y+800
            drawForceSize(g, currentY + HEIGHT, true);

            // 3. Draw Flipped Tile ABOVE
            // Covers: y-400 to y. (Crucial for when y starts at 0 or is small)
            drawForceSize(g, currentY - HEIGHT, true);

            // 4. Draw Normal Tile ABOVE that
            // Covers: y-800 to y-400. (Crucial for the loop wrap-around)
            drawForceSize(g, currentY - (HEIGHT * 2), false);
        } else {
            g.drawImage(image, 0, GameConstants.HUD_HEIGHT, WIDTH, HEIGHT * 2, null);
        }
    }

    /**
     * Helper method to draw a tile either normally or vertically flipped.
     * @param g Graphics context
     * @param yPos The Y position to draw at
     * @param isFlipped If true, draws the image upside down (mirrored)
     */
    private void drawForceSize(Graphics g, int yPos, boolean isFlipped) {
        if (!isFlipped) {
            // NORMAL: Force width 600, height 400
            g.drawImage(image, 0, yPos, WIDTH, HEIGHT, null);
        } else {
            // FLIPPED: Force width 600, height 400 (but drawn upwards)
            // Destination Y starts at bottom (yPos + HEIGHT)
            // Height is negative (-HEIGHT) to flip it
            g.drawImage(image, 0, yPos + HEIGHT, WIDTH, -HEIGHT, null);
        }
    }

    private boolean isStage2Img(){
        return (this.image == ResourceManager.stage2Img);
    }
}