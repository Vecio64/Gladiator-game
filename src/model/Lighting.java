package model;

import view.ResourceManager;
import java.awt.*;
import java.awt.geom.Ellipse2D;

/**
 * Lighting Class
 *
 * A BossProjectile used by Zeus (Boss) or the Player (Ability 2).
 * It travels vertically at high speed.
 *
 * Key Features:
 * - Power Level 3 (Ultimate).
 * - Penetrating: Hits multiple targets.
 */
public class Lighting extends BossProjectile {

    private int velY; // Vertical velocity

    /**
     * Constructor for Lighting Bolt.
     * @param summonerX X position of the entity creating the lighting.
     * @param summonerY Y position of the entity creating the lighting.
     * @param ZeusVelX Used to determine spawn offset relative to Zeus's movement. (only if Zeus is the summoner)
     * @param isSecondPhase If true Lighting is faster and blue (only if Zeus is the summoner)
     * @param friendly If true, belongs to Player; else belongs to Zeus.
     * @param ability2Active Flag to check if this is part of Zeus's Ability2. (only if Zeus is the summoner)
     */
    public Lighting(int summonerX, int summonerY, int ZeusVelX, boolean isSecondPhase, boolean friendly, boolean ability2Active) {
        super(0, 0,
                GameConstants.LIGHTING_WIDTH,
                GameConstants.LIGHTING_HEIGHT,
                isSecondPhase ? ResourceManager.lightingImg2 : ResourceManager.lightingImg,
                friendly ? Alignment.PLAYER : Alignment.ENEMY,
                3,
                1);

        this.isPlayerProjectile = friendly;
        this.isPenetrating = true;

        // Set Speed based on Phase
        velY = (isSecondPhase) ? GameConstants.LIGHTING_SPEED2 : GameConstants.LIGHTING_SPEED1;

        // --- Spawn & Direction Logic ---
        if(isPlayerProjectile){
            // PLAYER: Move Up
            velY = -velY;
            maxHP = GameConstants.LIGHTING_HP;
            currentHP = maxHP;

            // Center on Player
            this.x = summonerX + (GameConstants.PLAYER_WIDTH - width) / 2;
            this.y = summonerY - GameConstants.PLAYER_HEIGHT;
        } else {
            // ZEUS: Move Down
            if (!ability2Active){
                // Standard attack: Center on Zeus
                this.x = summonerX + (GameConstants.ZEUS_WIDTH - width) / 2;
            } else {
                // Zeus's Ability2 : Offset spawn based on movement direction
                this.x = (ZeusVelX > 0) ? summonerX : summonerX + GameConstants.ZEUS_WIDTH - width;
            }
            this.y = summonerY + height;
        }
    }

    @Override
    public void move() {
        y += velY;

        // Despawn if off-screen (Top or Bottom)
        if (y > GameConstants.HUD_HEIGHT + GameConstants.FIELD_HEIGHT || // BOTTOM
                y < GameConstants.HUD_HEIGHT - height) { // TOP
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
}