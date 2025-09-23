package com.informatikgame.core;

import java.util.LinkedList;
import java.util.Queue;

import com.informatikgame.combat.FightManager;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.Player;
import com.informatikgame.world.EnemyType;
import com.informatikgame.world.Room;
import com.informatikgame.world.RoomType;
import com.informatikgame.world.World;

/**
 * Habe den main game loop jetzt hier im game manager gemacht. damit kann ich
 * auch koordinieren zwischen World, Story, Player und FightManager
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
    private Queue<String> inputQueue;
    private boolean waitingForInput;
    private boolean gameRunning;

    private String[] roomNames = {
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
        // Test Spieler erstellen
        // lifeTotal, armourValue, initiative, attack, defense, damage
        this.player = new Player(36, 4, 8, 15, 10, 8);
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
     * Startet das Spiel (GUI-Version)
     */
    public void startGame() {
        if (eventListener != null) {
            // Initiale Events
            eventListener.onRoomChange(0, world.getgetCurrent_room_count()(), roomNames[0]
            );
            eventListener.onPlayerHealthChange(player.getLifeTotal(), 100);
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
            notifyLog("Dieser Raum ist leer.");
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
        if (world.current_room_number < world.room_count - 1) {
            // Warte auf Spieler-Entscheidung
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
        // (Muss noch implementiert werden)
    }

    private void processInput() {
        String input = inputQueue.poll();
        if (input != null) {
            if (input.equalsIgnoreCase("J") || input.equalsIgnoreCase("Y")) {
                waitingForInput = false;
                advanceToNextRoom();
            } else if (input.equalsIgnoreCase("N")) {
                notifyLog("Du ruhst dich aus...");
                // Könnte hier Heilung implementieren
            }
        }
    }

    /**
     * Spieler geht zum nächsten Raum
     */
    public void advanceToNextRoom() {
        if (world.getCurrent_room_number() < world.getRoom_count() - 1) {  // Bug-Fix!
            world.getCurrent_room_number()++;
            world.getCurrent_room() = world.getRoomList().get(world.getCurrent_room_number());

            if (eventListener != null) {
                String roomName = roomNames[Math.min(world.getCurrent_room_number(), roomNames.length - 1)];
                eventListener.onRoomChange(world.getCurrent_room_number(), world.getRoom_count(), roomName);
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
     * Gibt den aktuellen Raum-Namen zurück
     */
    public String getCurrentRoomName() {
        int index = Math.min(world.getCurrent_room_number(), roomNames.length - 1);
        return roomNames[index];
    }

    /**
     * Gibt die aktuelle Raum-Beschreibung zurück
     */
    public String getRoomDescription() {
        String[] descriptions = {
            "Ein düsterer Eingangsbereich. Die Luft ist stickig und riecht nach Verwesung.",
            "Ein langer, verlassener Flur. Blutspuren führen tiefer ins Gebäude.",
            "Die alte Bibliothek. Zerrissene Bücher liegen überall verstreut.",
            "Eine kleine Speisekammer. Hier riecht es modrig.",
            "Der große Speisesaal. Umgestürzte Tische zeugen von einem Kampf.",
            "Das Laboratorium. Zerbrochene Reagenzgläser knirschen unter deinen Füßen.",
            "Ein dunkler Korridor. Du hörst schwere Schritte in der Ferne.",
            "Die Boss-Kammer. Eine bedrohliche Aura erfüllt den Raum."
        };

        int index = Math.min(world.getCurrent_room_number(), descriptions.length - 1);
        return descriptions[index];
    }
}