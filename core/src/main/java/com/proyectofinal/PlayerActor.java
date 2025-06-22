package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * Actor que dibuja al jugador en pantalla y provee getBounds() para colisiones.
 */
public class PlayerActor extends Image {
    private final Jugador jugador;

    public PlayerActor(Jugador jugador, Texture texture) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.jugador = jugador;
        setSize(32,32);  // igual que tu grid
    }

    public Jugador getJugador() {
        return jugador;
    }

    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
}
