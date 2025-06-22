package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.math.Vector2;

public class DungeonScreen extends PantallaBase {
    private final String playerClass;
    private PlayerActor  playerActor;

    private SpriteBatch batch;
    private BitmapFont  font;

    private Texture texPlayer;
    private Texture texHP, texEXP, texMana, texEscudo, texMunicion;

    public DungeonScreen(String playerClass) {
        super();
        this.playerClass = playerClass;
    }

    @Override
    protected void initUI() {
        // 1) Batch y fuente para HUD
        batch = new SpriteBatch();
        font  = new BitmapFont();

        // 2) Crear Jugador lógico y actor visual
        Jugador jugadorLogico;
        switch (playerClass) {
            case "Arquero":
                jugadorLogico = new Arquero("Legolas", 100, 15, 0.8f, 10);
                texPlayer     = new Texture(Gdx.files.internal("PersonajesPrincipales/Arquero/arquero.png"));
                break;
            case "Mago":
                jugadorLogico = new Mago("Gandalf", 80, 12, 50);
                texPlayer     = new Texture(Gdx.files.internal("PersonajesPrincipales/Mago/mago.png"));
                break;
            case "Caballero":
                jugadorLogico = new Caballero("Arthur", 120, 15);
                texPlayer     = new Texture(Gdx.files.internal("PersonajesPrincipales/Caballero/caballero.png"));
                break;
            default:
                jugadorLogico = new Jugador("Héroe", 100, 10);
                texPlayer     = new Texture(Gdx.files.internal("PersonajesPrincipales/Arquero/arquero.png"));
        }
        playerActor = new PlayerActor(jugadorLogico, texPlayer);
        playerActor.setPosition(50, 50);
        stage.addActor(playerActor);

        // 3) Carga texturas de las pociones comunes
        texHP  = new Texture(Gdx.files.internal("Pociones/pocionHP.png"));
        texEXP = new Texture(Gdx.files.internal("Pociones/pocionXP.png"));

        // 4) Añade siempre HP y EXP
        crearPocionActor(new PocionHP("Poción Vida", 30), texHP,  100, 150);
        crearPocionActor(new PocionEXP("Poción EXP", 1),   texEXP, 200, 150);

        // 5) Según la clase, añade la tercera poción
        switch (playerClass) {
            case "Arquero":
                texMunicion = new Texture(Gdx.files.internal("Pociones/pocionMunicion.png"));
                crearPocionActor(new PocionFlechas("Poción Munición", 5),
                    texMunicion, 300, 150);
                break;
            case "Mago":
                texMana = new Texture(Gdx.files.internal("Pociones/pocionMana.png"));
                crearPocionActor(new PocionMana("Poción Maná", 20),
                    texMana,      300, 150);
                break;
            case "Caballero":
                texEscudo = new Texture(Gdx.files.internal("Pociones/pocionEscudo.png"));
                crearPocionActor(new PocionMana("Poción Escudo", 20),
                    texEscudo,   300, 150);
                break;
        }
    }

    private void crearPocionActor(Pocion pocion, Texture tex, float x, float y) {
        PocionActor actor = new PocionActor(pocion, tex);
        actor.setPosition(x, y);
        stage.addActor(actor);
    }

    @Override
    public void render(float delta) {
        manejarEntrada(delta);
        // 1) Limpiar y dibujar el stage
        super.render(delta);

        // 2) Detección de colisión poción <-> jugador
        Rectangle playerBounds = playerActor.getBounds();
        for (Actor a : stage.getActors()) {
            if (a instanceof PocionActor) {
                PocionActor pa = (PocionActor) a;
                if (pa.getBounds().overlaps(playerBounds)) {
                    playerActor.getJugador().recogerPocion(pa.getPocion());
                    pa.remove();
                }
            }
        }

        // 3) HUD y lógica de ráfaga del arquero
        batch.begin();
        playerActor.dibujarHUD(batch, font);
        if (playerActor.getJugador() instanceof Arquero) {
            ((Arquero)playerActor.getJugador()).actualizar(delta);
        }
        batch.end();
    }

    @Override
    public void dispose() {
        super.dispose();  // Stage y Skin
        batch.dispose();
        font.dispose();
        texPlayer.dispose();
        texHP.dispose();
        texEXP.dispose();
        if (texMana    != null) texMana.dispose();
        if (texEscudo  != null) texEscudo.dispose();
        if (texMunicion!= null) texMunicion.dispose();
    }


    /**
     * Procesa las teclas WASD o flechas para mover al jugador.
     * @param delta tiempo transcurrido desde el último frame
     */
    private void manejarEntrada(float delta) {
        float velocidad = 200f; // píxeles por segundo

        // 1) Calcula la dirección de movimiento
        Vector2 dir = new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP)) {
            dir.y += 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN)) {
            dir.y -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            dir.x -= 1;
        }
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            dir.x += 1;
        }

        // 2) Si hay movimiento, normaliza y escala por velocidad·delta
        if (dir.len2() > 0) {
            dir.nor().scl(velocidad * delta);
            float nuevaX = playerActor.getX() + dir.x;
            float nuevaY = playerActor.getY() + dir.y;

            // 3) Limitar dentro de la ventana
            nuevaX = Math.max(0, Math.min(nuevaX,
                Gdx.graphics.getWidth()  - playerActor.getWidth()));
            nuevaY = Math.max(0, Math.min(nuevaY,
                Gdx.graphics.getHeight() - playerActor.getHeight()));

            playerActor.setPosition(nuevaX, nuevaY);
        }
    }
}
