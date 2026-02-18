package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * Zeus Class
 *
 * Zeus is the second boss of the game.
 * He flies around throwing lightning
 *
 * Phases:
 * 1. Normal: White sprite, standard attacks.
 * 2. Enraged (HP < 50%): Blue sprite, increased speed, faster attacks, ability 2 unlocked
 *
 * States:
 * 1. STANDARD: Moves left/right and shoots lightning bolts.
 * 2. ABILITY 1 (Teleport):** Disappears and reappears in a zigzag pattern, attacking rapidly.
 * 3. ABILITY 2 (Thunderstorm): Moves across the screen while raining lightning bolts down.
 */
public class Zeus extends Boss {

    // --- State Machine Definition ---
    private enum State {
        STANDARD,    // Normal movement + periodic shooting
        ABILITY1,    // Teleportation Attack Sequence
        ABILITY2     // Raining Lightning Dash Sequence
    }

    private State currentState;
    private Random random = new Random();

    // Movement Variables
    private int velX = GameConstants.ZEUS_SPEED1;
    private double preciseX;
    private boolean secondPhase = false; // "Rage Mode" flag

    // Shooting Variables
    private int shootingTimer;
    private int maxShootTimer;

    // Ability 1 (Teleport) Variables
    private int bouncesRemainingForAbility1;     // Countdown of wall bounces before triggering Ability 1
    private int ability1InterTeleportPause;      // Delay between teleports
    private int ability1InterTeleportTimer;      // Timer for the delay between teleports
    private int ability1TeleportCounter;         // Tracks progress of the teleport sequence (0 to 4)

    // Ability 2 (Dash) Variables
    private int ability2Cooldown;                // Cooldown before Ability 2 can trigger
    private int ability2Timer;                   // Timer for ability 2 Cooldown
    private int ability2BouncesRemaining;        // Duration of the dash (measured in bounces)
    private boolean ability2Started;             // Flag to indicate Ability 2 is active

    private BufferedImage hitImg; // Stores the correct hit-flash image (normal or enraged)

    public Zeus (GameModel model) {
        super ( (GameConstants.WINDOW_WIDTH - GameConstants.ZEUS_WIDTH) / 2,
                GameConstants.HUD_HEIGHT,
                GameConstants.ZEUS_WIDTH,
                GameConstants.ZUES_HEIGHT,
                ResourceManager.zeusImg,
                GameConstants.ZEUS_HP,
                GameConstants.ZEUS_SCORE_POINTS,
                model
        );
        // set STANDARD state as default
        currentState = State.STANDARD;

        // SHOOTING TIMER SETUP
        maxShootTimer = GameConstants.ZEUS_SHOOT_TIMER;
        resetShootTimer();

        // ABILITY 1 SETUP
        setBouncesForAbility1();
        ability1InterTeleportPause = GameConstants.ZEUS_ABILITY1_INTER_TELEPORT_PAUSE1;
        ability1InterTeleportTimer = 0;
        ability1TeleportCounter = 0;

        // ABILITY 2 SETUP
        ability2Cooldown = GameConstants.ZEUS_ABILITY2_TIMER;
        resetAbility2Timer();
        ability2BouncesRemaining = 0;
        ability2Started = false;

        hitImg = ResourceManager.zeusHitImg;
    }

    /**
     * Delegates logic to specific handler methods based on 'currentState'.
     */
    @Override
    public void move() {
        super.move();

        // --- Ability 2 Trigger Logic ---
        // Only available in Phase 2 (Enraged) and not already active
        if (currentState != State.ABILITY2 && secondPhase) {
            if (ability2Timer > 0){
                ability2Timer--;
            } else {
                // If timer is ready and we are in standard state, start Ability 2
                if (currentState == State.STANDARD) {
                    currentState = State.ABILITY2;
                    ability2Started = true;
                    resetShootTimer();
                }
            }
        }

        // --- FSM Switch ---
        switch (currentState) {
            case STANDARD:
                handleStandardState();
                break;
            case ABILITY1:
                handleAbility1State();
                break;
            case ABILITY2:
                handleAbility2State();
                break;
        }
    }

    /**
     * Spawns a Lightning projectile.
     */
    private void shootLighting(){
        Lighting l = new Lighting(x, y, velX, secondPhase, false, ability2Started);
        model.spawnEnemyProjectile(l);
    }

    private void resetShootTimer(){
        shootingTimer = maxShootTimer;
    }

