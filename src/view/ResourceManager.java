package view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;

/**
 * ResourceManager Class
 *
 * Handles the loading and storage of all game assets (Images, Fonts).
 *
 * Key Feature:
 * Implements the "Cache" pattern.
 * Instead of reloading the image from disk every time an enemy is created (which is slow),
 * we load all images once at startup (`loadImages`) and store them in static variables.
 * Game objects then simply reference these loaded images in memory.
 */
public class ResourceManager {

    // --- PLAYER ASSETS ---
    public static BufferedImage playerImg;
    public static BufferedImage playerImg2; // With Wings
    public static BufferedImage arrowImg;
    public static BufferedImage arrowImgFire;

    // --- MINION ASSETS ---
    // Harpy
    public static BufferedImage harpyImg;
    public static BufferedImage harpyHitImg; // Pre-calculated white silhouette
    public static BufferedImage featherImg;

    // Cyclops
    public static BufferedImage cyclopsImg;
    public static BufferedImage cyclopsImg2;
    public static BufferedImage cyclopsHitImg; // Pre-calculated white silhouette
    public static BufferedImage cyclopsHitImg2; // Pre-calculated white silhouette
    public static BufferedImage boulderImg;

    // --- BOSS ASSETS ---
    // Apollo
    public static BufferedImage apolloImg;
    public static BufferedImage apolloImg2; // Red/Angry
    public static BufferedImage apolloHitImg; // Pre-calculated white silhouette
    public static BufferedImage sunImg;
    public static BufferedImage sunImg2;

    // Zeus
    public static BufferedImage zeusImg;
    public static BufferedImage zeusImg2; // Angry
    public static BufferedImage zeusHitImg; // Pre-calculated white silhouette
    public static BufferedImage zeusHitImg2; // Pre-calculated white silhouette
    public static BufferedImage lightingImg;
    public static BufferedImage lightingImg2;

    // --- UI & BACKGROUND ASSETS ---
    public static BufferedImage homeScreenImg;
    public static BufferedImage stage1Img;
    public static BufferedImage stage2Img;
    public static BufferedImage stage3Img;
    public static BufferedImage heartFullImg;
    public static BufferedImage heartEmptyImg;

    // --- FONTS ---
    public static Font pixelFont;

    /**
     * Loads all resources from the "res" directory.
     * Must be called exactly once during game initialization.
     */
    public static void loadImages() {
        try {
            System.out.println("Loading resources...");

            // 1. Load Player
            playerImg = loadTexture("player.png");
            playerImg2 = loadTexture("playerStage2.png");
            arrowImg   = loadTexture("arrow.png");
            arrowImgFire   = loadTexture("arrow_fire.png");

            // 2. Load Minions
            // Harpy
            harpyImg = loadTexture("enemy.png");
            harpyHitImg = createWhiteSilhouette(harpyImg); // Generate hit flash effect
            featherImg = loadTexture("feather.png");

            // Cyclops
            cyclopsImg = loadTexture("cyclops_openedwings.png");
            cyclopsImg2 = loadTexture("cyclops_closedwings.png");
            cyclopsHitImg = createWhiteSilhouette(cyclopsImg); // Generate hit flash effect
            cyclopsHitImg2 = createWhiteSilhouette(cyclopsImg2); // Generate hit flash effect
            boulderImg = loadTexture("boulder.png");

            // 3. Load Bosses
            // Apollo
            apolloImg = loadTexture("Apollo.png");
            apolloImg2 = loadTexture("ApolloRed.png");
            apolloHitImg = createWhiteSilhouette(apolloImg); // Generate hit flash effect
            sunImg = loadTexture("sun.png");
            sunImg2 = loadTexture("sunRed.png");

            // Zeus
            zeusImg = loadTexture("Zeus.png");
            zeusImg2 = loadTexture("ZeusAngry.png");
            zeusHitImg = createWhiteSilhouette(zeusImg); // Generate hit flash effect
            zeusHitImg2 = createWhiteSilhouette(zeusImg2); // Generate hit flash effect
            lightingImg = loadTexture("lighting.png");
            lightingImg2 = loadTexture("lightingAngry.png");

            // 4. Load UI & Backgrounds
            homeScreenImg = loadTexture("gladiatorGameScreen.png");
            stage1Img = loadTexture("stage1.png");
            stage2Img = loadTexture("stage2.png");
            stage3Img = loadTexture("stage3.png");
            heartFullImg = loadTexture("heart.png");
            heartEmptyImg = createBlackSilhouette(heartFullImg); // Generate empty heart dynamically

            // 5. Load Custom Font
            try {
                // Access font file as an input stream
                InputStream is = ResourceManager.class.getClassLoader().getResourceAsStream("PixelFont.ttf");

                if (is != null) {
                    // Create TrueType font
                    pixelFont = Font.createFont(Font.TRUETYPE_FONT, is);
                    System.out.println("Pixel Font loaded successfully!");
                } else {
                    System.err.println("Error: PixelFont.ttf not found. Using default font.");
                    pixelFont = new Font("Arial", Font.BOLD, 20); // Fallback
                }
            } catch (FontFormatException | IOException e) {
                e.printStackTrace();
                pixelFont = new Font("Arial", Font.BOLD, 20); // Fallback
            }

            System.out.println("All Resources loaded successfully!");

        } catch (IOException e) {
            System.err.println("Critical Error: Could not load images.");
            e.printStackTrace();
        }
    }

    /**
     * Helper method to safely load an image from the classpath.
     * @param path Relative path to the resource (e.g., "res/image.png").
     * @return The loaded BufferedImage.
     * @throws IOException If the file is not found or cannot be read.
     */
    private static BufferedImage loadTexture(String path) throws IOException {
        java.net.URL url = ResourceManager.class.getClassLoader().getResource(path);

        if (url == null) {
            // Fallback: Try reading as a standard file if getResource fails (e.g., in some IDE setups)
            try {
                return ImageIO.read(new File(path));
            } catch (IOException ex) {
                throw new IOException("Image not found: " + path);
            }
        }
        return ImageIO.read(url);
    }

    /**
     * Generates a "Hit Flash" effect dynamically.
     * Creates a copy of the original image where all non-transparent pixels are turned pure white.
     * This saves us from having to manually create and load separate "white" versions of every sprite.
     * @param original The source sprite.
     * @return A pure white silhouette of the source sprite.
     */
    private static BufferedImage createWhiteSilhouette(BufferedImage original) {
        BufferedImage whiteImg = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int p = original.getRGB(x, y);

                // Extract Alpha channel
                int a = (p >> 24) & 0xff;

                // If pixel is not transparent, paint it White
                if (a > 0) {
                    // ARGB: Alpha + R(255) + G(255) + B(255)
                    int whiteColor = (a << 24) | (255 << 16) | (255 << 8) | 255;
                    whiteImg.setRGB(x, y, whiteColor);
                }
            }
        }
        return whiteImg;
    }

    /**
     * Generates a Black Silhouette (Used for empty hearts).
     */
    private static BufferedImage createBlackSilhouette(BufferedImage original) {
        BufferedImage blackImg = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int p = original.getRGB(x, y);
                int a = (p >> 24) & 0xff;

                // If pixel is not transparent, paint it Black
                if (a > 0) {
                    // ARGB: Alpha + R(0) + G(0) + B(0)
                    int blackColor = (a << 24) | (0) | (0);
                    blackImg.setRGB(x, y, blackColor);
                }
            }
        }
        return blackImg;
    }
}