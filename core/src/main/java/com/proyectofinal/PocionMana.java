package com.proyectofinal;

/**
 * Poción que restaura mana al Mago o escudo al Caballero.
 */
public class PocionMana extends Pocion {
    public PocionMana(String nombre, int valor) {
        super(nombre, "Mana", valor, 1);
    }

    @Override
    public void consumir(Personaje p) {
        if (p instanceof RecargableInterface) {
            ((RecargableInterface)p).recargar(valor);
        } else {
            System.out.println(p.getNombre() + " no se beneficia de esta poción.");
        }
    }
}
