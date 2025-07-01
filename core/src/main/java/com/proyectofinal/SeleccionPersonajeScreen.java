package com.proyectofinal;

import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;


public class SeleccionPersonajeScreen extends PantallaBase {
    private final RPGGame game;

    public SeleccionPersonajeScreen(RPGGame game) {
        this.game = game;
    }

    @Override
    protected void initUI() {
        Table table = crearTabla();

        table.add(new Label("Elige tu personaje", skin))
            .colspan(3).padBottom(30);
        table.row();

        TextButton btnArquero = new TextButton("Arquero", skin);
        btnArquero.addListener(event -> {
            if (btnArquero.isPressed()) {
                game.setSelectedClass("Arquero");
                game.setScreen(new DungeonScreen(game, "Arquero"));
            }
            return false;
        });

        TextButton btnMago = new TextButton("Mago", skin);
        btnMago.addListener(event -> {
            if (btnMago.isPressed()) {
                game.setSelectedClass("Mago");
                game.setScreen(new DungeonScreen(game, "Mago"));
            }
            return false;
        });

        TextButton btnCaballero = new TextButton("Caballero", skin);
        btnCaballero.addListener(event -> {
            if (btnCaballero.isPressed()) {
                game.setSelectedClass("Caballero");
                game.setScreen(new DungeonScreen(game, "Caballero"));
            }
            return false;
        });

        float pad = 20f;
        table.add(btnArquero).width(200f).pad(pad);
        table.row();
        table.add(btnMago).width(200f).pad(pad);
        table.row();
        table.add(btnCaballero).width(200f).pad(pad);
    }
}

