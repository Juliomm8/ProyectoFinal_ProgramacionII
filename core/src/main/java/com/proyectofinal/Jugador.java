package com.proyectofinal;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa al jugador con inventario de pociones y nivel.
 */
public class Jugador extends Personaje {
    private List<Pocion> inventario;
    private int nivel;

    public Jugador(String nombre, int vida, int ataque) {
        super(nombre, vida, ataque);
        this.inventario = new ArrayList<>();
        this.nivel = 1;
    }

    public int getNivel() {
        return nivel;
    }

    public void recogerPocion(Pocion pocion) {
        inventario.add(pocion);
        System.out.println(nombre + " recogió " + pocion.getNombre());
    }

    public void usarPocion(Pocion pocion) {
        if (inventario.remove(pocion)) {
            pocion.consumir(this);
        }
    }

    public void subirNivel() {
        nivel++;
        System.out.println(nombre + " sube al nivel " + nivel);
    }
}
