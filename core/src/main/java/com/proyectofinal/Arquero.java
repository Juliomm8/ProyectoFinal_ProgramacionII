// Arquero.java
package com.proyectofinal;

/**
 * Subclase de Jugador especializada en disparos.
 */
public class Arquero extends Jugador implements EquipableInterface {
    private float precision;

    public Arquero(String nombre, int vida, int ataque, float precision) {
        super(nombre, vida, ataque);
        this.precision = precision;
    }

    public float getPrecision() {
        return precision;
    }

    @Override
    public void equipar() {
        System.out.println(getNombre() + " equipa su arco con precisión " + precision);
    }

    /**
     * Acción del arquero para disparar.
     */
    public void dispararFlecha() {
        System.out.println(getNombre() + " dispara una flecha con precisión " + precision);
    }
}
