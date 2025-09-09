public class Enemy extends Character
{
    // Constructor
    public Enemy(EnemyType enemyType)
    {
        super(enemyType.lifeTotal, enemyType.armourValue, enemyType.initiative, enemyType.attack, enemyType.defense, enemyType.damage);
    }
    public Enemy(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage)
    {
        super(lifeTotal, armourValue, initiative, attack, defense, damage);
    }
}