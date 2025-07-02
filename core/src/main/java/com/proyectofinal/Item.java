package com.proyectofinal;

/**
 * Representa un objeto equipable por el jugador.
 * Define requisitos de clase y nivel para ser equipado.
 */
public class Item {
    private final String nombre;
    private final String claseRequerida;
    private final int nivelRequerido;

    public Item(String nombre, String claseRequerida, int nivelRequerido) {
        this.nombre = nombre;
        this.claseRequerida = claseRequerida;
        this.nivelRequerido = nivelRequerido;
    }

    /** Verifica si el jugador cumple los requisitos para este objeto. */
    public boolean cumpleRequisitos(Jugador jugador) {
        boolean claseOk = claseRequerida == null
            || jugador.getClass().getSimpleName().equals(claseRequerida);
        boolean nivelOk = jugador.getNivel() >= nivelRequerido;
        return claseOk && nivelOk;
    }

    public String getNombre() {
        return nombre;
    }
}
