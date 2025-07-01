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

    // Referencias a las texturas de cada animación para liberarlas posteriormente
    private Texture[] idleTextures;
    private Texture[] walkTextures;
    private Texture[] runTextures;
    private Texture[] attackTextures;
    private Texture[] attack2Textures;
    private Texture[] hitTextures;
    private Texture[] deathTextures;

    public int getDanio() {
        return danio;
    }

    public Minotauro(float x, float y) {
        super(x, y, 1, 10, 80f + (float)(Math.random() * 10));
    }

    @Override
    protected void cargarAnimaciones() {
        try {
            // Cargar animación Idle
            idleTextures = new Texture[5];
            TextureRegion[] idleFrames = new TextureRegion[5];
            for (int i = 0; i < 5; i++) {
                idleTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Idle/" + i + ".png"));
                idleFrames[i] = new TextureRegion(idleTextures[i]);
            }
            idleAnimation = new Animation<>(FRAME_DURATION, idleFrames);

            // Cargar animación Walk
            walkTextures = new Texture[5];
            TextureRegion[] walkFrames = new TextureRegion[5];
            for (int i = 0; i < 5; i++) {
                walkTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Walk/" + i + ".png"));
                walkFrames[i] = new TextureRegion(walkTextures[i]);
            }
            walkAnimation = new Animation<>(FRAME_DURATION, walkFrames);

            // Cargar animación Run
            runTextures = new Texture[8];
            TextureRegion[] runFrames = new TextureRegion[8];
            for (int i = 0; i < 8; i++) {
                runTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Run/" + i + ".png"));
                runFrames[i] = new TextureRegion(runTextures[i]);
            }
            runAnimation = new Animation<>(FRAME_DURATION, runFrames);

            // Cargar animaciones de ataque
            attackTextures = new Texture[9];
            TextureRegion[] attackFrames = new TextureRegion[9];
            for (int i = 0; i < 9; i++) {
                attackTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Attack/" + i + ".png"));
                attackFrames[i] = new TextureRegion(attackTextures[i]);
            }
            attackAnimation = new Animation<>(FRAME_DURATION, attackFrames);

            attack2Textures = new Texture[9];
            TextureRegion[] attack2Frames = new TextureRegion[9];
            for (int i = 0; i < 9; i++) {
                attack2Textures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Attack2/" + i + ".png"));
                attack2Frames[i] = new TextureRegion(attack2Textures[i]);
            }
            attack2Animation = new Animation<>(FRAME_DURATION, attack2Frames);

            // Cargar animación Hit
            hitTextures = new Texture[3];
            TextureRegion[] hitFrames = new TextureRegion[3];
            for (int i = 0; i < 3; i++) {
                hitTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Hit/" + i + ".png"));
                hitFrames[i] = new TextureRegion(hitTextures[i]);
            }
            hitAnimation = new Animation<>(FRAME_DURATION, hitFrames);

            // Cargar animación Death
            deathTextures = new Texture[6];
            TextureRegion[] deathFrames = new TextureRegion[6];
            for (int i = 0; i < 6; i++) {
                deathTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Death/" + i + ".png"));
                deathFrames[i] = new TextureRegion(deathTextures[i]);

                deathAnimation = new Animation<>(FRAME_DURATION, deathFrames);
            }
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

    /**
     * Recibe daño y cambia inmediatamente a estado de muerte.
     * Cualquier ataque mata al Minotauro de un solo golpe.
     */
    @Override
    public void recibirDanio(int cantidad) {
        // Si ya está muerto, no hacer nada
        if (!estaVivo) {
            System.out.println("Minotauro ya está muerto, ignorando daño adicional");
            return;
        }

        // Forzar vida = 0 independientemente de la cantidad de daño
        vida = 0;
        estaVivo = false;

        // Cambiar a estado de muerte y reiniciar contador de animación
        estadoActual = EstadoEnemigo.DYING;
        stateTime = 0f;
        tiempoPostMortem = 0f;

        System.out.println("¡Minotauro abatido de un solo golpe! Reproduciendo animación de muerte...");
        System.out.println("Estado actual: " + estadoActual + ", estaVivo: " + estaVivo);
    }

    // Mantener método anterior para compatibilidad con código existente
    @Override
    @Deprecated
    public void recibirDano(int cantidad) {
        recibirDanio(cantidad); // Redirigir al método con nombre correcto
    }

    @Override
    protected void actualizarComportamiento(float deltaTime, float playerX, float playerY) {
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
        // Si ya está marcado para eliminar, no renderizar
        if (marcarParaEliminar) return;

        TextureRegion frameActual = null;

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
                // Comprobar si la animación de muerte ha terminado para actualizar
                if (deathAnimation.isAnimationFinished(stateTime)) {
                    // Solo para depuración: mostrar que se completó la animación
                    System.out.println("Animación de muerte de Minotauro completada");
                }
                break;
            default:
                frameActual = idleAnimation.getKeyFrame(stateTime, true);
        }

        // Verificar que el frame no sea nulo
        if (frameActual == null) {
            System.err.println("Error: frame nulo en render de Minotauro");
            return;
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

    /**
     * Libera las texturas utilizadas por este enemigo.
     */
    @Override
    public void dispose() {
        disposeTextures(idleTextures);
        disposeTextures(walkTextures);
        disposeTextures(runTextures);
        disposeTextures(attackTextures);
        disposeTextures(attack2Textures);
        disposeTextures(hitTextures);
        disposeTextures(deathTextures);
    }

    private void disposeTextures(Texture[] textures) {
        if (textures == null) return;
        for (Texture t : textures) {
            if (t != null) t.dispose();
        }
    }
}
