package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

/**
 * Actor que representa una flecha disparada por el Arquero.
 * Versión mejorada con soporte para viewport y mejor manejo de límites.
 */
public class FlechaActor extends ProyectilBase {

    // Frames de animación
    private TextureRegion[] framesVuelo;
    private TextureRegion[] framesImpacto;

    // Constantes de animación
    private static final float FRAME_DURACION = 0.1f;

    // Variables adicionales
    private int frameActual = 0;
    private float tiempoImpacto = 0f;
    private boolean finalizado = false;

    // Opciones
    private float escala = 1f;

    // Viewport para cálculo de límites
    private Viewport viewport;

    /**
     * Constructor de la flecha.
     *
     * @param framesVuelo     frames de vuelo
     * @param framesImpacto   frames de impacto
     * @param x               posición inicial X
     * @param y               posición inicial Y
     * @param direccion       "IZQUIERDA" o "DERECHA"
     * @param dano            daño que inflige
     * @param velocidad       velocidad en px/s
     * @param escala          escala de tamaño
     */
    public FlechaActor(TextureRegion[] framesVuelo,
                       TextureRegion[] framesImpacto,
                       float x, float y,
                       String direccion,
                       int dano,
                       float velocidad,
                       float escala) {
        super(x, y, direccion, dano, velocidad);
        this.framesVuelo     = framesVuelo;
        this.framesImpacto   = framesImpacto;
        this.escala          = escala;

        setSize(32 * escala, 32 * escala);

        hitbox = new Rectangle(
            x + 8 * escala,
            y + 8 * escala,
            16 * escala,
            16 * escala
        );

        // Correctamente manejar la orientación según la dirección
        boolean debeEstarVolteado = "IZQUIERDA".equals(direccion);

        if (framesVuelo != null) {
            for (TextureRegion f : framesVuelo) {
                if (f != null && f.isFlipX() != debeEstarVolteado) {
                    f.flip(true, false);
                }
            }
        }

        if (framesImpacto != null) {
            for (TextureRegion f : framesImpacto) {
                if (f != null && f.isFlipX() != debeEstarVolteado) {
                    f.flip(true, false);
                }
            }
        }
    }

    /**
     * Establece el viewport para determinar límites de la pantalla
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        if (finalizado || debeEliminarse) {
            remove();
            return;
        }

        if (impactado) {
            // Animación de impacto
            tiempoImpacto += delta;
            if (framesImpacto != null) {
                frameActual = (int)(tiempoImpacto / FRAME_DURACION);
                if (frameActual >= framesImpacto.length) {
                    finalizado = true;
                    debeEliminarse = true;
                }
            }
        } else {
            // La lógica de movimiento ya la maneja ProyectilBase en su método act()
            // Solo actualizamos la hitbox con el tamaño específico de la flecha
            hitbox.setPosition(getX() + 8 * escala, getY() + 8 * escala);
            hitbox.setSize(16 * escala, 16 * escala);

            // Calcular límites del mundo con margen adicional
            float margenExtra = 100f; // Margen más pequeño para mejor rendimiento
            float limiteIzquierdo, limiteDerecho;

            if (viewport != null) {
                // Usar dimensiones del viewport
                limiteIzquierdo = -getWidth() - margenExtra;
                limiteDerecho = viewport.getWorldWidth() + margenExtra;
            } else {
                // Fallback a tamaño de pantalla
                limiteIzquierdo = -getWidth() - margenExtra;
                limiteDerecho = Gdx.graphics.getWidth() + margenExtra;
            }

            // Comprobar si está fuera de límites
            if (getX() < limiteIzquierdo || getX() > limiteDerecho) {
                finalizado = true;
                debeEliminarse = true;
                System.out.println("Flecha fuera de límites: " + getX() +
                    " (límites: " + limiteIzquierdo + ", " + limiteDerecho + ")");
            }
        }
    }

    /**
     * Comprueba colisiones y aplica daño.
     * Si hay impacto, inicia la animación de impacto y luego elimina la flecha.
     */
    @Override
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (enemigos == null) return; // Protección contra null
        if (impactado || finalizado || debeEliminarse) return;

        for (Enemigo e : enemigos) {
            if (e == null || !e.estaVivo()) continue; // Usar método estaVivo() en lugar de acceder al campo directamente

            // Crear un rectángulo ligeramente más grande para facilitar la colisión
            Rectangle r = new Rectangle(e.getX(), e.getY(), 48, 48); // Aumentado de 32 a 48
            if (hitbox.overlaps(r)) {
                // Forzar muerte instantánea del enemigo
                e.recibirDanio(danio); // Usar el daño definido en ProyectilBase

                // Configurar para mostrar animación de impacto
                impactado = true;
                tiempoImpacto = 0f;
                frameActual = 0;
                stateTime = 0f; // Reiniciar el tiempo de animación

                // Posicionar la animación de impacto en el punto de colisión
                // Ajustamos para centrar mejor el impacto
                float impactoX = e.getX() + 16 - (getWidth()/2);
                float impactoY = e.getY() + 16 - (getHeight()/2);
                setPosition(impactoX, impactoY);

                System.out.println("¡Flecha impactó al enemigo! Reproduciendo animación de impacto");
                break; // La flecha impacta y se detiene
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (batch == null) return; // Protección contra null

        TextureRegion frame = null;
        if (impactado) {
            if (framesImpacto != null && frameActual < framesImpacto.length) {
                frame = framesImpacto[frameActual];
            } else {
                return;
            }
        } else {
            // Usar el stateTime de ProyectilBase para la animación
            if (framesVuelo != null && framesVuelo.length > 0) {
                frameActual = (int)(stateTime / FRAME_DURACION) % framesVuelo.length;
                frame = framesVuelo[frameActual];
            }
        }

        if (frame != null) {
            batch.draw(frame, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public boolean remove() {
        // Llamar al método de la superclase
        return super.remove();
    }

    /** No liberamos texturas aquí; lo hace quien creó los recursos. */
    public void dispose() {
        // vacío
    }
}
