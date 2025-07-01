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
 */
public class GestionPociones implements Disposable {
    private static final float TIEMPO_ENTRE_POCIONES = 15f; // Segundos entre generación de pociones
    private static final float MIN_DIST_POCIONES = 150f; // Distancia mínima entre pociones
    private static final int MAX_POCIONES = 10; // Número máximo de pociones simultáneas

    private float tiempoParaNuevaPociones = 3f; // Empezar generando pronto
    private List<PocionActor> pociones;
    private Stage stage;
    private MapaProcedural mapa;
    private Rectangle limitesJugador; // Para evitar generar pociones muy cerca del jugador

    // Texturas para las pociones
    private Texture texturaPocionHP;
    private Texture texturaPocionMana;
    private Texture texturaPocionEscudo;

    /**
     * Constructor que inicializa el gestor de pociones.
     * @param stage Stage donde se añadirán las pociones
     * @param mapa Mapa donde se generarán las pociones
     */
    public GestionPociones(Stage stage, MapaProcedural mapa) {
        this.stage = stage;
        this.mapa = mapa;
        this.pociones = new ArrayList<>();
        this.limitesJugador = new Rectangle(0, 0, 50, 50); // Inicialmente en origen

        cargarTexturas();
    }

    /**
     * Carga las texturas necesarias para las pociones.
     */
    private void cargarTexturas() {
        try {
            texturaPocionHP = new Texture("Pociones/pocionHP.png");
            texturaPocionMana = new Texture("Pociones/pocionMana.png");
            texturaPocionEscudo = new Texture("Pociones/pocionEscudo.png");
        } catch (Exception e) {
            System.err.println("Error al cargar texturas de pociones: " + e.getMessage());
            // Crear texturas fallback
            texturaPocionHP = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            texturaPocionMana = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
            texturaPocionEscudo = new Texture(32, 32, com.badlogic.gdx.graphics.Pixmap.Format.RGBA8888);
        }
    }

    /**
     * Actualiza el gestor de pociones: genera nuevas, comprueba colisiones y elimina expiradas.
     * @param delta Tiempo transcurrido desde el último frame
     * @param jugadorRect Rectángulo que representa al jugador para comprobar colisiones
     * @param personaje Personaje para aplicar efectos de pociones recogidas
     */
    public void actualizar(float delta, Rectangle jugadorRect, Personaje personaje) {
        // Actualizar la posición conocida del jugador
        this.limitesJugador = jugadorRect;

        // Actualizar temporizador para generar nuevas pociones
        tiempoParaNuevaPociones -= delta;

        // Generar nuevas pociones si es tiempo y no hay demasiadas
        if (tiempoParaNuevaPociones <= 0 && pociones.size() < MAX_POCIONES) {
            generarPocionAleatoria(personaje);
            tiempoParaNuevaPociones = TIEMPO_ENTRE_POCIONES;
        }

        // Comprobar colisiones y eliminar pociones expiradas
        Iterator<PocionActor> iter = pociones.iterator();
        while (iter.hasNext()) {
            PocionActor pocion = iter.next();

            // Comprobar si el jugador recoge la poción
            if (!pocion.estaRecogida() && pocion.comprobarColision(jugadorRect)) {
                pocion.recoger(personaje);
            }

            // Eliminar pociones que deben eliminarse
            if (pocion.debeEliminarse()) {
                pocion.remove(); // Eliminar del stage
                iter.remove(); // Eliminar de nuestra lista
            }
        }
    }

