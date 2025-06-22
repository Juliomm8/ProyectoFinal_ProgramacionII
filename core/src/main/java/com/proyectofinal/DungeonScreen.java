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
 * Pantalla principal con mundo procedural “infinito”.
 */
public class DungeonScreen extends PantallaBase {
    private final String playerClass;
    private PlayerActor  playerActor;

    private SpriteBatch        batch;
    private BitmapFont         font;
    private OrthographicCamera cam;

    // tu MapaProcedural con constructor(width,height,seed,spawnX,spawnY)
    private MapaProcedural     generator;
    private final int          tileSize = 32;
    private Texture            texPastoV, texPastoA, texCamino;

    // jugador y pociones
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
        // posición de spawn en píxeles
        float spawnPx = 50, spawnPy = 50;
        playerActor.setPosition(spawnPx, spawnPy);
        stage.addActor(playerActor);

        // Vista a tamaño de pantalla...
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.zoom = 0.3f;  // valores entre 0.1 y 1.0 funcionan bien; prueba 0.75f, 0.5f, 0.4f, etc.

        // 3) Inicializar MapaProcedural con spawn en tiles
        int mapWidth   = 200;
        int mapHeight  = 150;
        long seed      = System.currentTimeMillis();
        int spawnTileX = (int)(spawnPx / tileSize);
        int spawnTileY = (int)(spawnPy / tileSize);
        generator      = new MapaProcedural(mapWidth, mapHeight, seed, spawnTileX, spawnTileY);

        // 4) Cargar texturas de tiles
        texPastoV = new Texture(Gdx.files.internal("Mapa/Pasto/pastoVerde.png"));
        texPastoA = new Texture(Gdx.files.internal("Mapa/Pasto/pastoAmarillo.png"));
        texCamino = new Texture(Gdx.files.internal("Mapa/Piedras/piedras.png"));

        // 5) Cargar y posicionar pociones
        texHP  = new Texture(Gdx.files.internal("Pociones/pocionHP.png"));
        texEXP = new Texture(Gdx.files.internal("Pociones/pocionXP.png"));
        crearPocionActor(new PocionHP("Poción Vida", 30), texHP,  100, 150);
        crearPocionActor(new PocionEXP("Poción EXP", 1),   texEXP, 200, 150);
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
        // 1) Limpiar pantalla
        Gdx.gl.glClearColor(0f,0f,0f,1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2) Mover jugador
        manejarEntrada(delta);

        // 3) Cámara al jugador
        cam.position.set(
            playerActor.getX() + playerActor.getWidth()/2,
            playerActor.getY() + playerActor.getHeight()/2,
            0
        );
        cam.update();
        batch.setProjectionMatrix(cam.combined);

        // 4) Dibujar tiles visibles
        int minX = (int)((cam.position.x - cam.viewportWidth/2)/tileSize) - 1;
        int maxX = (int)((cam.position.x + cam.viewportWidth/2)/tileSize) + 1;
        int minY = (int)((cam.position.y - cam.viewportHeight/2)/tileSize) - 1;
        int maxY = (int)((cam.position.y + cam.viewportHeight/2)/tileSize) + 1;

        batch.begin();
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                MapaProcedural.Tile t = generator.getTile(x, y);
                Texture tex = t == MapaProcedural.Tile.CAMINO
                    ? texCamino
                    : (t == MapaProcedural.Tile.PASTO_AMARILLO ? texPastoA : texPastoV);
                batch.draw(tex, x * tileSize, y * tileSize, tileSize, tileSize);
            }
        }
        batch.end();

        // 5) Stage (jugador, pociones…)
        stage.act(delta);
        stage.draw();

        // 6) Colisiones poción <-> jugador & HUD
        Rectangle bounds = playerActor.getBounds();
        for (Actor a : stage.getActors()) {
            if (a instanceof PocionActor) {
                PocionActor pa = (PocionActor)a;
                if (pa.getBounds().overlaps(bounds)) {
                    playerActor.getJugador().recogerPocion(pa.getPocion());
                    pa.remove();
                }
            }
        }
        batch.begin();
        playerActor.dibujarHUD(batch, font);
        if (playerActor.getJugador() instanceof Arquero) {
            ((Arquero)playerActor.getJugador()).actualizar(delta);
        }
        batch.end();
    }

    private void manejarEntrada(float delta) {
        float vel = 200f;
        Vector2 dir = new Vector2();
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dir.y += 1;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dir.y -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dir.x -= 1;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dir.x += 1;
        if (dir.len2() > 0) {
            dir.nor().scl(vel * delta);
            playerActor.moveBy(dir.x, dir.y);
        }
    }

    @Override
    public void dispose() {
        super.dispose();
        batch.dispose();
        font.dispose();
        texPlayer.dispose();
        texHP.dispose();
        texEXP.dispose();
        if (texMana     != null) texMana.dispose();
        if (texEscudo   != null) texEscudo.dispose();
        if (texMunicion != null) texMunicion.dispose();
        texPastoV.dispose();
        texPastoA.dispose();
        texCamino.dispose();
    }
}
