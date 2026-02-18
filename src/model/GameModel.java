package model;

import view.ResourceManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.awt.geom.Area;

/**
 * GameModel Class
 *
 * Represents the "Brain" of the application (MVC Architecture).
 * It manages the game state, all game entities, collision detection,
 * level progression, and core mechanics.
 */
public class GameModel {

    // --- GAME OBJECTS MANAGEMENT ---
    private ArrayList<GameObject> objects;        // List of all active game entities
    private ArrayList<GameObject> newObjectsBuffer; // Buffer to add objects safely during iteration
    private Player player;
    private boolean isGameOver = false;
    private Random rand = new Random();

    // --- GAME STATE & INPUT ---
    private GameState state;       // Current state (TITLE, PLAYING, etc.)
    private boolean isFiring;      // Tracks if the shoot key is held down
    private int shotTimer;         // Controls the fire rate (cooldown)
    private int arrowDamage;       // Current damage of player's arrows
    private int arrowInterval;     // Current fire rate delay

    // --- PROGRESSION SYSTEM ---
    private static int score = 0;
    private int nextTargetScore;         // Score needed to reach the next event
    private int currentLevelIndex = 0;   // Current step in the LEVEL_MILESTONES array
    private int lastCheckpointIndex = 0; // Stores the index of the last major event (for Continue)
    private boolean isBossActive = false; // Flag to pause progression during boss fights

    // --- PLAYER STATS ---
    private int lives;       // Current lives
    private int damageTimer; // Invincibility frames after taking damage

    // --- ABILITIES COOLDOWNS ---
    private int ability1Timer;
    private int ability2Timer;
    private int ability3Timer;

    // --- ENVIRONMENT ---
    private Background background;
    private int currentStage; // Used for UI display (Stage 1, 2, 3)

    // --- UI MESSAGES ---
    private String[] currentMessageLines; // Stores text for the Message Box

    // --- ENEMY SPAWNING ---
    private List<EnemySpawner> activeSpawners; // List of active enemy generators

    public GameModel() {
        objects = new ArrayList<>();
        newObjectsBuffer = new ArrayList<>();
        activeSpawners = new ArrayList<>();
        state = GameState.TITLE;
    }

    /**
     * Initializes a fresh new game from the beginning.
     */
    public void initGame() {
        // Reset Checkpoint to 0 for a fresh start
        lastCheckpointIndex = 0;

        // Call the logic to load the first level
        resetToLevel(0);

        arrowDamage = GameConstants.ARROW_DAMAGE;
        arrowInterval = GameConstants.ARROW_INTERVAL;

        // Reset Abilities Cooldowns
        ability1Timer = 0;
        ability2Timer = 0;
        ability3Timer = 0;

        // Initialize Background
        if (background == null) background = new Background();
        background.setImage(ResourceManager.stage1Img);
        background.setSpeed(GameConstants.SCREEN_SPEED);

        // Reset Stage Number Default
        this.currentStage = 1;

        // Starting message
        String tutorial = "WELCOME GLADIATOR!\n\n" +
                "Controls:\n" +
                "[KEY-ARROWS] Move\n" +
                "[SPACE] Shoot\n" +
                "[P] Pause\n\n" +
                "Defeat enemies\n" +
                "and survive!";
        showMessage(tutorial);
    }

    /**
     * Continues the game from the last saved checkpoint.
     * Called when the player chooses "Continue" after Game Over.
     */
    public void continueGame() {
        // Do NOT reset lastCheckpointIndex. Use it to restore state.
        System.out.println("Continuing from Checkpoint Index: " + lastCheckpointIndex);

        // Restore the game to the saved level index
        resetToLevel(lastCheckpointIndex);

        // Hide any lingering message boxes so the player can play immediately
        if (this.state == GameState.MESSAGE) {
            this.state = GameState.PLAYING;
        }
    }

