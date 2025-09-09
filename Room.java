import java.util.ArrayList;

public class Room {
    ArrayList<Enemy> enemyList;
    
    // Constructor
    public Room(RoomType roomType)
    {
        EnemyType[] enemiesInRoom = roomType.enemiesInRoom;
        this.enemyList = new ArrayList(enemiesInRoom.length);
        for (EnemyType enemy : enemiesInRoom) {
            enemyList.add(new Enemy(enemy));
        }
    }
    public Room(EnemyType[] enemiesInRoom) {
        this.enemyList = new ArrayList(enemiesInRoom.length);
        for (EnemyType enemy : enemiesInRoom) {
            enemyList.add(new Enemy(enemy));
        }
    }
}