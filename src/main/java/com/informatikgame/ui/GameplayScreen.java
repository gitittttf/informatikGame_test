package com.informatikgame.ui;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.informatikgame.core.GameManager;
import com.informatikgame.entities.Enemy;

/**
 * Der Haupt-Gameplay-Screen - vollständig integriert mit GameManager
 */
public class GameplayScreen extends GameScreen implements GameManager.GameEventListener {

    private GameManager gameManager;
    private List<String> combatLog;
    private String currentRoomName = "";
    private String currentRoomDescription = "";
    private int currentRoomNumber = 0;
    private int totalRooms = 8;
    private boolean inCombat = false;
    @SuppressWarnings("FieldMayBeFinal")
    private int selectedAction = 0;
    private int selectedEnemy = 0;

    // Spieler-Stats
    private int playerHP = 100;
    private int playerMaxHP = 100;

    // Gegner-Liste
    private Enemy[] currentEnemies = new Enemy[0];

    public int getSelectedAction() {
        return selectedAction;
    }

    // UI-States
    private enum UIState {
        EXPLORATION, // Raum erkunden
        COMBAT, // Im Kampf
        ROOM_TRANSITION, // Zwischen Räumen
        GAME_OVER, // Spiel vorbei
        VICTORY        // Gewonnen
    }
    private UIState currentState = UIState.EXPLORATION;

    @Override
    public void initialize() {
        combatLog = new ArrayList<>();
        combatLog.add("=== Willkommen im Dungeon ===");

        // GameManager initialisieren und Listener setzen
        gameManager = new GameManager();
        gameManager.setEventListener(this);
        gameManager.startGame();
    }

    // === GameEventListener Implementation ===
    @Override
    public void onCombatLogUpdate(String message) {
        combatLog.add(message);
        // Nur die letzten 10 Nachrichten behalten
        if (combatLog.size() > 10) {
            combatLog.remove(0);
        }
    }

    @Override
    public void onRoomChange(int roomNumber, int totalRooms, String roomName) {
        this.currentRoomNumber = roomNumber;
        this.totalRooms = totalRooms;
        this.currentRoomName = roomName;
        this.currentRoomDescription = gameManager.getRoomDescription();
        currentState = UIState.EXPLORATION;

        combatLog.add(">>> Betreten: " + roomName);
    }

    @Override
    public void onPlayerHealthChange(int current, int max) {
        this.playerHP = current;
        this.playerMaxHP = max;
    }

    @Override
    public void onEnemyUpdate(Enemy[] enemies) {
        this.currentEnemies = enemies;
    }

    @Override
    public void onCombatStart() {
        currentState = UIState.COMBAT;
        inCombat = true;
        selectedEnemy = 0;
    }

    @Override
    public void onCombatEnd(boolean won) {
        inCombat = false;
        if (won) {
            currentState = UIState.ROOM_TRANSITION;
            combatLog.add(">>> Kampf gewonnen!");
        }
    }

    @Override
    public void onGameOver() {
        currentState = UIState.GAME_OVER;
    }

    @Override
    public void onVictory() {
        currentState = UIState.VICTORY;
    }

    @Override
    public void onPlayerAction(String action) {
        combatLog.add(">> " + action);
    }

    // === Render-Methoden ===
    @Override
    public void render(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();

        // Status-Bar immer anzeigen
        drawStatusBar(graphics);

        // Je nach State unterschiedliche Darstellung
        switch (currentState) {
            case GAME_OVER ->
                drawGameOverScreen(graphics);
            case VICTORY ->
                drawVictoryScreen(graphics);
            default -> {
                // Normales 3-Spalten-Layout
                int mainAreaY = 3;
                int columnWidth = size.getColumns() / 3;

                drawPlayerPanel(graphics, 0, mainAreaY, columnWidth, size.getRows() - mainAreaY - 5);
                drawGameArea(graphics, columnWidth, mainAreaY, columnWidth, size.getRows() - mainAreaY - 5);
                drawInfoPanel(graphics, columnWidth * 2, mainAreaY, columnWidth, size.getRows() - mainAreaY - 5);
                drawCombatLog(graphics, 0, size.getRows() - 5, size.getColumns(), 5);
            }
        }
    }

    private void drawStatusBar(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();

        // Hintergrund
        TextColor bgColor = inCombat ? new TextColor.RGB(30, 0, 0) : new TextColor.RGB(0, 30, 0);
        graphics.setBackgroundColor(bgColor);
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);

