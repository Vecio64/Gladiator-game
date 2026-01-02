package model;

import java.awt.Graphics;

/**
 * HostileEntity
 * Abstract class representing any entity that is hostile to the player.
 * Handles HP, damage logic, invincibility, and score points.
 */
public abstract class HostileEntity extends GameObject {

    protected int hp;
    protected int maxHp;
    protected int flashTimer = 0; // For hit effect
    protected boolean isInvincible = false; // If true, entity takes no damage
    protected int scorePoints; // Points awarded when destroyed

    public HostileEntity(int x, int y, int w, int h, int hp, int scorePoints) {
        super(x, y, w, h);
        this.hp = hp;
        this.maxHp = hp;
        this.scorePoints = scorePoints;
    }

    /**
     * Handles taking damage from player projectiles.
     * @param dmg Amount of damage to take
     */
    public void takeDamage(int dmg) {
        // If dead or invincible, do nothing
        if (isDead || isInvincible) return;

        this.hp -= dmg;
        this.flashTimer = GameConstants.FLASH_TIMER; // Set flash effect

        // Check for death
        if (this.hp <= 0) {
            this.isDead = true;
            GameModel.addScore(this.scorePoints); // Award points
        }
    }

    public int getFlashTimer() {
        return flashTimer;
    }

}