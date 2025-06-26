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
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.viewport.ScreenViewport;
/**
 * Pantalla principal con mundo procedural "infinito", usando variantes de pasto.
 */
public class DungeonScreen extends PantallaBase {
    private static final int MAP_WIDTH   = 150;
    private static final int MAP_HEIGHT  = 150;
    private static final int spawnTileX  = MAP_WIDTH  / 2;  // = 50
    private static final int spawnTileY  = MAP_HEIGHT / 2;  // = 50
    private final long seed               = System.currentTimeMillis();

    private SpriteBatch       batch;
    private BitmapFont        font;
    private OrthographicCamera cam;
    private Stage             stage;
    private PlayerActor       playerActor;
    private MapaProcedural    generator;
    private final String playerClass;

    private Texture[] texPastoVVariants;
    private Texture   texPastoA, texCamino, texHierbaV, texHierbaA;

    private Jugador jugador;

    // Tamaño de cada tile en píxeles (ajústalo a tu proyecto)
    private static final int TILE_SIZE = 32;

    // Jugador y pociones
    private Texture texPlayer, texHP, texEXP, texMana, texEscudo, texMunicion;

    public DungeonScreen(String playerClass) {
        this.playerClass = playerClass;
        jugador = new Jugador("Héroe", 100, 10, 100f, 100f, 32f, 32f, 1);  // Esto pasa todos los parámetros necesarios
        initUI();
    }

