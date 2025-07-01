package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.math.MathUtils;

/**
 * Actor que representa una poción en el mundo del juego.
 * Se encarga de renderizar la poción y controlar su tiempo de vida.
 */
public class PocionActor extends Image {
    private final Pocion pocion;
    private Texture texture;
    private Rectangle hitbox;
    private float tiempoVida = 10f; // Duración máxima en segundos
    private boolean recogida = false;
    private boolean debeEliminarse = false;
    private float tiempoTranscurrido = 0f;

    /**
     * Constructor simple para la poción con tamaño predeterminado.
     * @param pocion Objeto poción a representar
     * @param texture Textura para visualizar la poción
     */
    public PocionActor(Pocion pocion, Texture texture) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.pocion = pocion;
        this.texture = texture;
        setSize(32, 32);  // tamaño predeterminado

        // Crear un hitbox ligeramente más pequeño que la textura
        float hitboxScale = 0.8f;
        hitbox = UtilColisiones.crearHitbox(getX(), getY(), getWidth(), getHeight(), hitboxScale);
    }

    /**
     * Constructor completo para la poción con parámetros adicionales.
     * @param pocion Objeto poción a representar
     * @param texture Textura para visualizar la poción
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param escala Escala de la textura
     */
    public PocionActor(Pocion pocion, Texture texture, float x, float y, float escala) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.pocion = pocion;
        this.texture = texture;
        setPosition(x, y);
        setSize(texture.getWidth() * escala, texture.getHeight() * escala);

        // Crear un hitbox ligeramente más pequeño que la textura
        float hitboxScale = 0.8f;
        hitbox = UtilColisiones.crearHitbox(x, y, getWidth(), getHeight(), hitboxScale);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Si ya fue recogida o debe eliminarse, no procesar más
        if (recogida || debeEliminarse) {
            remove();
            return;
        }

        // Actualizar tiempo de vida
        tiempoTranscurrido += delta;
        tiempoVida -= delta;

        // Si se acabó el tiempo, marcar para eliminar
        if (tiempoVida <= 0) {
            debeEliminarse = true;
            System.out.println("Poción expirada: " + pocion.getNombre());
            return;
        }

        // Efecto de flotación para la poción (movimiento suave vertical)
        float offsetY = MathUtils.sin(tiempoTranscurrido * 2) * 3f;
        setY(getY() + offsetY - MathUtils.sin((tiempoTranscurrido - delta) * 2) * 3f);

        // Actualizar hitbox para seguir a la poción
        actualizarHitbox();
    }

    private void actualizarHitbox() {
        hitbox.setPosition(getX() + (getWidth() - hitbox.width) / 2,
                          getY() + (getHeight() - hitbox.height) / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (batch == null || texture == null || recogida || debeEliminarse) return;

        // Calcular un factor de parpadeo cuando la poción esté a punto de desaparecer
        float alpha = parentAlpha;
        if (tiempoVida < 3f) {
            // Parpadear rápidamente en los últimos 3 segundos
            alpha = parentAlpha * (MathUtils.sin(tiempoTranscurrido * 10) * 0.3f + 0.7f);
        }

        batch.setColor(1, 1, 1, alpha);
        super.draw(batch, alpha);
        batch.setColor(1, 1, 1, 1); // Restaurar
    }

    /**
     * Verifica si el jugador ha colisionado con esta poción.
     * @param jugador Rectángulo que representa al jugador
     * @return true si hay colisión
     */
    public boolean comprobarColision(Rectangle jugador) {
        // Agregar una pequeña tolerancia para facilitar la colisión
        return UtilColisiones.colisionConTolerancia(hitbox, jugador, 5f);
    }

    /**
     * Marca la poción como recogida y aplicar sus efectos al jugador.
     * @param personaje Personaje que recoge la poción
     */
    public void recoger(Personaje personaje) {
        if (recogida) return;

        recogida = true;
        System.out.println(personaje.getNombre() + " recoge " + pocion.getNombre());

        // Aplicar el efecto de la poción
        pocion.consumir(personaje);

        // Marcar para eliminar en el próximo frame
        debeEliminarse = true;
    }

    /**
     * Indica si esta poción debe ser eliminada del juego.
     */
    public boolean debeEliminarse() {
        return debeEliminarse;
    }

    /**
     * Indica si esta poción ha sido recogida.
     */
    public boolean estaRecogida() {
        return recogida;
    }

    /**
     * Libera los recursos utilizados por la poción.
     */
    public void dispose() {
        // No liberamos la textura aquí porque puede ser compartida
        // entre varias pociones
    }

    /**
     * Devuelve la poción asociada a este actor.
     */
    public Pocion getPocion() {
        return pocion;
    }

    /** Retorna el rectángulo de colisión de esta poción */
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
}
