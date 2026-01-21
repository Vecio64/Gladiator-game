package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Arrow extends Projectile {

    public Arrow(int x, int y, int arrowDamage) {
        // Alignment: PLAYER
        // Power Level: 1 (Light) -> Can be destroyed by Boulders (Lvl 2) or Sun (Lvl 3)
        // Damage: Standard Arrow Damage
        super(x, y, GameConstants.ARROW_WIDTH, GameConstants.ARROW_HEIGHT, ResourceManager.arrowImg,
                Alignment.PLAYER, 1, arrowDamage);
    }

    @Override
    public void move() {
        y -= GameConstants.ARROW_SPEED;
        if (y < -height) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (ResourceManager.arrowImg != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }
    }
}