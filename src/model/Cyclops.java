package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Cyclops extends Minion{

    private double preciseY;
    private double velY;
    private int movementTimer;
    private int attackTimer;
    private Boulder myBoulder = null;
    private boolean reachedMidScreen;
    private boolean wingsClosed = false;

    public Cyclops (int x, int y, GameModel model){
        super(x, y,
                GameConstants.CYCLOPS_WIDTH,
                GameConstants.CYCLOPS_HEIGHT,
                ResourceManager.cyclopsImg,
                GameConstants.CYCLOPS_HP,
                GameConstants.CYCLOPS_SCORE_POINTS,
                model);
        this.preciseY = y;
        this.velY = GameConstants.CYCLOPS_YSPEED;
        this.movementTimer = GameConstants.CYCLOPS_MOVEMENT_TIMER;
        this.attackTimer = GameConstants.CYCLOPS_ATTACK_TIMER;
        this.reachedMidScreen = false;
    }

    @Override
    public void move() {
        super.move();
        if (movementTimer <= 0){
            if (velY > 0){
                // Go up
                velY = GameConstants.CYCLOPS_YSPEED / -4;
                movementTimer = GameConstants.CYCLOPS_MOVEMENT_TIMER * 3 / 2;
                wingsClosed = true; // STATE: closed wings
            }
            else {
                // go down
                wingsClosed = false; // STATE: opened wings

                if (!reachedMidScreen) {
                    velY = GameConstants.CYCLOPS_YSPEED;
                    movementTimer = GameConstants.CYCLOPS_MOVEMENT_TIMER;
                } else {
                    velY = GameConstants.CYCLOPS_YSPEED / 4;
                    movementTimer = GameConstants.CYCLOPS_MOVEMENT_TIMER * 3 / 2;
                }
            }
            this.image = wingsClosed ? ResourceManager.cyclopsImg2 : ResourceManager.cyclopsImg;
        }
        preciseY += velY;
        y = (int) preciseY;

        if(y > (GameConstants.HUD_HEIGHT + GameConstants.FIELD_HEIGHT/2 - height)){
            reachedMidScreen = true;
        }

        // CHECK IF THERE IS BOULDER OR IF IT IS DEAD
        if (myBoulder == null || myBoulder.isDead()) {
            // REDUCE TIMER
            if(attackTimer > 0){
                attackTimer--;
            }
            // ONCE THE TIMER IS 0 SPAWN BOULDER AND RESET TIMER
            if (attackTimer == 0){
                throwBoulder();
                attackTimer = GameConstants.CYCLOPS_ATTACK_TIMER;
            }

        }
        movementTimer--;
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imgToDraw;

        if (image == ResourceManager.cyclopsImg){
            imgToDraw = (flashTimer > 0) ? ResourceManager.cyclopsHitImg : image;
        } else {
            imgToDraw = (flashTimer > 0) ? ResourceManager.cyclopsHitImg2 : image;
        }

        if (image != null) {
            g.drawImage(imgToDraw, x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }
    }

    private void throwBoulder(){
        Boulder b = new Boulder (x + (width - GameConstants.BOULDER_WIDTH)/2, y + height);
        this.myBoulder = b;
        model.spawnEnemyProjectile(b);
    }

}
