package com.proyectofinal;

import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;

public abstract class Enemigo {
    protected float width, height;

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

    public void recibirDano(int cantidad) {
        if (!estaVivo) return;

        vida -= cantidad;
        estadoActual = EstadoEnemigo.HIT;
        stateTime = 0; // Reiniciar tiempo para animación de hit

        if (vida <= 0) {
            estaVivo = false;
            estadoActual = EstadoEnemigo.DYING;
            stateTime = 0; // Reiniciar tiempo para animación de muerte
        }
    }

    public abstract void update(float deltaTime, float playerX, float playerY);

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
}
