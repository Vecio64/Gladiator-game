import java.awt.*;
import java.util.ArrayList;
import java.util.Random;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;

//TEST

// ゲームの状態を定義
enum GameState {
    TITLE, PLAYING, GAMEOVER
}

// --- 1. キャラクターの親クラス ---
abstract class GameObject {
    protected int x, y;
    protected int width, height;
    protected boolean isDead = false; // trueになったら消える

    public GameObject(int x, int y, int w, int h) {
        this.x = x; this.y = y;
        this.width = w; this.height = h;
    }

    public abstract void move();
    public abstract void draw(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public boolean isDead() {return isDead;}
    public void setDead(boolean dead) {this.isDead = dead;}
    public int getY() {return y;}
    public int getX() {return x;}
}

// --- Playerクラス (自分) ---
class Player extends GameObject {
    private int velX = 0; // 横の移動速度
    private int velY = 0; // 縦の移動速度
    private int speed = 5;

    public Player(int x, int y) {
        super(x, y, 40, 40); // 40x40の四角
    }

    @Override
    public void move() {
        x += velX;
        y += velY;

        // 画面からはみ出さないように制限
        if (x < 0) x = 0;
        if (x > 560) x = 560;
        if (y < 0) y = 0;
        if (y > 520) y = 520;
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.BLUE);
        g.fillRect(x, y, width, height);
    }

    // Controllerから呼ばれるメソッド
    public void setVelX(int vx) { this.velX = vx * speed; }
    public void setVelY(int vy) { this.velY = vy * speed; }
}

// --- Enemyクラス (敵) ---
class Enemy extends GameObject {
    public Enemy(int x, int y) {
        super(x, y, 30, 30); // 30x30の四角
    }

    @Override
    public void move() {
        y += 3; // 下にスピード3で落ちる

        // 画面外（下）に出たら死亡扱い
        if (y > 600) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }
}

// --- Bulletクラス (弾) ---
class Bullet extends GameObject {
    public Bullet(int x, int y) {
        super(x, y, 10, 20);
    }

    @Override
    public void move() {
        y -= 10; // 上にスピード10で飛ぶ

        // 画面外（上）に出たら死亡扱い
        if (y < -height) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        g.setColor(Color.YELLOW);
        g.fillRect(x, y, width, height);
    }
}


// --- GameModel (M) ---
class GameModel {
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
        player = new Player(280, 500);
        objects.add(player);

        isFiring = false;
        shotTimer = 0;
        state = GameState.PLAYING;
        score = 0; //スコアをリセット
    }

    public void setFiring(boolean firing) {
        this.isFiring = firing;
    }

    public GameState getState() {return state;}

    public void setState(GameState s) {this.state = s;}

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
            Bullet b = new Bullet(player.getX() + 15, player.getY() - 20);
            newObjectsBuffer.add(b);
        }
    }

    // 敵を出現させる
    public void spawnEnemy() {
        if (rand.nextInt(100) < 3) { // 3%の確率で出現（適当な頻度）
            int randomX = rand.nextInt(571);
            Enemy e = new Enemy(randomX, -30);
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

        // Bullet vs Enemy
        for (GameObject b : objects) {
            if (b instanceof Bullet) {
                for (GameObject e : objects) {
                    if (e instanceof Enemy) {
                        // 弾も敵も生きていて、かつ衝突したら
                        if (!b.isDead() && !e.isDead() && b.getBounds().intersects(e.getBounds())) {
                            b.setDead(true); // 弾消滅
                            e.setDead(true); // 敵消滅
                            System.out.println("Hit!");
                            score += 10; // スコアを増加する
                        }
                    }
                }
            }
        }
    }

    //SETTERS & GETTERS
    public ArrayList<GameObject> getObjects() {return objects;}
    public Player getPlayer() {return player;}
    public boolean isGameOver() {return isGameOver;}
    public int getScore() { return score; }
}


// --- Mainクラス (ViewとControllerを兼ねる簡易版) ---
public class GladiatorGame extends JPanel implements KeyListener {
    private GameModel model;
    private Timer timer;

    public GladiatorGame() {
        model = new GameModel(); // 初期状態はTITLE

        this.setPreferredSize(new Dimension(600, 600));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        timer = new Timer(16, e -> {
            model.update();
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 状態によって描画を切り替える
        GameState state = model.getState();

        if (state == GameState.TITLE) {
            drawTitleScreen(g);
        } else if (state == GameState.PLAYING) {
            drawGameScreen(g);
        } else if (state == GameState.GAMEOVER) {
            drawGameScreen(g); // ゲームオーバー時も背景にゲーム画面を残す
            drawGameOverScreen(g);
        }
    }

    // ゲーム本編の描画
    private void drawGameScreen(Graphics g) {
        for (GameObject obj : model.getObjects()) {
            obj.draw(g);
        }

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("SCORE: " + model.getScore(), 15, 30);

    }

    // タイトル画面
    private void drawTitleScreen(Graphics g) {
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 40));
        String title = "SHOOTING GAME";
        String msg = "Press SPACE to Start";

        // 中央寄せの計算（簡易的）
        g.drawString(title, 130, 250);
        g.setFont(new Font("Arial", Font.PLAIN, 20));
        g.drawString(msg, 190, 350);
    }

    // ゲームオーバー画面
    private void drawGameOverScreen(Graphics g) {
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, 600, 600);

        //Game OVER
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("GAME OVER", 140, 250);

        // SCORE
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String scoreMsg = "Final Score: " + model.getScore();
        g.drawString(scoreMsg, 180, 300);

        // CONTINUE QUIT
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("[C] Continue", 230, 350);
        g.drawString("[Q] Quit", 250, 400);
    }

    // --- キー操作 (Controller) ---
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        GameState state = model.getState();

        // タイトル画面の操作
        if (state == GameState.TITLE) {
            if (key == KeyEvent.VK_SPACE) {
                model.initGame(); // ゲーム開始！
            }
        }
        // プレイ中の操作
        else if (state == GameState.PLAYING) {
            Player p = model.getPlayer();
            if (key == KeyEvent.VK_LEFT) p.setVelX(-1);
            if (key == KeyEvent.VK_RIGHT) p.setVelX(1);
            if (key == KeyEvent.VK_UP) p.setVelY(-1);
            if (key == KeyEvent.VK_DOWN) p.setVelY(1);

            if (key == KeyEvent.VK_SPACE) {
                model.setFiring(true);
            }
        }
        // ゲームオーバー時の操作
        else if (state == GameState.GAMEOVER) {
            if (key == KeyEvent.VK_C) {
                model.initGame(); // リトライ
            } else if (key == KeyEvent.VK_Q) {
                System.exit(0);   // アプリ終了
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (model.getState() == GameState.PLAYING) {
            Player p = model.getPlayer();
            if (key == KeyEvent.VK_LEFT || key == KeyEvent.VK_RIGHT) p.setVelX(0);
            if (key == KeyEvent.VK_UP || key == KeyEvent.VK_DOWN) p.setVelY(0);

            if (key == KeyEvent.VK_SPACE) {
                model.setFiring(false);
            }
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("Shooting Game MVC");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(new GladiatorGame());
        frame.pack();
        frame.setLocationRelativeTo(null); // ウィンドウを画面中央に
        frame.setVisible(true);
    }
}