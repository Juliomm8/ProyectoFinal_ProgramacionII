package com.proyectofinal;

/**
 * Subclase de Jugador especializada en combate cuerpo a cuerpo con escudo.
 */
public class Caballero extends Jugador {
    private int escudo;

    public Caballero(String nombre, int vida, int ataque) {
        super(nombre, vida, ataque);
        this.escudo = 0;
    }

    public int getEscudo() {
        return escudo;
    }

    /**
     * Ataque básico: golpe con espada.
     */
    public void ataque1() {
        super.atacar();
    }

    /**
     * Añade escudo al caballero.
     */
    public void agregarEscudo(int valor) {
        escudo += valor;
        System.out.println(getNombre() + " gana " + valor + " puntos de escudo. Escudo actual: " + escudo);
    }

    @Override
    public void recibirDanio(int cantidad) {
        if (escudo >= cantidad) {
            escudo -= cantidad;
            System.out.println(getNombre() + " bloquea " + cantidad + " de daño con escudo. Escudo restante: " + escudo);
        } else {
            int resto = cantidad - escudo;
            escudo = 0;
            super.recibirDanio(resto);
        }
    }
}
