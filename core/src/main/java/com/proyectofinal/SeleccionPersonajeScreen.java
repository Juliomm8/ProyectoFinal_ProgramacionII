package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

/**
 * Pantalla para seleccionar entre Arquero, Mago o Caballero.
 */
public class SeleccionPersonajeScreen extends PantallaBase {
    private final RPGGame game;
    private Texture texArquero, texMago, texCaballero;

    public SeleccionPersonajeScreen(RPGGame game) {
        this.game = game;
    }

    @Override
    protected void initUI() {
        // Cargar texturas de los personajes
        texArquero   = new Texture(Gdx.files.internal("PersonajesPrincipales/Arquero/arquero.png"));
        texMago      = new Texture(Gdx.files.internal("PersonajesPrincipales/Mago/mago.png"));
        texCaballero = new Texture(Gdx.files.internal("PersonajesPrincipales/Caballero/caballero.png"));

        // Crear botones de imagen
        ImageButton btnArquero = new ImageButton(new TextureRegionDrawable(new TextureRegion(texArquero)));
        ImageButton btnMago    = new ImageButton(new TextureRegionDrawable(new TextureRegion(texMago)));
        ImageButton btnCaballero = new ImageButton(new TextureRegionDrawable(new TextureRegion(texCaballero)));

        btnArquero.addListener(event -> {
            if (btnArquero.isPressed()) {
                game.setSelectedClass("Arquero");
                game.setScreen(new DungeonScreen(game.getSelectedClass()));
            }
            return false;
        });
        btnMago.addListener(event -> {
            if (btnMago.isPressed()) {
                game.setSelectedClass("Mago");
                game.setScreen(new DungeonScreen(game.getSelectedClass()));
            }
            return false;
        });
        btnCaballero.addListener(event -> {
            if (btnCaballero.isPressed()) {
                game.setSelectedClass("Caballero");
                game.setScreen(new DungeonScreen(game.getSelectedClass()));
            }
            return false;
        });

        // Organizar con tabla
        Table table = new Table(skin);
        table.setFillParent(true);
        table.center();
        stage.addActor(table);

        float size = 150f, pad = 30f;
        table.add(btnArquero).size(size).pad(pad);
        table.add(btnMago).size(size).pad(pad);
        table.add(btnCaballero).size(size).pad(pad);
    }

    @Override
    public void dispose() {
        super.dispose();
        texArquero.dispose();
        texMago.dispose();
        texCaballero.dispose();
    }
}
