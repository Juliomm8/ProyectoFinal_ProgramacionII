package com.proyectofinal;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.ScreenAdapter;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

public class MainMenuScreen extends ScreenAdapter {
    private final Game game;
    private Stage stage;
    private Skin skin;

    public MainMenuScreen(Game game) {
        this.game = game;
    }

    @Override
    public void show() {
        stage = new Stage(new ScreenViewport());
        Gdx.input.setInputProcessor(stage);

        // Cargo skin desde assets/ui/
        TextureAtlas atlas = new TextureAtlas(Gdx.files.internal("ui/uiskin.atlas"));
        skin = new Skin(Gdx.files.internal("ui/uiskin.json"), atlas);

        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        TextButton btnJugar = new TextButton("Jugar", skin);
        btnJugar.addListener(event -> {
            if (btnJugar.isPressed()) {
                System.out.println("Jugar presionado");
                // Más adelante: game.setScreen(new DungeonScreen((RPGGame)game));
            }
            return false;
        });

        table.add(btnJugar).width(200f).pad(20f);
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0f, 0f, 0f, 1);
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
