package com.proyectofinal;

import java.util.List;

/**
 * Subclase de Jugador especializada en disparos con flechas limitadas y ráfaga ilimitada.
 */
public class Arquero extends Jugador implements RecargableInterface {
    private float precision;
    private int flechas;
    private boolean modoIlimitado;
    private float tiempoIlimitado;


    public Arquero(String nombre, int vida, int ataque, float x, float y, float width, float height, int flechasIniciales, float precision) {
        super(nombre, vida, ataque, x, y, width, height, 1);  // Llamada al constructor de Jugador
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
     * Devuelve si el arquero está en modo de ráfaga ilimitada.
     * @return true si está en modo ilimitado, false en caso contrario
     */
    public boolean estaModoIlimitado() {
        return modoIlimitado;
    }

    /**
     * Ataque básico: dispara flecha y consume flecha si no está en ráfaga ilimitada.
     * Utiliza la lógica común del método atacar.
     * @return true si se pudo realizar el ataque, false si no hay flechas disponibles
     */
    public boolean ataque1() {
        return atacar(null); // Pasar null ya que no necesitamos la lista de enemigos aquí
    }

    /**
     * Método de ataque que recibe lista de enemigos.
     * La lógica de impacto se maneja en FlechaActor.
     *
     * @param enemigos Lista de enemigos que podrían ser impactados
     * @return
     */
    public boolean atacar(List<? extends Enemigo> enemigos) {
        // Verificar si podemos disparar
        if (modoIlimitado || flechas > 0) {
            // Consumir flecha si no estamos en modo ilimitado
            if (!modoIlimitado) {
                flechas--;
            }
            System.out.println(getNombre() + " dispara flecha con precisión " + precision +
                ". Flechas restantes: " + flechas);

            // Aquí el PlayerActor se encargará de generar la flecha física
            // y gestionar su colisión con los enemigos
            // FlechaActor manejará la lógica de impacto
            return true; // Ataque exitoso
        } else {
            System.out.println(getNombre() + " no tiene flechas disponibles.");
            return false; // No se pudo atacar
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
     * Obtiene el tiempo restante de ráfaga ilimitada.
     * @return tiempo en segundos, 0 si no está en modo ilimitado
     */
    public float getTiempoIlimitadoRestante() {
        return modoIlimitado ? tiempoIlimitado : 0f;
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
