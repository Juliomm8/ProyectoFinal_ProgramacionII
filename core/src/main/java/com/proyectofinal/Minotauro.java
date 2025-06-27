package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;

public class Minotauro extends Enemigo {
    private static final float FRAME_DURATION = 0.1f;
    private static final float DETECTION_RANGE = 1000f; // Aumentado para detectar al jugador desde más lejos
    private static final float ATTACK_RANGE = 100f;

    public int getDanio() {
        return danio;
    }

    public Minotauro(float x, float y) {
        // Dar a cada minotauro una velocidad ligeramente diferente (entre 140 y 160)
        // para evitar que se muevan en grupo
        super(x, y, 100, 20, 140f + (float)(Math.random() * 20)); // vida: 100, daño: 20, velocidad: variada
    }

    @Override
    protected void cargarAnimaciones() {
        try {
            // Cargar animación Idle
            TextureRegion[] idleFrames = new TextureRegion[5];
            for (int i = 0; i < 5; i++) {
                idleFrames[i] = new TextureRegion(
                    new Texture(Gdx.files.internal("Enemigos/Minotauro/Idle/" + i + ".png"))
                );
            }
        idleAnimation = new Animation<>(FRAME_DURATION, idleFrames);

        // Cargar animación Walk
        TextureRegion[] walkFrames = new TextureRegion[5];
        for (int i = 0; i < 5; i++) {
            walkFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("Enemigos/Minotauro/Walk/" + i + ".png"))
            );
        }
        walkAnimation = new Animation<>(FRAME_DURATION, walkFrames);

        // Cargar animación Run
        TextureRegion[] runFrames = new TextureRegion[8];
        for (int i = 0; i < 8; i++) {
            runFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("Enemigos/Minotauro/Run/" + i + ".png"))
            );
        }
        runAnimation = new Animation<>(FRAME_DURATION, runFrames);

        // Cargar animaciones de ataque
        TextureRegion[] attackFrames = new TextureRegion[9];
        for (int i = 0; i < 9; i++) {
            attackFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("Enemigos/Minotauro/Attack/" + i + ".png"))
            );
        }
        attackAnimation = new Animation<>(FRAME_DURATION, attackFrames);

        TextureRegion[] attack2Frames = new TextureRegion[9];
        for (int i = 0; i < 9; i++) {
            attack2Frames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("Enemigos/Minotauro/Attack2/" + i + ".png"))
            );
        }
        attack2Animation = new Animation<>(FRAME_DURATION, attack2Frames);

        // Cargar animación Hit
        TextureRegion[] hitFrames = new TextureRegion[3];
        for (int i = 0; i < 3; i++) {
            hitFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("Enemigos/Minotauro/Hit/" + i + ".png"))
            );
        }
        hitAnimation = new Animation<>(FRAME_DURATION, hitFrames);

        // Cargar animación Death
        TextureRegion[] deathFrames = new TextureRegion[6];
        for (int i = 0; i < 6; i++) {
            deathFrames[i] = new TextureRegion(
                new Texture(Gdx.files.internal("Enemigos/Minotauro/Death/" + i + ".png"))
            );
        }
        deathAnimation = new Animation<>(FRAME_DURATION, deathFrames);
        } catch (Exception e) {
            System.err.println("Error al cargar las animaciones del Minotauro: " + e.getMessage());
            // Crear texturas de fallback/placeholder en caso de error
            Texture fallbackTexture = new Texture(64, 64, Pixmap.Format.RGBA8888);
            TextureRegion fallbackRegion = new TextureRegion(fallbackTexture);

            // Crear animaciones de un solo frame como fallback
            TextureRegion[] singleFrame = {fallbackRegion};
            idleAnimation = new Animation<>(FRAME_DURATION, singleFrame);
            walkAnimation = new Animation<>(FRAME_DURATION, singleFrame);
            runAnimation = new Animation<>(FRAME_DURATION, singleFrame);
            attackAnimation = new Animation<>(FRAME_DURATION, singleFrame);
            attack2Animation = new Animation<>(FRAME_DURATION, singleFrame);
            hitAnimation = new Animation<>(FRAME_DURATION, singleFrame);
            deathAnimation = new Animation<>(FRAME_DURATION, singleFrame);
        }
    }

    @Override
    public void update(float deltaTime, float playerX, float playerY) {
        stateTime += deltaTime;

        // Si el minotauro está muerto, solo actualizar el tiempo de la animación
        if (!estaVivo) {
            return;
        }

        // Si está recibiendo daño, mantener el estado de HIT durante un momento
        if (estadoActual == EstadoEnemigo.HIT) {
            if (hitAnimation.isAnimationFinished(stateTime)) {
                estadoActual = EstadoEnemigo.IDLE; // Volver a estado normal después del golpe
            } else {
                return; // No hacer nada más mientras está en animación de golpe
            }
        }

        // Si está atacando, esperar a que termine la animación
        if (estadoActual == EstadoEnemigo.ATTACKING) {
            if (attackAnimation.isAnimationFinished(stateTime)) {
                estadoActual = EstadoEnemigo.IDLE; // Reiniciar estado después del ataque
                stateTime = 0; // Reiniciar el tiempo de animación
            }
            return; // No hacer nada más mientras ataca
        }

        // Calcular distancia al jugador
        float distanciaAlJugador = (float) Math.sqrt(
            Math.pow(playerX - x, 2) + Math.pow(playerY - y, 2)
        );

        // Actualizar estado y comportamiento basado en la distancia
        if (distanciaAlJugador <= ATTACK_RANGE) {
            // Atacar al jugador cuando está en rango
            estadoActual = EstadoEnemigo.ATTACKING;
            stateTime = 0; // Reiniciar tiempo para la animación de ataque
        } else if (distanciaAlJugador <= DETECTION_RANGE) {
            // Perseguir al jugador cuando está dentro del rango de detección
            estadoActual = EstadoEnemigo.RUNNING;
            moverHaciaJugador(playerX, playerY, deltaTime);
        } else {
            // Estado de reposo cuando el jugador está lejos
            estadoActual = EstadoEnemigo.IDLE;
        }

        // Actualizar la hitbox en cada frame para seguir al sprite
        actualizarHitbox();
    }

    private boolean mirandoDerecha = true;
    private float ultimoX = 0;

    @Override
    public void render(SpriteBatch batch) {
        TextureRegion frameActual;

        switch (estadoActual) {
            case IDLE:
                frameActual = idleAnimation.getKeyFrame(stateTime, true);
                break;
            case WALKING:
                frameActual = walkAnimation.getKeyFrame(stateTime, true);
                break;
            case RUNNING:
                frameActual = runAnimation.getKeyFrame(stateTime, true);
                break;
            case ATTACKING:
                frameActual = attackAnimation.getKeyFrame(stateTime, false);
                break;
            case ATTACKING2:
                frameActual = attack2Animation.getKeyFrame(stateTime, false);
                break;
            case HIT:
                frameActual = hitAnimation.getKeyFrame(stateTime, false);
                break;
            case DYING:
                frameActual = deathAnimation.getKeyFrame(stateTime, false);
                break;
            default:
                frameActual = idleAnimation.getKeyFrame(stateTime, true);
        }

        // Determinar dirección basada en el movimiento
        if (ultimoX != x) {
            mirandoDerecha = x > ultimoX;
            ultimoX = x;
        }

        // Dibujar el frame actual con la orientación correcta
        if (mirandoDerecha) {
            batch.draw(frameActual, x, y, 64, 64);
        } else {
            // Voltear horizontalmente si mira a la izquierda
            batch.draw(frameActual, x + 64, y, -64, 64);
        }
    }
}
