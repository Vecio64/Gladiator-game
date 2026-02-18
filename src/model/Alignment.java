package model;

/**
 * Alignment Enum
 *
 * Defines the "team" allegiance of a game object.
 * This is crucial for collision detection to prevent friendly fire
 * (e.g., Player projectiles should not hurt the Player, but should hurt Enemies).
 */
public enum Alignment {
    PLAYER, // Belongs to the Player (Targets Enemies)
    ENEMY   // Belongs to Enemies (Targets Player)
}