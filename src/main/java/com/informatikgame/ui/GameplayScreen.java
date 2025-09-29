package com.informatikgame.ui;

import java.util.ArrayList;
import java.util.List;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.informatikgame.combat.FightManager;
import com.informatikgame.core.GameManager;
import com.informatikgame.entities.Enemy;

/**
 * Der Haupt-Gameplay-Screen - vollständig integriert mit GameManager
 */
public class GameplayScreen extends GameScreen implements GameManager.GameEventListener {

    private GameManager gameManager;

    public void setGameManager(GameManager gameManager) {
        this.gameManager = gameManager;
    }
    private List<String> combatLog;
    private String currentRoomName = "";
    private String currentRoomDescription = "";
    private String currentStoryText = "";
    private int currentRoomNumber = 0;
    private int totalRooms = 8;

    // Story display state
    private int storyAnimationFrame = 0;
    private List<String> storyLines = new ArrayList<>();
    private int visibleStoryLines = 0;
    private boolean waitingForStoryInput = false;
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
        STORY_DISPLAY, // Story wird angezeigt (fullscreen)
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
        gameManager.setEventListener(this);
        gameManager.startGame();
    }

    // Combat message with color information
    private static class ColoredCombatMessage {

        public final String message;
        public final FightManager.CombatMessageType type;

        public ColoredCombatMessage(String message, FightManager.CombatMessageType type) {
            this.message = message;
            this.type = type;
        }
    }

    // Queued combat message with timestamp
    private static class QueuedCombatMessage {

        public final String message;
        public final FightManager.CombatMessageType type;
        public final long displayTime;

        public QueuedCombatMessage(String message, FightManager.CombatMessageType type, long displayTime) {
            this.message = message;
            this.type = type;
            this.displayTime = displayTime;
        }
    }

    private List<ColoredCombatMessage> coloredCombatLog = new ArrayList<>();
    private List<QueuedCombatMessage> messageQueue = new ArrayList<>();
    private long combatStartTime = 0;
    private long lastScheduledDisplayTime = 0;

    // === GameEventListener Implementation ===
    @Override
    public void onCombatLogUpdate(String message) {
        combatLog.add(message);
        coloredCombatLog.add(new ColoredCombatMessage(message, FightManager.CombatMessageType.PLAYER_ACTION));
        // Nur die letzten 15 Nachrichten behalten
        if (combatLog.size() > 15) {
            combatLog.remove(0);
        }
        if (coloredCombatLog.size() > 15) {
            coloredCombatLog.remove(0);
        }
    }

    // New method for colored combat messages
    public void onCombatMessage(String message, FightManager.CombatMessageType messageType) {
        displayMessage(message, messageType);
    }

    // New method for queued combat messages
    public void onQueuedCombatMessage(String message, FightManager.CombatMessageType messageType, long delayMs) {
        long currentTime = System.currentTimeMillis();
        // Ensure messages are always scheduled after the last scheduled message
        long displayTime = Math.max(currentTime, lastScheduledDisplayTime) + delayMs;
        lastScheduledDisplayTime = displayTime;
        messageQueue.add(new QueuedCombatMessage(message, messageType, displayTime));
    }

    private void displayMessage(String message, FightManager.CombatMessageType messageType) {
        combatLog.add(message);
        coloredCombatLog.add(new ColoredCombatMessage(message, messageType));
        // Nur die letzten 15 Nachrichten behalten
        if (combatLog.size() > 15) {
            combatLog.remove(0);
        }
        if (coloredCombatLog.size() > 15) {
            coloredCombatLog.remove(0);
        }
    }

    @Override
    public void onRoomChange(int roomNumber, int totalRooms, String roomName) {
        this.currentRoomNumber = roomNumber;
        this.totalRooms = totalRooms;
        this.currentRoomName = roomName;
        this.currentRoomDescription = gameManager.getRoomDescription();
        currentState = UIState.EXPLORATION;

        String message = ">>> Betreten: " + roomName;
        combatLog.add(message);
        coloredCombatLog.add(new ColoredCombatMessage(message, FightManager.CombatMessageType.ROUND_START));
    }

    @Override
    public void onPlayerHealthChange(int current, int max) {
        this.playerHP = current;
        this.playerMaxHP = max;
    }

    @Override
    public void onEnemyUpdate(Enemy[] enemies) {
        this.currentEnemies = enemies;

        // Clamp selected enemy index to valid range when enemy list changes
        if (selectedEnemyIndex >= enemies.length) {
            selectedEnemyIndex = Math.max(0, enemies.length - 1);
        }
    }

    @Override
    public void onCombatStart() {
        currentState = UIState.COMBAT;
        inCombat = true;
        selectedEnemy = 0;
        combatStartTime = System.currentTimeMillis();
        lastScheduledDisplayTime = combatStartTime;
        messageQueue.clear(); // Clear any previous queued messages
    }

    @Override
    public void onCombatEnd(boolean won) {
        inCombat = false;
        messageQueue.clear(); // Clear any remaining queued messages
        lastScheduledDisplayTime = 0; // Reset timeline
        if (won) {
            currentState = UIState.ROOM_TRANSITION;
            String message = ">>> Kampf gewonnen!";
            displayMessage(message, FightManager.CombatMessageType.COMBAT_END);
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
    public void onWaitingForRoomTransition() {
        currentState = UIState.ROOM_TRANSITION;
    }

    @Override
    public void onStoryDisplay(String storyText) {
        currentStoryText = storyText;
        currentState = UIState.STORY_DISPLAY;

        // Prepare story for animated display
        prepareStoryDisplay(storyText);
    }

    private void prepareStoryDisplay(String storyText) {
        // Split story into lines and prepare for animation
        storyLines.clear();
        String[] words = storyText.trim().split("\\s+");
        StringBuilder currentLine = new StringBuilder();
        int maxLineLength = 60; // characters per line

        for (String word : words) {
            if (currentLine.length() + word.length() + 1 > maxLineLength) {
                if (currentLine.length() > 0) {
                    storyLines.add(currentLine.toString());
                    currentLine = new StringBuilder();
                }
            }
            if (currentLine.length() > 0) {
                currentLine.append(" ");
            }
            currentLine.append(word);
        }
        if (currentLine.length() > 0) {
            storyLines.add(currentLine.toString());
        }

        // Reset animation state
        storyAnimationFrame = 0;
        visibleStoryLines = 0;
        waitingForStoryInput = false;
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
            case STORY_DISPLAY ->
                drawStoryDisplay(graphics);
            case GAME_OVER ->
                drawGameOverScreen(graphics);
            case VICTORY ->
                drawVictoryScreen(graphics);
            default -> {
                // Normales 3-Spalten-Layout
                int mainAreaY = 3;
                int columnWidth = size.getColumns() / 3;

                drawPlayerPanel(graphics, 0, mainAreaY, columnWidth, size.getRows() - mainAreaY - 15);
                drawGameArea(graphics, columnWidth, mainAreaY, columnWidth, size.getRows() - mainAreaY - 15);
                drawInfoPanel(graphics, columnWidth * 2, mainAreaY, columnWidth, size.getRows() - mainAreaY - 15);
                drawCombatLog(graphics, 0, size.getRows() - 15, size.getColumns(), 15); // TODO
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

        // Map Shortcutanzeige
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        String mapShortcut = "M - Map";
        graphics.putString(new TerminalPosition(size.getColumns() - mapShortcut.length() - 2, 0), mapShortcut);

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
            case STORY_DISPLAY -> {
                if (keyStroke.getKeyType() == KeyType.Enter) {
                    if (waitingForStoryInput) {
                        // Story finished, continue to room exploration
                        currentState = UIState.EXPLORATION;
                        gameManager.continueAfterStory();
                    } else if (visibleStoryLines >= storyLines.size()) {
                        // All lines visible, now wait for input
                        waitingForStoryInput = true;
                    }
                }
            }
            case GAME_OVER, VICTORY -> {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    screenManager.switchToScreen("menu");
                }
            }
            case ROOM_TRANSITION -> {
                if (keyStroke.getKeyType() == KeyType.Enter
                        || (keyStroke.getKeyType() == KeyType.Character
                        && (keyStroke.getCharacter() == 'j' || keyStroke.getCharacter() == 'J'))) {
                    gameManager.handlePlayerInput("J");
                } else if (keyStroke.getKeyType() == KeyType.Character
                        && (keyStroke.getCharacter() == 'n' || keyStroke.getCharacter() == 'N')) {
                    gameManager.handlePlayerInput("N");
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

    // Combat input state
    private enum CombatInputState {
        SELECTING_ENEMY,
        SELECTING_ATTACK_TYPE;
    }

    private CombatInputState combatInputState = CombatInputState.SELECTING_ENEMY;
    private int selectedEnemyIndex = 0;
    private FightManager.AttackType selectedAttackType = FightManager.AttackType.NORMAL;

    private void handleCombatInput(KeyStroke keyStroke) {
        if (currentEnemies.length == 0) {
            return; // No enemies to fight
        }

        switch (combatInputState) {
            case SELECTING_ENEMY -> {
                if (keyStroke.getKeyType() == KeyType.ArrowLeft && selectedEnemyIndex > 0) {
                    selectedEnemyIndex--;
                } else if (keyStroke.getKeyType() == KeyType.ArrowRight && selectedEnemyIndex < currentEnemies.length - 1) {
                    selectedEnemyIndex++;
                } else if (keyStroke.getKeyType() == KeyType.Character) {
                    // Allow number key selection
                    try {
                        int index = Character.getNumericValue(keyStroke.getCharacter()) - 1;
                        if (index >= 0 && index < currentEnemies.length) {
                            selectedEnemyIndex = index;
                        }
                    } catch (Exception e) {
                        // Invalid input, ignore
                    }
                } else if (keyStroke.getKeyType() == KeyType.Enter) {
                    combatInputState = CombatInputState.SELECTING_ATTACK_TYPE;
                    selectedAttackType = FightManager.AttackType.NORMAL;
                }
            }
            case SELECTING_ATTACK_TYPE -> {
                if (null != keyStroke.getKeyType()) {
                    switch (keyStroke.getKeyType()) {
                        case ArrowLeft -> // Cycle backwards through attack types
                            selectedAttackType = switch (selectedAttackType) {
                                case NORMAL ->
                                    FightManager.AttackType.WUCHTSCHLAG;
                                case FINTE ->
                                    FightManager.AttackType.NORMAL;
                                case WUCHTSCHLAG ->
                                    FightManager.AttackType.FINTE;
                            };
                        case ArrowRight -> // Cycle forwards through attack types
                            selectedAttackType = switch (selectedAttackType) {
                                case NORMAL ->
                                    FightManager.AttackType.FINTE;
                                case FINTE ->
                                    FightManager.AttackType.WUCHTSCHLAG;
                                case WUCHTSCHLAG ->
                                    FightManager.AttackType.NORMAL;
                            };
                        case Character -> {
                            // Allow number key selection: 1=Normal, 2=Finte, 3=Wuchtschlag
                            char ch = keyStroke.getCharacter();
                            switch (ch) {
                                case '1' ->
                                    selectedAttackType = FightManager.AttackType.NORMAL;
                                case '2' ->
                                    selectedAttackType = FightManager.AttackType.FINTE;
                                case '3' ->
                                    selectedAttackType = FightManager.AttackType.WUCHTSCHLAG;
                            }
                        }
                        case Enter -> {
                            // Execute the combat action
                            gameManager.executeCombatAction(selectedEnemyIndex, selectedAttackType);
                            combatInputState = CombatInputState.SELECTING_ENEMY;
                            selectedEnemyIndex = 0;
                            selectedAttackType = FightManager.AttackType.NORMAL;
                        }
                        case Escape ->
                            combatInputState = CombatInputState.SELECTING_ENEMY;
                        default -> {
                        }
                    }
                }
            }
        }
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

    private void drawPlayerPanel(TextGraphics graphics, int x, int y, int width, int height) {
        drawBox(graphics, x, y, width - 1, height,
                ScreenManager.PRIMARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(new TerminalPosition(x + 2, y), "[ SPIELER ]");

        // Player health bar
        drawHealthBar(graphics, x + 2, y + 2, width - 6,
                "HP", playerHP, playerMaxHP, TextColor.ANSI.RED);

        // Combat input instructions
        if (inCombat) {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            int instructionY = y + 4;

            switch (combatInputState) {
                case SELECTING_ENEMY -> {
                    graphics.putString(new TerminalPosition(x + 2, instructionY), "Wähle Gegner:");
                    graphics.putString(new TerminalPosition(x + 2, instructionY + 1), "← → oder 1-" + currentEnemies.length);
                    graphics.putString(new TerminalPosition(x + 2, instructionY + 2), "ENTER: Weiter");
                }
                case SELECTING_ATTACK_TYPE -> {
                    graphics.putString(new TerminalPosition(x + 2, instructionY), "Wähle Angriff:");
                    graphics.putString(new TerminalPosition(x + 2, instructionY + 1), "← → oder 1-3");
                    graphics.putString(new TerminalPosition(x + 2, instructionY + 2), "1=Normal 2=Finte 3=Wuchtschlag");
                    graphics.putString(new TerminalPosition(x + 2, instructionY + 3), "ENTER: Angriff! ESC: Zurück");

                    // Show current selection
                    String attackName = switch (selectedAttackType) {
                        case NORMAL ->
                            "Normal";
                        case FINTE ->
                            "Finte";
                        case WUCHTSCHLAG ->
                            "Wuchtschlag";
                    };
                    graphics.putString(new TerminalPosition(x + 2, instructionY + 4), "Gewählt: " + attackName);
                }
            }
        }
    }

    private void drawInfoPanel(TextGraphics graphics, int x, int y, int width, int height) {
        drawBox(graphics, x, y, width - 1, height,
                ScreenManager.PRIMARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        graphics.putString(new TerminalPosition(x + 2, y), "[ INFO ]");

        if (inCombat && currentEnemies.length > 0) {
            // Split the panel in half horizontally
            int topHalfHeight = (height / 2) - 3;
            int bottomHalfY = y + topHalfHeight;
            int bottomHalfHeight = height - topHalfHeight;

            // Draw horizontal separator line
            graphics.setForegroundColor(ScreenManager.PRIMARY_COLOR);
            for (int i = 1; i < width - 1; i++) {
                graphics.setCharacter(x + i - 1, bottomHalfY, '═');
            }
            graphics.setCharacter(x, bottomHalfY, '╠');
            graphics.setCharacter(x + width - 2, bottomHalfY, '╣');

            // Top half: Detailed enemy info for selected enemy
            if (selectedEnemyIndex >= 0 && selectedEnemyIndex < currentEnemies.length) {
                Enemy selectedEnemy = currentEnemies[selectedEnemyIndex];

                graphics.setForegroundColor(TextColor.ANSI.CYAN);
                graphics.putString(new TerminalPosition(x + 2, y + 2), "Aktueller Gegner:");

                // Enemy name/type
                graphics.setForegroundColor(TextColor.ANSI.WHITE);
                graphics.putString(new TerminalPosition(x + 2, y + 4), selectedEnemy.getType());

                // Enemy HP bar
                drawHealthBar(graphics, x + 2, y + 6, width - 6,
                        "HP", selectedEnemy.getLifeTotal(), selectedEnemy.getMaxLife(), TextColor.ANSI.RED);
            }

            // Bottom half: Enemy list
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(new TerminalPosition(x + 2, bottomHalfY + 1), "Alle Gegner:");
            for (int i = 0; i < currentEnemies.length; i++) {
                Enemy enemy = currentEnemies[i];
                TextColor color = (i == selectedEnemyIndex) ? TextColor.ANSI.CYAN : TextColor.ANSI.WHITE;
                graphics.setForegroundColor(color);

                String enemyInfo = String.format("%d. %s (%d HP)",
                        i + 1, enemy.getType(), enemy.getLifeTotal());
                int lineY = bottomHalfY + 3 + i;
                if (lineY < y + height - 1) { // Make sure we don't draw outside the box
                    graphics.putString(new TerminalPosition(x + 2, lineY), enemyInfo);
                }
            }
        } else {
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(new TerminalPosition(x + 2, y + 2), "Erkunde den Raum...");
        }
    }

    private void drawCombatLog(TextGraphics graphics, int x, int y, int width, int height) {
        drawBox(graphics, x, y, width, height,
                ScreenManager.SECONDARY_COLOR, ScreenManager.BACKGROUND_COLOR);

        graphics.setForegroundColor(TextColor.ANSI.CYAN);
        graphics.putString(new TerminalPosition(x + 2, y), "[ KAMPF-LOG ]");

        // Display recent combat log messages with color coding
        int logStartY = y + 1;
        int maxLines = height - 2;

        int startIndex = Math.max(0, coloredCombatLog.size() - maxLines);
        for (int i = startIndex; i < coloredCombatLog.size(); i++) {
            ColoredCombatMessage coloredMessage = coloredCombatLog.get(i);
            String message = coloredMessage.message;
            if (message.length() > width - 4) {
                message = message.substring(0, width - 7) + "...";
            }

            // Set color based on message type
            TextColor messageColor = getColorForMessageType(coloredMessage.type);
            graphics.setForegroundColor(messageColor);
            graphics.putString(new TerminalPosition(x + 2, logStartY + (i - startIndex)), message);
        }
    }

    private TextColor getColorForMessageType(FightManager.CombatMessageType type) {
        switch (type) {
            case ROUND_START:
                return TextColor.ANSI.CYAN;
            case PLAYER_ACTION:
                return TextColor.ANSI.GREEN;
            case ENEMY_ACTION:
                return TextColor.ANSI.RED;
            case UPGRADE:
                return TextColor.ANSI.MAGENTA;
            case SPECIAL_MOVE:
                return TextColor.ANSI.YELLOW;
            case DAMAGE:
                return new TextColor.RGB(255, 140, 0); // Orange
            case DEFENSE:
                return TextColor.ANSI.BLUE;
            case COMBAT_START:
                return new TextColor.RGB(255, 215, 0); // Gold
            case COMBAT_END:
                return new TextColor.RGB(0, 255, 127); // Spring green
            default:
                return TextColor.ANSI.WHITE;
        }
    }

    private void drawHealthBar(TextGraphics graphics, int x, int y, int width,
            String label, int current, int max, TextColor color) {
        // Draw label
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        graphics.putString(new TerminalPosition(x, y), label + ":");

        // Calculate bar width (leave space for label and numbers)
        int barWidth = width - label.length() - 10;
        int filledWidth = max > 0 ? (current * barWidth) / max : 0;

        // Draw health bar background
        graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
        for (int i = 0; i < barWidth; i++) {
            graphics.setCharacter(x + label.length() + 2 + i, y, '█');
        }

        // Draw filled portion
        graphics.setForegroundColor(color);
        for (int i = 0; i < filledWidth; i++) {
            graphics.setCharacter(x + label.length() + 2 + i, y, '█');
        }

        // Draw numbers
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        String numbers = current + "/" + max;
        graphics.putString(new TerminalPosition(x + label.length() + 3 + barWidth, y), numbers);
    }

    // Helper method to draw boxes
    protected void drawBox(TextGraphics graphics, int x, int y, int width, int height,
            TextColor borderColor, TextColor bgColor) {
        graphics.setForegroundColor(borderColor);
        graphics.setBackgroundColor(bgColor);

        // Draw corners
        graphics.setCharacter(x, y, '╔');
        graphics.setCharacter(x + width - 1, y, '╗');
        graphics.setCharacter(x, y + height - 1, '╚');
        graphics.setCharacter(x + width - 1, y + height - 1, '╝');

        // Draw horizontal lines
        for (int i = 1; i < width - 1; i++) {
            graphics.setCharacter(x + i, y, '═');
            graphics.setCharacter(x + i, y + height - 1, '═');
        }

        // Draw vertical lines
        for (int i = 1; i < height - 1; i++) {
            graphics.setCharacter(x, y + i, '║');
            graphics.setCharacter(x + width - 1, y + i, '║');
        }

        // Fill background
        for (int row = 1; row < height - 1; row++) {
            for (int col = 1; col < width - 1; col++) {
                graphics.setCharacter(x + col, y + row, ' ');
            }
        }
    }

    // Helper method to draw centered text
    protected void drawCentered(TextGraphics graphics, String text, int y) {
        TerminalSize size = screenManager.getSize();
        int x = (size.getColumns() - text.length()) / 2;
        graphics.putString(new TerminalPosition(x, y), text);
    }

    @Override
    public void update() {
        animationFrame++;

        // Process queued combat messages
        processQueuedMessages();

        // Update story animation
        if (currentState == UIState.STORY_DISPLAY && !waitingForStoryInput) {
            storyAnimationFrame++;
            // Show one new line every 30 frames (roughly 1 second at 30 FPS)
            if (storyAnimationFrame % 30 == 0 && visibleStoryLines < storyLines.size()) {
                visibleStoryLines++;
            }
        }
    }

    private void processQueuedMessages() {
        if (messageQueue.isEmpty()) {
            return;
        }

        long currentTime = System.currentTimeMillis();

        // Process messages that are ready to be displayed
        messageQueue.removeIf(queuedMessage -> {
            if (currentTime >= queuedMessage.displayTime) {
                displayMessage(queuedMessage.message, queuedMessage.type);
                return true; // Remove from queue
            }
            return false; // Keep in queue
        });
    }

    private void drawStoryDisplay(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();

        // Fill background with dark color
        graphics.setBackgroundColor(new TextColor.RGB(10, 10, 20));
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (int y = 0; y < size.getRows(); y++) {
            for (int x = 0; x < size.getColumns(); x++) {
                graphics.setCharacter(x, y, ' ');
            }
        }

        // Calculate story display area
        int storyWidth = Math.min(70, size.getColumns() - 10);
        int storyStartX = (size.getColumns() - storyWidth) / 2;
        int storyStartY = (size.getRows() - storyLines.size()) / 2 - 3;

        // Draw story border
        drawStoryBorder(graphics, storyStartX - 2, storyStartY - 2, storyWidth + 4, visibleStoryLines + 4);

        // Draw story text with animation
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        for (int i = 0; i < visibleStoryLines && i < storyLines.size(); i++) {
            String line = storyLines.get(i);
            int lineX = storyStartX + (storyWidth - line.length()) / 2; // Center each line
            graphics.putString(new TerminalPosition(lineX, storyStartY + i), line);
        }

        // Draw input prompt at bottom
        drawInputPrompt(graphics);
    }

    private void drawStoryBorder(TextGraphics graphics, int x, int y, int width, int height) {
        graphics.setForegroundColor(TextColor.ANSI.CYAN);

        // Draw decorative border
        for (int i = 0; i < width; i++) {
            graphics.setCharacter(x + i, y, '═');
            graphics.setCharacter(x + i, y + height - 1, '═');
        }
        for (int i = 0; i < height; i++) {
            graphics.setCharacter(x, y + i, '║');
            graphics.setCharacter(x + width - 1, y + i, '║');
        }

        // Corners
        graphics.setCharacter(x, y, '╔');
        graphics.setCharacter(x + width - 1, y, '╗');
        graphics.setCharacter(x, y + height - 1, '╚');
        graphics.setCharacter(x + width - 1, y + height - 1, '╝');

        // Add decorative elements
        graphics.setForegroundColor(TextColor.ANSI.YELLOW);
        for (int i = 2; i < width - 2; i += 4) {
            graphics.setCharacter(x + i, y, '◈');
            graphics.setCharacter(x + i, y + height - 1, '◈');
        }
    }

    private void drawInputPrompt(TextGraphics graphics) {
        TerminalSize size = screenManager.getSize();
        int promptY = size.getRows() - 4;
        int promptX = 4;
        int promptWidth = size.getColumns() - 8;

        // Draw prompt box background
        graphics.setBackgroundColor(new TextColor.RGB(0, 20, 40));
        graphics.setForegroundColor(TextColor.ANSI.CYAN);

        // Draw prompt border
        for (int i = 0; i < promptWidth; i++) {
            graphics.setCharacter(promptX + i, promptY, '═');
            graphics.setCharacter(promptX + i, promptY + 3, '═');
        }
        for (int i = 0; i < 4; i++) {
            graphics.setCharacter(promptX, promptY + i, '║');
            graphics.setCharacter(promptX + promptWidth - 1, promptY + i, '║');
        }

        // Corners
        graphics.setCharacter(promptX, promptY, '╔');
        graphics.setCharacter(promptX + promptWidth - 1, promptY, '╗');
        graphics.setCharacter(promptX, promptY + 3, '╚');
        graphics.setCharacter(promptX + promptWidth - 1, promptY + 3, '╝');

        // Fill background
        for (int y = promptY + 1; y < promptY + 3; y++) {
            for (int x = promptX + 1; x < promptX + promptWidth - 1; x++) {
                graphics.setCharacter(x, y, ' ');
            }
        }

        // Draw prompt text
        graphics.setForegroundColor(TextColor.ANSI.WHITE);
        String promptText;
        if (waitingForStoryInput) {
            promptText = "Drücke ENTER um fortzufahren";
            // Add blinking arrow
            if (animationFrame % 30 < 15) {
                graphics.setForegroundColor(TextColor.ANSI.YELLOW);
                graphics.putString(new TerminalPosition(promptX + 2, promptY + 1), "►");
            }
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(new TerminalPosition(promptX + 4, promptY + 1), promptText);
        } else if (visibleStoryLines >= storyLines.size()) {
            promptText = "Drücke ENTER um fortzufahren";
            graphics.setForegroundColor(TextColor.ANSI.GREEN);
            graphics.putString(new TerminalPosition(promptX + 2, promptY + 1), "►");
            graphics.setForegroundColor(TextColor.ANSI.WHITE);
            graphics.putString(new TerminalPosition(promptX + 4, promptY + 1), promptText);
        } else {
            promptText = "Story wird geladen ";
            graphics.setForegroundColor(TextColor.ANSI.BLACK_BRIGHT);
            graphics.putString(new TerminalPosition(promptX + 2, promptY + 1), promptText);

            // Add loading dots animation
            int dots = (animationFrame / 10) % 4;
            String dotString = ".".repeat(dots) + " ".repeat(3 - dots);
            graphics.putString(new TerminalPosition(promptX + promptText.length() + 3, promptY + 1), dotString);
        }
    }
}
