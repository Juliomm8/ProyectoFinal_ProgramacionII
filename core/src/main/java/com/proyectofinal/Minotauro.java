package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;

/**
 * Clase Minotauro que representa un enemigo tipo jefe.
 * Posee animaciones para diferentes estados y persigue al jugador si está cerca.
 */
public class Minotauro extends Enemigo {
    private static final float FRAME_DURATION   = 0.1f;
    private static final float DETECTION_RANGE  = 2000f; // Rango de detección del jugador
    private static final float ATTACK_RANGE     = 25f;   // Rango para realizar ataque
    private final Jugador jugador;

    // Arreglos para almacenar texturas de cada animación
    private Texture[] idleTextures;
    private Texture[] walkTextures;
    private Texture[] runTextures;
    private Texture[] attackTextures;
    private Texture[] attack2Textures;
    private Texture[] hitTextures;
    private Texture[] deathTextures;

    private boolean facingRight = true; // Controla el volteo del sprite
    private float lastX;               // Para determinar dirección de movimiento

    public Minotauro(float x, float y, Jugador jugador) {
        super(x, y,
            /* vida */   1,
            /* danio */  1,
            /* velocidad */ 85f + (float)(Math.random() * 10));
        this.jugador = jugador;
        this.cooldownAttack = 1.0f; // Cooldown entre ataques
        this.width  = 64f;
        this.height = 64f;
    }

    /**
     * Carga todas las animaciones del minotauro desde los archivos.
     */
    @Override
    protected void cargarAnimaciones() {
        try {
            // Animación Idle
            idleTextures = new Texture[5];
            var idleFrames = new TextureRegion[5];
            for (int i = 0; i < idleTextures.length; i++) {
                idleTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Idle/" + i + ".png"));
                idleFrames[i]    = new TextureRegion(idleTextures[i]);
            }
            idleAnimation = new Animation<>(FRAME_DURATION, idleFrames);

            // Animación de caminar
            walkTextures = new Texture[5];
            var walkFrames = new TextureRegion[5];
            for (int i = 0; i < walkTextures.length; i++) {
                walkTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Walk/" + i + ".png"));
                walkFrames[i]   = new TextureRegion(walkTextures[i]);
            }
            walkAnimation = new Animation<>(FRAME_DURATION, walkFrames);

            // Animación de correr
            runTextures = new Texture[8];
            var runFrames = new TextureRegion[8];
            for (int i = 0; i < runTextures.length; i++) {
                runTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Run/" + i + ".png"));
                runFrames[i]   = new TextureRegion(runTextures[i]);
            }
            runAnimation = new Animation<>(FRAME_DURATION, runFrames);

            // Ataque 1
            attackTextures = new Texture[9];
            var attackFrames = new TextureRegion[9];
            for (int i = 0; i < attackTextures.length; i++) {
                attackTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Attack/" + i + ".png"));
                attackFrames[i]   = new TextureRegion(attackTextures[i]);
            }
            attackAnimation = new Animation<>(FRAME_DURATION, attackFrames);

            // Ataque 2
            attack2Textures = new Texture[9];
            var attack2Frames = new TextureRegion[9];
            for (int i = 0; i < attack2Textures.length; i++) {
                attack2Textures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Attack2/" + i + ".png"));
                attack2Frames[i]   = new TextureRegion(attack2Textures[i]);
            }
            attack2Animation = new Animation<>(FRAME_DURATION, attack2Frames);

            // Animación de recibir golpe
            hitTextures = new Texture[3];
            var hitFrames = new TextureRegion[3];
            for (int i = 0; i < hitTextures.length; i++) {
                hitTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Hit/" + i + ".png"));
                hitFrames[i]   = new TextureRegion(hitTextures[i]);
            }
            hitAnimation = new Animation<>(FRAME_DURATION, hitFrames);

            // Animación de muerte
            deathTextures = new Texture[6];
            var deathFrames = new TextureRegion[6];
            for (int i = 0; i < deathTextures.length; i++) {
                deathTextures[i] = new Texture(Gdx.files.internal("Enemigos/Minotauro/Death/" + i + ".png"));
                deathFrames[i]   = new TextureRegion(deathTextures[i]);
            }
            deathAnimation = new Animation<>(FRAME_DURATION, deathFrames);

        } catch (Exception e) {
            // Si hay error, usar frames vacíos
            System.err.println("Error al cargar animaciones de Minotauro: " + e.getMessage());
            var fallbackRegion = new TextureRegion(new Texture(64, 64, Pixmap.Format.RGBA8888));
            idleAnimation    = new Animation<>(FRAME_DURATION, fallbackRegion);
            walkAnimation    = new Animation<>(FRAME_DURATION, fallbackRegion);
            runAnimation     = new Animation<>(FRAME_DURATION, fallbackRegion);
            attackAnimation  = new Animation<>(FRAME_DURATION, fallbackRegion);
            attack2Animation = new Animation<>(FRAME_DURATION, fallbackRegion);
            hitAnimation     = new Animation<>(FRAME_DURATION, fallbackRegion);
            deathAnimation   = new Animation<>(FRAME_DURATION, fallbackRegion);
        }
    }

    /**
     * Comportamiento general del minotauro según distancia al jugador.
     */
    @Override
    protected void actualizarComportamiento(float deltaTime, float playerX, float playerY) {
        float distancia = Vector2.dst(playerX, playerY, x, y);

        if (distancia <= ATTACK_RANGE && canAttack()) {
            estadoActual = EstadoEnemigo.ATTACKING;
            stateTime    = 0f;
            jugador.recibirDanio(getDanio());
        } else if (distancia <= DETECTION_RANGE) {
            estadoActual = EstadoEnemigo.RUNNING;
            moverHaciaJugador(playerX, playerY, deltaTime);
        } else {
            estadoActual = EstadoEnemigo.IDLE;
        }

        actualizarHitbox();
    }

    /**
     * Renderiza al minotauro según su estado actual.
     */
    @Override
    public void render(SpriteBatch batch) {
        if (marcarParaEliminar) return;

        TextureRegion frame;
        switch (estadoActual) {
            case WALKING:    frame = walkAnimation.getKeyFrame(stateTime, true);  break;
            case RUNNING:    frame = runAnimation.getKeyFrame(stateTime, true); break;
            case ATTACKING:  frame = attackAnimation.getKeyFrame(stateTime, false); break;
            case ATTACKING2: frame = attack2Animation.getKeyFrame(stateTime, false); break;
            case HIT:        frame = hitAnimation.getKeyFrame(stateTime, false); break;
            case DYING:      frame = deathAnimation.getKeyFrame(stateTime, false); break;
            case IDLE:
            default:         frame = idleAnimation.getKeyFrame(stateTime, true); break;
        }

        // Volteo de sprite según movimiento
        if (x != lastX) {
            facingRight = x > lastX;
            lastX = x;
        }

        if (facingRight) {
            batch.draw(frame, x, y, 64, 64);
        } else {
            batch.draw(frame, x + 64, y, -64, 64); // Volteado horizontal
        }
    }

    /**
     * Libera las texturas de todas las animaciones.
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

    // Método auxiliar para liberar arrays de texturas
    private void disposeTextures(Texture[] textures) {
        if (textures == null) return;
        for (var t : textures) {
            if (t != null) t.dispose();
        }
    }
}
