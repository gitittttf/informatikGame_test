
import java.util.Scanner;

public class MainScreen {

    public static void show() {
        try {
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            // ASCII-Art (funktioniert nur mit UTF-8-Konsole)
            System.out.println("""
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
            Thread.sleep(3000);

            // Clear Screen für Windows
            new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();

            System.out.println("----------------------------------------------------------------");
            System.out.println("WILKOMMEN ZUM DUNGEON GAME");
            Thread.sleep(2000);
            System.out.println("Verfügbare commands:");
            Thread.sleep(1000);
            System.out.println("n - Neues Spiel Starten");
            Thread.sleep(1000);
            System.out.println("x - Spiel beenden");
            Thread.sleep(500);

            inputCommand();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void inputCommand() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print(">>  ");
            String input = scanner.nextLine();
            switch (input) {
                case "n" -> {
                    System.out.println("Starte neues Spiel...");
                    // Spiel starten
                    GameManager gameManager = new GameManager();

                    gameManager.startGame();
                }
                case "x" -> {
                    System.out.println("Beende Spiel...");
                    return;
                }
                default ->
                    System.out.println("Ungültige Eingabe! Erneut versuchen:");
            }
        }
    }
}
