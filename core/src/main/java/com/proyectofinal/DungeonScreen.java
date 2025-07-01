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

    private GestionPociones gestionPociones;
    private PlayerHUD playerHUD;
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

    // Jugador y textura para el personaje
    private Texture texPlayer;

    // Añadir esta lista para manejar los enemigos
    private List<Enemigo> enemigos;
    private static final int NUMERO_INICIAL_MINOTAUROS = 10;
    private static final float DISTANCIA_MINIMA_SPAWN = 500f; // Distancia mínima al jugador para spawn
    private static final float TIEMPO_ENTRE_SPAWNS = 4f; // Tiempo entre oleadas de spawn (segundos) - aumentado para reducir frecuencia
    private static final int MINOTAUROS_POR_OLEADA = 3; // Cantidad de minotauros por oleada
    private static final int MAX_MINOTAUROS = 30; // Máximo de minotauros simultáneos
    private float tiempoUltimoSpawn = 0f; // Tiempo desde el último spawn

    public DungeonScreen(String playerClass) {
        this.playerClass = playerClass;
        jugador = new Jugador("Héroe", 100, 10, 100f, 100f, 32f, 32f, 1);  // Esto pasa todos los parámetros necesarios
        initUI();
    }

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
                jugadorLogico = new Caballero("Arthur", 120, 15, 50, 0.8f, 32f, 32f, 100);
                texPlayer = new Texture("PersonajesPrincipales/Caballero/caballero.png");
                break;
            default:
                jugadorLogico = new Jugador("Héroe", 100, 10, 100f, 100f, 32f, 32f, 1);
                texPlayer = new Texture("PersonajesPrincipales/Arquero/arquero.png");
        }
        playerActor = new PlayerActor(jugadorLogico, texPlayer);

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
        playerActor.setStage(stage);
        playerActor.setViewport(stage.getViewport());
        stage.addActor(playerActor);

        // Sistema de generación y gestión de pociones
        gestionPociones = new GestionPociones(stage, generator);

        // Inicializar el HUD del jugador
        playerHUD = new PlayerHUD(playerActor.getJugador());

        // 2.5) Cargar texturas de tiles
        texPastoVVariants = new Texture[4];
        for (int i = 0; i < 4; i++) {
            texPastoVVariants[i] = new Texture("Mapa/Pasto/pastoVerde_" + i + ".png");
        }
        texPastoA = new Texture("Mapa/Pasto/pastoAmarillo.png");
        texCamino = new Texture("Mapa/Piedras/piedras.png");
        texHierbaV = new Texture("Mapa/Pasto/hiervaVerde.png");
        texHierbaA = new Texture("Mapa/Pasto/hiervaAmarilla.png");
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
                }
            }
        }
    }

    private void generarMinotauros() {
        enemigos = new ArrayList<>();
        // El spawn inicial será manejado por el sistema de oleadas
        // para simular el comportamiento de Vampire Survivors
    }

    /**
     * Actualiza la gestión de enemigos en cada frame
     * @param delta tiempo transcurrido desde el último frame
     */
    private void actualizarEnemigos(float delta) {
        // Eliminar enemigos muertos que han completado su animación
        int eliminados = GestionEnemigos.actualizarEnemigos(enemigos);
        if (eliminados > 0) {
            System.out.println("Eliminados " + eliminados + " enemigos muertos");
        }
    }

    @Override
    public void render(float delta) {
        // 1) Limpiar pantalla
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2) Manejar entrada genérica (spawn y actualización de enemigos)
        manejarEntrada(delta);

        // Actualizar sistema de pociones
        gestionPociones.actualizar(delta, playerActor.getBounds(), playerActor.getJugador());

        // 3) Centrar cámara en el jugador
        float px = playerActor.getX() + playerActor.getWidth() * 0.5f;
        float py = playerActor.getY() + playerActor.getHeight() * 0.5f;
        cam.position.set(px, py, 0f);
        cam.update();

        // 4) Sincronizar cámara del Stage
        OrthographicCamera stageCam = (OrthographicCamera)stage.getViewport().getCamera();
        stageCam.position.set(cam.position);
        stageCam.zoom = cam.zoom;
        stageCam.update();

        // 5) Dibujar el mundo (tiles y piedras)
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        // Dibujar tiles con margen de 1
        float halfW = cam.viewportWidth * 0.5f;
        float halfH = cam.viewportHeight * 0.5f;
        int minX = (int)((px - halfW)  / TILE_SIZE) - 1;
        int maxX = (int)((px + halfW)  / TILE_SIZE) + 1;
        int minY = (int)((py - halfH)  / TILE_SIZE) - 1;
        int maxY = (int)((py + halfH)  / TILE_SIZE) + 1;
        for (int y = minY; y <= maxY; y++) {
            for (int x = minX; x <= maxX; x++) {
                MapaProcedural.Tile t = generator.getTile(x, y);
                Texture base = (t == MapaProcedural.Tile.CAMINO ? texCamino
                    : t == MapaProcedural.Tile.PASTO_AMARILLO ? texPastoA
                    : texPastoVVariants[(x & 1) + ((y & 1) << 1)]);
                batch.draw(base, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                if (generator.hasOverlayVerde(x, y))
                    batch.draw(texHierbaV, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                if (generator.hasOverlayAmarillo(x, y))
                    batch.draw(texHierbaA, x * TILE_SIZE, y * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }
        for (Piedra p : generator.getPiedras()) {
            p.render(batch);
        }
        batch.end();

        // 6) Actualizar lógica y dibujar al jugador + pociones
        playerActor.update(delta, enemigos);
        stage.act(delta);
        stage.draw();

        // 7) Dibujar enemigos sobre el suelo
        batch.begin();
        for (Enemigo e : enemigos) {
            e.render(batch);
        }
        batch.end();

        // 8) Colisiones enemigo→jugador
        Rectangle pjBounds = playerActor.getBounds();
        Iterator<Enemigo> itE = enemigos.iterator();
        while (itE.hasNext()) {
            Enemigo e = itE.next();
            if (e.isReadyToRemove()) {
                itE.remove();
                continue;
            }
            if (e.getHitbox().overlaps(pjBounds)
                && e.estadoActual == Enemigo.EstadoEnemigo.ATTACKING) {
                playerActor.getJugador().recibirDanio(10);
            }
        }

        // 10) Dibujar árboles
        batch.begin();
        for (Arbol ar : generator.getArboles()) {
            ar.render(batch);
        }
        batch.end();

        // 11) HUD y lógica específica de clase
        batch.begin();
        // Renderizar el HUD con posición fija en la pantalla
        batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0,
            com.badlogic.gdx.Gdx.graphics.getWidth(),
            com.badlogic.gdx.Gdx.graphics.getHeight()));

        // Usar PlayerHUD para mostrar información fija en pantalla
        playerHUD.render(batch);

        // Restaurar la matriz de proyección a la cámara del juego
        batch.setProjectionMatrix(cam.combined);

        // Continuar con el HUD normal seguido al jugador
        playerActor.dibujarHUD(batch, font);
        if (playerActor.getJugador() instanceof Arquero arq) {
            arq.actualizar(delta);
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

        // Actualizar todos los enemigos y eliminar los que hayan terminado su animación
        Iterator<Enemigo> iterator = enemigos.iterator();
        while (iterator.hasNext()) {
            Enemigo enemigo = iterator.next();
            enemigo.update(delta, playerActor.getX(), playerActor.getY());
            if (enemigo.isReadyToRemove()) {
                iterator.remove();
            }
        }
    }

    @Override
    public void dispose() {
        try {
            super.dispose();

            // Liberar recursos básicos
            if (batch != null) batch.dispose();
            if (font != null) font.dispose();

            // Liberar textura del jugador
            if (texPlayer != null) texPlayer.dispose();

            // Liberar texturas de terreno
            if (texPastoVVariants != null) {
                for (Texture t : texPastoVVariants) {
                    if (t != null) t.dispose();
                }
            }
            if (texPastoA != null) texPastoA.dispose();
            if (texCamino != null) texCamino.dispose();
            if (texHierbaV != null) texHierbaV.dispose();
            if (texHierbaA != null) texHierbaA.dispose();

            // Liberar recursos del PlayerActor
            if (playerActor != null) {
                playerActor.dispose();
            }

            // Liberar recursos del generador de mapa
            if (generator != null) {
                generator.dispose();
            }

            // Liberar recursos de los enemigos
            if (enemigos != null) {
                for (Enemigo enemigo : enemigos) {
                    if (enemigo != null) {
                        enemigo.dispose();
                    }
                }
            }

            // Liberar recursos del sistema de pociones
            if (gestionPociones != null) {
                gestionPociones.dispose();
            }
            // Liberar recursos del HUD
            if (playerHUD != null) {
                playerHUD.dispose();
            }
        } catch (Exception e) {
            System.err.println("Error al liberar recursos en DungeonScreen: " + e.getMessage());
        }
    }
}
