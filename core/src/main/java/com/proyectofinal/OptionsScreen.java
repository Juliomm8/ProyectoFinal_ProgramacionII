package com.proyectofinal;

import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;

/**
 * Pantalla de opciones con un botón para volver al menú principal.
 */

public class OptionsScreen extends PantallaBase {
    private final RPGGame game;

    public OptionsScreen(RPGGame game) {
        this.game = game;
    }

    @Override
    protected void initUI() {
        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

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
