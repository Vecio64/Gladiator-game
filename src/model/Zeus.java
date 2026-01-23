package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;


public class Zeus extends Boss {


    private int speedX = GameConstants.ZEUS_SPEED;
    private boolean secondPhase = false; // Flag to track if the boss is in "Rage Mode"
    private int shootTimer;
    private int maxShootTimer;
    private int ability1Timer;
    private int ability2Timer;
    private int ability1Pause;
    private int maxAbility1Pause;
    private int ability1Position;
    private BufferedImage hitImg;
    boolean ability1Phase;
    boolean ability2Started;
    private int ability2Repetitions;

    public Zeus (GameModel model) {
        super ( (GameConstants.WINDOW_HEIGHT - GameConstants.ZEUS_WIDTH) / 2,
                GameConstants.HUD_HEIGHT,
                GameConstants.ZEUS_WIDTH,
                GameConstants.ZUES_HEIGHT,
                ResourceManager.zeusImg,
                GameConstants.ZEUS_HP,
                GameConstants.ZEUS_SCORE_POINTS,
                model
        );
        maxShootTimer = GameConstants.ZEUS_SHOOT_TIMER;
        resetShootTimer();
        maxAbility1Pause = GameConstants.ZEUS_ABILITY1_PAUSE;
        setAbility1Timer();
        ability1Position = 1;
        hitImg = ResourceManager.zeusHitImg;
        ability2Timer = GameConstants.ZEUS_ABILITY2_TIMER;
        ability2Started = false;
    }

    @Override
    public void move() {
        super.move();
        if (ability2Timer > 0 || ability1Phase){
            if (ability1Timer > 0) {
                // Update horizontal position
                x += speedX;

                // Bounce logic: If it hits the screen edges
                if (x <= 0 ) {
                    x = 0;
                    speedX = -speedX; // Reverse direction
                    ability1Timer--;
                } else if (x >= GameConstants.WINDOW_WIDTH - width) {
                    x = GameConstants.WINDOW_WIDTH - width;
                    speedX = -speedX; // Reverse direction
                    ability1Timer--;
                }
                if(shootTimer <= 0){
                    shootLighting();
                    resetShootTimer();
                }
                shootTimer--;
            } else if (ability1Pause <= 0){
                ability1();
            } else {
                ability1Pause--;
            }
        } else {
            if (!ability2Started) {
                if (Math.random() < 0.5) {
                    x = 0;
                    speedX = GameConstants.ZEUS_SPEED2;
                } else {
                    x = GameConstants.WINDOW_WIDTH - width;
                    speedX = -GameConstants.ZEUS_SPEED2;
                }
                Random random = new Random();
                ability2Repetitions = random.nextInt(3) + 1;
                ability2Started = true;
            }
            x += speedX;

            if (((x < (GameConstants.PLAYER_WIDTH - 20)) && (speedX < 0)) || (x > (GameConstants.WINDOW_WIDTH - (width + GameConstants.PLAYER_WIDTH)) && speedX > 0)){
            } else {
                shootLighting();
            }

            if (x <= 0 ) {
                x = 0;
                speedX = -speedX; // Reverse direction
                ability2Repetitions--;
            } else if (x >= GameConstants.WINDOW_WIDTH - width) {
                x = GameConstants.WINDOW_WIDTH - width;
                speedX = -speedX; // Reverse direction
                ability2Repetitions--;
            }
            if (ability2Repetitions == 0){
                ability2Timer = GameConstants.ZEUS_ABILITY2_TIMER;
                ability2Started = false;
                resetShootTimer();
            }

        }

        if (secondPhase && ability2Timer > 0){
            ability2Timer--;
        }

    }

    private void ability1() {
        if (ability1Position == 1){
            ability1Phase = true;
        }

        if (ability1Position == 5) {
            setAbility1Timer();
            ability1Position = 1;
            resetShootTimer();
            return;
        }


        if(speedX > 0) {
            x = ability1Position * (GameConstants.WINDOW_WIDTH-width)/4;
        } else {
            x = (4 - ability1Position) * (GameConstants.WINDOW_WIDTH-width)/4;
        }
        if (ability1Position % 2 == 0){
            y = GameConstants.HUD_HEIGHT;
        } else {
            y = GameConstants.HUD_HEIGHT + height / 3;
        }

        shootLighting();
        ability1Pause = maxAbility1Pause;
        ability1Position++;
    }

    private void shootLighting(){
        Lighting l = new Lighting(x, y, speedX, secondPhase, false, ability2Started);
        model.spawnEnemyProjectile(l);
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);

        if (hp <= maxHp / 2 && !secondPhase) {
            image = ResourceManager.zeusImg2;
            hitImg = ResourceManager.zeusHitImg2;
            speedX = (speedX > 0) ? GameConstants.ZEUS_SPEED2 : -GameConstants.ZEUS_SPEED2; // Double the movement speed
            secondPhase = true;
            maxShootTimer = GameConstants.ZEUS_SHOOT_TIMER2;
            maxAbility1Pause = GameConstants.ZEUS_ABILITY1_PAUSE2;
        }


    }

    private void setAbility1Timer(){
        Random random = new Random();
        ability1Timer = random.nextInt(4) + 1;;
        ability1Phase = false;
    }

    private void resetShootTimer(){
        shootTimer = maxShootTimer;
    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imgToDraw = (flashTimer > 0) ? hitImg : image;

        if (image != null) {

            if (speedX > 0) {

                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {

            g.setColor(Color.ORANGE);
            g.fillRect(x, y, width, height);

        }
    }


}