    /**
     * Resets the game state to a specific level index.
     * Used for both initialization and checkpoints.
     * @param levelIndex The index in LEVEL_MILESTONES to load.
     */
    private void resetToLevel(int levelIndex) {
        objects.clear();
        newObjectsBuffer.clear();
        activeSpawners.clear();

        // --- RESET PLAYER ---
        // Set the correct sprite (Normal vs Wings) based on progression
        if (levelIndex > 3){
            player = new Player(ResourceManager.playerImg2);
        } else {
            player = new Player(ResourceManager.playerImg);
        }
        objects.add(player);
        lives = GameConstants.PLAYER_MAX_LIVES;
        damageTimer = 0;

        // Reset Timers / Mechanics
        isFiring = false;
        shotTimer = 0;

        // Restore Level Index & Targets
        this.currentLevelIndex = levelIndex;
        // Safety check to avoid ArrayOutOfBounds
        if (currentLevelIndex + 1 < GameConstants.LEVEL_MILESTONES.length) {
            this.nextTargetScore = GameConstants.LEVEL_MILESTONES[currentLevelIndex + 1];
        } else {
            this.nextTargetScore = Integer.MAX_VALUE; // Max level reached
        }

        // Set default spawner for the start of the game
        if (levelIndex == 0) {
            activeSpawners.add(new EnemySpawner(Harpy.class, GameConstants.HARPY_SPAWN_INTERVAL, GameConstants.HARPY_SPAWN_VARIANCE));
        }

        // Restore Score (Set score to the previous milestone so we don't regress level)
        if (levelIndex > 0) {
            score = GameConstants.LEVEL_MILESTONES[levelIndex];
        } else {
            score = 0;
        }

        // Re-apply the specific effects (Bosses, Backgrounds) for this level
        applyLevelEffects(levelIndex);

        // Set State
        isGameOver = false;
        state = GameState.PLAYING;
    }

    /**
     * Checks if the player has reached the score required for the next event.
     */
    private void checkLevelProgression() {
        // If a Boss is active, pause progression
        if (isBossActive) return;

        // If max level reached, return
        if (currentLevelIndex >= GameConstants.LEVEL_MILESTONES.length - 1) return;

        if (score >= nextTargetScore){
            // Advance level index
            currentLevelIndex++;

            // Apply new level logic
            applyLevelEffects(currentLevelIndex);
            System.out.println("Level Up! Current Index: " + currentLevelIndex);

            // Set next target
            if (currentLevelIndex + 1 < GameConstants.LEVEL_MILESTONES.length) {
                nextTargetScore = GameConstants.LEVEL_MILESTONES[currentLevelIndex + 1];
            }
        }
    }

    /**
     * Applies game changes based on the current level index.
     * Handles difficulty spikes, boss spawns, and stage transitions.
     */
    private void applyLevelEffects(int levelIndex) {
        switch (levelIndex) {
            case 1: // Increase spawn rate
                for (EnemySpawner s : activeSpawners) s.increaseDifficulty(0.8);
                break;

            case 2: // Increase spawn rate
                for (EnemySpawner s : activeSpawners) s.increaseDifficulty(0.8);
                break;

            case 3: // BOSS 1: APOLLO
                // Save checkpoint
                lastCheckpointIndex = levelIndex;

                showMessage("WARNING!\n\nBOSS DETECTED:\nAPOLLO\n\nPrepare for battle!");
                isBossActive = true;
                clearEverything(); // Remove standard enemies
                spawnApollo();
                healPlayer();
                break;

            case 4: // STAGE 2 START
                // Save checkpoint
                lastCheckpointIndex = levelIndex;

                showMessage("STAGE 1 CLEARED!\n\nEntering the Heavens...\n\nNow you can fly!\n\nArrow Damage doubled!\n\n Press [1] to use\nABILITY 1:\nAPOLLO'S SUN");
                if (background != null) {
                    clearEverything();
                    healPlayer();
                    background.setImage(ResourceManager.stage2Img);
                    background.setSpeed(GameConstants.SCREEN_SPEED);

                    this.currentStage = 2;

                    // Reset Ability 1 Cooldown
                    ability1Timer = 0;

                    // Upgrade Player: Change image to Wings
                    player.setImage(ResourceManager.playerImg2);

                    // Upgrade Player: Double Damage
                    arrowDamage *= 2;

                    // Update Spawners for Stage 2
                    activeSpawners.clear();
                    // Harpy Spawner
                    activeSpawners.add(new EnemySpawner(Harpy.class, 100, 50));
                    // Cyclops Spawner (New Enemy)
                    activeSpawners.add(new EnemySpawner(Cyclops.class,
                            GameConstants.CYCLOPS_SPAWN_INTERVAL,
                            GameConstants.CYCLOPS_SPAWN_VARIANCE));
                }
                break;

            case 5: // Increase spawn rate
                for (EnemySpawner s : activeSpawners) s.increaseDifficulty(0.9);
                break;
            case 6: // Increase spawn rate
                for (EnemySpawner s : activeSpawners) s.increaseDifficulty(0.9);
                break;

            case 7: // BOSS 2: ZEUS
                // Save checkpoint
                lastCheckpointIndex = levelIndex;

                showMessage("WARNING!\n\nBOSS DETECTED:\nZEUS\n\nPrepare for battle!");
                isBossActive = true;
                clearEverything();
                spawnZeus();
                healPlayer();
                break;

            case 8: // STAGE 3 START
                // Save checkpoint
                lastCheckpointIndex = levelIndex;

                showMessage("STAGE 2 CLEARED!\n\nEntering the INFERNO...\n\nNow you can\nshoot faster!\n\nPress [2] to use\nABILITY 2:\nZEUS'S LIGHTING");
                if (background != null) {
                    clearEverything();
                    background.setImage(ResourceManager.stage3Img);
                    background.setSpeed(0); // Static background for Inferno

                    ability2Timer = 0;

                    // Upgrade Player: Faster Fire Rate
                    arrowInterval = GameConstants.ARROW_INTERVAL2;

                    this.currentStage = 3;

                    // Update Spawners
                    activeSpawners.clear();
                    activeSpawners.add(new EnemySpawner(Harpy.class, 80, 40));
                    activeSpawners.add(new EnemySpawner(Cyclops.class, 240, 120));
                }
                break;
            case 9:
                // Future content
                break;
        }
    }

