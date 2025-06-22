package com.proyectofinal;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Actor que dibuja al jugador en pantalla, provee getBounds() para colisiones
 * y dibuja su HUD (vida y recurso).
 */
public class PlayerActor extends Image {
    private final Jugador jugador;

    public PlayerActor(Jugador jugador, Texture texture) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.jugador = jugador;
        setSize(32, 32);
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }

    /**
     * Dibuja el HUD del jugador en la esquina superior izquierda:
     * Clase, Vida y su recurso (Mana/Escudo/Flechas).
     */
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
}
