package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

/**
 * Actor que maneja la lógica y dibujo del jugador,
 * con animaciones de idle, corrida y ataque.
 */
public class PlayerActor extends Image {
    private final Jugador jugador;
    private final TextureRegion idleRegion;

    // Ataque
    private boolean atacando = false;
    private boolean impactoHecho = false;
    private float tiempoAnimAtaque = 0f;
    private int frameAttack = 0;
    private TextureRegion[] attackFrames;
    private static final float ATTACK_FRAME_DUR = 0.07f;
    private static final int ATTACK_IMPACT = 4;

    // Corrida/Movimiento (Caballero, Mago y Arquero)
    private boolean corriendo = false;
    private float tiempoCorrida = 0f;
    private int frameRun = 0;
    private TextureRegion[] runFrames;
    private static final float RUN_FRAME_DUR = 0.08f;

    // Idle/Reposo
    private boolean enReposo = true;
    private float tiempoIdle = 0f;
    private int frameIdle = 0;
    private TextureRegion[] idleFrames;
    private static final float IDLE_FRAME_DUR = 0.2f; // Más lento (0.2 segundos por frame)

    public PlayerActor(Jugador jugador, Texture idleTexture) {
        super(new TextureRegionDrawable(new TextureRegion(idleTexture)));
        this.jugador = jugador;
        this.idleRegion = new TextureRegion(idleTexture);
        setSize(32, 32);

        // Carga animaciones según clase
        if (jugador instanceof Caballero) {
            // Idle Caballero
            idleFrames = new TextureRegion[8];
            for (int i = 0; i < idleFrames.length; i++) {
                idleFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Caballero/Caballero_Idle/Idle_" + i + ".png")
                );
            }
            // Ataque Caballero
            attackFrames = new TextureRegion[9];
            for (int i = 0; i < attackFrames.length; i++) {
                attackFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Caballero/Caballero_Attack1/Attack1_" + i + ".png")
                );
            }
            // Corrida Caballero
            runFrames = new TextureRegion[8];
            for (int i = 0; i < runFrames.length; i++) {
                runFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Caballero/Caballero_Run/Run_" + i + ".png")
                );
            }
        } else if (jugador instanceof Mago) {
            // Idle Mago
            idleFrames = new TextureRegion[6];
            for (int i = 0; i < idleFrames.length; i++) {
                idleFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Mago_Idle/Idle_" + i + ".png")
                );
            }
            // Corrida Mago
            runFrames = new TextureRegion[8];
            for (int i = 0; i < runFrames.length; i++) {
                runFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Mago_Run/Run_" + i + ".png")
                );
            }
            attackFrames = null; // Mago sin animación de ataque
        } else if (jugador instanceof Arquero) {
            // Idle Arquero
            idleFrames = new TextureRegion[5];
            for (int i = 0; i < idleFrames.length; i++) {
                idleFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Arquero_Idle/Idle_" + i + ".png")
                );
            }
            // Corrida Arquero
            runFrames = new TextureRegion[8];
            for (int i = 0; i < runFrames.length; i++) {
                runFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Arquero_Run/Run_" + i + ".png")
                );
            }
            attackFrames = null; // Arquero maneja sus ataques de otra forma
        }
    }


    /**
     * Getter para acceder al objeto Jugador interno.
     */
    public Jugador getJugador() {
        return jugador;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        update(delta, null);
    }

    /**
     * Actualiza input, movimiento, ataque y animaciones.
     */
    public void update(float delta, List<? extends Enemigo> enemigos) {
        // Detectar movimiento horizontal (tanto flechas como A/D)
        float dirX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) dirX = -1;
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) dirX = 1;

        // Detectar movimiento vertical
        float dirY = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) dirY = 1;
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) dirY = -1;

        // Si no está atacando, procesa el movimiento
        if (!atacando) {
            // Mover al personaje en ambas direcciones si hay input
            if (dirX != 0 || dirY != 0) {
                jugador.mover(dirX, dirY, delta);

                // Animar corriendo si hay movimiento HORIZONTAL o si es MAGO o ARQUERO (para cualquier dirección)
                if ((dirX != 0 || (dirY != 0 && (jugador instanceof Mago || jugador instanceof Arquero))) && runFrames != null && runFrames.length > 0) {
                    corriendo = true;
                    enReposo = false;
                    tiempoCorrida += delta;
                    frameRun = (int)(tiempoCorrida / RUN_FRAME_DUR) % runFrames.length;
                } else {
                    // En movimiento vertical para no-Mago, mantener la dirección pero no animar corriendo
                    corriendo = false;
                    enReposo = true;
                    tiempoCorrida = 0;
                    frameRun = 0;
                }
            } else {
                // Sin ningún movimiento - activar animación Idle
                corriendo = false;
                enReposo = true;
                tiempoCorrida = 0;
                frameRun = 0;
            }

            // Actualizar animación de reposo (Idle) si corresponde
            if (enReposo && idleFrames != null && idleFrames.length > 0) {
                tiempoIdle += delta;
                frameIdle = (int)(tiempoIdle / IDLE_FRAME_DUR) % idleFrames.length;
            } else {
                // Si no está en reposo, reiniciar contador pero mantener el frame actual
                // para que al volver a idle la animación sea fluida
                tiempoIdle = 0;
            }
        } else {
            // Durante ataque no se muestra animación de corrida
            corriendo = false;
            tiempoCorrida = 0;
            frameRun = 0;
        }

        // Detectar ataque según el tipo de personaje
        if (!atacando && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (jugador instanceof Caballero caballero && caballero.puedeAtacar()) {
                caballero.registrarAtaque();
                atacando = true;
                tiempoAnimAtaque = 0f;
                frameAttack = 0;
                impactoHecho = false;
            } else if (jugador instanceof Mago mago) {
                // Mago no tiene animación de ataque por ahora, solo ejecuta la acción
                if (enemigos != null) {
                    mago.atacar(enemigos);
                }
            } else if (jugador instanceof Arquero arquero) {
                // El arquero tiene su propio manejo de ataques
                if (enemigos != null) {
                    arquero.atacar(enemigos);
                } else {
                    arquero.ataque1(); // Uso alternativo si no hay enemigos
                }
            }
        }

        // Avanzar animación de ataque si está atacando y tiene frames de ataque
        if (atacando && attackFrames != null && attackFrames.length > 0) {
            tiempoAnimAtaque += delta;
            int idx = (int)(tiempoAnimAtaque / ATTACK_FRAME_DUR);
            if (idx >= attackFrames.length) {
                atacando = false;
            } else {
                frameAttack = idx;
                if (idx == ATTACK_IMPACT && !impactoHecho && jugador instanceof Caballero caballero && enemigos != null) {
                    caballero.atacar(enemigos);
                    impactoHecho = true;
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        float x = getX(), y = getY(), w = getWidth(), h = getHeight();
        TextureRegion drawFrame = idleRegion;

        // Seleccionar el frame correcto según tipo de personaje y estado
        if (jugador instanceof Caballero) {
            if (atacando && attackFrames != null && frameAttack < attackFrames.length) {
                // Frame de animación de ataque para Caballero
                drawFrame = attackFrames[frameAttack];
            } else if (corriendo && runFrames != null && frameRun < runFrames.length) {
                // Frame de animación de carrera
                drawFrame = runFrames[frameRun];
            } else if (enReposo && idleFrames != null && frameIdle < idleFrames.length) {
                // Frame de animación idle para Caballero
                drawFrame = idleFrames[frameIdle];
            }
        } else if (jugador instanceof Mago) {
            if (corriendo && runFrames != null && frameRun < runFrames.length) {
                // Frame de animación de carrera para Mago
                drawFrame = runFrames[frameRun];
            } else if (enReposo && idleFrames != null && frameIdle < idleFrames.length) {
                // Frame de animación idle para Mago
                drawFrame = idleFrames[frameIdle];
            }
        } else if (jugador instanceof Arquero) {
            if (corriendo && runFrames != null && frameRun < runFrames.length) {
                // Frame de animación de carrera para Arquero
                drawFrame = runFrames[frameRun];
            } else if (enReposo && idleFrames != null && frameIdle < idleFrames.length) {
                // Frame de animación idle para Arquero
                drawFrame = idleFrames[frameIdle];
            }
        }

        // Verificar que tenemos un frame válido antes de intentar voltear
        if (drawFrame != null) {
            // Verificar si necesitamos voltear el sprite según dirección
            boolean debeEstarVolteado = "IZQUIERDA".equals(jugador.direccion);

            // Solo voltear si el estado actual no coincide con lo que debería ser
            if (drawFrame.isFlipX() != debeEstarVolteado) {
                drawFrame.flip(true, false); // Voltear horizontalmente
            }

            // Dibujar el frame actual con el tamaño correcto
            batch.draw(drawFrame, x, y, w, h);
        }
    }

    public void dibujarHUD(SpriteBatch batch, BitmapFont font) {
        float px = 20, py = Gdx.graphics.getHeight() - 20;
        font.draw(batch, "Clase: " + jugador.getClass().getSimpleName(), px, py);
        py -= 20;
        font.draw(batch, "Vida: " + jugador.getVida(), px, py);
        py -= 20;
        if (jugador instanceof Mago) {
            font.draw(batch, "Mana: " + ((Mago)jugador).getMana(), px, py);
        } else if (jugador instanceof Caballero) {
            font.draw(batch, "Escudo: " + ((Caballero)jugador).getEscudo(), px, py);
        } else if (jugador instanceof Arquero) {
            font.draw(batch, "Flechas: " + ((Arquero)jugador).getFlechas(), px, py);
        }
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Método para liberar los recursos cuando la pantalla se descarte.
     * Debe ser llamado explícitamente por el propietario de este actor.
     */
    public void dispose() {
        try {
            // Liberar texturas de animación de ataque
            if (attackFrames != null) {
                for (int i = 0; i < attackFrames.length; i++) {
                    if (attackFrames[i] != null && attackFrames[i].getTexture() != null) {
                        attackFrames[i].getTexture().dispose();
                    }
                }
            }
            // Liberar texturas de animación de carrera
            if (runFrames != null) {
                for (int i = 0; i < runFrames.length; i++) {
                    if (runFrames[i] != null && runFrames[i].getTexture() != null) {
                        runFrames[i].getTexture().dispose();
                    }
                }
            }
            // Liberar texturas de animación idle
            if (idleFrames != null) {
                for (int i = 0; i < idleFrames.length; i++) {
                    if (idleFrames[i] != null && idleFrames[i].getTexture() != null) {
                        idleFrames[i].getTexture().dispose();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al liberar recursos del PlayerActor: " + e.getMessage());
        }
    }
}
