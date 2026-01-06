package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Harpy extends HostileEntity {

    private BufferedImage image;
    private int velX;
    private int velY;
    private int fireTimer;

    private boolean isInScreen;

    public Harpy(int x, int y) {
        // Pass params to parent: x, y, width, height, HP, Score Points
        super(x, y,
                GameConstants.HARPY_WIDTH,
                GameConstants.HARPY_HEIGHT,
                GameConstants.HARPY_HP,
                GameConstants.HARPY_SCORE_POINTS);

        this.image = ResourceManager.enemyImg;

        // --- MOVEMENT SETUP ---
        this.velY = GameConstants.HARPY_YSPEED;
        this.velX = GameConstants.HARPY_XSPEED;
        this.isInScreen = false;

        // Randomize direction
        if (Math.random() < 0.5) {
            this.velX = -this.velX;
        }

        resetFireTimer();
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
        if (y < GameConstants.HUD_HEIGHT && isInScreen) {
            y = GameConstants.HUD_HEIGHT;
            velY = -velY;
        }
        if (y > GameConstants.HUD_HEIGHT){
            isInScreen = true;
        }
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height) {
            y = GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height;
            velY = -velY;
        }

        // Decrease flash timer (handled in parent variable)
        if (flashTimer > 0) {
            flashTimer--;
        }

        if(fireTimer > 0) {
            fireTimer--;
        }
    }

    public boolean isReadyToFire() {
        return fireTimer <= 0;
    }

    public void resetFireTimer() {
        int base = GameConstants.FEATHER_FIRE_INTERVAL;
        int variance = GameConstants.FEATHER_FIRE_VARIANCE;

        int randomVariation = (int)(Math.random() * (variance * 2)) - variance;

        this.fireTimer = base + randomVariation;
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