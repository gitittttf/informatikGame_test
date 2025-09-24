package com.informatikgame.core;

import java.util.LinkedList;
import java.util.Queue;

import com.informatikgame.combat.FightManager;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.Player;
import com.informatikgame.world.EnemyType;
import com.informatikgame.world.PlayerType;
import com.informatikgame.world.Room;
import com.informatikgame.world.RoomType;
import com.informatikgame.world.World;

/**
 * GameManager koordiniert zwischen World, Story, Player und FightManager
 */
public class GameManager {

    public interface GameEventListener {

        void onCombatLogUpdate(String message);

        void onRoomChange(int roomNumber, int totalRooms, String roomName);

        void onPlayerHealthChange(int current, int max);

        void onEnemyUpdate(Enemy[] enemies);

        void onCombatStart();

        void onCombatEnd(boolean won);

        void onGameOver();

        void onVictory();

        void onPlayerAction(String action);
    }

    private World world;
    private FightManager fightManager;
    private Player player;
    private GameEventListener eventListener;
    private final Queue<String> inputQueue;
    private boolean waitingForInput;
    private boolean gameRunning;
    private int maxPlayerHealth = 100;  // Für HP anzeige

    private final String[] roomNames = {
        "Eingangsbereich",
        "Verlassener Flur",
        "Alte Bibliothek",
        "Speisekammer",
        "Speisesaal",
        "Laboratorium",
        "Dunkler Korridor",
        "Boss-Kammer"
    };

    // Konstruktor
    public GameManager() {
        this.inputQueue = new LinkedList<>();
        this.waitingForInput = false;
        initializeGame();
    }

    public void setEventListener(GameEventListener listener) {
        this.eventListener = listener;
    }

    /**
     * Initialisiert das Spiel mit Standardwerten
     */
    private void initializeGame() {
        // Spieler mit PlayerType erstellen (hier testweise SWORD_FIGHTER)
        // lifeTotal, armourValue, initiative, attack, defense, damage
        this.player = new Player(PlayerType.SWORD_FIGHTER);
        this.maxPlayerHealth = player.getLifeTotal();  // Maximum hp speichern
        this.fightManager = new FightManager(player);

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
        this.gameRunning = true;
    }

    /**
     * Startet das Spiel (GUI Version)
     */
    public void startGame() {
        if (eventListener != null) {
            // Initiale Events
            eventListener.onRoomChange(0, world.getRoom_count(), roomNames[0]);
            eventListener.onPlayerHealthChange(player.getLifeTotal(), maxPlayerHealth);
        }

        // Starte mit dem ersten Raum
        processCurrentRoom();
    }

    /**
     * Verarbeitet den aktuellen Raum
     */
    private void processCurrentRoom() {
        Room currentRoom = world.getCurrent_room();

        if (currentRoom.getEnemiesInRoom().length == 0) {
            notifyLog("Dieser Raum ist leer");
            checkForNextRoom();
        } else {
            startCombat(currentRoom);
        }
    }

    /**
     * Startet einen Kampf
     */
    private void startCombat(Room room) {
        if (eventListener != null) {
            eventListener.onCombatStart();
            eventListener.onEnemyUpdate(createEnemyArray(room.getEnemiesInRoom()));
        }

        notifyLog("Kampf beginnt! " + room.getEnemiesInRoom().length + " Gegner greifen an!");

        // FightManager übernimmt
        boolean won = fightManager.fight(room.getEnemiesInRoom());

        if (eventListener != null) {
            eventListener.onCombatEnd(won);
            eventListener.onPlayerHealthChange(player.getLifeTotal(), 100);
        }

        if (!won) {
            gameOver();
        } else {
            notifyLog("Kampf gewonnen!");
            checkForNextRoom();
        }
    }

