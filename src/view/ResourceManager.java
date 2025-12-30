package view;

import javax.imageio.ImageIO;
import java.awt.*;
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

    private static final int PLAYER_WIDTH = 20;
    private static final int PLAYER_HEIGHT = 20;
    private static final int ENEMY_WIDTH = 30;
    private static final int ENEMY_HEIGHT = 30;

    /**
     * Loads all resources from the "res" folder.
     * This method must be called once before the game starts.
     */
    public static void loadImages() {
        try {
            System.out.println("Loading resources...");

            // 読み込み(loadTexture) → リサイズ(resize) の順で実行
            BufferedImage rawPlayer = loadTexture("res/player.png");
            playerImg = resize(rawPlayer, PLAYER_WIDTH, PLAYER_HEIGHT);

            BufferedImage rawEnemy = loadTexture("res/enemy.png");
            enemyImg = resize(rawEnemy, ENEMY_WIDTH, ENEMY_HEIGHT);

            enemyHitImg = createWhiteSilhouette(enemyImg);
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

    // 画像をリサイズするメソッド
    private static BufferedImage resize(BufferedImage original, int width, int height) {
        BufferedImage resized = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resized.createGraphics();
        
        // 画質をきれいに保つ設定
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        
        g2.drawImage(original, 0, 0, width, height, null);
        g2.dispose();
        return resized;
    }

    // シルエットを作成するメソッド
    private static BufferedImage createWhiteSilhouette(BufferedImage original) {
        BufferedImage whiteImg = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int p = original.getRGB(x, y);
                // アルファ値（透明度）を取得
                int a = (p >> 24) & 0xff;

                // 透明じゃない部分を真っ白にする
                if (a > 0) {
                    int whiteColor = (a << 24) | (255 << 16) | (255 << 8) | 255;
                    whiteImg.setRGB(x, y, whiteColor);
                }
            }
        }
        return whiteImg;
    }
}