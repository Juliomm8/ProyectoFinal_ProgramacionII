package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.math.Rectangle;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Clase encargada de gestionar la generación, seguimiento y eliminación de pociones.
 * Controla cuántas hay, cuándo aparecen, dónde se colocan, colisiones con el jugador y liberación de recursos.
 */
public class GestionPociones implements Disposable {

    // Configuración general del sistema de pociones
    private static final float TIEMPO_ENTRE_POCIONES = 15f;   // Tiempo entre generación de nuevas pociones
    private static final float MIN_DIST_POCIONES = 150f;      // Distancia mínima entre pociones
    private static final int MAX_POCIONES = 10;               // Límite de pociones simultáneas en pantalla

    // Estado interno
    private float tiempoParaNuevaPociones = 3f;               // Temporizador para próxima poción (inicial con delay corto)
    private List<PocionActor> pociones;                       // Lista de pociones activas
    private Stage stage;                                      // Stage donde se colocan las pociones
    private MapaProcedural mapa;                              // Referencia al mapa (por si se necesita en el futuro)
    private Rectangle limitesJugador;                         // Rectángulo de colisión del jugador

    // Texturas para los diferentes tipos de pociones
    private Texture texturaPocionHP;
    private Texture texturaPocionMana;
    private Texture texturaPocionEscudo;

    /**
     * Constructor del sistema de gestión de pociones.
     * @param stage Stage del juego donde aparecerán las pociones
     * @param mapa Mapa del mundo actual (para integraciones futuras)
     */
    public GestionPociones(Stage stage, MapaProcedural mapa) {
        this.stage = stage;
        this.mapa = mapa;
        this.pociones = new ArrayList<>();
        this.limitesJugador = new Rectangle(0, 0, 50, 50); // Valor inicial seguro

        cargarTexturas();
    }

    /**
     * Carga las texturas necesarias para las pociones.
     * En caso de error, crea texturas vacías como respaldo.
     */
    private void cargarTexturas() {
        try {
            texturaPocionHP = new Texture("Pociones/pocionHP.png");
            texturaPocionMana = new Texture("Pociones/pocionMana.png");
            texturaPocionEscudo = new Texture("Pociones/pocionEscudo.png");
        } catch (Exception e) {
            System.err.println("Error al cargar texturas de pociones: " + e.getMessage());
            // Fallback en caso de error
            texturaPocionHP = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            texturaPocionMana = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            texturaPocionEscudo = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        }
    }

    /**
     * Lógica principal que se debe llamar desde el render o update del juego.
     * Controla generación, colisiones y eliminación de pociones.
     */
    public void actualizar(float delta, Rectangle jugadorRect, Personaje personaje) {
        this.limitesJugador = jugadorRect;  // Actualiza posición del jugador
        tiempoParaNuevaPociones -= delta;

        // Generar una nueva poción si es tiempo y no se superó el límite
        if (tiempoParaNuevaPociones <= 0 && pociones.size() < MAX_POCIONES) {
            generarPocionAleatoria(personaje);
            tiempoParaNuevaPociones = TIEMPO_ENTRE_POCIONES;
        }

        // Verificar colisiones y eliminar pociones recogidas o expiradas
        Iterator<PocionActor> iter = pociones.iterator();
        while (iter.hasNext()) {
            PocionActor pocion = iter.next();

            // Recoger si el jugador la toca
            if (!pocion.estaRecogida() && pocion.comprobarColision(jugadorRect)) {
                pocion.recoger(personaje);
            }

            // Eliminar del stage si ya está marcada
            if (pocion.debeEliminarse()) {
                pocion.remove();  // Se elimina del stage
                iter.remove();    // Se elimina de la lista interna
            }
        }
    }

