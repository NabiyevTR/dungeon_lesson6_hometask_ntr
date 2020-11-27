package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.MathUtils;
import lombok.Data;
import ru.geekbrains.dungeon.screens.ScreenManager;

import java.util.ArrayList;
import java.util.List;

@Data
public class UnitController {
    private GameController gc;
    private MonsterController monsterController;
    private Hero hero;
    private Unit currentUnit;
    private int index;
    private List<Unit> allUnits;

    public boolean isItMyTurn(Unit unit) {
        return currentUnit == unit;
    }

    public boolean isCellFree(int cellX, int cellY) {
        for (int i = 0; i < allUnits.size(); i++) {
            Unit u = allUnits.get(i);
            if (u.getCellX() == cellX && u.getCellY() == cellY) {
                return false;
            }
        }
        return true;
    }

    public UnitController(GameController gc) {
        this.gc = gc;
        this.allUnits = new ArrayList<>();
        this.hero = new Hero(gc);
        this.monsterController = new MonsterController(gc);
    }

    public void init(int monsterCount) {
        this.allUnits.add(hero);
        for (int i = 0; i < monsterCount; i++) {
            this.createMonsterInRandomCell();
        }
        this.index = -1;
        this.nextTurn();
    }

    public void startRound() {
        for (int i = 0; i < getAllUnits().size(); i++) {
            getAllUnits().get(i).startRound();
        }
    }

    public void nextTurn() {
        index++;
        if (index >= allUnits.size()) {
            index = 0;
            gc.roundUp();
        }
        currentUnit = allUnits.get(index);
        currentUnit.startTurn();
    }

    public void render(SpriteBatch batch, BitmapFont font18) {
        hero.render(batch, font18);
        monsterController.render(batch, font18);
    }

    public void update(float dt) {
        hero.update(dt);
        monsterController.update(dt);

        if (!currentUnit.isActive() || currentUnit.getTurns() == 0) {
            nextTurn();
        }
    }

    public void removeUnitAfterDeath(Unit unit) {
        int unitIndex = allUnits.indexOf(unit);
        allUnits.remove(unit);
        if (unitIndex <= index) {
            index--;
        }
    }

    public void createMonsterInRandomCell() {
        int cellX = -1, cellY = -1;
        do {
            cellX = MathUtils.random(gc.getGameMap().getCellsX() - 1);
            cellY = MathUtils.random(gc.getGameMap().getCellsY() - 1);
        } while (!gc.isCellEmpty(cellX, cellY));

        createMonster(cellX, cellY);
    }

    public void createMonster(int cellX, int cellY) {
        Monster m = monsterController.activate(cellX, cellY);
        allUnits.add(m);
    }
}
