package model;

import java.awt.*;
import java.awt.geom.Rectangle2D;

// --- 1. キャラクターの親クラス ---
public abstract class GameObject {
    protected int x, y;
    protected int width, height;
    protected boolean isDead = false; // trueになったら消える

    public GameObject(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public abstract void move();

    public abstract void draw(Graphics g);

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public Shape getShape() {
        return new Rectangle2D.Float(x, y, width, height);
    }

    public boolean isDead() {
        return isDead;
    }

    public void setDead(boolean dead) {
        this.isDead = dead;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}
