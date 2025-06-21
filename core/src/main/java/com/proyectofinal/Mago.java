package com.proyectofinal;

/**
 * Subclase de Jugador especializada en hechizos con dos ataques y recarga de mana.
 */
public class Mago extends Jugador {
    private int mana;

    public Mago(String nombre, int vida, int ataque, int mana) {
        super(nombre, vida, ataque);
        this.mana = mana;
    }

    public int getMana() {
        return mana;
    }

    /**
     * Restaura mana al mago.
     */
    public void recargarMana(int cantidad) {
        mana += cantidad;
        System.out.println(getNombre() + " recupera " + cantidad + " de mana. Mana actual: " + mana);
    }

    /**
     * Ataque básico: hechizo simple sin coste de mana.
     */
    public void ataque1() {
        System.out.println(getNombre() + " lanza hechizo básico sin consumir mana.");
    }

    /**
     * Ataque secundario: hechizo poderoso de área que consume mana.
     */
    public void ataque2() {
        int cost = 10;
        if (mana >= cost) {
            mana -= cost;
            System.out.println(getNombre() + " lanza hechizo de área, consume " + cost + " mana. Mana restante: " + mana);
            // Aquí se aplicaría el efecto de área a enemigos cercanos
        } else {
            System.out.println(getNombre() + " no tiene suficiente mana para hechizo de área.");
        }
    }
}
