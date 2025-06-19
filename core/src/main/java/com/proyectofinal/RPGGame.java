package com.proyectofinal;

import com.badlogic.gdx.Game;

public class RPGGame extends Game {
    private String selectedClass;

    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }

    public void setSelectedClass(String cls) {
        this.selectedClass = cls;
    }

    public String getSelectedClass() {
        return selectedClass;
    }
}
