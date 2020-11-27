package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.math.MathUtils;
import ru.geekbrains.dungeon.helpers.Assets;
import ru.geekbrains.dungeon.game.GameController;
import ru.geekbrains.dungeon.helpers.Utils;

public class Monster extends Unit {
    private float aiBrainsImplseTime;
    private Unit target;

    public Monster(GameController gc) {
        super(gc, 5, 2, 10);
        this.texture = Assets.getInstance().getAtlas().findRegion("monster");
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
        this.hp = -1;
    }

    public Monster activate(int cellX, int cellY) {
        this.cellX = cellX;
        this.cellY = cellY;
        this.targetX = cellX;
        this.targetY = cellY;
        this.hpMax = 10;
        this.hp = hpMax;
        this.target = gc.getUnitController().getHero();
        return this;
    }

    public void update(float dt) {
        super.update(dt);
        if (canIMakeAction()) {
            if (isStayStill()) {
                aiBrainsImplseTime += dt;
            }
            if (aiBrainsImplseTime > 0.4f) {
                aiBrainsImplseTime = 0.0f;
                think(dt);
            }
        }
    }

    public void think(float dt) {
        if (canIAttackThisTarget(target)) {
            attack(target);
            return;
        }
        if (amIBlocked()) {
            turns = 0;
            return;
        }
        if (Utils.getCellsIntDistance(cellX, cellY, target.getCellX(), target.getCellY()) < 5) {
            tryToMove(target.getCellX(), target.getCellY());
        } else {
            int dx, dy;
            do {
                dx = MathUtils.random(0, gc.getGameMap().getCellsX() - 1);
                dy = MathUtils.random(0, gc.getGameMap().getCellsY() - 1);
            } while (!(gc.isCellEmpty(dx, dy) && Utils.isCellsAreNeighbours(cellX, cellY, dx, dy)));
            tryToMove(dx, dy);
        }
    }

    public void tryToMove(int preferedX, int preferedY) {
        int bestX = -1, bestY = -1;
        float bestDst = 10000;
        for (int i = cellX - 1; i <= cellX + 1; i++) {
            for (int j = cellY - 1; j <= cellY + 1; j++) {
                if (Utils.isCellsAreNeighbours(cellX, cellY, i, j) && gc.isCellEmpty(i, j)) {
                    float dst = Utils.getCellsFloatDistance(preferedX, preferedY, i, j);
                    if (dst < bestDst) {
                        bestDst = dst;
                        bestX = i;
                        bestY = j;
                    }
                }
            }
        }
        goTo(bestX, bestY);
    }
}
