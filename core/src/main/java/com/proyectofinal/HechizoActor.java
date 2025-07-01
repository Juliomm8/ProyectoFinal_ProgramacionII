package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

/**
 * Actor que representa un hechizo lanzado por el Mago.
 * Versión mejorada con soporte para viewport y mejor manejo de límites.
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

    // Viewport para cálculo de límites
    private Viewport viewport;

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
                System.out.println("Hechizo fuera de límites: " + nx +
                    " (límites: " + limiteIzquierdo + ", " + limiteDerecho + ")");
            }
        }
    }

    /**
     * Comprueba colisiones y aplica daño.
     * El hechizo básico (no atraviesa) se elimina inmediatamente al impactar.
     * El hechizo especial (atraviesa) muestra animación de impacto y continúa.
     */
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (enemigos == null) return; // Protección contra null
        if (finalizado) return;
        // Quitamos el check de impactando para permitir múltiples impactos con hechizo especial

        for (Enemigo e : enemigos) {
            if (e == null || !e.estaVivo()) continue; // Usar método estaVivo() en lugar de acceder al campo directamente

            // Crear un rectángulo ligeramente más grande para facilitar la colisión
            Rectangle r = new Rectangle(e.getX(), e.getY(), 48, 48); // Aumentado de 32 a 48
            if (hitbox.overlaps(r)) {
                // Registrar la colisión para depuración
                System.out.println("¡Hechizo colisiona con enemigo! Estado enemigo: " + e.estadoActual);

                // Forzar vida = 0 en el enemigo (muerte a un solo golpe)
                e.recibirDanio(9999); // Valor muy alto para asegurar muerte inmediata

                // Comportamiento diferente según tipo de hechizo
                if (atraviesaEnemigos) {
                    // Hechizo especial: muestra animación de impacto y continúa
                    System.out.println("¡Hechizo especial impactó y atraviesa!");
                    impactando = true;
                    tiempoImpacto = 0f;
                    frameActual = 0;
                } else {
                    // Hechizo básico: eliminar inmediatamente sin animación de impacto
                    System.out.println("¡Hechizo básico impactó! Eliminando...");
                    finalizado = true;
                    remove();
                    return; // Salir inmediatamente
                }

                if (!atraviesaEnemigos) break;
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
