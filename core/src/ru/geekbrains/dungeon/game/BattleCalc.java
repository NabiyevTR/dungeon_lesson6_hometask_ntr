package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.math.MathUtils;

public class BattleCalc {
    public static int attack(Unit attacker, Unit target) {
        int out = attacker.getDamage();
        out -= target.getDefence();
        if (out < 0) {
            out = 0;
        }
        return out;
    }

    public static int checkCounterAttack(Unit attacker, Unit target) {
        if (MathUtils.random() < 0.2f) {
            int amount = attack(target, attacker);
            return amount;
        }
        return 0;
    }
}
