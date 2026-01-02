package model;

public final class GameConstants {

    // こらはゲームの定数を含めるクラスです

    // 画面のサイズ
    public static final int WINDOW_WIDTH = 600;
    public static final int WINDOW_HEIGHT = 800;
    public static final int HUD_HEIGHT = 50;
    public static final int FIELD_WIDTH = 600;
    public static final int FIELD_HEIGHT = WINDOW_HEIGHT - HUD_HEIGHT;

    //SCOREの設定
    public static final int SCORE_FOR_BOSS_1 = 100;
    public static final int SCORE_FOR_BOSS_2 = 3000;
    public static final int SCORE_FOR_BOSS_3 = 6000;

    // ゲームの設定
    public static final int FPS = 60;

    // PLAYERの設定
    public static final int PLAYER_WIDTH = 100;
    public static final int PLAYER_HEIGHT = PLAYER_WIDTH*923/721; // set to the image height width ration
    public static final int PLAYER_SPEED = 7;

    // ARROWの設定
    public static final int ARROW_WIDTH = 5;
    public static final int ARROW_HEIGHT = ARROW_WIDTH * 367/41; // set to the image height width ration
    public static final int ARROW_SPEED = 10;
    public static final int ARROW_PER_SECOND = 4;
    public static final int ARROW_DAMAGE = 1;

    // ENEMYの設定
    public static final int ENEMY_WIDTH = 100;
    public static final int ENEMY_HEIGHT = ENEMY_WIDTH*1911/1708; // set to the image height width ration
    public static final int ENEMY_XSPEED = 4;
    public static final int ENEMY_YSPEED = 2;
    public static final float ENEMY_SPAWNRATE = 100; // spawnrate probability per second
    public static final int ENEMY_HP = 2;
    public static final int ENEMY_SCORE_POINTS = 10;

    // FEATHERの設定
    public static final int FEATHER_WIDTH = 10;
    public static final int FEATHER_HEIGHT = FEATHER_WIDTH*1698/378; // set to the image height width ration
    public static final int FEATHER_SPEED = 7;
    public static final float FEATHER_SPAWNRATE = 33; // spawnrate probability per second

    // BOSSの設定


    // APOLLOの設定
    public static final int APOLLO_WIDTH = 200;
    public static final int APOLLO_HEIGHT = APOLLO_WIDTH * 1556 / 2463;
    public static final int APOLLO_SPEED = 3;
    public static final int APOLLO_HP = 20;

    // SUNの設定
    public static final int SUN_WIDTH = 150;
    public static final int SUN_HEIGHT = SUN_WIDTH;
    public static final double SUN_SPEED = 4;

    //他の設定
    public static final int FLASH_TIMER = 3;



    private GameConstants(){} //オブジェクトを作らないようにprivateにする
}
