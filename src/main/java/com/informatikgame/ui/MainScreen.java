package com.informatikgame.ui;

import java.io.IOException;

import com.googlecode.lanterna.TerminalSize;
import com.googlecode.lanterna.TextColor;
import com.googlecode.lanterna.gui2.BasicWindow;
import com.googlecode.lanterna.gui2.Button;
import com.googlecode.lanterna.gui2.Direction;
import com.googlecode.lanterna.gui2.EmptySpace;
import com.googlecode.lanterna.gui2.Label;
import com.googlecode.lanterna.gui2.LinearLayout;
import com.googlecode.lanterna.gui2.MultiWindowTextGUI;
import com.googlecode.lanterna.gui2.Panel;
import com.googlecode.lanterna.gui2.Window;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

public class MainScreen {

    @SuppressWarnings("CallToPrintStackTrace")
    public static void show() {
        try {
            // Screen erstellen
            Screen screen = new DefaultTerminalFactory().createScreen();
            screen.startScreen();

            // Einfache GUI erstellen
            MultiWindowTextGUI gui = new MultiWindowTextGUI(screen);

            // Hauptfenster erstellen
            BasicWindow window = new BasicWindow();
            window.setHints(java.util.Set.of(Window.Hint.CENTERED));

            // Panel mit vertikalem Layout
            Panel panel = new Panel();
            panel.setLayoutManager(new LinearLayout(Direction.VERTICAL));

            // ASCII-Art Titel
            Label title = new Label("""
             ▓█████▄  █    ██  ███▄    █   ▄████ ▓█████  ▒█████   ███▄    █ 
             ▒██▀ ██▌ ██  ▓██▒ ██ ▀█   █  ██▒ ▀█▒▓█   ▀ ▒██▒  ██▒ ██ ▀█   █ 
             ░██   █▌▓██  ▒██░▓██  ▀█ ██▒▒██░▄▄▄░▒███   ▒██░  ██▒▓██  ▀█ ██▒
             ░▓█▄   ▌▓▓█  ░██░▓██▒  ▐▌██▒░▓█  ██▓▒▓█  ▄ ▒██   ██░▓██▒  ▐▌██▒
             ░▒████▓ ▒▒█████▓ ▒██░   ▓██░░▒▓███▀▒░▒████▒░ ████▓▒░▒██░   ▓██░
              ▒▒▓  ▒ ░▒▓▒ ▒ ▒ ░ ▒░   ▒ ▒  ░▒   ▒ ░░ ▒░ ░░ ▒░▒░▒░ ░ ▒░   ▒ ▒ 
              ░ ▒  ▒ ░░▒░ ░ ░ ░ ░░   ░ ▒░  ░   ░  ░ ░  ░  ░ ▒ ▒░ ░ ░░   ░ ▒░
              ░ ░  ░  ░░░ ░ ░    ░   ░ ░ ░ ░   ░    ░   ░ ░ ░ ▒     ░   ░ ░ 
                ░       ░              ░       ░    ░  ░    ░ ░           ░ 
              ░                                                             
                        """);
            title.setForegroundColor(TextColor.ANSI.GREEN);
            panel.addComponent(title);

            // Abstand
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

            // Untertitel
            Label subtitle = new Label("Ein Zombie-Survivel-Dungeon-Irgendwas-Spiel");
            subtitle.setForegroundColor(TextColor.ANSI.CYAN);
            panel.addComponent(subtitle);

            panel.addComponent(new EmptySpace(new TerminalSize(0, 2)));

            // Buttons
            panel.addComponent(new Button(">>  Neues Spiel starten", () -> {
                // Screen schließen und Spiel starten
                try {
                    screen.stopScreen();

                    // GameManager starten (gerade noch in einer anderen branch)
                    // GameManager gameManager = new GameManager();
                    // gameManager.startGame();
                    // Nach Spielende: Zurück zum Hauptmenü oder beenden
                    System.out.println("\nDrücke Enter um zu beenden...");
                    System.in.read();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }));

            panel.addComponent(new Button("Einstellungen", () -> {
                // Einstellungen (optional)
            }));

            panel.addComponent(new Button("Anleitung", () -> {
                // Anleitung (optional)
            }));

            panel.addComponent(new Button("Spiel beenden", () -> System.exit(0)));

            // Panel zum Fenster hinzufügen
            window.setComponent(panel);

            // Fenster anzeigen
            gui.addWindowAndWait(window);

            // Screen beenden
            screen.stopScreen();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
