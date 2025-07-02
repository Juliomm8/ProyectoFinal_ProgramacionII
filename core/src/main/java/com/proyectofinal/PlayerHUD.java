package com.proyectofinal;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.utils.Disposable;

/**
 * Clase responsable de mostrar la informacion del jugador en pantalla.
 * Se ubica en la esquina superior izquierda y presenta clase, vida,
 * y recursos especiales como flechas, mana o escudo.
 */
public class PlayerHUD implements Disposable {
    private BitmapFont font;
    private final Jugador jugador;
    private GlyphLayout layout;
    private float padding = 10f;
    private com.badlogic.gdx.graphics.glutils.ShapeRenderer shapeRenderer;

    public PlayerHUD(Jugador jugador) {
        this.jugador = jugador;

        // Crear fuente para dibujar el texto
        font = new BitmapFont();
        font.setColor(Color.WHITE);
        font.getData().setScale(1.8f); // Aumentar tamaño de letra

        // Objeto para medir el texto
        layout = new GlyphLayout();

        // Inicializar shapeRenderer para dibujar el fondo
        shapeRenderer = new com.badlogic.gdx.graphics.glutils.ShapeRenderer();
    }

    /**
     * Renderiza el HUD del jugador con clase, vida y recursos especiales.
     * @param batch SpriteBatch donde se dibuja el texto
     */
    public void render(SpriteBatch batch) {
        if (batch == null || jugador == null) return;

        // Construir la informacion a mostrar
        StringBuilder info = new StringBuilder();
        info.append("Clase: ").append(determinarClase()).append("\n");
        info.append("Vida: ")
            .append(jugador.getVida())
            .append("/")
            .append(jugador.getVidaMaxima())
            .append("\n");

        // Agregar datos segun la clase del jugador
        if (jugador instanceof Arquero) {
            Arquero arquero = (Arquero) jugador;
            info.append("Flechas: ").append(arquero.getFlechas()).append("\n");

            // Si tiene modo rafaga activado, mostrar tiempo restante
            if (arquero.estaModoIlimitado()) {
                info.append("Rafaga: ").append(String.format("%.1f", arquero.getTiempoIlimitadoRestante())).append("s");
            }
        } else if (jugador instanceof Mago) {
            Mago mago = (Mago) jugador;
            info.append("Mana: ").append(mago.getMana()).append("\n");
        } else if (jugador instanceof Caballero) {
            Caballero caballero = (Caballero) jugador;
            info.append("Escudo: ").append(caballero.getEscudo()).append("\n");
        }

        // Medir el tamaño del texto
        layout.setText(font, info.toString());

        // Calcular posicion para dibujar (esquina superior izquierda)
        float textY = com.badlogic.gdx.Gdx.graphics.getHeight() - padding;
        float bgWidth = layout.width + padding * 2;
        float bgHeight = layout.height + padding * 2;
        float bgY = textY + padding - layout.height;

        // Guardar color original del batch
        com.badlogic.gdx.graphics.Color oldColor = batch.getColor().cpy();

        // Terminar el batch para poder dibujar el fondo con shapeRenderer
        batch.setColor(0, 0, 0, 0.6f);
        batch.end();

        // Configurar shapeRenderer para dibujar en pantalla completa
        shapeRenderer.setProjectionMatrix(new com.badlogic.gdx.math.Matrix4().setToOrtho2D(
            0, 0,
            com.badlogic.gdx.Gdx.graphics.getWidth(),
            com.badlogic.gdx.Gdx.graphics.getHeight()
        ));
        shapeRenderer.begin(com.badlogic.gdx.graphics.glutils.ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0, 0, 0, 0.6f);
        shapeRenderer.rect(0, bgY, bgWidth, bgHeight);
        shapeRenderer.end();

        // Volver a comenzar el batch
        batch.begin();
        batch.setColor(oldColor);

        // Dibujar el texto en pantalla
        font.draw(batch, info.toString(), padding, textY);
    }

    /**
     * Devuelve el nombre de la clase del jugador como texto.
     * @return Nombre de clase para mostrar en pantalla
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

    /**
     * Libera los recursos usados por el HUD.
     */
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
