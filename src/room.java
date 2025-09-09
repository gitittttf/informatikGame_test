
import java.util.ArrayList;

public class Room {

    ArrayList<Enemy> enemyList;

    // Constructor
    /**
     * Constructs a Room instance and populates it with enemies.
     *
     * @param enemiesInRoom An array of EnemyType representing the types of
     * enemies to be added to the room. For each EnemyType in the array, a new
     * Enemy instance is created and added to the room's enemy list.
     */
    public Room(EnemyType[] enemiesInRoom) {
        this.enemyList = new ArrayList<Enemy>(0);
        for (EnemyType enemy : enemiesInRoom) {
            enemyList.add(new Enemy(enemy));
        }
    }
}
