package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Representa una piedra decorativa del mapa.
 * No tiene colision, por lo tanto solo se usa para render visual.
 */
public class Piedra extends ElementoDeMapa {

    /**
     * Constructor que recibe la textura de la piedra.
     * @param texture textura visual que se renderizara en el mapa.
     */
    public Piedra(Texture texture) {
        this.texture = texture;
        this.collider = new Rectangle();  // Inicializamos el collider
    }

    /**
     * Posiciona visualmente la piedra en el mapa.
     * No tiene colision, por lo tanto el collider es 0x0.
     * @param x posicion X
     * @param y posicion Y
     */
    @Override
    public void colocar(float x, float y) {
        this.posX = x;
        this.posY = y;

        // Collider sin dimensiones ya que no colisiona
        this.collider.set(0, 0, 0, 0);
    }

    /**
     * Devuelve el collider, que en este caso es un rectangulo vacio.
     * @return Rectangle sin colision real
     */
    @Override
    public Rectangle getCollider() {
        return this.collider;
    }
}
