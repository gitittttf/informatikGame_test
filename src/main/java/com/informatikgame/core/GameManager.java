package com.informatikgame.core;

import com.informatikgame.combat.FightManager;
import com.informatikgame.entities.Player;
import com.informatikgame.world.RoomType;
import com.informatikgame.world.World;

/**
 * Habe den main game loop jetzt hier im game manager gemacht. damit kann ich
 * auch koordinieren zwischen World, Story, Player und FightManager
 */
public class GameManager {

    private World world;
    private FightManager fightManager;

    // Konstruktor
    public GameManager() {
        initializeGame();
    }

    /**
     * Initialisiert das Spiel mit Standardwerten
     */
    private void initializeGame() {
        // Test Spieler erstellen
        // lifeTotal, armourValue, initiative, attack, defense, damage
        this.fightManager = new FightManager(new Player(36, 4, 8, 15, 10, 8));

        // Welt mit vordefinierten Räumen erstellen (erstmal so testweise)
        RoomType[] gameRooms = {
            RoomType.INTRO_ROOM,
            RoomType.FLOOR_ROOM,
            RoomType.LIBRARY_ROOM,
            RoomType.PANTRY,
            RoomType.DINING_HALL,
            RoomType.LABORATORY,
            RoomType.CORRIDOR,
            RoomType.FINAL_ROOM
        };

        this.world = new World(gameRooms);
    }

    public World getWorld() {
        return world;
    }

    public FightManager getFightManager() {
        return fightManager;
    }
}
