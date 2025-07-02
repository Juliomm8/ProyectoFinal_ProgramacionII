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
 * Actor que maneja la logica y dibujo del jugador,
 * con animaciones de idle, corrida y ataque.
 */
public class PlayerActor extends Image {
    private final Jugador jugador;
    private final TextureRegion idleRegion;

    // Estado de ataque actual
    private boolean atacando = false;
    private boolean impactoHecho = false;
    private float tiempoAnimAtaque = 0f;
    private int frameAttack = 0;
    private TextureRegion[] attackFrames;
    private static final float ATTACK_FRAME_DUR = 0.07f;
    private static final int ATTACK_IMPACT = 4;

    // Tipo de ataque (se usa para distinguir entre ataque normal y especial en el Mago)
    private enum TipoAtaque { NORMAL, ESPECIAL }
    private TipoAtaque tipoAtaqueActual = TipoAtaque.NORMAL;

    // Animaciones especificas por clase
    private TextureRegion[] magoAttack1Frames;
    private TextureRegion[] magoAttack2Frames;
    private TextureRegion[] arqueroAttackFrames;

    // Proyectiles
    private TextureRegion[] hechizoFrames;
    private TextureRegion[] hechizoImpactoFrames;
    private TextureRegion[] flechaFrames;
    private TextureRegion[] flechaImpactoFrames;

    // Stage para añadir proyectiles (se inyecta desde fuera)
    private Stage stage;

    // Viewport para verificar limites del mundo (se inyecta desde fuera)
    private Viewport viewport;

    // Frame de impacto por clase
    private static final int MAGO_ATTACK1_IMPACT = 3;
    private static final int MAGO_ATTACK2_IMPACT = 4;
    private static final int ARQUERO_ATTACK_IMPACT = 9;
    private static final int COSTO_HECHIZO_ESPECIAL = 20;

    // Animacion de corrida
    private boolean corriendo = false;
    private float tiempoCorrida = 0f;
    private int frameRun = 0;
    private TextureRegion[] runFrames;
    private static final float RUN_FRAME_DUR = 0.08f;

    // Animacion de reposo (idle)
    private boolean enReposo = true;
    private float tiempoIdle = 0f;
    private int frameIdle = 0;
    private TextureRegion[] idleFrames;
    private static final float IDLE_FRAME_DUR = 0.2f;

    /**
     * Direccion actual del sprite ("DERECHA" o "IZQUIERDA")
     * Se usa para saber si se necesita voltear el frame.
     */
    private String direccionActualFrames = "DERECHA";

