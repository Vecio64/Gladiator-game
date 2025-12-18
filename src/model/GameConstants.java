package model;

public final class GameConstants {

    // こらはゲームの定数を含めるクラスです

    // 画面のサイズ
    public static final int SCREEN_WIDTH = 600;
    public static final int SCREEN_HEIGHT = 900;

    // ゲームの設定
    public static final int FPS = 60;

    // PLAYERの設定
    public static final int PLAYER_WIDTH = 100;
    public static final int PLAYER_HEIGHT = PLAYER_WIDTH*921/720; // set to the image height width ration
    public static final int PLAYER_SPEED = 5;

    // ARROWの設定
    public static final int ARROW_WIDTH = 5;
    public static final int ARROW_HEIGHT = ARROW_WIDTH * 367/41; // set to the image height width ration
    public static final int ARROW_SPEED = 10;
    public static final int ARROW_PER_SECOND = 5;

    // ENEMYの設定
    public static final int ENEMY_WIDTH = 100;
    public static final int ENEMY_HEIGHT = ENEMY_WIDTH*1911/1708; // set to the image height width ration
    public static final int ENEMY_XSPEED = 4;
    public static final int ENEMY_YSPEED = 2;
    public static final int ENEMY_SPAWNRATE = 3;


    // FEATHERの設定
    public static final int FEATHER_WIDTH = 10;
    public static final int FEATHER_HEIGHT = FEATHER_WIDTH*1698/378; // set to the image height width ration
    public static final int FEATHER_SPEED = 7;
    public static final int FEATHER_SPAWNRATE = 1;


    private GameConstants(){} //オブジェクトを作らないようにprivateにする
}
