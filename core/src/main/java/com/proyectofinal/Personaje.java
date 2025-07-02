package com.proyectofinal;

/**
 * Clase base para personajes con capacidad de atacar, recibir daño y curarse.
 * Es una clase abstracta porque representa un concepto genérico de personaje.
 */
public abstract class Personaje implements AtacanteInterface, DaniableInterface {
    // Nombre del personaje (por ejemplo: "Caballero", "Mago")
    protected String nombre;

    // Puntos de vida actuales
    protected int vida;

    // Valor base de ataque (daño que puede infligir)
    protected int ataque;

    // Vida máxima que puede tener el personaje (usado para curaciones o validaciones)
    protected int vidaMaxima;

    /**
     * Constructor base para inicializar los atributos principales.
     * @param nombre nombre del personaje
     * @param vida puntos de vida iniciales
     * @param ataque poder de ataque base
     */
    public Personaje(String nombre, int vida, int ataque) {
        this.nombre = nombre;
        this.vida = vida;
        this.ataque = ataque;
    }

    // Devuelve el nombre del personaje
    public String getNombre() { return nombre; }

    // Devuelve los puntos de vida actuales
    public int getVida() { return vida; }

    // Devuelve la vida máxima
    public int getVidaMaxima() { return vidaMaxima; }

    /**
     * Aplica daño al personaje restando puntos de vida.
     * Implementado desde DaniableInterface.
     */
    @Override
    public void recibirDanio(int cantidad) {
        vida -= cantidad;
        System.out.println(nombre + " recibe " + cantidad + " de daño. Vida actual: " + vida);
    }

    /**
     * Realiza un ataque y devuelve el valor del daño causado.
     * Implementado desde AtacanteInterface.
     */
    @Override
    public int atacar() {
        System.out.println(nombre + " ataca con daño base " + ataque);
        return ataque;
    }
}
