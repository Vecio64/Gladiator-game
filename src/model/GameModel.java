package model;

import java.util.ArrayList;
import java.util.Random;

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
    private int score = 0; //スコアの導入

    public GameModel() {
        objects = new ArrayList<>();
        newObjectsBuffer = new ArrayList<>();
        state = GameState.TITLE; // 最初はタイトル画面から
    }

    public void initGame() {
        objects.clear();
        newObjectsBuffer.clear();
        player = new Player((GameConstants.SCREEN_WIDTH-GameConstants.PLAYER_WIDTH)/2, GameConstants.SCREEN_HEIGHT-GameConstants.PLAYER_HEIGHT);
        objects.add(player);

        isFiring = false;
        shotTimer = 0;
        state = GameState.PLAYING;
        score = 0; //スコアをリセット
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

        // --- 連射ロジック ---
        if (isFiring) {
            if (shotTimer == 0) {
                playerShoot();
                shotTimer = 5;
            }
        }
        if (shotTimer > 0) {
            shotTimer--;
        }

        // 1. オブジェクト追加
        objects.addAll(newObjectsBuffer);
        newObjectsBuffer.clear();
        spawnEnemy();

        // 2. 移動
        for (GameObject obj : objects) {
            obj.move();
        }

        // 3. 当たり判定
        checkCollisions();

        // 4. 削除
        objects.removeIf(obj -> obj.isDead());
    }

    // プレイヤーが撃つ（Controllerから呼ばれる）
    public void playerShoot() {
        if (!isGameOver) {
            // プレイヤーの中央上から発射
            Arrow a = new Arrow(player.getX() + (GameConstants.PLAYER_WIDTH-GameConstants.BULLET_WIDTH)/2, player.getY() - GameConstants.BULLET_HEIGHT);
            newObjectsBuffer.add(a);
        }
    }

    // 敵を出現させる
    public void spawnEnemy() {
        if (rand.nextInt(100) < 3) { // 3%の確率で出現（適当な頻度）
            int randomX = rand.nextInt(GameConstants.SCREEN_WIDTH-GameConstants.ENEMY_WIDTH+1);
            Enemy e = new Enemy(randomX, -GameConstants.ENEMY_HEIGHT);
            newObjectsBuffer.add(e);
        }
    }

    // 当たり判定ロジック
    private void checkCollisions() {
        for (GameObject obj : objects) {
            if (obj instanceof Enemy) {
                if (player.getBounds().intersects(obj.getBounds())) {
                    state = GameState.GAMEOVER;
                    System.out.println("GAME OVER!!");
                }
            }
        }

        // model.Bullet vs model.Enemy
        for (GameObject b : objects) {
            if (b instanceof Arrow) {
                for (GameObject e : objects) {
                    if (e instanceof Enemy) {
                        // 弾も敵も生きていて、かつ衝突したら
                        if (!b.isDead() && !e.isDead() && b.getBounds().intersects(e.getBounds())) {
                            b.setDead(true); // 弾消滅
                            e.setDead(true); // 敵消滅
                            score += 10; // スコアを増加する
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
}
