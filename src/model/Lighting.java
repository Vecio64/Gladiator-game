package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;

public class Lighting extends BossProjectile {

    private int velY;
    public Lighting(int x, int y, int ZeusSpeedX, boolean isSecondPhase, boolean friendly, boolean ability2Started) {
        super(0,
                0,
                GameConstants.LIGHTING_WIDTH,
                GameConstants.LIGHTING_HEIGHT,
                isSecondPhase ? ResourceManager.lightingImg2 : ResourceManager.lightingImg,
                friendly ? Alignment.PLAYER : Alignment.ENEMY,
                3,
                GameConstants.LIGHTING_DAMAGE);

        this.isPlayerProjectile = friendly;
        this.isPenetrating = true;

        if (isSecondPhase) {
            velY = GameConstants.LIGHTING_SPEED2;
        } else {
            velY = GameConstants.LIGHTING_SPEED1;
        }

        if(isPlayerProjectile){
            velY = -velY;
            maxHP = GameConstants.LIGHTING_HP;
            currentHP = maxHP;
            // set position for player
            this.x = x + (GameConstants.PLAYER_WIDTH - width) / 2;
            this.y = y - GameConstants.PLAYER_HEIGHT;
        } else {
            if (!ability2Started){

            // set position for Zeus
            this.x = (ZeusSpeedX > 0) ? x + GameConstants.ZEUS_WIDTH - width : x + width;
            } else {
                this.x = (ZeusSpeedX > 0) ? x : x + GameConstants.ZEUS_WIDTH - width;
            }
            this.y = y + height;
        }
    }

    @Override
    public void move() {
        y += velY;

        if (y > GameConstants.HUD_HEIGHT + GameConstants.FIELD_HEIGHT || y < GameConstants.HUD_HEIGHT - height) {
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