    /**
     * Genera una poción aleatoria en una posición válida del mapa.
     */
    private void generarPocionAleatoria(Personaje personaje) {
        // Elegir tipo de poción aleatoriamente
        Pocion nuevaPocion;
        Texture texturaPocion;

        float r = MathUtils.random();
        if (r < 0.5f) { // 50% probabilidad de poción HP
            nuevaPocion = new PocionHP("Poción de Vida", MathUtils.random(10, 30));
            texturaPocion = texturaPocionHP;
        } else if (r < 0.8f) { // 30% probabilidad de poción Mana/Flechas
            nuevaPocion = new PocionMana("Poción de Energía", MathUtils.random(5, 15));
            texturaPocion = texturaPocionMana;
        } else { // 20% probabilidad de poción de escudo
            if (personaje instanceof Caballero) {
                nuevaPocion = new PocionEscudo("Poción de Escudo", 20);
                texturaPocion = texturaPocionEscudo;
            } else {
                // Si el personaje no es Caballero, elegir entre HP o Mana
                if (MathUtils.randomBoolean()) {
                    nuevaPocion = new PocionHP("Poción de Vida", MathUtils.random(10, 30));
                    texturaPocion = texturaPocionHP;
                } else {
                    nuevaPocion = new PocionMana("Poción de Energía", MathUtils.random(5, 15));
                    texturaPocion = texturaPocionMana;
                }
            }
        }

        // Buscar posición válida (no en camino, no cerca de otras pociones o jugador)
        Vector2 posicion = encontrarPosicionValida();

        if (posicion != null) {
            PocionActor pocionActor = new PocionActor(
                nuevaPocion,
                texturaPocion,
                posicion.x,
                posicion.y,
                0.75f // Escala
            );

            // Añadir la poción al stage y a nuestra lista
            stage.addActor(pocionActor);
            pociones.add(pocionActor);

            System.out.println("Generada " + nuevaPocion.getNombre() + " en " + posicion.x + ", " + posicion.y);
        }
    }

    /**
     * Encuentra una posición válida para colocar una poción.
     * @return Vector2 con la posición o null si no se encontró
     */
    private Vector2 encontrarPosicionValida() {
        // Obtener dimensiones del mapa
        int maxX = (int) stage.getViewport().getWorldWidth();
        int maxY = (int) stage.getViewport().getWorldHeight();

        // Obtener la posición actual de la cámara para generar pociones visibles
        float camX = stage.getViewport().getCamera().position.x;
        float camY = stage.getViewport().getCamera().position.y;

        // Dimensiones de la ventana visible
        float viewportWidth = stage.getViewport().getWorldWidth();
        float viewportHeight = stage.getViewport().getWorldHeight();

        // Área visible donde preferimos colocar pociones (70% del área visible)
        float minVisibleX = Math.max(100, camX - viewportWidth * 0.35f);
        float maxVisibleX = Math.min(maxX - 100, camX + viewportWidth * 0.35f);
        float minVisibleY = Math.max(100, camY - viewportHeight * 0.35f);
        float maxVisibleY = Math.min(maxY - 100, camY + viewportHeight * 0.35f);

        // Intentar primero en el área visible con más intentos
        for (int intento = 0; intento < 30; intento++) {
            float x = MathUtils.random(minVisibleX, maxVisibleX);
            float y = MathUtils.random(minVisibleY, maxVisibleY);

            // Comprobar si la posición es adecuada
            boolean posicionValida = true;

            // Logear información sobre el intento de generación
            System.out.println("Intentando generar poción en área visible: (" + x + ", " + y + ")");

            // Verificar que no esté cerca del jugador
            if (Math.abs(x - limitesJugador.x) < 200 && Math.abs(y - limitesJugador.y) < 200) {
                posicionValida = false;
                continue;
            }

            // Verificar que no esté cerca de otras pociones
            for (PocionActor p : pociones) {
                if (Math.abs(p.getX() - x) < MIN_DIST_POCIONES &&
                    Math.abs(p.getY() - y) < MIN_DIST_POCIONES) {
                    posicionValida = false;
                    break;
                }
            }

            if (posicionValida) {
                return new Vector2(x, y);
            }
        }

        return null; // No se encontró posición válida
    }

    /**
     * Comprueba si hay colisiones entre el jugador y las pociones.
     * @param jugadorRect Rectángulo del jugador
     * @param personaje Personaje que recoge las pociones
     * @return Número de pociones recogidas
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
     * Libera todos los recursos utilizados.
     */
    @Override
    public void dispose() {
        if (texturaPocionHP != null) {
            texturaPocionHP.dispose();
            texturaPocionHP = null;
        }

        if (texturaPocionMana != null) {
            texturaPocionMana.dispose();
            texturaPocionMana = null;
        }

        if (texturaPocionEscudo != null) {
            texturaPocionEscudo.dispose();
            texturaPocionEscudo = null;
        }

        // Las pociones se eliminarán a través del stage
        pociones.clear();
    }
}
