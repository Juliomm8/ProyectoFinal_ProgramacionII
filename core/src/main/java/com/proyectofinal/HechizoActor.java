package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.Animation;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.viewport.Viewport;

import java.util.List;

/**
 * Actor que representa un hechizo lanzado por el Mago.
 * Versión mejorada con soporte para viewport y mejor manejo de límites.
 */
public class HechizoActor extends ProyectilBase {
    private float radioEfecto; // Radio para daño en área
    private boolean atraviesaEnemigos; // Si el hechizo atraviesa enemigos
    private float escala; // Escala visual del hechizo
    private Viewport viewport; // Viewport para límites de pantalla

    /**
     * Constructor para hechizos
     * @param frames frames de la animación del hechizo
     * @param impactoFrames frames de la animación de impacto
     * @param x posición inicial X
     * @param y posición inicial Y
     * @param direccion dirección del lanzamiento
     * @param danio daño base del hechizo
     * @param velocidad velocidad del proyectil
     * @param escala escala visual
     * @param radioEfecto radio de efecto para daño en área
     */
    public HechizoActor(TextureRegion[] frames, TextureRegion[] impactoFrames,
                        float x, float y, String direccion, int danio,
                        float velocidad, float escala, float radioEfecto) {
        super(x, y, direccion, danio + 20, velocidad);

        this.radioEfecto = radioEfecto;
        this.atraviesaEnemigos = false; // Por defecto no atraviesa enemigos
        this.escala = escala; // Guardar la escala como propiedad

        // Establecer tamaño
        setSize(32 * escala, 32 * escala);

        // Crear animaciones
        animacion = new Animation<>(0.1f, frames);
        animacionImpacto = new Animation<>(0.1f, impactoFrames);

        // Ajustar hitbox circular para hechizos
        actualizarHitbox();
    }

    /**
     * Constructor para hechizos especiales (atraviesan enemigos)
     * @param frames frames de la animación del hechizo
     * @param impactoFrames frames de la animación de impacto
     * @param x posición inicial X
     * @param y posición inicial Y
     * @param direccion dirección del lanzamiento
     * @param danio daño base del hechizo
     * @param velocidad velocidad del proyectil
     * @param escala escala visual
     * @param atraviesaEnemigos si el hechizo atraviesa enemigos
     */
    public HechizoActor(TextureRegion[] frames, TextureRegion[] impactoFrames,
                        float x, float y, String direccion, int danio,
                        float velocidad, float escala, boolean atraviesaEnemigos) {
        this(frames, impactoFrames, x, y, direccion, danio, velocidad, escala, 50f);
        this.atraviesaEnemigos = atraviesaEnemigos; // Si atraviesa enemigos, este parámetro se pasa como true
        System.out.println("Creando hechizo " + (atraviesaEnemigos ? "que atraviesa enemigos" : "normal"));
    }

    public HechizoActor(TextureRegion[] frames, TextureRegion[] impactoFrames,
                        float x, float y, String direccion, int danio,
                        float velocidad, float escala, float radioEfecto,
                        boolean atraviesaEnemigos) {
        this(frames, impactoFrames, x, y, direccion, danio, velocidad, escala, radioEfecto);
        this.atraviesaEnemigos = atraviesaEnemigos; // Si atraviesa enemigos, este parámetro se pasa como true
        System.out.println("Creando hechizo " + (atraviesaEnemigos ? "que atraviesa enemigos" : "normal"));
    }

    @Override
    protected void actualizarHitbox() {
        // Hitbox centrada para hechizos, ajustada según la escala
        float hitboxWidth = getWidth() * 0.6f;
        float hitboxHeight = getHeight() * 0.6f;

        // Posición centrada respecto al sprite
        float hitboxX = getX() + (getWidth() - hitboxWidth) / 2;
        float hitboxY = getY() + (getHeight() - hitboxHeight) / 2;

        hitbox.set(hitboxX, hitboxY, hitboxWidth, hitboxHeight);

        // Debug: mostrar dimensiones de la hitbox
        System.out.println("Hitbox hechizo: x=" + hitbox.x + ", y=" + hitbox.y +
                         ", w=" + hitbox.width + ", h=" + hitbox.height);
    }

    @Override
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (impactado || enemigos == null || enemigos.isEmpty()) return;

        System.out.println("Comprobando colisiones para hechizo en posición (" + getX() + ", " + getY() + ")");
        System.out.println("Hitbox hechizo: x=" + hitbox.x + ", y=" + hitbox.y + ", w=" + hitbox.width + ", h=" + hitbox.height);

