package com.proyectofinal;

import com.badlogic.gdx.math.Rectangle;

/**
 * Clase de utilidad para facilitar la detección de colisiones
 */
public class UtilColisiones {

    /**
     * Crea un rectángulo para colisión con tamaño ajustado
     * @param x Coordenada X
     * @param y Coordenada Y
     * @param width Ancho original
     * @param height Alto original
     * @param factor Factor de escala (0.8f = 80% del tamaño original)
     * @return Rectángulo ajustado para colisiones
     */
    public static Rectangle crearHitbox(float x, float y, float width, float height, float factor) {
        float nuevoAncho = width * factor;
        float nuevoAlto = height * factor;
        float offsetX = (width - nuevoAncho) / 2;
        float offsetY = (height - nuevoAlto) / 2;

        return new Rectangle(x + offsetX, y + offsetY, nuevoAncho, nuevoAlto);
    }

    /**
     * Comprueba si hay colisión entre dos rectángulos con tolerancia
     * @param r1 Primer rectángulo
     * @param r2 Segundo rectángulo
     * @param tolerancia Tolerancia adicional para facilitar la colisión
     * @return true si hay colisión
     */
    public static boolean colisionConTolerancia(Rectangle r1, Rectangle r2, float tolerancia) {
        Rectangle r1Ampliado = new Rectangle(
            r1.x - tolerancia,
            r1.y - tolerancia,
            r1.width + tolerancia * 2,
            r1.height + tolerancia * 2
        );

        return r1Ampliado.overlaps(r2);
    }
}
