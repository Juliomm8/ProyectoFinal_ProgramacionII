package com.proyectofinal;

/**
 * Clase base para las pociones del juego.
 */

public abstract class Pocion {
    protected String nombre;
    protected String tipo;  // Ejemplo: "HP", "EXP", "Mana"
    protected int valor;     // La cantidad de HP, EXP o Mana que otorga
    private int cantidad;

    public Pocion(String nombre, String tipo, int valor, int cantidad) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
        this.cantidad = cantidad;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public int getValor() {
        return valor;
    }

    public int getCantidad() {
        return cantidad;
    }

    // Método abstracto que será implementado por cada tipo de poción
    public abstract void consumir(Personaje personaje);
}
