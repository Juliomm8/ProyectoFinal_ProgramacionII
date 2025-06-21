package com.proyectofinal;

/**
 * Poción que restaura Mana al personaje.
 */
public class PocionMana extends Pocion {

    public PocionMana(String nombre, int valor) {
        super(nombre, "Mana", valor);
    }

    @Override
    public void consumir(Personaje personaje) {
        // Aquí agregarías lógica para restaurar el mana
        // Si tienes un campo mana en Personaje, lo incrementarías
        System.out.println(personaje.getNombre() + " ha restaurado " + valor + " Mana.");
    }
}
