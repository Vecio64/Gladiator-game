package model;

import java.awt.*;
import java.awt.image.BufferedImage;

public abstract class Boss extends HostileEntity {

    public Boss(int x, int y, int w, int h, BufferedImage image, int hp, int scorePoints, GameModel model) {
        super(x, y, w, h, image, hp, scorePoints, model);
    }

    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);

        if (this.isDead){
            model.bossDefeated();
        }
    }
}