package com.proyectofinal;

// Importaciones necesarias para gráficos, entradas, escena y utilidades
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
 * Pantalla principal del juego. Aquí se gestiona el mapa procedural, la cámara, el HUD,
 * el jugador, los enemigos y el renderizado general del mundo.
 */
public class DungeonScreen extends PantallaBase {

    // Referencia al juego principal
    private final RPGGame juego;

    // Dimensiones del mapa en tiles
    private static final int MAP_WIDTH = 150;
    private static final int MAP_HEIGHT = 150;

    // Coordenadas del punto de aparición inicial del jugador
    private static final int spawnTileX = MAP_WIDTH / 2;
    private static final int spawnTileY = MAP_HEIGHT / 2;

    // Semilla para la generación aleatoria del mapa
    private final long seed = System.currentTimeMillis();

    // Componentes de UI y lógica del juego
    private GestionPociones gestionPociones;
    private PlayerHUD playerHUD;
    private SpriteBatch batch;
    private BitmapFont font;
    private OrthographicCamera cam;
    private Stage stage;
    private PlayerActor playerActor;
    private MapaProcedural generator;
    private final String playerClass;

    // Texturas del terreno
    private Texture[] texPastoVVariants;
    private Texture texPastoA, texCamino, texHierbaV, texHierbaA;

    // Objeto que representa la lógica del jugador (modelo)
    private Jugador jugador;

    // Tamaño de cada tile (casilla del mapa) en pixeles
    private static final int TILE_SIZE = 32;

    // Textura del sprite del personaje del jugador
    private Texture texPlayer;

    // Lista de enemigos activos en la escena
    private List<Enemigo> enemigos;

    // Configuración de generación de enemigos tipo minotauro
    private static final int NUMERO_INICIAL_MINOTAUROS = 10;
    private static final float DISTANCIA_MINIMA_SPAWN = 500f; // Distancia mínima del jugador
    private static final float TIEMPO_ENTRE_SPAWNS = 4f;       // Tiempo entre oleadas de minotauros
    private static final int MINOTAUROS_POR_OLEADA = 3;
    private static final int MAX_MINOTAUROS = 30;
    private float tiempoUltimoSpawn = 0f; // Cronómetro entre oleadas

    // Constructor que recibe la referencia al juego y la clase del jugador seleccionada
    public DungeonScreen(RPGGame juego, String playerClass) {
        this.juego = juego;
        this.playerClass = playerClass;
        jugador = new Jugador("Héroe", 100, 10, 100f, 100f, 32f, 32f, 1); // Placeholder temporal
        initUI(); // Inicializa cámara, mapa, jugador y HUD
    }