    public void showMessage(String text) {
        // Split text to handle multiple lines in the UI
        this.currentMessageLines = text.split("\n");
        this.state = GameState.MESSAGE;
    }

    public static void addScore(int points){
        score += points;
    }

    public void setFiring(boolean firing) {
        this.isFiring = firing;
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState s) {
        this.state = s;
    }

    /**
     * Main Game Loop Update.
     * Called every frame by the GamePanel timer.
     */
    public void update() {
        if (state != GameState.PLAYING) return;

        // 1. Update Background
        if(background!= null) {
            background.update();
        }

        // 2. Check Progression
        checkLevelProgression();

        // 3. Handle Shooting
        if (isFiring) {
            if (shotTimer == 0) {
                playerShoot();
                shotTimer = arrowInterval;
            }
        }
        if (shotTimer > 0) {
            shotTimer--;
        }

        // 4. Update Invincibility Timer
        if (damageTimer > 0) {
            damageTimer--;
        }

        // 5. Update Ability Cooldowns
        if (ability1Timer > 0) ability1Timer--;
        if (ability2Timer > 0) ability2Timer--;
        if (ability3Timer > 0) ability3Timer--;

        // 6. Spawn Enemies
        if (!isBossActive) {
            for (EnemySpawner spawner : activeSpawners) {
                if (spawner.update()) {
                    spawnMinion(spawner.getEnemyType());
                }
            }
        }

        // 7. Add new objects from buffer
        objects.addAll(newObjectsBuffer);
        newObjectsBuffer.clear();

        // 8. Move all objects
        for (GameObject obj : objects) {
            obj.move();
        }

        // 9. Check Collisions
        checkCollisions();

        // 10. Remove dead objects
        objects.removeIf(obj -> obj.isDead());
    }

    /**
     * Helper method to instantiate the correct enemy based on Class type.
     */
    private void spawnMinion(Class<? extends Minion> type) {
        int x,y;
        if (type == Harpy.class) {
            x = rand.nextInt(GameConstants.WINDOW_WIDTH - GameConstants.HARPY_WIDTH); // Random X
            y = GameConstants.HUD_HEIGHT - GameConstants.HARPY_HEIGHT; // Start at top
            Harpy h = new Harpy(x, y, this);
            newObjectsBuffer.add(h);
        }
        else if (type == Cyclops.class) {
            x = rand.nextInt(GameConstants.WINDOW_WIDTH - GameConstants.CYCLOPS_WIDTH); // Random X
            y = GameConstants.HUD_HEIGHT - GameConstants.CYCLOPS_HEIGHT; // Start at top
            Cyclops c = new Cyclops(x, y, this);
            newObjectsBuffer.add(c);
        }
    }

