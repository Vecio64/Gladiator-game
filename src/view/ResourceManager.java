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

            playerImg = loadTexture("res/player.png");
            enemyImg  = loadTexture("res/enemy.png");
            // enemyHitImg = createWhiteSilhouette(enemyImg);
            arrowImg  = loadTexture("res/arrow.png");
            featherImg = loadTexture("res/feather.png");

            System.out.println("Resources loaded successfully!");
        } catch (IOException e) {
            System.err.println("Error: Could not load images.");
            e.printStackTrace();
        }
    }
    
    private static BufferedImage loadTexture(String path) throws IOException {
        java.net.URL url = ResourceManager.class.getClassLoader().getResource(path);

        if (url == null) {
            throw new IOException("Image not found: " + path);
        }
        return ImageIO.read(url);
    }
}