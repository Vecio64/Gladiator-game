package view;

import model.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.awt.image.BufferedImage;

// --- Main Class (View and Controller simplified) ---
public class GamePanel extends JPanel implements KeyListener {
    private GameModel model;
    private Timer timer;

    // Variables to track button states
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    private long startTime; // Game start time
    private long endTime;   // Game end time

    public GamePanel() {
        model = new GameModel(); // Initial state is TITLE

        // Update preferred size to include the new BOTTOM_HUD_HEIGHT
        this.setPreferredSize(new Dimension(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(this);

        // Game Loop Timer
        timer = new Timer(1000/GameConstants.FPS, e -> {
            model.update();
            repaint();
        });
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Switch drawing based on game state
        GameState state = model.getState();

        if (state == GameState.TITLE) {
            drawTitleScreen(g);
        } else if (state == GameState.PLAYING || state == GameState.PAUSED || state == GameState.MESSAGE) {
            drawGameScreen(g);
            if (state == GameState.PAUSED){
                drawPauseScreen(g);
            }
            else if (state == GameState.MESSAGE) {
                drawMessageScreen(g);
            }

        } else if (state == GameState.GAMEOVER) {
            drawGameScreen(g); // Draw game screen in background
            drawGameOverScreen(g);
        }
    }

    // Helper method to set font easily
    private void setPixelFont(Graphics g, float size) {
        if (ResourceManager.pixelFont != null) {
            g.setFont(ResourceManager.pixelFont.deriveFont(size));
        } else {
            g.setFont(new Font("Arial", Font.BOLD, (int)size));
        }
    }



    // Main Game Drawing Method
    private void drawGameScreen(Graphics g) {

        // 1. Draw Background
        if (model.getBackground() != null) {
            model.getBackground().draw(g);
        } else {
            // Fallback: Black background if image is missing
            g.setColor(Color.BLACK);
            // Fill only the game field area
            g.fillRect(0, GameConstants.HUD_HEIGHT, GameConstants.WINDOW_WIDTH, GameConstants.FIELD_HEIGHT);
        }

        // 2. Draw Game Objects (Player, Enemies, Projectiles)
        for (GameObject obj : model.getObjects()) {
            // Invincibility flashing logic for Player
            if (obj instanceof Player && model.isInvincible()) {
                // Toggle visibility every 100ms
                if (System.currentTimeMillis() % 200 < 100) {
                    continue;
                }
            }
            obj.draw(g);
        }

        // 3. Draw Top HUD (Score, Stage, Lives)
        drawTopHUD(g);

        // 4. Draw Bottom HUD (Ability Slots) -> NEW!
        drawBottomHUD(g);
    }

    // Helper method to draw the Top HUD
    private void drawTopHUD(Graphics g) {
        // Draw dark background bar
        g.setColor(new Color(50, 50, 80));
        g.fillRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.HUD_HEIGHT);

        // Draw white separator line
        g.setColor(Color.WHITE);
        g.drawLine(0, GameConstants.HUD_HEIGHT, GameConstants.WINDOW_WIDTH, GameConstants.HUD_HEIGHT);

        // Font settings
        setPixelFont(g, 18f);
        int textY = 35;

        // A. Score
        g.drawString("SCORE:" + model.getScore(), 10, textY);

        // B. Stage (Centered)
        String stageText = model.getStageText();
        int stageX = (GameConstants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(stageText)) / 2;
        g.drawString(stageText, stageX, textY);

        // C. Hearts / Lives (Right aligned)
        int maxLives = GameConstants.PLAYER_MAX_LIVES;
        int currentLives = model.getLives();
        int heartSize = 32;
        int spacing = 8;
        int startX = GameConstants.WINDOW_WIDTH - 20 - (maxLives * (heartSize + spacing));
        int heartY = (GameConstants.HUD_HEIGHT - heartSize) / 2;

        for (int i = 0; i < maxLives; i++) {
            // Determine which icon to draw (Full or Empty)
            BufferedImage icon = (i < currentLives) ? ResourceManager.heartFullImg : ResourceManager.heartEmptyImg;

            if (icon != null) {
                g.drawImage(icon, startX + (i * (heartSize + spacing)), heartY, heartSize, heartSize, null);
            } else {
                // Fallback drawing if images are missing
                g.setColor(i < currentLives ? Color.RED : Color.GRAY);
                g.fillOval(startX + (i * (heartSize + spacing)), heartY, heartSize, heartSize);
            }
        }
    }

    // Helper method to draw the Bottom HUD (Ability Slots)
    // Helper method to draw the Bottom HUD (Ability Slots)
    private void drawBottomHUD(Graphics g) {
        // Calculate start Y position (below the game field)
        int startY = GameConstants.HUD_HEIGHT + GameConstants.FIELD_HEIGHT;
        int height = GameConstants.BOTTOM_HUD_HEIGHT;

        // 1. Background Bar
        g.setColor(new Color(50, 50, 80));
        g.fillRect(0, startY, GameConstants.WINDOW_WIDTH, height);

        // 2. Separator Line
        g.setColor(Color.WHITE);
        g.drawLine(0, startY, GameConstants.WINDOW_WIDTH, startY);

        // 3. Draw Slots
        int slotSize = 60;
        int gap = 40;
        int totalWidth = (3 * slotSize) + (2 * gap);
        int startX = (GameConstants.WINDOW_WIDTH - totalWidth) / 2;
        int slotY = startY + (height - slotSize) / 2 - 9;

        setPixelFont(g, 14f);

        for (int i = 0; i < 3; i++) {
            int x = startX + (i * (slotSize + gap));

            // A. Draw Slot Background (Black)
            g.setColor(Color.BLACK);
            g.fillRect(x, slotY, slotSize, slotSize);

            // Check if this is the first slot (Index 0) AND if Ability 1 is unlocked
            if (i == 0 && model.isAbilityUnclocked(1)) {

                // 1. Draw the Icon (The Sun)
                if (ResourceManager.sunImg != null) {
                    g.drawImage(ResourceManager.sunImg, x, slotY, slotSize, slotSize, null);
                }

                // 2. Draw Cooldown Overlay (The fading effect)
                int timer = model.getAbility1Timer();
                int maxTime = GameConstants.ABILITY1TIMER; // Make sure this is set correctly in Constants!

                if (timer > 0) {
                    // Calculate percentage of time remaining (0.0 to 1.0)
                    float ratio = (float) timer / maxTime;

                    // Calculate height of the dark overlay based on the ratio
                    // If ratio is 1.0 (just used), height is full (60).
                    // If ratio is 0.5, height is half (30), covering the top half.
                    // This creates the effect of the color "filling up from bottom".
                    int overlayHeight = (int) (slotSize * ratio);

                    // Set color to semi-transparent black
                    g.setColor(new Color(0, 0, 0, 180)); // 180 is the alpha (transparency)

                    // Draw the overlay from the top of the slot downwards
                    g.fillRect(x, slotY, slotSize, overlayHeight);

                    // Optional: Draw the text timer on top if you want
                     g.setColor(Color.WHITE);
                    String keyNum = String.valueOf(timer/60 + 1);
                    int numWidth = g.getFontMetrics().stringWidth(keyNum);
                    g.drawString(keyNum, x + (slotSize - numWidth) / 2, slotY + 37);
                }
            }
            // ------------------------------------------

            // B. Draw Slot Border
            g.setColor(Color.GRAY);
            g.drawRect(x, slotY, slotSize, slotSize);

            // C. Draw Key Number (1, 2, 3)
            g.setColor(Color.WHITE);
            String keyNum = String.valueOf(i + 1);
            int numWidth = g.getFontMetrics().stringWidth(keyNum);
            g.drawString(keyNum, x + (slotSize - numWidth) / 2, slotY + slotSize + 25);
        }
    }

    // Draw Title Screen
    private void drawTitleScreen(Graphics g) {
        g.setColor(Color.WHITE);

        // Title: Big Pixel Font
        setPixelFont(g, 28f);
        String title = "GLADIATOR GAME"; // Cambia il nome se vuoi
        int titleWidth = g.getFontMetrics().stringWidth(title);
        g.drawString(title, (GameConstants.WINDOW_WIDTH - titleWidth)/2, 250);

        // Subtitle: Smaller
        setPixelFont(g, 15f);
        String msg = "Press SPACE to Start";
        int msgWidth = g.getFontMetrics().stringWidth(msg);
        g.drawString(msg, (GameConstants.WINDOW_WIDTH - msgWidth)/2, 350);
    }

    private void drawPauseScreen(Graphics g) {
        // 1. Semi-transparent black overlay
        g.setColor(new Color(0, 0, 0, 150)); // 150 = Alpha (Transparency)
        g.fillRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);

        // 2. "PAUSE" Text
        g.setColor(Color.WHITE);
        setPixelFont(g, 40f); // Large font
        String pauseText = "PAUSE";
        int pauseWidth = g.getFontMetrics().stringWidth(pauseText);
        // Center text
        g.drawString(pauseText, (GameConstants.WINDOW_WIDTH - pauseWidth) / 2, GameConstants.WINDOW_HEIGHT / 2 - 100);

        // 3. Instruction Text
        setPixelFont(g, 20f); // Smaller font
        String resumeText = "Press [P] to Resume";
        int resumeWidth = g.getFontMetrics().stringWidth(resumeText);
        g.drawString(resumeText, (GameConstants.WINDOW_WIDTH - resumeWidth) / 2, GameConstants.WINDOW_HEIGHT / 2 - 50);
    }

