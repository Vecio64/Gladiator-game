package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Sun extends BossProjectile {

    private double preciseX, preciseY;
    private double velX, velY;

    private double initialSize;

    public Sun(int summonerX, int summonerY, int ApolloSpeedX, boolean isSecondPhase, boolean friendly) {
        // Call parent constructor
        // HP = 1 (doesn't matter), Score = 0
        super(0,
                0,
                GameConstants.SUN_WIDTH,
                GameConstants.SUN_HEIGHT,
                isSecondPhase ? ResourceManager.sunImg2 : ResourceManager.sunImg,
                friendly ? Alignment.PLAYER : Alignment.ENEMY,
                3,
                GameConstants.SUN_DAMAGE);

        this.isPlayerProjectile = friendly;
        this.isPenetrating = true;

        if (isPlayerProjectile) {
            maxHP = GameConstants.SUN_HP;
            currentHP = maxHP;
        }

        this.initialSize = GameConstants.SUN_WIDTH;

        velX = 1;
        velY = 1;
        double currentSpeed;
        double angleRadians;

        if (isSecondPhase) {
            currentSpeed = GameConstants.SUN_SPEED2;
        } else {
            currentSpeed = GameConstants.SUN_SPEED1;
        }

        if (!friendly){
            // sun comes from apollo
            preciseX = (ApolloSpeedX > 0) ? (summonerX + GameConstants.APOLLO_WIDTH + width / 2) : (summonerX - width / 2);
            preciseY = summonerY + height;
            x = (int) preciseX;
            y = (int) preciseY;
            // --- Phase Logic ---
            // --- Trajectory Calculation ---
            double minAngle = 20.0;
            double maxAngle = 70;
            angleRadians = Math.toRadians(minAngle + Math.random() * (maxAngle - minAngle));
        } else {
            // sun comes from ability
            preciseX = summonerX;
            preciseY = summonerY - height / 2;

            // 2. right boarder check
            if (preciseX > GameConstants.WINDOW_WIDTH - width / 2) {
                preciseX = GameConstants.WINDOW_WIDTH - width / 2;
            }

            // 3. left boarder check
            if (preciseX < width / 2) {
                preciseX = width / 2;
            }

            x = (int) preciseX;
            y = (int) preciseY;
            angleRadians = Math.toRadians(360-30);
            velX = (Math.random() < 0.5) ? 1 : -1;
        }

        velX *= currentSpeed * Math.cos(angleRadians);
        velY *= currentSpeed * Math.sin(angleRadians);

        if (ApolloSpeedX < 0) {
            velX = -velX;
        }
    }

    @Override
    public void move() {
        preciseX += velX;
        preciseY += velY;

        x = (int) preciseX;
        y = (int) preciseY;

        double currentRadius = getCurrentSize() / 2.0;

        // Wall Bounce
        if (x - currentRadius < 0) {
            x = (int)currentRadius;
            preciseX = x;
            velX = -velX;
        }
        if (x + currentRadius > GameConstants.WINDOW_WIDTH) {
            x = (int)(GameConstants.WINDOW_WIDTH - currentRadius);
            preciseX = x;
            velX = -velX;
        }

        // Despawn
        if (y - currentRadius > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT ||
                y + currentRadius < GameConstants.HUD_HEIGHT) {
            isDead = true;
        }
    }

    // Helper to calculate size based on HP
    private double getCurrentSize() {
        if (!isPlayerProjectile) return initialSize;

        // Calculate ratio: currentHP / maxHP
        double ratio = (double) currentHP / maxHP;
        return initialSize * ratio;
    }

    @Override
    public void draw(Graphics g) {
        double size = getCurrentSize();
        double radius = size / 2.0;

        int drawX = (int) (x - radius);
        int drawY = (int) (y - radius);
        int drawSize = (int) size;

        if (image != null) {
            g.drawImage(image, drawX, drawY, drawSize, drawSize, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, width, height);
        }
    }

    @Override
    public Shape getShape() {
        double size = getCurrentSize();
        double radius = size / 2.0;
        return new Ellipse2D.Float((float)(x - radius), (float)(y - radius), (float)size, (float)size);
    }

}