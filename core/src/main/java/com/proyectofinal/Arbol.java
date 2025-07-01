package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;

public class Arbol extends ElementoDeMapa {

    public Arbol(Texture texture) {
        this.texture = texture;
        this.collider = new Rectangle();  // Inicializamos el collider
    }

    @Override
    public void actualizar(float delta) {

    }

    @Override
    public void colocar(float x, float y) {
        // Guardamos la posición original para renderizado
        this.posX = x;
        this.posY = y;

        // Configuramos el collider para que cubra solo el tronco del árbol
        float troncoWidth = texture.getWidth() * 0.2f; // Ancho reducido para el tronco (20%)
        float troncoHeight = texture.getHeight() * 0.1f; // Altura aumentada para el tronco (40%)
        // Calculamos la posición para centrar el collider horizontalmente
        float troncoX = x + (texture.getWidth() * 0.4f); // Desplazado al 40% desde la izquierda
        // Posición vertical elevada para que el collider esté un poco más arriba de la base
        float troncoY = y + (texture.getHeight() * 0.15f); // Elevado un 15% de la altura total

        // Configuramos el collider con el tamaño y la posición del tronco
        this.collider.set(troncoX, troncoY, troncoWidth, troncoHeight);
    }

    @Override
    public void crearCollider() {
        // Aquí puedes agregar lógica adicional para modificar el collider si es necesario
    }

    // Método para obtener el collider
    @Override
    public Rectangle getCollider() {
        return this.collider; // Retorna el collider ajustado al tronco
    }
}
