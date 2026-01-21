package model;

import view.ResourceManager;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
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
    private int arrowDamage;
    private int arrowInterval;

    // Score progression
    private static int score = 0; //スコアの導入
    private int nextTargetScore;
    private int currentLevelIndex = 0;
    private boolean isBossActive = false; //ボスのフェーズの確認

    // 追加:ライフ機能用変数
    private int lives;// 初期ライフ
    private int damageTimer; // ダメージを受けた後の無敵時間（フレーム数）

    private int ability1Timer;
    private int ability2Timer;
    private int ability3Timer;

    //background
    private Background background;

    // for writing the stage number in GamePanel
    private int currentStage;

    private String[] currentMessageLines;

    private List<EnemySpawner> activeSpawners;

    public GameModel() {
        objects = new ArrayList<>();
        newObjectsBuffer = new ArrayList<>();
        activeSpawners = new ArrayList<>();
        state = GameState.TITLE; // 最初はタイトル画面から
    }

    public void initGame() {
        objects.clear();
        newObjectsBuffer.clear();
        activeSpawners.clear();
        player = new Player((GameConstants.WINDOW_WIDTH -GameConstants.PLAYER_WIDTH)/2, GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - GameConstants.PLAYER_HEIGHT);
        objects.add(player);

        // Initialize Background
        background = new Background();

        isFiring = false;
        shotTimer = 0;
        arrowDamage = GameConstants.ARROW_DAMAGE;
        arrowInterval = GameConstants.ARROW_INTERVAL;
        state = GameState.PLAYING;
        score = 0; //スコアをリセット

        // 追加:ライフ初期化
        lives = GameConstants.PLAYER_MAX_LIVES;
        damageTimer = 0;

        //Score progression resets
        this.currentLevelIndex = 0;
        this.nextTargetScore = GameConstants.LEVEL_MILESTONES[currentLevelIndex];
        isBossActive = false;

        // STAGE 1 SETUP: Add Harpy Spawner
        activeSpawners.add(new EnemySpawner(Harpy.class, GameConstants.HARPY_SPAWN_INTERVAL, GameConstants.HARPY_SPAWN_VARIANCE));
        this.currentStage = 1;


        String tutorial = "WELCOME GLADIATOR!\n\n" +
                "Controls:\n" +
                "[KEY-ARROWS] Move\n" +
                "[SPACE] Shoot\n" +
                "[P] Pause\n\n" +
                "Defeat enemies\n" +
                "and survive.\n" +
                "Good Luck!";
        showMessage(tutorial);


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

        // Update background scrolling
        if(background!= null) {
            background.update();
        }

        checkLevelProgression();



        // --- 連射ロジック ---
        if (isFiring) {
            if (shotTimer == 0) {
                playerShoot();
                shotTimer = GameConstants.ARROW_INTERVAL;
            }
        }
        if (shotTimer > 0) {
            shotTimer--;
        }

        // 無敵時間の更新
        if (damageTimer > 0) {
            damageTimer--;
        }

        if (ability1Timer > 0) {
            ability1Timer--;
        }

        if (ability2Timer > 0) {
            ability2Timer--;
        }

        if (ability3Timer > 0) {
            ability3Timer--;
        }

        // --- NEW SPAWN LOGIC ---
        // Iterate through all active spawners
        if (!isBossActive) {
            for (EnemySpawner spawner : activeSpawners) {
                // If spawner says "True", create that enemy
                if (spawner.update()) {
                    spawnMinion(spawner.getEnemyType());
                }
            }
        }

        // オブジェクト追加
        objects.addAll(newObjectsBuffer);
        newObjectsBuffer.clear();

        // 移動
        for (GameObject obj : objects) {
            obj.move();
        }

        // 当たり判定
        checkCollisions();

        // 削除
        objects.removeIf(obj -> obj.isDead());
    }

    /**
     * Helper method to instantiate the correct enemy based on Class type.
     */
    private void spawnMinion(Class<? extends Minion> type) {
        int x,y;
        if (type == Harpy.class) {
            x = rand.nextInt(GameConstants.WINDOW_WIDTH - GameConstants.HARPY_WIDTH); // Simple random X
            y = GameConstants.HUD_HEIGHT - GameConstants.HARPY_HEIGHT; // Start at top
            Harpy h = new Harpy(x, y, this);
            newObjectsBuffer.add(h);
        }
        else if (type == Cyclops.class) {
            x = rand.nextInt(GameConstants.WINDOW_WIDTH - GameConstants.CYCLOPS_WIDTH); // Simple random X
            y = GameConstants.HUD_HEIGHT - GameConstants.CYCLOPS_HEIGHT;
            Cyclops g = new Cyclops(x, y, this); // Pass 'this' (GameModel)
            newObjectsBuffer.add(g);
        }
    }

    /**
     * Allows enemies (Minions/Bosses) to add projectiles to the game.
     * Using a specific method is safer than exposing the whole list.
     */
    public void spawnEnemyProjectile(Projectile p) {
        if (p != null) {
            newObjectsBuffer.add(p);
        }
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
            System.out.println("The current level index is: " + currentLevelIndex);
            nextTargetScore = GameConstants.LEVEL_MILESTONES[currentLevelIndex];
        }
    }

    private void applyLevelEffects(int levelIndex) {
        switch (levelIndex) {
            case 0: //START OF THE GAME
                System.out.println("Start of the Game");
                break;

            case 1:
                System.out.println("Difficulty UP!");
                for (EnemySpawner s : activeSpawners) s.increaseDifficulty(0.8);
                break;

            case 2:
                System.out.println("Difficulty UP!");
                for (EnemySpawner s : activeSpawners) s.increaseDifficulty(0.8);
                break;

            case 3:
                showMessage("WARNING!\n\nBOSS DETECTED:\nAPOLLO\n\nPrepare for battle!");
                isBossActive = true;
                clearEverything();
                spawnApollo();
                healPlayer();
                break;
            case 4:
                showMessage("STAGE 1 CLEARED!\n\nEntering the Heavens...\n\nArrow Damage doubled!\n\n Press [1] to use\nABILITY 1:\nAPOLLO'S SUN");
                if (background != null) {
                    clearEverything();
                    healPlayer();
                    background.setImage(ResourceManager.stage2Img);
                    background.setSpeed(GameConstants.SCREEN_SPEED);
                    ability1Timer = 0;
                    this.currentStage = 2;

                    // DOUBLE ARROW DAMAGE
                    arrowDamage*=2;

                    // Clear old spawners (remove Stage 1 config)
                    activeSpawners.clear();
                    // Harpy Spawner
                    activeSpawners.add(new EnemySpawner(Harpy.class, 100, 50));
                    // Cyclops Spawner
                    activeSpawners.add(new EnemySpawner(Cyclops.class,
                             GameConstants.CYCLOPS_SPAWN_INTERVAL,
                             GameConstants.CYCLOPS_SPAWN_VARIANCE));

                }
                break;
            case 5:
                System.out.println("Difficulty UP!");
                for (EnemySpawner s : activeSpawners) s.increaseDifficulty(0.9);
                break;
            case 6:
                System.out.println("Difficulty UP!");
                for (EnemySpawner s : activeSpawners) s.increaseDifficulty(0.9);
                break;
            case 7:
                showMessage("WARNING!\n\nBOSS DETECTED:\nZEUS\n\nPrepare for battle!");
                isBossActive = true;
                clearEverything();
                spawnZeus();
                healPlayer();
                break;
            case 8:
                showMessage("STAGE 2 CLEARED!\n\nEntering the INFERNO...\n\nShooting speed increased!\n\nPress [2] to use\nABILITY 2:\nZEUS'S LIGHTING");
                if (background != null) {
                    clearEverything();
                    background.setImage(ResourceManager.stage1Img);
                    background.setSpeed(0);
                    ability2Timer = 0;
                    arrowInterval = GameConstants.ARROW_INTERVAL2;
                    this.currentStage = 3;
                    activeSpawners.clear();
                    activeSpawners.add(new EnemySpawner(Harpy.class, 100, 50));
                    // Cyclops Spawner
                    activeSpawners.add(new EnemySpawner(Cyclops.class,
                            GameConstants.CYCLOPS_SPAWN_INTERVAL,
                            GameConstants.CYCLOPS_SPAWN_VARIANCE));
                }
                break;
            case 9:

        }
    }

    public void showMessage(String text) {
        // Split the text by newline character to handle multiple lines
        this.currentMessageLines = text.split("\n");
        this.state = GameState.MESSAGE;
    }

    public void bossDefeated(){
        System.out.println("BOSS DEFEATED! Stage clear.");
        this.isBossActive = false;
        healPlayer();
    }

    // 全ての敵を消すメソッド
    private void clearEverything() {
        // "敵または羽の場合、消す"
        objects.removeIf(obj -> obj instanceof HostileEntity || obj instanceof Projectile);
        newObjectsBuffer.removeIf(obj -> obj instanceof HostileEntity || obj instanceof Projectile);
    }

    // プレイヤーが撃つ（Controllerから呼ばれる）
    public void playerShoot() {
        if (!isGameOver) {
            // プレイヤーの中央上から発射
            Arrow a = new Arrow(player.getX() + (GameConstants.PLAYER_WIDTH-GameConstants.ARROW_WIDTH)/2, player.getY() - GameConstants.ARROW_HEIGHT, arrowDamage);
            newObjectsBuffer.add(a);
        }
    }

    public void ability1(){
        if (ability1Timer > 0) return;
        ability1Timer = GameConstants.ABILITY1TIMER;
        Sun sun = new Sun(
                player.getX() + GameConstants.PLAYER_WIDTH / 2,
                player.getY(),
                0, false, true
        );        newObjectsBuffer.add(sun);
        System.out.println("Player shoots sun");
    }

    public void ability2(){
        if (ability2Timer > 0) return;
        ability2Timer = GameConstants.ABILITY2TIMER;
        Lighting l = new Lighting(
                player.getX() + GameConstants.PLAYER_WIDTH / 2,
                player.getY(),
                0, false, true, false
        );        newObjectsBuffer.add(l);
        System.out.println("Player shoots Lighting");
    }

    private void spawnApollo() {
        Apollo apollo = new Apollo(this);
        objects.add(apollo);
        System.out.println("APOLLO HAS DESCENDED!");
    }

    public void spawnZeus() {
        Zeus zeus = new Zeus(this);
        objects.add(zeus);
        System.out.println("ZEUS HAS DESCENDED!");
    }

    // ダメージ処理メソッド
    private void playerTakesDamage() {
        if (damageTimer == 0) { // 無敵時間中でなければダメージ
            lives--;
            damageTimer = 120; // 180フレーム（約3秒）無敵にする
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

                // SUN CASE: p1 is a Sun, it takes damage equal to p2's damage
                if (p1 instanceof BossProjectile) {
                    ((BossProjectile) p1).reduceHealth(p2.getDamage());
                }

            } else if (p2.getPowerLevel() > p1.getPowerLevel()) {
                p1.setDead(); // p2 dominates

                // SUN CASE: If p2 is a Sun, it takes damage equal to p1's damage
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

                    // SUN CASE: If the projectile is a Sun, it also loses HP upon contact
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


    // 無敵時間中かどうか
    public boolean isInvincible() {
        return damageTimer > 0;
    }

    public int getAbilityNthTimer(int n) {
        switch (n){
            case 1 -> {
                return ability1Timer;
            }
            case 2 -> {
                return ability2Timer;
            }
            case 3 -> {
                return ability3Timer;
            }
        }
        return ability1Timer;
    }

    public boolean isAbilityUnclocked(int abilityIndex) {
        // Logic for Ability 1 (Sun)
        if (abilityIndex == 1) {
            // Unlocks after defeating the first boss (Apollo)
            // Apollo is Level Index 4. So > 4 means Stage 2 started.
            return this.currentLevelIndex > 4;
        }
        // Logic for Ability 2 (Lighting)
        if (abilityIndex == 2) {
            // Unlocks after defeating the second boss (Zeus)
            // Zeus is Level Index 8. So > 8 means Stage 3 started.
            return this.currentLevelIndex > 8;
        }

        // Future logic for Ability 2 and 3
        return false;
    }

    public void resumeGame() {
        this.state = GameState.PLAYING;
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