    public void update(float delta) {
        float direccionX = 0, direccionY = 0;

        // Detectar las teclas presionadas para mover al jugador
        if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) direccionX = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) direccionX = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.UP)) direccionY = 1;
        if (Gdx.input.isKeyPressed(Input.Keys.DOWN)) direccionY = -1;

        // Llamar al método de movimiento del jugador
        jugador.mover(direccionX, direccionY, delta);

        // Actualizar la posición del jugador en la pantalla
        // Si es necesario, puedes llamar a setPosition para ajustar la posición en el mundo
        playerActor.setPosition(jugador.getX(), jugador.getY());
    }

    @Override
    protected void initUI() {
        // 2.1) Batch, fuente y cámara
        batch = new SpriteBatch();
        font  = new BitmapFont();
        cam   = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.zoom = 0.6f; // ajustable

        // 2.2) Inicializar el mapa procedural (ahora sí con width, height, seed y spawn)
        generator = new MapaProcedural(
            MAP_WIDTH, MAP_HEIGHT, seed, spawnTileX, spawnTileY
        );

        // 2.3) Crear jugador y actor

        Jugador jugadorLogico;
        switch (playerClass) {
            case "Arquero":
                jugadorLogico = new Arquero("Legolas", 100, 15, 0.8f, 10, 32f, 32f, 20, 15);
                texPlayer = new Texture("PersonajesPrincipales/Arquero/arquero.png");
                break;
            case "Mago":
                jugadorLogico = new Mago("Gandalf", 80, 12, 50, 0.8f, 32f, 32f, 1);
                texPlayer = new Texture("PersonajesPrincipales/Mago/mago.png");
                break;
            case "Caballero":
                jugadorLogico = new Caballero("Arthur", 120, 15, 50, 0.8f, 32f, 32f, 1);
                texPlayer = new Texture("PersonajesPrincipales/Caballero/caballero.png");
                break;
            default:
                jugadorLogico = new Jugador("Héroe", 100, 10, 100f, 100f, 32f, 32f, 1);
                texPlayer = new Texture("PersonajesPrincipales/Arquero/arquero.png");
        }
        playerActor = new PlayerActor(jugadorLogico, texPlayer);  // Asegúrate de que esta línea se ejecute



        // Asegúrate de que playerActor esté inicializado antes de usarlo
        float spawnPx = spawnTileX * TILE_SIZE;
        float spawnPy = spawnTileY * TILE_SIZE;
        playerActor.setPosition(spawnPx, spawnPy);
        cam.position.set(spawnPx, spawnPy, 0f);
        cam.update();

        stage = new Stage(new ScreenViewport(cam), batch);
        stage.addActor(playerActor);

        // 2.5) Cargar texturas de tiles
        texPastoVVariants = new Texture[4];
        for (int i = 0; i < 4; i++) {
            texPastoVVariants[i] = new Texture("Mapa/Pasto/pastoVerde_" + i + ".png");
        }
        texPastoA  = new Texture("Mapa/Pasto/pastoAmarillo.png");
        texCamino  = new Texture("Mapa/Piedras/piedras.png");
        texHierbaV = new Texture("Mapa/Pasto/hiervaVerde.png");
        texHierbaA = new Texture("Mapa/Pasto/hiervaAmarilla.png");


        // 6) Cargar y colocar pociones (idéntico al tuyo)
        texHP  = new Texture("Pociones/pocionHP.png");
        texEXP = new Texture("Pociones/pocionXP.png");
        crearPocionActor(new PocionHP("Poción Vida", 30), texHP,  100, 150);
        crearPocionActor(new PocionEXP("Poción EXP",  1),  texEXP, 200, 150);
        switch (playerClass) {
            case "Arquero":
                texMunicion = new Texture("Pociones/pocionMunicion.png");
                crearPocionActor(new PocionFlechas("Poción Munición", 5), texMunicion, 300, 150);
                break;
            case "Mago":
                texMana = new Texture("Pociones/pocionMana.png");
                crearPocionActor(new PocionMana("Poción Maná", 20), texMana, 300, 150);
                break;
            case "Caballero":
                texEscudo = new Texture("Pociones/pocionEscudo.png");
                crearPocionActor(new PocionMana("Poción Escudo", 20), texEscudo, 300, 150);
                break;
        }
        // 9) Detección de colisiones poción–jugador
        Rectangle pjBounds = playerActor.getBounds();
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

        // 2) Procesar entrada y mover jugador
        manejarEntrada(delta);

        // 3) Centrar cámara en el jugador
        float px = playerActor.getX() + playerActor.getWidth() * 0.5f;
        float py = playerActor.getY() + playerActor.getHeight() * 0.5f;
        cam.position.set(px, py, 0f);
        cam.update();

        // 4) Sincronizar cámara del stage
        OrthographicCamera stageCam = (OrthographicCamera) stage.getViewport().getCamera();
        stageCam.position.set(cam.position);
        stageCam.zoom = cam.zoom;
        stageCam.update();

        // 5) Preparar batch para el mundo
        batch.setProjectionMatrix(cam.combined);

        // 6) Calcular rango de tiles visibles (con un margen de 1 tile para evitar huecos)
        float halfW = cam.viewportWidth * 0.5f;
        float halfH = cam.viewportHeight * 0.5f;
        int minX = (int)((px - halfW) / TILE_SIZE) - 1;
        int maxX = (int)((px + halfW) / TILE_SIZE) + 1;
        int minY = (int)((py - halfH) / TILE_SIZE) - 1;
        int maxY = (int)((py + halfH) / TILE_SIZE) + 1;

        // -------- DIBUJO DE FONDO Y OBJETOS DEL SUELO --------
        batch.begin();
        // 7) Dibujar tiles y overlays (SIEMPRE primero)
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                // Obtener tipo de tile
                MapaProcedural.Tile t = generator.getTile(x, y);

                // Seleccionar textura base
                Texture baseTex;
                switch (t) {
                    case CAMINO:
                        baseTex = texCamino;
                        break;
                    case PASTO_AMARILLO:
                        baseTex = texPastoA;
                        break;
                    default:
                        int idx = (x & 1) + ((y & 1) << 1);
                        baseTex = texPastoVVariants[idx];
                }
                // Dibujar base
                batch.draw(baseTex, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);

                // Dibujar overlays
                if (generator.hasOverlayVerde(x, y)) {
                    batch.draw(texHierbaV, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
                if (generator.hasOverlayAmarillo(x, y)) {
                    batch.draw(texHierbaA, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }
            }
        }

        // 8) DIBUJAR PIEDRAS DEL SUELO (debajo del jugador)
        for (Piedra piedra : generator.getPiedras()) {
            piedra.render(batch); // Las piedras están bajo el jugador
        }
        batch.end();

        // 9) Dibujar stage (jugador y pociones, SIEMPRE encima de las piedras)
        stage.act(delta);
        stage.draw();

        // 10) DIBUJAR ÁRBOLES (encima del jugador, si quieres ese efecto)
        batch.begin();
        for (Arbol arbol : generator.getArboles()) {
            arbol.render(batch); // Si los árboles deben tapar al jugador, ponlos aquí
        }
        batch.end();

        // 11) Detección de colisiones poción–jugador
        Rectangle pjBounds = playerActor.getBounds();
        for (Actor a : stage.getActors()) {
            if (a instanceof PocionActor) {
                PocionActor pa = (PocionActor) a;
                if (pa.getBounds().overlaps(pjBounds)) {
                    playerActor.getJugador().recogerPocion(pa.getPocion());
                    pa.remove();
                }
            }
        }

        // 12) HUD y actualizaciones extra (SIEMPRE arriba de todo)
        batch.begin();
        playerActor.dibujarHUD(batch, font);
        if (playerActor.getJugador() instanceof Arquero) {
            ((Arquero) playerActor.getJugador()).actualizar(delta);
        }
        batch.end();
    }


    private void manejarEntrada(float delta) {
        float speed = 200f * delta;
        float dx = 0f, dy = 0f;

        // Verificar las teclas presionadas para mover al jugador
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += speed;

        // Verificar si la nueva posición está dentro de los límites del mapa
        float newX = playerActor.getX() + dx;
        float newY = playerActor.getY() + dy;

        // Verificar límites horizontales
        if (newX < 0) newX = 0;
        if (newX > (MAP_WIDTH * TILE_SIZE) - playerActor.getWidth()) newX = (MAP_WIDTH * TILE_SIZE) - playerActor.getWidth();

        // Verificar límites verticales
        if (newY < 0) newY = 0;
        if (newY > (MAP_HEIGHT * TILE_SIZE) - playerActor.getHeight()) newY = (MAP_HEIGHT * TILE_SIZE) - playerActor.getHeight();

        // Crear un rectángulo para la posición futura del jugador
        Rectangle futurePosition = new Rectangle(newX, newY, playerActor.getWidth(), playerActor.getHeight());

        // Verificar colisiones con troncos de árboles
        boolean colisionConArbol = false;
        for (Arbol arbol : generator.getArboles()) {
            if (futurePosition.overlaps(arbol.getCollider())) {
                colisionConArbol = true;
                break;
            }
        }

        // Las piedras no tienen collider, así que no hay verificación de colisiones

        // Mover el jugador solo si no se sale de los límites y no colisiona con un árbol
        if (dx != 0f || dy != 0f) {
            if (!colisionConArbol) {
                playerActor.setPosition(newX, newY);
            }
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
        if (texMana   != null) texMana.dispose();
        if (texEscudo != null) texEscudo.dispose();
        if (texMunicion != null) texMunicion.dispose();
        for (Texture t : texPastoVVariants) t.dispose();
        texPastoA.dispose();
        texCamino.dispose();
        texHierbaV.dispose();
        texHierbaA.dispose();
    }
}
