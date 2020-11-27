package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import lombok.Data;
import ru.geekbrains.dungeon.screens.ScreenManager;

@Data
public class GameController {
    public static final int INITIAL_MONSTERS_COUNT = 3;
    public static final int TURNS_COUNT = 5;

    private SpriteBatch batch;
    private ProjectileController projectileController;
    private UnitController unitController;
    private GameMap gameMap;

    private Vector2 mouse;
    private Vector2 pressedMouse;

    private int cursorX, cursorY;
    private int round;

    public GameController(SpriteBatch batch) {
        this.batch = batch;
        this.mouse = new Vector2(0, 0);
        this.pressedMouse = new Vector2(0, 0);
        this.gameMap = new GameMap();
        this.unitController = new UnitController(this);
        this.projectileController = new ProjectileController();
        this.unitController.init(INITIAL_MONSTERS_COUNT);
        this.round = 1;
    }

    public void roundUp() {
        round++;
        unitController.startRound();
        if (round % 3 == 0) {
            unitController.createMonsterInRandomCell();
        }
    }

    public boolean isCellEmpty(int cx, int cy) {
        return gameMap.isCellPassable(cx, cy) && unitController.isCellFree(cx, cy);
    }

    public void update(float dt) {
        checkMouse();
        projectileController.update(dt);
        unitController.update(dt);
    }

    public void checkMouse() {
        mouse.set(Gdx.input.getX(), Gdx.input.getY());
        ScreenManager.getInstance().getViewport().unproject(mouse);

        if (Gdx.input.isTouched() && Gdx.input.isButtonPressed(Input.Buttons.RIGHT)) {
            float camX = ScreenManager.getInstance().getCamera().position.x;
            float camY = ScreenManager.getInstance().getCamera().position.y;

            camX += pressedMouse.x - mouse.x;
            camY += pressedMouse.y - mouse.y;

            mouse.x += pressedMouse.x - mouse.x;
            mouse.y += pressedMouse.y - mouse.y;

            ScreenManager.getInstance().pointCameraTo(camX, camY);
        }

        cursorX = (int) (mouse.x / GameMap.CELL_SIZE);
        cursorY = (int) (mouse.y / GameMap.CELL_SIZE);

        pressedMouse.set(mouse);
    }
}
