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
 * Incluye logica de colision, animacion, daño directo y en area,
 * y comportamiento para atravesar enemigos.
 */
public class HechizoActor extends ProyectilBase {
    private float radioEfecto;             // Radio de efecto para daño en area
    private boolean atraviesaEnemigos;     // Si el hechizo atraviesa enemigos o no
    private float escala;                  // Escala visual del hechizo
    private Viewport viewport;             // Viewport para calcular limites de pantalla

    // Constructor para hechizos normales
    public HechizoActor(TextureRegion[] frames, TextureRegion[] impactoFrames,
                        float x, float y, String direccion, int danio,
                        float velocidad, float escala, float radioEfecto) {
        super(x, y, direccion, danio + 20, velocidad);
        this.radioEfecto = radioEfecto;
        this.atraviesaEnemigos = false;
        this.escala = escala;
        setSize(32 * escala, 32 * escala);

        if (frames != null) animacion = new Animation<>(0.1f, frames);
        if (impactoFrames != null) animacionImpacto = new Animation<>(0.1f, impactoFrames);

        actualizarHitbox();
    }

    // Constructor para hechizos que pueden atravesar enemigos
    public HechizoActor(TextureRegion[] frames, TextureRegion[] impactoFrames,
                        float x, float y, String direccion, int danio,
                        float velocidad, float escala, boolean atraviesaEnemigos) {
        this(frames, impactoFrames, x, y, direccion, danio, velocidad, escala, 50f);
        this.atraviesaEnemigos = atraviesaEnemigos;
    }

    // Constructor completo con radio de efecto y posibilidad de atravesar
    public HechizoActor(TextureRegion[] frames, TextureRegion[] impactoFrames,
                        float x, float y, String direccion, int danio,
                        float velocidad, float escala, float radioEfecto,
                        boolean atraviesaEnemigos) {
        this(frames, impactoFrames, x, y, direccion, danio, velocidad, escala, radioEfecto);
        this.atraviesaEnemigos = atraviesaEnemigos;
    }

    // Actualiza la hitbox del hechizo basada en su escala
    @Override
    protected void actualizarHitbox() {
        float hitboxWidth = getWidth() * 0.6f;
        float hitboxHeight = getHeight() * 0.6f;
        float hitboxX = getX() + (getWidth() - hitboxWidth) / 2;
        float hitboxY = getY() + (getHeight() - hitboxHeight) / 2;
        hitbox.set(hitboxX, hitboxY, hitboxWidth, hitboxHeight);
    }

    // Revisa colisiones con enemigos y aplica daño
    @Override
    public void comprobarColisiones(List<? extends Enemigo> enemigos) {
        if (impactado || enemigos == null || enemigos.isEmpty()) return;

        boolean colisionDetectada = false;
        Enemigo enemigoImpactado = null;

        // Verifica colision directa con enemigos
        for (Enemigo e : enemigos) {
            if (e.estaVivo() && hitbox.overlaps(e.getHitbox())) {
                enemigoImpactado = e;
                colisionDetectada = true;
                break;
            }
        }

        if (colisionDetectada) {
            impactado = true;
            stateTime = 0f;
            enemigoImpactado.recibirDanio(danio);

            // Daño en area a enemigos cercanos
            float centroX = getX() + getWidth() / 2;
            float centroY = getY() + getHeight() / 2;

            for (Enemigo e : enemigos) {
                if (e != enemigoImpactado && e.estaVivo()) {
                    float eX = e.getX() + e.getWidth() / 2;
                    float eY = e.getY() + e.getHeight() / 2;
                    float distancia = (float) Math.sqrt(Math.pow(centroX - eX, 2) + Math.pow(centroY - eY, 2));

                    if (distancia <= radioEfecto) {
                        float factorDanio = 1 - (distancia / radioEfecto);
                        int danioArea = (int)(danio * factorDanio * 0.5f);
                        if (danioArea > 0) e.recibirDanio(danioArea);
                    }
                }
            }

            // Si atraviesa, el hechizo sigue activo
            impactado = !atraviesaEnemigos;
        }
    }

    // Lógica de actualización por frame
    @Override
    public void act(float delta) {
        super.act(delta);

        if (debeEliminarse) {
            remove();
            return;
        }

        stateTime += delta;

        if (impactado) {
            if (animacionImpacto != null && animacionImpacto.isAnimationFinished(stateTime)) {
                if (atraviesaEnemigos) {
                    impactado = false;
                    stateTime = 0f;
                } else {
                    debeEliminarse = true;
                }
            }
        } else {
            float movX = "DERECHA".equals(direccion) ? velocidad * delta : -velocidad * delta;
            moveBy(movX, 0);
            actualizarHitbox();

            float margenExtra = 100f;
            float limiteIzquierdo = -getWidth() - margenExtra;
            float limiteDerecho = viewport != null ? viewport.getWorldWidth() + margenExtra : Gdx.graphics.getWidth() + margenExtra;

            if (getX() < limiteIzquierdo || getX() > limiteDerecho) {
                debeEliminarse = true;
            }
        }
    }

    // Dibuja el hechizo segun su estado (vuelo o impacto)
    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (batch == null) return;

        TextureRegion frame = impactado ? (animacionImpacto != null ? animacionImpacto.getKeyFrame(stateTime, false) : null)
            : (animacion != null ? animacion.getKeyFrame(stateTime, true) : null);

        if (frame != null) {
            if ("DERECHA".equals(direccion)) {
                batch.draw(frame, getX(), getY(), getWidth(), getHeight());
            } else {
                batch.draw(frame, getX() + getWidth(), getY(), -getWidth(), getHeight());
            }
        }
    }

    // Elimina el hechizo de forma segura
    @Override
    public boolean remove() {
        return super.remove();
    }

    // El dispose esta vacio porque quien crea los frames gestiona las texturas
    public void dispose() {
        // vacio
    }

    // Asigna el viewport al hechizo para calcular limites del mundo
    public void setViewport(Viewport viewport) {
        this.viewport = viewport;
    }
}
