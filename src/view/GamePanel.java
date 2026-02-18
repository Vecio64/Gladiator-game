package view;

import model.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import javax.swing.*;
import java.awt.image.BufferedImage;

/**
 * GamePanel Class
 *
 * Acts as both the **View** and the **Controller** in the MVC architecture.
 *
 * Responsibilities:
 * 1. View: Renders the game state to the screen (Background, Player, Enemies, UI).
 * 2. Controller: Listens for keyboard input and updates the GameModel accordingly.
 * 3. Game Loop: Contains the Swing Timer that drives the game update cycle (60 FPS).
 */
public class GamePanel extends JPanel implements KeyListener {

    private GameModel model; // Reference to the Model (Game Logic)
    private Timer timer;     // Timer for the game loop

    // --- Input State Variables ---
    // Tracks which keys are currently held down for smooth movement
    private boolean leftPressed = false;
    private boolean rightPressed = false;
    private boolean upPressed = false;
    private boolean downPressed = false;

    // ==========================================
    // ROMAN THEME COLOR PALETTE
    // ==========================================
    // Declared as 'final' to improve performance (avoids creating new Color objects every frame)

    // Backgrounds
    private final Color romanRedDark = new Color(40, 5, 5);           // Deep blood red for HUD backgrounds
    private final Color overlayDark = new Color(0, 0, 0, 150);        // Semi-transparent black for dimming
    private final Color boxBackground = new Color(60, 10, 10, 240);   // Imperial red for message boxes
    private final Color slotBackground = new Color(30, 10, 5);        // Dark brown for ability slots

    // Text & Borders
    private final Color romanGold = new Color(218, 165, 32);          // Ancient Gold for borders/titles
    private final Color marbleWhite = new Color(255, 250, 225);       // Cream/Marble white for main text
    private final Color highlightGold = new Color(255, 215, 0);       // Bright Gold for blinking text

    // Utilities
    private final Color cooldownOverlay = new Color(0, 0, 0, 180);    // Dark overlay for ability cooldowns

    public GamePanel() {
        model = new GameModel(); // Initialize Model (Starts in TITLE state)

        // Set Panel dimensions (Game Field + Top HUD + Bottom HUD)
        this.setPreferredSize(new Dimension(GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT));
        this.setBackground(Color.BLACK);

        // Input Setup
        this.setFocusable(true);
        this.addKeyListener(this);

        // --- Start Game Loop ---
        // Timer fires every ~16ms (1000ms / 60 FPS)
        timer = new Timer(1000/GameConstants.FPS, e -> {
            model.update(); // Update Logic
            repaint();      // Trigger Redraw
        });
        timer.start();
    }

    /**
     * Main Painting Method.
     * Called automatically by Swing whenever repaint() is triggered.
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clear previous frame

        // Switch drawing logic based on the current Game State
        GameState state = model.getState();

        if (state == GameState.TITLE) {
            drawTitleScreen(g);
        }
        else if (state == GameState.PLAYING || state == GameState.PAUSED || state == GameState.MESSAGE) {
            drawGameScreen(g); // Draw the game world first

            // Draw overlays on top if needed
            if (state == GameState.PAUSED){
                drawPauseScreen(g);
            }
            else if (state == GameState.MESSAGE) {
                drawMessageScreen(g);
            }
        }
        else if (state == GameState.GAMEOVER) {
            drawGameScreen(g); // Keep game visible in background
            drawGameOverScreen(g);
        }
    }

    /**
     * Helper to set the custom Pixel Font if available, otherwise fallback to Arial.
     */
    private void setPixelFont(Graphics g, float size) {
        if (ResourceManager.pixelFont != null) {
            g.setFont(ResourceManager.pixelFont.deriveFont(size));
        } else {
            g.setFont(new Font("Arial", Font.BOLD, (int)size));
        }
    }

    // --- DRAWING METHODS ---

