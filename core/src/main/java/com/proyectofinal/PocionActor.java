package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.MathUtils;

/**
 * Clase visual que representa una pocion dentro del juego.
 * Maneja su render, colision, animacion y desaparicion automatica.
 */
public class PocionActor extends Image {
    private final Pocion pocion;               // Referencia a la pocion logica
    private Texture texture;                   // Textura visual de la pocion
    private Rectangle hitbox;                  // Hitbox para colisiones con el jugador

    private static final float TIEMPO_VIDA_MAXIMO = 10f; // Tiempo que dura en pantalla
    private float tiempoVida = TIEMPO_VIDA_MAXIMO;
    private float tiempoTranscurrido = 0f;

    private boolean recogida = false;          // Si ya fue recogida
    private boolean debeEliminarse = false;    // Si debe eliminarse en el siguiente frame

    /**
     * Constructor simple con tamaño por defecto.
     */
    public PocionActor(Pocion pocion, Texture texture) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.pocion = pocion;
        this.texture = texture;
        setSize(40, 40); // Tamaño visual

        // Hitbox mas grande para facilitar recogida
        float hitboxScale = 0.9f;
        hitbox = UtilColisiones.crearHitbox(getX(), getY(), getWidth(), getHeight(), hitboxScale);
    }

    /**
     * Constructor completo para ubicar la pocion con escala personalizada.
     */
    public PocionActor(Pocion pocion, Texture texture, float x, float y, float escala) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.pocion = pocion;
        this.texture = texture;
        setPosition(x, y);
        setSize(texture.getWidth() * escala, texture.getHeight() * escala);

        // Hitbox mas pequeno
        float hitboxScale = 0.8f;
        hitbox = UtilColisiones.crearHitbox(x, y, getWidth(), getHeight(), hitboxScale);
    }

    @Override
    public void act(float delta) {
        super.act(delta);

        // Si ya fue recogida o debe desaparecer
        if (recogida || debeEliminarse) {
            remove();
            return;
        }

        // Actualizar tiempo
        tiempoTranscurrido += delta;
        tiempoVida -= delta;

        // Si se acaba el tiempo
        if (tiempoVida <= 0) {
            debeEliminarse = true;
            System.out.println("Pocion expirada: " + pocion.getNombre());
            return;
        }

        // Efecto de flotacion (suave movimiento vertical)
        float offsetY = MathUtils.sin(tiempoTranscurrido * 3) * 5f;
        setY(getY() + offsetY - MathUtils.sin((tiempoTranscurrido - delta) * 3) * 5f);

        // Pequeño giro oscilante
        setRotation(MathUtils.sin(tiempoTranscurrido * 1.5f) * 8f);

        // Actualizar posicion del hitbox
        actualizarHitbox();
    }

    /**
     * Reposiciona el hitbox segun la pocion.
     */
    private void actualizarHitbox() {
        hitbox.setPosition(getX() + (getWidth() - hitbox.width) / 2,
            getY() + (getHeight() - hitbox.height) / 2);
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (batch == null || texture == null || recogida || debeEliminarse) return;

        // Parpadeo en los ultimos segundos
        float alpha = parentAlpha;
        if (tiempoVida < 3f) {
            alpha = parentAlpha * (MathUtils.sin(tiempoTranscurrido * 15) * 0.5f + 0.5f);
        }

        // Reduccion progresiva de visibilidad
        float factorTiempo = tiempoVida / TIEMPO_VIDA_MAXIMO;
        float alphaFinal = alpha * Math.max(0.5f, factorTiempo); // Nunca menor a 0.5

        batch.setColor(1, 1, 1, alphaFinal);
        super.draw(batch, alphaFinal);
        batch.setColor(1, 1, 1, 1); // Restaurar color
    }

    /**
     * Verifica si el jugador colisiona con la pocion.
     */
    public boolean comprobarColision(Rectangle jugador) {
        return UtilColisiones.colisionConTolerancia(hitbox, jugador, 5f);
    }

    /**
     * Aplica el efecto de la pocion al personaje si es valida.
     */
    public void recoger(Personaje personaje) {
        if (recogida) return;

        recogida = true;
        System.out.println(personaje.getNombre() + " recoge " + pocion.getNombre());

        try {
            pocion.consumir(personaje);
        } catch (InvalidPotionException e) {
            System.out.println(e.getMessage());
            recogida = false; // Revertir si no se pudo consumir
            return;
        }

        debeEliminarse = true;
    }

    public boolean debeEliminarse() {
        return debeEliminarse;
    }

    public boolean estaRecogida() {
        return recogida;
    }

    public Pocion getPocion() {
        return pocion;
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
}
