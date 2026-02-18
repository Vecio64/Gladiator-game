package main;

import view.GamePanel;
import view.ResourceManager;
import javax.swing.*;

/**
 * Main Class
 *
 * The entry point of the application.
 * It handles the initialization sequence: loading resources, setting up the window,
 * and starting the main GamePanel where the game loop runs.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Load resources (Images, Fonts) BEFORE creating the window.
        // This ensures all assets are ready in memory when the GamePanel tries to draw them.
        ResourceManager.loadImages();

        // 2. Setup the main game window (JFrame)
        JFrame frame = new JFrame("Shooting Game MVC");

        // Ensure the application stops running when the window is closed
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // 3. Add the GamePanel
        // The GamePanel constructor initializes the GameModel and starts the Game Loop timer.
        frame.add(new GamePanel());

        // 4. Finalize Window Layout
        frame.pack(); // Resize the window to fit the GamePanel's preferred size (Constants)
        frame.setLocationRelativeTo(null); // Center the window on the screen
        frame.setVisible(true); // Make the window visible to the user
    }

}