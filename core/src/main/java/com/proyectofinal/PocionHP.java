package com.proyectofinal;

/**
 * Poción que restaura HP al personaje.
 */
public class PocionHP extends Pocion {

    public PocionHP(String nombre, int valor) {
        super(nombre, "HP", valor);
    }

    @Override
    public void consumir(Personaje personaje) {
        personaje.recibirDanio(-valor);  // Restaurar salud
        System.out.println(personaje.getNombre() + " ha restaurado " + valor + " HP.");
    }
}
