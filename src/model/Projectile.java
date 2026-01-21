package model;

import view.ResourceManager;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Projectile Class
 * Abstract base class for all flying objects (Arrows, Feathers, Suns, Boulders).
 * It introduces the concept of "Power Level" to handle projectile-vs-projectile collisions.
 */
public abstract class Projectile extends GameObject {

    protected Alignment alignment;

    // Power Level determines priority when two projectiles collide:
    // 0 = Ephemeral (Destroyed by anything)
    // 1 = Light (Arrow, Feather)
    // 2 = Heavy (Boulder - Destroys Light)
    // 3 = Ultimate (Sun - Destroys Heavy and Light)
    protected int powerLevel;

    protected int damage;

    // If true, the projectile does not vanish after hitting a target (e.g., The Sun)
    protected boolean isPenetrating;

    public Projectile(int x, int y, int w, int h, BufferedImage image, Alignment alignment, int powerLevel, int damage) {
        super(x, y, w, h, image);
        this.alignment = alignment;
        this.powerLevel = powerLevel;
        this.damage = damage;
        this.isPenetrating = false; // Default: destroys itself on impact
    }

    // Getters
    public Alignment getAlignment() { return alignment; }
    public int getPowerLevel() { return powerLevel; }
    public int getDamage() { return damage; }
    public boolean isPenetrating() { return isPenetrating; }
}