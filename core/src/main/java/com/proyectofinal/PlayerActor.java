package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;

import java.util.List;

/**
 * Actor que dibuja al jugador en pantalla, provee getBounds() para colisiones
 * y dibuja su HUD (vida y recurso), además de manejar la animación de ataque.
 */
public class PlayerActor extends Image {
    private final Jugador jugador;
    private boolean atacando = false;
    private boolean impactoHecho = false;
    private float tiempoAnimacion = 0f;
    private int frameAnimActual = 0;

    private TextureRegion[] ataqueCaballeroFrames;
    private final float frameDuration = 0.07f; // segundos por frame
    private final int impactFrame = 4; // frame donde ocurre el golpe

    public PlayerActor(Jugador jugador, Texture texture) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.jugador = jugador;
        setSize(32, 32);

        if (jugador instanceof Caballero) {
            ataqueCaballeroFrames = new TextureRegion[9];
            for (int i = 0; i < ataqueCaballeroFrames.length; i++) {
                ataqueCaballeroFrames[i] = new TextureRegion(
                    new Texture("PersonajesPrincipales/Caballero/Caballero_Attack1/Attack1_" + i + ".png")
                );
            }
        }
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    public void dibujarHUD(SpriteBatch batch, BitmapFont font) {
        float x = 20;
        float y = Gdx.graphics.getHeight() - 20;
        font.draw(batch, "Clase: " + jugador.getClass().getSimpleName(), x, y);
        y -= 20;
        font.draw(batch, "Vida: " + jugador.getVida(), x, y);
        y -= 20;

        if (jugador instanceof Mago) {
            font.draw(batch, "Mana: " + ((Mago) jugador).getMana(), x, y);
        } else if (jugador instanceof Caballero) {
            font.draw(batch, "Escudo: " + ((Caballero) jugador).getEscudo(), x, y);
        } else if (jugador instanceof Arquero) {
            font.draw(batch, "Flechas: " + ((Arquero) jugador).getFlechas(), x, y);
        }
    }

    /**
     * Debe llamarse cada frame (por ejemplo en DungeonScreen.render) para manejar
     * input y animación de ataque.
     */
    public void update(float delta, List<? extends Enemigo> enemigosEnPantalla) {
        if (!(jugador instanceof Caballero)) return;
        Caballero cab = (Caballero) jugador;

        // Input de ataque
        if (!atacando && Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            if (cab.puedeAtacar()) {
                cab.registrarAtaque();
                atacando = true;
                tiempoAnimacion = 0f;
                frameAnimActual = 0;
                impactoHecho = false;
                System.out.println("INICIANDO ATAQUE");
            }
        }

        // Animación en progreso
        if (atacando) {
            tiempoAnimacion += delta;
            int frame = (int) (tiempoAnimacion / frameDuration);
            if (frame >= ataqueCaballeroFrames.length) {
                atacando = false;
                System.out.println("Animación de ataque terminada, permitiendo nuevo ataque");
            } else {
                frameAnimActual = frame;
                if (frame == impactFrame && !impactoHecho) {
                    cab.atacar(enemigosEnPantalla);
                    impactoHecho = true;
                }
            }
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        if (jugador instanceof Caballero && atacando && ataqueCaballeroFrames != null) {
            batch.draw(
                ataqueCaballeroFrames[frameAnimActual],
                getX(), getY(), getWidth(), getHeight()
            );
        } else {
            super.draw(batch, parentAlpha);
        }
    }
}
