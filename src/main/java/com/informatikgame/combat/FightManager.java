package com.informatikgame.combat;

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Scanner;

import com.informatikgame.entities.Character;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.Player;
import com.informatikgame.world.EnemyType;

public class FightManager {

    private final Player player;
    ArrayList<Enemy> enemiesLeftToRight;
    private final Scanner scanner;

    public FightManager(final Player player) {
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
        System.out.println("Fight starts!");

        for (int i = 1; (this.player.getLifeTotal() > 0 && !enemiesLeftToRight.isEmpty()); i++) {
            fightTurn(i);
        }
        //returns true when fight is won, else false
        return this.player.getLifeTotal() > 0;
    }

    public void printCurrentFightStatus(int roundCounter) {
        System.out.println("Round " + roundCounter + ":");
        System.out.println("");

        System.out.println("You have " + player.getLifeTotal() + " life.");
        System.out.println("");

        StringBuilder sb = new StringBuilder("In front of you are (left to right): ");

        int i = 0;
        for (Character enemy : this.enemiesLeftToRight) {
            if (i == 0) {
                sb.append(enemy.getType());
            } else {
                sb.append(", ").append(enemy.getType());
            }
            i++;
        }

        System.out.println(sb);
    }

    int[] getPlayerAction() {
        int[] action = new int[3];

        boolean validInput = false;
        while (!validInput) {
            System.out.println("Choose enemy to attack (1-" + this.enemiesLeftToRight.size() + ", counting from left):");
            System.out.print(">>  ");

            try {
                int choice = scanner.nextInt() - 1;
                if (choice >= 0 && choice < this.enemiesLeftToRight.size()) {
                    action[0] = choice;
                    validInput = true;
                } else {
                    System.out.print("Invalid input. Try again!");
                }
            } catch (Exception e) {
                System.out.println("Please enter a valid number");
                scanner.next(); // invalid input verwerfen
            }

        }

        validInput = false;
        while (!validInput) {
            System.out.println("Feint level (0-3):");
            System.out.print(">>  ");

            try {
                int choice = scanner.nextInt();
                if (choice >= 0 && choice <= 3) {
                    action[1] = choice;
                    validInput = true;
                } else {
                    System.out.println("Please enter a number between 0 and 3");
                }
            } catch (Exception e) {
                System.out.println("Please enter a valid number");
                scanner.next(); // invalid input verwerfen
            }

        }
        validInput = false;
        while (!validInput) {
            System.out.println("Forceful Blow level (0-3):");
            System.out.print(">>  ");

            try {
                int choice = scanner.nextInt();
                if (choice >= 0 && choice <= 3) {
                    action[2] = choice;
                    validInput = true;
                } else {
                    System.out.println("Please enter a number between 0 and 3");
                }
            } catch (Exception e) {
                System.out.println("Please enter a valid number");
                scanner.next(); // invalid input verwerfen
            }
        }
        return action;
    }

    @SuppressWarnings("null") // ignorieren wegen null warning in actingFighter.attack(this.player, randomNumberFinte, randomNumberWuchtschlag);
    public void fightTurn(int roundCounter) {
        printCurrentFightStatus(roundCounter);

        //Priority queue
        PriorityQueue<Character> actionQueue = new PriorityQueue<>(Character.initiativeComparator);

        actionQueue.add(this.player);
        for (Character enemy : this.enemiesLeftToRight) {
            actionQueue.add(enemy);
        }

        while (!actionQueue.isEmpty()) {
            Character actingFighter = actionQueue.poll();

            if (actingFighter instanceof Player) {
                int[] action = getPlayerAction();
                actingFighter.attack(this.enemiesLeftToRight.get(action[0]), action[1], action[2]);
            } else {
                // Lebt player target und existiert er
                if (this.player != null && this.player.isAlive()) {
                    int randomNumberFinte = (int) (Math.random() * 3);
                    int randomNumberWuchtschlag = (int) (Math.random() * 3);
                    actingFighter.attack(this.player, randomNumberFinte, randomNumberWuchtschlag);
                } else {
                    // player ist tot oder existiert nicht
                    // TODO error handling
                }
            }
        }
        this.enemiesLeftToRight.removeIf(character -> !character.isAlive());

    }

    public void close() {
        if (scanner != null) {
            scanner.close();
        }
    }

    public Player getPlayer() {
        return player;
    }
}
