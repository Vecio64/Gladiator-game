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
            arrowImg  = ImageIO.read(new File("res/arrow.png"));
            featherImg  = ImageIO.read(new File("res/feather.png"));

            System.out.println("Resources loaded successfully!");

        } catch (IOException e) {
            System.err.println("Error: Could not load images.");
            e.printStackTrace();
        }
    }
}