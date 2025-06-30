package com.proyectofinal;

import java.util.List;
import com.badlogic.gdx.math.Rectangle;

/**
 * Caballero: combate cuerpo a cuerpo con escudo, cooldown de ataque,
 * un solo golpe letal, y regeneración de escudo.
 */
public class Caballero extends Jugador implements RecargableInterface {
    // ——— Escudo ———
    private int escudo;
    private int escudoMaximo;
    private float tiempoRegeneracionEscudo;
    private static final float TIEMPO_REGENERACION_BASE = 2.0f; // seg

    // ——— Ataque ———
    private float rangoAtaque = 80f;              // píxeles
    private float duracionAnimacionAtaque = 0.5f; // seg
    private boolean atacando = false;
    private float tiempoAtaque = 0f;

    // Cooldown en ms
    private long tiempoUltimoAtaque = 0L;
    private static final long COOLDOWN_MS = 500L; // 0.5 seg

    /**
     * @param escudoInicial tanto escudoMaximo como valor de escudo al inicio
     */
    public Caballero(String nombre, int vida, int ataque,
                     float x, float y, float width, float height,
                     int escudoInicial) {
        super(nombre, vida, ataque, x, y, width, height, /*nivel*/1);
        this.escudo = escudoInicial;
        this.escudoMaximo = escudoInicial;
        this.tiempoRegeneracionEscudo = 0f;
    }

    /** ¿Ha pasado el cooldown para poder atacar de nuevo? */
    public boolean puedeAtacar() {
        return System.currentTimeMillis() - tiempoUltimoAtaque >= COOLDOWN_MS;
    }

    /** Registra el instante del ataque para el cooldown. */
    public void registrarAtaque() {
        tiempoUltimoAtaque = System.currentTimeMillis();
    }

    /**
     * Mata de un solo golpe a cualquier enemigo en la misma fila
     * y dentro de rango frontal.
     */
    public void atacar(List<? extends Enemigo> enemigos) {
        if (atacando || !puedeAtacar()) return;
        atacando = true;
        tiempoAtaque = 0f;
        registrarAtaque();
        System.out.println(getNombre() + " realiza un ataque demoledor!");

        if (enemigos != null) {
            for (Enemigo e : enemigos) {
                if (e.estaVivo() && estaEnRango(e)) {
                    e.recibirDano(Integer.MAX_VALUE);
                }
            }
        }
    }

    /** Lógica de animación de ataque y regeneración de escudo. */
    @Override
    public void actualizar(float delta) {
        // Animación de ataque
        if (atacando) {
            tiempoAtaque += delta;
            if (tiempoAtaque >= duracionAnimacionAtaque) {
                atacando = false;
            }
        }
        // Regeneración de escudo
        tiempoRegeneracionEscudo += delta;
        if (tiempoRegeneracionEscudo >= TIEMPO_REGENERACION_BASE
            && escudo < escudoMaximo) {
            escudo++;
            tiempoRegeneracionEscudo = 0f;
        }
    }

    /** Comprueba si un enemigo está en la zona frontal de ataque. */
    private boolean estaEnRango(Enemigo e) {
        Rectangle area;
        if ("DERECHA".equals(direccion)) {
            area = new Rectangle(getX() + getWidth(),
                getY(),
                rangoAtaque,
                getHeight());
        } else {
            area = new Rectangle(getX() - rangoAtaque,
                getY(),
                rangoAtaque,
                getHeight());
        }
        Rectangle hitE = new Rectangle(e.getX(), e.getY(),
            e.getWidth(), e.getHeight());
        return area.overlaps(hitE);
    }

    /** ¿Está en plena animación de ataque? */
    public boolean estaAtacando() {
        return atacando;
    }

    /** Tiempo transcurrido desde que inició su último ataque. */
    public float getTiempoAtaque() {
        return tiempoAtaque;
    }

    // ——— RecargableInterface ———

    @Override
    public void recargar(int cantidad) {
        escudo = Math.min(escudo + cantidad, escudoMaximo);
        System.out.println(getNombre() + " recupera escudo: +" + cantidad);
    }

    // ——— Absorción de daño con escudo ———

    @Override
    public void recibirDanio(int cantidad) {
        if (cantidad <= 0) {
            super.recibirDanio(cantidad);
            return;
        }
        if (escudo >= cantidad) {
            escudo -= cantidad;
            System.out.println(getNombre() + " bloquea " + cantidad + " de daño.");
        } else {
            int resto = cantidad - escudo;
            escudo = 0;
            System.out.println(getNombre() + " pierde escudo, recibe " + resto);
            super.recibirDanio(resto);
        }
    }

    public String getEscudo() {
        return "Escudo: " + escudo + "/" + escudoMaximo;
    }

    public void aumentarEscudo(int valor) {
        escudoMaximo += valor;
        System.out.println(getNombre() + " aumenta su escudo a " + escudoMaximo);
        if (escudo > escudoMaximo) {
            escudo = escudoMaximo;
        }
        if (escudo < 0) {
            escudo = 0;
        }
        System.out.println(getNombre() + " ahora tiene " + escudo + " de escudo.");
    }
}
