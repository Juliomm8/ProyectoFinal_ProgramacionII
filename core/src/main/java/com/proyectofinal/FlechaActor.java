package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

/**
 * Actor que representa una flecha disparada por el Arquero.
 * Usa animaciones para vuelo e impacto. Se mueve en línea recta y verifica colisión con enemigos.
 */
public class FlechaActor extends ProyectilBase {

    // Animaciones
    private TextureRegion[] framesVuelo;
    private TextureRegion[] framesImpacto;

    // Control de animación
    private static final float FRAME_DURACION = 0.1f;
    private int frameActual = 0;
    private float tiempoImpacto = 0f;
    private boolean finalizado = false;

    // Escala visual
    private float escala = 1f;

    // Viewport para calcular limites visibles
    private Viewport viewport;

    /**
     * Constructor principal de FlechaActor.
     *
     * @param framesVuelo    animación de vuelo
     * @param framesImpacto  animación de impacto
     * @param x              posición X inicial
     * @param y              posición Y inicial
     * @param direccion      "IZQUIERDA" o "DERECHA"
     * @param dano           daño que inflige
     * @param velocidad      velocidad de movimiento
     * @param escala         escala de tamaño de sprite
     */
    public FlechaActor(TextureRegion[] framesVuelo,
                       TextureRegion[] framesImpacto,
                       float x, float y,
                       String direccion,
                       int dano,
                       float velocidad,
                       float escala) {
        super(x, y, direccion, dano, velocidad);
        this.framesVuelo = framesVuelo;
        this.framesImpacto = framesImpacto;
        this.escala = escala;

        setSize(32 * escala, 32 * escala);

        hitbox = new Rectangle(
            x + 8 * escala,
            y + 8 * escala,
            16 * escala,
            16 * escala
        );

        // Corregir orientación de los sprites
        boolean voltear = "IZQUIERDA".equals(direccion);
        if (framesVuelo != null) {
            for (TextureRegion f : framesVuelo) {
                if (f != null && f.isFlipX() != voltear) f.flip(true, false);
            }
        }
        if (framesImpacto != null) {
            for (TextureRegion f : framesImpacto) {
                if (f != null && f.isFlipX() != voltear) f.flip(true, false);
            }
        }
    }

    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    /**
     * Actualiza la lógica de la flecha en cada frame.
     */
    @Override
    public void act(float delta) {
        super.act(delta);

        if (finalizado || debeEliminarse) {
            remove();
            return;
        }

        if (impactado) {
            tiempoImpacto += delta;
            if (framesImpacto != null) {
                frameActual = (int)(tiempoImpacto / FRAME_DURACION);
                if (frameActual >= framesImpacto.length) {
                    finalizado = true;
                    debeEliminarse = true;
                }
            }
        } else {
            // Actualizar hitbox
            hitbox.setPosition(getX() + 8 * escala, getY() + 8 * escala);
            hitbox.setSize(16 * escala, 16 * escala);

            // Comprobar límites de pantalla o mundo
            float margen = 100f;
            float limiteIzq = -getWidth() - margen;
            float limiteDer = (viewport != null)
                ? viewport.getWorldWidth() + margen
                : Gdx.graphics.getWidth() + margen;

            if (getX() < limiteIzq || getX() > limiteDer) {
                finalizado = true;
                debeEliminarse = true;
                System.out.println("Flecha fuera de límites: " + getX());
            }
        }
    }

    /**
     * Verifica colisión con enemigos y aplica daño.
     */
    @Override
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (enemigos == null || impactado || finalizado || debeEliminarse) return;

        for (Enemigo e : enemigos) {
            if (e == null || !e.estaVivo()) continue;

            Rectangle r = new Rectangle(e.getX(), e.getY(), 48, 48); // hitbox de impacto amplia
            if (hitbox.overlaps(r)) {
                e.recibirDanio(danio);

                impactado = true;
                tiempoImpacto = 0f;
                frameActual = 0;
                stateTime = 0f;

                float impactoX = e.getX() + 16 - (getWidth() / 2);
                float impactoY = e.getY() + 16 - (getHeight() / 2);
                setPosition(impactoX, impactoY);

                System.out.println("¡Flecha impactó al enemigo!");
                break;
            }
        }
    }

    /**
     * Dibuja el frame correspondiente a vuelo o impacto.
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (batch == null) return;

        TextureRegion frame = null;

        if (impactado) {
            if (framesImpacto != null && frameActual < framesImpacto.length) {
                frame = framesImpacto[frameActual];
            }
        } else if (framesVuelo != null && framesVuelo.length > 0) {
            frameActual = (int)(stateTime / FRAME_DURACION) % framesVuelo.length;
            frame = framesVuelo[frameActual];
        }

        if (frame != null) {
            batch.draw(frame, getX(), getY(), getWidth(), getHeight());
        }
    }

    @Override
    public boolean remove() {
        return super.remove();
    }

    /**
     * No libera texturas aquí, ya que las texturas las gestiona quien crea las flechas.
     */
    public void dispose() {
        // Intencionalmente vacío
    }
}
