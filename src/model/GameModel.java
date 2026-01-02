package model;

import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.geom.Area;

// --- GameModel (M) ---
public class GameModel {
    private ArrayList<GameObject> objects;
    private Player player;
    private boolean isGameOver = false;
    private Random rand = new Random();
    private ArrayList<GameObject> newObjectsBuffer; // 弾を追加するための予約リスト（ループ中のエラー回避用）
    private GameState state; // 現在のゲーム状態
    private boolean isFiring; // スペースキーが押されているか
    private int shotTimer; // 連射間隔を制御するタイマー
    private static int score = 0; //スコアの導入
    private boolean isBossActive = false; //ボスのフェーズの確認

    // 追加:ライフ機能用変数
    private int lives; 
    private static final int MAX_LIVES = 3; // 初期ライフ
    private int damageTimer; // ダメージを受けた後の無敵時間（フレーム数）

    public GameModel() {
        objects = new ArrayList<>();
        newObjectsBuffer = new ArrayList<>();
        state = GameState.TITLE; // 最初はタイトル画面から
    }

    public void initGame() {
        objects.clear();
        newObjectsBuffer.clear();
        player = new Player((GameConstants.FIELD_WIDTH -GameConstants.PLAYER_WIDTH)/2, GameConstants.FIELD_HEIGHT -GameConstants.PLAYER_HEIGHT);
        objects.add(player);

        isFiring = false;
        shotTimer = 0;
        state = GameState.PLAYING;
        score = 0; //スコアをリセット

        // 追加:ライフ初期化
        lives = MAX_LIVES;
        damageTimer = 0;

        //追加：ボスのフェーズの初期化
        isBossActive = false;
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

    public void update() {
        if (state != GameState.PLAYING) return;

        // 追加:無敵時間の更新
        if (damageTimer > 0) {
            damageTimer--;
        }

        checkLevelProgression();

        // --- 連射ロジック ---
        if (isFiring) {
            if (shotTimer == 0) {
                playerShoot();
                shotTimer = GameConstants.FPS / GameConstants.ARROW_PER_SECOND;
            }
        }
        if (shotTimer > 0) {
            shotTimer--;
        }

        //Logic for Enemy Shooting
        // We iterate through all objects to find Enemies
        for (GameObject obj : objects) {
            if (obj instanceof Enemy) {
                if (rand.nextFloat(100) < GameConstants.FEATHER_SPAWNRATE / GameConstants.FPS) {
                    // Calculate spawn position (center of the enemy)
                    int featherX = obj.getX() + (GameConstants.ENEMY_WIDTH-GameConstants.FEATHER_WIDTH)/2;
                    int featherY = obj.getY() + GameConstants.ENEMY_HEIGHT;

                    newObjectsBuffer.add(new Feather(featherX, featherY));
                }
            }
        }

        // オブジェクト追加
        objects.addAll(newObjectsBuffer);
        newObjectsBuffer.clear();
        spawnEnemy();

        // 移動
        for (GameObject obj : objects) {
            obj.move();
        }

        // 当たり判定
        checkCollisions();

        // 削除
        objects.removeIf(obj -> obj.isDead());
    }


    private void checkLevelProgression() {
        // ボスがいたら、何もしない
        if (isBossActive) return;

        // SCOREを達成すれば
        if (score >= GameConstants.SCORE_FOR_BOSS_1) {
            System.out.println("BOSS PHASE STARTED! Score: " + score);

            // 1. ボスのフェーズになる
            isBossActive = true;

            // 2. 全ての敵を消す
            clearEnemies();

            // 3. BOSSの登場
            spawnBoss();
        }
    }

    // 全ての敵を消すメソッド
    private void clearEnemies() {
        // "敵または羽の場合、消す"
        objects.removeIf(obj -> obj instanceof Enemy || obj instanceof Feather);
    }

    // プレイヤーが撃つ（Controllerから呼ばれる）
    public void playerShoot() {
        if (!isGameOver) {
            // プレイヤーの中央上から発射
            Arrow a = new Arrow(player.getX() + (GameConstants.PLAYER_WIDTH-GameConstants.ARROW_WIDTH)/2, player.getY() - GameConstants.ARROW_HEIGHT);
            newObjectsBuffer.add(a);
        }
    }

    // 敵を出現させる
    public void spawnEnemy() {
        //ボスのフェーズの時に、何もしない
        if(isBossActive) return;

        if (rand.nextFloat(100) < GameConstants.ENEMY_SPAWNRATE / GameConstants.FPS) { // 3%の確率で出現（適当な頻度）
            int randomX = rand.nextInt(GameConstants.FIELD_WIDTH -GameConstants.ENEMY_WIDTH+1);
            Enemy e = new Enemy(randomX, GameConstants.HUD_HEIGHT-GameConstants.ENEMY_HEIGHT);
            newObjectsBuffer.add(e);
        }
    }


    private void spawnBoss() {
        Apollo boss = new Apollo(this);
        objects.add(boss);
        System.out.println("APOLLO HAS DESCENDED!");
    }

    public void shootSun(int ApolloX, int ApolloY, int ApolloSpeedX, boolean isSecondPhase){
        Sun sun = new Sun(ApolloX, ApolloY, ApolloSpeedX, isSecondPhase);
        newObjectsBuffer.add(sun);
        System.out.println("Sun shot");
    }

    // 追加:ダメージ処理メソッド
    private void takeDamage() {
        if (damageTimer == 0) { // 無敵時間中でなければダメージ
            lives--;
            damageTimer = 60; // 60フレーム（約1秒）無敵にする
            System.out.println("Damage taken! Lives remaining: " + lives);

            if (lives <= 0) {
                state = GameState.GAMEOVER;
                System.out.println("GAME OVER");
            }
        }
    }

    private boolean checkIntersection(GameObject obj1, GameObject obj2) {
        // 1. take the precise shapes
        Shape s1 = obj1.getShape();
        Shape s2 = obj2.getShape();

        // 2. Quick check: if the outer rectangles don't touch we skip the complicated calculations
        if (!s1.getBounds2D().intersects(s2.getBounds2D())) {
            return false;
        }

        // 3. Precise Calculation
        Area area1 = new Area(s1);
        Area area2 = new Area(s2);

        area1.intersect(area2);

        // if the area is not empty it means they are touching
        return !area1.isEmpty();
    }

    // 当たり判定ロジック
    private void checkCollisions() {

        // Player vs Enemy
        for (GameObject obj : objects) {
            if (obj instanceof Enemy || obj instanceof Boss || obj instanceof Sun) {
                if (checkIntersection(player, obj)) {
                    takeDamage();
                }
            }
        }

        // Arrow vs Enemy OR Boss
        for (GameObject b : objects) {
            if (b instanceof Arrow) {
                Arrow arrow = (Arrow) b; // Cast for using Arrow method
                for (GameObject e : objects) {

                    // CASE 1 VS Enemy
                    if (e instanceof Enemy) {
                        Enemy enemy = (Enemy) e; // Cast for using Enemy method
                        if (!b.isDead() && !e.isDead() && checkIntersection(b, e)) {
                            arrow.setDead(true); // 弾消滅
                            enemy.takeDamage(arrow.getDamage()); // 敵が矢のダメージを受ける
                        }
                    }

                    // CASE 2 VS BOSS
                    if (e instanceof Boss){
                        Boss boss = (Boss) e;
                        if (!b.isDead() && !e.isDead() && checkIntersection(b, e)){
                            arrow.setDead(true);
                            boss.takeDamage(arrow.getDamage());
                        }
                    }

                    // CASE 3 VS SUN
                    if(e instanceof Sun){
                        if (!b.isDead() && !e.isDead() && checkIntersection(b, e)){
                            arrow.setDead(true);
                        }
                    }

                }
            }
        }

        // Feather vs Player
        for (GameObject obj : objects) {
            if (obj instanceof Feather) {
                if (checkIntersection(obj, player)) {
                    takeDamage();
                    obj.setDead(true);
                }
            }
        }
    }



    //SETTERS & GETTERS
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
    
    // 無敵時間中かどうか
    public boolean isInvincible() {
        return damageTimer > 0;
    }
}