        // Verificar colisión directa primero
        boolean colisionDetectada = false;
        Enemigo enemigoImpactado = null;

        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                Rectangle enemyHitbox = e.getHitbox();
                boolean colision = hitbox.overlaps(enemyHitbox);

                if (colision) {
                    enemigoImpactado = e;
                    colisionDetectada = true;
                    break;
                }
            }
        }

        if (colisionDetectada) {
            // Cambiar a estado de impacto
            impactado = true;
            stateTime = 0f; // Reiniciar para animación de impacto

            // Aplicar daño al enemigo impactado
            System.out.println("¡Hechizo impactó directamente a un enemigo en (" +
                              enemigoImpactado.getX() + ", " + enemigoImpactado.getY() + ")!");
            enemigoImpactado.recibirDanio(danio);

            // Aplicar daño en área a enemigos cercanos
            float centroX = getX() + getWidth() / 2;
            float centroY = getY() + getHeight() / 2;

            for (Enemigo e : enemigos) {
                if (e != enemigoImpactado && e.estaVivo()) {
                    // Calcular distancia al centro del impacto
                    float eX = e.getX() + e.getWidth() / 2;
                    float eY = e.getY() + e.getHeight() / 2;
                    float distancia = (float) Math.sqrt(
                        Math.pow(centroX - eX, 2) + Math.pow(centroY - eY, 2));

                    // Si está dentro del radio de efecto, aplicar daño reducido
                    if (distancia <= radioEfecto) {
                        // El daño se reduce con la distancia
                        float factorDanio = 1 - (distancia / radioEfecto);
                        int danioArea = (int)(danio * factorDanio * 0.5f); // 50% del daño directo como máximo

                        if (danioArea > 0) {
                            System.out.println("Daño de área a enemigo a distancia " + distancia);
                            e.recibirDanio(danioArea);
                        }
                    }
                }
            }

            // Si el hechizo atraviesa enemigos, no terminamos aquí
            if (!atraviesaEnemigos) {
                // Solo si no atraviesa enemigos, mantenemos impactado = true
                impactado = true;
            } else {
                // Si atraviesa, seguimos en movimiento
                System.out.println("El hechizo atraviesa al enemigo y continúa");
                impactado = false;
            }
        }
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Si debe eliminarse, remover del escenario
        if (debeEliminarse) {
            remove();
            return;
        }

        // Incrementar contador de tiempo
        stateTime += delta;

        if (impactado) {
            // Si está impactado y la animación terminó, marcar para eliminar
            if (animacionImpacto.isAnimationFinished(stateTime)) {
                if (atraviesaEnemigos) {
                    // Si atraviesa enemigos, volver a estado de vuelo
                    impactado = false;
                    stateTime = 0f;
                } else {
                    // Si no atraviesa, eliminar
                    debeEliminarse = true;
                }
            }
        } else {
            // Mover el hechizo según su dirección
            float movX = "DERECHA".equals(direccion) ? velocidad * delta : -velocidad * delta;
            moveBy(movX, 0);

            // Actualizar hitbox
            actualizarHitbox();

            // Calcular límites del mundo con margen
            float margenExtra = 100f;
            float limiteIzquierdo, limiteDerecho;

            if (viewport != null) {
                limiteIzquierdo = -getWidth() - margenExtra;
                limiteDerecho = viewport.getWorldWidth() + margenExtra;
            } else {
                limiteIzquierdo = -getWidth() - margenExtra;
                limiteDerecho = Gdx.graphics.getWidth() + margenExtra;
            }

            // Eliminar si está fuera de límites
            if (getX() < limiteIzquierdo || getX() > limiteDerecho) {
                debeEliminarse = true;
                System.out.println("Hechizo fuera de límites: " + getX() +
                    " (límites: " + limiteIzquierdo + ", " + limiteDerecho + ")");
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (batch == null) return; // Protección contra null

        TextureRegion frame;
        if (impactado) {
            frame = animacionImpacto.getKeyFrame(stateTime, false);
        } else {
            frame = animacion.getKeyFrame(stateTime, true);
        }

        if (frame != null) {
            if ("DERECHA".equals(direccion)) {
                batch.draw(frame, getX(), getY(), getWidth(), getHeight());
            } else {
                batch.draw(frame, getX() + getWidth(), getY(), -getWidth(), getHeight());
            }
        }
    }

    @Override
    public boolean remove() {
        // Llamar al método de la superclase
        return super.remove();
    }

    /** No liberamos texturas aquí; lo hace quien creó los recursos. */
    public void dispose() {
        // vacío
    }

    /**
     * Establece el viewport para determinar los límites de la pantalla
     * @param viewport El viewport a usar
     */
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
}