    /**
     * Allows enemies (Minions/Bosses) to add projectiles to the game.
     */
    public void spawnEnemyProjectile(Projectile p) {
        if (p != null) {
            newObjectsBuffer.add(p);
        }
    }

    public void bossDefeated(){
        this.isBossActive = false;
        healPlayer();
    }

    /**
     * Removes all enemies and projectiles from the screen.
     * Used when starting a boss fight or changing stages.
     */
    private void clearEverything() {
        objects.removeIf(obj -> obj instanceof HostileEntity || obj instanceof Projectile);
        newObjectsBuffer.removeIf(obj -> obj instanceof HostileEntity || obj instanceof Projectile);
    }

    /**
     * Spawns a player's arrow. Called by the Controller (Input).
     */
    public void playerShoot() {
        if (!isGameOver) {
            // Spawn arrow centered above the player
            Arrow a = new Arrow(player.getX() + (GameConstants.PLAYER_WIDTH - GameConstants.ARROW_WIDTH)/2,
                    player.getY() - GameConstants.ARROW_HEIGHT,
                    arrowDamage);
            newObjectsBuffer.add(a);
        }
    }

    // --- ABILITIES LOGIC ---

    public void ability1(){
        if (ability1Timer > 0) return;
        ability1Timer = GameConstants.ABILITY1TIMER;
        Sun sun = new Sun(
                player.getX() + GameConstants.PLAYER_WIDTH / 2,
                player.getY(),
                0, false, true
        );
        newObjectsBuffer.add(sun);
    }

    public void ability2(){
        if (ability2Timer > 0) return;
        ability2Timer = GameConstants.ABILITY2TIMER;
        Lighting l = new Lighting(
                player.getX(),
                player.getY(),
                0, false, true, false
        );
        newObjectsBuffer.add(l);
    }

    private void spawnApollo() {
        Apollo apollo = new Apollo(this);
        objects.add(apollo);
    }

    public void spawnZeus() {
        Zeus zeus = new Zeus(this);
        objects.add(zeus);
    }

    /**
     * Handles player taking damage.
     */
    private void playerTakesDamage() {
        if (damageTimer == 0) { // Only damage if not invincible
            lives--;
            damageTimer = GameConstants.PLAYER_INVINCIBLE_AFTER_DAMAGE; // 2 seconds invincibility (at 60 FPS)
            // death check
            if (lives <= 0) {
                state = GameState.GAMEOVER;
            }
        }
    }

    private void healPlayer(){
        lives = GameConstants.PLAYER_MAX_LIVES;
    }

    /**
     * Detailed Intersection Check.
     * Uses Java AWT Area class for precise pixel-perfect collision detection.
     */
    private boolean checkIntersection(GameObject obj1, GameObject obj2) {
        // 1. Get Shapes
        Shape s1 = obj1.getShape();
        Shape s2 = obj2.getShape();

        // 2. Quick Check: If bounds don't overlap, skip complex calculation
        if (!s1.getBounds2D().intersects(s2.getBounds2D())) {
            return false;
        }

        // 3. Precise Calculation (Area Intersection)
        Area area1 = new Area(s1);
        Area area2 = new Area(s2);

        area1.intersect(area2);

        // If the resulting area is not empty, they are touching
        return !area1.isEmpty();
    }

    /**
     * Centralized Collision Logic.
     * Iterates through all objects to check for intersections.
     */
    private void checkCollisions() {
        for (int i = 0; i < objects.size(); i++) {
            GameObject objA = objects.get(i);
            if (objA.isDead()) continue;

            for (int j = i + 1; j < objects.size(); j++) {
                GameObject objB = objects.get(j);
                if (objB.isDead()) continue;

                if (checkIntersection(objA, objB)) {
                    handleCollision(objA, objB);
                }
            }
        }
    }

