package model;

import java.awt.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.image.BufferedImage;

// --- Playerクラス (自分) ---
public class Player extends GameObject {
    private int velX = 0; // 横の移動速度
    private int velY = 0; // 縦の移動速度
    private int speed = GameConstants.PLAYER_SPEED;

    private BufferedImage image;

    public Player(int x, int y) {
        super(x, y, GameConstants.PLAYER_WIDTH, GameConstants.PLAYER_HEIGHT); // 40x40の四角

        try{
            image = ImageIO.read(new File("res/player.png"));
        }
        catch (IOException e) {
            System.out.println("Player image not found!");
            e.printStackTrace();;
        }
    }

    @Override
    public void move() {
        x += velX;
        y += velY;

        // 画面からはみ出さないように制限
        if (x < 0) x = 0;
        if (x > GameConstants.SCREEN_WIDTH - width) x = GameConstants.SCREEN_WIDTH - width;
        if (y < 0) y = 0;
        if (y > GameConstants.SCREEN_HEIGHT - height) y = GameConstants.SCREEN_HEIGHT - height;
    }

    @Override
    public void draw(Graphics g) {
        if(image != null){
            g.drawImage(image, x, y, width, height, null);
        }
        else{
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }

    }

    // Controllerから呼ばれるメソッド
    public void setVelX(int vx) {
        this.velX = vx * speed;
    }

    public void setVelY(int vy) {
        this.velY = vy * speed;
    }
}
