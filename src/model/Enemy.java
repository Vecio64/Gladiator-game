package model;

import view.ResourceManager; // Import the manager
import java.awt.*;
import java.awt.image.BufferedImage;

// --- Enemyクラス (敵) ---
class Enemy extends GameObject {

    private BufferedImage image;

    private int velX;
    private int velY;
    private int hp;

    private int flashTimer = 0;

    public Enemy(int x, int y) {
        super(x, y, GameConstants.ENEMY_WIDTH, GameConstants.ENEMY_HEIGHT); // 30x30の四角
        this.image = ResourceManager.enemyImg;

        // --- SETUP MOVEMENT ---
        // Vertical speed: Slow
        this.velY = GameConstants.ENEMY_YSPEED;

        // Horizontal speed: Fast
        this.velX = GameConstants.ENEMY_XSPEED;

        // Randomize initial direction (50% chance left or right)
        // Math.random() returns 0.0 to 1.0
        if (Math.random() < 0.5) {
            this.velX = -this.velX;
        }

        //Set HP
        this.hp = GameConstants.ENEMY_HP;
    }

    public void takeDamage(int dmg){
        if(isDead) return;

        this.hp -=dmg;

        this.flashTimer = GameConstants.FLASH_TIMER;

        if(this.hp <= 0){
            this.isDead = true;
            GameModel.addScore(GameConstants.ENEMY_SCORE_POINTS);
        }
    }

    @Override
    public void move() {
        // Update positions
        x += velX;
        y += velY;

        // --- BOUNCE LOGIC ---

        // Check Left Border
        if (x < 0) {
            x = 0;          // Fix position to avoid sticking
            velX = -velX;   // Reverse direction (become positive)
        }

        // Check Right Border
        if (x > GameConstants.FIELD_WIDTH - width) {
            x = GameConstants.FIELD_WIDTH - width; // Fix position
            velX = -velX;   // Reverse direction (become negative)
        }

        // Check Left Border
        if (y < GameConstants.HUD_HEIGHT) {
            y = GameConstants.HUD_HEIGHT;          // Fix position to avoid sticking
            velY = -velY;   // Reverse direction (become positive)
        }

        // Check Right Border
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT - height) {
            y = GameConstants.FIELD_HEIGHT - height; // Fix position
            velY = -velY;   // Reverse direction (become negative)
        }

        if(this.flashTimer > 0){
            this.flashTimer--;
        }

    }
    @Override
    public void draw(Graphics g) {
        //for decide which immage to use
        BufferedImage imgToDraw;

        if(flashTimer > 0) {
            // ダメージを受けたばかりならばピカピカする(FLASH)
            imgToDraw = ResourceManager.enemyHitImg;
        }
        else {
            imgToDraw = image;
        }


        if (image != null) {
            // Check direction to flip image
            if (velX < 0) {
                // Moving RIGHT: Draw normal
                // (Assuming the original sprite faces Right)
                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                // Moving LEFT: Draw flipped (Mirror effect)
                // We start at (x + width) and draw with negative width
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {
            // Fallback (Red square)
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }
}



