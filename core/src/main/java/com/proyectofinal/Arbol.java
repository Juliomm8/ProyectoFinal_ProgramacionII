package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

/**
 * Clase que representa un árbol como elemento del mapa.
 * Tiene una textura completa, pero su colisión solo se aplica en el área del tronco.
 */
public class Arbol extends ElementoDeMapa {

    public Arbol(Texture texture) {
        this.texture = texture;
        this.collider = new Rectangle();  // Se inicializa el collider del tronco
    }

    /**
     * Posiciona el árbol en las coordenadas dadas y configura el área de colisión (collider)
     * para que solo cubra el tronco, no toda la imagen del árbol.
     */
    @Override
    public void colocar(float x, float y) {
        // Guardamos la posición para renderizar la textura correctamente
        this.posX = x;
        this.posY = y;

        // Calculamos el tamaño del collider basado en la textura:
        float troncoWidth = texture.getWidth() * 0.2f;   // El tronco mide un 20% del ancho total
        float troncoHeight = texture.getHeight() * 0.1f; // El tronco mide un 10% del alto total

        // Centramos el collider horizontalmente (40% desde la izquierda lo coloca centrado con 20% de ancho)
        float troncoX = x + (texture.getWidth() * 0.4f);

        // Elevamos un poco el collider para que no esté en la base exacta del árbol
        float troncoY = y + (texture.getHeight() * 0.15f); // A 15% desde abajo

        // Establecemos el collider con la posición y tamaño calculado
        this.collider.set(troncoX, troncoY, troncoWidth, troncoHeight);
    }

    /**
     * Retorna el área de colisión del árbol, que está limitada al tronco.
     */
    @Override
    public Rectangle getCollider() {
        return this.collider;
    }
}