    /**
     * Handles the specific logic when two objects collide.
     * Uses Projectile Power Levels and Alignment to determine the outcome.
     */
    private void handleCollision(GameObject a, GameObject b) {

        // --- CASE 1: PROJECTILE vs PROJECTILE ---
        if (a instanceof Projectile && b instanceof Projectile) {
            Projectile p1 = (Projectile) a;
            Projectile p2 = (Projectile) b;

            // Same team projectiles do not destroy each other
            if (p1.getAlignment() == p2.getAlignment()) return;

            // Compare Power Levels to see who survives
            if (p1.getPowerLevel() > p2.getPowerLevel()) {
                p2.setDead(); // p1 dominates

                //  BOSS PROJECTILE CASE: takes damage equal to p2's damage
                if (p1 instanceof BossProjectile) {
                    ((BossProjectile) p1).reduceHealth(p2.getDamage());
                }

            } else if (p2.getPowerLevel() > p1.getPowerLevel()) {
                p1.setDead();

                // BOSS PROJECTILE CASE: takes damage equal to p1's damage
                if (p2 instanceof BossProjectile) {
                    ((BossProjectile) p2).reduceHealth(p1.getDamage());
                }

            } else {
                // Equal power (e.g., Arrow vs Feather) -> Both destroyed
                p1.setDead();
                p2.setDead();
            }
            return;
        }

        // --- CASE 2: PROJECTILE vs LIVING ENTITY (Player or Enemy) ---
        Projectile proj = null;
        GameObject entity = null;

        // Identify which is which
        if (a instanceof Projectile) { proj = (Projectile) a; entity = b; }
        else if (b instanceof Projectile) { proj = (Projectile) b; entity = a; }

        if (proj != null) {
            // Sub-case A: Projectile hits Player
            if (entity instanceof Player) {
                if (proj.getAlignment() == Alignment.ENEMY) {
                    playerTakesDamage();
                    if (!proj.isPenetrating()) proj.setDead();
                }
            }
            // Sub-case B: Projectile hits Enemy (Harpy, Apollo, Golem, etc.)
            else if (entity instanceof HostileEntity) {
                HostileEntity enemy = (HostileEntity) entity;

                // Only damage if the projectile belongs to the Player
                if (proj.getAlignment() == Alignment.PLAYER) {
                    enemy.takeDamage(proj.getDamage());

                    // BOSS PROJECTILE CASE: loses HP upon contact
                    if (proj instanceof BossProjectile) {
                        ((BossProjectile) proj).reduceHealth(1);
                    }

                    else if (!proj.isPenetrating()) {
                        proj.setDead();
                    }
                }
            }
            return;
        }

        // --- CASE 3: PHYSICAL COLLISION (Player vs Enemy Body) ---
        if ((a instanceof Player && b instanceof HostileEntity) ||
                (b instanceof Player && a instanceof HostileEntity)) {
            playerTakesDamage();
        }
    }

    // used in the GamePanel for making the player flashing after taking damage
    public boolean isInvincible() {
        return damageTimer > 0;
    }
    // used in the GamePanel for the graphics of the abilities slots
    public int getAbilityNthTimer(int n) {
        switch (n){
            case 1:
                return ability1Timer;
            case 2:
                return ability2Timer;
            case 3:
                return ability3Timer;
            default:
                System.out.println("Error: Unknown Ability Index");
        }
        return ability1Timer;
    }

    /**
     * Checks if an ability is unlocked based on current level progression.
     */
    public boolean isAbilityUnclocked(int abilityIndex) {
        // Logic for Ability 1 (Sun)
        if (abilityIndex == 1) {
            // Unlocks after defeating the first boss (Apollo)
            // Apollo is Level Index 3. So > 3 means Stage 2 started.
            return this.currentLevelIndex > 3;
        }
        // Logic for Ability 2 (Lighting)
        if (abilityIndex == 2) {
            // Unlocks after defeating the second boss (Zeus)
            // Zeus is Level Index 7. So > 7 means Stage 3 started.
            return this.currentLevelIndex > 7;
        }

        return false;
    }

    public void resumeGame() {
        this.state = GameState.PLAYING;
    }

    // --- SETTERS & GETTERS ---
    public ArrayList<GameObject> getObjects() {
        return objects;
    }

    public Player getPlayer() {
        return player;
    }

    public boolean isGameOver() {
        return isGameOver;
    }

    public int getScore() {
        return score;
    }

    public int getLives() {
        return lives;
    }

    public Background getBackground() {
        return background;
    }

    public String getStageText(){
        if (currentStage > 3) {
            return "EXTRA STAGE";
        }
        return "STAGE " + currentStage;
    }

    public int getCurrentLevelIndex(){
        return this.currentLevelIndex;
    }

    public String[] getCurrentMessageLines() {
        return currentMessageLines;
    }

}