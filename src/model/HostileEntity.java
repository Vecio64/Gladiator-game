package model;

import java.awt.image.BufferedImage;

/**
 * HostileEntity (Abstract)
 *
 * Represents any entity that is an enemy to the player (Minions or Bosses).
 * Extends GameObject to add combat-related features:
 * - Health Points (HP) management.
 * - Damage taking logic.
 * - Invincibility frames (flashTimer).
 * - Score points awarded upon destruction.
 */
public abstract class HostileEntity extends GameObject {

    protected int hp;
    protected int maxHp;
    protected int flashTimer = 0;   // Counter for the white flash effect when hit
    protected int scorePoints;      // Points awarded to the player when this entity is destroyed
    protected GameModel model;      // Reference to the main game model (to add score, etc.)

    /**
     * Constructor for HostileEntity.
     * Passes the first 5 parameters to GameOjbect +:
     * @param hp Health Points
     * @param scorePoints Points awarded upon destruction
     * @param model pointer to the GameModel
     */

    public HostileEntity(int x, int y, int w, int h, BufferedImage image, int hp, int scorePoints, GameModel model) {
        super(x, y, w, h, image);
        this.hp = hp;
        this.maxHp = hp;
        this.scorePoints = scorePoints;
        this.model = model;
    }

    /**
     * Standard logic for taking damage from player projectiles.
     * @param dmg The amount of damage to inflict.
     */
    public void takeDamage(int dmg) {
        // Safety checks: If already dead, ignore damage
        if (isDead) return;

        this.hp -= dmg;
        this.flashTimer = GameConstants.FLASH_TIMER; // Trigger visual feedback (white flash)

        // Death Check
        if (this.hp <= 0) {
            this.isDead = true;
            GameModel.addScore(this.scorePoints); // Award points to the global score
        }
    }

    /**
     * Updates the entity's state.
     * Specifically handles the countdown for the hit-flash effect.
     */
    @Override
    public void move() {
        if (flashTimer > 0) {
            flashTimer--;
        }
    }
}