    private void drawMessageScreen(Graphics g) {
        // 1. Semi-transparent black background for the whole screen (dimming)
        g.setColor(new Color(0, 0, 0, 100));
        g.fillRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);

        // 2. The Message Box Dimensions
        int boxWidth = 500;
        int boxHeight = 400;
        int boxX = (GameConstants.WINDOW_WIDTH - boxWidth) / 2;
        int boxY = (GameConstants.WINDOW_HEIGHT - boxHeight) / 2;

        // 3. Draw the Box Background (Dark Blue)
        g.setColor(new Color(20, 20, 80));
        g.fillRect(boxX, boxY, boxWidth, boxHeight);

        // 4. Draw the Box Border (White)
        g.setColor(Color.WHITE);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(4)); // Thicker border
        g2.drawRect(boxX, boxY, boxWidth, boxHeight);

        // 5. Draw the Text
        String[] lines = model.getCurrentMessageLines();
        if (lines != null) {
            setPixelFont(g, 20f); // Size for text
            g.setColor(Color.WHITE);

            int lineHeight = 30;
            // Calculate starting Y to center the block of text vertically
            int totalTextHeight = lines.length * lineHeight;
            int startTextY = boxY + (boxHeight - totalTextHeight) / 2 + 10; // +10 adjustment

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];
                // Center align each line horizontally
                int lineWidth = g.getFontMetrics().stringWidth(line);
                int lineX = (GameConstants.WINDOW_WIDTH - lineWidth) / 2;

                g.drawString(line, lineX, startTextY + (i * lineHeight));
            }
        }

        // 6. Draw "Press Space" prompt at the bottom of the box
        setPixelFont(g, 14f);
        g.setColor(Color.YELLOW);
        String prompt = "PRESS [SPACE] TO CONTINUE";
        int promptWidth = g.getFontMetrics().stringWidth(prompt);
        g.drawString(prompt, (GameConstants.WINDOW_WIDTH - promptWidth) / 2, boxY + boxHeight - 20);
    }

    // Draw Game Over Screen
    private void drawGameOverScreen(Graphics g) {
        // Semi-transparent overlay
        g.setColor(new Color(0, 0, 0, 150));
        g.fillRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);

        // Game Over Text
        g.setColor(Color.RED);
        setPixelFont(g, 35f); // Big Red Text
        String GO = "GAME OVER";
        int goWidth = g.getFontMetrics().stringWidth(GO);
        g.drawString(GO, (GameConstants.WINDOW_WIDTH - goWidth)/2, 250);

        g.setColor(Color.WHITE);
        setPixelFont(g, 20f);

        String scoreMsg = "Final Score: " + model.getScore();
        int scoreWidth = g.getFontMetrics().stringWidth(scoreMsg);
        g.drawString(scoreMsg, (GameConstants.WINDOW_WIDTH - scoreWidth)/2, 320);

        // ... Time e Quit/Continue (usa la stessa logica per centrare) ...
        String cont = "[C] Continue";
        String quit = "[Q] Quit";

        g.drawString(cont, (GameConstants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(cont))/2, 400);
        g.drawString(quit, (GameConstants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(quit))/2, 440);
    }

    // Update Player Velocity based on key states
    private void updatePlayerVelocity() {
        Player p = model.getPlayer();

        int vx = 0;
        int vy = 0;

        if (leftPressed && !rightPressed) vx = -1;
        if (rightPressed && !leftPressed) vx = 1;
        if (upPressed && !downPressed) vy = -1;
        if (downPressed && !upPressed) vy = 1;

        p.setVelX(vx);
        p.setVelY(vy);
    }

    // Reset keys when restarting game
    private void resetKeyState() {
        leftPressed = false;
        rightPressed = false;
        upPressed = false;
        downPressed = false;

        Player p = model.getPlayer();
        if (p != null) {
            p.setVelX(0);
            p.setVelY(0);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        GameState state = model.getState();

        // Title Screen Input
        if (state == GameState.TITLE) {
            if (key == KeyEvent.VK_SPACE) {
                model.initGame(); // Start Game
                startTime = System.currentTimeMillis();
                endTime = 0;
            }
        }

        // Playing State Input
        else if (state == GameState.PLAYING) {
            if (key == KeyEvent.VK_LEFT) leftPressed = true;
            if (key == KeyEvent.VK_RIGHT) rightPressed = true;
            if (key == KeyEvent.VK_UP) upPressed = true;
            if (key == KeyEvent.VK_DOWN) downPressed = true;

            if (key == KeyEvent.VK_SPACE) {
                model.setFiring(true);
            }

            if (key == KeyEvent.VK_P) {
                model.setState(GameState.PAUSED);
                resetKeyState();
                System.out.println("Game Paused");
            }

            updatePlayerVelocity();

            // Placeholder for Abilities
            // ABILITY 1
            if(model.getCurrentLevelIndex() > 3 && key == KeyEvent.VK_1){
                model.ability1();
            }

            // ABILITY 2
            if (key == KeyEvent.VK_2) System.out.println("Ability 2 pressed");

            // ABILITY 3
            if (key == KeyEvent.VK_3) System.out.println("Ability 3 pressed");
        }

        else if (state == GameState.PAUSED){
            if (key == KeyEvent.VK_P){
                model.setState(GameState.PLAYING);
                resetKeyState();
                System.out.println("Game Resumed");
            }
        }

        else if (state == GameState.MESSAGE) {
            if (key == KeyEvent.VK_SPACE) {
                model.resumeGame(); // Go back to Playing
                resetKeyState();
            }
        }

        // Game Over State Input
        else if (state == GameState.GAMEOVER) {
            if (key == KeyEvent.VK_C) {
                model.initGame(); // Retry
                resetKeyState();
                startTime = System.currentTimeMillis();
                endTime = 0;
            } else if (key == KeyEvent.VK_Q) {
                System.exit(0);   // Quit App
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_DOWN) downPressed = false;

        if (key == KeyEvent.VK_SPACE) {
            model.setFiring(false);
        }
        if (model.getState() == GameState.PLAYING){
            updatePlayerVelocity();
        }

    }

    @Override
    public void keyTyped(KeyEvent e) {}
}