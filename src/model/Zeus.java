package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;


public class Zeus extends Boss {
    
    private BufferedImage image;
    private int speedX = GameConstants.ZEUS_SPEED;
    
    public Zeus (GameModel model) {
        super ( (GameConstants.WINDOW_HEIGHT - GameConstants.ZEUS_WIDTH) / 2,
                GameConstants.HUD_HEIGHT,
                GameConstants.ZEUS_WIDTH,
                GameConstants.ZUES_HEIGHT,
                GameConstants.ZEUS_HP,
                GameConstants.ZEUS_SCORE_POINTS,
                model
        );
        this.image = ResourceManager.zeusImg;
    }

    @Override
    public void move() {
        x += speedX;

        if (x < 0 || x > (GameConstants.WINDOW_WIDTH - width)) {
            speedX = -speedX;

            model.shootLighting(x, y, speedX);
        }

        if (flashTimer > 0) flashTimer--;
    }

    @Override
    public void takeDamage(int dmg) {

        super.takeDamage(dmg);

    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imgToDraw = (flashTimer > 0) ? ResourceManager.zeusHitImg : image;
        
        if (image != null) {

            if (speedX > 0) {

                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {

            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);

        }
    }


}
