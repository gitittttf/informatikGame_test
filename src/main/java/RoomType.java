public enum RoomType {
    //int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage
    ZOMBIE_ROOM(new EnemyType[]{EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE});
    INTRO_ROOM(new EnemyType[]{EnemyType.MINI_Zombie});
    FLOOR_ROOM(new EnemyType[]{EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE});
    LIBRARY_ROOM(new EnemyType[]{EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE, EnemyType.SCIENTIST});
    DININGHALL(new EnemyType[]{EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE, EnemyType.SCIENTIST, EnemyType.SCIENTIST});
    LABORATORY(new EnemyType[]{EnemyType.SCIENTIST, EnemyType.SCIENTIST, EnemyType.SCIENTIST, EnemyType.SCIENTIST});
    CORRIDOR(new EnemyType[]{EnemyType.BIG_ZOMBIE, EnemyType.BIG_ZOMBIE, EnemyType.BIG_ZOMBIE});
    PANTRY(new EnemyTpye[]{EnemyType.MINI_ZOMBIE, EnemyType.MINI_ZOMBIE});
    FINAL_ROOM(new EnemyType[]{EnemyType.ENDBOSS});


    public EnemyType[] enemiesInRoom;

    RoomType(EnemyType[] enemiesInRoom) {
        this.enemiesInRoom = enemiesInRoom;
    }
}
