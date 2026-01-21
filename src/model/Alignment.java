package model;

/**
 * Alignment Enum
 * Defines which "team" a game object belongs to.
 * Used to prevent friendly fire and determine collision logic.
 */
public enum Alignment {
    PLAYER, // Belongs to the Player (Targets Enemies)
    ENEMY  // Belongs to Enemies (Targets Player)
}