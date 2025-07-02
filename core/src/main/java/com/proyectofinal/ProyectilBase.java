package com.proyectofinal;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Actor;

import java.util.List;

/**
 * Clase base abstracta para todos los proyectiles del juego (como flechas o hechizos).
 * Incluye logica comun de movimiento, animacion, colision e impacto.
 */
public abstract class ProyectilBase extends Actor {

    protected Animation<TextureRegion> animacion;            // Animacion durante el vuelo
    protected Animation<TextureRegion> animacionImpacto;     // Animacion al impactar
    protected float stateTime = 0f;                           // Tiempo acumulado para las animaciones
    protected String direccion;                              // Direccion del disparo: "DERECHA" o "IZQUIERDA"
    protected int danio;                                     // Cantidad de danio que causa
    protected float velocidad;                               // Velocidad de movimiento
    protected boolean impactado = false;                     // Marca si ya impacto
    protected boolean debeEliminarse = false;                // Marca si debe eliminarse del escenario
    protected Rectangle hitbox = new Rectangle();            // Rectangulo de colision

    /**
     * Constructor base del proyectil.
     * @param x posicion inicial en X
     * @param y posicion inicial en Y
     * @param direccion direccion del proyectil ("DERECHA" o "IZQUIERDA")
     * @param danio cantidad de danio que causa
     * @param velocidad velocidad de desplazamiento
     */
    public ProyectilBase(float x, float y, String direccion, int danio, float velocidad) {
        setPosition(x, y);
        this.direccion = direccion;
        this.danio = danio;
        this.velocidad = velocidad;
        actualizarHitbox();
    }

    /**
     * Actualiza la posicion de la hitbox en base a la posicion del proyectil.
     * Puede ajustarse segun el tamano real del sprite.
     */
    protected void actualizarHitbox() {
        hitbox.set(getX(), getY(), 16, 8); // Tamano por defecto
    }

    /**
     * Actualiza el estado del proyectil en cada frame.
     * Maneja el movimiento y el ciclo de vida.
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if (!impactado) {
            // Movimiento continuo en la direccion asignada
            float movX = "DERECHA".equals(direccion) ? velocidad * delta : -velocidad * delta;
            moveBy(movX, 0);
            actualizarHitbox();

            // Eliminar si sale del area visible
            if (getX() < -50 || getX() > 1000) {
                debeEliminarse = true;
            }

        } else {
            // Si ya impacto, esperar a que termine la animacion de impacto
            if (animacionImpacto.isAnimationFinished(stateTime)) {
                debeEliminarse = true;
            }
        }
    }

    /**
     * Comprueba si el proyectil colisiona con algun enemigo.
     * Si impacta, aplica danio y cambia a animacion de impacto.
     * @param enemigos lista de enemigos a verificar
     */
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (impactado || enemigos == null || enemigos.isEmpty()) return;

        // Debug: imprimir datos de posicion y colision
        System.out.println("Comprobando colisiones para proyectil en posicion (" + getX() + ", " + getY() + ")");
        System.out.println("Hitbox proyectil: x=" + hitbox.x + ", y=" + hitbox.y + ", w=" + hitbox.width + ", h=" + hitbox.height);

        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                Rectangle enemyHitbox = e.getHitbox();
                boolean colision = hitbox.overlaps(enemyHitbox);

                if (colision) {
                    System.out.println("¡Colision detectada con enemigo en (" + e.getX() + ", " + e.getY() + ")!");
                    e.recibirDanio(danio);  // Aplicar danio
                    impactado = true;
                    stateTime = 0f;         // Reiniciar para animacion de impacto
                    break;                  // Solo un impacto por proyectil
                }
            }
        }
    }

    /**
     * Dibuja el proyectil con la animacion correspondiente.
     * @param batch el batch donde se dibuja
     * @param parentAlpha opacidad heredada
     */
    @Override
    public void draw(Batch batch, float parentAlpha) {
        TextureRegion frame;

        // Elegir animacion segun si ya impacto o no
        if (impactado) {
            frame = animacionImpacto.getKeyFrame(stateTime, false);
        } else {
            frame = animacion.getKeyFrame(stateTime, true);
        }

        if (frame != null) {
            // Dibujar volteado si va a la izquierda
            if ("DERECHA".equals(direccion)) {
                batch.draw(frame, getX(), getY(), getWidth(), getHeight());
            } else {
                batch.draw(frame, getX() + getWidth(), getY(), -getWidth(), getHeight());
            }
        }
    }

    /**
     * Indica si el proyectil ya cumplio su ciclo y debe eliminarse del escenario.
     */
    public boolean debeEliminarse() {
        return debeEliminarse;
    }

    /**
     * Metodo opcional para que subclases liberen recursos si es necesario.
     */
    public void dispose() {
        // Sobrescribir si hay recursos que liberar
    }
}
