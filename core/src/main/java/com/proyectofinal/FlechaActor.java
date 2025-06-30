package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

/**
 * Actor que representa una flecha lanzada por el Arquero.
 * Versión mejorada con soporte para viewport y mejor manejo de límites.
 */
public class FlechaActor extends Actor {
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
        this.framesVuelo     = framesVuelo;
        this.framesImpacto   = framesImpacto;
        this.direccion       = direccion;
        this.dano            = dano;
        this.velocidad       = velocidad;
        this.escala          = escala;

        setPosition(x, y);
        setSize(32 * escala, 32 * escala);

        hitbox = new Rectangle(
            x + 8 * escala,
            y + 8 * escala,
            16 * escala,
            16 * escala
        );

        // Correctamente manejar la orientación según la dirección
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
     * Establece el viewport para determinar límites de la pantalla
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
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
                finalizado = true;
            }
        } else {
            // Mover flecha
            float mov = velocidad * delta;
            float nx  = getX() + ("DERECHA".equals(direccion) ? mov : -mov);
            setPosition(nx, getY());

            hitbox.setPosition(nx + 8 * escala, getY() + 8 * escala);

            // Animación de vuelo
            tiempoAnimacion += delta;
            frameActual = (int)(tiempoAnimacion / FRAME_DURACION) % framesVuelo.length;

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
            if (nx < limiteIzquierdo || nx > limiteDerecho) {
                finalizado = true;
                System.out.println("Flecha fuera de límites: " + nx +
                    " (límites: " + limiteIzquierdo + ", " + limiteDerecho + ")");
            }
        }
    }

    /**
     * Comprueba colisiones y aplica daño.
     * Si hay impacto, inicia la animación de impacto y luego elimina la flecha.
     */
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (enemigos == null) return; // Protección contra null
        if (impactando || finalizado) return;

        for (Enemigo e : enemigos) {
            if (e == null || !e.estaVivo) continue; // Protección contra null y enemigos ya muertos

            // Crear un rectángulo ligeramente más grande para facilitar la colisión
            Rectangle r = new Rectangle(e.getX(), e.getY(), 48, 48); // Aumentado de 32 a 48
            if (hitbox.overlaps(r)) {
                // Forzar muerte instantánea del enemigo
                e.recibirDano(9999); // Valor muy alto para asegurar muerte inmediata

                // Configurar para mostrar animación de impacto
                impactando = true;
                tiempoImpacto = 0f;
                frameActual = 0;

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
