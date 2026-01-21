package model;

import view.ResourceManager;
import java.awt.*;

public class Feather extends Projectile {

    public Feather(int x, int y) {
        // Alignment: ENEMY
        // Power Level: 1 (Light) -> Clashes equally with Arrows
        // Damage: Standard Feather Damage
        super(x, y, GameConstants.FEATHER_WIDTH, GameConstants.FEATHER_HEIGHT, ResourceManager.featherImg,
                Alignment.ENEMY, 1, GameConstants.FEATHER_DAMAGE);
    }

    @Override
    public void move() {
        y += GameConstants.FEATHER_SPEED;
        // Despawn logic
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (ResourceManager.featherImg != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.MAGENTA);
            g.fillRect(x, y, width, height);
        }
    }
}