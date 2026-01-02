package model;

import view.ResourceManager; // Import the manager
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.Ellipse2D; // Import for circles and oval

public class Sun extends GameObject{


     BufferedImage image;

    private double preciseX;
    private double preciseY;
    public  double currentSpeed;

    // Set the x and y speeds
    private double velX;
    private double velY;



    Sun(int ApolloX, int ApolloY, int ApolloSpeedX, boolean isSecondPhase){
        int startX;
        if(ApolloSpeedX > 0){
            startX = ApolloX + GameConstants.APOLLO_WIDTH;
        } else {
            startX = ApolloX - GameConstants.SUN_WIDTH;

        }
        super(startX, ApolloY, GameConstants.SUN_WIDTH, GameConstants.SUN_HEIGHT);

        this.preciseX = startX;
        this.preciseY = ApolloY;

        if (isSecondPhase) {
            this.image = ResourceManager.sunImg2; // Usa immagine Rossa
            this.currentSpeed = GameConstants.SUN_SPEED * 2; // Velocità Doppia
        } else {
            this.image = ResourceManager.sunImg; // Usa immagine Normale
            this.currentSpeed = GameConstants.SUN_SPEED; // Velocità Normale
        }

        // set angle Range
        double minAngle = 20.0;
        double maxAngle = 90.0;

        // set random angle in Range
        double angleDegrees = minAngle + Math.random() * (maxAngle - minAngle);

        // Convert to Radiant
        double angleRadians = Math.toRadians(angleDegrees);

        // 4. Get Sin and Cos
        double cosValue = Math.cos(angleRadians);
        double sinValue = Math.sin(angleRadians);

        this.velX = currentSpeed * cosValue;
        this.velY = currentSpeed * sinValue;

        if (ApolloSpeedX < 0){
            this.velX = -this.velX;
        }


    }

    @Override
    public void move() {
        preciseX += velX;
        preciseY += velY;

        this.x = (int) preciseX;
        this.y = (int) preciseY;

        if(x < 0){
            x = 0;
            velX = -velX;
        }

        if(x > GameConstants.FIELD_WIDTH - width){
            x = GameConstants.FIELD_WIDTH - width;
            velX = -velX;
        }

        if(y > GameConstants.FIELD_HEIGHT + GameConstants.HUD_HEIGHT){
            isDead = true;
        }

    }

    @Override
    public void draw(Graphics g) {
        if(image!= null){
            g.drawImage(image, x, y, width, height, null);
        }
        else {
            // Fallback if image failed to load
            g.setColor(Color.YELLOW);
            g.fillRect(x, y, width, height);
        }
    }

    @Override
    public Shape getShape() {
        return new Ellipse2D.Float(x, y, width, height);
    }
}
