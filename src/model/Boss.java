package model;

import java.awt.*;

public abstract class Boss extends HostileEntity {

    public Boss(int x, int y, int w, int h, int hp, int scorePoints) {
        super(x, y, w, h, hp, scorePoints);
    }
}