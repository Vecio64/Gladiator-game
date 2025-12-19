package view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * ResourceManager
 * This class handles loading all game assets (images, sounds, etc.) at startup.
 * By loading images once and storing them in static variables, we improve performance
 * and avoid accessing the disk every time an object is spawned.
 */
public class ResourceManager {

    // Static variables to hold the loaded images.
    public static BufferedImage playerImg;
    public static BufferedImage enemyImg;
    public static BufferedImage arrowImg;
    public static BufferedImage featherImg;
    public static BufferedImage enemyHitImg;

    /**
     * Loads all resources from the "res" folder.
     * This method must be called once before the game starts.
     */
    public static void loadImages() {
        try {
            System.out.println("Loading resources...");

            // Load images from disk
            playerImg = ImageIO.read(new File("res/player.png"));
            enemyImg  = ImageIO.read(new File("res/enemy.png"));
            enemyHitImg = createWhiteSilhouette(enemyImg);
            arrowImg  = ImageIO.read(new File("res/arrow.png"));
            featherImg  = ImageIO.read(new File("res/feather.png"));


            System.out.println("Resources loaded successfully!");

        } catch (IOException e) {
            System.err.println("Error: Could not load images.");
            e.printStackTrace();
        }
    }
    // Method for creating the DMG version of the img
    private static BufferedImage createWhiteSilhouette(BufferedImage original) {
        // Make an empty copy with the same dimensions
        BufferedImage whiteImg = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        // scan each pixel
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int p = original.getRGB(x, y);

                // Alpha value (Transparency)
                int a = (p >> 24) & 0xff;

                // if the pixel is not transparent, make it pure white
                if (a > 0) {
                    int whiteColor = (a << 24) | (255 << 16) | (255 << 8) | 255;
                    whiteImg.setRGB(x, y, whiteColor);
                }
            }
        }
        return whiteImg;
    }

}