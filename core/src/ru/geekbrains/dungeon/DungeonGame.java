package ru.geekbrains.dungeon;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import ru.geekbrains.dungeon.game.GameController;
import ru.geekbrains.dungeon.game.GameMap;
import ru.geekbrains.dungeon.helpers.Assets;
import ru.geekbrains.dungeon.screens.ScreenManager;

public class DungeonGame extends Game {
    private SpriteBatch batch;

    // Домашнее задание:
    // 1. Разобраться с кодом
    // 2. Каждому персонажу дается количество шагов и атак, в начале хода они генерятся в пределах 1-4
    // 3. На соответствующие действия персонаж тратит эти очки, если сделать ничего не может/не хочет, то
    // счетчик его ходов обнуляется (действия шаги/атака заменит turns)
    // 4. Над головой текущего игрока можно отобразить количество каждого действия S2, A3 (step - 2, attack - 3)
    // 5. * Монеты высыпаются на пол, и чтобы их забрать, надо на них наступить
    // 6. * Добавить туман войны

    // Идея механики:
    // 1. На ваш ход выбатает случайный набор действий (2 шага, 4 атаки, 1 спец действие)
    // 2. На движение тратится по 1 шагу
    // 3. На обычную атаку требуется 1 атака, на усиленную 3 атаки
    // 4. Спец действия тратятся на лечение/приготовление/крафт/...
    // 5. Можно выбирать оружие, на каждого монстра оружие действует по разному, даже в рамках одного класса
    // *. Для монстров то же самое

    @Override
    public void create() {
        batch = new SpriteBatch();
        ScreenManager.getInstance().init(this, batch);
        ScreenManager.getInstance().changeScreen(ScreenManager.ScreenType.GAME);
    }

    @Override
    public void render() {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        float dt = Gdx.graphics.getDeltaTime();
        getScreen().render(dt);
    }

    @Override
    public void dispose() {
        batch.dispose();
    }
}
