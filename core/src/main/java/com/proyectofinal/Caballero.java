package com.proyectofinal;

import java.util.List;

/**
 * Subclase de Jugador especializada en combate cuerpo a cuerpo con escudo.
 */
public class Caballero extends Jugador implements RecargableInterface {
    private int escudo;

    // Control de ataque usando milisegundos para evitar pérdida de precisión
    private long tiempoUltimoAtaque = 0L;                // instante del último ataque en ms
    private static final long COOLDOWN_ATAQUE_MS = 500L; // cooldown de 0.5 segundos
    private int alcance = 1;                             // tiles de alcance

    public Caballero(String nombre, int vida, int ataque,
                     float x, float y, float width, float height,
                     int escudoInicial) {
        super(nombre, vida, ataque, x, y, width, height, escudoInicial);
        this.escudo = escudoInicial;
    }

    public int getEscudo() {
        return escudo;
    }

    /**
     * Comprueba si ha pasado el cooldown desde el último ataque usando ms.
     */
    public boolean puedeAtacar() {
        long ahora = System.currentTimeMillis();
        long diferencia = ahora - tiempoUltimoAtaque;
        System.out.println("DEBUG CanAttack? diffMs=" + diferencia + " vs cooldownMs=" + COOLDOWN_ATAQUE_MS);
        return diferencia >= COOLDOWN_ATAQUE_MS;
    }

    /**
     * Registra el instante en ms al iniciar el ataque para el cooldown.
     */
    public void registrarAtaque() {
        tiempoUltimoAtaque = System.currentTimeMillis();
        System.out.println("DEBUG Attack registered ms=" + tiempoUltimoAtaque);
    }

    /**
     * Aplica daño en área frontal a todos los enemigos en la misma fila.
     */
    public void atacar(List<? extends Enemigo> enemigos) {
        System.out.println(getNombre() + " realiza un ataque con su espada!");
        for (Enemigo enemigo : enemigos) {
            if (enemigo.getY() == getY()) {
                if ("DERECHA".equals(direccion)
                    && enemigo.getX() > getX()
                    && enemigo.getX() <= getX() + alcance) {
                    enemigo.recibirDano(getDanoBase());
                } else if ("IZQUIERDA".equals(direccion)
                    && enemigo.getX() < getX()
                    && enemigo.getX() >= getX() - alcance) {
                    enemigo.recibirDano(getDanoBase());
                }
            }
        }
    }

    @Override
    public void recargar(int cantidad) {
        escudo += cantidad;
        System.out.println(getNombre() + " gana " + cantidad + " puntos de escudo. Escudo actual: " + escudo);
    }

    @Override
    public void recibirDanio(int cantidad) {
        if (escudo >= cantidad) {
            escudo -= cantidad;
            System.out.println(getNombre() + " bloquea " + cantidad + " de daño con el escudo.");
        } else {
            int resto = cantidad - escudo;
            escudo = 0;
            System.out.println(getNombre() + " pierde el escudo. Daño restante: " + resto);
            super.recibirDanio(resto);
        }
    }

    public void aumentarEscudo(int valor) {
        escudo += valor;
        System.out.println(getNombre() + " aumenta su escudo en " + valor + " puntos.");
    }
}
