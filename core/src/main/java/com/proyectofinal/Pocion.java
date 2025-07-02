package com.proyectofinal;

/**
 * Clase base abstracta para todas las pociones del juego.
 * Define atributos comunes como nombre, tipo, valor y cantidad.
 * Todas las subclases deben implementar el metodo consumir().
 */
public abstract class Pocion {
    protected String nombre;     // Nombre de la pocion (ej: "Pocion de Vida")
    protected String tipo;       // Tipo de efecto (ej: "HP", "EXP", "Mana")
    protected int valor;         // Cantidad que restaura o aplica (ej: +20 HP)
    private int cantidad;        // Numero de usos disponibles para la pocion

    /**
     * Constructor base para inicializar una pocion.
     * @param nombre Nombre que se mostrara en la interfaz
     * @param tipo Tipo de recurso que afecta (HP, Mana, etc.)
     * @param valor Cantidad de efecto aplicado al personaje
     * @param cantidad Veces que puede usarse esta pocion
     */
    public Pocion(String nombre, String tipo, int valor, int cantidad) {
        this.nombre = nombre;
        this.tipo = tipo;
        this.valor = valor;
        this.cantidad = cantidad;
    }

    // Retorna el nombre de la pocion
    public String getNombre() {
        return nombre;
    }

    // Retorna el tipo (HP, Mana, etc.)
    public String getTipo() {
        return tipo;
    }

    // Retorna el valor (cantidad de efecto)
    public int getValor() {
        return valor;
    }

    // Retorna la cantidad de usos restantes
    public int getCantidad() {
        return cantidad;
    }

    /**
     * Metodo abstracto que debe implementar cada subtipo de pocion.
     * Aplica el efecto de la pocion al personaje indicado.
     * @param personaje El personaje que recibe el efecto
     */
    public abstract void consumir(Personaje personaje);
}
