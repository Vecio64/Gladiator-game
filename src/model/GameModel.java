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

    // Score progression
    private static int score = 0; //スコアの導入
    private int nextTargetScore;
    private int currentLevelIndex = 0;
    private boolean isBossActive = false; //ボスのフェーズの確認

    // Enemies Spawnrate
    private float harpySpawnrate;

    // 追加:ライフ機能用変数
    private int lives;// 初期ライフ
    private int damageTimer; // ダメージを受けた後の無敵時間（フレーム数）

    public GameModel() {
        objects = new ArrayList<>();
        newObjectsBuffer = new ArrayList<>();
        state = GameState.TITLE; // 最初はタイトル画面から
    }

    public void initGame() {
        objects.clear();
        newObjectsBuffer.clear();
        player = new Player((GameConstants.FIELD_WIDTH -GameConstants.PLAYER_WIDTH)/2, GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - GameConstants.PLAYER_HEIGHT);
        objects.add(player);

        isFiring = false;
        shotTimer = 0;
        state = GameState.PLAYING;
        score = 0; //スコアをリセット

        // 追加:ライフ初期化
        lives = GameConstants.PLAYER_MAX_LIVES;
        damageTimer = 0;

        //Score progression resets
        this.currentLevelIndex = 0;
        this.nextTargetScore = GameConstants.LEVEL_MILESTONES[currentLevelIndex];
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

        checkLevelProgression();

        // 追加:無敵時間の更新
        if (damageTimer > 0) {
            damageTimer--;
        }

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
            if (obj instanceof Harpy) {
                if (rand.nextFloat(100) < GameConstants.FEATHER_SPAWNRATE / GameConstants.FPS) {
                    // Calculate spawn position (center of the enemy)
                    int featherX = obj.getX() + (GameConstants.HARPY_WIDTH -GameConstants.FEATHER_WIDTH)/2;
                    int featherY = obj.getY() + GameConstants.HARPY_HEIGHT;

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
        //次のフェーズがなかったらreturn
        if (currentLevelIndex >= GameConstants.LEVEL_MILESTONES.length) return;

        if (score >= nextTargetScore){
            //apply the effect we decided in the applyLevelEffects function
            applyLevelEffects(currentLevelIndex);
            currentLevelIndex++;
            nextTargetScore = GameConstants.LEVEL_MILESTONES[currentLevelIndex];
        }
    }

    private void applyLevelEffects(int levelIndex) {
        switch (levelIndex) {
            case 0: //START OF THE GAME
                System.out.println("Start of the Game");
                this.harpySpawnrate = GameConstants.HARPY_SPAWNRATE;
                break;

            case 1: // Raggiunti 100 punti
                System.out.println("Difficulty UP! INSANE enemies.");
                // Aumentiamo ancora (doppio rispetto all'inizio)
                this.harpySpawnrate = GameConstants.HARPY_SPAWNRATE * 2.0f;
                break;

            case 2: // Raggiunti 300 punti
                System.out.println("Difficulty UP! INSANE enemies.");
                // Aumentiamo ancora (doppio rispetto all'inizio)
                this.harpySpawnrate = GameConstants.HARPY_SPAWNRATE * 3.0f;
                break;

            case 3: // Raggiunti 500 punti
                System.out.println("WARNING: BOSS APPROACHING!");
                isBossActive = true;
                clearEnemies();
                spawnBoss();
                break;

            // In futuro aggiungerai qui case 3 (1500 punti), case 4, ecc.
        }
    }

    public void bossDefeated(){
        System.out.println("BOSS DEFEATED! Stage clear.");
        this.isBossActive = false;
        healPlayer();
    }

    // 全ての敵を消すメソッド
    private void clearEnemies() {
        // "敵または羽の場合、消す"
        objects.removeIf(obj -> obj instanceof Harpy || obj instanceof Feather);
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

        if (rand.nextFloat(100) < harpySpawnrate / GameConstants.FPS) { // 3%の確率で出現（適当な頻度）
            int randomX = rand.nextInt(GameConstants.FIELD_WIDTH - GameConstants.HARPY_WIDTH + 1);
            Harpy e = new Harpy(randomX, GameConstants.HUD_HEIGHT - GameConstants.HARPY_HEIGHT);
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

    // ダメージ処理メソッド
    private void playerTakesDamage() {
        if (damageTimer == 0) { // 無敵時間中でなければダメージ
            lives--;
            damageTimer = 180; // 180フレーム（約3秒）無敵にする
            System.out.println("Damage taken! Lives remaining: " + lives);

            if (lives <= 0) {
                state = GameState.GAMEOVER;
                System.out.println("GAME OVER");
            }
        }
    }

    private void healPlayer(){
        lives = GameConstants.PLAYER_MAX_LIVES;
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

        // 1. Player vs ANYTHING HOSTILE (Enemy, Boss, Sun)
        for (GameObject obj : objects) {
            // Check if object is a living hostile entity
            if (obj instanceof HostileEntity) {
                if (checkIntersection(player, obj)) {
                    playerTakesDamage(); // Player loses 1 life (Fixed)
                }
            }
            // 2. Player vs ENEMY PROJECTILES (Feather)
            // Logic: Is it a Projectile? YES. Is it NOT my own Arrow? YES.
            else if (obj instanceof Projectile && !(obj instanceof Arrow)) {
                if (checkIntersection(player, obj)) {
                    playerTakesDamage();      // Player loses 1 life (Fixed)
                    obj.setDead(true); // Destroy the feather
                }
            }
        }

        // 3. Arrow vs HOSTILE ENTITIES (Enemy, Boss, Sun)
        for (GameObject objA : objects) {
            if (objA instanceof Arrow) {
                Arrow arrow = (Arrow) objA;

                for (GameObject objB : objects) {
                    // Unified check for all enemies
                    if (objB instanceof HostileEntity) {
                        HostileEntity hostile = (HostileEntity) objB;

                        if (!arrow.isDead() && !hostile.isDead() && checkIntersection(arrow, hostile)) {

                            arrow.setDead(true); // Arrow breaks

                            // HERE we use the variable damage!
                            hostile.takeDamage(arrow.getDamage());
                        }
                    }
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
