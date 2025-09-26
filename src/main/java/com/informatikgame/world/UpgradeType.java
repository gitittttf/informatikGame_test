package com.informatikgame.world;

public enum UpgradeType {
<<<<<<< HEAD
    //int lifeTotal, int maxLife, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel
=======
    //int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel
>>>>>>> 6dd8e08b464ee04de95fe397b88be93771b3e5c4
    FINTE_UPGRADE(0, 0, 0, 0, 0, 0, 0, 1, 0);

    public int lifeTotal;
    public int maxLife;
    public int armourValue;
    public int initiative;
    public int attack;
    public int defense;
    public int damage;
    public int finteLevel;
    public int wuchtschlagLevel;

    UpgradeType(int lifeTotal, int maxLife, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel) {
        this.lifeTotal = lifeTotal;
        this.maxLife = maxLife;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.finteLevel = finteLevel;
        this.wuchtschlagLevel = wuchtschlagLevel;
    }
}
