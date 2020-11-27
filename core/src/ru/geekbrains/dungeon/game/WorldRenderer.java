package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.geekbrains.dungeon.helpers.Assets;
import ru.geekbrains.dungeon.screens.ScreenManager;

public class WorldRenderer {
    private GameController gc;
    private SpriteBatch batch;
    private TextureRegion cursorTexture;
    private BitmapFont font18;
    private BitmapFont font24;
    private StringBuilder stringHelper;

    public WorldRenderer(GameController gc, SpriteBatch batch) {
        this.gc = gc;
        this.batch = batch;
        this.cursorTexture = Assets.getInstance().getAtlas().findRegion("cursor");
        this.font18 = Assets.getInstance().getAssetManager().get("fonts/font18.ttf");
        this.font24 = Assets.getInstance().getAssetManager().get("fonts/font24.ttf");
        this.stringHelper = new StringBuilder();
    }

    public void render() {
        Gdx.gl.glClearColor(1, 1, 1, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        batch.begin();
        gc.getGameMap().render(batch);
        gc.getUnitController().render(batch, font18);
        gc.getProjectileController().render(batch);
        batch.setColor(1, 1, 1, 0.5f);
        batch.draw(cursorTexture, gc.getCursorX() * GameMap.CELL_SIZE, gc.getCursorY() * GameMap.CELL_SIZE);
        batch.setColor(1, 1, 1, 1);
        batch.end();

        float camX = ScreenManager.getInstance().getCamera().position.x;
        float camY = ScreenManager.getInstance().getCamera().position.y;
        ScreenManager.getInstance().resetCamera();
        batch.begin();
        gc.getUnitController().getHero().renderHUD(batch, font24, 10, ScreenManager.WORLD_HEIGHT - 10);
        batch.end();

        ScreenManager.getInstance().pointCameraTo(camX, camY);
    }
}
