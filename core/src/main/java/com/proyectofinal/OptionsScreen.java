package com.proyectofinal;

import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Pantalla de opciones del juego. Actualmente solo permite volver al menu principal.
 */
public class OptionsScreen extends PantallaBase {
    private final RPGGame game;  // Referencia al juego principal para poder cambiar de pantalla

    /**
     * Constructor que recibe el juego principal para gestion de pantallas.
     * @param game instancia del juego principal
     */
    public OptionsScreen(RPGGame game) {
        this.game = game;
    }

    /**
     * Inicializa la interfaz de usuario de la pantalla de opciones.
     */
    @Override
    protected void initUI() {
        Table table = crearTabla();  // Metodo heredado que crea una tabla centrada con estilo

        // Boton para volver al menu principal
        TextButton btnVolver = new TextButton("Volver", skin);
        btnVolver.addListener(event -> {
            if (btnVolver.isPressed()) {
                game.setScreen(new MainMenuScreen(game));  // Cambia de pantalla al menu principal
            }
            return false;
        });

        // Agrega el boton a la tabla con ancho fijo y padding
        table.add(btnVolver).width(200f).pad(20f);
    }
}
