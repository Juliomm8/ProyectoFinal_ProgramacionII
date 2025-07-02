package com.proyectofinal;

import com.badlogic.gdx.Game;

/**
 * Clase principal del juego RPG. Extiende de Game, que permite manejar pantallas (screens) en LibGDX.
 */
public class RPGGame extends Game {

    // Almacena la clase seleccionada por el jugador (Ejemplo: "Mago", "Caballero", "Arquero")
    private String selectedClass;

    /**
     * Metodo que se llama al iniciar el juego.
     * Establece la pantalla inicial como el menu principal.
     */
    @Override
    public void create() {
        setScreen(new MainMenuScreen(this));
    }

    /**
     * Asigna la clase seleccionada por el jugador.
     * @param cls Clase elegida (por ejemplo: "Mago")
     */
    public void setSelectedClass(String cls) {
        this.selectedClass = cls;
    }

    /**
     * Devuelve la clase seleccionada por el jugador.
     * @return Clase seleccionada
     */
    public String getSelectedClass() {
        return selectedClass;
    }
}
