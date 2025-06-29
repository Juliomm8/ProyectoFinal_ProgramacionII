package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Stage;
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

    // Animaciones específicas por clase
    private TextureRegion[] magoAttackFrames;    // Frames de ataque del Mago
    private TextureRegion[] arqueroAttackFrames; // Frames de ataque del Arquero

    // Proyectiles
    private TextureRegion[] hechizoFrames;       // Frames del hechizo volando
    private TextureRegion[] hechizoImpactoFrames; // Frames del impacto del hechizo
    private TextureRegion[] flechaFrames;        // Frames de la flecha volando
    private TextureRegion[] flechaImpactoFrames;  // Frames del impacto de la flecha

    // Stage para añadir proyectiles
    private Stage stage;  // Se inyectará desde fuera

    // Constantes para ataques
    private static final int MAGO_ATTACK_IMPACT = 3;    // Frame donde el Mago hace el impacto
    private static final int ARQUERO_ATTACK_IMPACT = 9; // Frame donde el Arquero dispara la flecha

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

            // Ataque Mago
            magoAttackFrames = new TextureRegion[7];
            for (int i = 0; i < magoAttackFrames.length; i++) {
                magoAttackFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Mago_Attack1/Attack1_" + i + ".png")
                );
            }

            // Frames de hechizo volando
            hechizoFrames = new TextureRegion[5];
            for (int i = 0; i < hechizoFrames.length; i++) {
                hechizoFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Hechizo/" + i + ".png")
                );
            }

            // Frames de impacto del hechizo (usamos los mismos para simplificar)
            hechizoImpactoFrames = new TextureRegion[5];
            for (int i = 0; i < hechizoImpactoFrames.length; i++) {
                hechizoImpactoFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Hechizo/" + i + ".png")
                );
            }

            // No usamos attackFrames para mago, sino su versión específica
            attackFrames = null;
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

            // Ataque Arquero (tensar arco)
            arqueroAttackFrames = new TextureRegion[11];
            for (int i = 0; i < arqueroAttackFrames.length; i++) {
                arqueroAttackFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Arquero_Attack1/Attack1_" + i + ".png")
                );
            }

            // Frames de flecha volando
            flechaFrames = new TextureRegion[3];
            for (int i = 0; i < flechaFrames.length; i++) {
                flechaFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Flecha/Flecha_Attack/" + i + ".png")
                );
            }

            // Frames de impacto de flecha
            flechaImpactoFrames = new TextureRegion[5];
            for (int i = 0; i < flechaImpactoFrames.length; i++) {
                flechaImpactoFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Flecha/Flecha_Hit/" + i + ".png")
                );
            }

            // No usamos attackFrames para arquero, sino su versión específica
            attackFrames = null;
        }
    }


    /**
     * Getter para acceder al objeto Jugador interno.
     */
    public Jugador getJugador() {
        return jugador;
    }

    /**
     * Establece el stage donde se añadirán los proyectiles.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Crea y añade un hechizo al stage desde la posición del mago.
     */
    private void generarHechizo() {
        if (stage == null || !(jugador instanceof Mago mago)) return;

        // Posición ligeramente adelantada según dirección
        float offsetX = "DERECHA".equals(jugador.direccion) ? getWidth() : 0;

        HechizoActor hechizo = new HechizoActor(
            hechizoFrames,
            hechizoImpactoFrames,
            getX() + offsetX,
            getY() + getHeight()/2 - 16, // Centrar verticalmente
            jugador.direccion,
            mago.getDanoBase()
        );

        stage.addActor(hechizo);
    }

    /**
     * Crea y añade una flecha al stage desde la posición del arquero.
     */
    private void generarFlecha() {
        if (stage == null || !(jugador instanceof Arquero arquero)) return;

        // Si no tiene flechas disponibles, no disparar
        if (arquero.getFlechas() <= 0) return;

        // Consumir una flecha
        arquero.ataque1();

        // Posición ligeramente adelantada según dirección
        float offsetX = "DERECHA".equals(jugador.direccion) ? getWidth() : 0;

        FlechaActor flecha = new FlechaActor(
            flechaFrames,
            flechaImpactoFrames,
            getX() + offsetX,
            getY() + getHeight()/2 - 16, // Centrar verticalmente
            jugador.direccion,
            arquero.getDanoBase()
        );

        stage.addActor(flecha);
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

        // Detectar ataque según el tipo de personaje (SPACE o clic izquierdo)
        boolean ataqueDetectado = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
                                 Gdx.input.justTouched();

        if (!atacando && ataqueDetectado) {
            if (jugador instanceof Caballero caballero && caballero.puedeAtacar()) {
                caballero.registrarAtaque();
                atacando = true;
                tiempoAnimAtaque = 0f;
                frameAttack = 0;
                impactoHecho = false;
            } else if (jugador instanceof Mago mago) {
                // Iniciar animación de ataque del mago
                atacando = true;
                tiempoAnimAtaque = 0f;
                frameAttack = 0;
                impactoHecho = false;
            } else if (jugador instanceof Arquero arquero) {
                // Iniciar animación de ataque del arquero si tiene flechas
                if (arquero.getFlechas() > 0) {
                    atacando = true;
                    tiempoAnimAtaque = 0f;
                    frameAttack = 0;
                    impactoHecho = false;
                }
            }
        }

        // Avanzar animación de ataque según la clase de personaje
        if (atacando) {
            tiempoAnimAtaque += delta;
            int idx = (int)(tiempoAnimAtaque / ATTACK_FRAME_DUR);

            if (jugador instanceof Caballero && attackFrames != null) {
                // Lógica para Caballero (usando attackFrames)
                if (idx >= attackFrames.length) {
                    atacando = false;
                } else {
                    frameAttack = idx;
                    if (idx == ATTACK_IMPACT && !impactoHecho && enemigos != null) {
                        ((Caballero)jugador).atacar(enemigos);
                        impactoHecho = true;
                    }
                }
            } else if (jugador instanceof Mago && magoAttackFrames != null) {
                // Lógica para Mago
                if (idx >= magoAttackFrames.length) {
                    atacando = false;
                    // Al terminar la animación, generar el hechizo
                    generarHechizo();
                } else {
                    frameAttack = idx;
                    if (idx == MAGO_ATTACK_IMPACT && !impactoHecho && enemigos != null) {
                        ((Mago)jugador).atacar(enemigos);
                        impactoHecho = true;
                    }
                }
            } else if (jugador instanceof Arquero && arqueroAttackFrames != null) {
                // Lógica para Arquero
                if (idx >= arqueroAttackFrames.length) {
                    atacando = false;
                    // Al terminar la animación, generar la flecha
                    generarFlecha();
                } else {
                    frameAttack = idx;
                    if (idx == ARQUERO_ATTACK_IMPACT && !impactoHecho) {
                        impactoHecho = true;
                    }
                }
            } else {
                // Si no tiene frames de ataque, terminar rápido
                atacando = false;
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
            if (atacando && magoAttackFrames != null && frameAttack < magoAttackFrames.length) {
                // Frame de animación de ataque para Mago
                drawFrame = magoAttackFrames[frameAttack];
            } else if (corriendo && runFrames != null && frameRun < runFrames.length) {
                // Frame de animación de carrera para Mago
                drawFrame = runFrames[frameRun];
            } else if (enReposo && idleFrames != null && frameIdle < idleFrames.length) {
                // Frame de animación idle para Mago
                drawFrame = idleFrames[frameIdle];
            }
        } else if (jugador instanceof Arquero) {
            if (atacando && arqueroAttackFrames != null && frameAttack < arqueroAttackFrames.length) {
                // Frame de animación de ataque para Arquero
                drawFrame = arqueroAttackFrames[frameAttack];
            } else if (corriendo && runFrames != null && frameRun < runFrames.length) {
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

            // Liberar texturas de animación de ataque del mago
            if (magoAttackFrames != null) {
                for (int i = 0; i < magoAttackFrames.length; i++) {
                    if (magoAttackFrames[i] != null && magoAttackFrames[i].getTexture() != null) {
                        magoAttackFrames[i].getTexture().dispose();
                    }
                }
            }

            // Liberar texturas de animación de ataque del arquero
            if (arqueroAttackFrames != null) {
                for (int i = 0; i < arqueroAttackFrames.length; i++) {
                    if (arqueroAttackFrames[i] != null && arqueroAttackFrames[i].getTexture() != null) {
                        arqueroAttackFrames[i].getTexture().dispose();
                    }
                }
            }

            // Liberar texturas de hechizo
            if (hechizoFrames != null) {
                for (int i = 0; i < hechizoFrames.length; i++) {
                    if (hechizoFrames[i] != null && hechizoFrames[i].getTexture() != null) {
                        hechizoFrames[i].getTexture().dispose();
                    }
                }
            }

            // Liberar texturas de impacto de hechizo
            if (hechizoImpactoFrames != null) {
                for (int i = 0; i < hechizoImpactoFrames.length; i++) {
                    if (hechizoImpactoFrames[i] != null && hechizoImpactoFrames[i].getTexture() != null) {
                        hechizoImpactoFrames[i].getTexture().dispose();
                    }
                }
            }

            // Liberar texturas de flecha
            if (flechaFrames != null) {
                for (int i = 0; i < flechaFrames.length; i++) {
                    if (flechaFrames[i] != null && flechaFrames[i].getTexture() != null) {
                        flechaFrames[i].getTexture().dispose();
                    }
                }
            }

            // Liberar texturas de impacto de flecha
            if (flechaImpactoFrames != null) {
                for (int i = 0; i < flechaImpactoFrames.length; i++) {
                    if (flechaImpactoFrames[i] != null && flechaImpactoFrames[i].getTexture() != null) {
                        flechaImpactoFrames[i].getTexture().dispose();
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error al liberar recursos del PlayerActor: " + e.getMessage());
        }
    }
}
