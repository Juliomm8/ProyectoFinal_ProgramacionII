package com.proyectofinal;

import com.badlogic.gdx.math.Rectangle;

/**
 * Clase de utilidad para facilitar la deteccion de colisiones
 * entre objetos del juego mediante rectangulos (hitboxes).
 */
public class UtilColisiones {

    /**
     * Crea un rectangulo de colision (hitbox) con un tamaño reducido o ampliado,
     * dependiendo del factor de escala dado.
     *
     * @param x Coordenada X del objeto
     * @param y Coordenada Y del objeto
     * @param width Ancho original del objeto
     * @param height Alto original del objeto
     * @param factor Factor de escala para el hitbox (ej: 0.8 = 80% del tamaño original)
     * @return Rectangulo ajustado que puede usarse como hitbox
     */
    public static Rectangle crearHitbox(float x, float y, float width, float height, float factor) {
        // Calcular nuevo tamaño basado en el factor
        float nuevoAncho = width * factor;
        float nuevoAlto = height * factor;

        // Calcular el desplazamiento para centrar el nuevo hitbox
        float offsetX = (width - nuevoAncho) / 2;
        float offsetY = (height - nuevoAlto) / 2;

        // Retornar el nuevo rectangulo centrado
        return new Rectangle(x + offsetX, y + offsetY, nuevoAncho, nuevoAlto);
    }

    /**
     * Verifica si dos rectangulos colisionan, pero aplicando una tolerancia adicional
     * al primero para facilitar la recogida o deteccion.
     *
     * @param r1 Primer rectangulo (se ampliara con la tolerancia)
     * @param r2 Segundo rectangulo (normal)
     * @param tolerancia Margen adicional aplicado a r1 en todas las direcciones
     * @return true si r1 (ampliado) colisiona con r2
     */
    public static boolean colisionConTolerancia(Rectangle r1, Rectangle r2, float tolerancia) {
        // Expandir el primer rectangulo con la tolerancia dada
        Rectangle r1Ampliado = new Rectangle(
            r1.x - tolerancia,
            r1.y - tolerancia,
            r1.width + tolerancia * 2,
            r1.height + tolerancia * 2
        );

        // Verificar si hay interseccion entre r1 ampliado y r2
        return r1Ampliado.overlaps(r2);
    }
}
