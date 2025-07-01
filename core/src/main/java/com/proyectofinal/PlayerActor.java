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
import com.badlogic.gdx.utils.viewport.Viewport;

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

    // Tipo de ataque activo
    private enum TipoAtaque { NORMAL, ESPECIAL }
    private TipoAtaque tipoAtaqueActual = TipoAtaque.NORMAL;

    // Animaciones específicas por clase
    private TextureRegion[] magoAttack1Frames;    // Frames de ataque básico del Mago
    private TextureRegion[] magoAttack2Frames;    // Frames de ataque especial del Mago
    private TextureRegion[] arqueroAttackFrames;  // Frames de ataque del Arquero

    // Proyectiles
    private TextureRegion[] hechizoFrames;        // Frames del hechizo volando
    private TextureRegion[] hechizoImpactoFrames; // Frames del impacto del hechizo
    private TextureRegion[] flechaFrames;         // Frames de la flecha volando
    private TextureRegion[] flechaImpactoFrames;  // Frames del impacto de la flecha

    // Stage para añadir proyectiles
    private Stage stage;  // Se inyectará desde fuera

    // Viewport para determinar límites de la pantalla
    private Viewport viewport; // Se inyectará desde fuera

    // Constantes para ataques
    private static final int MAGO_ATTACK1_IMPACT = 3;   // Frame donde el Mago hace el impacto básico
    private static final int MAGO_ATTACK2_IMPACT = 4;   // Frame donde el Mago hace el impacto especial
    private static final int ARQUERO_ATTACK_IMPACT = 9; // Frame donde el Arquero dispara la flecha
    private static final int COSTO_HECHIZO_ESPECIAL = 20; // Costo de mana del hechizo especial

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

    /**
     * Dirección actual para la que están orientados los frames
     * Usada para detectar cambios de dirección y voltear frames solo cuando sea necesario
     */

    private String direccionActualFrames = "DERECHA"; // Por defecto, los sprites miran a la derecha

    public PlayerActor(Jugador jugador, Texture idleTexture) {
        super(new TextureRegionDrawable(new TextureRegion(idleTexture)));
        this.jugador = jugador;
        this.idleRegion = new TextureRegion(idleTexture);
        setSize(32, 32);

        // Inicializar con la dirección del jugador
        direccionActualFrames = jugador.direccion;

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

            // Ataque básico Mago (Attack1)
            magoAttack1Frames = new TextureRegion[7];
            for (int i = 0; i < magoAttack1Frames.length; i++) {
                magoAttack1Frames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Mago_Attack1/Attack1_" + i + ".png")
                );
            }

            // Ataque especial Mago (Attack2)
            magoAttack2Frames = new TextureRegion[8];
            for (int i = 0; i < magoAttack2Frames.length; i++) {
                magoAttack2Frames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Mago_Attack2/Attack2_" + i + ".png")
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

            // No usamos attackFrames para mago, sino sus versiones específicas
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
     * Establece el viewport para determinar límites de la pantalla.
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    /**
     * Crea y añade un hechizo al stage desde la posición del mago.
     * Distingue automáticamente entre hechizo básico y especial según tipoAtaqueActual.
     */
    private void generarHechizo() {
        if (stage == null || !(jugador instanceof Mago mago)) return;

        // Posición ligeramente adelantada según dirección
        float offsetX = "DERECHA".equals(jugador.direccion) ? getWidth() : -16; // Ajuste para izquierda

        // Clonamos los frames para evitar problemas de orientación compartida
        TextureRegion[] hechizoFramesClone = new TextureRegion[hechizoFrames.length];
        TextureRegion[] hechizoImpactoFramesClone = new TextureRegion[hechizoImpactoFrames.length];

        for (int i = 0; i < hechizoFrames.length; i++) {
            hechizoFramesClone[i] = new TextureRegion(hechizoFrames[i]);
        }

        for (int i = 0; i < hechizoImpactoFrames.length; i++) {
            hechizoImpactoFramesClone[i] = new TextureRegion(hechizoImpactoFrames[i]);
        }

        // Determinar parámetros según tipo de ataque
        boolean esAtaqueEspecial = (tipoAtaqueActual == TipoAtaque.ESPECIAL);

        // Configuración del hechizo según tipo
        float escala = esAtaqueEspecial ? 1.5f : 1.0f;     // Hechizo más grande para ataque especial
        float velocidad = esAtaqueEspecial ? 500f : 400f;  // Mayor velocidad para hechizo especial
        boolean atraviesa = esAtaqueEspecial;              // Solo el especial atraviesa enemigos
        int danoHechizo = mago.getDanoBase() * (esAtaqueEspecial ? 2 : 1) + 20;

        // Crear el hechizo con las propiedades determinadas
        HechizoActor hechizo = new HechizoActor(
            hechizoFramesClone,
            hechizoImpactoFramesClone,
            getX() + offsetX,
            getY() + getHeight()/2 - 8, // Mejorado centrado vertical
            jugador.direccion,
            danoHechizo,
            velocidad,
            escala,
            50f, // Radio de efecto del hechizo
            atraviesa // Si atraviesa enemigos o no
        );

        // Configurar viewport si está disponible
        if (viewport != null) {
            hechizo.setViewport(viewport);
        }

        // Añadir el hechizo asegurando que esté por delante del personaje en Z
        hechizo.setZIndex(1000); // Asegurar que se dibuje por encima
        stage.addActor(hechizo);

        // Mostrar información sobre el tipo de hechizo lanzado
        if (esAtaqueEspecial) {
            System.out.println("Lanzado hechizo ESPECIAL - Escala: " + escala + ", Daño: " + danoHechizo + ", Atraviesa: " + atraviesa);
        } else {
            System.out.println("Lanzado hechizo básico - Escala: " + escala + ", Daño: " + danoHechizo);
        }
    }

    /**
     * Crea y añade una flecha al stage desde la posición del arquero.
     * La flecha se creará con un tamaño adecuado y velocidad alta.
     */
    private void generarFlecha() {
        if (stage == null || !(jugador instanceof Arquero arquero)) return;

        // Si no tiene flechas disponibles, no disparar
        if (arquero.getFlechas() <= 0) return;


        // Posición ligeramente adelantada según dirección
        float offsetX = "DERECHA".equals(jugador.direccion) ? getWidth() : -16;

        // Clonamos los frames para evitar problemas de orientación compartida
        TextureRegion[] flechaFramesClone = new TextureRegion[flechaFrames.length];
        TextureRegion[] flechaImpactoFramesClone = new TextureRegion[flechaImpactoFrames.length];

        for (int i = 0; i < flechaFrames.length; i++) {
            flechaFramesClone[i] = new TextureRegion(flechaFrames[i]);
        }

        for (int i = 0; i < flechaImpactoFrames.length; i++) {
            flechaImpactoFramesClone[i] = new TextureRegion(flechaImpactoFrames[i]);
        }

        // Velocidad aumentada para que el proyectil sea más rápido
        float velocidadFlecha = 600f; // Velocidad duplicada para mayor alcance

        FlechaActor flecha = new FlechaActor(
            flechaFramesClone,
            flechaImpactoFramesClone,
            getX() + offsetX,
            getY() + getHeight()/2 - 8, // Mejorado centrado vertical
            jugador.direccion,
            9999, // Daño que mata de un solo golpe
            velocidadFlecha,
            0.5f    // Tamaño reducido al 50%
        );

        // Configurar viewport si está disponible
        if (viewport != null) {
            flecha.setViewport(viewport);
        }

        // Añadir la flecha asegurando que esté por delante del personaje en Z
        flecha.setZIndex(1000); // Asegurar que se dibuje por encima
        stage.addActor(flecha);

        System.out.println("Flecha generada con daño letal");
    }

    /**
     * Método para generar un hechizo básico desde la posición del mago
     */
    private void generarHechizoBasico() {
        if (stage == null || !(jugador instanceof Mago mago)) return;

        // Posición ligeramente adelantada según dirección
        float offsetX = "DERECHA".equals(jugador.direccion) ? getWidth() : -16;

        // Clonar frames para evitar problemas de orientación
        TextureRegion[] hechizoFramesClone = new TextureRegion[hechizoFrames.length];
        TextureRegion[] hechizoImpactoFramesClone = new TextureRegion[hechizoImpactoFrames.length];

        for (int i = 0; i < hechizoFrames.length; i++) {
            hechizoFramesClone[i] = new TextureRegion(hechizoFrames[i]);
        }

        for (int i = 0; i < hechizoImpactoFrames.length; i++) {
            hechizoImpactoFramesClone[i] = new TextureRegion(hechizoImpactoFrames[i]);
        }

        // Crear hechizo básico (no atraviesa enemigos)
        HechizoActor hechizo = new HechizoActor(
            hechizoFramesClone,
            hechizoImpactoFramesClone,
            getX() + offsetX,
            getY() + getHeight()/2 - 8,
            jugador.direccion,
            9999, // Daño que mata de un solo golpe
            500f, // Velocidad
            0.5f, // Escala
            false // No atraviesa enemigos
        );

        if (viewport != null) {
            hechizo.setViewport(viewport);
        }

        hechizo.setZIndex(1000);
        stage.addActor(hechizo);

        System.out.println("Hechizo básico generado con daño letal");
    }

    /**
     * Lista de enemigos en la pantalla actual, necesaria para procesar ataques correctamente.
     * Debe ser configurada por la pantalla principal del juego.
     */
    private List<? extends Enemigo> enemigosActuales;

    @Override
    public void act(float delta) {
        super.act(delta);
        // Usamos la lista de enemigos configurada en lugar de null
        update(delta, enemigosActuales);
    }

    /**
     * Actualiza input, movimiento, ataque y animaciones.
     */
    /**
     * Voltea todos los frames de animación cuando el jugador cambia de dirección
     * Esto evita tener que voltear cada frame individualmente en cada ciclo de renderizado
     */
    private void actualizarDireccionFrames() {
        // Si la dirección no ha cambiado, no hacer nada
        if (direccionActualFrames.equals(jugador.direccion)) return;

        // La dirección ha cambiado, volteamos todos los frames
        boolean debeEstarVolteado = "IZQUIERDA".equals(jugador.direccion);

        // Actualizar frames según tipo de personaje
        if (jugador instanceof Caballero) {
            voltearFrames(idleFrames, debeEstarVolteado);
            voltearFrames(runFrames, debeEstarVolteado);
            voltearFrames(attackFrames, debeEstarVolteado);
        } else if (jugador instanceof Mago) {
            voltearFrames(idleFrames, debeEstarVolteado);
            voltearFrames(runFrames, debeEstarVolteado);
            voltearFrames(magoAttack1Frames, debeEstarVolteado);
            voltearFrames(magoAttack2Frames, debeEstarVolteado);
        } else if (jugador instanceof Arquero) {
            voltearFrames(idleFrames, debeEstarVolteado);
            voltearFrames(runFrames, debeEstarVolteado);
            voltearFrames(arqueroAttackFrames, debeEstarVolteado);
        }

        // Actualizar dirección actual de los frames
        direccionActualFrames = jugador.direccion;
        System.out.println("Dirección de frames cambiada a: " + direccionActualFrames);
    }

    /**
     * Voltea horizontalmente un array de frames si es necesario
     */
    private void voltearFrames(TextureRegion[] frames, boolean debeEstarVolteado) {
        if (frames == null) return;

        for (TextureRegion frame : frames) {
            if (frame != null && frame.isFlipX() != debeEstarVolteado) {
                frame.flip(true, false); // Voltear horizontalmente
            }
        }
    }

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

                // Verificar si cambió la dirección y actualizar frames si es necesario
                if (!direccionActualFrames.equals(jugador.direccion)) {
                    actualizarDireccionFrames();
                }

                // Animar corriendo si hay cualquier movimiento (horizontal o vertical) para todos los personajes
                if ((dirX != 0 || dirY != 0) && runFrames != null && runFrames.length > 0) {
                    // Transición a estado de corriendo
                    if (!corriendo) {
                        // Solo si es una transición, reiniciamos contadores
                        corriendo = true;
                        enReposo = false;
                        tiempoCorrida = 0f;
                        frameRun = 0;
                        System.out.println("Transición: Reposo -> Corriendo");
                    } else {
                        // Ya estaba corriendo, seguimos la animación
                        tiempoCorrida += delta;
                        frameRun = (int)(tiempoCorrida / RUN_FRAME_DUR) % runFrames.length;
                    }
                } else {
                    // Transición a estado de reposo
                    if (!enReposo) {
                        // Solo si es una transición, reiniciamos contadores
                        corriendo = false;
                        enReposo = true;
                        tiempoCorrida = 0f;
                        frameRun = 0;
                        tiempoIdle = 0f;
                        frameIdle = 0;
                        System.out.println("Transición: Corriendo -> Reposo");
                    }
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

        // Detectar ataques según tipo de personaje y botón
        boolean ataqueIzquierdoDetectado = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT));
        boolean ataqueDerechoDetectado = Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && Gdx.input.justTouched();

        // Solo procesar ataques si no está ya atacando
        if (!atacando) {
            if (ataqueIzquierdoDetectado) {
                // Ataque básico (botón izquierdo o SPACE)
                if (jugador instanceof Caballero caballero && caballero.puedeAtacar()) {
                    caballero.registrarAtaque();

                    // Transición a estado de ataque
                    if (!atacando) {
                        System.out.println("Transición: " + (corriendo ? "Corriendo" : "Reposo") + " -> Atacando (Caballero)");
                        // Guardar estado anterior para restaurarlo después del ataque
                        boolean estabaEnReposo = enReposo;
                        boolean estabaCorriendo = corriendo;

                        // Configurar estado de ataque
                        atacando = true;
                        corriendo = false;
                        enReposo = false;
                        tiempoAnimAtaque = 0f;
                        frameAttack = 0;
                        impactoHecho = false;
                        tipoAtaqueActual = TipoAtaque.NORMAL;
                    }
                } else if (jugador instanceof Mago) {
                    // Ataque básico del mago (no consume mana)
                    atacando = true;
                    tiempoAnimAtaque = 0f;
                    frameAttack = 0;
                    impactoHecho = false;
                    tipoAtaqueActual = TipoAtaque.NORMAL;
                } else if (jugador instanceof Arquero arquero) {
                    // Ataque del arquero, comprobando flechas
                    if (arquero.getFlechas() > 0) {
                        atacando = true;
                        tiempoAnimAtaque = 0f;
                        frameAttack = 0;
                        impactoHecho = false;
                        tipoAtaqueActual = TipoAtaque.NORMAL;
                    }
                }
            } else if (ataqueDerechoDetectado && jugador instanceof Mago mago) {
                // Ataque especial del mago (botón derecho)
                if (mago.getMana() >= COSTO_HECHIZO_ESPECIAL) {
                    atacando = true;
                    tiempoAnimAtaque = 0f;
                    frameAttack = 0;
                    impactoHecho = false;
                    tipoAtaqueActual = TipoAtaque.ESPECIAL;
                    mago.consumirMana(COSTO_HECHIZO_ESPECIAL);
                    System.out.println("Mago lanza hechizo especial. Mana restante: " + mago.getMana());
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
                    if (idx == ATTACK_IMPACT && !impactoHecho) {
                        // Verificar que tenemos enemigos para evitar NPE
                        if (enemigos != null && !enemigos.isEmpty()) {
                            ((Caballero)jugador).atacar(enemigos);
                        } else {
                            // Si no hay enemigos, solo registramos el ataque sin procesarlo
                            System.out.println("Caballero ataca pero no hay enemigos cercanos");
                        }
                        impactoHecho = true;
                    }
                }
            } else if (jugador instanceof Mago) {
                // Lógica para Mago según tipo de ataque
                if (tipoAtaqueActual == TipoAtaque.NORMAL && magoAttack1Frames != null) {
                    // Ataque básico del mago
                    if (idx >= magoAttack1Frames.length) {
                        // IMPORTANTE: Primero generar el hechizo y luego marcar como no atacando
                        // para evitar que la animación se quede trabada
                        generarHechizo(); // Generará hechizo básico porque tipoAtaqueActual es NORMAL
                        atacando = false;
                        impactoHecho = false;
                        tiempoAnimAtaque = 0;
                    } else {
                        frameAttack = idx;
                        if (idx == MAGO_ATTACK1_IMPACT && !impactoHecho) {
                            impactoHecho = true;
                        }
                    }
                } else if (tipoAtaqueActual == TipoAtaque.ESPECIAL && magoAttack2Frames != null) {
                    // Ataque especial del mago
                    if (idx >= magoAttack2Frames.length) {
                        // IMPORTANTE: Primero generar el hechizo especial y luego marcar como no atacando
                        generarHechizo(); // Generará hechizo especial porque tipoAtaqueActual es ESPECIAL
                        atacando = false;
                        impactoHecho = false;
                        tiempoAnimAtaque = 0;
                    } else {
                        frameAttack = idx;
                        if (idx == MAGO_ATTACK2_IMPACT && !impactoHecho) {
                            impactoHecho = true;
                        }
                    }
                } else {
                    // Por seguridad, generamos el hechizo según el tipo de ataque actual
                    // en caso de que algo fallara y la animación se quedara trabada
                    generarHechizo(); // Utilizará el tipo de ataque actual (NORMAL o ESPECIAL)
                    atacando = false;
                    impactoHecho = false;
                    tiempoAnimAtaque = 0;
                }
            } else if (jugador instanceof Arquero && arqueroAttackFrames != null) {
                // Lógica para Arquero
                if (idx >= arqueroAttackFrames.length) {
                    // IMPORTANTE: Primero verificar si el arquero puede disparar
                    boolean pudoDisparar = false;

                    // Llamar a atacar para consumir flecha
                    if (enemigos != null) {
                        pudoDisparar = ((Arquero)jugador).atacar(enemigos);
                    } else {
                        // Modo alternativo si no hay enemigos
                        if (((Arquero)jugador).getFlechas() > 0) {
                            ((Arquero)jugador).ataque1();
                            pudoDisparar = true;
                        }
                    }

                    // Solo generar la flecha si pudo disparar
                    if (pudoDisparar) {
                        // Generar la flecha visualmente
                        generarFlecha();
                        System.out.println("Flecha generada exitosamente");
                    } else {
                        System.out.println("No se pudo generar la flecha - sin munición");
                    }

                    // Resetear estado de ataque
                    atacando = false;
                    impactoHecho = false;
                    tiempoAnimAtaque = 0;
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
            if (atacando) {
                // Seleccionar el frame de ataque según tipo
                if (tipoAtaqueActual == TipoAtaque.NORMAL &&
                    magoAttack1Frames != null && frameAttack < magoAttack1Frames.length) {
                    // Frame de animación de ataque básico para Mago
                    drawFrame = magoAttack1Frames[frameAttack];
                } else if (tipoAtaqueActual == TipoAtaque.ESPECIAL &&
                    magoAttack2Frames != null && frameAttack < magoAttack2Frames.length) {
                    // Frame de animación de ataque especial para Mago
                    drawFrame = magoAttack2Frames[frameAttack];
                }
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

        // Dibujar el frame actual si es válido
        // Ya no necesitamos voltear aquí pues se hace en actualizarDireccionFrames()
        if (drawFrame != null) {
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
     * Sobrescribe el método remove() de Actor para asegurar que se limpien recursos.
     * @return true si el actor fue removido correctamente
     */
    @Override
    public boolean remove() {
        // Liberar recursos antes de remover
        dispose();
        return super.remove();
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

            // Liberar texturas de animación de ataque básico del mago
            if (magoAttack1Frames != null) {
                for (int i = 0; i < magoAttack1Frames.length; i++) {
                    if (magoAttack1Frames[i] != null && magoAttack1Frames[i].getTexture() != null) {
                        magoAttack1Frames[i].getTexture().dispose();
                    }
                }
            }

            // Liberar texturas de animación de ataque especial del mago
            if (magoAttack2Frames != null) {
                for (int i = 0; i < magoAttack2Frames.length; i++) {
                    if (magoAttack2Frames[i] != null && magoAttack2Frames[i].getTexture() != null) {
                        magoAttack2Frames[i].getTexture().dispose();
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
