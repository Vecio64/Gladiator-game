package model;

import java.awt.Graphics;

/**
 * Projectile Class
 * Abstract class for all flying objects that deal damage (Arrows, Feathers).
 * It handles the damage value and checks if the object is out of the screen bounds.
 */
public abstract class Projectile extends GameObject {

    protected int damage; // Amount of damage this projectile deals

    public Projectile(int x, int y, int w, int h, int damage) {
        super(x, y, w, h);
        this.damage = damage;
    }

    /**
     * Helper method to check if the projectile is outside the screen.
     * Checks both TOP (for Arrows) and BOTTOM (for Feathers).
     * @return true if out of bounds
     */
    protected boolean isOutOfBounds() {
        return (y < GameConstants.HUD_HEIGHT - this.height || y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT);
    }

    public int getDamage() {
        return damage;
    }
}