package model;

import java.awt.*;

// --- Bulletクラス (弾) ---
class Bullet extends GameObject {
    public Bullet(int x, int y) {
        super(x, y, 10, 20);
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
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }
}
