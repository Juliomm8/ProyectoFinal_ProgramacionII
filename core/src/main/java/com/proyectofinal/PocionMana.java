package com.proyectofinal;

/**
 * Poción que restaura mana al Mago, flechas al Arquero o escudo al Caballero.
 * Se adapta al tipo de personaje que la consume.
 */
public class PocionMana extends Pocion {

    /**
     * Constructor de la poción de energía/mana.
     * @param nombre Nombre descriptivo de la poción
     * @param valor Cantidad de mana/flechas/escudo que restaura
     */
    public PocionMana(String nombre, int valor) {
        super(nombre, "Energía", valor, 1);
    }

    /**
     * Consume la poción y restaura el recurso correspondiente al personaje.
     * - Para Arquero: restaura flechas
     * - Para Mago: restaura mana
     * - Para Caballero: restaura escudo
     * @param p Personaje que consume la poción
     */
    @Override
    public void consumir(Personaje p) {
        if (p instanceof RecargableInterface) {
            // Mensaje específico según la clase
            if (p instanceof Arquero) {
                Arquero arquero = (Arquero) p;
                int flechasAntes = arquero.getFlechas();
                ((RecargableInterface)p).recargar(valor);
                int flechasDespues = arquero.getFlechas();

                System.out.println(p.getNombre() + " ha recargado " + (flechasDespues - flechasAntes) +
                                 " flechas. (" + flechasAntes + " → " + flechasDespues + ")");
            } else {
                // Para otros tipos recargables (Mago, Caballero)
                ((RecargableInterface)p).recargar(valor);
                System.out.println(p.getNombre() + " ha recargado " + valor + " de energía.");
            }
        } else {
            System.out.println(p.getNombre() + " no se beneficia de esta poción.");
        }
    }
}
