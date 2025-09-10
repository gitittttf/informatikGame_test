public enum EnemyType {
    //int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage
    MINI_ZOMBIE(10, 0, 12, 12, 4, 3);

    public int lifeTotal;
    public int armourValue;
    public int initiative;
    public int attack;
    public int defense;
    public int damage;

    EnemyType(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage) {
        this.lifeTotal = lifeTotal;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
    }
}