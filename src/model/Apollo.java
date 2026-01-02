package model;

import view.ResourceManager; // Import the manager
import java.awt.*;
import java.awt.image.BufferedImage;

public class Apollo extends Boss {

    private BufferedImage image;

    private int speedX = GameConstants.APOLLO_SPEED;

    private GameModel model;

    private boolean secondPhase = false;


    public Apollo(GameModel model) {
        super(
                (GameConstants.FIELD_WIDTH - GameConstants.APOLLO_WIDTH) / 2,
                GameConstants.HUD_HEIGHT,
                GameConstants.APOLLO_WIDTH,
                GameConstants.APOLLO_HEIGHT,
                GameConstants.APOLLO_HP

        );
        this.image = ResourceManager.apolloImg;
        this.model = model;
    }

    @Override
    public void move() {
        x += speedX;

        if (x <= 0 || x >= GameConstants.FIELD_WIDTH - width) {
            speedX = -speedX;
            model.shootSun(x, y, speedX, secondPhase);
        }

        if (flashTimer > 0) flashTimer--;
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);
        if(hp <= maxHp / 2 && !secondPhase){
            this.image = ResourceManager.apolloImg2;
            this.speedX *= 2;
            secondPhase = true;
        }
    }

    @Override
    public void draw(Graphics g) {

        BufferedImage imgToDraw;

        if(flashTimer > 0) {
            // ダメージを受けたばかりならばピカピカする(FLASH)
            imgToDraw = ResourceManager.apolloHitImg;
        }
        else {
            imgToDraw = image;
        }

        if (image != null) {
            // Check direction to flip image
            if (speedX > 0) {
                // Moving RIGHT: Draw normal
                // (Assuming the original sprite faces Right)
                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                // Moving LEFT: Draw flipped (Mirror effect)
                // We start at (x + width) and draw with negative width
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {
            // Fallback (Red square)
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);
        }
    }
}