package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Enemy extends HostileEntity {

    private BufferedImage image;
    private int velX;
    private int velY;

    public Enemy(int x, int y) {
        // Pass params to parent: x, y, width, height, HP, Score Points
        super(x, y,
                GameConstants.ENEMY_WIDTH,
                GameConstants.ENEMY_HEIGHT,
                GameConstants.ENEMY_HP,
                GameConstants.ENEMY_SCORE_POINTS);

        this.image = ResourceManager.enemyImg;

        // --- MOVEMENT SETUP ---
        this.velY = GameConstants.ENEMY_YSPEED;
        this.velX = GameConstants.ENEMY_XSPEED;

        // Randomize direction
        if (Math.random() < 0.5) {
            this.velX = -this.velX;
        }
    }

    @Override
    public void move() {
        x += velX;
        y += velY;

        // Bounce Logic
        if (x < 0) {
            x = 0;
            velX = -velX;
        }
        if (x > GameConstants.FIELD_WIDTH - width) {
            x = GameConstants.FIELD_WIDTH - width;
            velX = -velX;
        }
        if (y < GameConstants.HUD_HEIGHT) {
            y = GameConstants.HUD_HEIGHT;
            velY = -velY;
        }
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height) {
            y = GameConstants.FIELD_HEIGHT - height;
            velY = -velY;
        }

        // Decrease flash timer (handled in parent variable)
        if (flashTimer > 0) flashTimer--;
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imgToDraw = (flashTimer > 0) ? ResourceManager.enemyHitImg : image;

        if (image != null) {
            if (velX < 0) {
                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }
}