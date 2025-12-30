package view;

import model.*;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;


// --- Mainクラス (ViewとControllerを兼ねる簡易版) ---
public class GamePanel extends JPanel implements KeyListener {
    private GameModel model;
    private Timer timer;

    //ボタンが押されているかを判断する変数
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private long startTime; // ゲーム開始時刻
    private long endTime; // ゲーム終了時刻

    private final int HUD_HEIGHT = 50;

    public GamePanel() {
        model = new GameModel(); // 初期状態はTITLE

        this.setPreferredSize(new Dimension(GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        timer = new Timer(1000/GameConstants.FPS, e -> {
            GameState previousState = model.getState();
            model.update();
            GameState currentState = model.getState();
            if (previousState == GameState.PLAYING && currentState == GameState.GAMEOVER) {
                endTime = System.currentTimeMillis();
            }
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
        // 1. まずゲームオブジェクトを描画
        for (GameObject obj : model.getObjects()) {
            obj.draw(g);
        }

        // 2. 画面上部に背景色とは別の色のバーを描画 (HUD背景)
        g.setColor(new Color(50, 50, 80)); // 紺色っぽいグレー
        g.fillRect(0, 0, GameConstants.SCREEN_WIDTH, HUD_HEIGHT);
        
        // 境界線（オプション）
        g.setColor(Color.WHITE);
        g.drawLine(0, HUD_HEIGHT, GameConstants.SCREEN_WIDTH, HUD_HEIGHT);

        // 3. 各種ステータスの表示設定
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        
        // Y座標のベース位置（バーの真ん中あたり）
        int textY = 32; 

        // [SCORE表示] (左側)
        g.drawString("SCORE: " + model.getScore(), 20, textY);

        // [TIME表示] (中央)
        long currentSeconds;
        if (model.getState() == GameState.PLAYING) {
            // プレイ中は「現在時刻 - 開始時刻」
            currentSeconds = (System.currentTimeMillis() - startTime) / 1000;
        } else {
            // ゲームオーバー時などは「終了時刻 - 開始時刻」で固定
            if (endTime != 0) {
                currentSeconds = (endTime - startTime) / 1000;
            } else {
                currentSeconds = 0;
            }
        }
        String timeStr = "TIME: " + currentSeconds;
        g.drawString(timeStr, GameConstants.SCREEN_WIDTH / 2 - 40, textY);

        // [LIFE表示] (右側)
        // 実装がない場合は仮置きで "LIFE: 3" と表示するか、下のコメントアウトを外して修正してください
        // int lives = 3; // model.getLives(); ← Modelにメソッドを追加したらこう書く
        // String lifeStr = "LIFE: " + lives;
        // g.drawString(lifeStr, GameConstants.SCREEN_WIDTH - 120, textY);
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
        g.fillRect(0, 0, GameConstants.SCREEN_WIDTH, GameConstants.SCREEN_HEIGHT);

        //Game OVER
        g.setColor(Color.RED);
        g.setFont(new Font("Arial", Font.BOLD, 50));
        g.drawString("GAME OVER", 140, 250);

        // SCORE
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 30));
        String scoreMsg = "Final Score: " + model.getScore();
        g.drawString(scoreMsg, 180, 300);

        // Final Time
        long finalSeconds = (endTime - startTime) / 1000;
        g.drawString("Final Time: " + finalSeconds, 180, 340);

        // CONTINUE QUIT
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 20));
        g.drawString("[C] Continue", 230, 420);
        g.drawString("[Q] Quit", 250, 450);
    }

    // --- キー操作 (Controller) ---
    //キー操作においてボタンの押下状態に合わせて動きを決める
    private void updatePlayerVelocity() {
        Player p = model.getPlayer();

        int vx = 0;
        int vy = 0;

        if (leftPressed && !rightPressed) vx = -1;
        if (rightPressed && !leftPressed) vx = 1;
        if (upPressed && !downPressed) vy = -1;
        if (downPressed && !upPressed) vy = 1;

        p.setVelX(vx);
        p.setVelY(vy);
    }

    //ゲームオーバーした時にリセットする
    private void resetKeyState() {
        leftPressed = false;
        rightPressed = false;
        upPressed = false;
        downPressed = false;

        Player p = model.getPlayer();
        if (p != null) {
            p.setVelX(0);
            p.setVelY(0);
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        GameState state = model.getState();

        // タイトル画面の操作
        if (state == GameState.TITLE) {
            if (key == KeyEvent.VK_SPACE) {
                model.initGame(); // ゲーム開始！
                startTime = System.currentTimeMillis();
                endTime = 0;
            }
        }
        
        // プレイ中の操作
        else if (state == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) leftPressed = true;
            if (key == KeyEvent.VK_RIGHT) rightPressed = true;
            if (key == KeyEvent.VK_UP) upPressed = true;
            if (key == KeyEvent.VK_DOWN) downPressed = true;

            if (key == KeyEvent.VK_SPACE) {
                model.setFiring(true);
            }

            updatePlayerVelocity();
        }
        // ゲームオーバー時の操作
        else if (state == GameState.GAMEOVER) {
            if (key == KeyEvent.VK_C) {
                model.initGame(); // リトライ
                resetKeyState();
                startTime = System.currentTimeMillis();
                endTime = 0;
            } else if (key == KeyEvent.VK_Q) {
                System.exit(0);   // アプリ終了
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        if (model.getState() == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) leftPressed = false;
            if (key == KeyEvent.VK_RIGHT) rightPressed = false;
            if (key == KeyEvent.VK_UP) upPressed = false;
            if (key == KeyEvent.VK_DOWN) downPressed = false;
            
            if (key == KeyEvent.VK_SPACE) {
                model.setFiring(false);
            }

            updatePlayerVelocity();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}