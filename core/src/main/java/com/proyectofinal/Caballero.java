package com.proyectofinal;

/**
 * Subclase de Jugador especializada en combate cuerpo a cuerpo con escudo.
 */
public class Caballero extends Jugador implements RecargableInterface{
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
    @Override
    public void recargar(int cantidad) {
        escudo += cantidad;
        System.out.println(getNombre() + " gana " + cantidad + " puntos de escudo. Escudo actual: " + escudo);
    }

    @Override
    public void recibirDanio(int cantidad) {
        if (escudo >= cantidad) {
            escudo -= cantidad;
        } else {
            int resto = cantidad - escudo;
            escudo = 0;
            super.recibirDanio(resto);
        }
    }
}
