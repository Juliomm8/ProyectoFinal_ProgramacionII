package com.proyectofinal;

import java.util.List;

/**
 * Clase que representa a un jugador del tipo Arquero.
 * Especializado en ataques a distancia con flechas, tiene un modo de disparo ilimitado temporal.
 */
public class Arquero extends Jugador implements RecargableInterface {
    private float precision;
    private int flechas;
    private boolean modoIlimitado;
    private float tiempoIlimitado;

    /**
     * Constructor del Arquero.
     * @param nombre Nombre del personaje
     * @param vida Vida inicial (se sobrescribe por la vida máxima del arquero)
     * @param ataque Daño base del ataque
     * @param x Posición inicial en X
     * @param y Posición inicial en Y
     * @param width Ancho del sprite
     * @param height Alto del sprite
     * @param flechasIniciales Número de flechas iniciales
     * @param precision Precisión del disparo
     */
    public Arquero(String nombre, int vida, int ataque, float x, float y, float width, float height, int flechasIniciales, float precision) {
        super(nombre, vida, ataque, x, y, width, height, 1);  // Llama al constructor base de Jugador
        this.precision = precision;
        this.flechas = flechasIniciales;
        this.modoIlimitado = false;
        this.tiempoIlimitado = 0;
        this.vidaMaxima = 180; // Vida máxima propia del arquero
        this.vida = vidaMaxima; // Inicia con la vida llena
    }

    public float getPrecision() {
        return precision;
    }

    public int getFlechas() {
        return flechas;
    }

    /**
     * Indica si el arquero se encuentra en modo de disparo ilimitado.
     * @return true si está activo el modo ilimitado, false si no
     */
    public boolean estaModoIlimitado() {
        return modoIlimitado;
    }

    /**
     * Atajo para ejecutar un ataque sin necesidad de pasar lista de enemigos.
     * Usado cuando la colisión se maneja en otro lugar (como FlechaActor).
     * @return true si se pudo disparar, false si no hay flechas
     */
    public boolean ataque1() {
        return atacar(null); // No se pasa lista de enemigos porque se maneja externamente
    }

    /**
     * Realiza un ataque disparando una flecha.
     * Si no está en modo ilimitado, consume una flecha.
     * @param enemigos Lista de enemigos (puede ser null si no se usa aquí)
     * @return true si se realizó el disparo, false si no fue posible
     */
    public boolean atacar(List<? extends Enemigo> enemigos) {
        if (modoIlimitado || flechas > 0) {
            if (!modoIlimitado) {
                flechas--;
            }
            System.out.println(getNombre() + " dispara una flecha con precisión " + precision +
                ". Flechas restantes: " + flechas);
            // La lógica real del impacto la maneja FlechaActor
            return true;
        } else {
            System.out.println(getNombre() + " no tiene flechas disponibles.");
            return false;
        }
    }

    /**
     * Recarga flechas al arquero.
     * @param cantidad Número de flechas a añadir
     */
    @Override
    public void recargar(int cantidad) {
        int antes = flechas;
        flechas += cantidad;
        System.out.println(getNombre() + " recarga " + cantidad + " flechas. Flechas: " + antes + " → " + flechas);
    }

    /**
     * Devuelve el tiempo restante en el modo de disparo ilimitado.
     * @return Tiempo en segundos, o 0 si no está activo
     */
    public float getTiempoIlimitadoRestante() {
        return modoIlimitado ? tiempoIlimitado : 0f;
    }

    /**
     * Actualiza el contador del modo ilimitado. Llamar en cada frame con el delta time.
     * @param delta Tiempo transcurrido desde el último frame
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
