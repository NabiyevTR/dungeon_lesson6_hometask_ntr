package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import lombok.Data;
import ru.geekbrains.dungeon.helpers.Assets;
import ru.geekbrains.dungeon.game.GameController;
import ru.geekbrains.dungeon.screens.ScreenManager;

public class Hero extends Unit {
    private String name;

    public Hero(GameController gc) {
        super(gc, 1, 1, 10);
        this.name = "Sir Lancelot";
        this.hpMax = 100;
        this.hp = this.hpMax;
        this.texture = Assets.getInstance().getAtlas().findRegion("knight");
        this.textureHp = Assets.getInstance().getAtlas().findRegion("hp");
    }

    public void update(float dt) {
        super.update(dt);
        if (Gdx.input.justTouched() && canIMakeAction()) {
            Monster m = gc.getUnitController().getMonsterController().getMonsterInCell(gc.getCursorX(), gc.getCursorY());
            if (m != null && canIAttackThisTarget(m)) {
                attack(m);
            } else {
                goTo(gc.getCursorX(), gc.getCursorY());
            }
        }
    }

    public void renderHUD(SpriteBatch batch, BitmapFont font, int x, int y) {
        stringHelper.setLength(0);
        stringHelper
                .append("Player: ").append(name).append("\n")
                .append("Gold: ").append(gold).append("\n");
        font.draw(batch, stringHelper, x, y);
    }
}
