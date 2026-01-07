package model;

import view.ResourceManager; // Import the manager
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D;

// --- Playerクラス (自分) ---
public class Player extends GameObject {
    private int velX = 0; // 横の移動速度
    private int velY = 0; // 縦の移動速度
    private int speed = GameConstants.PLAYER_SPEED;
    private int level = 1;

    private BufferedImage image;

    public Player(int x, int y) {
        super(x, y, GameConstants.PLAYER_WIDTH, GameConstants.PLAYER_HEIGHT); // 40x40の四角
        this.image = ResourceManager.playerImg;
    }

    @Override
    public void move() {
        x += velX;
        y += velY;

        // 画面からはみ出さないように制限
        if (x < 0) x = 0;
        if (x > GameConstants.WINDOW_WIDTH - width) x = GameConstants.WINDOW_WIDTH - width;
        if (y < GameConstants.HUD_HEIGHT) y = GameConstants.HUD_HEIGHT;
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height) y = GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height;
    }

    @Override
    public void draw(Graphics g) {
        if(image != null){
            g.drawImage(image, x, y, width, height, null);
        }
        else{
            // Fallback if image failed to load
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }

    }

    @Override
    public Shape getShape() {
        // we define the margin
        float paddingX = width * 0.3f;  // remove 20 % width
        float paddingY = height * 0.2f; // remove 10 % height

        // the smaller hitbox get centered compared to the original immage
        return new Ellipse2D.Float(
                x + paddingX / 2,     // move right
                y + paddingY / 2,     // move down
                width - paddingX,     // reduce width
                height - paddingY     // reduce height
        );
    }

    // Controllerから呼ばれるメソッド
    public void setVelX(int vx) {
        this.velX = vx * speed;
    }

    public void setVelY(int vy) {
        this.velY = vy * speed;
    }

    public int getLevel(){
        return level;
    }

}
