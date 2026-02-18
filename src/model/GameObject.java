package model;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

/**
 * GameObject (Abstract Base Class)
 *
 * This is the root of the entire object hierarchy in the game.
 * Everything that appears on the screen (Player, Enemies, Projectiles) inherits from this class.
 *
 * Responsibilities:
 * - Position (x, y) and Dimensions (width, height).
 * - Lifecycle state (isDead flag).
 * - Object Movement (move method)
 * - Rendering interface (draw method).
 * - Collision boundaries (getBounds/getShape).
 */
public abstract class GameObject {
    protected int x, y;            // Screen coordinates
    protected int width, height;   // Object dimensions
    protected boolean isDead = false; // If true, the GameModel will remove this object from the list
    protected BufferedImage image; // The sprite texture

    /**
     * Constructor for a generic game object.
     * @param x Initial X position
     * @param y Initial Y position
     * @param w Width
     * @param h Height
     * @param image The visual sprite for this object
     */
    public GameObject(int x, int y, int w, int h, BufferedImage image) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
        this.image = image;
    }

    /**
     * Abstract method to update the object's logic (movement, animation, etc.).
     */
    public abstract void move();

    /**
     * Abstract method to render the object to the screen.
     * @param g The Graphics context from the GamePanel.
     */
    public abstract void draw(Graphics g);

    /**
     * Returns a precise Shape for collision detection.
     * Subclasses (like balls or circular enemies) can override this to return
     * Ellipses or Polygons for pixel-perfect collision.
     */
    public Shape getShape() {
        // Default shape is a rectangle
        return new Rectangle2D.Float(x, y, width, height);
    }

    public boolean isDead() {
        return isDead;
    }

    /**
     * Marks the object for deletion.
     * The Game Loop will remove it during the next update cycle.
     */
    public void setDead() {
        this.isDead = true;
    }

    public void setImage(BufferedImage image){
        this.image = image;
    }

    public int getY() {
        return y;
    }

    public int getX() {
        return x;
    }
}