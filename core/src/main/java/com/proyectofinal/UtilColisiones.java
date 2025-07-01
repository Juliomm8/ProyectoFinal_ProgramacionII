package com.proyectofinal;

import com.badlogic.gdx.math.Rectangle;

/**
 * Clase de utilidad para facilitar la detección de colisiones
 */
public class UtilColisiones {

    /**
     * Comprueba si un punto está dentro de un rectángulo
     * @param x Coordenada X del punto
     * @param y Coordenada Y del punto
     * @param rect Rectángulo a comprobar
     * @return true si el punto está dentro del rectángulo
     */
    public static boolean puntoEnRectangulo(float x, float y, Rectangle rect) {
        return x >= rect.x && x <= rect.x + rect.width &&
               y >= rect.y && y <= rect.y + rect.height;
    }

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

    /**
     * Imprime información de depuración sobre un rectángulo
     * @param nombre Nombre del rectángulo para identificarlo
     * @param rect Rectángulo a imprimir
     */
    public static void imprimirRectangulo(String nombre, Rectangle rect) {
        System.out.println(nombre + ": x=" + rect.x + ", y=" + rect.y +
                          ", width=" + rect.width + ", height=" + rect.height);
    }
}
