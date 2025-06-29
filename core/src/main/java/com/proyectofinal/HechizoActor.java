package com.proyectofinal;

import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.List;

/**
 * Actor que representa un hechizo lanzado por el Mago.
 */
public class HechizoActor extends Actor {
    // Propiedades del hechizo
    private TextureRegion[] framesVuelo;    // Frames de animación del hechizo volando
    private TextureRegion[] framesImpacto;  // Frames de animación del impacto
    private float velocidad = 150f;         // Velocidad en píxeles por segundo
    private String direccion;               // Dirección: "IZQUIERDA" o "DERECHA"
    private float tiempoAnimacion = 0;      // Tiempo para animación
    private int frameActual = 0;            // Frame actual
    private static final float FRAME_DURACION = 0.1f; // Duración de cada frame

    private boolean impactando = false;     // Si está en fase de impacto
    private int dano;                       // Daño que causa el hechizo
    private float tiempoImpacto = 0;        // Tiempo de la animación de impacto
    private boolean finalizado = false;     // Si ha terminado y debe eliminarse

    private Rectangle hitbox;               // Área de colisión

    /**
     * Constructor del hechizo con animaciones y posición inicial.
     */
    public HechizoActor(TextureRegion[] framesVuelo, TextureRegion[] framesImpacto,
                        float x, float y, String direccion, int dano) {
        this.framesVuelo = framesVuelo;
        this.framesImpacto = framesImpacto;
        this.direccion = direccion;
        this.dano = dano;

        setPosition(x, y);
        setSize(32, 32); // Tamaño estándar

        // Crear hitbox un poco más pequeña que el actor visual
        hitbox = new Rectangle(x + 8, y + 8, 16, 16);

        // Voltear frames si es necesario
        if ("IZQUIERDA".equals(direccion)) {
            for (TextureRegion frame : framesVuelo) {
                if (!frame.isFlipX()) {
                    frame.flip(true, false);
                }
            }
            for (TextureRegion frame : framesImpacto) {
                if (!frame.isFlipX()) {
                    frame.flip(true, false);
                }
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (finalizado) {
            // Si terminó la animación de impacto, eliminar el actor
            remove();
            return;
        }

        if (impactando) {
            // Avanzar animación de impacto
            tiempoImpacto += delta;
            frameActual = (int)(tiempoImpacto / FRAME_DURACION);
            if (frameActual >= framesImpacto.length) {
                finalizado = true; // Terminar cuando finalice la animación de impacto
            }
        } else {
            // Mover el hechizo en la dirección adecuada
            float movimiento = velocidad * delta;
            float nuevaX = getX();

            if ("DERECHA".equals(direccion)) {
                nuevaX += movimiento;
            } else {
                nuevaX -= movimiento;
            }

            setPosition(nuevaX, getY());

            // Actualizar hitbox
            hitbox.setPosition(nuevaX + 8, getY() + 8);

            // Avanzar animación de vuelo
            tiempoAnimacion += delta;
            frameActual = (int)(tiempoAnimacion / FRAME_DURACION) % framesVuelo.length;
        }
    }

    /**
     * Verifica colisiones con enemigos y aplica daño.
     */
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (impactando || finalizado) return; // Ya impactó o terminó

        for (Enemigo enemigo : enemigos) {
            Rectangle enemigoRect = new Rectangle(enemigo.getX(), enemigo.getY(), 32, 32);
            if (hitbox.overlaps(enemigoRect)) {
                // Colisión detectada
                enemigo.recibirDano(dano);
                iniciarImpacto();
                break;
            }
        }
    }

    /**
     * Inicia la animación de impacto.
     */
    public void iniciarImpacto() {
        impactando = true;
        tiempoImpacto = 0;
        frameActual = 0;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frameToDraw;

        if (impactando) {
            // Mostrar frame de impacto
            if (frameActual < framesImpacto.length) {
                frameToDraw = framesImpacto[frameActual];
            } else {
                return; // No dibujar si ya terminó
            }
        } else {
            // Mostrar frame de vuelo
            frameToDraw = framesVuelo[frameActual];
        }

        batch.draw(frameToDraw, getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Liberar recursos al eliminar el actor.
     */
    public void dispose() {
        // Las texturas se manejan externamente, no las liberamos aquí
    }
}