    /**
     * Konvertiert EnemyTypes zu Enemy-Objekten für die GUI
     */
    private Enemy[] createEnemyArray(EnemyType[] types) {
        Enemy[] enemies = new Enemy[types.length];
        for (int i = 0; i < types.length; i++) {
            enemies[i] = new Enemy(types[i]);
        }
        return enemies;
    }

    /**
     * Prüft ob es weitere Räume gibt
     */
    private void checkForNextRoom() {
        if (world.getCurrent_room_number() < world.getRoom_count() - 1) {
            // Warte auf choice
            waitingForInput = true;
            notifyLog("Möchtest du zum nächsten Raum gehen? (J/N)");
        } else {
            victory();
        }
    }

    /**
     * Verarbeitet Spieler-Input von der GUI
     */
    public void handlePlayerInput(String input) {
        inputQueue.add(input);

        if (waitingForInput) {
            processInput();
        }
    }

    /**
     * Verarbeitet Spieler-Aktion im Kampf
     */
    public void handleCombatAction(int enemyIndex, int finte, int wuchtschlag) {
        if (eventListener != null) {
            String action = "Angriff auf Gegner " + (enemyIndex + 1);
            if (finte > 0) {
                action += " (Finte Lv." + finte + ")";
            }
            if (wuchtschlag > 0) {
                action += " (Wuchtschlag Lv." + wuchtschlag + ")";
            }
            eventListener.onPlayerAction(action);
        }
        // Diese Info wird an FightManager weitergegeben
    }

    private void processInput() {
        String input = inputQueue.poll();
        if (input != null) {
            if (input.equalsIgnoreCase("J") || input.equalsIgnoreCase("Y")) {
                waitingForInput = false;
                advanceToNextRoom();
            } else if (input.equalsIgnoreCase("N")) {
                // falls man ausruhen + heilen haben will:
                //
                // notifyLog("Du ruhst dich aus...");
                // // Kleine Heilung beim Ausruhen
                // int healAmount = 5;
                // player.heal(healAmount);
                // notifyLog("Du heilst " + healAmount + " HP.");
                // if (eventListener != null) {
                //     eventListener.onPlayerHealthChange(player.getLifeTotal(), maxPlayerHealth);
                // }
                // 
            }
        }
    }

    /**
     * Spieler geht zum nächsten Raum
     */
    public void advanceToNextRoom() {
        if (world.hasNextRoom()) {
            world.advance_to_next_room();

            if (eventListener != null) {
                int roomNum = world.getCurrent_room_number();
                String roomName = roomNames[Math.min(roomNum, roomNames.length - 1)];
                eventListener.onRoomChange(roomNum, world.getRoom_count(), roomName);
            }

            notifyLog("Du betrittst: " + roomNames[world.getCurrent_room_number()]);
            processCurrentRoom();
        }
    }

    private void gameOver() {
        gameRunning = false;
        notifyLog("GAME OVER - Du wurdest besiegt!");
        if (eventListener != null) {
            eventListener.onGameOver();
        }
    }

    private void victory() {
        gameRunning = false;
        notifyLog("SIEG! Du hast alle Räume gemeistert!");
        if (eventListener != null) {
            eventListener.onVictory();
        }
    }

    private void notifyLog(String message) {
        if (eventListener != null) {
            eventListener.onCombatLogUpdate(message);
        }
    }

    public FightManager getFightManager() {
        return fightManager;
    }

    public Player getPlayer() {
        return player;
    }

    public World getWorld() {
        return world;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public boolean isWaitingForInput() {
        return waitingForInput;
    }

    /**
     * Gibt den aktuellen raumnamen zurück
     */
    public String getCurrentRoomName() {
        int index = Math.min(world.getCurrent_room_number(), roomNames.length - 1);
        return roomNames[index];
    }

    /**
     * Gibt die aktuelle raumbeschreibung zurück
     */
    public String getRoomDescription() {
        String[] descriptions = { // beschreibungen
        };

        int index = Math.min(world.getCurrent_room_number(), descriptions.length - 1);
        return descriptions[index];
    }
}
