package com.proyectofinal;

/**
 * Clase base para personajes con capacidad de atacar y recibir daño.
 */

public abstract class Personaje implements AtacanteInterface, DaniableInterface {
    protected String nombre;
    protected int vida;
    protected int ataque;

    public Personaje(String nombre, int vida, int ataque) {
        this.nombre = nombre;
        this.vida = vida;
        this.ataque = ataque;
    }

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    @Override
    public void recibirDanio(int cantidad) {
        vida -= cantidad;
        System.out.println(nombre + " recibe " + cantidad + " de daño. \nVida actual: " + vida);

    }

    @Override
    public int atacar() {
        System.out.println(nombre + " ataca con daño base " + ataque);
        return ataque;
    }
}
