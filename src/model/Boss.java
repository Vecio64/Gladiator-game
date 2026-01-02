package model;

import java.awt.*;

public abstract class Boss extends GameObject {

    protected int hp;
    protected int maxHp;
    protected int flashTimer = 0;

    public Boss(int x, int y, int w, int h, int hp) {
        super(x, y, w, h);
        this.maxHp = hp;
        this.hp = hp;
    }

    public void takeDamage(int dmg) {
        if (isDead) return;

        this.hp -= dmg;
        this.flashTimer = GameConstants.FLASH_TIMER;

        System.out.println("Boss HP: " + hp); // Debug

        if (this.hp <= 0) {
            this.isDead = true;
            GameModel.addScore(1000);
        }
    }
}