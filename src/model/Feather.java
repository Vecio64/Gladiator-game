package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

// --- Feather Class (Enemy Projectile) ---
public class Feather extends GameObject {

    private BufferedImage image;

    public Feather(int x, int y) {
        super(x, y, GameConstants.FEATHER_WIDTH, GameConstants.FEATHER_HEIGHT);
        this.image = ResourceManager.featherImg;
    }

    @Override
    public void move() {
        y += GameConstants.FEATHER_SPEED;;

        if(y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT){
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if(image!= null){
            g.drawImage(image, x, y, width, height, null);
        }
        else {
        // Fallback if image failed to load
        g.setColor(Color.GRAY);
        g.fillRect(x, y, width, height);
        }
    }
}