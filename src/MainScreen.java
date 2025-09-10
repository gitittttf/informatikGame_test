
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
            title.setForegroundColor(TextColor.ANSI.BLACK);
            panel.addComponent(title);

            // Abstand
            panel.addComponent(new EmptySpace(new TerminalSize(0, 1)));

            // Buttons
            panel.addComponent(new Button("Spiel starten", () -> {
                // Hier später die Logik für Spielstart
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
