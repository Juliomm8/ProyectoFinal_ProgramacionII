package com.proyectofinal;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;

public class PocionActor extends Image {
    private final Pocion pocion;

    public PocionActor(Pocion pocion, Texture texture) {
        super(new TextureRegionDrawable(new TextureRegion(texture)));
        this.pocion = pocion;
        setSize(32,32);  // ajusta si necesitas otro tamaño
    }

    public Pocion getPocion() {
        return pocion;
    }

    /** Retorna el rectángulo de colisión de esta poción */
    public Rectangle getBounds() {
        return new Rectangle(getX(), getY(), getWidth(), getHeight());
    }
}
