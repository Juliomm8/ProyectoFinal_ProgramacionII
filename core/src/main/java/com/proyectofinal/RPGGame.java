package com.proyectofinal;

import com.badlogic.gdx.Game;

public class RPGGame extends Game {
    @Override
    public void create() {
        // Al iniciar, cargamos el menú
        setScreen(new MainMenuScreen(this));
    }
}
