package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.List;

/**
 * Actor que representa un hechizo lanzado por el Mago.
 */
public class HechizoActor extends Actor {
    // Frames de animación
    private TextureRegion[] framesVuelo;
    private TextureRegion[] framesImpacto;

    // Movimiento y dirección
    private float velocidad = 150f;        // px/s
    private String direccion;              // "IZQUIERDA" o "DERECHA"

    // Animación de vuelo
    private float tiempoAnimacion = 0f;
    private int frameActual = 0;
    private static final float FRAME_DURACION = 0.1f;

    // Impacto
    private boolean impactando = false;
    private float tiempoImpacto = 0f;
    private boolean finalizado = false;

    // Daño y colisión
    private int dano;
    private Rectangle hitbox;

    // Opciones
    private float escala = 1f;
    private boolean atraviesaEnemigos = false;

    /**
     * Constructor del hechizo.
     *
     * @param framesVuelo          frames de vuelo
     * @param framesImpacto        frames de impacto
     * @param x                    posición inicial X
     * @param y                    posición inicial Y
     * @param direccion            "IZQUIERDA" o "DERECHA"
     * @param dano                 daño que inflige
     * @param velocidad            velocidad en px/s
     * @param escala               escala de tamaño
     * @param atraviesaEnemigos    si el hechizo atraviesa enemigos tras impactar
     */
    public HechizoActor(TextureRegion[] framesVuelo,
                        TextureRegion[] framesImpacto,
                        float x, float y,
                        String direccion,
                        int dano,
                        float velocidad,
                        float escala,
                        boolean atraviesaEnemigos) {
        this.framesVuelo          = framesVuelo;
        this.framesImpacto        = framesImpacto;
        this.direccion            = direccion;
        this.dano                 = dano;
        this.velocidad            = velocidad;
        this.escala               = escala;
        this.atraviesaEnemigos    = atraviesaEnemigos;

        setPosition(x, y);
        setSize(32 * escala, 32 * escala);

        hitbox = new Rectangle(
            x + 8 * escala,
            y + 8 * escala,
            16 * escala,
            16 * escala
        );

        // Correctamente manejar la orientación según la dirección
        // Aseguramos que los hechizos estén correctamente orientados
        // Por defecto los hechizos están mirando a la DERECHA
        boolean debeEstarVolteado = "IZQUIERDA".equals(direccion);

        for (TextureRegion f : framesVuelo) {
            // Solo volteamos si no coincide con la orientación deseada
            if (f.isFlipX() != debeEstarVolteado) {
                f.flip(true, false);
            }
        }

        for (TextureRegion f : framesImpacto) {
            // Solo volteamos si no coincide con la orientación deseada
            if (f.isFlipX() != debeEstarVolteado) {
                f.flip(true, false);
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (finalizado) {
            remove();
            return;
        }

        if (impactando) {
            // Animación de impacto
            tiempoImpacto += delta;
            frameActual = (int)(tiempoImpacto / FRAME_DURACION);
            if (frameActual >= framesImpacto.length) {
                if (atraviesaEnemigos) {
                    // volver a vuelo si atraviesa
                    impactando     = false;
                    tiempoAnimacion = 0f;
                    frameActual     = 0;
                } else {
                    finalizado = true;
                }
            }
        } else {
            // Mover hechizo
            float mov = velocidad * delta;
            float nx  = getX() + ("DERECHA".equals(direccion) ? mov : -mov);
            setPosition(nx, getY());

            hitbox.setPosition(nx + 8 * escala, getY() + 8 * escala);

            // Animación de vuelo
            tiempoAnimacion += delta;
            frameActual = (int)(tiempoAnimacion / FRAME_DURACION) % framesVuelo.length;

            // Ampliar el rango de movimiento para que el hechizo viaje más lejos antes de ser eliminado
            // Se aumenta el margen para permitir lanzar hechizos desde más lejos
            float margenExtra = 500f; // Margen adicional para permitir disparos desde fuera de la pantalla
            if (nx < -getWidth() - margenExtra || nx > Gdx.graphics.getWidth() + margenExtra) {
                finalizado = true;
            }
        }
    }

    /**
     * Comprueba colisiones y aplica daño.
     */
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (impactando && !atraviesaEnemigos) return;
        if (finalizado) return;

        for (Enemigo e : enemigos) {
            Rectangle r = new Rectangle(e.getX(), e.getY(), 32, 32);
            if (hitbox.overlaps(r)) {
                e.recibirDano(dano);
                impactando     = true;
                tiempoImpacto  = 0f;
                frameActual    = 0;
                if (!atraviesaEnemigos) break;
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame;
        if (impactando) {
            if (frameActual < framesImpacto.length) {
                frame = framesImpacto[frameActual];
            } else {
                return;
            }
        } else {
            frame = framesVuelo[frameActual];
        }
        batch.draw(frame, getX(), getY(), getWidth(), getHeight());
    }

    /** No liberamos texturas aquí; lo hace quien creó los recursos. */
    public void dispose() {
        // vacío
    }
}
