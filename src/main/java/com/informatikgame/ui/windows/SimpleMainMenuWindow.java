package com.informatikgame.ui.windows;

import java.util.Arrays;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;

/**
 * Simple Main Menu using Native Lanterna GUI Windows to test fullscreen
 * (Animations will be added back once basic functionality works)
 */
public class SimpleMainMenuWindow extends BasicWindow {

    private MultiWindowTextGUI textGUI;

    public SimpleMainMenuWindow(MultiWindowTextGUI textGUI) {
        super("Informatik Game");
        this.textGUI = textGUI;

        // Configure window for fullscreen
        setHints(Arrays.asList(Window.Hint.FULL_SCREEN, Window.Hint.NO_DECORATIONS));

        setupUI();
    }

    private void setupUI() {
        // Create main panel
        Panel mainPanel = new Panel();

        // Add some spacing and title
        mainPanel.addComponent(new EmptySpace(new TerminalSize(1, 5)));
        mainPanel.addComponent(new Label("== INFORMATIK GAME =="));
        mainPanel.addComponent(new Label("Von Paul, Benedikt und Simon"));
        mainPanel.addComponent(new EmptySpace(new TerminalSize(1, 3)));

        // Menu buttons
        mainPanel.addComponent(new Button("► Neues Spiel starten", () -> startNewGame()));
        mainPanel.addComponent(new Button("► Spiel fortsetzen", () -> continueGame()));
        mainPanel.addComponent(new Button("► Einstellungen", () -> openSettings()));
        mainPanel.addComponent(new Button("► Anleitung", () -> showHelp()));
        mainPanel.addComponent(new Button("► Credits", () -> showCredits()));
        mainPanel.addComponent(new Button("► Spiel beenden", () -> exitGame()));

        setComponent(mainPanel);
    }

    // Menu Actions
    private void startNewGame() {
        close();
        // TODO: Open GameplayWindow
        System.out.println("Starting new game...");
    }

    private void continueGame() {
        System.out.println("Continue game...");
    }

    private void openSettings() {
        System.out.println("Open settings...");
    }

    private void showHelp() {
        System.out.println("Show help...");
    }

    private void showCredits() {
        System.out.println("Show credits...");
    }

    private void exitGame() {
        System.out.println("Exiting game gracefully...");
        close();
        // Window closes automatically, GuiScreenManager will handle shutdown
    }
}
