package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Sun extends HostileEntity {

    private BufferedImage image;
    private double preciseX, preciseY;
    private double velX, velY;
    private boolean isfriendly;
    private int damage = GameConstants.SUN_DAMAGE;

    public Sun(int x, int y, int ApolloSpeedX, boolean isSecondPhase, boolean friendly) {
        // Call parent constructor
        // HP = 1 (doesn't matter), Score = 0
        super(0, 0, GameConstants.SUN_WIDTH, GameConstants.SUN_HEIGHT, 1, 0);

        // IMPORTANT: Makes the sun immune to arrows
        this.isInvincible = true;
        this.isfriendly = friendly;

        this.velX = 1;
        this.velY = 1;

        int startX;
        double angleRadians;
        double currentSpeed;

        if (isSecondPhase) {
            this.image = ResourceManager.sunImg2;
            currentSpeed = GameConstants.SUN_SPEED2;
        } else {
            this.image = ResourceManager.sunImg;
            currentSpeed = GameConstants.SUN_SPEED1;
        }

        if (!friendly){
            // sun comes from apollo
            startX = (ApolloSpeedX > 0) ? x + GameConstants.APOLLO_WIDTH : x - GameConstants.SUN_WIDTH;
            this.x = startX;
            this.y = y + GameConstants.SUN_HEIGHT / 2;
            this.preciseX = startX;
            this.preciseY = y + GameConstants.SUN_HEIGHT / 2;
            // --- Phase Logic ---
            // --- Trajectory Calculation ---
            double minAngle = 20.0;
            double maxAngle = 70;
            angleRadians = Math.toRadians(minAngle + Math.random() * (maxAngle - minAngle));
        } else {
            // sun comes from ability
            startX = x - (GameConstants.SUN_WIDTH - GameConstants.PLAYER_WIDTH)/2;
            // 2. right boarder check
            if (startX > GameConstants.WINDOW_WIDTH - GameConstants.SUN_WIDTH) {
                startX = GameConstants.WINDOW_WIDTH - GameConstants.SUN_WIDTH;
            }

            // 3. left boarder check
            if (startX < 0) {
                startX = 0;
            }
            this.x = startX;
            this.y = y - GameConstants.SUN_HEIGHT;
            this.preciseX = startX;
            this.preciseY = y - GameConstants.SUN_HEIGHT;
            angleRadians = Math.toRadians(360-30);
            this.velX = (Math.random() < 0.5) ? 1 : -1;
        }

        this.velX *= currentSpeed * Math.cos(angleRadians);
        this.velY *= currentSpeed * Math.sin(angleRadians);

        if (ApolloSpeedX < 0) {
            this.velX = -this.velX;
        }
    }

    @Override
    public void move() {
        preciseX += velX;
        preciseY += velY;

        x = (int) preciseX;
        y = (int) preciseY;

        // Wall Bounce
        if (x < 0) {
            x = 0;
            velX = -velX;
        }
        if (x > GameConstants.WINDOW_WIDTH - width) {
            x = GameConstants.WINDOW_WIDTH - width;
            velX = -velX;
        }

        // Despawn
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT || y < GameConstants.HUD_HEIGHT - height) {
            isDead = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        if (image != null) {
            g.drawImage(image, x, y, width, height, null);
        } else {
            g.setColor(Color.YELLOW);
            g.fillOval(x, y, width, height);
        }
    }

    @Override
    public Shape getShape() {
        return new Ellipse2D.Float(x, y, width, height);
    }

    // GETTERS
    public boolean getIsFriendly() { return this.isfriendly; }

    public int getDamage() {
        return damage;
    }
}