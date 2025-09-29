package com.informatikgame.entities;

import java.util.Comparator;

import com.informatikgame.world.UpgradeType;

public class Character {

    protected int lifeTotal;
    protected int maxLife;
    protected int armourValue;
    protected int initiative;
    private int randomizedInitiative;
    protected int attack;
    protected int defense;
    protected int damage;
    protected int numW6;
    protected int finteLevel;
    protected int wuchtschlagLevel;
    protected String type;

    // Constructor
    public Character(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int numW6, int finteLevel, int wuchtschlagLevel, String type) {
        this.lifeTotal = lifeTotal;
        this.maxLife = lifeTotal;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.setRandomizedInitiative(0);
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.numW6 = numW6;
        this.finteLevel = finteLevel;
        this.wuchtschlagLevel = wuchtschlagLevel;
        this.type = type;
    }

    public int getRandomizedInitiative() {
        return randomizedInitiative;

    }

    public void setRandomizedInitiative(int randomizedInitiative) {
        this.randomizedInitiative = randomizedInitiative;

    }

    //is alive
    public boolean isAlive() {
        return this.lifeTotal > 0;
    }

    // Listener für Combat Events
    private CombatEventListener combatEventListener;

    public void setCombatEventListener(CombatEventListener listener) {
        this.combatEventListener = listener;
    }

    public interface CombatEventListener {

        void onCombatMessage(String message, CombatMessageType type);

        void onCombatPause(int milliseconds);
    }

    // Import the message type enum from FightManager
    public enum CombatMessageType {
        ROUND_START,
        PLAYER_ACTION,
        ENEMY_ACTION,
        UPGRADE,
        SPECIAL_MOVE,
        DAMAGE,
        DEFENSE,
        COMBAT_START,
        COMBAT_END
    }

    //Attack target, Finte und Wuchtschlag Level 0-3 (Finte erschwert Angriff um 1, Verteidigung Gegner um 2, Wuchtschlag erschwert um 2 für +2 Schaden)
    public void attack(Character target, int finte, int wuchtschlag) {
        finte = clamp(finte, 0, this.finteLevel);
        wuchtschlag = clamp(wuchtschlag, 0, this.wuchtschlagLevel);
        int diceRoll = (int) (Math.random() * 19) + 1;

        // Announce attack start
        CombatMessageType actionType = this.type.equals("Spieler") ? CombatMessageType.PLAYER_ACTION : CombatMessageType.ENEMY_ACTION;
        logMessage(this.type + " startet den Angriff!", actionType);
        pauseCombat(800);

        // Announce special moves
        if (finte > 0) {
            logMessage("Versuchte eine Finte (Level " + finte + ") auszuführen...", CombatMessageType.SPECIAL_MOVE);
            pauseCombat(600);
        }
        if (wuchtschlag > 0) {
            logMessage("Versuchte einen Wuchtschlag (Level " + wuchtschlag + ") auszuführen...", CombatMessageType.SPECIAL_MOVE);
            pauseCombat(600);
        }

        if (diceRoll <= this.attack - finte - wuchtschlag * 2) {
            int defenseDebuff = 2 * finte;
            int damageBonus = 2 * wuchtschlag;

            //numW6 Würfelwürfe für Schaden
            for (int i = 0; i < this.numW6; i++) {
                damageBonus += (int) Math.round(Math.random() * 5 + 1);
            }

            // Success messages for special moves
            if (finte > 0) {
                logMessage("Die Finte hat geklappt! Der Gegner " + target.type + " ist verwirrt!", CombatMessageType.SPECIAL_MOVE);
                pauseCombat(700);
            }
            if (wuchtschlag > 0) {
                logMessage("Der Wuchtschlag hat geklappt! Zusätzlicher Schaden wird verursacht!", CombatMessageType.SPECIAL_MOVE);
                pauseCombat(700);
            }

            logMessage("Greife " + target.type + " an mit " + (this.damage + damageBonus) + " Schaden!", CombatMessageType.DAMAGE);
            pauseCombat(500);
            target.defense(this.damage + damageBonus, defenseDebuff);
        } else {
            // Failed attack messages
            if (finte > 0 || wuchtschlag > 0) {
                logMessage("Die Spezialangriffe sind fehlgeschlagen!", CombatMessageType.SPECIAL_MOVE);
                pauseCombat(600);
            }
            logMessage(this.type + " scheiterte " + target.type + " anzugreifen.", actionType);
        }
    }

    //Defense dmageTaken und Verteidigungsdebuff durch Finte
    public void defense(int damageTaken, int defenseDebuff) {
        int diceRoll = (int) (Math.random() * 20);

        logMessage(this.type + " versucht zu parieren...", CombatMessageType.DEFENSE);
        pauseCombat(600);

        if (diceRoll <= this.defense - defenseDebuff) {
            logMessage(this.type + " parriert erfolgreich!", CombatMessageType.DEFENSE);
            return;
        }

        int actualDamage = Math.max(0, damageTaken - this.armourValue);
        this.lifeTotal -= actualDamage;

        logMessage(this.type + " konnte nicht parieren!", CombatMessageType.DEFENSE);
        pauseCombat(400);
        logMessage(this.type + " nimmt " + actualDamage + " Schaden.", CombatMessageType.DAMAGE);
    }

