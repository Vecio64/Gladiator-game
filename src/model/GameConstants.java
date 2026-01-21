package model;

public final class GameConstants {

    // こらはゲームの定数を含めるクラスです

    // 画面のサイズ
    public static final int WINDOW_WIDTH = 600;

    //HEIGTHS
    public static final int HUD_HEIGHT = 50;
    public static final int FIELD_HEIGHT = 800;
    public static final int BOTTOM_HUD_HEIGHT = 100;
    public static final int WINDOW_HEIGHT = HUD_HEIGHT + FIELD_HEIGHT + BOTTOM_HUD_HEIGHT;

    // ゲームの設定
    public static final int FPS = 60;

    // 背景の設定
    public static final double SCREEN_SPEED = 1.0;

    // PLAYERの設定
    public static final int PLAYER_WIDTH = 90;
    public static final int PLAYER_HEIGHT = PLAYER_WIDTH*923/721; // set to the image height width ration
    public static final int PLAYER_SPEED = 8;
    public static final int PLAYER_MAX_LIVES = 3;

    //ABILITY TIMERS
    public static final int ABILITY1TIMER = FPS * 15; // 15 seconds
    public static final int ABILITY2TIMER = FPS * 8; // 10 seconds
    public static final int ABILITY3TIMER = FPS * 10; // 10 seconds

    // ARROWの設定
    public static final int ARROW_WIDTH = 8;
    public static final int ARROW_HEIGHT = ARROW_WIDTH * 367/41; // set to the image height width ration
    public static final int ARROW_SPEED = 30;
    public static final int ARROW_INTERVAL = 20;
    public static final int ARROW_INTERVAL2 = 15;
    public static final int ARROW_DAMAGE = 1;

    // ************************************
    // MINIONSの設定
    // ************************************

    // HARPYの設定
    public static final int HARPY_WIDTH = 100;
    public static final int HARPY_HEIGHT = HARPY_WIDTH *1911/1708; // set to the image height width ration
    public static final int HARPY_XSPEED = 4;
    public static final int HARPY_YSPEED = 2;
    public static final int HARPY_HP = 2;
    public static final int HARPY_SCORE_POINTS = 10;
    public static final int HARPY_SPAWN_INTERVAL = 120;
    public static final int HARPY_SPAWN_VARIANCE = HARPY_SPAWN_INTERVAL / 2;

    // FEATHERの設定
    public static final int FEATHER_WIDTH = 12;
    public static final int FEATHER_HEIGHT = FEATHER_WIDTH * 1698/378; // set to the image height width ration
    public static final int FEATHER_SPEED = 7;
    public static final int FEATHER_FIRE_INTERVAL = 90;
    public static final int FEATHER_FIRE_VARIANCE = FEATHER_FIRE_INTERVAL / 2;
    public static final int FEATHER_DAMAGE = 1;

    // CYCLOPSの設定
    public static final int CYCLOPS_WIDTH = 150;
    public static final int CYCLOPS_HEIGHT = CYCLOPS_WIDTH;
    public static final double CYCLOPS_YSPEED = 2;
    public static final int CYCLOPS_HP = 10;
    public static final int CYCLOPS_SCORE_POINTS = 50;
    public static final int CYCLOPS_SPAWN_INTERVAL = 400;
    public static final int CYCLOPS_SPAWN_VARIANCE = CYCLOPS_SPAWN_INTERVAL / 2;
    public static final int CYCLOPS_MOVEMENT_TIMER = 20;
    public static final int CYCLOPS_ATTACK_TIMER = 60;



    // BOULDERの設定
    public static final int BOULDER_WIDTH = 100;
    public static final int BOULDER_HEIGHT = BOULDER_WIDTH;
    public static final double BOULDER_INITIAL_SPEED = 0;
    public static final double BOULDER_GRAVITY = 0.3;
    public static final int BOULDER_DAMAGE = 5;


    // ************************************
    // BOSSの設定
    // ************************************

