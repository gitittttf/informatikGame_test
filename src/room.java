
import java.util.ArrayList;

public class Room {

    ArrayList<Enemy> enemyList;

    public Room(String[] enemiesInRoom) {
        for (String enemy : enemiesInRoom) {
            enemyList = addEnemy(this.enemyList, enemy);
        }
    }

    ArrayList<Enemy> addEnemy(ArrayList<Enemy> enemyList, EnemyType enemy) {
        enemyList.add(new Enemy(enemy));
        return enemyList;
    }

}
