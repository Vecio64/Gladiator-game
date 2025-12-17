package model;

import java.awt.*;

// --- Enemyクラス (敵) ---
class Enemy extends GameObject {
    public Enemy(int x, int y) {
        super(x, y, 30, 30); // 30x30の四角
    }

    @Override
    public void move() {
        y += 3; // 下にスピード3で落ちる

        // 画面外（下）に出たら死亡扱い
        if (y > 600) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }
}
