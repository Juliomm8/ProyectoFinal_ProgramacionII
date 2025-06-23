package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Pantalla principal con mundo procedural “infinito”, usando variantes de pasto.
 */
public class DungeonScreen extends PantallaBase {
    private final String playerClass;
    private PlayerActor playerActor;

    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera cam;

    // Mapa procedural
    private MapaProcedural generator;
    private final int tileSize = 32;
    private Texture[] texPastoV_variants;    // tamaño 4
    private Texture   texPastoA, texCamino, texHierbaV, texHierbaA;


    // posición lógica del jugador en el mundo (en píxeles)
    private float worldX, worldY;

    // Jugador y pociones
    private Texture texPlayer, texHP, texEXP, texMana, texEscudo, texMunicion;

    public DungeonScreen(String playerClass) {
        super();
        this.playerClass = playerClass;
    }

    @Override
    protected void initUI() {
        // 1) Batch, fuente y cámara
        batch = new SpriteBatch();
        font  = new BitmapFont();
        cam   = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.zoom = 0.6f; // ajústalo al gusto

        // 2) Crear jugador y actor
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
        // Posición de spawn en píxeles (por ejemplo)
        float spawnPx = 50, spawnPy = 50;
        playerActor.setPosition(spawnPx, spawnPy);
        stage.addActor(playerActor);

        // 3) Inicializar el mapa procedural (ahora solo con semilla)
        int mapWidth  = 200;  // o el ancho que quieras
        int mapHeight = 150;  // o el alto que quieras
        long seed     = System.currentTimeMillis();

        generator = new MapaProcedural(mapWidth, mapHeight, seed);

        // 4) Cargar texturas de tiles
        texPastoV_variants = new Texture[4];
        for (int i = 0; i < 4; i++) {
            texPastoV_variants[i] = new Texture(
                Gdx.files.internal("Mapa/Pasto/pastoVerde_" + i + ".png")
            );
        }
        texPastoA  = new Texture(Gdx.files.internal("Mapa/Pasto/pastoAmarillo.png"));
        texCamino  = new Texture(Gdx.files.internal("Mapa/Piedras/piedras.png"));
        texHierbaV = new Texture(Gdx.files.internal("Mapa/Pasto/hiervaVerde.png"));
        texHierbaA = new Texture(Gdx.files.internal("Mapa/Pasto/hiervaAmarilla.png"));

        // 5) Cargar y colocar pociones
        texHP  = new Texture(Gdx.files.internal("Pociones/pocionHP.png"));
        texEXP = new Texture(Gdx.files.internal("Pociones/pocionXP.png"));
        crearPocionActor(new PocionHP("Poción Vida", 30), texHP,  100, 150);
        crearPocionActor(new PocionEXP("Poción EXP",  1),  texEXP, 200, 150);
        switch (playerClass) {
            case "Arquero":
                texMunicion = new Texture(Gdx.files.internal("Pociones/pocionMunicion.png"));
                crearPocionActor(new PocionFlechas("Poción Munición", 5), texMunicion, 300, 150);
                break;
            case "Mago":
                texMana = new Texture(Gdx.files.internal("Pociones/pocionMana.png"));
                crearPocionActor(new PocionMana("Poción Maná", 20), texMana, 300, 150);
                break;
            case "Caballero":
                texEscudo = new Texture(Gdx.files.internal("Pociones/pocionEscudo.png"));
                crearPocionActor(new PocionMana("Poción Escudo", 20), texEscudo, 300, 150);
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
        // 1) Limpiar pantalla
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2) Mover jugador
        manejarEntrada(delta);

        // 3) Centrar la cámara “mundo” en el jugador
        float px = playerActor.getX() + playerActor.getWidth()  / 2f;
        float py = playerActor.getY() + playerActor.getHeight() / 2f;
        cam.position.set(px, py, 0f);
        cam.update();

        // 4) Sincronizar cámara de Stage
        OrthographicCamera stageCam = (OrthographicCamera) stage.getViewport().getCamera();
        stageCam.position.set(cam.position);
        stageCam.zoom = cam.zoom;
        stageCam.update();

        // 5) Aplicar proyección al SpriteBatch
        batch.setProjectionMatrix(cam.combined);

        // 6) Calcular rango de tiles visibles
        float halfW = cam.viewportWidth  / 2f;
        float halfH = cam.viewportHeight / 2f;
        int minX = (int)((px - halfW) / tileSize) - 1;
        int maxX = (int)((px + halfW) / tileSize) + 1;
        int minY = (int)((py - halfH) / tileSize) - 1;
        int maxY = (int)((py + halfH) / tileSize) + 1;

        // 7) Dibujar mundo (tiles + overlays)
        batch.begin();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                MapaProcedural.Tile t = generator.getTile(x, y);
                Texture baseTex;
                if (t == MapaProcedural.Tile.CAMINO) {
                    baseTex = texCamino;
                } else if (t == MapaProcedural.Tile.PASTO_AMARILLO) {
                    baseTex = texPastoA;
                } else {
                    // variante de pasto verde según (x,y)
                    int idx = (x&1) + ((y&1)<<1);
                    baseTex = texPastoV_variants[idx];
                }
                batch.draw(baseTex, x*tileSize, y*tileSize, tileSize, tileSize);

                if (generator.hasOverlayVerde(x, y))
                    batch.draw(texHierbaV, x*tileSize, y*tileSize, tileSize, tileSize);
                if (generator.hasOverlayAmarillo(x, y))
                    batch.draw(texHierbaA, x*tileSize, y*tileSize, tileSize, tileSize);
            }
        }

        batch.end();

        // 8) Dibujar actores (jugador + pociones) con la cámara del Stage
        stage.act(delta);
        stage.draw();

        // 9) Colisiones poción <-> jugador
        Rectangle bounds = playerActor.getBounds();
        for (Actor a : stage.getActors()) {
            if (a instanceof PocionActor pa && pa.getBounds().overlaps(bounds)) {
                playerActor.getJugador().recogerPocion(pa.getPocion());
                pa.remove();
            }
        }

        // 10) HUD y lógica extra del arquero
        batch.begin();
        playerActor.dibujarHUD(batch, font);
        if (playerActor.getJugador() instanceof Arquero ar) {
            ar.actualizar(delta);
        }
        batch.end();
    }


    /**
     * Usa isKeyPressed para mover el actor directamente.
     */
    private void manejarEntrada(float delta) {
        float speed = 200f * delta;
        float dx = 0f, dy = 0f;
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += speed;

        if (dx != 0 || dy != 0) {
            playerActor.moveBy(dx, dy);
        }
    }

    @Override
    public void dispose() {
        super.dispose(); batch.dispose(); font.dispose();
        texPlayer.dispose(); texHP.dispose(); texEXP.dispose();
        if (texMana!=null) texMana.dispose(); if (texEscudo!=null) texEscudo.dispose();
        if (texMunicion!=null) texMunicion.dispose();
        for (Texture t: texPastoV_variants) t.dispose();
        texPastoA.dispose(); texCamino.dispose(); texHierbaV.dispose(); texHierbaA.dispose();
    }
}
