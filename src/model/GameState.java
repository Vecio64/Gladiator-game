package model;

/**
 * GameState Enum
 *
 * Defines the high-level states of the game application.
 * Used by the GameLoop and GamePanel to control logic updates and rendering modes.
 */
public enum GameState {
    TITLE,      // The initial screen (Press Start)
    PLAYING,    // The main gameplay loop is active
    PAUSED,     // Game logic is suspended, pause menu is shown
    MESSAGE,    //  alert box is on screen (gameplay suspended)
    GAMEOVER    // Player has died, game over screen is shown
}