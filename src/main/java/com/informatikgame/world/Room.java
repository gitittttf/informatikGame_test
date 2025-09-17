package com.informatikgame.world;

public class Room {

    EnemyType[] enemiesInRoom;

    // Constructor
    public Room(RoomType roomType) {
        this.enemiesInRoom = roomType.enemiesInRoom;
    }

    public Room(EnemyType[] enemiesInRoom) {
        this.enemiesInRoom = enemiesInRoom;
    }
}
