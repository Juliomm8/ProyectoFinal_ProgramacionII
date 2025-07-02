package com.proyectofinal;

/**
 * Subclase de Jugador especializada en ataques mágicos con mana.
 */
public class Mago extends Jugador implements RecargableInterface {
    private int mana;           // Mana actual del mago
    private int manaMaximo;     // Mana máximo que puede tener
    private int costoHechizo = 20; // Costo por defecto de lanzar un hechizo

    /**
     * Constructor para el Mago.
     * @param nombre Nombre del jugador
     * @param vida Vida inicial (se ajusta luego a la máxima del mago)
     * @param ataque Daño base del mago
     * @param x Posición X inicial
     * @param y Posición Y inicial
     * @param width Ancho del collider
     * @param height Alto del collider
     * @param manaInicial Mana inicial (y también se toma como máximo)
     */
    public Mago(String nombre, int vida, int ataque,
                float x, float y, float width, float height,
                int manaInicial) {
        super(nombre, vida, ataque, x, y, width, height, 0); // Nivel inicial = 0
        this.mana = manaInicial;
        this.manaMaximo = manaInicial;
        this.vidaMaxima = 150; // Vida máxima fija para el mago
        this.vida = vidaMaxima; // Vida actual igual a la máxima al iniciar
    }

    // Getters para mana actual y máximo
    public int getMana() {
        return mana;
    }

    public int getManaMaximo() {
        return manaMaximo;
    }

    /**
     * Recarga mana al mago según la cantidad especificada.
     * No puede superar el mana máximo.
     */
    @Override
    public void recargar(int cantidad) {
        int antes = mana;
        mana = Math.min(mana + cantidad, manaMaximo); // Limita al máximo
        System.out.println(getNombre() + " recupera " + (mana - antes) +
            " puntos de mana. Mana actual: " + mana);
    }

    /**
     * Intenta consumir mana para lanzar un hechizo especial.
     * Si no hay suficiente mana, no lo consume.
     * @param costoHechizoEspecial cantidad de mana a consumir
     */
    public void consumirMana(int costoHechizoEspecial) {
        if (mana >= costoHechizoEspecial) {
            mana -= costoHechizoEspecial;
            System.out.println(getNombre() + " consume " + costoHechizoEspecial + " mana.");
        } else {
            System.out.println(getNombre() + " no tiene suficiente mana para consumir el hechizo especial.");
        }
    }
}
