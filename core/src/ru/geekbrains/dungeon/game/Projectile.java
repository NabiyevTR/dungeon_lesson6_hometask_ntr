package ru.geekbrains.dungeon.game;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import ru.geekbrains.dungeon.helpers.Poolable;

public class Projectile implements Poolable {
    private TextureRegion texture;
    private Vector2 position;
    private Vector2 velocity;
    private boolean active;

    @Override
    public boolean isActive() {
        return active;
    }

    public Projectile(TextureRegion texture) {
        this.position = new Vector2(0, 0);
        this.velocity = new Vector2(0, 0);
        this.texture = texture;
        this.active = false;
    }

    public void deactivate() {
        active = false;
    }

    public void activate(float x, float y, float vx, float vy) {
        active = true;
        position.set(x, y);
        velocity.set(vx, vy);
    }

    public void update(float dt) {
        position.mulAdd(velocity, dt);
        if (position.x < 0 || position.x > 1280 || position.y < 0 || position.y > 720) {
            deactivate();
        }
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, position.x - 8, position.y - 8);
    }
}
