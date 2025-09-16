
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class FightManager {

    Player player;
    ArrayList<Character> orderedFighters;

    public FightManager(Player player, Enemy[] enemies) {
        ArrayList<Character> orderedFighters = new ArrayList(enemies.length + 1);
        orderedFighters.add(player);
        for (int i = 0; i < enemies.length; i++) {
            orderedFighters.add(enemies[i]);
        }
        orderedFighters.sort(Comparator.comparingInt(fighter -> fighter.initiative));
        Collections.reverse(orderedFighters);
        this.player = player;
        this.orderedFighters = orderedFighters;
    }

    //TODO
    public void fightTurn() {
        for (Character fighter : orderedFighters) {
            if (fighter instanceof Player) {
                continue;
            } else {
                fighter.attack(this.player, 0, 0);
            }
        }
    }
}