    private void logMessage(String message, CombatMessageType type) {
        if (combatEventListener != null) {
            combatEventListener.onCombatMessage(message, type);
        } else {
            // Fallback to console if no listener
            System.out.println(message);
        }
    }

    private void pauseCombat(int milliseconds) {
        if (combatEventListener != null) {
            combatEventListener.onCombatPause(milliseconds);
        } else {
            // Fallback to thread sleep
            try {
                Thread.sleep(milliseconds);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    //Upgrade Character
    public void upgrade(int lifeTotal, int maxLife, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel) {
        StringBuilder upgradeMessage = new StringBuilder(this.type + " erhält Verbesserungen: ");
        boolean hasUpgrade = false;

        if (maxLife > 0) {
            this.maxLife += maxLife;
            upgradeMessage.append("Max HP +").append(maxLife).append(" ");
            hasUpgrade = true;
        }
        if (lifeTotal > 0) {
            this.lifeTotal += lifeTotal;
            upgradeMessage.append("HP +").append(lifeTotal).append(" ");
            hasUpgrade = true;
        }
        if (armourValue > 0) {
            this.armourValue += armourValue;
            upgradeMessage.append("Rüstung +").append(armourValue).append(" ");
            hasUpgrade = true;
        }
        if (initiative > 0) {
            this.initiative += initiative;
            upgradeMessage.append("Initiative +").append(initiative).append(" ");
            hasUpgrade = true;
        }
        if (attack > 0) {
            this.attack += attack;
            upgradeMessage.append("Angriff +").append(attack).append(" ");
            hasUpgrade = true;
        }
        if (defense > 0) {
            this.defense += defense;
            upgradeMessage.append("Verteidigung +").append(defense).append(" ");
            hasUpgrade = true;
        }
        if (damage > 0) {
            this.damage += damage;
            upgradeMessage.append("Schaden +").append(damage).append(" ");
            hasUpgrade = true;
        }
        if (finteLevel > 0) {
            this.finteLevel += finteLevel;
            upgradeMessage.append("Finte +").append(finteLevel).append(" ");
            hasUpgrade = true;
        }
        if (wuchtschlagLevel > 0) {
            this.wuchtschlagLevel += wuchtschlagLevel;
            upgradeMessage.append("Wuchtschlag +").append(wuchtschlagLevel).append(" ");
            hasUpgrade = true;
        }

        if (hasUpgrade) {
            logMessage(upgradeMessage.toString().trim(), CombatMessageType.UPGRADE);
        }
    }

    //Upgrade Character with Upgrade Type
    public void upgrade(UpgradeType upgradeType) {
        logMessage(this.type + " erhält das Upgrade: " + upgradeType.name(), CombatMessageType.UPGRADE);
        upgrade(upgradeType.lifeTotal, upgradeType.maxLife, upgradeType.armourValue, upgradeType.initiative, upgradeType.attack, upgradeType.defense, upgradeType.damage, upgradeType.finteLevel, upgradeType.wuchtschlagLevel);
    }

    // Test support Methoden für die Unit Tests
    public void takeDamage(int damageAmount) {
        int actualDamage = Math.max(0, damageAmount - armourValue);
        this.lifeTotal = Math.max(0, this.lifeTotal - actualDamage);
    }

    public void heal(int healAmount) {
        this.lifeTotal += healAmount;
    }

    //Utilities
    int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static Comparator<Character> initiativeComparator = (Character c1, Character c2) -> Integer.compare(c2.getRandomizedInitiative(), c1.getRandomizedInitiative());

    // ===== GETTER METHODEN =====
    public int getLifeTotal() {
        return lifeTotal;
    }

    public int getArmourValue() {
        return armourValue;
    }

    public int getArmorValue() {
        return armourValue;
    }  // Alias für Tests

    public int getInitiative() {
        return initiative;
    }

    public int getAttack() {
        return attack;
    }

    public int getDefense() {
        return defense;
    }

    public int getDamage() {
        return damage;
    }

    public int getNumW6() {
        return numW6;
    }

    public int getFinteLevel() {
        return finteLevel;
    }

    public int getWuchtschlagLevel() {
        return wuchtschlagLevel;
    }

    public String getType() {
        return type;
    }

    // ===== SETTER METHODEN =====
    public void setLifeTotal(int lifeTotal) {
        this.lifeTotal = lifeTotal;
    }

    public void setArmourValue(int armourValue) {
        this.armourValue = armourValue;
    }

    public void setInitiative(int initiative) {
        this.initiative = initiative;
    }

    public void setAttack(int attack) {
        this.attack = attack;
    }

    public void setDefense(int defense) {
        this.defense = defense;
    }

    public void setDamage(int damage) {
        this.damage = damage;
    }

    public void setNumW6(int numW6) {
        this.numW6 = numW6;
    }

    public void setFinteLevel(int finteLevel) {
        this.finteLevel = finteLevel;
    }

    public void setWuchtschlagLevel(int wuchtschlagLevel) {
        this.wuchtschlagLevel = wuchtschlagLevel;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getMaxLife() {
        return maxLife;
    }
}
