package com.proyectofinal;

/**
 * Poción que otorga experiencia al personaje.
 */
public class PocionEXP extends Pocion {

    public PocionEXP(String nombre, int valor) {
        super(nombre, "EXP", valor, 1);
    }

    @Override
    public void consumir(Personaje personaje) {
        if (personaje instanceof Jugador) {
            ((Jugador) personaje).subirNivel();
            System.out.println(personaje.getNombre() + " ha ganado " + valor + " EXP.");
        } else {
            System.out.println(personaje.getNombre() + " no puede recibir EXP.");
        }
    }

}
