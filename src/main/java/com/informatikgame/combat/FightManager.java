package com.informatikgame.combat;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

import com.informatikgame.entities.Character;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.Player;
import com.informatikgame.world.EnemyType;

public class FightManager {

    Player player;
    ArrayList<Enemy> enemiesLeftToRight;
    @SuppressWarnings({"unused", "FieldMayBeFinal"})
    private Scanner scanner;

    public FightManager(Player player) {
        this.player = player;
        this.scanner = new Scanner(System.in);
    }

    public boolean fight(EnemyType[] enemyTypesLeftToRight) {
        //Create list with all enemy objects enemiesLeftToRight for fight
        this.enemiesLeftToRight = new ArrayList<>(enemyTypesLeftToRight.length);
        for (EnemyType enemy : enemyTypesLeftToRight) {
            this.enemiesLeftToRight.add(new Enemy(enemy));
        }

        System.out.println("");
        System.out.println("Der Kampf beginnt!");

        for (int i = 1; (this.player.isAlive() && !enemiesLeftToRight.isEmpty()); i++) {
            fightTurn(i);
        }

        //returns true when fight is won, else false
        return this.player.isAlive();
    }

    public void printCurrentFightStatus(int roundCounter) {
        System.out.println("");
        System.out.println("Runde " + roundCounter + ":");
        System.out.println("");

        System.out.println("Du hast " + player.getLifeTotal() + " Leben.");
        System.out.println("");

        StringBuilder sb = new StringBuilder("Vor dir stehen (von links nach rechts): ");

        int i = 0;
        for (Enemy enemy : this.enemiesLeftToRight) {
            if (i == 0) {
                sb.append(enemy.getType()).append(" (").append(enemy.getLifeTotal()).append(" Leben)");
            } else {
                sb.append(", ").append(enemy.getType()).append(" (").append(enemy.getLifeTotal()).append(" Leben)");
            }
            i++;
        }

        System.out.println(sb);
    }

    int[] getPlayerAction() {
        int[] action = new int[3];

        Scanner scanner = new Scanner(System.in);
        boolean validInput = false;

        System.out.print("");
        while (!validInput) {
            System.out.println("Wen möchtest du angreifen (1-" + this.enemiesLeftToRight.size() + ", von links):");
            System.out.print(">>  ");

            try {
                int choice = scanner.nextInt() - 1;
                if (choice >= 0 && choice < this.enemiesLeftToRight.size()) {
                    action[0] = choice;
                    validInput = true;
                } else {
                    System.out.println("Ungültige Eingabe. Versuch es nochmal");
                }
            } catch (Exception e) {
                System.out.println("Ungültige Eingabe. Bitte eine Zahl eingeben!");
                scanner.nextLine(); // Clear buffer
            }
        }

        validInput = false;
        while (!validInput) {
            System.out.println("Finte Level (0-" + this.player.getFinteLevel() + "):");
            System.out.print(">>  ");

            try {
                int choice = scanner.nextInt();
                if (choice >= 0 && choice <= this.player.getFinteLevel()) {
                    action[1] = choice;
                    validInput = true;
                } else {
                    System.out.println("Ungültige Eingabe. Versuch es nochmal");
                }
            } catch (Exception e) {
                System.out.println("Ungültige Eingabe. Bitte eine Zahl eingeben");
                scanner.nextLine(); // Clear buffer
            }
        }

        validInput = false;
        while (!validInput) {
            System.out.println("Wuchtschlag Level (0-" + this.player.getWuchtschlagLevel() + "):");
            System.out.print(">>  ");

            try {
                int choice = scanner.nextInt();
                if (choice >= 0 && choice <= this.player.getWuchtschlagLevel()) {
                    action[2] = choice;
                    validInput = true;
                } else {
                    System.out.println("Ungültige Eingabe. Versuch es nochmal");
                }
            } catch (Exception e) {
                System.out.println("Ungültige Eingabe. Bitte eine Zahl eingeben");
                scanner.nextLine(); // Clear buffer
            }
        }
        return action;
    }

    public void fightTurn(int roundCounter) {
        printCurrentFightStatus(roundCounter);

        //Priority queue
        PriorityQueue<Character> actionQueue = new PriorityQueue<>(Character.initiativeComparator);

        actionQueue.add(this.player);
        for (Enemy enemy : this.enemiesLeftToRight) {
            actionQueue.add(enemy);
        }

        while (!actionQueue.isEmpty()) {
            Character actingFighter = actionQueue.poll();

            // prüfen ob kämpfer lbet
            if (!actingFighter.isAlive()) {
                continue;
            }

            if (actingFighter instanceof Player) {
                int[] action = getPlayerAction();
                Enemy target = this.enemiesLeftToRight.get(action[0]);
                actingFighter.attack(target, action[1], action[2]);
            } else {
                // Gegner greift an (nur wenn noch lebendig)
                Enemy enemy = (Enemy) actingFighter;
                if (enemy.isAlive()) {
                    int randomNumberFinte = (int) (Math.random() * (enemy.getFinteLevel() + 1));
                    int randomNumberWuchtschlag = (int) (Math.random() * (enemy.getWuchtschlagLevel() + 1));
                    enemy.attack(this.player, randomNumberFinte, randomNumberWuchtschlag);
                }
            }
        }
        // tote entfernen
        this.enemiesLeftToRight.removeIf(character -> !character.isAlive());
    }

    // ===== GETTER METHODEN =====
    public Player getPlayer() {
        return player;
    }

    public ArrayList<Enemy> getEnemiesLeftToRight() {
        return enemiesLeftToRight;
    }
}
