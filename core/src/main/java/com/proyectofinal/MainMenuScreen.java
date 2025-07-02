package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Pantalla principal del menu del juego.
 * Contiene los botones para jugar, acceder a opciones o salir del juego.
 */
public class MainMenuScreen extends PantallaBase {
    private final RPGGame game; // Referencia al juego principal

    /**
     * Constructor que recibe el juego principal para cambiar pantallas.
     * @param game instancia principal del juego
     */
    public MainMenuScreen(RPGGame game) {
        this.game = game;
    }

    /**
     * Inicializa los botones y la interfaz del menu.
     */
    @Override
    protected void initUI() {
        Table table = crearTabla(); // Tabla para organizar botones

        // Boton para comenzar a jugar
        TextButton btnJugar = new TextButton("Jugar", skin);
        btnJugar.addListener(event -> {
            if (btnJugar.isPressed()) {
                game.setScreen(new SeleccionPersonajeScreen(game));
            }
            return false;
        });

        // Boton para ir a las opciones del juego
        TextButton btnOpciones = new TextButton("Opciones", skin);
        btnOpciones.addListener(event -> {
            if (btnOpciones.isPressed()) {
                game.setScreen(new OptionsScreen(game));
            }
            return false;
        });

        // Boton para salir del juego
        TextButton btnSalir = new TextButton("Salir", skin);
        btnSalir.addListener(event -> {
            if (btnSalir.isPressed()) {
                Gdx.app.exit();
            }
            return false;
        });

        // Agrega los botones a la tabla con padding
        float pad = 20f;
        table.add(btnJugar).width(200f).pad(pad);
        table.row();
        table.add(btnOpciones).width(200f).pad(pad);
        table.row();
        table.add(btnSalir).width(200f).pad(pad);
    }
}
