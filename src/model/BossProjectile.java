package model;

import java.awt.image.BufferedImage;

public abstract class BossProjectile extends Projectile {

    protected boolean isPlayerProjectile;
    protected int maxHP;
    protected int currentHP;

    public BossProjectile(int x, int y, int w, int h, BufferedImage image, Alignment alignment, int powerLevel, int damage){
        super(x, y, w, h, image, alignment, powerLevel, damage);
    }

    public void reduceHealth(int damage) {
        // Only Player's Projectiles can die
        if (!isPlayerProjectile) return;
        this.currentHP -= damage;
        if (this.currentHP <= 0) {
            this.isDead = true;
        }
    }
}