    /**
     * Genera una nueva poción aleatoria en el mapa según probabilidad y clase del personaje.
     */
    private void generarPocionAleatoria(Personaje personaje) {
        Pocion nuevaPocion;
        Texture texturaPocion;

        // Lógica de probabilidad para decidir el tipo de poción
        float r = MathUtils.random();
        if (r < 0.5f) {
            nuevaPocion = new PocionHP("Poción de Vida", MathUtils.random(10, 30));
            texturaPocion = texturaPocionHP;
        } else if (r < 0.8f) {
            nuevaPocion = new PocionMana("Poción de Energía", MathUtils.random(5, 15));
            texturaPocion = texturaPocionMana;
        } else {
            if (personaje instanceof Caballero) {
                nuevaPocion = new PocionEscudo("Poción de Escudo", 20);
                texturaPocion = texturaPocionEscudo;
            } else {
                if (MathUtils.randomBoolean()) {
                    nuevaPocion = new PocionHP("Poción de Vida", MathUtils.random(10, 30));
                    texturaPocion = texturaPocionHP;
                } else {
                    nuevaPocion = new PocionMana("Poción de Energía", MathUtils.random(5, 15));
                    texturaPocion = texturaPocionMana;
                }
            }
        }

        // Buscar una ubicación válida
        Vector2 posicion = encontrarPosicionValida();
        if (posicion != null) {
            PocionActor actor = new PocionActor(nuevaPocion, texturaPocion, posicion.x, posicion.y, 0.75f);
            stage.addActor(actor);
            pociones.add(actor);
            System.out.println("Generada " + nuevaPocion.getNombre() + " en " + posicion.x + ", " + posicion.y);
        }
    }

    /**
     * Encuentra una posición en pantalla para colocar una poción,
     * asegurándose de no estar cerca del jugador ni de otras pociones.
     */
    private Vector2 encontrarPosicionValida() {
        // Dimensiones de la cámara y mundo
        int maxX = (int) stage.getViewport().getWorldWidth();
        int maxY = (int) stage.getViewport().getWorldHeight();
        float camX = stage.getViewport().getCamera().position.x;
        float camY = stage.getViewport().getCamera().position.y;

        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();

        // Área central visible preferida para generación
        float minX = Math.max(100, camX - viewportWidth * 0.35f);
        float maxXv = Math.min(maxX - 100, camX + viewportWidth * 0.35f);
        float minY = Math.max(100, camY - viewportHeight * 0.35f);
        float maxYv = Math.min(maxY - 100, camY + viewportHeight * 0.35f);

        for (int i = 0; i < 30; i++) {
            float x = MathUtils.random(minX, maxXv);
            float y = MathUtils.random(minY, maxYv);

            // Muy cerca del jugador
            if (Math.abs(x - limitesJugador.x) < 200 && Math.abs(y - limitesJugador.y) < 200) continue;

            // Muy cerca de otra poción
            boolean muyCercaOtra = false;
            for (PocionActor p : pociones) {
                if (Math.abs(p.getX() - x) < MIN_DIST_POCIONES &&
                    Math.abs(p.getY() - y) < MIN_DIST_POCIONES) {
                    muyCercaOtra = true;
                    break;
                }
            }

            if (!muyCercaOtra) return new Vector2(x, y);
        }

        return null; // No se encontró una posición válida
    }

    /**
     * Permite verificar colisiones por separado (si quieres forzar la detección desde otro método).
     */
    public int comprobarColisiones(Rectangle jugadorRect, Personaje personaje) {
        int recogidas = 0;

        for (PocionActor pocion : pociones) {
            if (!pocion.estaRecogida() && pocion.comprobarColision(jugadorRect)) {
                pocion.recoger(personaje);
                recogidas++;
            }
        }

        return recogidas;
    }

    /**
     * Libera texturas cargadas y limpia la lista de pociones.
     */
    @Override
    public void dispose() {
        if (texturaPocionHP != null) texturaPocionHP.dispose();
        if (texturaPocionMana != null) texturaPocionMana.dispose();
        if (texturaPocionEscudo != null) texturaPocionEscudo.dispose();
        pociones.clear();
    }
}
