package com.proyectofinal;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

public class PantallaMuerte extends PantallaBase {
    private final RPGGame juego;

    public PantallaMuerte(RPGGame juego) {
        this.juego = juego;
    }

    @Override
    protected void initUI() {
        Table tabla = crearTabla();
        tabla.add(new Label("Has muerto", skin)).pad(20f);
        tabla.row();
        TextButton volver = new TextButton("Volver al menú", skin);
        volver.addListener(event -> {
            if (volver.isPressed()) {
                juego.setScreen(new MainMenuScreen(juego));
            }
            return false;
        });
        tabla.add(volver).width(200f).pad(20f);
    }
}
