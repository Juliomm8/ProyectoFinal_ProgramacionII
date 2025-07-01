package com.proyectofinal;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.List;

/**
 * Clase base para todos los proyectiles (flechas, hechizos, etc.)
 */
public abstract class ProyectilBase extends Actor {
    protected Animation<TextureRegion> animacion;
    protected Animation<TextureRegion> animacionImpacto;
    protected float stateTime = 0f;
    protected String direccion; // "DERECHA" o "IZQUIERDA"
    protected int danio;
    protected float velocidad;
    protected boolean impactado = false;
    protected boolean debeEliminarse = false;
    protected Rectangle hitbox = new Rectangle();

    /**
     * Constructor base para proyectiles
     */
    public ProyectilBase(float x, float y, String direccion, int danio, float velocidad) {
        setPosition(x, y);
        this.direccion = direccion;
        this.danio = danio;
        this.velocidad = velocidad;
        actualizarHitbox();
    }

    /**
     * Actualiza la posición de la hitbox basada en la posición del actor
     */
    protected void actualizarHitbox() {
        // Ajustar según el tamaño real del proyectil
        hitbox.set(getX(), getY(), 16, 8);
    }

    /**
     * Actualiza el estado del proyectil
     * @param delta tiempo transcurrido entre frames
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if (!impactado) {
            // Mover el proyectil en la dirección adecuada
            float movX = "DERECHA".equals(direccion) ? velocidad * delta : -velocidad * delta;
            moveBy(movX, 0);
            actualizarHitbox();

            // Eliminar si sale de la pantalla
            if (getX() < -50 || getX() > 1000) { // Ajustar según tamaño del mundo
                debeEliminarse = true;
            }
        } else {
            // Si está en animación de impacto, comprobar si ha terminado
            if (animacionImpacto.isAnimationFinished(stateTime)) {
                debeEliminarse = true;
            }
        }
    }

    /**
     * Comprueba colisiones con enemigos
     * @param enemigos lista de enemigos a comprobar
     */
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (impactado || enemigos == null || enemigos.isEmpty()) return;

        // Imprimir información de depuración sobre el proyectil
        System.out.println("Comprobando colisiones para proyectil en posición (" + getX() + ", " + getY() + ")");
        System.out.println("Hitbox proyectil: x=" + hitbox.x + ", y=" + hitbox.y + ", w=" + hitbox.width + ", h=" + hitbox.height);

        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                Rectangle enemyHitbox = e.getHitbox();
                boolean colision = hitbox.overlaps(enemyHitbox);

                if (colision) {
                    System.out.println("¡Colisión detectada con enemigo en (" + e.getX() + ", " + e.getY() + ")!");
                    // Aplicar daño al enemigo
                    e.recibirDanio(danio);

                    // Cambiar a estado de impacto
                    impactado = true;
                    stateTime = 0f; // Reiniciar para animación de impacto
                    break;
                }
            }
        }
    }

    /**
     * Dibujar el proyectil con la animación adecuada
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame;

        if (impactado) {
            frame = animacionImpacto.getKeyFrame(stateTime, false);
        } else {
            frame = animacion.getKeyFrame(stateTime, true);
        }

        if (frame != null) {
            if ("DERECHA".equals(direccion)) {
                batch.draw(frame, getX(), getY(), getWidth(), getHeight());
            } else {
                batch.draw(frame, getX() + getWidth(), getY(), -getWidth(), getHeight());
            }
        }
    }

    /**
     * Indica si el proyectil debe eliminarse
     */
    public boolean debeEliminarse() {
        return debeEliminarse;
    }

    /**
     * Libera recursos
     */
    public void dispose() {
        // Las subclases pueden sobrescribir para liberar recursos específicos
    }
}
