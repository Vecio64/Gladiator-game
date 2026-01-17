package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Lighting extends HostileEntity{

    private BufferedImage image;
    private double preciseX, preciseY;
    private double velX, velY;
    private boolean isfriendly;
    private int damage = GameConstants.SUN_DAMAGE;

    public Lighting(int x, int y, int ZeusSpeedX, boolean friendly) {
        super(0, 0, GameConstants.LIGHTING_WIDTH, GameConstants.LIGHTING_HEIGHT, 1, 0);

        isInvincible = true;
        this.isfriendly = friendly;

        this.velX = 0;
        this.velY = 5;

        this.image = ResourceManager.lightingImg;


        if (!friendly) {
            // zeusが打つとき
            this.x = x;
            this.y = y;
            this.preciseX = x;
            this.preciseY = y;
        } else {
            // playerが打つとき
            this.x = x;
            this.y = y;
            this.preciseX = x;
            this.preciseY = y;
        }
    }

    @Override
    public void move() {
        preciseX += velX;
        preciseY += velY;

        x = (int) preciseX;
        y = (int) preciseY;

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

    public boolean getIsFriendly() {
        return this.isfriendly;
    }

    public int getDamage() {
        return damage;
    }
}
