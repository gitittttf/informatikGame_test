package com.informatikgame.ui;

import java.awt.Font;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
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

    // Display mode settings
    public enum DisplayMode {
        FULLSCREEN, WINDOWED
    }

    public enum ScalingMode {
        SMALL(0.5), NORMAL(1.0), LARGE(1.5), EXTRA_LARGE(2.0);

        private final double scaleFactor;

        ScalingMode(double scaleFactor) {
            this.scaleFactor = scaleFactor;
        }

        public double getScaleFactor() {
            return scaleFactor;
        }
    }

    private DisplayMode currentDisplayMode = DisplayMode.FULLSCREEN;
    private ScalingMode currentScalingMode = ScalingMode.NORMAL;

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
     * Initialisiert das Terminal im echten Vollbildmodus mit korrekter
     * Skalierung
     */
    public void initialize() throws IOException {
        initializeWithSettings(currentDisplayMode, currentScalingMode);
    }

    /**
     * Initialisiert das Terminal mit den gegebenen Display-Einstellungen
     */
    private void initializeWithSettings(DisplayMode displayMode, ScalingMode scalingMode) throws IOException {
        // Get screen dimensions
        GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int screenWidth = gd.getDisplayMode().getWidth();
        int screenHeight = gd.getDisplayMode().getHeight();

        // Calculate base font size for the target grid
        int baseFontWidthSize = (screenWidth - 100) / TARGET_COLUMNS;
        int baseFontHeightSize = (screenHeight - 150) / TARGET_ROWS;
        int baseFontSize = Math.min(baseFontWidthSize, baseFontHeightSize);

        // Apply scaling factor
        int fontSize = (int) (baseFontSize * scalingMode.getScaleFactor());
        fontSize = Math.max(MIN_FONT_SIZE, Math.min(MAX_FONT_SIZE, fontSize));
        currentFontSize = fontSize;

        // Terminal-Factory setup
        DefaultTerminalFactory terminalFactory = new DefaultTerminalFactory();
        terminalFactory.setPreferTerminalEmulator(true);

        TerminalSize initialSize = new TerminalSize(TARGET_COLUMNS, TARGET_ROWS);
        terminalFactory.setInitialTerminalSize(initialSize);

        // Font-Konfiguration mit berechneter Größe
        Font terminalFont = new Font(Font.MONOSPACED, Font.PLAIN, fontSize);
        SwingTerminalFontConfiguration fontConfig = SwingTerminalFontConfiguration.newInstance(terminalFont);
        terminalFactory.setTerminalEmulatorFontConfiguration(fontConfig);

        // Terminal erstellen
        terminal = terminalFactory.createTerminal();

        // Configure frame based on display mode
        if (terminal instanceof SwingTerminalFrame frame) {
            this.swingFrame = frame;
            frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

            // Dispose first to allow setUndecorated
            frame.dispose();

            if (displayMode == DisplayMode.FULLSCREEN) {
                configureFullscreenMode(frame, gd, screenWidth, screenHeight, fontSize);
            } else {
                configureWindowedMode(frame, screenWidth, screenHeight, fontSize, scalingMode);
            }

            frame.setVisible(true);
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
        System.out.println("Terminal-Setup abgeschlossen - Modus: " + displayMode + ", Skalierung: " + scalingMode + ", Schriftgröße: " + fontSize);
    }

    /**
     * Konfiguriert Vollbildmodus
     */
    private void configureFullscreenMode(SwingTerminalFrame frame, GraphicsDevice gd,
            int screenWidth, int screenHeight, int fontSize) {
        frame.setUndecorated(true);
        frame.setResizable(false);

        if (gd.isFullScreenSupported()) {
            try {
                gd.setFullScreenWindow(frame);
                System.out.println("Vollbildmodus aktiviert - " + screenWidth + "x" + screenHeight + ", Font: " + fontSize);
            } catch (Exception e) {
                System.out.println("Vollbildmodus fehlgeschlagen: " + e.getMessage() + ", verwende maximiert");
                frame.setExtendedState(Frame.MAXIMIZED_BOTH);
            }
        } else {
            System.out.println("Vollbildmodus nicht unterstützt, verwende maximiert - Font: " + fontSize);
            frame.setExtendedState(Frame.MAXIMIZED_BOTH);
        }
    }

    /**
     * Konfiguriert Fenstermodus mit zentriertem Spiel und Letterboxing
     */
    private void configureWindowedMode(SwingTerminalFrame frame, int screenWidth, int screenHeight,
            int fontSize, ScalingMode scalingMode) {
        frame.setUndecorated(false); // Decorated window for windowed mode
        frame.setResizable(true);    // Allow resizing in windowed mode

        // Calculate window size based on font size and target grid
        int gameWidth = (int) (TARGET_COLUMNS * fontSize * 0.6); // Approximate character width
        int gameHeight = (int) (TARGET_ROWS * fontSize * 1.2);   // Approximate character height

        // Add some padding
        int windowWidth = gameWidth + 50;
        int windowHeight = gameHeight + 100;

        // Center window on screen
        int windowX = (screenWidth - windowWidth) / 2;
        int windowY = (screenHeight - windowHeight) / 2;

        frame.setBounds(windowX, windowY, windowWidth, windowHeight);
        System.out.println("Fenstermodus aktiviert - Größe: " + windowWidth + "x" + windowHeight + ", Font: " + fontSize);
    }

    /**
     * Öffentliche Methoden für SettingsScreen
     */
    public boolean isFullscreenMode() {
        return currentDisplayMode == DisplayMode.FULLSCREEN;
    }

    public ScalingMode getCurrentScalingMode() {
        return currentScalingMode;
    }

    /**
     * Wendet neue Display-Einstellungen an und startet das Terminal neu
     */
    public void applyDisplaySettings(Object displayMode, Object scalingMode) {
        try {
            // Convert from SettingsScreen enums to ScreenManager enums
            DisplayMode newDisplayMode = DisplayMode.valueOf(displayMode.toString());
            ScalingMode newScalingMode = ScalingMode.valueOf(scalingMode.toString());

            // Store new settings
            currentDisplayMode = newDisplayMode;
            currentScalingMode = newScalingMode;

            // Shutdown current terminal
            shutdownTerminal();

            // Reinitialize with new settings
            initializeWithSettings(newDisplayMode, newScalingMode);

            System.out.println("Einstellungen angewendet - Modus: " + newDisplayMode + ", Skalierung: " + newScalingMode);

        } catch (Exception e) {
            System.err.println("Fehler beim Anwenden der Einstellungen: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Beendet nur das Terminal (ohne die Anwendung zu beenden)
     */
    private void shutdownTerminal() {
        try {
            // Exit fullscreen mode first
            GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
            if (gd.getFullScreenWindow() != null) {
                gd.setFullScreenWindow(null);
            }

            // Clean up terminal and screen
            if (screen != null) {
                screen.stopScreen();
            }
            if (terminal != null) {
                terminal.close();
            }
        } catch (Exception e) {
            System.err.println("Fehler beim Beenden des Terminals: " + e.getMessage());
        }
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
     * Resize-Check deaktiviert da Fenster nicht mehr verändert werden kann
     * (Vollbildmodus ohne Dekoration, Größenänderung deaktiviert)
     */
    private void checkForSimpleResize() {
        // Resize-Check deaktiviert da Vollbildmodus mit fester Größe
        // Das verhindert das "Herumspringen" der ASCII-Zeichen
        return;
    }

    /**
     * Beendet die GUI sauber
     */
    public void shutdown() {
        shutdownTerminal();
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
