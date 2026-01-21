package view;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File; // Fileクラスのインポートが必要になる場合があります
import java.io.InputStream;

/**
 * ResourceManager
 * ゲームのリソース（画像など）を管理するクラスです。
 * 起動時に一度だけ画像を読み込み、メモリに保持することでパフォーマンスを向上させます。
 * （ディスクへのアクセス回数を減らすため）
 */
public class ResourceManager {


    // PLAYER
    public static BufferedImage playerImg;
    public static BufferedImage arrowImg;

    //******************
    // MINIONS
    //******************
    // HARPY
    public static BufferedImage harpyImg;
    public static BufferedImage harpyHitImg;
    // HARPY's FEATHER
    public static BufferedImage featherImg;
    // CYCLOPS
    public static BufferedImage cyclopsImg;
    public static BufferedImage cyclopsImg2;
    public static BufferedImage cyclopsHitImg;
    public static BufferedImage cyclopsHitImg2;
    // CYCLOPS's BOULCER
    public static BufferedImage boulderImg;

    //******************
    // BOSSES
    //******************

    // APOLLO
    public static BufferedImage apolloImg;
    public static BufferedImage apolloImg2;
    public static BufferedImage apolloHitImg;
    // APOLLO's SUN
    public static BufferedImage sunImg;
    public static BufferedImage sunImg2;
    // ZEUS
    public static BufferedImage zeusImg;
    public static BufferedImage zeusImg2;
    public static BufferedImage zeusHitImg;
    public static BufferedImage zeusHitImg2;
    // ZEUS's LIGHTNING
    public static BufferedImage lightingImg;
    public static BufferedImage lightingImg2;

    //******************
    // HUD
    //******************

    // STAGES
    public static BufferedImage stage1Img;
    public static BufferedImage stage2Img;
    // HEART
    public static BufferedImage heartFullImg;
    public static BufferedImage heartEmptyImg;

    // PIXEL FONT
    public static Font pixelFont;

    /**
     * "res"フォルダからすべてのリソースを読み込みます。
     * このメソッドは、ゲーム開始前に「一度だけ」呼び出す必要があります。
     */
    public static void loadImages() {
        try {
            System.out.println("Loading resources...");

            // PLAYER
            playerImg = loadTexture("res/player.png");
            arrowImg   = loadTexture("res/arrow.png");

            //******************
            // MINIONS
            //******************
            // HARPY
            harpyImg = loadTexture("res/enemy.png");
            harpyHitImg = createWhiteSilhouette(harpyImg);
            // HARPY's FEATHER
            featherImg = loadTexture("res/feather.png");
            // CYCLOPS
            cyclopsImg = loadTexture("res/cyclops_openedwings.png");
            cyclopsImg2 = loadTexture("res/cyclops_closedwings.png");
            cyclopsHitImg = createWhiteSilhouette(cyclopsImg);
            cyclopsHitImg2 = createWhiteSilhouette(cyclopsImg2);
            // CYCLOPS's BOULDER
            boulderImg = loadTexture("res/boulder.png");

            //******************
            // BOSSES
            //******************

            // APOLLO
            apolloImg = loadTexture("res/Apollo.png");
            apolloImg2 = loadTexture("res/ApolloRed.png");
            apolloHitImg = createWhiteSilhouette(apolloImg);
            // APOLLO's SUN
            sunImg = loadTexture("res/sun.png");
            sunImg2 = loadTexture("res/sunRed.png");
            // ZEUS
            zeusImg = loadTexture("res/Zeus.png");
            zeusImg2 = loadTexture("res/ZeusAngry.png");
            zeusHitImg = createWhiteSilhouette(zeusImg);
            zeusHitImg2 = createWhiteSilhouette(zeusImg2);
            // ZEUS's LIGHTNING
            lightingImg = loadTexture("res/lighting.png");
            lightingImg2 = loadTexture("res/lightingAngry.png");

            //******************
            // HUD
            //******************

            // STAGES
            stage1Img = loadTexture("res/stage1.png");
            stage2Img = loadTexture("res/stage2.png");
            // HEART
            heartFullImg = loadTexture("res/heart.png");
            heartEmptyImg = createBlackSilhouette(heartFullImg);

            // --- LOAD CUSTOM FONT ---
            try {
                // Load the font file from the res folder
                InputStream is = ResourceManager.class.getClassLoader().getResourceAsStream("res/PixelFont.ttf");

                if (is != null) {
                    // Create the font object (default size is 1pt)
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
            System.err.println("Error: Could not load images.");
            e.printStackTrace();
        }

    }

    // 画像を安全に読み込むためのヘルパーメソッド
    private static BufferedImage loadTexture(String path) throws IOException {
        // クラスパス（srcフォルダ内など）からリソースを探す
        java.net.URL url = ResourceManager.class.getClassLoader().getResource(path);

        if (url == null) {
            // もし getResource で見つからない場合（フォルダ構成の違いなど）、
            // 通常のファイルパスとして読み込みを試みる（フォールバック処理）
            try {
                return ImageIO.read(new File(path));
            } catch (IOException ex) {
                throw new IOException("Image not found: " + path);
            }
        }
        return ImageIO.read(url);
    }

    // ダメージ演出用に、透明度を維持したまま「真っ白なシルエット」を作成するメソッド
    private static BufferedImage createWhiteSilhouette(BufferedImage original) {
        // 元の画像と同じサイズで、空の画像を作成
        BufferedImage whiteImg = new BufferedImage(
                original.getWidth(),
                original.getHeight(),
                BufferedImage.TYPE_INT_ARGB
        );

        // すべてのピクセルを走査する
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int p = original.getRGB(x, y);

                // アルファ値（透明度）を取得
                int a = (p >> 24) & 0xff;

                // 透明ではない部分（キャラクター部分）だけを「真っ白」に塗りつぶす
                if (a > 0) {
                    // ARGB: アルファ値 + R(255) + G(255) + B(255)
                    int whiteColor = (a << 24) | (255 << 16) | (255 << 8) | 255;
                    whiteImg.setRGB(x, y, whiteColor);
                }
            }
        }
        return whiteImg;
    }

    private static BufferedImage createBlackSilhouette(BufferedImage original) {
        BufferedImage blackImg = new BufferedImage(original.getWidth(), original.getHeight(), BufferedImage.TYPE_INT_ARGB);
        for (int x = 0; x < original.getWidth(); x++) {
            for (int y = 0; y < original.getHeight(); y++) {
                int p = original.getRGB(x, y);
                int a = (p >> 24) & 0xff; // Get Alpha

                // If the pixel is not transparent, make it BLACK
                if (a > 0) {
                    // ARGB: Alpha + R(0) + G(0) + B(0)
                    int blackColor = (a << 24) | (0 << 16) | (0 << 8) | 0;
                    blackImg.setRGB(x, y, blackColor);
                }
            }
        }
        return blackImg;
    }
}