    @Override
    protected void initUI() {
        // Inicializa batch de dibujo, fuente para texto y la cámara principal
        batch = new SpriteBatch();
        font = new BitmapFont();
        cam = new OrthographicCamera();
        cam.setToOrtho(false, Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        cam.zoom = 0.6f; // Zoom para alejar la cámara (opcional)

        // Generación del mapa procedural con los parámetros establecidos
        generator = new MapaProcedural(
            MAP_WIDTH, MAP_HEIGHT, seed, spawnTileX, spawnTileY
        );

        // Crea una instancia del jugador según la clase seleccionada por el usuario
        Jugador jugadorLogico;
        switch (playerClass) {
            case "Arquero":
                jugadorLogico = new Arquero("Legolas", 180, 15, 0.8f, 10, 32f, 32f, 40, 15);
                texPlayer = new Texture("PersonajesPrincipales/Arquero/arquero.png");
                break;
            case "Mago":
                jugadorLogico = new Mago("Gandalf", 150, 12, 50, 0.8f, 32f, 32f, 1);
                texPlayer = new Texture("PersonajesPrincipales/Mago/mago.png");
                break;
            case "Caballero":
                jugadorLogico = new Caballero("Arthur", 200, 15, 50, 0.8f, 32f, 32f, 100);
                texPlayer = new Texture("PersonajesPrincipales/Caballero/caballero.png");
                break;
            default:
                jugadorLogico = new Jugador("Héroe", 100, 10, 100f, 100f, 32f, 32f, 1);
                texPlayer = new Texture("PersonajesPrincipales/Arquero/arquero.png");
        }

        // Crear el actor visual del jugador, pasándole la lógica y su textura
        playerActor = new PlayerActor(jugadorLogico, texPlayer);

        // Inicializar la lista donde se almacenarán los enemigos activos
        enemigos = new ArrayList<>();

        // Generar una primera oleada de minotauros
        generarMinotauros();

        // Calcular la posicion en pixeles para colocar al jugador en el centro del mapa
        float spawnPx = spawnTileX * TILE_SIZE;
        float spawnPy = spawnTileY * TILE_SIZE;

        // Colocar al actor y al modelo logico del jugador en esa posicion
        playerActor.setPosition(spawnPx, spawnPy);
        jugadorLogico.setPosition(spawnPx, spawnPy);

        // Ajustar la camara para que inicie centrada en el jugador
        cam.position.set(spawnPx, spawnPy, 0f);
        cam.update();

        // Crear el Stage que gestiona los actores visuales y asignarle la camara y el batch
        stage = new Stage(new ScreenViewport(cam), batch);

        // Configurar playerActor con la referencia al stage y viewport
        playerActor.setStage(stage);
        playerActor.setViewport(stage.getViewport());

        // Agregar el playerActor al stage para que sea renderizado
        stage.addActor(playerActor);

        // Pasar la lista de enemigos al actor para que pueda interactuar con ellos
        playerActor.setEnemigosActuales(enemigos);

        // Crear el sistema de pociones que gestiona las apariciones y efectos
        gestionPociones = new GestionPociones(stage, generator);

        // Crear el HUD del jugador para mostrar vida, flechas, etc.
        playerHUD = new PlayerHUD(playerActor.getJugador());

        // Cargar las texturas de los distintos tipos de terreno
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
     * Genera una oleada de minotauros que aparecen fuera del area visible de la camara.
     * Se inspiran en el estilo de aparicion de enemigos del juego Vampire Survivors.
     */
    private void spawnMinotaurosOleada() {
        Random random = new Random();
        int minotaurosGenerados = 0;

        // Obtener el tamaño actual de la camara considerando el zoom
        float camWidth = cam.viewportWidth * cam.zoom;
        float camHeight = cam.viewportHeight * cam.zoom;

        // Calcular los bordes visibles actuales de la camara
        float camLeftX = cam.position.x - camWidth / 2;
        float camRightX = cam.position.x + camWidth / 2;
        float camBottomY = cam.position.y - camHeight / 2;
        float camTopY = cam.position.y + camHeight / 2;

        // Ampliar los limites para que el spawn ocurra fuera del campo visual
        float margenExterior = 300f;
        float minSpawnX = camLeftX - margenExterior;
        float maxSpawnX = camRightX + margenExterior;
        float minSpawnY = camBottomY - margenExterior;
        float maxSpawnY = camTopY + margenExterior;

        // Ajustar los valores para que no se salgan del tamaño del mapa
        minSpawnX = Math.max(minSpawnX, 0);
        maxSpawnX = Math.min(maxSpawnX, MAP_WIDTH * TILE_SIZE);
        minSpawnY = Math.max(minSpawnY, 0);
        maxSpawnY = Math.min(maxSpawnY, MAP_HEIGHT * TILE_SIZE);

        // Repetir mientras no se haya alcanzado el maximo de minotauros por oleada
        // ni se haya excedido el limite total en pantalla
        while (minotaurosGenerados < MINOTAUROS_POR_OLEADA && enemigos.size() < MAX_MINOTAUROS) {
            // Escoger al azar un lado desde el cual apareceran
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

            // Evitar que se salgan del mapa (ajustado por tamaño del sprite 64x64)
            spawnX = Math.max(0, Math.min(spawnX, MAP_WIDTH * TILE_SIZE - 64));
            spawnY = Math.max(0, Math.min(spawnY, MAP_HEIGHT * TILE_SIZE - 64));

            // Verificar si la posicion es valida para el spawn
            boolean posicionValida = true;
            int tileX = (int) (spawnX / TILE_SIZE);
            int tileY = (int) (spawnY / TILE_SIZE);

            // No permitir aparecer sobre caminos
            if (generator.getTile(tileX, tileY) == MapaProcedural.Tile.CAMINO) {
                continue;
            }

            // Verificar si esta colisionando con algun arbol
            Rectangle spawnArea = new Rectangle(spawnX, spawnY, 64, 64);
            for (Arbol arbol : generator.getArboles()) {
                if (spawnArea.overlaps(arbol.getCollider())) {
                    posicionValida = false;
                    break;
                }
            }

            if (posicionValida) {
                // Verificar si esta muy cerca de otros minotauros
                boolean demasiadoCerca = false;
                for (Enemigo otroEnemigo : enemigos) {
                    float distancia = Vector2.dst(spawnX, spawnY, otroEnemigo.x, otroEnemigo.y);
                    if (distancia < 150) {
                        demasiadoCerca = true;
                        break;
                    }
                }

                // Si pasa todas las verificaciones, crear el minotauro
                if (!demasiadoCerca) {
                    Minotauro minotauro = new Minotauro(spawnX, spawnY, playerActor.getJugador());
                    minotauro.estadoActual = Enemigo.EstadoEnemigo.RUNNING; // activar persecucion
                    enemigos.add(minotauro);
                    minotaurosGenerados++;

                    // Hacer que se mueva inmediatamente en el primer frame
                    minotauro.update(0.016f, playerActor.getX(), playerActor.getY());
                }
            }
        }
    }

    /**
     * Inicializa la lista de enemigos. El primer spawn se hara
     * por medio del sistema de oleadas para imitar Vampire Survivors.
     */
    private void generarMinotauros() {
        enemigos = new ArrayList<>();
    }

    /**
     * Elimina enemigos muertos en cada frame
     * @param delta tiempo entre frames
     */
    private void actualizarEnemigos(float delta) {
        // Se eliminan enemigos que han muerto y ya terminaron su animacion
        int eliminados = GestionEnemigos.actualizarEnemigos(enemigos);
        if (eliminados > 0) {
            System.out.println("Eliminados " + eliminados + " enemigos muertos");
        }
    }

    /**
     * Metodo principal que se ejecuta en cada frame.
     * Se encarga de dibujar el mundo, jugador, enemigos, HUD y procesar logica.
     */
    @Override
    public void render(float delta) {
        // 1) Limpiar pantalla
        Gdx.gl.glClearColor(0f, 0f, 0f, 1f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        // 2) Entrada del teclado + actualizacion de enemigos
        manejarEntrada(delta);

        // 2.5) Actualizar sistema de pociones
        gestionPociones.actualizar(delta, playerActor.getBounds(), playerActor.getJugador());

        // 3) Centrar la camara en el jugador
        float px = playerActor.getX() + playerActor.getWidth() * 0.5f;
        float py = playerActor.getY() + playerActor.getHeight() * 0.5f;
        cam.position.set(px, py, 0f);
        cam.update();

        // 4) Sincronizar la camara del stage con la camara principal
        OrthographicCamera stageCam = (OrthographicCamera)stage.getViewport().getCamera();
        stageCam.position.set(cam.position);
        stageCam.zoom = cam.zoom;
        stageCam.update();

        // 5) Dibujar el fondo del mapa (tiles y overlays)
        batch.setProjectionMatrix(cam.combined);
        batch.begin();
        float halfW = cam.viewportWidth * 0.5f;
        float halfH = cam.viewportHeight * 0.5f;
        int minX = (int)((px - halfW) / TILE_SIZE) - 1;
        int maxX = (int)((px + halfW) / TILE_SIZE) + 1;
        int minY = (int)((py - halfH) / TILE_SIZE) - 1;
        int maxY = (int)((py + halfH) / TILE_SIZE) + 1;

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

        // Dibujar piedras
        for (Piedra p : generator.getPiedras()) {
            p.render(batch);
        }
        batch.end();

        // 6) Actualizar logica del jugador, pociones y colisiones
        playerActor.update(delta, enemigos);
        stage.act(delta);
        GestionEnemigos.comprobarColisionesProyectiles(stage, enemigos);
        stage.draw();

        // 7) Dibujar enemigos
        batch.begin();
        for (Enemigo e : enemigos) {
            e.render(batch);
        }
        batch.end();

        // 8) Colisiones enemigo → jugador
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
                playerActor.getJugador().recibirDanio(e.getDanio());
            }
        }

        // Si el jugador muere, pasar a la pantalla de muerte
        if (playerActor.getJugador().estaMuerto()) {
            juego.setScreen(new PantallaMuerte(juego));
            return;
        }

        // 10) Dibujar arboles sobre el terreno
        batch.begin();
        for (Arbol ar : generator.getArboles()) {
            ar.render(batch);
        }
        batch.end();

        // 11) HUD fijo + HUD de clase especifica
        batch.begin();
        // Configurar matriz para dibujar HUD en pantalla (sin camara)
        batch.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(
            0, 0, Gdx.graphics.getWidth(), Gdx.graphics.getHeight()
        ));

        // Mostrar barra de vida, mana, nombre, etc
        playerHUD.render(batch);

        // Restaurar matriz para continuar el HUD que sigue al jugador
        batch.setProjectionMatrix(cam.combined);

        // Dibujar HUD encima del personaje (flechas, texto flotante, etc)
        playerActor.dibujarHUD(batch, font);

        // Si el jugador es un arquero, ejecutar logica adicional (como cooldown o disparos)
        if (playerActor.getJugador() instanceof Arquero arq) {
            arq.actualizar(delta);
        }

        batch.end();
    }

