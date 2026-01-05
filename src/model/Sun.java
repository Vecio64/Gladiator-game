package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Sun extends HostileEntity {

    private BufferedImage image;
    private double preciseX, preciseY;
    private double velX, velY;

    public Sun(int ApolloX, int ApolloY, int ApolloSpeedX, boolean isSecondPhase) {
        // Call parent constructor
        // HP = 1 (doesn't matter), Score = 0
        super(0, 0, GameConstants.SUN_WIDTH, GameConstants.SUN_HEIGHT, 1, 0);

        // IMPORTANT: Makes the sun immune to arrows
        this.isInvincible = true;

        // --- Position Calculation ---
        int startX = (ApolloSpeedX > 0) ? ApolloX + GameConstants.APOLLO_WIDTH : ApolloX - GameConstants.SUN_WIDTH;

        // Update position in parent
        this.x = startX;
        this.y = ApolloY + GameConstants.SUN_HEIGHT / 2;
        this.preciseX = startX;
        this.preciseY = ApolloY + GameConstants.SUN_HEIGHT / 2;

        // --- Phase Logic ---
        double currentSpeed;
        if (isSecondPhase) {
            this.image = ResourceManager.sunImg2;
            currentSpeed = GameConstants.SUN_SPEED * 2;
        } else {
            this.image = ResourceManager.sunImg;
            currentSpeed = GameConstants.SUN_SPEED;
        }

        // --- Trajectory Calculation ---
        double minAngle = 20.0;
        double maxAngle = 70;
        double angleRadians = Math.toRadians(minAngle + Math.random() * (maxAngle - minAngle));

        this.velX = currentSpeed * Math.cos(angleRadians);
        this.velY = currentSpeed * Math.sin(angleRadians);

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
        if (x > GameConstants.FIELD_WIDTH - width) {
            x = GameConstants.FIELD_WIDTH - width;
            velX = -velX;
        }

        // Despawn
        if (y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT) {
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
}