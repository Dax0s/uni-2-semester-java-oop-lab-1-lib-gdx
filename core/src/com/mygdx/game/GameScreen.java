package com.mygdx.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.TimeUtils;

import java.util.Iterator;

public class GameScreen implements Screen {
    private final int SCREEN_WIDTH = 1600;
    private final int SCREEN_HEIGHT = 900;

    final Lab1 game;

    Texture dropImage;
    Texture bucketImage;
    Sound dropSound;
    Music rainMusic;
    OrthographicCamera camera;
    Rectangle bucket;
    Array<Rectangle> raindrops;
    long lastDropTime;
    int dropsGathered;

    private void spawnRainDrop() {
        Rectangle raindrop = new Rectangle();
        raindrop.x = MathUtils.random(0, SCREEN_WIDTH - 64);
        raindrop.y = SCREEN_HEIGHT;
        raindrop.width = 64;
        raindrop.height = 64;
        raindrops.add(raindrop);
        lastDropTime = TimeUtils.nanoTime();
    }

    public GameScreen(final Lab1 game) {
        this.game = game;

        dropImage = new Texture(Gdx.files.internal("droplet.png"));
        bucketImage = new Texture(Gdx.files.internal("bucket.png"));

        dropSound = Gdx.audio.newSound(Gdx.files.internal("drop.wav"));
        rainMusic = Gdx.audio.newMusic(Gdx.files.internal("rain.mp3"));
        rainMusic.setLooping(true);

        camera = new OrthographicCamera();
        camera.setToOrtho(false, SCREEN_WIDTH, SCREEN_HEIGHT);

        bucket = new Rectangle();
        bucket.x = SCREEN_WIDTH / 2 - 64 / 2;
        bucket.y = 20;
        bucket.width = 64;
        bucket.height = 64;

        raindrops = new Array<Rectangle>();
        spawnRainDrop();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0, 0, 0.2f, 1);

        camera.update();

        if (Gdx.input.isKeyPressed(Input.Keys.A)) {
            bucket.x -= 800 * Gdx.graphics.getDeltaTime();
        } else if (Gdx.input.isKeyPressed(Input.Keys.D)) {
            bucket.x += 800 * Gdx.graphics.getDeltaTime();
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.DOWN) && rainMusic.getVolume() > 0.1f) {
            rainMusic.setVolume(rainMusic.getVolume() - 0.1f);
        } else if (Gdx.input.isKeyJustPressed(Input.Keys.UP) && rainMusic.getVolume() < 1) {
            rainMusic.setVolume(rainMusic.getVolume() + 0.1f);
        }

        if (bucket.x < 0)
            bucket.x = 0;
        if (bucket.x > SCREEN_WIDTH - 64)
            bucket.x = SCREEN_WIDTH - 64;

        if (TimeUtils.nanoTime() - lastDropTime > 1000000000)
            spawnRainDrop();

        Iterator<Rectangle> iter = raindrops.iterator();
        while (iter.hasNext()) {
            Rectangle raindrop = iter.next();
            raindrop.y -= 200 * Gdx.graphics.getDeltaTime();
            if (raindrop.y + 64 < 0) iter.remove();

            if (raindrop.overlaps(bucket)) {
                dropSound.play();
                dropsGathered++;
                iter.remove();
            }
        }

        game.batch.setProjectionMatrix(camera.combined);
        game.batch.begin();
        game.font.draw(game.batch, "Drops Collected: " + dropsGathered, 20, SCREEN_HEIGHT - 20);
        game.font.draw(game.batch, "Volume: " + (int) (rainMusic.getVolume() * 100), SCREEN_WIDTH - 100, SCREEN_HEIGHT - 20);
        game.batch.draw(bucketImage, bucket.x, bucket.y);
        for (Rectangle raindrop: raindrops) {
            game.batch.draw(dropImage, raindrop.x, raindrop.y);
        }
        game.batch.end();
    }

    @Override
    public void resize(int width, int height) {

    }

    @Override
    public void pause() {

    }

    @Override
    public void resume() {

    }

    @Override
    public void hide() {

    }

    @Override
    public void dispose() {

    }

    @Override
    public void show() {
        rainMusic.play();
    }
}
