package com.proyectofinal;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Pantalla que se muestra cuando el jugador muere.
 * Extiende de PantallaBase para reutilizar la configuracion de UI y stage.
 */
public class PantallaMuerte extends PantallaBase {
    private final RPGGame juego;  // Referencia al juego principal para cambiar de pantalla

    /**
     * Constructor que recibe el juego para poder cambiar de pantalla posteriormente.
     * @param juego instancia del juego principal
     */
    public PantallaMuerte(RPGGame juego) {
        this.juego = juego;
    }

    /**
     * Inicializa la UI mostrando el mensaje de muerte y un boton para volver al menu.
     */
    @Override
    protected void initUI() {
        Table tabla = crearTabla(); // Crea tabla centrada y añadida al stage

        // Mensaje principal
        tabla.add(new Label("Has muerto", skin)).pad(20f);
        tabla.row();

        // Boton para volver al menu principal
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
