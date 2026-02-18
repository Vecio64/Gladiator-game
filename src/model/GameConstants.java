package model;

/**
 * GameConstants Class
 *
 * A central repository for all game-related constants.
 * This class prevents "magic numbers" from being scattered throughout the code,
 * making it easy to tweak game balance (speed, damage, health) in one place.
 *
 * It is declared 'final' to prevent inheritance and has a private constructor
 * to prevent instantiation.
 */
public final class GameConstants {

    // --- WINDOW & SCREEN DIMENSIONS ---
    public static final int WINDOW_WIDTH = 600;

    // Height calculation: HUD (Top) + Gameplay Field + Bottom HUD
    public static final int HUD_HEIGHT = 50;
    public static final int FIELD_HEIGHT = 800;
    public static final int BOTTOM_HUD_HEIGHT = 100;
    public static final int WINDOW_HEIGHT = HUD_HEIGHT + FIELD_HEIGHT + BOTTOM_HUD_HEIGHT;

    // --- SYSTEM SETTINGS ---
    public static final int FPS = 60; // Target Frames Per Second
    public static final double SCREEN_SPEED = 1.0; // Background scroll speed

    // --- PLAYER STATS ---
    public static final int PLAYER_WIDTH = 100; // 100
    public static final int PLAYER_HEIGHT = 125; // 125
    public static final int PLAYER_SPEED = 8; // 8
    public static final int PLAYER_MAX_LIVES = 3; // 3
    public static final int PLAYER_INVINCIBLE_AFTER_DAMAGE = FPS * 2; // 2 seconds

    // --- ABILITY COOLDOWNS ---
    public static final int ABILITY1TIMER = FPS * 10; // 10 seconds (Apollo's Sun)
    public static final int ABILITY2TIMER = FPS * 8;  // 8 seconds (Zeus's Lightning)
    public static final int ABILITY3TIMER = FPS * 10; // 10 seconds (Reserved)

    // --- PROJECTILE: ARROW (Player) ---
    public static final int ARROW_WIDTH = 10; // 10
    public static final int ARROW_HEIGHT = 70; // 70
    public static final int ARROW_SPEED = 30; // 30
    public static final int ARROW_INTERVAL = 20;  // 20; Standard fire rate
    public static final int ARROW_INTERVAL2 = 15; // 15; Upgraded fire rate (Stage 3)
    public static final int ARROW_DAMAGE = 1; // 1

    // ************************************
    // MINIONS (Standard Enemies)
    // ************************************

    // --- HARPY ---
    public static final int HARPY_WIDTH = 100; // 100
    public static final int HARPY_HEIGHT = 110; // 110
    public static final int HARPY_VELX = 4; // 4
    public static final int HARPY_VELY = 2; // 2
    public static final int HARPY_HP = 2; // 2
    public static final int HARPY_SCORE_POINTS = 10; // 10
    public static final int HARPY_SPAWN_INTERVAL = FPS * 2; // 2 seconds
    public static final int HARPY_SPAWN_VARIANCE = HARPY_SPAWN_INTERVAL / 2;

    // --- FEATHER (Harpy Projectile) ---
    public static final int FEATHER_WIDTH = 10; // 12
    public static final int FEATHER_HEIGHT = 50; // Aspect Ratio
    public static final int FEATHER_SPEED = 7; // 7
    public static final int FEATHER_FIRE_INTERVAL = 90; // 1.5 seconds
    public static final int FEATHER_FIRE_VARIANCE = FEATHER_FIRE_INTERVAL / 2;
    public static final int FEATHER_DAMAGE = 1; // 1

    // --- CYCLOPS ---
    public static final int CYCLOPS_WIDTH = 150; // 150
    public static final int CYCLOPS_HEIGHT = CYCLOPS_WIDTH;
    public static final double CYCLOPS_VELY = 2; // 2
    public static final int CYCLOPS_HP = 10; // 10
    public static final int CYCLOPS_SCORE_POINTS = 50; // 50
    public static final int CYCLOPS_SPAWN_INTERVAL = FPS * 5; // 5 seconds
    public static final int CYCLOPS_SPAWN_VARIANCE = CYCLOPS_SPAWN_INTERVAL / 2;
    public static final int CYCLOPS_MOVEMENT_TIMER = 20; // 20: Duration of up/down bobbing
    public static final int CYCLOPS_ATTACK_TIMER = FPS * 1;   // 1 second

    // --- BOULDER (Cyclops Projectile) ---
    public static final int BOULDER_WIDTH = 100; // 100
    public static final int BOULDER_HEIGHT = BOULDER_WIDTH;
    public static final double BOULDER_INITIAL_SPEED = 0; // 0; Starts stationary
    public static final double BOULDER_GRAVITY = 0.3;     // 0.3; Accelerates down
    public static final int BOULDER_DAMAGE = 5; // 5

    // ************************************
    // BOSSES
    // ************************************

