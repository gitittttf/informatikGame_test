package com.informatikgame.entities;

public class Player extends Character {

    // Constructor
    public Player(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage) {
        super(lifeTotal, armourValue, initiative, attack, defense, damage, "Player");
    }

    public void takeDamage(int damageAmount) {
        lifeTotal -= damageAmount;
        if (this.lifeTotal < 0) {
            this.lifeTotal = 0;
        }
    }

    public void heal(int healAmount) {
        lifeTotal += healAmount;
    }
}
