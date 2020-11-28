package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.MathUtils;
import lombok.Data;
import org.graalvm.compiler.loop.MathUtil;
import ru.geekbrains.dungeon.helpers.Assets;

@Data
public class GameMap {
    public enum CellType {
        GRASS, WATER, TREE
    }

    public int cellX;
    public int cellY;

    @Data
    private class Cell {
        CellType type;
        int index;
        boolean visible;
        int gold;


        public Cell() {
            type = CellType.GRASS;
            index = 0;
        }

        public void changeType(CellType to) {
            type = to;
            if (type == CellType.TREE) {
                index = MathUtils.random(4);
            }
        }
    }

    public static final int CELLS_X = 22;
    public static final int CELLS_Y = 12;
    public static final int CELL_SIZE = 60;
    public static final int FOREST_PERCENTAGE = 5;
    public static final int VISIBLE_RADIUS = 10;

    public int getCellsX() {
        return CELLS_X;
    }

    public int getCellsY() {
        return CELLS_Y;
    }

    private Cell[][] data;
    private TextureRegion grassTexture;
    private TextureRegion goldTexture;
    private TextureRegion[] treesTextures;

    public GameMap() {
        this.data = new Cell[CELLS_X][CELLS_Y];
        for (int i = 0; i < CELLS_X; i++) {
            for (int j = 0; j < CELLS_Y; j++) {
                this.data[i][j] = new Cell();
            }
        }
        int treesCount = (int) ((CELLS_X * CELLS_Y * FOREST_PERCENTAGE) / 100.0f);
        for (int i = 0; i < treesCount; i++) {
            this.data[MathUtils.random(0, CELLS_X - 1)][MathUtils.random(0, CELLS_Y - 1)].changeType(CellType.TREE);

        }

        this.grassTexture = Assets.getInstance().getAtlas().findRegion("grass");
        this.goldTexture = Assets.getInstance().getAtlas().findRegion("projectile");
        this.treesTextures = Assets.getInstance().getAtlas().findRegion("trees").split(60, 90)[0];
    }

    public boolean isCellPassable(int cx, int cy) {
        if (cx < 0 || cx > getCellsX() - 1 || cy < 0 || cy > getCellsY() - 1) {
            return false;
        }
        if (data[cx][cy].type != CellType.GRASS) {
            return false;
        }
        return true;
    }

    public boolean isCellVisible(int cx, int cy) {
        return data[cx][cy].isVisible();
    }

    // 6. * Добавить туман войны +
    public void updateCellsVisibility() {
        for (int i = 0; i < CELLS_X; i++) {
            for (int j = CELLS_Y - 1; j >= 0; j--) {
                if (Math.sqrt(Math.pow(i - cellX, 2) + Math.pow(j - cellY, 2)) <= VISIBLE_RADIUS) {
                    data[i][j].setVisible(true);
                }
            }
        }
    }

    public void setGold(int cx, int cy, int gold) {
        data[cx][cy].gold += gold;
    }

    public int getGold(int cx, int cy) {
        int gold = data[cx][cy].gold;
        data[cx][cy].gold = 0;
        return gold;
    }

    public void render(SpriteBatch batch) {
        for (int i = 0; i < CELLS_X; i++) {
            for (int j = CELLS_Y - 1; j >= 0; j--) {
                if (!data[i][j].isVisible()) continue;
                batch.draw(grassTexture, i * CELL_SIZE, j * CELL_SIZE);
                if (data[i][j].gold > 0) {
                    batch.draw(goldTexture, (i * CELL_SIZE + CELL_SIZE / 2 - goldTexture.getRegionWidth() / 2),
                            (j * CELL_SIZE + CELL_SIZE / 2) - goldTexture.getRegionHeight() / 2);
                }
                if (data[i][j].type == CellType.TREE) {
                    batch.draw(treesTextures[data[i][j].index], i * CELL_SIZE, j * CELL_SIZE);
                }
            }
        }
    }
}
