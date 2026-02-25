package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;
import java.awt.image.BufferedImage;

public class Minotaur extends Minion{

    private int velX;
    private int lastY;
    private boolean isAngry = false;
    private boolean passedScreen = false;

    public Minotaur (int x, int y, GameModel model){
        super(x, y,
                GameConstants.MINOTAUR_WIDTH,
                GameConstants.MINOTAUR_HEIGHT,
                ResourceManager.minotaurImg,
                GameConstants.MINOTAUR_HP,
                GameConstants.MINOTAUR_SCORE_POINTS,
                model);
        velX = (Math.random() < 0.5) ? GameConstants.MINOTAUR_VELX : -GameConstants.MINOTAUR_VELX;
    }

    @Override
    public void move() {
        super.move();

        if (isAngry){
            y += GameConstants.MINOTAUR_VELY;
            if (y > GameConstants.HUD_HEIGHT + GameConstants.FIELD_HEIGHT){
                passedScreen = true;
                y = GameConstants.HUD_HEIGHT - height;
            }
            if (passedScreen && y >= lastY){
                y = lastY;
                passedScreen = false;
                isAngry = false;
                image = ResourceManager.minotaurImg;
            }
        } else {
            x += velX;

            // check right bounce
            if (x > GameConstants.WINDOW_WIDTH - width){
                x = GameConstants.WINDOW_WIDTH - width;
                velX *= -1;
                y += height / 2;
            }

            // check left bounce
            if (x < 0) {
                x = 0;
                velX *= -1;
                y += height / 2;
            }

            lastY = y;

            // check if minotaur is on top of player
            int playerCentralX = model.getPlayer().getX() + GameConstants.PLAYER_WIDTH / 2;
            int minotaurCentralX = x + width / 2;
            if( Math.abs(minotaurCentralX - playerCentralX) < 6){
                isAngry = true;
                image = ResourceManager.minotaurImg2;
            }
        }

    }

    @Override
    public void draw(Graphics g) {
        BufferedImage imgToDraw;

        imgToDraw = (flashTimer > 0) ? ResourceManager.minotaurHitImg : image;

        if (imgToDraw != null) {
            if(velX > 0){
                // Moving Right
                g.drawImage(imgToDraw, x, y, width, height, null);
            } else {
                // Moving Left
                g.drawImage(imgToDraw, x + width, y, -width, height, null);
            }
        } else {
            // Fallback
            g.setColor(Color.BLACK);
            g.fillRect(x, y, width, height);
        }
    }

    /**
     * Override getShape to provide a Circular Hitbox.
     */
    @Override
    public Shape getShape() {
        return new Ellipse2D.Float(x, y, width, height);
    }

}
