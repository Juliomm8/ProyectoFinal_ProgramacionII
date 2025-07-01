package com.proyectofinal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

/**
 * Clase de utilidad para depuración visual.
 */
public class DebugUtils {
    private static ShapeRenderer shapeRenderer;
    private static boolean debugEnabled = true;
    private static boolean hitboxEnabled = true; // Para mostrar hitboxes

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
     * Activa o desactiva la visualización de hitboxes
     * @param enabled true para activar, false para desactivar
     */
    public static void setHitboxEnabled(boolean enabled) {
        hitboxEnabled = enabled;
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
     * Dibuja hitboxes de personajes para depuración
     * @param player El jugador
     * @param enemigos Lista de enemigos
     */
    public static void drawHitboxes(Jugador player, List<? extends Enemigo> enemigos) {
        if (!hitboxEnabled || shapeRenderer == null) return;

        // Iniciar renderizado
        shapeRenderer.begin(ShapeRenderer.ShapeType.Line);

        // Dibujar hitbox del jugador
        shapeRenderer.setColor(Color.GREEN);
        Rectangle playerRect = player.getCollider();
        shapeRenderer.rect(playerRect.x, playerRect.y, playerRect.width, playerRect.height);

        // Dibujar hitboxes de enemigos
        shapeRenderer.setColor(Color.RED);
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                Rectangle enemyRect = e.getHitbox();
                shapeRenderer.rect(enemyRect.x, enemyRect.y, enemyRect.width, enemyRect.height);
            }
        }

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
