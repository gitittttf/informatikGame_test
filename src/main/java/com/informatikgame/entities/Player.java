package com.informatikgame.entities;

public class Player extends Character
{
    // Constructor
    public Player(int lifeTotal, int armourValue, int initiative, int attack, int defense, int damage, int numW6, int finteLevel, int wuchtschlagLevel)
    {
        super(lifeTotal, armourValue, initiative, attack, defense, damage, numW6, finteLevel, wuchtschlagLevel, "Spieler");
    }
    public Player(PlayerType playerType)
    {
        super(playerType.lifeTotal, playerType.armourValue, playerType.initiative, playerType.attack, playerType.defense, playerType.damage, playerType.numW6, playerType.finteLevel, playerType.wuchtschlagLevel, "Spieler");
    }
}