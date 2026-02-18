package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Cyclops Class
 *
 * It's a flying Cyclops who throw boulders
 *
 * Behavior:
 * 1. Descent: Flies down until it reaches the middle of the screen.
 * 2. Hovering: Bobs up and down slowly in place.
 * 3. Attack: Periodically drops a heavy "Boulder" projectile.
 * - It only throws one boulder at a time (waits for the previous one to disappear).
 * 4. Animation: Switches sprites (Wings Open vs Wings Closed) based on vertical direction.
 */
public class Cyclops extends Minion {

    private double preciseY;        // High-precision Y position for smooth movement
    private double velY;            // Current vertical velocity
    private int movementTimer;      // Counter to switch between moving Up/Down
    private int attackTimer;        // Counter for throwing boulders
    private Boulder myBoulder = null; // Reference to the currently active boulder (limits spam)
    private boolean reachedMidScreen; // Flag: true once the initial descent is complete
    private boolean wingsClosed = false; // Animation State

    /**
     * Constructor for the Cyclops enemy.
     *
     * @param x     Initial X coordinate.
     * @param y     Initial Y coordinate.
     * @param model Reference to the GameModel (used to spawn boulders).
     */

    public Cyclops (int x, int y, GameModel model){
        super(x, y,
                GameConstants.CYCLOPS_WIDTH,
                GameConstants.CYCLOPS_HEIGHT,
                ResourceManager.cyclopsImg,
                GameConstants.CYCLOPS_HP,
                GameConstants.CYCLOPS_SCORE_POINTS,
                model);

        this.preciseY = y;
        this.velY = GameConstants.CYCLOPS_VELY; // Start moving down
        this.movementTimer = GameConstants.CYCLOPS_MOVEMENT_TIMER;
        this.attackTimer = GameConstants.CYCLOPS_ATTACK_TIMER;
        this.reachedMidScreen = false;
    }

    /**
     * Updates movement and attack logic.
     */
    @Override
    public void move() {
        super.move();

        // --- Movement Pattern State Machine ---
        if (movementTimer <= 0){
            if (velY > 0){
                // State Transition: Switch to Moving UP
                velY = GameConstants.CYCLOPS_VELY / -4.0; // Move up slowly
                movementTimer = GameConstants.CYCLOPS_MOVEMENT_TIMER * 3 / 2;
                wingsClosed = true; // Visual: Close wings
            }
            else {
                // State Transition: Switch to Moving DOWN
                wingsClosed = false; // Visual: Open wings

                if (!reachedMidScreen) {
                    // Initial Descent (Fast)
                    velY = GameConstants.CYCLOPS_VELY;
                    movementTimer = GameConstants.CYCLOPS_MOVEMENT_TIMER;
                } else {
                    // Hovering Descent (Slow)
                    velY = GameConstants.CYCLOPS_VELY / 4.0;
                    movementTimer = GameConstants.CYCLOPS_MOVEMENT_TIMER * 3 / 2;
                }
            }
            // Update Sprite based on state
            this.image = wingsClosed ? ResourceManager.cyclopsImg2 : ResourceManager.cyclopsImg;
        }

        // Apply Movement
        preciseY += velY;
        y = (int) preciseY;

        // Check if we reached the hovering altitude (mid-screen)
        if(y > (GameConstants.HUD_HEIGHT + GameConstants.FIELD_HEIGHT/2 - height)){
            reachedMidScreen = true;
        }

        // --- Attack Logic ---
        // Only attack if we don't currently have an active boulder (Limit: 1 at a time)
        if (myBoulder == null || myBoulder.isDead()) {
            if(attackTimer > 0){
                attackTimer--;
            }
            // Spawn boulder when timer hits 0
            if (attackTimer == 0){
                throwBoulder();
                attackTimer = GameConstants.CYCLOPS_ATTACK_TIMER; // Reset timer
            }
        }

        movementTimer--;
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imgToDraw;

        // Determine which hit-flash image to use based on current animation frame
        if (image == ResourceManager.cyclopsImg){
            imgToDraw = (flashTimer > 0) ? ResourceManager.cyclopsHitImg : image;
        } else {
            imgToDraw = (flashTimer > 0) ? ResourceManager.cyclopsHitImg2 : image;
        }

        if (imgToDraw != null) {
            g.drawImage(imgToDraw, x, y, width, height, null);
        } else {
            // Fallback
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    /**
     * Helper method to spawn a boulder directly below the Cyclops.
     */
    private void throwBoulder(){
        Boulder b = new Boulder (x + (width - GameConstants.BOULDER_WIDTH)/2, y + height);
        this.myBoulder = b; // Track this boulder
        model.spawnEnemyProjectile(b);
    }
}