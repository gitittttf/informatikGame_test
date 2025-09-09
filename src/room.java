import java.util.ArrayList;

public class Room {
    ArrayList<Enemy> enemyList;
    
    // Constructor
    public Room(EnemyType[] enemiesInRoom) {
        this.enemyList = new ArrayList<Enemy>(0);
        for (EnemyType enemy : enemiesInRoom) {
            enemyList = addEnemy(this.enemyList, enemy);
        }
    }
    
    //add Enemy to list
    ArrayList<Enemy> addEnemy(ArrayList<Enemy> enemyList, EnemyType enemy) {
        enemyList.add(new Enemy(enemy));
        return enemyList;
    }
}