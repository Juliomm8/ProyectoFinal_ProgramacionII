package com.proyectofinal;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Disposable;

public abstract class Enemigo implements Disposable {
    protected float width, height;

    // Tiempo de retraso para eliminar el enemigo después de la animación de muerte
    protected static final float TIEMPO_ELIMINACION = 1.0f;
    // Flag para indicar que el enemigo debe ser eliminado
    protected boolean marcarParaEliminar = false;
    // Contador para el tiempo después de la muerte
    protected float tiempoPostMortem = 0f;

    public float getHeight() {
    return height;
    }
    public float getWidth() {
    return width;
    }

    // Estados del enemigo
    public enum EstadoEnemigo {
        IDLE, WALKING, RUNNING, ATTACKING, ATTACKING2, HIT, DYING
    }

    // Coordenadas y propiedades básicas
    protected float x, y;
    protected int vida;
    public int danio;
    protected float velocidad;
    protected boolean estaVivo = true;
    protected Rectangle hitbox = new Rectangle();

    // Propiedades de animación
    public float stateTime = 0;
    public EstadoEnemigo estadoActual = EstadoEnemigo.IDLE;

    // Animaciones
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> attack2Animation;
    protected Animation<TextureRegion> hitAnimation;
    protected Animation<TextureRegion> deathAnimation;

    // Tiempo (en segundos) de reutilización entre ataques.
    protected float cooldownAttack = 0f;
    // Instante en nanosegundos del último ataque realizado.
    protected long lastAttackTime = 0L;

    public Enemigo(float x, float y, int vida, int danio, float velocidad) {
        this.x = x;
        this.y = y;
        this.vida = vida;
        this.danio = danio;
        this.velocidad = velocidad;
        this.estaVivo = true;
        this.stateTime = 0;
        actualizarHitbox();
        cargarAnimaciones();
    }

    public float getY() {
        return y;
    }

    public float getX() {
        return x;
    }

    protected abstract void cargarAnimaciones();

    protected void actualizarHitbox() {
        // Ajusta estos valores según el tamaño de tu sprite
        hitbox.set(x, y, 64, 64);
    }

    public Rectangle getHitbox() {
        return hitbox;
    }

    public boolean estaVivo() {
        return estaVivo;
    }

    /**
     * Comprueba si este enemigo debe ser eliminado del stage
     * @return true si debe eliminarse
     */
    public boolean debeEliminarse() {
        return marcarParaEliminar;
    }

    /**
     * Indica si este enemigo completó su animación de muerte y puede ser retirado.
     */
    public boolean isReadyToRemove() {
        return marcarParaEliminar;
    }

    public void recibirDanio(int cantidad) {
        // Si ya está muerto, no hacer nada
        if (!estaVivo) {
            System.out.println("Enemigo ya está muerto, ignorando daño adicional");
            return;
        }

        // Aplicar daño y cambiar a estado de golpe
        vida -= cantidad;
        System.out.println("Enemigo recibió " + cantidad + " de daño. Vida restante: " + vida);

        // Verificar si el enemigo debe morir
        if (vida <= 0) {
            System.out.println("¡Enemigo ha muerto! Cambiando a animación de muerte");
            vida = 0; // Asegurar que la vida no sea negativa
            estaVivo = false;
            estadoActual = EstadoEnemigo.DYING;
            stateTime = 0; // Reiniciar tiempo para animación de muerte
            tiempoPostMortem = 0f; // Reiniciar contador de tiempo post-mortem
        } else {
            // Solo cambiar a estado de golpe si no va a morir
            estadoActual = EstadoEnemigo.HIT;
            stateTime = 0; // Reiniciar tiempo para animación de hit
        }
    }

    /**
     * Comprueba si el enemigo puede ejecutar un nuevo ataque.
     *
     * @return {@code true} si ha transcurrido el tiempo de reutilización.
     */
    protected boolean canAttack() {
        return com.badlogic.gdx.utils.TimeUtils.nanoTime() - lastAttackTime >= (long) (cooldownAttack * 1_000_000_000L);
    }

    // Mantener método anterior para compatibilidad con código existente
    @Deprecated
    public void recibirDano(int cantidad) {
        recibirDanio(cantidad); // Redirigir al método con nombre correcto
    }

    /**
     * Actualiza el estado y comportamiento del enemigo.
     *
     * @param delta    tiempo transcurrido desde el último frame
     * @param playerX  posición X del jugador para la IA
     * @param playerY  posición Y del jugador para la IA
     */
    public void update(float delta, float playerX, float playerY) {
        stateTime += delta;

        if (!estaVivo) {
            if (estadoActual != EstadoEnemigo.DYING) {
                estadoActual = EstadoEnemigo.DYING;
                stateTime = 0f;
            } else if (deathAnimation.isAnimationFinished(stateTime)) {
                tiempoPostMortem += delta;
                if (tiempoPostMortem >= TIEMPO_ELIMINACION) {
                    marcarParaEliminar = true;
                }
            }
            return;
        }

        if (estadoActual == EstadoEnemigo.HIT) {
            if (hitAnimation != null && hitAnimation.isAnimationFinished(stateTime)) {
                estadoActual = EstadoEnemigo.IDLE;
                stateTime = 0f;
            } else {
                return;
            }
        }

        if (estadoActual == EstadoEnemigo.ATTACKING) {
            if (attackAnimation != null && attackAnimation.isAnimationFinished(stateTime)) {
                afterAttack();
                estadoActual = EstadoEnemigo.IDLE;
                stateTime = 0f;
            } else {
                return;
            }
        }

        actualizarComportamiento(delta, playerX, playerY);
    }

    /**
     * Método abstracto para implementar el comportamiento específico de cada enemigo
     */
    protected abstract void actualizarComportamiento(float deltaTime, float playerX, float playerY);

    public abstract void render(SpriteBatch batch);

    /** Hook ejecutado justo antes de iniciar la animación de ataque. */
    protected void beforeAttack() {
        // Implementación opcional en subclases
    }

    /** Hook ejecutado tras finalizar la animación de ataque. */
    protected void afterAttack() {
        // Implementación opcional en subclases
    }

    protected void moverHaciaJugador(float playerX, float playerY, float deltaTime) {
        // Calcular dirección hacia el jugador
        Vector2 direccion = new Vector2(playerX - x, playerY - y);
        float distancia = direccion.len();
        direccion.nor();

        // Añadir variación aleatoria MAYOR al movimiento para evitar agrupaciones
        // Con esto se evita que varios enemigos sigan exactamente la misma trayectoria
        if (Math.random() < 0.3) { // 30% de probabilidad por frame (aumentada)
            float variacionX = (float)(Math.random() * 0.8 - 0.4); // Variación entre -0.4 y 0.4 (aumentada)
            float variacionY = (float)(Math.random() * 0.8 - 0.4); // Variación entre -0.4 y 0.4 (aumentada)
            direccion.add(variacionX, variacionY);
            direccion.nor(); // Normalizar de nuevo
        }

        // Reducir velocidad cuando está cerca para evitar sobrepasarse
        float factorVelocidad = 1.0f;
        if (distancia < 150) {
            factorVelocidad = distancia / 150;
        }

        // Aplicar movimiento
        x += direccion.x * velocidad * factorVelocidad * deltaTime;
        y += direccion.y * velocidad * factorVelocidad * deltaTime;

        actualizarHitbox();
    }

    /**
     * Libera los recursos utilizados por este enemigo
     */
    @Override
    public void dispose() {
        // Las subclases pueden sobreescribir este método para liberar recursos adicionales
    }
}
