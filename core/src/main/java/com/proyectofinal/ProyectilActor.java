package com.proyectofinal;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;

/**
 * Actor que representa un proyectil (flecha, hechizo, etc.)
 */
public class ProyectilActor extends Actor {
    private Animation<TextureRegion> animation;
    private float stateTime = 0;
    private Vector2 velocity;
    private float speed;
    private int damage;
    private float width, height;
    private boolean impacted = false;
    private Animation<TextureRegion> impactAnimation;

    /**
     * Crea un proyectil
     * @param animation Animación del proyectil en vuelo
     * @param impactAnimation Animación del impacto
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param dirX Dirección X (normalizada)
     * @param dirY Dirección Y (normalizada)
     * @param speed Velocidad del proyectil
     * @param damage Daño que causa
     * @param width Ancho del sprite
     * @param height Alto del sprite
     */
    public ProyectilActor(Animation<TextureRegion> animation, Animation<TextureRegion> impactAnimation,
        float x, float y, float dirX, float dirY, float speed, int damage,
        float width, float height) {
            this.animation = animation;
            this.impactAnimation = impactAnimation;
            this.setPosition(x, y);
            this.velocity = new Vector2(dirX, dirY).nor();
            this.speed = speed;
            this.damage = damage;
            this.width = width;
            this.height = height;
            this.setSize(width, height);
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        stateTime += delta;

        if (impacted) {
            // Si ya impactó, verificar si terminó la animación de impacto
            if (impactAnimation != null && impactAnimation.isAnimationFinished(stateTime)) {
                remove(); // Eliminar del stage
            }
            return;
        }

        // Mover el proyectil según su velocidad
        float newX = getX() + velocity.x * speed * delta;
        float newY = getY() + velocity.y * speed * delta;
        setPosition(newX, newY);

        // Eliminar si sale de la pantalla
        if (isOutOfBounds()) {
            remove();
        }
    }

    private boolean isOutOfBounds() {
        // Asumiendo un mapa de 150x150 tiles, cada una de 32 píxeles
        return getX() < 0 || getX() > 150 * 32 || getY() < 0 || getY() > 150 * 32;
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (impacted && impactAnimation != null) {
            // Dibujar animación de impacto
            TextureRegion currentFrame = impactAnimation.getKeyFrame(stateTime, false);
            batch.draw(currentFrame, getX(), getY(), width, height);
        } else {
            // Dibujar animación de vuelo
            TextureRegion currentFrame = animation.getKeyFrame(stateTime, true);
            batch.draw(currentFrame, getX(), getY(), width, height);
        }
    }

    /**
     * Marca el proyectil como impactado y comienza la animación de impacto
     */
    public void impact() {
        impacted = true;
        stateTime = 0; // Reiniciar tiempo para la animación de impacto
    }

    /**
     * Obtiene el rectángulo de colisión del proyectil
     */
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), width, height);
    }

    /**
     * Obtiene el daño que causa el proyectil
     */
    public int getDamage() {
        return damage;
    }
}