    // --- APOLLO (Boss 1) ---
    public static final int APOLLO_WIDTH = 200; // 200
    public static final int APOLLO_HEIGHT = 125; // 125
    public static final int APOLLO_SPEED1 = 4; // 4
    public static final int APOLLO_SPEED2 = APOLLO_SPEED1 * 3 / 2; // 50% faster in Phase 2
    public static final int APOLLO_HP = 50; // 50
    public static final int APOLLO_SCORE_POINTS = 1000; // 1000

    // --- SUN (Apollo Projectile / Ability 1) ---
    public static final int SUN_WIDTH = 150; // 150
    public static final int SUN_HEIGHT = SUN_WIDTH;
    public static final double SUN_SPEED1 = 6; // 6
    public static final double SUN_SPEED2 = SUN_SPEED1 * 3 / 2; // 50% faster in Phase 2
    public static final int SUN_HP = 20; // 20

    // --- ZEUS (Boss 2) ---
    public static final int ZEUS_WIDTH = 150; // 150
    public static final int ZUES_HEIGHT = ZEUS_WIDTH;
    public static final int ZEUS_SPEED1 = 6; // 6
    public static final int ZEUS_SPEED2 = 8; // 8
    public static final int ZEUS_HP = 150; // 150
    public static final int ZEUS_SCORE_POINTS = 1500; // 1500
    public static final int ZEUS_SHOOT_TIMER = 45; // 45; 0.75 seconds
    public static final int ZEUS_SHOOT_TIMER2 = 30; // 30; 0.5 seconds (Enraged)

    // Zeus Ability 1 (Teleport) Delays
    public static final int ZEUS_ABILITY1_INTER_TELEPORT_PAUSE1 = 30; // 30; 0.5 seconds
    public static final int ZEUS_ABILITY1_INTER_TELEPORT_PAUSE2 = 20; // 20; 0.33 seconds (Enraged)

    // Zeus Ability 2 (Thunderstorm) Cooldown
    public static final int ZEUS_ABILITY2_TIMER = FPS * 5; // 5 seconds

    // --- LIGHTNING (Zeus Projectile / Ability 2) ---
    public static final int LIGHTING_WIDTH = 25; // 25
    public static final int LIGHTING_HEIGHT = 150; // 150
    public static final int LIGHTING_SPEED1 = 10; // 10
    public static final int LIGHTING_SPEED2 = 15; // 15
    public static final int LIGHTING_HP = 14; // 14

    // ************************************
    // GAME PROGRESSION & SCORE MILESTONES
    // ************************************

    // STAGE 1 PROGRESSION
    public static final int SCORE_STAGE1_PHASE1 = 0;
    public static final int SCORE_STAGE1_PHASE2 = HARPY_SCORE_POINTS * 10; // 10 Harpies
    public static final int SCORE_STAGE1_PHASE3 = HARPY_SCORE_POINTS * 30; // 30 Harpies
    public static final int SCORE_FOR_BOSS_1 = HARPY_SCORE_POINTS * 50; // 50 Harpies; Trigger Apollo

    // STAGE 2 PROGRESSION
    public static final int SCORE_STAGE2_PHASE1 = SCORE_FOR_BOSS_1 + APOLLO_SCORE_POINTS; // 1500
    public static final int SCORE_STAGE2_PHASE2 = SCORE_STAGE2_PHASE1 + 500; // 2000
    public static final int SCORE_STAGE2_PHASE3 = SCORE_STAGE2_PHASE2 + 750; // 2750
    public static final int SCORE_FOR_BOSS_2 = SCORE_STAGE2_PHASE3 + 750; // 3500; Trigger Zeus

    // STAGE 3 PROGRESSION
    public static final int SCORE_STAGE3_PHASE1 = SCORE_FOR_BOSS_2 + ZEUS_SCORE_POINTS; // 5000
    public static final int SCORE_STAGE3_PHASE2 = 100000; // 6000
    public static final int SCORE_STAGE3_PHASE3 = 300000; // 7000
    public static final int SCORE_FOR_BOSS_3 = 500000; // 8000

    // EXTRA STAGE
    public static final int SCORE_EXTRA_STAGE = 10000; // 10000

    // Level Milestones Array (Used by GameModel to check progression)
    public static final int[] LEVEL_MILESTONES = {
            SCORE_STAGE1_PHASE1, SCORE_STAGE1_PHASE2, SCORE_STAGE1_PHASE3, SCORE_FOR_BOSS_1,
            SCORE_STAGE2_PHASE1, SCORE_STAGE2_PHASE2, SCORE_STAGE2_PHASE3, SCORE_FOR_BOSS_2,
            SCORE_STAGE3_PHASE1, SCORE_STAGE3_PHASE2, SCORE_STAGE3_PHASE3, SCORE_FOR_BOSS_3,
            SCORE_EXTRA_STAGE
    };

    // VISUALS
    public static final int FLASH_TIMER = 5; // Duration of hit-flash (in frames)

    // Private constructor prevents instantiation
    private GameConstants(){}
}