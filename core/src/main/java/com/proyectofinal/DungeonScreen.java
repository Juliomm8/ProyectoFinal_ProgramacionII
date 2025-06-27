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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
/**
 * Pantalla principal con mundo procedural "infinito", usando variantes de pasto.
 */
public class DungeonScreen extends PantallaBase {
    private static final int MAP_WIDTH = 150;
    private static final int MAP_HEIGHT = 150;
    private static final int spawnTileX = MAP_WIDTH / 2;  // = 50
    private static final int spawnTileY = MAP_HEIGHT / 2;  // = 50
    private final long seed = System.currentTimeMillis();

    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera cam;
    private Stage stage;
    private PlayerActor playerActor;
    private MapaProcedural generator;
    private final String playerClass;

    private Texture[] texPastoVVariants;
    private Texture texPastoA, texCamino, texHierbaV, texHierbaA;

    private Jugador jugador;

    // Tamaño de cada tile en píxeles (ajústalo a tu proyecto)
    private static final int TILE_SIZE = 32;

    // Jugador y pociones
    private Texture texPlayer, texHP, texEXP, texMana, texEscudo, texMunicion;

    // Añadir esta lista para manejar los enemigos
    private List<Minotauro> enemigos;
    private static final int NUMERO_INICIAL_MINOTAUROS = 10;
    private static final float DISTANCIA_MINIMA_SPAWN = 500f; // Distancia mínima al jugador para spawn
    private static final float TIEMPO_ENTRE_SPAWNS = 2f; // Tiempo entre oleadas de spawn (segundos)
    private static final int MINOTAUROS_POR_OLEADA = 3; // Cantidad de minotauros por oleada
    private static final int MAX_MINOTAUROS = 30; // Máximo de minotauros simultáneos
    private float tiempoUltimoSpawn = 0f; // Tiempo desde el último spawn

    public DungeonScreen(String playerClass) {
        this.playerClass = playerClass;
        jugador = new Jugador("Héroe", 100, 10, 100f, 100f, 32f, 32f, 1);  // Esto pasa todos los parámetros necesarios
        initUI();
    }

    // Este método fue fusionado con manejarEntrada
    // para evitar actualización duplicada

