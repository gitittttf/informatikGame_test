package com.informatikgame.ui;

import java.awt.Font;
import java.awt.Frame;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.swing.WindowConstants;

import com.googlecode.lanterna.TerminalPosition;
import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.graphics.TextGraphics;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFontConfiguration;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;

/**
 * Zentrale Screen-Verwaltung für das gesamte Spiel. Verwaltet alle Screens und
 * ermöglicht nahtlose Übergänge mit Animationen
 */
public class ScreenManager {

    private static ScreenManager instance;
    private Terminal terminal;
    private Screen screen;
    private Map<String, GameScreen> screens;
    private GameScreen currentScreen;
    private TextGraphics graphics;
    private boolean running;
    private SwingTerminalFrame swingFrame;
    private int currentFontSize = 16;

    // Simple dynamic scaling
    private final AtomicBoolean isRecreating = new AtomicBoolean(false);
    private final int TARGET_COLUMNS = 120;
    private final int TARGET_ROWS = 40;
    private final int MIN_FONT_SIZE = 8;
    private final int MAX_FONT_SIZE = 32;
    private int resizeCheckCounter = 3; // Check every N frames

    // Farb-Theme für das Spiel
    public static final TextColor BACKGROUND_COLOR = TextColor.ANSI.BLACK;
    public static final TextColor PRIMARY_COLOR = TextColor.ANSI.GREEN;
    public static final TextColor SECONDARY_COLOR = TextColor.ANSI.CYAN;
    public static final TextColor DANGER_COLOR = TextColor.ANSI.RED;
    public static final TextColor WARNING_COLOR = TextColor.ANSI.YELLOW;
    public static final TextColor TEXT_COLOR = TextColor.ANSI.WHITE;

    private ScreenManager() {
        screens = new HashMap<>();
        running = true;
    }

    public static ScreenManager getInstance() {
        if (instance == null) {
            instance = new ScreenManager();
        }
        return instance;
    }

