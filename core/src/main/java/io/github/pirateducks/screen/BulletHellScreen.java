package io.github.pirateducks.screen;

import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;
import io.github.pirateducks.level.GameObject;
import io.github.pirateducks.level.LevelManager;
import io.github.pirateducks.level.college.College;

public class BulletHellScreen extends College implements Screen {

    private final Array<GameObject> objects = new Array<>();
    private Sprite map;

    public BulletHellScreen(LevelManager levelManager) {
        super(levelManager);
    }

    /**
     * @return The max health of the college building
     */
    @Override
    public int getMaxHeath() {
        // Each cannon will be worth 100 health
        return 400;
    }

    /**
     * Called to draw the screen
     *
     * @param batch
     * @param camera
     */
    @Override
    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        map.draw(batch);

        for (GameObject object : objects) {
            object.render(batch);
        }
    }

    /**
     * Called to update the screen
     *
     * @param delta The delta time since the last update
     */
    @Override
    public void update(float delta) {

    }

    /**
     * Called when this screen becomes the active screen
     *
     * @param camera
     */
    @Override
    public void startDisplaying(OrthographicCamera camera) {

        // load the map
        Texture texture = new Texture("BulletHellBackground.png");
        map = new Sprite(texture);
        // scales the sprite depending on window size multiplied by a constant
        map.setSize(camera.viewportWidth, camera.viewportHeight);
        // Centers the map sprite
        map.setPosition(0, 0);
    }

    /**
     * Called when this screen is no longer the active screen
     * Use this method to dispose of everything
     */
    @Override
    public void stopDisplaying() {
        map.getTexture().dispose();
        for (GameObject object : objects) {
            object.dispose();
        }
    }
}
