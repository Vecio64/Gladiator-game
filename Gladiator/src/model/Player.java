package model;

import java.awt.*;

// --- Playerクラス (自分) ---
public class Player extends GameObject {
    private int velX = 0; // 横の移動速度
    private int velY = 0; // 縦の移動速度
    private int speed = 5;

    public Player(int x, int y) {
        super(x, y, 40, 40); // 40x40の四角
    }

    @Override
    public void move() {
        x += velX;
        y += velY;

        // 画面からはみ出さないように制限
        if (x < 0) x = 0;
        if (x > 560) x = 560;
        if (y < 0) y = 0;
        if (y > 1000) y = 1000;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }

    // Controllerから呼ばれるメソッド
    public void setVelX(int vx) {
        this.velX = vx * speed;
    }

    public void setVelY(int vy) {
        this.velY = vy * speed;
    }
}
