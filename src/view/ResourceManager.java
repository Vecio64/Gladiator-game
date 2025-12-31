package view;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.File; // Fileクラスのインポートが必要になる場合があります

/**
 * ResourceManager
 * ゲームのリソース（画像など）を管理するクラスです。
 * 起動時に一度だけ画像を読み込み、メモリに保持することでパフォーマンスを向上させます。
 * （ディスクへのアクセス回数を減らすため）
 */
public class ResourceManager {

    // 読み込んだ画像を保持する静的変数 (Static variables)
    // どこからでも ResourceManager.playerImg のようにアクセスできます
    public static BufferedImage playerImg;
    public static BufferedImage enemyImg;
    public static BufferedImage arrowImg;
    public static BufferedImage featherImg;
    public static BufferedImage enemyHitImg;

    /**
     * "res"フォルダからすべてのリソースを読み込みます。
     * このメソッドは、ゲーム開始前に「一度だけ」呼び出す必要があります。
     */
    public static void loadImages() {
        try {
            System.out.println("Loading resources...");

            // 1. 画像を読み込む（画質を維持するため、リサイズは行いません）
            // ドット絵がぼやけないように、元の解像度のまま読み込みます
            playerImg = loadTexture("res/player.png");
            enemyImg  = loadTexture("res/enemy.png");

            // 2. 敵がダメージを受けた時の「白いシルエット画像」を自動生成する
            enemyHitImg = createWhiteSilhouette(enemyImg);

            // 3. その他の画像を読み込む
            arrowImg   = loadTexture("res/arrow.png");
            featherImg = loadTexture("res/feather.png");

            System.out.println("Resources loaded successfully!");
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
}