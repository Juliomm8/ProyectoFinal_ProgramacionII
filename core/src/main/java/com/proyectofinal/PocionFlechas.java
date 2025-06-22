package com.proyectofinal;

/**
 * Poción que recarga flechas al Arquero.
 */
public class PocionFlechas extends Pocion {
    public PocionFlechas(String nombre, int valor) {
        super(nombre, "Flechas", valor);
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

