package com.proyectofinal;

/**
 * Poción que restaura mana al Mago, flechas al Arquero o escudo al Caballero.
 * Se adapta dinamicamente al tipo de personaje que la consume.
 */
public class PocionMana extends Pocion {

    /**
     * Constructor de la pocion de energia.
     * @param nombre Nombre descriptivo de la pocion
     * @param valor Cantidad de recurso a restaurar (mana, flechas o escudo)
     */
    public PocionMana(String nombre, int valor) {
        // Llama al constructor de la clase base con tipo "Energia"
        super(nombre, "Energia", valor, 1);
    }

    /**
     * Metodo que aplica el efecto de la pocion sobre el personaje.
     * Restaura recursos dependiendo del tipo:
     * - Mago: mana
     * - Arquero: flechas
     * - Caballero: escudo
     * @param p Personaje que consume la pocion
     */
    @Override
    public void consumir(Personaje p) {
        // Verifica que el personaje implemente la interfaz RecargableInterface
        if (p instanceof RecargableInterface) {
            // Caso especifico: Mago
            if (p instanceof Mago) {
                Mago mago = (Mago) p;

                // Si ya tiene el mana al maximo, lanzar excepcion
                if (mago.getMana() >= mago.getManaMaximo()) {
                    throw new InvalidPotionException(
                        "No puedes usar una pocion de mana si no has perdido mana.");
                }

                // Registrar mana antes y despues para mostrar en consola
                int antes = mago.getMana();
                ((RecargableInterface) p).recargar(valor);
                System.out.println(p.getNombre() + " ha recargado " + (mago.getMana() - antes)
                    + " de mana. (" + antes + " → " + mago.getMana() + ")");

                // Caso especifico: Arquero
            } else if (p instanceof Arquero) {
                Arquero arquero = (Arquero) p;
                int flechasAntes = arquero.getFlechas();
                ((RecargableInterface) p).recargar(valor);
                int flechasDespues = arquero.getFlechas();

                // Mostrar cuantas flechas se recargaron
                System.out.println(p.getNombre() + " ha recargado " + (flechasDespues - flechasAntes)
                    + " flechas. (" + flechasAntes + " → " + flechasDespues + ")");

                // Caso general: Caballero u otro tipo recargable
            } else {
                ((RecargableInterface) p).recargar(valor);
                System.out.println(p.getNombre() + " ha recargado " + valor + " de energia.");
            }

        } else {
            // Si el personaje no puede recargar recursos, se informa al jugador
            System.out.println(p.getNombre() + " no se beneficia de esta pocion.");
        }
    }
}
