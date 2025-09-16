
import java.util.Scanner;

/**
 * Habe den main game loop jetzt hier im game manager gemacht. damit kann ich
 * auch koordinieren zwischen World, Story, Player und FightManager
 */
public class GameManager {

    private World world;
    private Player player;
    private Story story;
    private Scanner scanner;
    private boolean gameRunning;

    // Konstruktor
    public GameManager() {
        this.scanner = new Scanner(System.in);
        this.gameRunning = true;
        this.story = new Story();
        initializeGame();
    }

    /**
     * Initialisiert das Spiel mit Standardwerten
     */
    private void initializeGame() {
        // Test Spieler erstellen
        // lifeTotal, armourValue, initiative, attack, defense, damage
        this.player = new Player(100, 2, 8, 10, 10, 8);

        // Welt mit vordefinierten Räumen erstellen (erstmal so testweise)
        RoomType[] gameRooms = {
            RoomType.INTRO_ROOM,
            RoomType.FLOOR_ROOM,
            RoomType.LIBRARY_ROOM,
            RoomType.PANTRY,
            RoomType.DININGHALL,
            RoomType.LABORATORY,
            RoomType.CORRIDOR,
            RoomType.FINAL_ROOM
        };

        this.world = new World(gameRooms);
    }

    /**
     * Main game loop
     */
    public void startGame() {
        // System.out.println("=== WILLKOMMEN IM DUNGEON ===\n");

        // Intro-Story
        tellStory("intro"); // erstmal nur platzhalter methode, muss noch ein Story.java implementiert werden von paul

        // Hauptspielschleife
        while (gameRunning && player.lifeTotal > 0) {
            // Aktuellen Raum anzeigen
            // System.out.println("\n--- RAUM " + (world.current_room_number + 1)
            //        + " von " + world.room_count + " ---");

            // Story für diesen Raum erzählen (falls vorhanden)
            tellRoomStory();
            /**
             * Dazu: ich weiß nicht ob wir die story so handeln sollen, also
             * einmal tellStory(storyKey) und tellRoomStory, oder nur tellStory
             * und dann könnte man für storyKey auch ein raum nennen oder so
             */

            // Kampf in diesem Raum durchführen
            boolean fightWon = handleRoomCombat();

            if (!fightWon) {
                // Spieler hat verloren
                gameOver();
                break;
            }

            // Nach dem Kampf Spieler-Status anzeigen falls wir so wollen
            showPlayerStatus();

            // Prüfen ob noch Räume übrig sind
            if (world.current_room_number < world.room_count - 1) {
                // Spieler fragen ob er weitergehen möchte
                if (askToContinue()) {
                    world.advance_to_next_room();
                } else {
                    pauseGame();
                }
            } else {
                // Gewonnen
                victory();
                break;
            }
        }

        scanner.close();
    }

    /**
     * Führt den Kampf im aktuellen Raum durch
     *
     * @return true wenn gewonnen, false wenn verloren
     */
    private boolean handleRoomCombat() {

        // So gut benedikt? keine ahnung
        Room currentRoom = world.current_room;

        if (currentRoom.enemyList.isEmpty()) {
            // TODO: leerer raum handling
            return true;
        }

        // Start von kampf
        // System.out.println("Gegner in diesem Raum: " + currentRoom.enemyList.size());
        // FightManager mit Player und Enemies aus dem aktuellen Raum initialisieren
        Enemy[] enemies = currentRoom.enemyList.toArray(new Enemy[0]);
        FightManager fightManager = new FightManager(player, enemies);

        // Kampfschleife (bis du  die fight() Methode fertig hast benedikt)
        // Temporäre Implementation:
        while (player.lifeTotal > 0 && hasLivingEnemies(currentRoom)) {
            // System.out.println("\n-- Neue Kampfrunde --");

            // Spielerzug (vereinfacht für jetzt)
            playerTurn(currentRoom);

            // Gegnerzüge
            fightManager.fightTurn();

            // Status anzeigen
            showCombatStatus(currentRoom);
        }

        // TODO: Später ersetzen durch: return fightManager.fight();
        return player.lifeTotal > 0;
    }

    /**
     * Spielerzug im Kampf (vereinfacht) Kein ahnung ob das hier rein soll, habe
     * ich aber mal gemacht
     */
    private void playerTurn(Room room) {
        System.out.println("\n TEST: Wähle einen Gegner (1-" + room.enemyList.size() + "):");

        // Lebende Gegner anzeigen
        for (int i = 0; i < room.enemyList.size(); i++) {
            Enemy enemy = room.enemyList.get(i);
            if (enemy.lifeTotal > 0) {
                System.out.println((i + 1) + ". Gegner - HP: " + enemy.lifeTotal);
            }
        }
        System.out.print(">>  ");
        int choice = scanner.nextInt() - 1;
        if (choice >= 0 && choice < room.enemyList.size()) {
            Enemy target = room.enemyList.get(choice);

            // Frage nach Kampftaktik
            System.out.println("Taktik? (1=Normal, 2=Finte, 3=Wuchtschlag)");
            System.out.print(">>  ");
            int tactic = scanner.nextInt();

            switch (tactic) {
                case 2 ->
                    player.attack(target, 1, 0); // TESTZWEKCE: FINTE LVL 1
                case 3 ->
                    player.attack(target, 0, 1); // TESTZWECKE: WUCHTSCHLAG LVL 1
                default ->
                    player.attack(target, 0, 0); // TESTZWECKE: NORMALER ANGRIFF
            }
        }
    }

    /**
     * Prüft ob noch lebende Gegner vorhanden sind
     */
    private boolean hasLivingEnemies(Room room) {
        for (Enemy enemy : room.enemyList) {
            if (enemy.lifeTotal > 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Zeigt den Kampfstatus an
     */
    private void showCombatStatus(Room room) {
        System.out.println("\n TEST Spieler HP: " + player.lifeTotal + "/" + 100);
        int livingEnemies = 0;
        for (Enemy enemy : room.enemyList) {
            if (enemy.lifeTotal > 0) {
                livingEnemies++;
            }
        }
        System.out.println(" TEST Lebende Gegner: " + livingEnemies);
    }

    /**
     * Zeigt den Spielerstatus nach einem Kampf
     */
    private void showPlayerStatus() {
        System.out.println("\nTEST SPIELER STATUS");
        // Anzeigen von Spieler status
    }

    /**
     * Fragt ob der Spieler weitergehen möchte
     */
    private boolean askToContinue() {
        // TODO: nächste raum frage, vielleicht anderes konzept machbar falls wir abzweigende räume machen werden
        System.out.print(">>  ");
        String answer = scanner.next();
        return answer.equalsIgnoreCase("j");
    }

    /**
     * Story-Methoden (Platzhalter, bis Story.java implementiert ist)
     */
    private void tellStory(String storyKey) {
        // TODO: story.tellStory(storyKey);
        System.out.println("[Story: " + storyKey + "]");
    }

    private void tellRoomStory() {
        // Je nach Raum unterschiedliche Story
        // TODO: Implementierung mit Story Klasse
        switch (world.current_room_number) {
            case 0:
                System.out.println("TEST STORY ERSTER RAUM");
                break;
            case 7:
                System.out.println("TEST STORY LETZER RAUM");
                break;
            default:
                System.out.println("TEST STORY DEFAULT RAUM");
        }
    }

    private void gameOver() {
        // TODO: game over handeling
        gameRunning = false;
    }

    private void victory() {
        // TODO: sieg handeling
        gameRunning = false;
    }

    private void pauseGame() {
        // TODO: pause handeling
        gameRunning = false;
    }
}
