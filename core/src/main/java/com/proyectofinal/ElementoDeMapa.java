package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Disposable;

public abstract class ElementoDeMapa implements Disposable {
    // Método abstracto para actualizar el elemento
    public abstract void actualizar(float delta);
    protected Texture texture;  // Textura del objeto
    protected Rectangle collider;  // Collider del objeto
    protected float posX, posY;  // Posición para renderizado

    // Método abstracto para colocar el objeto en el mapa
    public abstract void colocar(float x, float y);

    // Método abstracto para crear el collider
    public abstract void crearCollider();

    // Método común para renderizar el objeto
    public void render(SpriteBatch batch) {
        batch.draw(texture, posX, posY);  // Renderiza la textura en la posición original
    }

    // Método para obtener el collider del objeto
    public Rectangle getCollider() {
        return collider;
    }

    /**
     * Libera los recursos utilizados por este elemento
     */
    @Override
    public void dispose() {
        if (texture != null) {
            texture.dispose();
            texture = null;
        }
    }
}
