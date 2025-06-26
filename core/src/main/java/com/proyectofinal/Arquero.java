package com.proyectofinal;

/**
 * Subclase de Jugador especializada en disparos con flechas limitadas y ráfaga ilimitada.
 */
public class Arquero extends Jugador implements RecargableInterface {
    private float precision;
    private int flechas;
    private boolean modoIlimitado;
    private float tiempoIlimitado;


    public Arquero(String nombre, int vida, int ataque, float x, float y, float width, float height, int flechasIniciales, float precision) {
        super(nombre, vida, ataque, x, y, width, height, flechasIniciales);  // Llamada al constructor de Jugador
        this.precision = precision;
        this.flechas = flechasIniciales;
        this.modoIlimitado = false;
        this.tiempoIlimitado = 0;
    }

    public float getPrecision() {
        return precision;
    }

    public int getFlechas() {
        return flechas;
    }

    /**
     * Ataque básico: dispara flecha y consume flecha si no está en ráfaga ilimitada.
     */
    public void ataque1() {
        if (modoIlimitado || flechas > 0) {
            if (!modoIlimitado) flechas--;
            System.out.println(getNombre() + " dispara flecha con precisión " + precision +
                ". Flechas restantes: " + flechas);
        } else {
            System.out.println(getNombre() + " no tiene flechas.");
        }
    }

    /**
     * Recarga flechas al arquero.
     */
    @Override
    public void recargar(int cantidad) {
        int antes = flechas;
        flechas += cantidad;
        System.out.println(getNombre()
            + " recarga " + cantidad + " flechas. Flechas: " + antes + " → " + flechas);
    }

    /**
     * Activa ráfaga de flechas ilimitadas durante segundos especificados.
     */
    public void activarRafagaIlimitada(float duracionSegundos) {
        modoIlimitado = true;
        tiempoIlimitado = duracionSegundos;
        System.out.println(getNombre() + " activa ráfaga ilimitada por " + duracionSegundos + "s.");
    }

    /**
     * Actualiza temporizador de ráfaga ilimitada; llamar cada frame con delta.
     */
    public void actualizar(float delta) {
        if (modoIlimitado) {
            tiempoIlimitado -= delta;
            if (tiempoIlimitado <= 0) {
                modoIlimitado = false;
                System.out.println(getNombre() + " termina ráfaga ilimitada.");
            }
        }
    }
}
