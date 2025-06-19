package com.proyectofinal;

import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class DungeonScreen extends ScreenAdapter {
    private SpriteBatch batch;
    private BitmapFont font;
    private final String playerClass;

    public DungeonScreen(String playerClass) {
        this.playerClass = playerClass;
        }

    @Override
    public void show() {
        batch = new SpriteBatch();
        font  = new BitmapFont(); // fuente por defecto
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.1f, 0.1f, 0.1f, 1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        font.draw(batch, "Clase elegida: " + playerClass, 100, 120);
        batch.end();
    }

    @Override
    public void dispose() {
        batch.dispose();
        font.dispose();
    }
}
