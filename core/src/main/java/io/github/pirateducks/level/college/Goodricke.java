package io.github.pirateducks.level.college;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import io.github.pirateducks.level.GameObject;
import io.github.pirateducks.level.MainLevel;
import io.github.pirateducks.level.gameObjects.CannonBall;
import io.github.pirateducks.level.gameObjects.Fruit;
import io.github.pirateducks.level.gameObjects.GoodrickeCannon;
import io.github.pirateducks.screen.PauseScreen;

import java.util.Random;

public class Goodricke extends College { // Projectiles
    private final OrthographicCamera camera;
    private final Array<Fruit> fruit = new Array<>();
    private final Array<GoodrickeCannon> cannons = new Array<>();

    private float fruitSize = 10; // Size of fruit
    private final float sizeMultiplier = (float) 1;
    private float playerX = 0; // Default player co-ordinates
    private float playerY = 0;
    private boolean save = false;
    public Music sfx_ocean;
    public Music gameMusic;
    public Sound explode;

    public Goodricke(MainLevel level, OrthographicCamera camera) {
        super(level);

        this.camera = camera;

        sfx_ocean = Gdx.audio.newMusic(Gdx.files.internal("Ocean.ogg"));
        sfx_ocean.setLooping(true);
        sfx_ocean.setVolume(0.005f);
        sfx_ocean.play();

        /*
         * Music: https://www.bensound.com
         * Name: Epic | Link https://www.bensound.com/royalty-free-music/track/epic
         * Licence: You are free to use this music in your multimedia project (online videos(YouTube,...), websites, animations, etc.)
         * as long as you credit Bensound.com, For example: "Music: www.bensound.com" or "Royalty Free Music from Bensound"
         */
        gameMusic = Gdx.audio.newMusic(Gdx.files.internal("goodricke/bensound-epic.mp3"));
        gameMusic.setLooping(true);
        gameMusic.setVolume(0.15f);
        gameMusic.play();

        /*
         * Name: Squish Footstep Watery Grass 3
         * Source: https://www.dreamstime.com/stock-music-sound-effect/squish.html
         * Licence: Royalty free
         */
        explode = Gdx.audio.newSound(Gdx.files.internal("goodricke/fruit-destroy.mp3"));

    }

    @Override
    public int getMaxHealth() {
        return 10;
    }

    /**
     * Called to draw the screen
     *
     * @param batch
     * @param camera
     */
    @Override
    public void draw(SpriteBatch batch, OrthographicCamera camera) {
        super.draw(batch, camera);

        for (GoodrickeCannon cannon : cannons) {
            cannon.render(batch);
        }

        for (Fruit f : fruit) {
            f.render(batch);
        }
    }

    @Override
    public void update(float delta) {
        // updating all game objects
        super.update(delta);

        for(GoodrickeCannon cannon : cannons) {
            cannon.update(delta);
        }
        for (Fruit f : fruit) {
            f.update(delta);
        }

        if (cannons.isEmpty()) {
            // all cannons are dead, the college has been defeated setting its health to 0
            setHealth(0);
        }

        // Pause game when escape key is pressed
        if(Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)){
            save = true;
            // Save players position for unpause
            playerX = getPlayer().getX();
            playerY = getPlayer().getY();
            this.stopDisplaying();
            // Load pause screen
            getLevelManager().getMainClass().setCurrentScreen(new PauseScreen(getLevelManager().getMainClass(),this));
        }

        // Return to main level if college is defeated
        if (isDefeated()) {
            save = false;
            getLevelManager().getMainClass().setCurrentScreen(getLevelManager());
        }

        // checking if any cannonballs are hitting any fruit or cannons
        for (GameObject object : getObjectsClone()) {
            if (object instanceof CannonBall) {
                // checking if the cannonball is colliding with fruit
                Rectangle collision = ((CannonBall) object).getCollision();

                for (Fruit f : getFruitClone()) {
                    // looping through fruit to check collision
                    if (collision.overlaps(f.getCollision())) {
                        // despawning the cannonball and the fruit
                        f.explode();
                        ((CannonBall) object).collide();
                        explode.play(0.5f);
                        continue; // cannot both collide with fruit and cannon in the same collision
                    }
                }

                for(GoodrickeCannon cannon : cannons){
                    // looping through the cannons to check collision
                    if(cannon.getHealth() > 0 && collision.overlaps(cannon.getCollision())){
                        cannon.setHealth(cannon.getHealth() - 2);
                        ((CannonBall)object).collide();
                        continue;
                        // no need to check other cannons
                    }
                }
            }
        }
        // checking if the player has hit any fruit
        Rectangle collision = getPlayer().getCollision();
        for (Fruit f : getFruitClone()) {
            if (f.getCollision().overlaps(collision)) {
                // despawning the fruit
                f.explode();
                // damaging the player
                getPlayer().setHealth(getPlayer().getHealth() - 2);

                explode.play(0.5f);
            }
        }
    }

    /**
     * Used to spawn a new fruit at the specified location
     *
     * @param x The x coord of the fruit
     * @param y the y coord of the fruit
     */
    public void spawnFruit(float x, float y, float angle) {
        // To make the game more difficult, the size can be decreased
        fruitSize = fruitSize * sizeMultiplier;
        Random rnd = new Random();
        Fruit f = new Fruit(x - fruitSize / 2, y - fruitSize / 2, fruitSize, rnd.nextInt(Fruit.UNIQUEFRUIT), this, camera, angle);
        fruit.add(f);
    }

    @Override
    public void setup(OrthographicCamera camera) {
        // Don't add new cannons when unpausing
        if (!save) {
            // init the cannons
            cannons.add(new GoodrickeCannon(130, 130, 200, 300, this, camera));
            cannons.add(new GoodrickeCannon(130, 130, 350, 240, this, camera));
            cannons.add(new GoodrickeCannon(130, 130, 500, 300, this, camera));
        }
        // Keep players position when unpausing
        getPlayer().setX(playerX);
        getPlayer().setY(playerY);
    }

    @Override
    public void stopDisplaying() {
        // Disposes objects if not unpausing
        if (!save) {
            for (Fruit f : fruit) {
                f.dispose();
            }
            for (GoodrickeCannon cannon : cannons) {
                cannon.dispose();
            }
        }
        sfx_ocean.dispose();
        gameMusic.dispose();
    }

    public void removeCannon(GoodrickeCannon cannon) {
        cannons.removeValue(cannon, false);
    }

    /**
     * @return the texture of the map for this level
     */
    @Override
    protected Texture getMapTexture() {
        return new Texture("goodricke/goodrickemap.png");
    }

    /**
     * @return a clone of the fruit array to avoid GdxRuntimeException
     */
    public Array<Fruit> getFruitClone() {
        return new Array<>(fruit);
    }

    /**
     * Used to remove a fruit from the level
     * @param toRemove The fruit that should be removed
     */
    public void removeFruit(Fruit toRemove){
        fruit.removeValue(toRemove, true);
        removeObject(toRemove);
    }
}
