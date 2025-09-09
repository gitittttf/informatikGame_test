
public class Character {

    boolean player;
    int lifeTotal;
    int armourValue;
    int initiative;
    int attack;
    int defense;
    int damage;

    /**
     * Constructs a Character with specified attributes.
     *
     * @param player true if the character is a player, false otherwise
     * @param lifeTotal the total life points of the character
     * @param armourValue the armour value providing damage reduction
     * @param initiative the initiative value determining turn order
     * @param attack the attack value used in combat
     * @param defense the defense value used in combat
     * @param damage the damage value inflicted during attacks
     */
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
    /**
     * Performs an attack on a target character, applying optional modifiers for
     * finte (feint) and wuchtschlag (power strike). The attack success is
     * determined by a dice roll and the attacker's stats, adjusted by the
     * modifiers. If successful, the target's defense is called with increased
     * damage and a debuff based on the modifiers.
     *
     * @param target The character to be attacked.
     * @param finte The feint modifier (0-3), reduces attack threshold and
     * increases defense debuff.
     * @param wuchtschlag The power strike modifier (0-3), reduces attack
     * threshold and increases damage.
     */
    public void attack(Character target, int finte, int wuchtschlag) {
        finte = clamp(finte, 0, 3);
        wuchtschlag = clamp(wuchtschlag, 0, 3);
        int diceRoll = (int) (Math.random() * 19) + 1;
        if (diceRoll <= this.attack - finte - wuchtschlag * 2) {
            int defenseDebuff = 2 * finte;
            int damageBonus = 2 * wuchtschlag;
            target.defense(this.damage + damageBonus, defenseDebuff);
        }
    }

    //Defense dmageTaken und Verteidigungsdebuff durch Finte
    /**
     * Calculates the result of a defense action when the character takes
     * damage. A dice roll is performed to determine if the character
     * successfully defends against the attack, factoring in any defense debuff.
     * If the defense fails, the character's life total is reduced by the damage
     * taken minus the armour value.
     *
     * @param damageTaken The amount of damage the character would take from the
     * attack.
     * @param defenseDebuff The debuff value that reduces the character's
     * defense for this calculation.
     */
    public void defense(int damageTaken, int defenseDebuff) {
        int diceRoll = (int) (Math.random() * 20);
        if (diceRoll <= this.defense - defenseDebuff) {
            return;
        }
        this.lifeTotal -= damageTaken - this.armourValue;
    }

    //Utilities
    /**
     * Clamps the given value between the specified minimum and maximum bounds.
     *
     * @param value the value to clamp
     * @param min the minimum bound
     * @param max the maximum bound
     * @return the clamped value, which will be no less than {@code min} and no
     * greater than {@code max}
     */
    int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