    /**
     * Controla el movimiento del jugador, colisiones con arboles
     * y el sistema de oleadas estilo Vampire Survivors.
     */
    private void manejarEntrada(float delta) {
        // 1) Velocidad de movimiento segun el tiempo entre frames
        float speed = 200f * delta;
        float dx = 0f, dy = 0f;

        // 2) Leer teclas de movimiento
        if (Gdx.input.isKeyPressed(Input.Keys.W)) dy += speed;
        if (Gdx.input.isKeyPressed(Input.Keys.S)) dy -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.A)) dx -= speed;
        if (Gdx.input.isKeyPressed(Input.Keys.D)) dx += speed;

        // 3) Calcular nueva posicion del jugador
        float newX = playerActor.getX() + dx;
        float newY = playerActor.getY() + dy;

        // 4) Verificar limites del mapa (borde izquierdo y derecho)
        if (newX < 0) newX = 0;
        if (newX > (MAP_WIDTH * TILE_SIZE) - playerActor.getWidth())
            newX = (MAP_WIDTH * TILE_SIZE) - playerActor.getWidth();

        // 5) Verificar limites del mapa (borde inferior y superior)
        if (newY < 0) newY = 0;
        if (newY > (MAP_HEIGHT * TILE_SIZE) - playerActor.getHeight())
            newY = (MAP_HEIGHT * TILE_SIZE) - playerActor.getHeight();

        // 6) Verificar colision con troncos de arboles
        Rectangle futurePosition = new Rectangle(newX, newY,
            playerActor.getWidth(),
            playerActor.getHeight());

        boolean colisionConArbol = false;
        for (Arbol arbol : generator.getArboles()) {
            if (futurePosition.overlaps(arbol.getCollider())) {
                colisionConArbol = true;
                break;
            }
        }

        // 7) Si hay movimiento y no hay colision, mover al jugador
        if (dx != 0f || dy != 0f) {
            if (!colisionConArbol) {
                playerActor.setPosition(newX, newY);
                playerActor.getJugador().setPosition(newX, newY);
            }
        }

        // 8) Control de oleadas estilo Vampire Survivors
        tiempoUltimoSpawn += delta;
        if (tiempoUltimoSpawn >= TIEMPO_ENTRE_SPAWNS && enemigos.size() < MAX_MINOTAUROS) {
            spawnMinotaurosOleada();
            tiempoUltimoSpawn = 0f;
        }

        // 9) Actualizar enemigos activos y remover los que ya deben desaparecer
        Iterator<Enemigo> iterator = enemigos.iterator();
        while (iterator.hasNext()) {
            Enemigo enemigo = iterator.next();
            enemigo.update(delta, playerActor.getX(), playerActor.getY());
            if (enemigo.isReadyToRemove()) {
                iterator.remove();
            }
        }
    }

    /**
     * Libera todos los recursos usados en esta pantalla para evitar fugas de memoria.
     */
    @Override
    public void dispose() {
        try {
            // Llamar al dispose() del padre
            super.dispose();

            // 1) Liberar recursos básicos
            if (batch != null) batch.dispose();
            if (font != null) font.dispose();

            // 2) Liberar textura del jugador
            if (texPlayer != null) texPlayer.dispose();

            // 3) Liberar texturas de pasto verde (array de variantes)
            if (texPastoVVariants != null) {
                for (Texture t : texPastoVVariants) {
                    if (t != null) t.dispose();
                }
            }

            // 4) Liberar texturas individuales del terreno
            if (texPastoA != null) texPastoA.dispose();
            if (texCamino != null) texCamino.dispose();
            if (texHierbaV != null) texHierbaV.dispose();
            if (texHierbaA != null) texHierbaA.dispose();

            // 5) Liberar recursos del actor del jugador
            if (playerActor != null) {
                playerActor.dispose();
            }

            // 6) Liberar recursos del generador del mapa procedural
            if (generator != null) {
                generator.dispose();
            }

            // 7) Liberar recursos de todos los enemigos activos
            if (enemigos != null) {
                for (Enemigo enemigo : enemigos) {
                    if (enemigo != null) {
                        enemigo.dispose();
                    }
                }
            }

            // 8) Liberar sistema de pociones
            if (gestionPociones != null) {
                gestionPociones.dispose();
            }

            // 9) Liberar HUD del jugador
            if (playerHUD != null) {
                playerHUD.dispose();
            }

        } catch (Exception e) {
            System.err.println("Error al liberar recursos en DungeonScreen: " + e.getMessage());
        }
    }
}
