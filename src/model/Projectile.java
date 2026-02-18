package model;

import java.awt.image.BufferedImage;

/**
 * Projectile (Abstract)
 *
 * Base class for all flying objects (Arrows, Feathers, Boulders).
 * It introduces advanced combat mechanics:
 *
 * 1. Alignment: Determines if it hurts the Player or Enemies.
 * 2. Power Level: A priority system for projectile collisions.
 * - Level 1: Light (Arrow, Feather)
 * - Level 2: Heavy (Boulder)
 * - Level 3: Ultimate (BossProjectiles)
 * 3. Penetration: Whether it continues flying after hitting a target.
 */
public abstract class Projectile extends GameObject {

    protected Alignment alignment; // PLAYER or ENEMY team
    protected int powerLevel;      // Priority for collision resolution
    protected int damage;          // Damage inflicted on impact

    // If true, the projectile does not vanish after hitting a target (BossProjectiles)
    // Default is false (standard bullet behavior).
    protected boolean isPenetrating;

    /**
     * Constructor for the Projectile class.
     * Passes the first 5 parameters to GameOjbect +:
     * @param alignment   Determines the "team" (Player vs Enemy) to prevent friendly fire.
     * @param powerLevel  Collision priority, higher levels destroy lower levels.
     * @param damage      The amount of Health Points (HP) to deduct from the target upon impact.
     */

    public Projectile(int x, int y, int w, int h, BufferedImage image, Alignment alignment, int powerLevel, int damage) {
        super(x, y, w, h, image);
        this.alignment = alignment;
        this.powerLevel = powerLevel;
        this.damage = damage;
        this.isPenetrating = false; // Default: destroys itself on impact
    }

    // Getters for collision logic
    public Alignment getAlignment() { return alignment; }
    public int getPowerLevel() { return powerLevel; }
    public int getDamage() { return damage; }
    public boolean isPenetrating() { return isPenetrating; }
}