    /**
     * LOGIC: STANDARD STATE
     * - Move horizontally.
     * - Bounce off walls.
     * - Shoot periodically.
     * - Count bounces to trigger Ability 1.
     */
    private void handleStandardState(){
        x += velX;

        // Left Wall Bounce
        if (x <= 0){
            x = 0;
            velX *= -1;
            bouncesRemainingForAbility1--;
        }

        // Right Wall Bounce
        if (x >= GameConstants.WINDOW_WIDTH - width){
            x = GameConstants.WINDOW_WIDTH - width;
            velX *= -1;
            bouncesRemainingForAbility1--;
        }

        // Shooting Logic
        shootingTimer--;
        if (shootingTimer <= 0) {
            shootLighting();
            resetShootTimer();
        }

        // Transition Check: Ability 1
        if (bouncesRemainingForAbility1 == 0){
            currentState = State.ABILITY1;
            resetShootTimer();
            setBouncesForAbility1(); // Reset counter for next time
        }
    }

    /**
     * LOGIC: ABILITY 1 (Teleport)
     * - Wait for pause timer.
     * - Teleport to calculated screen position (Zig-Zag pattern).
     * - Shoot immediately after teleporting.
     * - Repeat 4 times, then return to Standard.
     */
    private void handleAbility1State(){
        if(ability1InterTeleportTimer >= 0){
            ability1InterTeleportTimer--;
        } else {
            // Check if sequence is finished
            if (ability1TeleportCounter == 4) {
                currentState = State.STANDARD;
                ability1TeleportCounter = 0;
                return;
            }
            ability1TeleportCounter++;

            // Calculate new X position (interpolates across the screen width based on step 1-4)
            preciseX = (velX > 0) ?
                    (GameConstants.WINDOW_WIDTH - width) * ability1TeleportCounter/4.0 :
                    (GameConstants.WINDOW_WIDTH - width) * (1 - ability1TeleportCounter/4.0);
            x = (int) preciseX;

            // Calculate new Y position (Alternates Top / Middle)
            y = (ability1TeleportCounter % 2 == 1) ? GameConstants.HUD_HEIGHT + height/2 : GameConstants.HUD_HEIGHT;

            // Attack
            shootLighting();

            // Reset pause timer
            ability1InterTeleportTimer = ability1InterTeleportPause;
        }
    }
    // Set the number of bounces needed for activate ability1
    private void setBouncesForAbility1(){
        bouncesRemainingForAbility1 = random.nextInt(4) + 1; // random between 1 and 4
    }

    /**
     * LOGIC: ABILITY 2 (Raining Dash)
     * - Initializes bounce counter on start.
     * - shoot a lighting bolts every update call.
     * - Returns to Standard after N bounces.
     */
    private void handleAbility2State(){
        // Initialization (First Frame)
        if (ability2BouncesRemaining == 0){
            // Teleport to opposite side to start the dash run
            x = (velX < 0) ? 0 : GameConstants.WINDOW_WIDTH - width;
            velX *= -1;
            setAbility2BouncesRemaining();
        }

        // Attack Logic: Shoot while inside the screen "danger zone" (not near edges)
        int range = 50;
        if ((velX > 0 && x > 0 && x < GameConstants.WINDOW_WIDTH - width - range) ||
                (velX < 0 && x > range && x < GameConstants.WINDOW_WIDTH - width)) {
            shootLighting();
        }
        // move Zeus forward
        x += velX;

        // Bounce Logic & Counter Decrement
        // left bounce
        if (x <= 0){
            x = 0;
            velX *= -1;
            ability2BouncesRemaining--;
        }
        // right bounce
        if (x >= GameConstants.WINDOW_WIDTH - width){
            x = GameConstants.WINDOW_WIDTH - width;
            velX *= -1;
            ability2BouncesRemaining--;
        }

        // End Condition
        if(ability2BouncesRemaining == 0){
            currentState = State.STANDARD;
            ability2Started = false;
            resetAbility2Timer();
        }
    }

    private void resetAbility2Timer(){
        ability2Timer = ability2Cooldown;
    }

    private void setAbility2BouncesRemaining(){
        ability2BouncesRemaining = random.nextInt(3) + 1;
    }

    /**
     * Handles damage and Phase 2 transition (Enrage).
     */
    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);

        // Check for Phase 2 Trigger
        if (hp <= maxHp / 2 && !secondPhase) {
            // Upgrade Stats
            image = ResourceManager.zeusImg2;          // Phase2 Sprite
            hitImg = ResourceManager.zeusHitImg2;      // Phase2 Hit Flash
            velX = (velX > 0) ? GameConstants.ZEUS_SPEED2 : -GameConstants.ZEUS_SPEED2; // increase Speed
            secondPhase = true;

            // Upgrade Attack Timers (Faster shooting)
            maxShootTimer = GameConstants.ZEUS_SHOOT_TIMER2;
            ability1InterTeleportPause = GameConstants.ZEUS_ABILITY1_INTER_TELEPORT_PAUSE2;
        }
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imgToDraw = (flashTimer > 0) ? hitImg : image;

        if (image != null) {
            // Directional Flipping
            if (velX > 0) {
                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {
            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);
        }
    }
}