package com.proyectofinal;

/**
 * Poción que restaura escudo al caballero.
 */


public class PocionEscudo extends Pocion {

    public PocionEscudo(String nombre, int valor) {
        super(nombre, "Escudo", valor, 1);
    }
    @Override
    public void consumir(Personaje personaje) {
        if (personaje instanceof Caballero caballero) {
            caballero.recargar(valor);
            System.out.println(personaje.getNombre() + " ha recuperado " + valor + " puntos de escudo.");
        } else {
            System.out.println(personaje.getNombre() + " no puede usar esta poción de escudo.");
        }
    }

}

