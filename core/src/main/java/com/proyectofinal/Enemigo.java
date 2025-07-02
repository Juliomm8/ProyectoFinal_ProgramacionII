package com.proyectofinal;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

/**
 * Clase base abstracta para todos los enemigos del juego.
 * Maneja estados, animaciones, logica de ataque, daño y movimiento.
 */
public abstract class Enemigo implements Disposable {

    // Dimensiones del sprite
    protected float width, height;

    // Tiempo que debe pasar tras la muerte para eliminar al enemigo
    protected static final float TIEMPO_ELIMINACION = 1.0f;
    protected boolean marcarParaEliminar = false;
    protected float tiempoPostMortem = 0f;

    // Estados posibles del enemigo
    public enum EstadoEnemigo {
        IDLE, WALKING, RUNNING, ATTACKING, ATTACKING2, HIT, DYING
    }

    // Posicion y propiedades basicas
    protected float x, y;
    protected int vida;
    protected int danio;
    protected float velocidad;
    protected boolean estaVivo = true;
    protected Rectangle hitbox = new Rectangle();

    // Animacion y estado actual
    public float stateTime = 0f;
    public EstadoEnemigo estadoActual = EstadoEnemigo.IDLE;

    // Animaciones por estado
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> attack2Animation;
    protected Animation<TextureRegion> hitAnimation;
    protected Animation<TextureRegion> deathAnimation;

    // Cooldown entre ataques (en segundos)
    protected float cooldownAttack = 0f;
    protected long lastAttackTime = 0L; // Tiempo del ultimo ataque en milisegundos

    // Constructor
    public Enemigo(float x, float y, int vida, int danio, float velocidad) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.danio = danio;
        this.velocidad = velocidad;
        this.estaVivo = true;
        this.stateTime = 0f;
        actualizarHitbox();
        cargarAnimaciones(); // Cada subclase implementa esto
    }

    // Getters
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getDanio() { return danio; }

    // Actualizar hitbox segun posicion
    protected void actualizarHitbox() {
        hitbox.set(x, y, 64, 64);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean estaVivo() {
        return estaVivo;
    }

    public boolean debeEliminarse() {
        return marcarParaEliminar;
    }

    // Verifica si puede atacar (segun cooldown)
    public boolean canAttack() {
        return TimeUtils.millis() - lastAttackTime >= (long)(cooldownAttack * 1000);
    }

    public boolean isReadyToRemove() {
        return marcarParaEliminar;
    }

    // Metodo principal para recibir daño
    public void recibirDanio(int cantidad) {
        if (!estaVivo) {
            System.out.println("Enemigo ya esta muerto, ignorando daño adicional");
            return;
        }
        vida -= cantidad;
        System.out.println("Enemigo recibio " + cantidad + " de daño. Vida restante: " + vida);
        if (vida <= 0) {
            vida = 0;
            estaVivo = false;
            estadoActual = EstadoEnemigo.DYING;
            stateTime = 0f;
            tiempoPostMortem = 0f;
            System.out.println("¡Enemigo ha muerto! Iniciando animacion de muerte.");
        } else {
            estadoActual = EstadoEnemigo.HIT;
            stateTime = 0f;
        }
    }

    // Metodo deprecated por si alguien usa "Dano" en lugar de "Danio"
    @Deprecated
    public void recibirDano(int cantidad) {
        recibirDanio(cantidad);
    }

    /**
     * Metodo de actualizacion general del enemigo.
     * Controla la animacion de muerte, golpe y ataque.
     * Delega la logica especifica a la subclase.
     */
    public void update(float deltaTime, float playerX, float playerY) {
        stateTime += deltaTime;

        // Gestion de muerte
        if (!estaVivo) {
            if (estadoActual != EstadoEnemigo.DYING) {
                estadoActual = EstadoEnemigo.DYING;
                stateTime = 0f;
            }
            if (deathAnimation.isAnimationFinished(stateTime)) {
                tiempoPostMortem += deltaTime;
                if (tiempoPostMortem >= TIEMPO_ELIMINACION) {
                    marcarParaEliminar = true;
                }
            }
            return;
        }

        // Gestion de animacion de golpe
        if (estadoActual == EstadoEnemigo.HIT) {
            if (hitAnimation.isAnimationFinished(stateTime)) {
                estadoActual = EstadoEnemigo.IDLE;
                stateTime = 0f;
            }
            return;
        }

        // Gestion de animacion de ataque
        if (estadoActual == EstadoEnemigo.ATTACKING || estadoActual == EstadoEnemigo.ATTACKING2) {
            Animation<TextureRegion> anim = (estadoActual == EstadoEnemigo.ATTACKING)
                ? attackAnimation
                : attack2Animation;
            if (anim.isAnimationFinished(stateTime)) {
                estadoActual = EstadoEnemigo.IDLE;
                stateTime = 0f;
                lastAttackTime = TimeUtils.millis();
            }
            return;
        }

        // Llamar a la logica personalizada de la subclase
        actualizarComportamiento(deltaTime, playerX, playerY);
    }

    /**
     * Metodo abstracto para cargar las animaciones del enemigo.
     * Cada clase hija lo implementa.
     */
    protected abstract void cargarAnimaciones();

    /**
     * Metodo abstracto para implementar la logica personalizada del enemigo.
     * Se llama desde update().
     */
    protected abstract void actualizarComportamiento(float deltaTime, float playerX, float playerY);

    /**
     * Metodo abstracto para renderizar el sprite segun la animacion actual.
     */
    public abstract void render(SpriteBatch batch);

    /**
     * Movimiento generico hacia el jugador.
     * Se puede sobrescribir si se necesita un comportamiento mas complejo.
     */
    protected void moverHaciaJugador(float playerX, float playerY, float deltaTime) {
        Vector2 dir = new Vector2(playerX - x, playerY - y).nor(); // Direccion normalizada
        float dist = Vector2.dst(playerX, playerY, x, y); // Distancia al jugador

        // Variacion aleatoria para que los enemigos no se agrupen exactamente
        if (Math.random() < 0.3) {
            dir.add((float)(Math.random()*0.8 - 0.4), (float)(Math.random()*0.8 - 0.4)).nor();
        }

        float factor = dist < 150 ? dist / 150f : 1f; // Frenar si esta muy cerca
        x += dir.x * velocidad * factor * deltaTime;
        y += dir.y * velocidad * factor * deltaTime;
        actualizarHitbox();
    }

    /**
     * Las subclases deben liberar sus texturas u otros recursos aqui.
     */
    @Override
    public void dispose() {
        // Nada aqui, lo hacen las subclases
    }
}