    /**
     * Renders the main gameplay view.
     */
    private void drawGameScreen(Graphics g) {

        // 1. Draw Background
        if (model.getBackground() != null) {
            model.getBackground().draw(g);
        } else {
            // Fallback: Black background if image is missing
            g.setColor(Color.BLACK);
            g.fillRect(0, GameConstants.HUD_HEIGHT, GameConstants.WINDOW_WIDTH, GameConstants.FIELD_HEIGHT);
        }

        // 2. Draw Game Objects (Player, Enemies, Projectiles)
        for (GameObject obj : model.getObjects()) {
            // Logic: Flicker player visibility when invincible (Visual feedback)
            if (obj instanceof Player && model.isInvincible()) {
                // Toggle visibility every 100ms
                if (System.currentTimeMillis() % 200 < 100) {
                    continue; // Skip drawing this frame
                }
            }
            obj.draw(g);
        }

        // 3. Draw Top HUD (Score, Stage, Lives)
        drawTopHUD(g);

        // 4. Draw Bottom HUD (Ability Slots)
        drawBottomHUD(g);
    }

    /**
     * Draws the Top HUD containing Score, Stage Number, and Lives.
     */
    private void drawTopHUD(Graphics g) {
        // 1. Draw Background Bar (Imperial Red)
        g.setColor(romanRedDark);
        g.fillRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.HUD_HEIGHT);

        // 2. Draw Gold Separator Line
        g.setColor(romanGold);
        g.drawLine(0, GameConstants.HUD_HEIGHT, GameConstants.WINDOW_WIDTH, GameConstants.HUD_HEIGHT);

        // Optional: Draw a second darker line for a "chiseled" 3D effect
        g.setColor(new Color(100, 50, 0));
        g.drawLine(0, GameConstants.HUD_HEIGHT + 1, GameConstants.WINDOW_WIDTH, GameConstants.HUD_HEIGHT + 1);

        // Font settings
        setPixelFont(g, 18f);
        int textY = 35;

        // A. Score (Marble White)
        g.setColor(marbleWhite);
        g.drawString("SCORE:" + model.getScore(), 10, textY);

        // B. Stage (Centered, Gold)
        g.setColor(romanGold);
        String stageText = model.getStageText();
        int stageX = (GameConstants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(stageText)) / 2;
        g.drawString(stageText, stageX, textY);

        // C. Lives / Hearts (Right aligned)
        int maxLives = GameConstants.PLAYER_MAX_LIVES;
        int currentLives = model.getLives();
        int heartSize = 32;
        int spacing = 8;
        int startX = GameConstants.WINDOW_WIDTH - 20 - (maxLives * (heartSize + spacing));
        int heartY = (GameConstants.HUD_HEIGHT - heartSize) / 2;

