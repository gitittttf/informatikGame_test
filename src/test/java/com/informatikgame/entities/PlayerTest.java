package com.informatikgame.entities;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.informatikgame.world.EnemyType;

public class PlayerTest {

    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player(36, 4, 8, 15, 10, 8);
    }

    @Test
    public void testPlayerCreation() {
        assertNotNull(player);
        assertEquals(36, player.getLifeTotal());
        assertEquals(4, player.getArmorValue());
        assertEquals(8, player.getInitiative());
        assertEquals(15, player.getAttack());
        assertEquals("Player", player.getType());
    }

    @Test
    public void testPlayerTakeDamage() {
        int initialHealth = player.getLifeTotal();
        player.takeDamage(10);
        assertEquals(initialHealth - 10, player.getLifeTotal());
    }

    @Test
    public void testPlayerHeal() {
        player.takeDamage(20);
        player.heal(10);
        assertEquals(26, player.getLifeTotal()); // 36 - 20 + 10
    }

    @Test
    public void testPlayerAttacking() {
        // Test um zu gucken ob spieler angreifen kann ohne error
        assertDoesNotThrow(() -> {
            player.attack(new Enemy(EnemyType.MINI_ZOMBIE), 0, 0); // anpassbare werte
        });
    }
}
