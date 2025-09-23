import java.util.ArrayList;

public class Room {
    EnemyType[] enemiesInRoom;
    String roomName;
    
    // Constructor
    public Room(RoomType roomType)
    {
        this.enemiesInRoom = roomType.enemiesInRoom;
        this.roomName = roomType.name();
    }
    public Room(EnemyType[] enemiesInRoom, String roomName) {
        this.enemiesInRoom = enemiesInRoom;
        this.roomName = roomName;
    }
}