    /**
     * Constructor: inicializa el actor con texturas segun la clase del jugador
     */
    public PlayerActor(Jugador jugador, Texture idleTexture) {
        super(new TextureRegionDrawable(new TextureRegion(idleTexture)));
        this.jugador = jugador;
        this.idleRegion = new TextureRegion(idleTexture);
        setSize(32, 32);  // tamaño inicial del actor

        direccionActualFrames = jugador.direccion;

        // Configura animaciones segun el tipo de jugador
        if (jugador instanceof Caballero) {
            // Idle del Caballero
            idleFrames = new TextureRegion[8];
            for (int i = 0; i < idleFrames.length; i++) {
                idleFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Caballero/Caballero_Idle/Idle_" + i + ".png")
                );
            }
            // Ataque del Caballero
            attackFrames = new TextureRegion[9];
            for (int i = 0; i < attackFrames.length; i++) {
                attackFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Caballero/Caballero_Attack1/Attack1_" + i + ".png")
                );
            }
            // Corrida del Caballero
            runFrames = new TextureRegion[8];
            for (int i = 0; i < runFrames.length; i++) {
                runFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Caballero/Caballero_Run/Run_" + i + ".png")
                );
            }
        } else if (jugador instanceof Mago) {
            // Idle del Mago
            idleFrames = new TextureRegion[6];
            for (int i = 0; i < idleFrames.length; i++) {
                idleFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Mago_Idle/Idle_" + i + ".png")
                );
            }
            // Corrida del Mago
            runFrames = new TextureRegion[8];
            for (int i = 0; i < runFrames.length; i++) {
                runFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Mago_Run/Run_" + i + ".png")
                );
            }
            // Ataque 1 del Mago
            magoAttack1Frames = new TextureRegion[7];
            for (int i = 0; i < magoAttack1Frames.length; i++) {
                magoAttack1Frames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Mago_Attack1/Attack1_" + i + ".png")
                );
            }
            // Ataque especial del Mago
            magoAttack2Frames = new TextureRegion[8];
            for (int i = 0; i < magoAttack2Frames.length; i++) {
                magoAttack2Frames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Mago_Attack2/Attack2_" + i + ".png")
                );
            }
            // Proyectil de hechizo
            hechizoFrames = new TextureRegion[5];
            for (int i = 0; i < hechizoFrames.length; i++) {
                hechizoFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Hechizo/" + i + ".png")
                );
            }
            // Impacto de hechizo (mismo sprite por simplicidad)
            hechizoImpactoFrames = new TextureRegion[5];
            for (int i = 0; i < hechizoImpactoFrames.length; i++) {
                hechizoImpactoFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Mago/Hechizo/" + i + ".png")
                );
            }
            attackFrames = null;  // No se usa en el mago

        } else if (jugador instanceof Arquero) {
            // Idle del Arquero
            idleFrames = new TextureRegion[5];
            for (int i = 0; i < idleFrames.length; i++) {
                idleFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Arquero_Idle/Idle_" + i + ".png")
                );
            }
            // Corrida del Arquero
            runFrames = new TextureRegion[8];
            for (int i = 0; i < runFrames.length; i++) {
                runFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Arquero_Run/Run_" + i + ".png")
                );
            }
            // Ataque del Arquero
            arqueroAttackFrames = new TextureRegion[11];
            for (int i = 0; i < arqueroAttackFrames.length; i++) {
                arqueroAttackFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Arquero_Attack1/Attack1_" + i + ".png")
                );
            }
            // Proyectil de flecha
            flechaFrames = new TextureRegion[3];
            for (int i = 0; i < flechaFrames.length; i++) {
                flechaFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Flecha/Flecha_Attack/" + i + ".png")
                );
            }
            // Impacto de flecha
            flechaImpactoFrames = new TextureRegion[5];
            for (int i = 0; i < flechaImpactoFrames.length; i++) {
                flechaImpactoFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Arquero/Flecha/Flecha_Hit/" + i + ".png")
                );
            }
            attackFrames = null; // No se usa en arquero
        }
    }

    /**
     * Getter para obtener el objeto Jugador asociado a este actor.
     */
    public Jugador getJugador() {
        return jugador;
    }

    /**
     * Permite establecer el Stage donde se van a añadir los proyectiles.
     * Esto es necesario para que los hechizos y flechas se dibujen y actualicen correctamente.
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Establece el viewport actual, que se usará para restringir el movimiento
     * de proyectiles dentro de los limites de la pantalla.
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }

    /**
     * Crea un hechizo (proyectil) desde la posicion actual del jugador Mago.
     * Dependiendo del tipo de ataque (normal o especial), cambia las propiedades del hechizo:
     * escala, velocidad, si atraviesa enemigos, y el danio causado.
     */
    private void generarHechizo() {
        if (stage == null || !(jugador instanceof Mago mago)) return;

        // Desplazamiento horizontal del hechizo segun la direccion del jugador
        float offsetX = "DERECHA".equals(jugador.direccion) ? getWidth() : -16;

        // Clonamos los frames del hechizo para evitar problemas si se usan en varias instancias
        TextureRegion[] hechizoFramesClone = new TextureRegion[hechizoFrames.length];
        TextureRegion[] hechizoImpactoFramesClone = new TextureRegion[hechizoImpactoFrames.length];
        for (int i = 0; i < hechizoFrames.length; i++) {
            hechizoFramesClone[i] = new TextureRegion(hechizoFrames[i]);
        }
        for (int i = 0; i < hechizoImpactoFrames.length; i++) {
            hechizoImpactoFramesClone[i] = new TextureRegion(hechizoImpactoFrames[i]);
        }

        // Verificamos si es un ataque especial para ajustar sus parametros
        boolean esAtaqueEspecial = (tipoAtaqueActual == TipoAtaque.ESPECIAL);
        float escala = esAtaqueEspecial ? 1.5f : 1.0f;
        float velocidad = esAtaqueEspecial ? 500f : 400f;
        boolean atraviesa = esAtaqueEspecial;
        int danoHechizo = mago.getDanoBase() * (esAtaqueEspecial ? 2 : 1) + 20;

        // Instanciamos el objeto Hechizo con todos sus parametros
        HechizoActor hechizo = new HechizoActor(
            hechizoFramesClone,
            hechizoImpactoFramesClone,
            getX() + offsetX,
            getY() + getHeight()/2 - 8,
            jugador.direccion,
            danoHechizo,
            velocidad,
            escala,
            50f,
            atraviesa
        );

        if (viewport != null) hechizo.setViewport(viewport);

        // Se asegura que el hechizo se dibuje por encima del jugador
        hechizo.setZIndex(1000);
        stage.addActor(hechizo);

        if (esAtaqueEspecial) {
            System.out.println("Lanzado hechizo ESPECIAL - Escala: " + escala + ", Da\u00f1o: " + danoHechizo + ", Atraviesa: " + atraviesa);
        } else {
            System.out.println("Lanzado hechizo basico - Escala: " + escala + ", Da\u00f1o: " + danoHechizo);
        }
    }

    /**
     * Crea una flecha (proyectil) desde la posicion actual del Arquero.
     * Si no tiene flechas disponibles, no se genera nada.
     * La flecha tiene velocidad alta, danio letal, y tamanio reducido.
     */
    private void generarFlecha() {
        if (stage == null || !(jugador instanceof Arquero arquero)) return;

        // Verifica que el arquero tenga flechas
        if (arquero.getFlechas() <= 0) return;

        // Desplazamiento horizontal de la flecha
        float offsetX = "DERECHA".equals(jugador.direccion) ? getWidth() : -16;

        // Clon de los frames de la flecha para evitar que compartan referencias
        TextureRegion[] flechaFramesClone = new TextureRegion[flechaFrames.length];
        TextureRegion[] flechaImpactoFramesClone = new TextureRegion[flechaImpactoFrames.length];
        for (int i = 0; i < flechaFrames.length; i++) {
            flechaFramesClone[i] = new TextureRegion(flechaFrames[i]);
        }
        for (int i = 0; i < flechaImpactoFrames.length; i++) {
            flechaImpactoFramesClone[i] = new TextureRegion(flechaImpactoFrames[i]);
        }

        float velocidadFlecha = 600f;

        FlechaActor flecha = new FlechaActor(
            flechaFramesClone,
            flechaImpactoFramesClone,
            getX() + offsetX,
            getY() + getHeight()/2 - 8,
            jugador.direccion,
            9999,
            velocidadFlecha,
            0.5f
        );

        if (viewport != null) flecha.setViewport(viewport);

        flecha.setZIndex(1000);
        stage.addActor(flecha);

        System.out.println("Flecha generada con dano letal");
    }
    /**
     * Método para generar un hechizo básico desde la posición del mago
     */
    private void generarHechizoBasico() {
        // Verifica que el stage esté inicializado y que el jugador sea un mago
        if (stage == null || !(jugador instanceof Mago mago)) return;

        // Ajusta la posición del hechizo según la dirección del jugador
        float offsetX = "DERECHA".equals(jugador.direccion) ? getWidth() : -16;

        // Clona los frames del hechizo y su impacto para evitar errores visuales
        TextureRegion[] hechizoFramesClone = new TextureRegion[hechizoFrames.length];
        TextureRegion[] hechizoImpactoFramesClone = new TextureRegion[hechizoImpactoFrames.length];

        for (int i = 0; i < hechizoFrames.length; i++) {
            hechizoFramesClone[i] = new TextureRegion(hechizoFrames[i]);
        }

        for (int i = 0; i < hechizoImpactoFrames.length; i++) {
            hechizoImpactoFramesClone[i] = new TextureRegion(hechizoImpactoFrames[i]);
        }

        // Crea una nueva instancia del hechizo con daño letal, velocidad y escala
        HechizoActor hechizo = new HechizoActor(
            hechizoFramesClone,
            hechizoImpactoFramesClone,
            getX() + offsetX,
            getY() + getHeight()/2 - 8,   // Centra el hechizo verticalmente
            jugador.direccion,
            9999,     // Daño extremadamente alto
            500f,     // Velocidad del hechizo
            0.5f,     // Escala del sprite
            false     // No atraviesa enemigos
        );

        // Aplica el viewport si está disponible
        if (viewport != null) {
            hechizo.setViewport(viewport);
        }

        // Asegura que se dibuje encima de otros actores
        hechizo.setZIndex(1000);
        stage.addActor(hechizo);

        System.out.println("Hechizo básico generado con daño letal");
    }

    /**
     * Lista de enemigos en la pantalla actual, necesaria para procesar ataques correctamente.
     * Debe ser configurada por la pantalla principal del juego.
     */
    private List<? extends Enemigo> enemigosActuales;

    /**
     * Permite inyectar la lista de enemigos visibles. Debe llamarse desde la
     * pantalla principal antes de actualizar el actor para que los ataques
     * puedan procesar las colisiones correctamente.
     */
    public void setEnemigosActuales(List<? extends Enemigo> enemigosActuales) {
        this.enemigosActuales = enemigosActuales;
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        // Llama a la lógica de actualización usando la lista actual de enemigos
        update(delta, enemigosActuales);
    }

    /**
     * Actualiza input, movimiento, ataque y animaciones.
     */

    /**
     * Voltea todos los frames de animación cuando el jugador cambia de dirección.
     * Esto evita tener que voltear los sprites durante cada renderizado.
     */
    private void actualizarDireccionFrames() {
        // Si la dirección actual es la misma que antes, no se hace nada
        if (direccionActualFrames.equals(jugador.direccion)) return;

        // Determina si los frames deben estar volteados horizontalmente
        boolean debeEstarVolteado = "IZQUIERDA".equals(jugador.direccion);

        // Voltea los frames según la clase del jugador
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

        // Guarda la nueva dirección para evitar repeticiones
        direccionActualFrames = jugador.direccion;
        System.out.println("Dirección de frames cambiada a: " + direccionActualFrames);
    }

    /**
     * Voltea horizontalmente un arreglo de frames si es necesario.
     */
    private void voltearFrames(TextureRegion[] frames, boolean debeEstarVolteado) {
        if (frames == null) return;

        for (TextureRegion frame : frames) {
            if (frame != null && frame.isFlipX() != debeEstarVolteado) {
                frame.flip(true, false); // Voltea horizontalmente el frame
            }
        }
    }

    public void update(float delta, List<? extends Enemigo> enemigos) {
        // Detecta movimiento horizontal (A/D o flechas izquierda/derecha)
        float dirX = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT) || Gdx.input.isKeyPressed(Input.Keys.A)) dirX = -1;
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT) || Gdx.input.isKeyPressed(Input.Keys.D)) dirX = 1;

        // Detecta movimiento vertical (W/S o flechas arriba/abajo)
        float dirY = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.UP) || Gdx.input.isKeyPressed(Input.Keys.W)) dirY = 1;
        else if (Gdx.input.isKeyPressed(Input.Keys.DOWN) || Gdx.input.isKeyPressed(Input.Keys.S)) dirY = -1;

        // Si no está atacando, procesar movimiento
        if (!atacando) {
            // Si hay movimiento en alguna dirección, actualizar posición y dirección
            if (dirX != 0 || dirY != 0) {
                jugador.setPosition(getX(), getY());

                // Determina dirección horizontal del jugador
                if (dirX < 0) jugador.direccion = "IZQUIERDA";
                else if (dirX > 0) jugador.direccion = "DERECHA";

                // Actualiza los frames si la dirección cambió
                if (!direccionActualFrames.equals(jugador.direccion)) {
                    actualizarDireccionFrames();
                }

                // Si se está moviendo, activar animación de corrida
                if ((dirX != 0 || dirY != 0) && runFrames != null && runFrames.length > 0) {
                    if (!corriendo) {
                        // Transición de estado: Reposo -> Corriendo
                        corriendo = true;
                        enReposo = false;
                        tiempoCorrida = 0f;
                        frameRun = 0;
                        System.out.println("Transición: Reposo -> Corriendo");
                    } else {
                        // Ya estaba corriendo, actualiza animación
                        tiempoCorrida += delta;
                        frameRun = (int)(tiempoCorrida / RUN_FRAME_DUR) % runFrames.length;
                    }
                } else {
                    // Si ya no se mueve, transición de Corriendo -> Reposo
                    if (!enReposo) {
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
                // No hay movimiento, mantener en animación Idle
                corriendo = false;
                enReposo = true;
                tiempoCorrida = 0;
                frameRun = 0;
            }

            // Si está en reposo, actualizar la animación Idle
            if (enReposo && idleFrames != null && idleFrames.length > 0) {
                tiempoIdle += delta;
                frameIdle = (int)(tiempoIdle / IDLE_FRAME_DUR) % idleFrames.length;
            } else {
                // Reinicia el tiempo de Idle si dejó de estar en reposo
                tiempoIdle = 0;
            }
        } else {
            // Si está atacando, detener animación de corrida
            corriendo = false;
            tiempoCorrida = 0;
            frameRun = 0;
        }

        // Detectar ataques según tipo de personaje y botón
        // Ataque izquierdo: barra espaciadora o clic izquierdo
        boolean ataqueIzquierdoDetectado = Gdx.input.isKeyJustPressed(Input.Keys.SPACE) ||
            (Gdx.input.justTouched() && Gdx.input.isButtonPressed(Input.Buttons.LEFT));

        // Ataque derecho: clic derecho (para hechizos especiales del mago)
        boolean ataqueDerechoDetectado = Gdx.input.isButtonPressed(Input.Buttons.RIGHT) && Gdx.input.justTouched();

        // Solo se permite atacar si no se está ejecutando un ataque actualmente
        if (!atacando) {
            if (ataqueIzquierdoDetectado) {
                // === ATAQUE BÁSICO ===

                // Caballero: verifica si puede atacar y lanza ataque cuerpo a cuerpo
                if (jugador instanceof Caballero caballero && caballero.puedeAtacar()) {
                    caballero.registrarAtaque();
                    caballero.iniciarAtaque();

                    // Transición de estado a "Atacando"
                    if (!atacando) {
                        System.out.println("Transición: " + (corriendo ? "Corriendo" : "Reposo") + " -> Atacando (Caballero)");

                        // Se podrían usar estos flags para restaurar estado después del ataque (no usados directamente aquí)
                        boolean estabaEnReposo = enReposo;
                        boolean estabaCorriendo = corriendo;

                        // Configuración de estado de ataque
                        atacando = true;
                        corriendo = false;
                        enReposo = false;
                        tiempoAnimAtaque = 0f;
                        frameAttack = 0;
                        impactoHecho = false;
                        tipoAtaqueActual = TipoAtaque.NORMAL;
                    }

                } else if (jugador instanceof Mago) {
                    // Mago: lanza hechizo básico (no consume maná)
                    atacando = true;
                    tiempoAnimAtaque = 0f;
                    frameAttack = 0;
                    impactoHecho = false;
                    tipoAtaqueActual = TipoAtaque.NORMAL;

                } else if (jugador instanceof Arquero arquero) {
                    // Arquero: lanza flecha si tiene munición
                    if (arquero.getFlechas() > 0) {
                        atacando = true;
                        tiempoAnimAtaque = 0f;
                        frameAttack = 0;
                        impactoHecho = false;
                        tipoAtaqueActual = TipoAtaque.NORMAL;
                    }
                }

            } else if (ataqueDerechoDetectado && jugador instanceof Mago mago) {
                // === ATAQUE ESPECIAL DEL MAGO ===
                // Solo se permite si tiene suficiente maná
                if (mago.getMana() >= COSTO_HECHIZO_ESPECIAL) {
                    atacando = true;
                    tiempoAnimAtaque = 0f;
                    frameAttack = 0;
                    impactoHecho = false;
                    tipoAtaqueActual = TipoAtaque.ESPECIAL;

                    // Consumir maná para el hechizo especial
                    mago.consumirMana(COSTO_HECHIZO_ESPECIAL);
                    System.out.println("Mago lanza hechizo especial. Mana restante: " + mago.getMana());
                }
            }
        }
        // Si el jugador está atacando, avanzar la animación de ataque
        if (atacando) {
            // Acumular el tiempo transcurrido
            tiempoAnimAtaque += delta;

            // Calcular el frame actual de la animación
            int idx = (int)(tiempoAnimAtaque / ATTACK_FRAME_DUR);

            // === CABALLERO ===
            if (jugador instanceof Caballero && attackFrames != null) {
                // Si terminó la animación de ataque, finalizar ataque
                if (idx >= attackFrames.length) {
                    atacando = false;
                    ((Caballero)jugador).terminarAtaque();
                } else {
                    // Avanzar al frame correspondiente
                    frameAttack = idx;

                    // Si llegó al frame donde ocurre el golpe y aún no se aplicó
                    if (idx == ATTACK_IMPACT && !impactoHecho) {
                        if (enemigos != null && !enemigos.isEmpty()) {
                            ((Caballero)jugador).atacar(enemigos); // Aplica daño
                        } else {
                            // No hay enemigos, solo se registra el intento
                            System.out.println("Caballero ataca pero no hay enemigos cercanos");
                        }
                        impactoHecho = true;
                    }
                }

                // === MAGO ===
            } else if (jugador instanceof Mago) {
                if (tipoAtaqueActual == TipoAtaque.NORMAL && magoAttack1Frames != null) {
                    // Hechizo básico
                    if (idx >= magoAttack1Frames.length) {
                        // Generar hechizo y terminar ataque
                        generarHechizo();
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
                    // Hechizo especial
                    if (idx >= magoAttack2Frames.length) {
                        generarHechizo();
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
                    // Fallback en caso de error: siempre genera hechizo para evitar freeze
                    generarHechizo();
                    atacando = false;
                    impactoHecho = false;
                    tiempoAnimAtaque = 0;
                }

                // === ARQUERO ===
            } else if (jugador instanceof Arquero && arqueroAttackFrames != null) {
                if (idx >= arqueroAttackFrames.length) {
                    boolean pudoDisparar = false;

                    // Ataca si hay enemigos
                    if (enemigos != null) {
                        pudoDisparar = ((Arquero)jugador).atacar(enemigos);
                    } else {
                        // Si no hay enemigos, dispara de todos modos si tiene flechas
                        if (((Arquero)jugador).getFlechas() > 0) {
                            ((Arquero)jugador).ataque1(); // Solo consume flecha
                            pudoDisparar = true;
                        }
                    }

                    if (pudoDisparar) {
                        generarFlecha();
                        System.out.println("Flecha generada exitosamente");
                    } else {
                        System.out.println("No se pudo generar la flecha - sin munición");
                    }

                    atacando = false;
                    impactoHecho = false;
                    tiempoAnimAtaque = 0;
                } else {
                    frameAttack = idx;
                    if (idx == ARQUERO_ATTACK_IMPACT && !impactoHecho) {
                        impactoHecho = true;
                    }
                }

                // === OTRAS CLASES SIN ANIMACIONES ===
            } else {
                atacando = false;
                if (jugador instanceof Caballero cab) {
                    cab.terminarAtaque();
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        // Obtener posición y tamaño del actor
        float x = getX(), y = getY(), w = getWidth(), h = getHeight();

        // Por defecto, usamos el frame de reposo
        TextureRegion drawFrame = idleRegion;

        // Seleccionar el frame correcto según el tipo de personaje y su estado
        if (jugador instanceof Caballero) {
            // CABALLERO:
            if (atacando && attackFrames != null && frameAttack < attackFrames.length) {
                drawFrame = attackFrames[frameAttack]; // Frame actual del ataque
            } else if (corriendo && runFrames != null && frameRun < runFrames.length) {
                drawFrame = runFrames[frameRun]; // Frame actual de la animación de correr
            } else if (enReposo && idleFrames != null && frameIdle < idleFrames.length) {
                drawFrame = idleFrames[frameIdle]; // Frame actual de la animación idle
            }

        } else if (jugador instanceof Mago) {
            // MAGO:
            if (atacando) {
                if (tipoAtaqueActual == TipoAtaque.NORMAL &&
                    magoAttack1Frames != null && frameAttack < magoAttack1Frames.length) {
                    drawFrame = magoAttack1Frames[frameAttack]; // Ataque básico
                } else if (tipoAtaqueActual == TipoAtaque.ESPECIAL &&
                    magoAttack2Frames != null && frameAttack < magoAttack2Frames.length) {
                    drawFrame = magoAttack2Frames[frameAttack]; // Ataque especial
                }
            } else if (corriendo && runFrames != null && frameRun < runFrames.length) {
                drawFrame = runFrames[frameRun]; // Animación de correr
            } else if (enReposo && idleFrames != null && frameIdle < idleFrames.length) {
                drawFrame = idleFrames[frameIdle]; // Animación idle
            }

        } else if (jugador instanceof Arquero) {
            // ARQUERO:
            if (atacando && arqueroAttackFrames != null && frameAttack < arqueroAttackFrames.length) {
                drawFrame = arqueroAttackFrames[frameAttack]; // Ataque con arco
            } else if (corriendo && runFrames != null && frameRun < runFrames.length) {
                drawFrame = runFrames[frameRun]; // Animación de correr
            } else if (enReposo && idleFrames != null && frameIdle < idleFrames.length) {
                drawFrame = idleFrames[frameIdle]; // Animación idle
            }
        }

        // Finalmente dibujamos el frame seleccionado, si es válido
        // No se necesita voltear aquí porque eso ya lo maneja actualizarDireccionFrames()
        if (drawFrame != null) {
            batch.draw(drawFrame, x, y, w, h);
        }
    }

    /**
     * Dibuja el HUD del jugador (vida, clase y recurso especial) en pantalla.
     * Se muestra en la esquina superior izquierda con el uso de BitmapFont.
     */
    public void dibujarHUD(SpriteBatch batch, BitmapFont font) {
        float px = 20, py = Gdx.graphics.getHeight() - 20;

        // Mostrar la clase del jugador (Caballero, Mago, Arquero)
        font.draw(batch, "Clase: " + jugador.getClass().getSimpleName(), px, py);
        py -= 20;

        // Mostrar la vida actual
        font.draw(batch, "Vida: " + jugador.getVida(), px, py);
        py -= 20;

        // Mostrar el recurso especial según la clase
        if (jugador instanceof Mago) {
            font.draw(batch, "Mana: " + ((Mago)jugador).getMana(), px, py);
        } else if (jugador instanceof Caballero) {
            font.draw(batch, "Escudo: " + ((Caballero)jugador).getEscudo(), px, py);
        } else if (jugador instanceof Arquero) {
            font.draw(batch, "Flechas: " + ((Arquero)jugador).getFlechas(), px, py);
        }
    }

    /**
     * Devuelve el rectángulo de colisión del jugador, basado en su posición y tamaño actual.
     */
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Sobrescribe el método remove() para asegurar que se liberen correctamente los recursos antes de eliminar el actor.
     * @return true si se eliminó exitosamente del Stage
     */
    @Override
    public boolean remove() {
        // Liberar recursos usados antes de eliminar al jugador
        dispose();
        return super.remove();
    }

    /**
     * Libera todas las texturas de animaciones del jugador para evitar fugas de memoria.
     * Este método debe ser llamado manualmente cuando se termine de usar este actor.
     */
    public void dispose() {
        try {
            // === CABALLERO ===
            if (attackFrames != null) {
                for (TextureRegion frame : attackFrames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }
            if (runFrames != null) {
                for (TextureRegion frame : runFrames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }
            if (idleFrames != null) {
                for (TextureRegion frame : idleFrames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }

            // === MAGO ===
            if (magoAttack1Frames != null) {
                for (TextureRegion frame : magoAttack1Frames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }
            if (magoAttack2Frames != null) {
                for (TextureRegion frame : magoAttack2Frames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }
            if (hechizoFrames != null) {
                for (TextureRegion frame : hechizoFrames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }
            if (hechizoImpactoFrames != null) {
                for (TextureRegion frame : hechizoImpactoFrames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }

            // === ARQUERO ===
            if (arqueroAttackFrames != null) {
                for (TextureRegion frame : arqueroAttackFrames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }
            if (flechaFrames != null) {
                for (TextureRegion frame : flechaFrames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }
            if (flechaImpactoFrames != null) {
                for (TextureRegion frame : flechaImpactoFrames) {
                    if (frame != null && frame.getTexture() != null) frame.getTexture().dispose();
                }
            }

        } catch (Exception e) {
            System.err.println("Error al liberar recursos del PlayerActor: " + e.getMessage());
        }
    }
}
