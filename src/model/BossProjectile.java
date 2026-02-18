package model;

import java.awt.image.BufferedImage;

/**
 * BossProjectile (Abstract)
 *
 * Represents a specialized projectile fired by bosses.
 * This projectiles can be also fired by the player,
 * but in this case they are not invincible.
 */
public abstract class BossProjectile extends Projectile {

    protected boolean isPlayerProjectile; // Flag to determine if it belongs to player or BOSS
    protected int maxHP;
    protected int currentHP;

    /**
     * Constructor for a BossProjectile.
     * It's the same as a simple Projectile
     */

    public BossProjectile(int x, int y, int w, int h, BufferedImage image, Alignment alignment, int powerLevel, int damage){
        super(x, y, w, h, image, alignment, powerLevel, damage);
    }

    /**
     * Reduces the health of the projectile.
     * This logic is used when .
     * * @param damage Amount of damage to subtract.
     */
    public void reduceHealth(int damage) {
        // if a BOSS Projectile it doesn't take damage
        if (!isPlayerProjectile) return;
        // if a PLAYER Projectile it takes damage
        this.currentHP -= damage;
        if (this.currentHP <= 0) {
            this.isDead = true;
        }
    }
}