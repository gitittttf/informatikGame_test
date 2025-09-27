package com.informatikgame.core;

import java.io.IOException;

import com.informatikgame.ui.GuiScreenManager;

/**
 * New main class using Native Lanterna GUI Windows This provides stable
 * fullscreen with proper input handling
 */
public class GuiMain {

    @SuppressWarnings("CallToPrintStackTrace")
    public static void main(String[] args) {
        try {
            // Initialize GUI-based screen manager
            GuiScreenManager guiManager = GuiScreenManager.getInstance();
            guiManager.initialize();

            System.out.println("Starting GUI-based game with Native Lanterna Windows...");

            // Run the game
            guiManager.run();

        } catch (IOException e) {
            System.err.println("Error initializing GUI: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
