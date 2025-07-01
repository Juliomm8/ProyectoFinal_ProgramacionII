package com.proyectofinal;

/**
 * Clase base para las pociones del juego.
 * Representa los atributos y comportamiento común de todas las pociones.
 */
public abstract class Pocion {
    protected String nombre;
    protected String tipo;  // Ejemplo: "HP", "EXP", "Mana"
    protected int valor;     // La cantidad de HP, EXP o Mana que otorga
    private int cantidad;

    /**
     * Constructor base para todas las pociones.
     * @param nombre Nombre descriptivo de la poción
     * @param tipo Tipo de poción (HP, Mana, etc.)
     * @param valor Cantidad de efecto que aplica
     * @param cantidad Número de usos de la poción
     */
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
