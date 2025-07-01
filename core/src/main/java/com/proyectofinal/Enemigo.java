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
    public int danio; // Hecho público para acceso desde DungeonScreen
    protected float velocidad;
    protected boolean estaVivo = true;
    protected Rectangle hitbox = new Rectangle();

    // Propiedades de animación
    public float stateTime = 0; // Hecho público para acceso desde DungeonScreen
    public EstadoEnemigo estadoActual = EstadoEnemigo.IDLE;

    // Animaciones
    protected Animation<TextureRegion> idleAnimation;
    protected Animation<TextureRegion> walkAnimation;
    protected Animation<TextureRegion> runAnimation;
    protected Animation<TextureRegion> attackAnimation;
    protected Animation<TextureRegion> attack2Animation;
    protected Animation<TextureRegion> hitAnimation;
    protected Animation<TextureRegion> deathAnimation;

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

    // Mantener método anterior para compatibilidad con código existente
    @Deprecated
    public void recibirDano(int cantidad) {
        recibirDanio(cantidad); // Redirigir al método con nombre correcto
    }

    public void update(float deltaTime, float playerX, float playerY) {
        // Incrementar el tiempo de estado
        stateTime += deltaTime;

        // Si el enemigo está muerto (en animación de muerte)
        if (!estaVivo) {
            // Si está en estado de morir, comprobar si la animación ha terminado
            if (estadoActual == EstadoEnemigo.DYING) {
                // Verificar si la animación ha terminado
                if (deathAnimation.isAnimationFinished(stateTime)) {
                    // Incrementar el contador post-mortem
                    tiempoPostMortem += deltaTime;

                    // Imprimir información de depuración
                    System.out.println("Enemigo muerto, tiempo post-mortem: " + tiempoPostMortem + "/" + TIEMPO_ELIMINACION);

                    // Si ha pasado el tiempo de retraso, marcar para eliminar
                    if (tiempoPostMortem >= TIEMPO_ELIMINACION) {
                        System.out.println("Marcando enemigo para eliminar");
                        marcarParaEliminar = true;
                    }
                }
            } else {
                // Si el enemigo está muerto pero no en estado DYING, corregir el estado
                estadoActual = EstadoEnemigo.DYING;
                stateTime = 0f;
                System.out.println("Corrigiendo estado de enemigo muerto a DYING");
            }
            return; // No procesar más la lógica de enemigo vivo
        }

        // Implementación específica en las subclases
        actualizarComportamiento(deltaTime, playerX, playerY);
    }

    /**
     * Método abstracto para implementar el comportamiento específico de cada enemigo
     */
    protected abstract void actualizarComportamiento(float deltaTime, float playerX, float playerY);

    public abstract void render(SpriteBatch batch);

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