    // APOLLOの設定
    public static final int APOLLO_WIDTH = 200;
    public static final int APOLLO_HEIGHT = APOLLO_WIDTH * 1556 / 2463;
    public static final int APOLLO_SPEED1 = 4;
    public static final int APOLLO_SPEED2 = APOLLO_SPEED1 * 3 / 2;
    public static final int APOLLO_HP = 50; //50
    public static final int APOLLO_SCORE_POINTS = 1000;

    // SUNの設定
    public static final int SUN_WIDTH = 150;
    public static final int SUN_HEIGHT = SUN_WIDTH;
    public static final double SUN_SPEED1 = 6;
    public static final double SUN_SPEED2 = SUN_SPEED1 * 3 / 2;
    public static final int SUN_DAMAGE = 1;
    public static final int SUN_HP = 20;

    // ZEUSの設定
    public static final int ZEUS_SPEED = 4;
    public static final int ZEUS_SPEED2 = 8;
    public static final int ZEUS_WIDTH = 150;
    public static final int ZUES_HEIGHT = 150;
    public static final int ZEUS_HP = 200; // 100
    public static final int ZEUS_SCORE_POINTS = 1500;
    public static final int ZEUS_SHOOT_TIMER = 60;
    public static final int ZEUS_SHOOT_TIMER2 = 40;
    public static final int ZEUS_ABILITY1_PAUSE = 40;
    public static final int ZEUS_ABILITY1_PAUSE2 = 25;
    public static final int ZEUS_ABILITY2_TIMER = FPS * 6; // 10 seconds



    //LIGHTINGの設定
    public static final int LIGHTING_WIDTH = 25;
    public static final int LIGHTING_HEIGHT = 150;
    public static final int LIGHTING_SPEED1 = 10;
    public static final int LIGHTING_SPEED2 = 15;
    public static final int LIGHTING_DAMAGE = 1;
    public static final int LIGHTING_HP = 10;



    //SCOREの設定
    // STAGE 1
    public static final int SCORE_STAGE1_PHASE1 = 0;
    public static final int SCORE_STAGE1_PHASE2 = HARPY_SCORE_POINTS * 10; // 100
    public static final int SCORE_STAGE1_PHASE3 = HARPY_SCORE_POINTS * 30; // 300
    public static final int SCORE_FOR_BOSS_1 = HARPY_SCORE_POINTS * 50; // 500
    // STAGE 2
    public static final int SCORE_STAGE2_PHASE1 = SCORE_FOR_BOSS_1 + APOLLO_SCORE_POINTS; //1500
    public static final int SCORE_STAGE2_PHASE2 = SCORE_STAGE2_PHASE1 + 500; // 2000
    public static final int SCORE_STAGE2_PHASE3 = SCORE_STAGE2_PHASE2 + 750; // 2750
    public static final int SCORE_FOR_BOSS_2 = SCORE_STAGE2_PHASE3 + 750; // 3500
    // STAGE 3
    public static final int SCORE_STAGE3_PHASE1 = SCORE_FOR_BOSS_2 + ZEUS_SCORE_POINTS; // 5000
    public static final int SCORE_STAGE3_PHASE2 = 100000; // 6000
    public static final int SCORE_STAGE3_PHASE3 = 300000; // 7000
    public static final int SCORE_FOR_BOSS_3 = 500000; // 8000
    // EXTRA STAGE
    public static final int SCORE_EXTRA_STAGE = 10000; // 10000

    // We put them all in an Array
    public static final int[] LEVEL_MILESTONES = {
            SCORE_STAGE1_PHASE1, SCORE_STAGE1_PHASE2, SCORE_STAGE1_PHASE3, SCORE_FOR_BOSS_1,
            SCORE_STAGE2_PHASE1, SCORE_STAGE2_PHASE2, SCORE_STAGE2_PHASE3, SCORE_FOR_BOSS_2,
            SCORE_STAGE3_PHASE1, SCORE_STAGE3_PHASE2, SCORE_STAGE3_PHASE3, SCORE_FOR_BOSS_3,
            SCORE_EXTRA_STAGE
    };

    //他の設定
    public static final int FLASH_TIMER = 5;



    private GameConstants(){} //オブジェクトを作らないようにprivateにする
}
