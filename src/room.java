
import java.util.ArrayList;

public class room {

    ArrayList<Character> enemyList;

    public room(String[] enemiesInRoom) {
        for (String enemy : enemiesInRoom) {
            enemyList = addEnemy(this.enemyList, enemy);
        }
    }

    ArrayList<Character> addEnemy(ArrayList<Character> enemyList, String enemy) {
        enemyList.add(new Character(EnemyType.MINI_ZOMBIE));
        return enemyList;
    }

}
