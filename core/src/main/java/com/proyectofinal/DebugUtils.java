package com.proyectofinal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

/**
 * Clase de utilidad para depuración visual
 */
public class DebugUtils {
    private static ShapeRenderer shapeRenderer;
    private static boolean debugEnabled = true;

    /**
     * Inicializa el renderizador de formas
     */
    public static void init() {
        if (shapeRenderer == null) {
            shapeRenderer = new ShapeRenderer();
        }
    }

    /**
     * Activa o desactiva el modo de depuración
     * @param enabled true para activar, false para desactivar
     */
    public static void setDebugEnabled(boolean enabled) {
        debugEnabled = enabled;
    }

    /**
     * Dibuja un rectángulo de depuración
     * @param rect Rectángulo a dibujar
     * @param color Color del rectángulo
     */
    public static void drawDebugRect(Rectangle rect, Color color) {
        if (!debugEnabled || shapeRenderer == null) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.rect(rect.x, rect.y, rect.width, rect.height);
        shapeRenderer.end();
    }

    /**
     * Dibuja una línea de depuración
     * @param x1 Coordenada X inicial
     * @param y1 Coordenada Y inicial
     * @param x2 Coordenada X final
     * @param y2 Coordenada Y final
     * @param color Color de la línea
     */
    public static void drawDebugLine(float x1, float y1, float x2, float y2, Color color) {
        if (!debugEnabled || shapeRenderer == null) return;

        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);
        shapeRenderer.setColor(color);
        shapeRenderer.line(x1, y1, x2, y2);
        shapeRenderer.end();
    }

    /**
     * Libera los recursos utilizados
     */
    public static void dispose() {
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
    }
}
