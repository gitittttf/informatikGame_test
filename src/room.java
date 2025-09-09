import java.util.ArrayList;

public class Room {
    ArrayList<Enemy> enemyList;
    
    // Constructor
    public Room(EnemyType[] enemiesInRoom) {
        this.enemyList = new ArrayList<Enemy>(0);
        for (EnemyType enemy : enemiesInRoom) {
            enemyList.add(new Enemy(enemy));
        }
    }
}