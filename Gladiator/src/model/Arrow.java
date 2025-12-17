package model;

import java.awt.*;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

// --- Bulletクラス (弾) ---
class Arrow extends GameObject {

    private BufferedImage image;

    public Arrow(int x, int y) {
        super(x, y, GameConstants.BULLET_WIDTH, GameConstants.BULLET_HEIGHT);

        try{
            image = ImageIO.read(new File("res/arrow.png"));
        }
        catch (IOException e) {
            System.out.println("Arrow image not found!");
            e.printStackTrace();;
        }
    }

    @Override
    public void move() {
        y -= 10; // 上にスピード10で飛ぶ

        // 画面外（上）に出たら死亡扱い
        if (y < -height) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
//        g.setColor(Color.YELLOW);
//        g.fillRect(x, y, width, height);

        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }

    }
}
