package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

// --- Arrowクラス  ---
class Arrow extends GameObject {

    private BufferedImage image;
    private int damage = GameConstants.ARROW_DAMAGE;

    public Arrow(int x, int y) {
        super(x, y, GameConstants.ARROW_WIDTH, GameConstants.ARROW_HEIGHT);
        this.image = ResourceManager.arrowImg;

    }

    @Override
    public void move() {
        y -= GameConstants.ARROW_SPEED; // 上にスピード10で飛ぶ

        // 画面外（上）に出たら死亡扱い
        if (y < -height) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            // Fallback if image failed to load
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }

    }

    public int getDamage(){
        return damage;
    }

}
