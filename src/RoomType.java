public enum RoomType {
    //int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage
    ZOMBIE_ROOM(new EnemyType[]{EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE});

    public EnemyType[] enemiesInRoom;

    RoomType(EnemyType[] enemiesInRoom) {
        this.enemiesInRoom = enemiesInRoom;
    }
}