package model;

import java.awt.*;

public abstract class Boss extends HostileEntity {

    protected GameModel model; // Reference to GameModel to spawn projectiles


    public Boss(int x, int y, int w, int h, int hp, int scorePoints, GameModel model) {
        super(x, y, w, h, hp, scorePoints);
        this.model = model;
    }


    @Override
    public void takeDamage(int dmg) {
        super.takeDamage(dmg);

        if (this.isDead){
            model.bossDefeated();
        }

    }
}