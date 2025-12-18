package model;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

// --- Enemyクラス (敵) ---
class Enemy extends GameObject {

    private BufferedImage image;

    public Enemy(int x, int y) {
        super(x, y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT); // 30x30の四角

        try{
            image = ImageIO.read(new File("res/enemy.png"));
        }
        catch (IOException e) {
            System.out.println("Enemy image not found!");
            e.printStackTrace();
        }
    }

    @Override
    public void move() {
        y += 3; // 下にスピード3で落ちる

        // 画面外（下）に出たら死亡扱い
        if (y > GameConstants.SCREEN_HEIGHT) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
//        g.setColor(Color.RED);
//        g.fillRect(x, y, width, height);

        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }

    }
}