        for (int x = 0; x < size.getColumns(); x++) {
            graphics.setCharacter(x, 0, ' ');
            graphics.setCharacter(x, 1, ' ');
        }

        // Raum-Information
        String roomInfo = String.format("RAUM %d/%d - %s",
                currentRoomNumber + 1, totalRooms, currentRoomName);
        graphics.putString(new TerminalPosition(2, 0), roomInfo);

        // Status
        String status = inCombat ? "[KAMPF]" : "[ERKUNDUNG]";
        TextColor statusColor = inCombat ? TextColor.ANSI.RED : TextColor.ANSI.GREEN;
        graphics.setForegroundColor(statusColor);
        graphics.putString(new TerminalPosition(size.getColumns() / 2 - status.length() / 2, 0), status);

        // HP-Anzeige
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        String hp = String.format("HP: %d/%d", playerHP, playerMaxHP);
        graphics.putString(new TerminalPosition(size.getColumns() - hp.length() - 2, 0), hp);

        // Trennlinie
        graphics.setForegroundColor(ScreenManager.PRIMARY_COLOR);
        for (int x = 0; x < size.getColumns(); x++) {
            graphics.setCharacter(x, 2, '═');
        }
    }

    private void drawGameArea(TextGraphics graphics, int x, int y, int width, int height) {
        drawBox(graphics, x, y, width - 1, height,
                ScreenManager.PRIMARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        if (inCombat) {
            graphics.setForegroundColor(TextColor.ANSI.RED);
            graphics.putString(new TerminalPosition(x + 2, y), "[ KAMPFZONE ]");

            // Kampf-Animation
            drawCombatScene(graphics, x, y, width, height);
        } else {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            graphics.putString(new TerminalPosition(x + 2, y), "[ " + currentRoomName.toUpperCase() + " ]");

            // Raum-Beschreibung
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            String[] lines = wrapText(currentRoomDescription, width - 6);
            for (int i = 0; i < lines.length && i < 5; i++) {
                graphics.putString(new TerminalPosition(x + 3, y + 3 + i), lines[i]);
            }

            // Raum-ASCII-Art
            drawRoomVisualization(graphics, x, y + 10, width, height - 10);
        }
    }

    private void drawCombatScene(TextGraphics graphics, int x, int y, int width, int height) {
        int centerY = y + height / 2;

        // Spieler links (animiert)
        String[] playerArt = {
            " ╔═╗ ",
            " ║♦║ ",
            " ╠═╣ ",
            " ║║║ ",
            " ╚╩╝ "
        };

        // Spieler-Position (leicht wackelnd im Kampf)
        int playerX = x + 5;
        if (inCombat && animationFrame % 20 < 10) {
            playerX += 1;
        }

        graphics.setForegroundColor(ScreenManager.SECONDARY_COLOR);
        for (int i = 0; i < playerArt.length; i++) {
            graphics.putString(new TerminalPosition(playerX, centerY - 2 + i), playerArt[i]);
        }

        // Kampf-Effekt in der Mitte
        if (animationFrame % 15 < 8) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            String[] effects = {"⚔", "💥", "✦", "◈"};
            String effect = effects[(animationFrame / 15) % effects.length];
            graphics.putString(new TerminalPosition(x + width / 2 - 1, centerY), effect);
        }

        // Gegner rechts (wenn vorhanden)
        if (currentEnemies.length > selectedEnemy) {
            drawEnemyArt(graphics, x + width - 15, centerY - 2, currentEnemies[selectedEnemy]);
        }
    }

    private void drawEnemyArt(TextGraphics graphics, int x, int y, Enemy enemy) {
        String[] art;

        // Wähle Art basierend auf Enemy-Typ
        if (enemy.getType().contains("Mini")) {
            art = new String[]{
                " ╔═╗",
                " ║☠║",
                " ╚╬╝",
                "  ║ ",
                " ╱ ╲"
            };
        } else if (enemy.getType().contains("Scientist")) {
            art = new String[]{
                " ╔═╗",
                " ║◉║",
                " ╠═╣",
                " ║║║",
                " ╚╩╝"
            };
        } else if (enemy.getType().contains("Big")) {
            art = new String[]{
                "╔═══╗",
                "║ ☠ ║",
                "╠═══╣",
                "║║║║║",
                "╚╩═╩╝"
            };
        } else { // Boss
            art = new String[]{
                " ╔═══╗ ",
                "╔╣ ☠ ╠╗",
                "║╚═══╝║",
                "║ ║║║ ║",
                "╚═╩╩╩═╝"
            };
        }

        // Farbe basierend auf HP
        TextColor color;
        if (enemy.getLifeTotal() > enemy.getLifeTotal() * 0.66) {
            color = TextColor.ANSI.RED;
        } else if (enemy.getLifeTotal() > enemy.getLifeTotal() * 0.33) {
            color = TextColor.ANSI.YELLOW;
        } else {
            color = TextColor.ANSI.BLACK_BRIGHT;
        }

        graphics.setForegroundColor(color);
        for (int i = 0; i < art.length; i++) {
            graphics.putString(new TerminalPosition(x, y + i), art[i]);
        }
    }

    private void drawGameOverScreen(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();

        // Roter Bildschirm-Effekt
        graphics.setBackgroundColor(new TextColor.RGB(20, 0, 0));
        for (int y = 0; y < size.getRows(); y++) {
            for (int x = 0; x < size.getColumns(); x++) {
                graphics.setCharacter(x, y, ' ');
            }
        }

        String[] gameOverArt = {
            " ▄████  ▄▄▄       ███▄ ▄███▓▓█████     ▒█████   ██▒   █▓▓█████  ██▀███  ",
            "██▒ ▀█▒▒████▄    ▓██▒▀█▀ ██▒▓█   ▀    ▒██▒  ██▒▓██░   █▒▓█   ▀ ▓██ ▒ ██▒",
            "▒██░▄▄▄░▒██  ▀█▄  ▓██    ▓██░▒███      ▒██░  ██▒ ▓██  █▒░▒███   ▓██ ░▄█ ▒",
            "░▓█  ██▓░██▄▄▄▄██ ▒██    ▒██ ▒▓█  ▄    ▒██   ██░  ▒██ █░░▒▓█  ▄ ▒██▀▀█▄  ",
            "░▒▓███▀▒ ▓█   ▓██▒▒██▒   ░██▒░▒████▒   ░ ████▓▒░   ▒▀█░  ░▒████▒░██▓ ▒██▒"
        };

        graphics.setForegroundColor(TextColor.ANSI.RED);
        int artY = size.getRows() / 2 - 5;
        for (int i = 0; i < gameOverArt.length; i++) {
            drawCentered(graphics, gameOverArt[i], artY + i);
        }

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        drawCentered(graphics, "Du wurdest von den Zombies überwältigt...", artY + 7);

        if (animationFrame % 30 < 15) {
            graphics.setForegroundColor(TextColor.ANSI.YELLOW);
            drawCentered(graphics, "Drücke ESC für das Hauptmenü", artY + 10);
        }
    }

    private void drawVictoryScreen(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();

        // Goldener Hintergrund
        graphics.setBackgroundColor(new TextColor.RGB(20, 20, 0));
        for (int y = 0; y < size.getRows(); y++) {
            for (int x = 0; x < size.getColumns(); x++) {
                graphics.setCharacter(x, y, ' ');
            }
        }

        // Konfetti-Effekt
        for (int i = 0; i < 50; i++) {
            int confX = (int) (Math.random() * size.getColumns());
            int confY = (int) (Math.random() * size.getRows());
            graphics.setForegroundColor(new TextColor.RGB(
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255),
                    (int) (Math.random() * 255)
            ));
            graphics.setCharacter(confX, confY, '◆');
        }

        String[] victoryArt = {
            "██╗   ██╗██╗ ██████╗████████╗ ██████╗ ██████╗ ██╗   ██╗",
            "██║   ██║██║██╔════╝╚══██╔══╝██╔═══██╗██╔══██╗╚██╗ ██╔╝",
            "██║   ██║██║██║        ██║   ██║   ██║██████╔╝ ╚████╔╝ ",
            "╚██╗ ██╔╝██║██║        ██║   ██║   ██║██╔══██╗  ╚██╔╝  ",
            " ╚████╔╝ ██║╚██████╗   ██║   ╚██████╔╝██║  ██║   ██║   ",
            "  ╚═══╝  ╚═╝ ╚═════╝   ╚═╝    ╚═════╝ ╚═╝  ╚═╝   ╚═╝   "
        };

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        int artY = size.getRows() / 2 - 8;
        for (int i = 0; i < victoryArt.length; i++) {
            drawCentered(graphics, victoryArt[i], artY + i);
        }

        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        drawCentered(graphics, "Du hast den Dungeon gemeistert!", artY + 8);
        drawCentered(graphics, "Der Endboss wurde besiegt!", artY + 10);

        if (animationFrame % 30 < 15) {
            graphics.setForegroundColor(TextColor.ANSI.GREEN);
            drawCentered(graphics, "Drücke ESC für das Hauptmenü", artY + 13);
        }
    }

    // Rest der Methoden bleiben gleich...
    private void drawRoomVisualization(TextGraphics graphics, int x, int y, int width, int height) {
        // Dynamische Raum-Darstellung basierend auf currentRoomNumber
        String[][] roomArts = {
            // Eingang
            {
                "╔══════╪══════╗",
                "║      ▼      ║",
                "║  EINGANG    ║",
                "╚═════════════╝"
            },
            // Flur
            {
                "╔═════════════╗",
                "║ ░░░░░░░░░░░ ║",
                "║─────────────║",
                "╚═════════════╝"
            },
            // Bibliothek
            {
                "╔═════════════╗",
                "║ ▓▓▓ ▓▓▓ ▓▓▓║",
                "║ ▓▓▓ ▓▓▓ ▓▓▓║",
                "╚═════════════╝"
            }
        };

        String[] art = roomArts[Math.min(currentRoomNumber, roomArts.length - 1)];

        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        for (int i = 0; i < art.length && i < height; i++) {
            int centerX = x + (width - art[i].length()) / 2;
            graphics.putString(new TerminalPosition(centerX, y + i), art[i]);
        }
    }

    private String[] wrapText(String text, int width) {
        List<String> lines = new ArrayList<>();
        String[] words = text.split(" ");
        StringBuilder line = new StringBuilder();

        for (String word : words) {
            if (line.length() + word.length() + 1 > width) {
                lines.add(line.toString());
                line = new StringBuilder();
            }
            if (line.length() > 0) {
                line.append(" ");
            }
            line.append(word);
        }
        if (line.length() > 0) {
            lines.add(line.toString());
        }

        return lines.toArray(String[]::new);
    }

    @Override
    public void handleInput(KeyStroke keyStroke) {
        switch (currentState) {
            case GAME_OVER, VICTORY -> {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    screenManager.switchToScreen("menu");
                }
            }
            case ROOM_TRANSITION -> {
                if (keyStroke.getKeyType() == KeyType.Enter
                        || (keyStroke.getKeyType() == KeyType.Character
                        && keyStroke.getCharacter() == 'j')) {
                    gameManager.handlePlayerInput("J");
                }
            }

            case COMBAT ->
                handleCombatInput(keyStroke);

            case EXPLORATION -> {
                // Warte auf nächsten Raum
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    gameManager.advanceToNextRoom();
                }
            }
        }
    }

    @SuppressWarnings("unused") // erstmal, weil noch nicht fertig implementiert
    private void handleCombatInput(KeyStroke keyStroke) {
        // Combat input handling
    }

    @Override
    public boolean onEscape() {
        if (currentState == UIState.GAME_OVER || currentState == UIState.VICTORY) {
            screenManager.switchToScreen("menu");
            return false;
        }
        // Zeige Pause-Menü oder gehe zurück
        screenManager.switchToScreen("menu");
        return false;
    }

    @SuppressWarnings("unused") // erstmal, weil noch nicht fertig implementiert
    private void drawPlayerPanel(TextGraphics graphics, int x, int y, int width, int height) {
        // Implementation wie vorher...
    }

    @SuppressWarnings("unused") // erstmal, weil noch nicht fertig implementiert
    private void drawInfoPanel(TextGraphics graphics, int x, int y, int width, int height) {
        // Implementation wie vorher...
    }

    @SuppressWarnings("unused") // erstmal, weil noch nicht fertig implementiert
    private void drawCombatLog(TextGraphics graphics, int x, int y, int width, int height) {
        // Implementation wie vorher...
    }

    @SuppressWarnings("unused") // erstmal, weil noch nicht fertig implementiert
    private void drawHealthBar(TextGraphics graphics, int x, int y, int width,
            String label, int current, int max, TextColor color) {
        // Implementation wie vorher...
    }
}
