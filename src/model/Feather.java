package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

// --- Feather Class (Enemy Projectile) ---
public class Feather extends Projectile {

    private BufferedImage image;

    public Feather(int x, int y) {
        // We pass the FIXED damage (1)
        super(x, y, GameConstants.FEATHER_WIDTH, GameConstants.FEATHER_HEIGHT, GameConstants.FEATHER_DAMAGE);
        this.image = ResourceManager.featherImg;
    }

    @Override
    public void move() {
        y += GameConstants.FEATHER_SPEED;;

        // Use the common check from Projectile class
        if(isOutOfBounds()){
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.GRAY);
            g.fillRect(x, y, width, height);
        }
    }
}