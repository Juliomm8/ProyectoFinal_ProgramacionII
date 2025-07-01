package com.proyectofinal;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;
import com.badlogic.gdx.utils.TimeUtils;

public abstract class Enemigo implements Disposable {
    protected float width, height;

    // Tiempo de retraso para eliminar el enemigo después de la animación de muerte
    protected static final float TIEMPO_ELIMINACION = 1.0f;
    // Flag para indicar que el enemigo debe ser eliminado
    protected boolean marcarParaEliminar = false;
    // Contador para el tiempo después de la muerte
    protected float tiempoPostMortem = 0f;

    // Estados del enemigo
    public enum EstadoEnemigo {
        IDLE, WALKING, RUNNING, ATTACKING, ATTACKING2, HIT, DYING
    }

    // Coordenadas y propiedades básicas
    protected float x, y;
    protected int vida;
    protected int danio;
    protected float velocidad;
    protected boolean estaVivo = true;
    protected Rectangle hitbox = new Rectangle();

    // Propiedades de animación
    public float stateTime = 0f;
    public EstadoEnemigo estadoActual = EstadoEnemigo.IDLE;

    // Animaciones
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> attack2Animation;
    protected Animation<TextureRegion> hitAnimation;
    protected Animation<TextureRegion> deathAnimation;

    /** Tiempo (en segundos) de reutilización entre ataques. */
    protected float cooldownAttack = 0f;
    /** Marca de tiempo en milisegundos del último ataque completado. */
    protected long lastAttackTime = 0L;

    public Enemigo(float x, float y, int vida, int danio, float velocidad) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.danio = danio;
        this.velocidad = velocidad;
        this.estaVivo = true;
        this.stateTime = 0f;
        actualizarHitbox();
        cargarAnimaciones();
    }

    // Getters básicos
    public float getX() { return x; }
    public float getY() { return y; }
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public int getDanio() { return danio; }

    // Hitbox
    protected void actualizarHitbox() {
        hitbox.set(x, y, 64, 64);
    }
    public Rectangle getHitbox() {
        return hitbox;
    }

    // Estado de vida/eliminación
    public boolean estaVivo() {
        return estaVivo;
    }
    public boolean debeEliminarse() {
        return marcarParaEliminar;
    }

    // Carga de animaciones (implementar en cada subclase)
    protected abstract void cargarAnimaciones();

    // Verifica si el cooldown entre ataques ha pasado
    public boolean canAttack() {
        return TimeUtils.millis() - lastAttackTime >= (long)(cooldownAttack * 1000);
    }

    public boolean isReadyToRemove() {
        return marcarParaEliminar;
    }

    public void recibirDanio(int cantidad) {
        if (!estaVivo) {
            System.out.println("Enemigo ya está muerto, ignorando daño adicional");
            return;
        }
        vida -= cantidad;
        System.out.println("Enemigo recibió " + cantidad + " de daño. Vida restante: " + vida);
        if (vida <= 0) {
            vida = 0;
            estaVivo = false;
            estadoActual = EstadoEnemigo.DYING;
            stateTime = 0f;
            tiempoPostMortem = 0f;
            System.out.println("¡Enemigo ha muerto! Iniciando animación de muerte.");
        } else {
            estadoActual = EstadoEnemigo.HIT;
            stateTime = 0f;
        }
    }

    @Deprecated
    public void recibirDano(int cantidad) {
        recibirDanio(cantidad);
    }

    /**
     * Actualiza estados, animaciones y controla el ciclo de vida del enemigo.
     */
    public void update(float deltaTime, float playerX, float playerY) {
        stateTime += deltaTime;

        // 1) Gestión de muerte y eliminación
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

        // 2) Gestión de animación de golpe (HIT)
        if (estadoActual == EstadoEnemigo.HIT) {
            if (hitAnimation.isAnimationFinished(stateTime)) {
                estadoActual = EstadoEnemigo.IDLE;
                stateTime = 0f;
            }
            return;
        }

        // 3) Gestión de animación de ataque (ATTACKING)
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

        // 4) Lógica específica de cada subclase
        actualizarComportamiento(deltaTime, playerX, playerY);
    }

    /**
     * Implementar en subclases: detección, decisión de movimiento/ataque (usando canAttack()).
     */
    protected abstract void actualizarComportamiento(float deltaTime, float playerX, float playerY);

    /**
     * Renderiza el sprite correspondiente al estado actual.
     */
    public abstract void render(SpriteBatch batch);

    /**
     * Movimiento genérico hacia el jugador (puede usarse o sobrescribirse).
     */
    protected void moverHaciaJugador(float playerX, float playerY, float deltaTime) {
        Vector2 dir = new Vector2(playerX - x, playerY - y).nor();
        float dist = Vector2.dst(playerX, playerY, x, y);
        // Variación aleatoria para evitar agrupamientos
        if (Math.random() < 0.3) {
            dir.add((float)(Math.random()*0.8-0.4), (float)(Math.random()*0.8-0.4)).nor();
        }
        float factor = dist < 150 ? dist/150f : 1f;
        x += dir.x * velocidad * factor * deltaTime;
        y += dir.y * velocidad * factor * deltaTime;
        actualizarHitbox();
    }

    @Override
    public void dispose() {
        // Las subclases liberan sus texturas/recursos aquí
    }
}
