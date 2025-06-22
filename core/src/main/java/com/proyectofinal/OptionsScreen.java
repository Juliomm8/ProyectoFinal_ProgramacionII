package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class OptionsScreen extends PantallaBase {
    private final RPGGame game;

    public OptionsScreen(RPGGame game) {
        this.game = game;
    }

    @Override
    protected void initUI() {
        Table table = crearTabla();

        TextButton btnVolver = new TextButton("Volver", skin);
        btnVolver.addListener(event -> {
            if (btnVolver.isPressed()) {
                game.setScreen(new MainMenuScreen(game));
            }
            return false;
        });

        table.add(btnVolver).width(200f).pad(20f);
    }
}
