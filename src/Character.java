import java.lang.Math;

public class Character {
    boolean player;
    int lifeTotal;
    int armourValue;
    int initiative;
    int attack;
    int defense;
    int damage;

    // Constructor
    public Character(boolean player, int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage) {
        this.player = player;
        this.lifeTotal = lifeTotal;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
    }

    //Attack target, Finte und Wuchtschlag Level 0-3 (Finte erschwert Angriff um 1, Verteidigung Gegner um 2, Wuchtschlag erschwert um 2 für +2 Schaden)
    public void attack(Character target, int finte, int wuchtschlag) {
        finte = clamp(finte, 0, 3);
        wuchtschlag = clamp(wuchtschlag, 0, 3);
        int diceRoll = (int)(Math.random() * 19) + 1;
        if (diceRoll <= this.attack - finte - wuchtschlag * 2) {
            int defenseDebuff = 2 * finte;
            int damageBonus = 2 * wuchtschlag;
            target.defense(this.damage + damageBonus, defenseDebuff);
        }
    }

    //Defense dmageTaken und Verteidigungsdebuff durch Finte
    public void defense(int damageTaken, int defenseDebuff) {
        int diceRoll = (int)(Math.random() * 20);
        if (diceRoll <= this.defense - defenseDebuff) {
            return;
        }
        this.lifeTotal -= damageTaken - this.armourValue;
    }

    //Utilities
    int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}