    @Override
    protected void initUI() {
        // 2.1) Batch, fuente y cámara
        batch = new SpriteBatch();
        font = new BitmapFont();
        cam = new OrthographicCamera();
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

        // Inicializar la lista de enemigos
        enemigos = new ArrayList<>();

        // Generar los minotauros iniciales
        generarMinotauros();

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
        texPastoA = new Texture("Mapa/Pasto/pastoAmarillo.png");
        texCamino = new Texture("Mapa/Piedras/piedras.png");
        texHierbaV = new Texture("Mapa/Pasto/hiervaVerde.png");
        texHierbaA = new Texture("Mapa/Pasto/hiervaAmarilla.png");

        // 6) Cargar y colocar pociones (idéntico al tuyo)
        texHP = new Texture("Pociones/pocionHP.png");
        texEXP = new Texture("Pociones/pocionXP.png");
        crearPocionActor(new PocionHP("Poción Vida", 30), texHP, 100, 150);
        crearPocionActor(new PocionEXP("Poción EXP", 1), texEXP, 200, 150);
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

    /**
     * Genera una oleada de minotauros en ubicaciones fuera de la cámara
     * siguiendo el estilo de Vampire Survivors.
     */
    private void spawnMinotaurosOleada() {
        Random random = new Random();
        int minotaurosGenerados = 0;

        // Obtener dimensiones de la cámara
        float camWidth = cam.viewportWidth * cam.zoom;
        float camHeight = cam.viewportHeight * cam.zoom;

        // Determinar los límites de la cámara actual
        float camLeftX = cam.position.x - camWidth / 2;
        float camRightX = cam.position.x + camWidth / 2;
        float camBottomY = cam.position.y - camHeight / 2;
        float camTopY = cam.position.y + camHeight / 2;

        // Ampliar el área de spawn para que sea fuera de la cámara pero no demasiado lejos
        float margenExterior = 300f;
        float minSpawnX = camLeftX - margenExterior;
        float maxSpawnX = camRightX + margenExterior;
        float minSpawnY = camBottomY - margenExterior;
        float maxSpawnY = camTopY + margenExterior;

        // Asegurar que no nos salgamos de los límites del mapa
        minSpawnX = Math.max(minSpawnX, 0);
        maxSpawnX = Math.min(maxSpawnX, MAP_WIDTH * TILE_SIZE);
        minSpawnY = Math.max(minSpawnY, 0);
        maxSpawnY = Math.min(maxSpawnY, MAP_HEIGHT * TILE_SIZE);

        while (minotaurosGenerados < MINOTAUROS_POR_OLEADA && enemigos.size() < MAX_MINOTAUROS) {
            // Decidir en qué lado de la cámara aparecerá (0: arriba, 1: derecha, 2: abajo, 3: izquierda)
            int lado = random.nextInt(4);

            float spawnX, spawnY;

            switch (lado) {
                case 0: // Arriba
                    spawnX = minSpawnX + random.nextFloat() * (maxSpawnX - minSpawnX);
                    spawnY = camTopY + random.nextFloat() * margenExterior;
                    break;
                case 1: // Derecha
                    spawnX = camRightX + random.nextFloat() * margenExterior;
                    spawnY = minSpawnY + random.nextFloat() * (maxSpawnY - minSpawnY);
                    break;
                case 2: // Abajo
                    spawnX = minSpawnX + random.nextFloat() * (maxSpawnX - minSpawnX);
                    spawnY = camBottomY - random.nextFloat() * margenExterior;
                    break;
                case 3: // Izquierda
                default:
                    spawnX = camLeftX - random.nextFloat() * margenExterior;
                    spawnY = minSpawnY + random.nextFloat() * (maxSpawnY - minSpawnY);
                    break;
            }

            // Asegurar que la posición esté dentro del mapa
            spawnX = Math.max(0, Math.min(spawnX, MAP_WIDTH * TILE_SIZE - 64));
            spawnY = Math.max(0, Math.min(spawnY, MAP_HEIGHT * TILE_SIZE - 64));

            // Verificar que no esté en un camino o colisionando con árboles
            boolean posicionValida = true;
            int tileX = (int) (spawnX / TILE_SIZE);
            int tileY = (int) (spawnY / TILE_SIZE);

            // Verificar que no esté en un camino
            if (generator.getTile(tileX, tileY) == MapaProcedural.Tile.CAMINO) {
                continue;
            }

            // Verificar colisiones con árboles
            Rectangle spawnArea = new Rectangle(spawnX, spawnY, 64, 64);
            for (Arbol arbol : generator.getArboles()) {
                if (spawnArea.overlaps(arbol.getCollider())) {
                    posicionValida = false;
                    break;
                }
            }

            if (posicionValida) {
                // Verificar si no está demasiado cerca de otros minotauros para evitar agrupaciones
                boolean demasiadoCerca = false;
                for (Enemigo otroEnemigo : enemigos) {
                    // Usar los valores directos de x, y
                    float distancia = Vector2.dst(spawnX, spawnY, otroEnemigo.x, otroEnemigo.y);
                    if (distancia < 150) { // Distancia mínima aumentada entre minotauros
                        demasiadoCerca = true;
                        break;
                    }
                }

                if (!demasiadoCerca) {
                    Minotauro minotauro = new Minotauro(spawnX, spawnY);
                    // Establecer estado inmediatamente para que persiga al jugador
                    minotauro.estadoActual = Enemigo.EstadoEnemigo.RUNNING;
                    enemigos.add(minotauro);
                    minotaurosGenerados++;
                    minotauro.update(0.016f, playerActor.getX(), playerActor.getY());
                    enemigos.add(minotauro);
                    minotaurosGenerados++;
                }
            }
        }
    }

    private void generarMinotauros() {
        enemigos = new ArrayList<>();
        // El spawn inicial será manejado por el sistema de oleadas
        // para simular el comportamiento de Vampire Survivors
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

        // Renderizar minotauros
        batch.begin();
        for (Minotauro minotauro : enemigos) {
            minotauro.render(batch);
        }
        batch.end();

        // Verificar colisiones con el jugador y aplicar daño
        Rectangle jugadorBounds = playerActor.getBounds();
        Iterator<Minotauro> iterMinotauros = enemigos.iterator();
        while (iterMinotauros.hasNext()) {
            Minotauro minotauro = iterMinotauros.next();

            // Comprobar si el minotauro está vivo
            if (!minotauro.estaVivo()) {
                // Verificar si la animación de muerte ha terminado
                if (minotauro.deathAnimation.isAnimationFinished(minotauro.stateTime)) {
                    iterMinotauros.remove();
                }
                continue;
            }

            // Verificar colisión y aplicar daño si el minotauro está atacando
            if (minotauro.getHitbox().overlaps(jugadorBounds) &&
                minotauro.estadoActual == Enemigo.EstadoEnemigo.ATTACKING) {
                playerActor.getJugador().recibirDanio(minotauro.getDanio());
            }
        }

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

        // Sistema de spawn de minotauros estilo Vampire Survivors
        tiempoUltimoSpawn += delta;
        if (tiempoUltimoSpawn >= TIEMPO_ENTRE_SPAWNS && enemigos.size() < MAX_MINOTAUROS) {
            spawnMinotaurosOleada();
            tiempoUltimoSpawn = 0f;
        }

        // Actualizar todos los minotauros
        Iterator<Minotauro> iterator = enemigos.iterator();
        while (iterator.hasNext()) {
            Minotauro minotauro = iterator.next();
            if (minotauro.estaVivo()) {
                minotauro.update(delta, playerActor.getX(), playerActor.getY());
            } else {
                // Si la animación de muerte ha terminado
                if (minotauro.deathAnimation.isAnimationFinished(minotauro.stateTime)) {
                    iterator.remove(); // Eliminar minotauros muertos
                }
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
        // Liberar recursos de los enemigos si tienen texturas propias
        for (Minotauro minotauro : enemigos) {
            // Si tienes texturas que liberar en los minotauros, hazlo aquí
        }
    }
}
