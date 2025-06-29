package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;
import java.util.List;

/**
 * Actor que representa una flecha disparada por el Arquero.
 */
public class FlechaActor extends Actor {
    // Frames de animación
    private TextureRegion[] framesVuelo;
    private TextureRegion[] framesImpacto;

    // Movimiento y direcciones
    private float velocidad = 200f;         // px/s
    private String direccion;               // "IZQUIERDA" o "DERECHA"

    // Animación
    private float tiempoAnimacion = 0f;
    private int frameActual = 0;
    private static final float FRAME_DURACION = 0.1f;

    // Impacto
    private boolean impactando = false;
    private float tiempoImpacto = 0f;
    private boolean finalizado = false;

    // Daño y colisiones
    private int dano;
    private Rectangle hitbox;

    /**
     * Constructor de la flecha.
     */
    public FlechaActor(TextureRegion[] framesVuelo,
                       TextureRegion[] framesImpacto,
                       float x, float y,
                       String direccion,
                       int dano,
                       float velocidad,
                       float escala) {
        this.framesVuelo = framesVuelo;
        this.framesImpacto = framesImpacto;
        this.direccion    = direccion;
        this.dano         = dano;
        this.velocidad    = velocidad;

        setPosition(x, y);
        setSize(32 * escala, 32 * escala);

        hitbox = new Rectangle(x + 10 * escala, y + 12 * escala, 12 * escala, 8 * escala);

        // Correctamente manejar la orientación según la dirección
        // Aseguramos que las flechas estén correctamente orientadas
        // Por defecto las flechas están mirando a la DERECHA
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

    /**
     * Constructor simplificado para mantener compatibilidad.
     */
    public FlechaActor(TextureRegion[] framesVuelo,
                       TextureRegion[] framesImpacto,
                       float x, float y,
                       String direccion,
                       int dano) {
        this(framesVuelo, framesImpacto, x, y, direccion, dano, 200f, 1.0f);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (finalizado) {
            remove();
            return;
        }

        if (impactando) {
            // Avanza animación de impacto
            tiempoImpacto += delta;
            frameActual = (int)(tiempoImpacto / FRAME_DURACION);
            if (frameActual >= framesImpacto.length) {
                finalizado = true;
            }
        } else {
            // Mueve la flecha
            float mov = velocidad * delta;
            float nx  = getX() + ("DERECHA".equals(direccion) ? mov : -mov);
            setPosition(nx, getY());

            hitbox.setPosition(nx + 10, getY() + 12);

            // Avanza animación de vuelo
            tiempoAnimacion += delta;
            frameActual = (int)(tiempoAnimacion / FRAME_DURACION) % framesVuelo.length;

            // Ampliar el rango de movimiento para que la flecha viaje más lejos antes de ser eliminada
            // Se aumenta el margen para permitir disparos desde más lejos
            float margenExtra = 500f; // Margen adicional para permitir disparos desde fuera de la pantalla
            if (nx < -getWidth() - margenExtra || nx > Gdx.graphics.getWidth() + margenExtra) {
                finalizado = true;
            }
        }
    }

    /**
     * Comprueba colisiones contra la lista de enemigos.
     */
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (impactando || finalizado) return;

        for (Enemigo e : enemigos) {
            Rectangle r = new Rectangle(e.getX(), e.getY(), 32, 32);
            if (hitbox.overlaps(r)) {
                e.recibirDano(dano);
                impactando    = true;
                tiempoImpacto = 0f;
                frameActual   = 0;
                break;
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

    /** No liberamos texturas aquí; lo hace el creador de recursos. */
    public void dispose() {
        // vacío
    }
}
