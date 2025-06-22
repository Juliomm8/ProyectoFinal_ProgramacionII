package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class MainMenuScreen extends PantallaBase {
    private final RPGGame game;

    public MainMenuScreen(RPGGame game) {
        this.game = game;
    }

    @Override
    protected void initUI() {
        Table table = crearTabla();

        TextButton btnJugar = new TextButton("Jugar", skin);
        btnJugar.addListener(event -> {
            if (btnJugar.isPressed()) {
                game.setScreen(new SeleccionPersonajeScreen(game));
            }
            return false;
        });

        TextButton btnOpciones = new TextButton("Opciones", skin);
        btnOpciones.addListener(event -> {
            if (btnOpciones.isPressed()) {
                game.setScreen(new OptionsScreen(game));
            }
            return false;
        });

        TextButton btnSalir = new TextButton("Salir", skin);
        btnSalir.addListener(event -> {
            if (btnSalir.isPressed()) {
                Gdx.app.exit();
            }
            return false;
        });

        float pad = 20f;
        table.add(btnJugar).width(200f).pad(pad);
        table.row();
        table.add(btnOpciones).width(200f).pad(pad);
        table.row();
        table.add(btnSalir).width(200f).pad(pad);
    }
}