    /**
     * Initialisiert das Terminal im Vollbildmodus mit dynamischer Skalierung
     */
    public void initialize() throws IOException {
        // Terminal-Factory mit dynamischer Schriftgrößenberechnung
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setPreferTerminalEmulator(true);

        TerminalSize initialSize = new TerminalSize(TARGET_COLUMNS, TARGET_ROWS);
        terminalFactory.setInitialTerminalSize(initialSize);

        // Dynamische Schriftgrößenberechnung
        int fontSize = Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, TARGET_COLUMNS / 4));
        currentFontSize = fontSize;

        // Font-Konfiguration
        Font terminalFont = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        SwingTerminalFontConfiguration fontConfig = SwingTerminalFontConfiguration.newInstance(terminalFont);
        terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig);

        // Terminal erstellen
        terminal = terminalFactory.createTerminal();

        // Fullscreen konfigurieren
        if (terminal instanceof SwingTerminalFrame frame) {
            this.swingFrame = frame;
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            frame.setVisible(true);
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        }

        // Screen erstellen
        screen = new TerminalScreen(terminal);
        screen.startScreen();
        screen.setCursorPosition(null);

        // Graphics konfigurieren
        graphics = screen.newTextGraphics();
        graphics.setBackgroundColor(BACKGROUND_COLOR);
        graphics.setForegroundColor(TEXT_COLOR);

        clearScreen();
        System.out.println("Terminal mit dynamischer Schriftgröße " + fontSize + " gestartet - einfache Skalierung aktiv");
    }

    /**
     * Registriert einen neuen Screen
     */
    public void registerScreen(String name, GameScreen screen) {
        screens.put(name, screen);
        screen.setScreenManager(this);
    }

    /**
     * Wechselt zu einem anderen Screen mit Fade-Animation
     */
    public void switchToScreen(String screenName) {
        GameScreen newScreen = screens.get(screenName);
        if (newScreen != null) {
            // Fade-Out Animation
            if (currentScreen != null) {
                fadeOut();
            }

            currentScreen = newScreen;
            currentScreen.initialize();

            // Fade-In Animation
            fadeIn();
        }
    }

    /**
     * Hauptspiel-Loop
     */
    public void run() throws IOException {

        while (running && currentScreen != null) {
            // Bildschirm löschen
            clearScreen();

            // Aktuellen Screen rendern
            currentScreen.render(graphics);

            // Bildschirm aktualisieren
            screen.refresh();

            // Input verarbeiten
            KeyStroke keyStroke = screen.pollInput();
            if (keyStroke != null) {
                if (keyStroke.getKeyType() == KeyType.Escape) {
                    // ESC zum Beenden (kann angepasst werden)
                    if (currentScreen.onEscape()) {
                        running = false;
                    }
                } else {
                    currentScreen.handleInput(keyStroke);
                }
            }

            // Update-Logik (für Animationen)
            currentScreen.update();

            // Einfache dynamische Font-Skalierung
            checkForSimpleResize();

            // Frame-Rate begrenzen (ca. 30 FPS)
            try {
                Thread.sleep(33);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        shutdown();
    }

    /**
     * Bildschirm komplett schwarz färben
     */
    private void clearScreen() {
        TerminalSize size = screen.getTerminalSize();
        graphics.setBackgroundColor(BACKGROUND_COLOR);
        graphics.setForegroundColor(TEXT_COLOR);
        graphics.fillRectangle(new TerminalPosition(0, 0), size, ' ');
    }

    /**
     * Fade-In Animation
     */
    private void fadeIn() {
        // Einfache Fade-Animation durch schrittweises Zeichnen
        for (int i = 0; i < 10; i++) {
            clearScreen();
            graphics.setForegroundColor(new TextColor.RGB(
                    i * 25, i * 25, i * 25
            ));
            currentScreen.render(graphics);
            try {
                screen.refresh();
                Thread.sleep(50);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        graphics.setForegroundColor(TEXT_COLOR);
    }

    /**
     * Fade-Out Animation
     */
    private void fadeOut() {
        for (int i = 10; i >= 0; i--) {
            clearScreen();
            graphics.setForegroundColor(new TextColor.RGB(
                    i * 25, i * 25, i * 25
            ));
            currentScreen.render(graphics);
            try {
                screen.refresh();
                Thread.sleep(30);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Einfache dynamische Skalierung (alle 120 Frames = ~4 Sekunden)
     */
    private void checkForSimpleResize() {
        if (swingFrame == null || isRecreating.get()) {
            return;
        }

        resizeCheckCounter++;
        if (resizeCheckCounter < 120) {
            return; // Check every ~4 seconds

        }
        resizeCheckCounter = 0;

        try {
            TerminalSize currentTerminalSize = screen.getTerminalSize();

            // Einfache Neuberechnung basierend auf aktueller Terminal-Größe
            int newFontSize = Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, currentTerminalSize.getColumns() / 4));

            // Wenn sich Größe signifikant geändert hat
            if (Math.abs(newFontSize - currentFontSize) >= 3) {
                System.out.println("Fenstergrößenänderung erkannt - Schriftgröße von " + currentFontSize + " zu " + newFontSize);
                // Für jetzt nur loggen - volle Recreation kann später hinzugefügt werden
                currentFontSize = newFontSize;
            }

        } catch (Exception e) {
            System.err.println("Fehler beim Resize-Check: " + e.getMessage());
        }
    }

    /**
     * Beendet die GUI sauber
     */
    public void shutdown() {
        try {
            // Clean up terminal and screen
            if (screen != null) {
                screen.stopScreen();
            }
            if (terminal != null) {
                terminal.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // Getter für Screens
    public Screen getScreen() {
        return screen;
    }

    public TextGraphics getGraphics() {
        return graphics;
    }

    public TerminalSize getSize() {
        return screen.getTerminalSize();
    }

    public void stopRunning() {
        running = false;
    }
}
