package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import ru.geekbrains.dungeon.helpers.Poolable;

import java.util.List;

@Data
public abstract class Unit implements Poolable {
    GameController gc;
    TextureRegion texture;
    TextureRegion textureHp;
    int damage;
    int defence;
    int hp;
    int hpMax;
    int cellX;
    int cellY;
    int gold;
    int attackRange;
    float movementTime;
    float movementMaxTime;
    int targetX, targetY;
    //int turns, maxTurns;
    int attackTurns;
    int stepTurns;
    float innerTimer;

    public enum unitType {HERO, MONSTER}

    unitType type;
    StringBuilder stringHelper;

    public Unit(GameController gc, int cellX, int cellY, int hpMax) {
        this.gc = gc;
        this.hpMax = hpMax;
        this.hp = hpMax;
        this.cellX = cellX;
        this.cellY = cellY;
        this.targetX = cellX;
        this.targetY = cellY;
        this.damage = 2;
        this.defence = 0;
        //this.maxTurns = GameController.TURNS_COUNT;
        this.movementMaxTime = 0.2f;
        this.attackRange = 2;
        this.innerTimer = MathUtils.random(1000.0f);
        this.stringHelper = new StringBuilder();
        this.gold = MathUtils.random(1, 5);
    }

    /*
    public void addGold(int amount) {
        gold += amount;
    }
    */

    public void getGoldInCell(int cellX, int cellY) {
        gold += gc.getGameMap().getGold(cellX, cellY);
    }

    public void cure(int amount) {
        hp += amount;
        if (hp > hpMax) {
            hp = hpMax;
        }
    }

    public int maxAttackTurns(int max, int min) {
        return MathUtils.random(max, min);
    }

    public int maxStepTurns(int max, int min) {
        return MathUtils.random(max, min);
    }

    public void startTurn() {
        //turns = maxTurns;
        attackTurns = maxAttackTurns(1, 4);
        stepTurns = maxStepTurns(1, 4);
    }

    public void startRound() {
        cure(1);
    }

    @Override
    public boolean isActive() {
        return hp > 0;
    }

    public boolean takeDamage(Unit source, int amount) {
        hp -= amount;
        if (hp <= 0) {
            gc.getUnitController().removeUnitAfterDeath(this);
            //source.addGold(this.gold);
        }
        return hp <= 0;
    }

    /*
    public boolean canIMakeAction() {
        return gc.getUnitController().isItMyTurn(this) && isStayStill()
                && (stepTurns > 0 || (attackTurns > 0 && gc.getUnitController().areActiveUnitsInAttackRange(this)));
    }
    */
    // 2. Каждому персонажу дается количество шагов и атак, в начале хода они генерятся в пределах 1-4
    // 3. На соответствующие действия персонаж тратит эти очки, если сделать ничего не может/не хочет, то
    // счетчик его ходов обнуляется (действия шаги/атака заменит turns)
    public boolean canIMove() {
        return gc.getUnitController().isItMyTurn(this) && isStayStill() && (stepTurns > 0);
    }

    public boolean canIAttack() {
        return gc.getUnitController().isItMyTurn(this) && isStayStill() && attackTurns > 0 && gc.getUnitController().areActiveUnitsInAttackRange(this);
    }

    public boolean isStayStill() {
        return cellY == targetY && cellX == targetX;
    }

    public void goTo(int argCellX, int argCellY) {
        if (!gc.getGameMap().isCellPassable(argCellX, argCellY) || !gc.getUnitController().isCellFree(argCellX, argCellY)) {
            return;
        }
        if (Math.abs(argCellX - cellX) + Math.abs(argCellY - cellY) == 1) {
            targetX = argCellX;
            targetY = argCellY;
            getGoldInCell(targetX, targetY);
        }
    }

    public boolean canIAttackThisTarget(Unit target) {
        if (attackTurns <= 0) return false;
        if (this.type == target.type) return false;
        return cellX - target.getCellX() == 0 && Math.abs(cellY - target.getCellY()) <= attackRange ||
                cellY - target.getCellY() == 0 && Math.abs(cellX - target.getCellX()) <= attackRange;
    }

    public void attack(Unit target) {
        target.takeDamage(this, BattleCalc.attack(this, target));
        this.takeDamage(target, BattleCalc.checkCounterAttack(this, target));
        attackTurns--;
    }

    public boolean isInVisibleCell() {
        return gc.getGameMap().isCellVisible(targetX, targetY);
    }

    public void update(float dt) {

        innerTimer += dt;
        if (!isStayStill() && stepTurns > 0) {
            movementTime += dt;
            // персонажи за пределами видимости ходят без задержки
            if (movementTime > movementMaxTime || !isInVisibleCell()) {
                movementTime = 0;
                cellX = targetX;
                cellY = targetY;
                stepTurns--;
            }
        }
    }

    public void render(SpriteBatch batch, BitmapFont font18) {

        // Если вне видимой зоны, то не рисуем.
        if (!isInVisibleCell()) return;

        float hpAlpha = hp == hpMax ? 0.4f : 1.0f;

        float px = cellX * GameMap.CELL_SIZE;
        float py = cellY * GameMap.CELL_SIZE;

        if (!isStayStill()) {
            px = cellX * GameMap.CELL_SIZE + (targetX - cellX) * (movementTime / movementMaxTime) * GameMap.CELL_SIZE;
            py = cellY * GameMap.CELL_SIZE + (targetY - cellY) * (movementTime / movementMaxTime) * GameMap.CELL_SIZE;
        }

        batch.draw(texture, px, py);
        batch.setColor(0.0f, 0.0f, 0.0f, hpAlpha);

        float barX = px, barY = py + MathUtils.sin(innerTimer * 5.0f) * 2;
        batch.draw(textureHp, barX + 1, barY + 51, 58, 10);
        batch.setColor(0.7f, 0.0f, 0.0f, hpAlpha);
        batch.draw(textureHp, barX + 2, barY + 52, 56, 8);
        batch.setColor(0.0f, 1.0f, 0.0f, hpAlpha);
        batch.draw(textureHp, barX + 2, barY + 52, (float) hp / hpMax * 56, 8);
        batch.setColor(1.0f, 1.0f, 1.0f, hpAlpha);
        stringHelper.setLength(0);
        stringHelper.append(hp);
        font18.setColor(1.0f, 1.0f, 1.0f, hpAlpha);
        font18.draw(batch, stringHelper, barX, barY + 64, 60, 1, false);

        font18.setColor(1.0f, 1.0f, 1.0f, 1.0f);
        batch.setColor(1.0f, 1.0f, 1.0f, 1.0f);

        // 4. Над головой текущего игрока можно отобразить количество каждого действия S2, A3 (step - 2, attack - 3)
        if (gc.getUnitController().isItMyTurn(this)) {
            stringHelper.setLength(0);
            stringHelper.append("S" + stepTurns + ", A" + attackTurns);
            font18.setColor(1.0f, 1.0f, 1.0f, 1.0f);
            font18.draw(batch, stringHelper, barX, barY + 80, 60, 1, false);
        }
    }

    public boolean amIBlocked() {
        return !(gc.isCellEmpty(cellX - 1, cellY) || gc.isCellEmpty(cellX + 1, cellY) || gc.isCellEmpty(cellX, cellY - 1) || gc.isCellEmpty(cellX, cellY + 1));
    }
}