        for (int i = 0; i < maxLives; i++) {
            // Choose icon: Full Heart vs Empty Heart
            BufferedImage icon = (i < currentLives) ? ResourceManager.heartFullImg : ResourceManager.heartEmptyImg;

            if (icon != null) {
                g.drawImage(icon, startX + (i * (heartSize + spacing)), heartY, heartSize, heartSize, null);
            } else {
                // Fallback shape if images missing
                g.setColor(i < currentLives ? Color.RED : Color.GRAY);
                g.fillOval(startX + (i * (heartSize + spacing)), heartY, heartSize, heartSize);
            }
        }
    }

    /**
     * Draws the Bottom HUD containing Ability Slots and Cooldowns.
     */
    private void drawBottomHUD(Graphics g) {
        // Calculate start Y position (below the game field)
        int startY = GameConstants.HUD_HEIGHT + GameConstants.FIELD_HEIGHT;
        int height = GameConstants.BOTTOM_HUD_HEIGHT;

        // 1. Background Bar
        g.setColor(romanRedDark);
        g.fillRect(0, startY, GameConstants.WINDOW_WIDTH, height);

        // 2. Separator Line
        g.setColor(romanGold);
        g.drawLine(0, startY, GameConstants.WINDOW_WIDTH, startY);

        // 3. Draw Ability Slots
        int slotSize = 60;
        int gap = 40;
        int totalWidth = (3 * slotSize) + (2 * gap);
        int startX = (GameConstants.WINDOW_WIDTH - totalWidth) / 2;
        int slotY = startY + (height - slotSize) / 2 - 9;

        setPixelFont(g, 14f);

        for (int i = 0; i < 3; i++) {
            int x = startX + (i * (slotSize + gap));

            // A. Draw Slot Background
            g.setColor(slotBackground);
            g.fillRect(x, slotY, slotSize, slotSize);

            // B. Draw Ability Icons and Cooldown Overlays

            // --- ABILITY 1 (SUN) ---
            if (i == 0 && model.isAbilityUnclocked(1)) {
                if (ResourceManager.sunImg != null) {
                    g.drawImage(ResourceManager.sunImg, x, slotY, slotSize, slotSize, null);
                }

                // Cooldown Visualization
                int timer = model.getAbilityNthTimer(1);
                if (timer > 0) {
                    float ratio = (float) timer / GameConstants.ABILITY1TIMER;
                    int overlayHeight = (int) (slotSize * ratio);

                    // Draw dark overlay representing remaining cooldown
                    g.setColor(cooldownOverlay);
                    g.fillRect(x, slotY, slotSize, overlayHeight);

                    // Draw countdown number
                    g.setColor(marbleWhite);
                    String keyNum = String.valueOf(timer/60 + 1);
                    int numWidth = g.getFontMetrics().stringWidth(keyNum);
                    g.drawString(keyNum, x + (slotSize - numWidth) / 2, slotY + 37);
                }
            }

            // --- ABILITY 2 (LIGHTNING) ---
            if (i == 1 && model.isAbilityUnclocked(2)) {
                if (ResourceManager.lightingImg != null) {
                    g.drawImage(ResourceManager.lightingImg, x, slotY, slotSize, slotSize, null);
                }

                // Cooldown Visualization
                int timer = model.getAbilityNthTimer(2);
                if (timer > 0) {
                    float ratio = (float) timer / GameConstants.ABILITY2TIMER;
                    int overlayHeight = (int) (slotSize * ratio);

                    g.setColor(cooldownOverlay);
                    g.fillRect(x, slotY, slotSize, overlayHeight);

                    g.setColor(marbleWhite);
                    String keyNum = String.valueOf(timer/60 + 1);
                    int numWidth = g.getFontMetrics().stringWidth(keyNum);
                    g.drawString(keyNum, x + (slotSize - numWidth) / 2, slotY + 37);
                }
            }

            // C. Draw Slot Border (Gold)
            g.setColor(romanGold);
            g.drawRect(x, slotY, slotSize, slotSize);
            g.drawRect(x - 1, slotY - 1, slotSize + 2, slotSize + 2); // Double border for thickness

            // D. Draw Key Number (1, 2, 3)
            g.setColor(marbleWhite);
            String keyNum = String.valueOf(i + 1);
            int numWidth = g.getFontMetrics().stringWidth(keyNum);
            g.drawString(keyNum, x + (slotSize - numWidth) / 2, slotY + slotSize + 25);
        }
    }

    // Draw Title Screen
    private void drawTitleScreen(Graphics g) {
        // 1. Draw the Background Image
        if (ResourceManager.homeScreenImg != null) {
            g.drawImage(ResourceManager.homeScreenImg, 0, 0, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT, null);
        } else {
            // Fallback: Imperial Red
            g.setColor(romanRedDark);
            g.fillRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);
        }

        // 2. Draw Flashing "Press Start" Text
        // Logic: (CurrentTime / 500ms) % 2 gives 0 or 1.
        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            setPixelFont(g, 20f);
            String msg = "PRESS [SPACE] TO START";

            FontMetrics metrics = g.getFontMetrics();
            int msgWidth = metrics.stringWidth(msg);
            int x = (GameConstants.WINDOW_WIDTH - msgWidth) / 2;
            int y = GameConstants.WINDOW_HEIGHT - 100;

            // Draw Shadow
            g.setColor(Color.BLACK);
            g.drawString(msg, x + 2, y + 2);
            g.drawString(msg, x - 2, y - 2);

            // Draw Main Text
            g.setColor(highlightGold);
            g.drawString(msg, x, y);
        }
    }

    private void drawPauseScreen(Graphics g) {
        // 1. Red Overlay
        g.setColor(new Color(40, 5, 5, 200));
        g.fillRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);

        // 2. "PAUSE" Title
        setPixelFont(g, 40f);
        String pauseText = "PAUSE";
        int pauseWidth = g.getFontMetrics().stringWidth(pauseText);

        g.setColor(romanGold);
        g.drawString(pauseText, (GameConstants.WINDOW_WIDTH - pauseWidth) / 2, GameConstants.WINDOW_HEIGHT / 2 - 20);

        // 3. Instruction Text
        setPixelFont(g, 20f);
        String resumeText = "Press [P] to Resume";
        int resumeWidth = g.getFontMetrics().stringWidth(resumeText);

        g.setColor(marbleWhite);
        g.drawString(resumeText, (GameConstants.WINDOW_WIDTH - resumeWidth) / 2, GameConstants.WINDOW_HEIGHT / 2 + 30);
    }

    private void drawMessageScreen(Graphics g) {
        // 1. Dim Background
        g.setColor(overlayDark);
        g.fillRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);

        // 2. Box Dimensions
        int boxWidth = 500;
        int boxHeight = 400;
        int boxX = (GameConstants.WINDOW_WIDTH - boxWidth) / 2;
        int boxY = (GameConstants.WINDOW_HEIGHT - boxHeight) / 2;

        // 3. Draw Box Background (Imperial Red)
        g.setColor(boxBackground);
        g.fillRect(boxX, boxY, boxWidth, boxHeight);

        // 4. Draw Box Border (Gold)
        g.setColor(romanGold);
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(5)); // Thicker border
        g2.drawRect(boxX, boxY, boxWidth, boxHeight);

        // Inner decorative border
        g2.setStroke(new BasicStroke(2));
        g2.setColor(new Color(150, 100, 0));
        g2.drawRect(boxX + 10, boxY + 10, boxWidth - 20, boxHeight - 20);

        // 5. Draw Text Content
        String[] lines = model.getCurrentMessageLines();
        if (lines != null) {
            setPixelFont(g, 20f);

            int lineHeight = 30;
            int totalTextHeight = lines.length * lineHeight;
            int startTextY = boxY + (boxHeight - totalTextHeight) / 2;

            for (int i = 0; i < lines.length; i++) {
                String line = lines[i];

                // Highlight Headers (e.g., BOSS name) in Gold
                if (line.endsWith(":") || line.contains("APOLLO") || line.contains("ZEUS")) {
                    g.setColor(highlightGold);
                } else {
                    g.setColor(marbleWhite);
                }

                // Center align text
                int lineWidth = g.getFontMetrics().stringWidth(line);
                int lineX = (GameConstants.WINDOW_WIDTH - lineWidth) / 2;

                g.drawString(line, lineX, startTextY + (i * lineHeight));
            }
        }

        // 6. Draw "Press Space" Prompt (Blinking)
        setPixelFont(g, 16f);
        g.setColor(highlightGold);
        String prompt = "- PRESS [SPACE] TO CONTINUE -";
        int promptWidth = g.getFontMetrics().stringWidth(prompt);

        if ((System.currentTimeMillis() / 500) % 2 == 0) {
            g.drawString(prompt, (GameConstants.WINDOW_WIDTH - promptWidth) / 2, boxY + boxHeight - 30);
        }
    }

    private void drawGameOverScreen(Graphics g) {
        // 1. Red Overlay
        g.setColor(new Color(40, 5, 5, 220));
        g.fillRect(0, 0, GameConstants.WINDOW_WIDTH, GameConstants.WINDOW_HEIGHT);

        // 2. GAME OVER Title
        setPixelFont(g, 50f);
        String GO = "GAME OVER";
        int goWidth = g.getFontMetrics().stringWidth(GO);
        int centerX = (GameConstants.WINDOW_WIDTH - goWidth) / 2;
        int centerY = 250;

        // Shadow
        g.setColor(new Color(20, 0, 0));
        g.drawString(GO, centerX + 4, centerY + 4);

        // Main Text
        g.setColor(romanGold);
        g.drawString(GO, centerX, centerY);

        // 3. Final Score
        setPixelFont(g, 25f);
        g.setColor(marbleWhite);
        String scoreMsg = "Final Score: " + model.getScore();
        int scoreWidth = g.getFontMetrics().stringWidth(scoreMsg);
        g.drawString(scoreMsg, (GameConstants.WINDOW_WIDTH - scoreWidth) / 2, 330);

        // 4. Options
        setPixelFont(g, 20f);

        // Continue Option
        g.setColor(highlightGold);
        String cont1 = "Press [C] to continue";
        String cont2 = "from last checkpoint";
        g.drawString(cont1, (GameConstants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(cont1)) / 2, 440);
        g.drawString(cont2, (GameConstants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(cont1)) / 2, 480);

        // Quit Option
        g.setColor(new Color(200, 150, 100)); // Bronze
        String quit = "Press [Q] to Quit";
        g.drawString(quit, (GameConstants.WINDOW_WIDTH - g.getFontMetrics().stringWidth(quit)) / 2, 560);
    }

    // --- INPUT HANDLING ---

    /**
     * Updates the player's velocity based on the current state of WASD/Arrow keys.
     *
     * Logic:
     * 1. Determines the raw direction vector (-1, 0, or 1) for X and Y.
     * 2. Normalizes the vector if moving diagonally.
     */
    private void updatePlayerVelocity() {
        Player p = model.getPlayer();
        if (p == null) return;

        double vx = 0;
        double vy = 0;

        // 1. Determine direction (Additive Logic)
        // If both Left and Right are pressed, -1 + 1 = 0 (Player stops horizontally)
        if (leftPressed)  vx -= 1;
        if (rightPressed) vx += 1;

        if (upPressed)    vy -= 1;
        if (downPressed)  vy += 1;

        // 2. Normalize Diagonal Movement
        // If moving along both axes, multiply by approx 0.71 (1 / sqrt(2))
        // This ensures the total speed remains constant.
        if (vx != 0 && vy != 0) {
            vx *= 0.71;
            vy *= 0.71;
        }

        // 3. Apply velocity to Player
        // We pass the normalized double value; Player class handles the speed multiplication.
        p.setVelX(vx);
        p.setVelY(vy);
    }

    /**
     * Resets all key states to false.
     * Called when pausing or game over occurs to stop the player from "drifting".
     */
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

    // --- KEY LISTENER METHODS ---

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        GameState state = model.getState();

        // 1. TITLE SCREEN
        if (state == GameState.TITLE) {
            if (key == KeyEvent.VK_SPACE) {
                model.initGame(); // Start Game
            }
        }

        // 2. PLAYING
        else if (state == GameState.PLAYING) {
            // Movement
            if (key == KeyEvent.VK_LEFT) leftPressed = true;
            if (key == KeyEvent.VK_RIGHT) rightPressed = true;
            if (key == KeyEvent.VK_UP) upPressed = true;
            if (key == KeyEvent.VK_DOWN) downPressed = true;

            // Shooting
            if (key == KeyEvent.VK_SPACE) {
                model.setFiring(true);
            }

            // Pause
            if (key == KeyEvent.VK_P) {
                model.setState(GameState.PAUSED);
                resetKeyState();
            }

            updatePlayerVelocity();

            // Abilities
            // Ability 1 (Sun)
            if(model.getCurrentLevelIndex() > 3 && key == KeyEvent.VK_1){
                model.ability1();
            }

            // Ability 2 (Lightning)
            if (model.getCurrentLevelIndex() > 7 && key == KeyEvent.VK_2){
                model.ability2();
            }
        }

        // 3. PAUSED
        else if (state == GameState.PAUSED){
            if (key == KeyEvent.VK_P){
                model.setState(GameState.PLAYING);
                resetKeyState();
            }
        }

        // 4. MESSAGE (Dialogue)
        else if (state == GameState.MESSAGE) {
            if (key == KeyEvent.VK_SPACE) {
                model.resumeGame(); // Close message box
                resetKeyState();
            }
        }

        // 5. GAME OVER
        else if (state == GameState.GAMEOVER) {
            if (key == KeyEvent.VK_C) {
                // Continue from checkpoint
                model.continueGame();
                resetKeyState();
            } else if (key == KeyEvent.VK_Q) {
                System.exit(0);   // Quit App
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int key = e.getKeyCode();

        // Update flags
        if (key == KeyEvent.VK_LEFT) leftPressed = false;
        if (key == KeyEvent.VK_RIGHT) rightPressed = false;
        if (key == KeyEvent.VK_UP) upPressed = false;
        if (key == KeyEvent.VK_DOWN) downPressed = false;

        // Stop shooting
        if (key == KeyEvent.VK_SPACE) {
            model.setFiring(false);
        }

        // Recalculate velocity if still playing
        if (model.getState() == GameState.PLAYING){
            updatePlayerVelocity();
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
}