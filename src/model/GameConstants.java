package model;

public final class GameConstants {

    // こらはゲームの定数を含めるクラスです

    // 画面のサイズ
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 850;
    public static final int HUD_HEIGHT = 50;
    public static final int FIELD_WIDTH = 600;
    public static final int FIELD_HEIGHT = WINDOW_HEIGHT - HUD_HEIGHT;

    // ゲームの設定
    public static final int FPS = 60;

    // 背景の設定
    public static final double SCREEN_SPEED = 1.0;

    // PLAYERの設定
    public static final int PLAYER_WIDTH = 90;
    public static final int PLAYER_HEIGHT = PLAYER_WIDTH*923/721; // set to the image height width ration
    public static final int PLAYER_SPEED = 8;
    public static final int PLAYER_MAX_LIVES = 3;

    // ARROWの設定
    public static final int ARROW_WIDTH = 6;
    public static final int ARROW_HEIGHT = ARROW_WIDTH * 367/41; // set to the image height width ration
    public static final int ARROW_SPEED = 30;
    public static final int ARROW_INTERVAL = 20;
    public static final int ARROW_DAMAGE = 1;

    // HARPYの設定
    public static final int HARPY_WIDTH = 100;
    public static final int HARPY_HEIGHT = HARPY_WIDTH *1911/1708; // set to the image height width ration
    public static final int HARPY_XSPEED = 4;
    public static final int HARPY_YSPEED = 2;
    public static final int HARPY_SPAWN_INTERVAL = 120;
    public static final int HARPY_SPAWN_VARIANCE = HARPY_SPAWN_INTERVAL / 2;
    public static final int HARPY_HP = 2;
    public static final int HARPY_SCORE_POINTS = 10;

    // FEATHERの設定
    public static final int FEATHER_WIDTH = 10;
    public static final int FEATHER_HEIGHT = FEATHER_WIDTH*1698/378; // set to the image height width ration
    public static final int FEATHER_SPEED = 7;
    public static final int FEATHER_FIRE_INTERVAL = 90;
    public static final int FEATHER_FIRE_VARIANCE = FEATHER_FIRE_INTERVAL / 2;
    public static final int FEATHER_DAMAGE = 1;

    // BOSSの設定


    // APOLLOの設定
    public static final int APOLLO_WIDTH = 200;
    public static final int APOLLO_HEIGHT = APOLLO_WIDTH * 1556 / 2463;
    public static final int APOLLO_SPEED = 3;
    public static final int APOLLO_HP = 1;
    public static final int APOLLO_SCORE_POINTS = 1000;

    // SUNの設定
    public static final int SUN_WIDTH = 150;
    public static final int SUN_HEIGHT = SUN_WIDTH;
    public static final double SUN_SPEED = 4;


    //SCOREの設定
    // STAGE 1
    public static final int SCORE_STAGE1_PHASE1 = 0;
    public static final int SCORE_STAGE1_PHASE2 = HARPY_SCORE_POINTS * 10;
    public static final int SCORE_STAGE1_PHASE3 = HARPY_SCORE_POINTS * 30;
    public static final int SCORE_FOR_BOSS_1 = HARPY_SCORE_POINTS * 50;
    // STAGE 2
    public static final int SCORE_STAGE2_PHASE1 = SCORE_FOR_BOSS_1 + APOLLO_SCORE_POINTS;
    public static final int SCORE_STAGE2_PHASE2 = 2000;
    public static final int SCORE_STAGE2_PHASE3 = 3000;
    public static final int SCORE_FOR_BOSS_2 = 4000;
    // STAGE 3
    public static final int SCORE_STAGE3_PHASE1 = 100;
    public static final int SCORE_STAGE3_PHASE2 = 100;
    public static final int SCORE_STAGE3_PHASE3 = 300;
    public static final int SCORE_FOR_BOSS_3 = 500;

    // We put them all in an Array
    public static final int[] LEVEL_MILESTONES = {
            SCORE_STAGE1_PHASE1,
            SCORE_STAGE1_PHASE2,
            SCORE_STAGE1_PHASE3,
            SCORE_FOR_BOSS_1,
            SCORE_STAGE2_PHASE1,
            SCORE_STAGE2_PHASE2,
            SCORE_STAGE2_PHASE3,
            SCORE_FOR_BOSS_2,
            SCORE_STAGE3_PHASE1,
            SCORE_STAGE3_PHASE2,
            SCORE_STAGE3_PHASE3,
            SCORE_FOR_BOSS_3
    };

    //他の設定
    public static final int FLASH_TIMER = 5;



    private GameConstants(){} //オブジェクトを作らないようにprivateにする
}
