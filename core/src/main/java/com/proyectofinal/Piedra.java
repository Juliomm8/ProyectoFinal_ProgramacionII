package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Piedra extends ElementoDeMapa {

    public Piedra(Texture texture) {
        this.texture = texture;
        this.collider = new Rectangle();  // Inicializamos el collider
    }

    @Override
    public void colocar(float x, float y) {
        // Guardamos la posición original para renderizado
        this.posX = x;
        this.posY = y;

        // Creamos un collider de tamaño cero (sin colisión)
        this.collider.set(0, 0, 0, 0);
    }

    @Override
    public Rectangle getCollider() {
        return this.collider;
    }
}
