package com.proyectofinal;

/**
 * Poción que restaura mana al Mago o escudo al Caballero.
 */
public class PocionMana extends Pocion {

    public PocionMana(String nombre, int valor) {
        super(nombre, "Mana", valor);
    }

    @Override
    public void consumir(Personaje personaje) {
        if (personaje instanceof Mago) {
            Mago m = (Mago) personaje;
            m.recargarMana(valor);
        } else if (personaje instanceof Caballero) {
            Caballero c = (Caballero) personaje;
            c.agregarEscudo(valor);
        } else {
            System.out.println(personaje.getNombre() + " no se beneficia de esta poción.");
        }
    }
}
