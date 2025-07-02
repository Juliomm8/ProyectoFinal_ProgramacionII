package com.proyectofinal;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;

/**
 * Pantalla donde el jugador selecciona la clase de personaje que usara en el juego.
 * Permite elegir entre Arquero, Mago o Caballero.
 */
public class SeleccionPersonajeScreen extends PantallaBase {
    // Referencia al juego principal para cambiar de pantalla y guardar la clase elegida
    private final RPGGame game;

    /**
     * Constructor de la pantalla de seleccion de personaje.
     * @param game Instancia del juego principal
     */
    public SeleccionPersonajeScreen(RPGGame game) {
        this.game = game;
    }

    /**
     * Metodo que inicializa la interfaz de usuario.
     * Se ejecuta cuando se muestra esta pantalla.
     */
    @Override
    protected void initUI() {
        // Crear una tabla para organizar los elementos UI
        Table table = crearTabla();

        // Titulo principal
        table.add(new Label("Elige tu personaje", skin))
            .colspan(3).padBottom(30);
        table.row();

        // Boton para seleccionar Arquero
        TextButton btnArquero = new TextButton("Arquero", skin);
        btnArquero.addListener(event -> {
            if (btnArquero.isPressed()) {
                game.setSelectedClass("Arquero"); // Guardar clase seleccionada
                game.setScreen(new DungeonScreen(game, "Arquero")); // Cambiar a la pantalla del juego
            }
            return false;
        });

        // Boton para seleccionar Mago
        TextButton btnMago = new TextButton("Mago", skin);
        btnMago.addListener(event -> {
            if (btnMago.isPressed()) {
                game.setSelectedClass("Mago");
                game.setScreen(new DungeonScreen(game, "Mago"));
            }
            return false;
        });

        // Boton para seleccionar Caballero
        TextButton btnCaballero = new TextButton("Caballero", skin);
        btnCaballero.addListener(event -> {
            if (btnCaballero.isPressed()) {
                game.setSelectedClass("Caballero");
                game.setScreen(new DungeonScreen(game, "Caballero"));
            }
            return false;
        });

        // Añadir los botones a la tabla con separacion vertical
        float pad = 20f;
        table.add(btnArquero).width(200f).pad(pad);
        table.row();
        table.add(btnMago).width(200f).pad(pad);
        table.row();
        table.add(btnCaballero).width(200f).pad(pad);
    }
}
