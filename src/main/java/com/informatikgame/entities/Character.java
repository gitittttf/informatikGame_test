package com.informatikgame.entities;

import java.util.Comparator;

public class Character {

    protected int lifeTotal;
    protected int armorValue;
    protected int initiative;
    protected int attack;
    protected int defense;
    protected int damage;
    protected String type;

    // Constructor
    public Character(int lifeTotal, int armorValue, int initiative, int attack, int defense, int damage, String type) {
        this.lifeTotal = lifeTotal;
        this.armorValue = armorValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.type = type;
    }

    //is alive
    public boolean isAlive() {
        return this.lifeTotal > 0;
    }

    //Attack target, Finte und Wuchtschlag Level 0-3 (Finte erschwert Angriff um 1, Verteidigung Gegner um 2, Wuchtschlag erschwert um 2 für +2 Schaden)
    public void attack(Character target, int finte, int wuchtschlag) {
        finte = clamp(finte, 0, 3);
        wuchtschlag = clamp(wuchtschlag, 0, 3);
        int diceRoll = (int) (Math.random() * 19) + 1;
        System.out.println("");
        if (diceRoll <= this.attack - finte - wuchtschlag * 2) {
            int defenseDebuff = 2 * finte;
            int damageBonus = 2 * wuchtschlag;
            System.out.println(this.type + " attacked " + target.type + " successfully (Feint: " + finte + ", Forceful Blow: " + wuchtschlag + ").");
            target.defense(this.damage + damageBonus, defenseDebuff);
        } else {
            System.out.println(this.type + " failed to attack the " + target.type + ".");
        }
    }

    //Defense dmageTaken und Verteidigungsdebuff durch Finte
    public void defense(int damageTaken, int defenseDebuff) {
        int diceRoll = (int) (Math.random() * 20);
        if (diceRoll <= this.defense - defenseDebuff) {
            System.out.println(this.type + " parried successfully.");
            return;
        }
        this.lifeTotal -= damageTaken - this.armorValue;
        System.out.println(this.type + " failed to parry.");
        System.out.println(this.type + " takes " + (damageTaken - this.armorValue) + " damage.");
    }

    //Utilities
    int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }

    public static Comparator<Character> initiativeComparator = (Character c1, Character c2) -> Integer.compare(c2.initiative, c1.initiative);

    public int getLifeTotal() {
        return lifeTotal;
    }

    public int getArmorValue() {
        return armorValue;
    }

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

    public String getType() {
        return type;
    }

}
