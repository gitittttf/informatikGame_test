package com.informatikgame.ui;

import java.io.IOException;

import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.swing.SwingTerminalFrame;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.informatikgame.ui.windows.SimpleMainMenuWindow;

/**
 * New GUI-based screen manager using Native Lanterna GUI Windows Provides
 * stable fullscreen with proper input handling
 */
public class GuiScreenManager {

    private static GuiScreenManager instance;
    private Terminal terminal;
    private Screen screen;
    private MultiWindowTextGUI textGUI;
    private boolean running = true;

    private GuiScreenManager() {
    }

    public static GuiScreenManager getInstance() {
        if (instance == null) {
            instance = new GuiScreenManager();
        }
        return instance;
    }

    public void initialize() throws IOException {
        try {
            // Try GUI first - Create terminal with SwingTerminalFrame for GUI support
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setPreferTerminalEmulator(true);

            terminal = factory.createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();

            // Create the GUI system
            textGUI = new MultiWindowTextGUI(screen);

            // Configure fullscreen if using SwingTerminalFrame
            if (terminal instanceof SwingTerminalFrame) {
                SwingTerminalFrame frame = (SwingTerminalFrame) terminal;

                // Configure for fullscreen with letterboxing
                configureFullscreen(frame);
            }

            System.out.println("GUI fullscreen initialized successfully!");

        } catch (Exception e) {
            System.out.println("GUI initialization failed, falling back to console mode: " + e.getMessage());

            // Fallback to console terminal
            DefaultTerminalFactory factory = new DefaultTerminalFactory();
            factory.setForceTextTerminal(true);

            terminal = factory.createTerminal();
            screen = new TerminalScreen(terminal);
            screen.startScreen();

            // Create the GUI system
            textGUI = new MultiWindowTextGUI(screen);

            System.out.println("Console GUI mode initialized as fallback.");
        }
    }

    private void configureFullscreen(SwingTerminalFrame frame) {
        // Use the direct approach - no component extraction needed!
        try {
            // Use reflection to access Swing methods since we can't import them directly
            Class.forName("javax.swing.WindowConstants");

            // Set close operation using reflection
            frame.getClass().getMethod("setDefaultCloseOperation", int.class).invoke(frame, 3); // EXIT_ON_CLOSE

            // Try to maximize the window
            frame.getClass().getMethod("setExtendedState", int.class).invoke(frame, 6); // MAXIMIZED_BOTH
            frame.setVisible(true);

            System.out.println("GUI fullscreen activated with maximized window!");
        } catch (Exception e) {
            System.out.println("Fullscreen setup failed: " + e.getMessage());
            frame.setVisible(true);
        }
    }

    public void run() throws IOException {
        // Show main menu
        showMainMenu();

        // Window closed, stop immediately
        running = false;
        shutdown();
    }

    public void showMainMenu() {
        SimpleMainMenuWindow mainMenu = new SimpleMainMenuWindow(textGUI);
        textGUI.addWindowAndWait(mainMenu); // This blocks until window closes
    }

    public void stop() {
        running = false;
    }

    private void shutdown() {
        try {
            if (screen != null) {
                screen.stopScreen();
            }
            if (terminal != null) {
                terminal.close();
            }
        } catch (Exception e) {
            System.err.println("Error during shutdown: " + e.getMessage());
        }
    }
}
