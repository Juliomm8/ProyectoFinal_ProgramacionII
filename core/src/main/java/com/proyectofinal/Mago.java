package com.proyectofinal;

/**
 * Subclase de Jugador especializada en ataques mágicos con mana.
 */
public class Mago extends Jugador implements RecargableInterface {
    private int mana;
    private int manaMaximo;
    private int costoHechizo = 20;

    public Mago(String nombre, int vida, int ataque,
                float x, float y, float width, float height,
                int manaInicial) {
        super(nombre, vida, ataque, x, y, width, height, 0);
        this.mana = manaInicial;
        this.manaMaximo = manaInicial;
        this.vidaMaxima = 150;
        this.vida = vidaMaxima;
    }

    public int getMana() {
        return mana;
    }
    public int getManaMaximo() {
        return manaMaximo;
    }


    /**
     * Recarga mana al mago.
     */
    @Override
    public void recargar(int cantidad) {
        int antes = mana;
        mana = Math.min(mana + cantidad, manaMaximo);
        System.out.println(getNombre() + " recupera " + (mana - antes) +
            " puntos de mana. Mana actual: " + mana);
    }

    public void consumirMana(int costoHechizoEspecial) {
        if (mana >= costoHechizoEspecial) {
            mana -= costoHechizoEspecial;
            System.out.println(getNombre() + " consume " + costoHechizoEspecial + " mana.");
        } else {
            System.out.println(getNombre() + " no tiene suficiente mana para consumir el hechizo especial.");
        }
    }
}
