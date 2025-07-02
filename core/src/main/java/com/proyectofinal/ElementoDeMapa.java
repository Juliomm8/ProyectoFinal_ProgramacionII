package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

/**
 * Clase base abstracta para todos los objetos del mapa (como arboles o piedras).
 * Define la textura, posicion y colision.
 */
public abstract class ElementoDeMapa implements Disposable {
    protected Texture texture;         // Textura del objeto a renderizar
    protected Rectangle collider;      // Rectangulo de colision del objeto
    protected float posX, posY;        // Posicion del objeto en el mundo

    /**
     * Metodo abstracto que las clases hijas deben implementar
     * para definir como se coloca el objeto en el mapa.
     */
    public abstract void colocar(float x, float y);

    /**
     * Renderiza la textura del objeto en su posicion actual.
     */
    public void render(SpriteBatch batch) {
        batch.draw(texture, posX, posY);
    }

    /**
     * Devuelve el rectangulo de colision del objeto.
     */
    public Rectangle getCollider() {
        return collider;
    }

    /**
     * Libera los recursos asociados con este objeto (como la textura).
     */
    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
