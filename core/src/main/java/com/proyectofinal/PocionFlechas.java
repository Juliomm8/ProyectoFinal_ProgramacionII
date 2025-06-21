package com.proyectofinal;

/**
 * Poción que recarga flechas al Arquero.
 */
public class PocionFlechas extends Pocion {
    public PocionFlechas(String nombre, int valor) {
        super(nombre, "Flechas", valor);
    }

    @Override
    public void consumir(Personaje personaje) {
        if (personaje instanceof Arquero) {
            Arquero arquero = (Arquero) personaje;
            // Recargar flechas al arquero
            int before = arquero.getFlechas();
            arquero.recargarFlechas(valor);
            System.out.println(arquero.getNombre() + " recarga " + valor + " flechas. Flechas: " + before + " -> " + arquero.getFlechas());
        } else {
            System.out.println(personaje.getNombre() + " no se beneficia de esta poción.");
        }
    }
}
