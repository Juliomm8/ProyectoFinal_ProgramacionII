package com.proyectofinal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Disposable;

/**
 * Clase responsable de mostrar la información del jugador en la esquina superior izquierda
 * como su clase, vida y flechas (si es arquero).
 */
public class PlayerHUD implements Disposable {
    private BitmapFont font;
    private final Jugador jugador;
    private GlyphLayout layout;
    private float padding = 10f;
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    public PlayerHUD(Jugador jugador) {
        this.jugador = jugador;
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        // Aumentar el tamaño del texto para mejor visualización
        font.getData().setScale(1.8f);
        layout = new GlyphLayout();
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
    }

    /**
     * Renderiza la información del jugador en la esquina superior izquierda.
     * @param batch SpriteBatch para dibujar
     */
    public void render(SpriteBatch batch) {
        if (batch == null || jugador == null) return;

        // Determinar tipo de jugador y mostrar información relevante
        StringBuilder info = new StringBuilder();
        info.append("Clase: ").append(determinarClase()).append("\n");
        info.append("Vida: ")
            .append(jugador.getVida())
            .append("/")
            .append(jugador.getVidaMaxima())
            .append("\n");

        // Mostrar información específica según el tipo de personaje
        if (jugador instanceof Arquero) {
            Arquero arquero = (Arquero) jugador;
            info.append("Flechas: ").append(arquero.getFlechas()).append("\n");

            // Si está en modo ráfaga ilimitada, mostrar tiempo restante
            if (arquero.estaModoIlimitado()) {
                info.append("Ráfaga: ").append(String.format("%.1f", arquero.getTiempoIlimitadoRestante())).append("s");
            }
        } else if (jugador instanceof Mago) {
            // Añadir información de maná para el Mago
            Mago mago = (Mago) jugador;
            info.append("Maná: ").append(mago.getMana()).append("\n");
        } else if (jugador instanceof Caballero) {
            // Añadir información de escudo para el Caballero
            Caballero caballero = (Caballero) jugador;
            info.append(caballero.getEscudo()).append("\n");
        }

        // Medir el texto para determinar el tamaño
        layout.setText(font, info.toString());

        // Calcular posición en la esquina superior izquierda
        // Asegurarnos que el texto se dibuja desde arriba (en la esquina superior)
        float textY = com.badlogic.gdx.Gdx.graphics.getHeight() - padding;

        // Calcular dimensiones del fondo
        float bgWidth = layout.width + padding * 2;
        float bgHeight = layout.height + padding * 2;
        float bgY = textY + padding - layout.height;

        // Guardar el color actual del batch
        com.badlogic.gdx.graphics.Color oldColor = batch.getColor().cpy();

        // Dibujar fondo semi-transparente para mejorar legibilidad
        batch.setColor(0, 0, 0, 0.6f);
        batch.end();

        shapeRenderer.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(0, 0,
                                    com.badlogic.gdx.Gdx.graphics.getWidth(),
                                    com.badlogic.gdx.Gdx.graphics.getHeight()));
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.6f);
        shapeRenderer.rect(0, bgY, bgWidth, bgHeight);
        shapeRenderer.end();

        batch.begin();
        batch.setColor(oldColor);

        // Dibujar el texto directamente en la posición fija
        font.draw(batch, info.toString(), padding, textY);
    }

    /**
     * Determina la clase del jugador para mostrarla en el HUD.
     * @return String con el nombre de la clase
     */
    private String determinarClase() {
        if (jugador instanceof Arquero) {
            return "Arquero";
        } else if (jugador.getClass().getSimpleName().equals("Mago")) {
            return "Mago";
        } else if (jugador.getClass().getSimpleName().equals("Caballero")) {
            return "Caballero";
        } else {
            return "Desconocido";
        }
    }

    @Override
    public void dispose() {
        if (font != null) {
            font.dispose();
            font = null;
        }
        if (shapeRenderer != null) {
            shapeRenderer.dispose();
            shapeRenderer = null;
        }
    }
}
