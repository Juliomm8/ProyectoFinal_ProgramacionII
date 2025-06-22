package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public abstract class PantallaBase extends ScreenAdapter {
    protected Stage stage;
    protected Skin  skin;

    public PantallaBase() { }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);
        skin  = new Skin(Gdx.files.internal("ui/uiskin.json"));
        initUI();
    }

    /**
     * Crea una tabla centrada que ocupa toda la pantalla y la añade al stage.
     */
    protected Table crearTabla() {
        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        stage.addActor(table);
        return table;
    }

    protected abstract void initUI();

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
        stage.act(delta);
        stage.draw();
    }

    @Override
    public void resize(int width, int height) {
        stage.getViewport().update(width, height, true);
    }

    @Override
    public void dispose() {
        stage.dispose();
        skin.dispose();
    }
}
