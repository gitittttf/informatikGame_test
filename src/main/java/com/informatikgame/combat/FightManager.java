package com.informatikgame.combat;

import java.util.ArrayList;
import java.util.PriorityQueue;

import com.informatikgame.entities.Character;
import com.informatikgame.entities.Enemy;
import com.informatikgame.entities.Player;
import com.informatikgame.world.EnemyType;

public class FightManager {

    public interface CombatEventListener {
        void onRoundStart(int roundNumber);
        void onCombatMessage(String message);
        void onPlayerTurn();
        void onEnemyTurn(Enemy enemy);
        void onPlayerHealthUpdate(int current, int max);
        void onEnemyHealthUpdate(Enemy[] enemies);
        void onCombatEnd(boolean playerWon);
    }

    private Player player;
    private ArrayList<Enemy> enemiesLeftToRight;
    private CombatEventListener eventListener;
    private boolean waitingForPlayerAction;
    private int[] pendingPlayerAction;
    private int currentRound;
    private PriorityQueue<Character> currentActionQueue;
    private int playerMaxHP;

    public FightManager(Player player) {
        this.player = player;
        this.waitingForPlayerAction = false;
        this.currentRound = 0;
        this.playerMaxHP = player.getLifeTotal(); // Store initial HP as max
    }

    public void setCombatEventListener(CombatEventListener listener) {
        this.eventListener = listener;
    }

    public void startFight(EnemyType[] enemyTypesLeftToRight) {
        // Create list with all enemy objects for fight
        this.enemiesLeftToRight = new ArrayList<>(enemyTypesLeftToRight.length);
        for (EnemyType enemy : enemyTypesLeftToRight) {
            this.enemiesLeftToRight.add(new Enemy(enemy));
        }

        this.currentRound = 1;
        this.waitingForPlayerAction = false;
        
        // Notify GUI that combat has started
        if (eventListener != null) {
            eventListener.onCombatMessage("Der Kampf beginnt!");
            eventListener.onEnemyHealthUpdate(enemiesLeftToRight.toArray(new Enemy[0]));
        }

        // Start first round
        startRound();
    }

    private void startRound() {
        if (!player.isAlive() || enemiesLeftToRight.isEmpty()) {
            endFight();
            return;
        }

        if (eventListener != null) {
            eventListener.onRoundStart(currentRound);
            eventListener.onPlayerHealthUpdate(player.getLifeTotal(), playerMaxHP);
        }

        // Create priority queue for this round
        currentActionQueue = new PriorityQueue<>(Character.initiativeComparator);
        currentActionQueue.add(this.player);
        for (Enemy enemy : this.enemiesLeftToRight) {
            currentActionQueue.add(enemy);
        }

        processNextAction();
    }

    private void processNextAction() {
        if (currentActionQueue.isEmpty()) {
            // Round is finished, clean up dead enemies and start next round
            this.enemiesLeftToRight.removeIf(character -> !character.isAlive());
            
            // Update GUI with current enemy state after removing dead ones
            if (eventListener != null) {
                eventListener.onEnemyHealthUpdate(enemiesLeftToRight.toArray(new Enemy[0]));
            }
            
            currentRound++;
            startRound();
            return;
        }

        Character actingFighter = currentActionQueue.poll();

        // Check if fighter is still alive
        if (!actingFighter.isAlive()) {
            processNextAction(); // Skip to next action
            return;
        }

        if (actingFighter instanceof Player) {
            // Player's turn - wait for GUI input
            waitingForPlayerAction = true;
            if (eventListener != null) {
                eventListener.onPlayerTurn();
            }
        } else {
            // Enemy's turn - process automatically
            Enemy enemy = (Enemy) actingFighter;
            if (eventListener != null) {
                eventListener.onEnemyTurn(enemy);
            }
            
            // Enemy attacks with random abilities
            int randomNumberFinte = (int) (Math.random() * (enemy.getFinteLevel() + 1));
            int randomNumberWuchtschlag = (int) (Math.random() * (enemy.getWuchtschlagLevel() + 1));
            
            if (eventListener != null) {
                eventListener.onCombatMessage(enemy.getType() + " greift an!");
            }
            
            enemy.attack(this.player, randomNumberFinte, randomNumberWuchtschlag);
            
            if (eventListener != null) {
                eventListener.onPlayerHealthUpdate(player.getLifeTotal(), playerMaxHP);
            }

            // Continue with next action after a brief pause
            processNextAction();
        }
    }

    public void executePlayerAction(int targetEnemyIndex, int finteLevel, int wuchtschlagLevel) {
        if (!waitingForPlayerAction) {
            return; // Not waiting for input
        }

        // Validate input
        if (targetEnemyIndex < 0 || targetEnemyIndex >= enemiesLeftToRight.size()) {
            if (eventListener != null) {
                eventListener.onCombatMessage("Ungültiges Ziel!");
            }
            return;
        }

        if (finteLevel < 0 || finteLevel > player.getFinteLevel()) {
            if (eventListener != null) {
                eventListener.onCombatMessage("Ungültiger Finte-Level!");
            }
            return;
        }

        if (wuchtschlagLevel < 0 || wuchtschlagLevel > player.getWuchtschlagLevel()) {
            if (eventListener != null) {
                eventListener.onCombatMessage("Ungültiger Wuchtschlag-Level!");
            }
            return;
        }

        // Execute player attack
        Enemy target = enemiesLeftToRight.get(targetEnemyIndex);
        
        if (eventListener != null) {
            String action = "Angriff auf " + target.getType();
            if (finteLevel > 0) action += " (Finte Lv." + finteLevel + ")";
            if (wuchtschlagLevel > 0) action += " (Wuchtschlag Lv." + wuchtschlagLevel + ")";
            eventListener.onCombatMessage(action);
        }
        
        player.attack(target, finteLevel, wuchtschlagLevel);
        
        if (eventListener != null) {
            eventListener.onEnemyHealthUpdate(enemiesLeftToRight.toArray(new Enemy[0]));
        }

        waitingForPlayerAction = false;
        processNextAction();
    }

    private void endFight() {
        boolean playerWon = player.isAlive();
        if (eventListener != null) {
            eventListener.onCombatEnd(playerWon);
        }
    }

    // Method that GameManager can call to run the fight (for compatibility)
    public boolean fight(EnemyType[] enemyTypesLeftToRight) {
        startFight(enemyTypesLeftToRight);
        
        // For now, we'll simulate the fight synchronously for compatibility
        // In the future, this should be handled asynchronously through the GUI
        while (player.isAlive() && !enemiesLeftToRight.isEmpty() && !waitingForPlayerAction) {
            // This is a temporary solution - in a real GUI implementation,
            // the fight would be controlled by user input events
            try {
                Thread.sleep(100); // Small delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        
        return player.isAlive();
    }

    public boolean isWaitingForPlayerAction() {
        return waitingForPlayerAction;
    }

    // ===== GETTER METHODEN =====
    public Player getPlayer() {
        return player;
    }

    public ArrayList<Enemy> getEnemiesLeftToRight() {
        return enemiesLeftToRight;
    }
}
