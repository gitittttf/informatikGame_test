public enum UpgradeType {
    //int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel
    FINTE_UPGRADE(0, 0, 0, 0, 0, 0, 1, 0),
    LIFETOTAL_UPGRADE(3,0,0,0,0,0,0,0),
    ARMOURVALUE_UPGRADE(0,2,0,0,0,0,0,0),
    INITIATIVE_UPGRADE(0,0,3,0,0,0,0,0),
    ATTACK_UPGRADE(0,0,0,3,0,0,0,0),
    DEFENSE_UPGRADE(0,0,0,0,3,0,0,0),
    DAMAGE_UPGRADE(0,0,0,0,0,3,0,0),
    WUCHTSCHLAGLEVEL(0,0,0,0,0,0,0,2);
    

    public int lifeTotal;
    public int armourValue;
    public int initiative;
    public int attack;
    public int defense;
    public int damage;
    public int finteLevel;
    public int wuchtschlagLevel;

    UpgradeType(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int finteLevel, int wuchtschlagLevel) {
        this.lifeTotal = lifeTotal;
        this.armourValue = armourValue;
        this.initiative = initiative;
        this.attack = attack;
        this.defense = defense;
        this.damage = damage;
        this.finteLevel = finteLevel;
        this.wuchtschlagLevel = wuchtschlagLevel;
    }
}
