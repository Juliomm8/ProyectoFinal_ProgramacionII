package com.proyectofinal;

import java.util.List;
import com.badlogic.gdx.math.Rectangle;

/**
 * Caballero: personaje especializado en combate cuerpo a cuerpo.
 * Posee un escudo que absorbe daño, ataque con hitbox frontal,
 * y un cooldown entre ataques.
 */
public class Caballero extends Jugador implements RecargableInterface {

    // ——— Atributos del escudo ———
    private int escudo;                      // Escudo actual
    private int escudoMaximo;                // Máximo escudo posible
    private float tiempoRegeneracionEscudo;  // Temporizador para regenerar escudo
    private static final float TIEMPO_REGENERACION_BASE = 2.0f; // Tiempo en segundos para regenerar 1 punto de escudo

    // ——— Atributos del ataque ———
    private float rangoAtaque = 80f;              // Alcance horizontal del golpe
    private float duracionAnimacionAtaque = 0.5f; // Duración de la animación del ataque
    private boolean atacando = false;             // ¿Está atacando actualmente?
    private float tiempoAtaque = 0f;              // Tiempo transcurrido desde que comenzó a atacar
    private boolean hitboxActiva = false;         // ¿Está activa la hitbox de daño?

    private Rectangle hitboxAtaque = new Rectangle(); // Área que detecta colisiones con enemigos durante el ataque

    // ——— Cooldown entre ataques ———
    private long tiempoUltimoAtaque = 0L;
    private static final long COOLDOWN_MS = 500L; // Tiempo en milisegundos entre ataques

    /**
     * Constructor del Caballero.
     * @param escudoInicial Valor inicial y máximo del escudo.
     */
    public Caballero(String nombre, int vida, int ataque,
                     float x, float y, float width, float height,
                     int escudoInicial) {
        super(nombre, vida, ataque, x, y, width, height, 1);
        this.escudo = escudoInicial;
        this.escudoMaximo = escudoInicial;
        this.tiempoRegeneracionEscudo = 0f;
        this.vidaMaxima = 200;  // Vida máxima predeterminada del caballero
        this.vida = vidaMaxima;
    }

    // ——— ATAQUE ———

    /** Verifica si ha pasado el tiempo de cooldown para volver a atacar. */
    public boolean puedeAtacar() {
        return System.currentTimeMillis() - tiempoUltimoAtaque >= COOLDOWN_MS;
    }

    /** Registra el instante actual como momento del último ataque. */
    public void registrarAtaque() {
        tiempoUltimoAtaque = System.currentTimeMillis();
    }

    /** Activa la animación e hitbox del ataque. */
    public void iniciarAtaque() {
        atacando = true;
        tiempoAtaque = 0f;
        hitboxActiva = true;
    }

    /** Finaliza la animación e inactiva la hitbox del ataque. */
    public void terminarAtaque() {
        atacando = false;
        hitboxActiva = false;
        tiempoAtaque = 0f;
    }

    /**
     * Actualiza la posición de la hitbox según la dirección del caballero.
     * La hitbox se ubica justo frente a él.
     */
    private void actualizarHitboxAtaque() {
        float altura = getHeight() * 1.5f; // Abarca un poco más que su altura
        float offsetY = (altura - getHeight()) / 2f; // Centra verticalmente

        float x = "DERECHA".equals(direccion)
            ? getX() + getWidth()
            : getX() - rangoAtaque;

        hitboxAtaque.set(x, getY() - offsetY, rangoAtaque, altura);
    }

    /**
     * Ataca a todos los enemigos dentro de la hitbox activa.
     * Solo funciona si está atacando y la hitbox está activa.
     */
    public boolean atacar(List<? extends Enemigo> enemigos) {
        if (!atacando || !hitboxActiva) return false;

        hitboxActiva = false;
        atacando = true;
        tiempoAtaque = 0f;
        actualizarHitboxAtaque();

        System.out.println(getNombre() + " realiza un ataque demoledor!");

        if (enemigos == null || enemigos.isEmpty()) {
            System.out.println("No hay enemigos para atacar");
            return false;
        }

        int enemigosGolpeados = 0;
        for (Enemigo e : enemigos) {
            if (e.estaVivo() && estaEnRango(e)) {
                System.out.println("Aplicando daño al enemigo");
                e.recibirDanio(getDanoBase());
                enemigosGolpeados++;
            }
        }

        System.out.println("Enemigos golpeados en este ataque: " + enemigosGolpeados);
        return enemigosGolpeados > 0;
    }

    /** Verifica si un enemigo está dentro del área de la hitbox. */
    private boolean estaEnRango(Enemigo e) {
        return hitboxAtaque.overlaps(e.getHitbox());
    }

    // ——— ACTUALIZACIÓN GENERAL ———

    /**
     * Lógica que se ejecuta en cada frame:
     * - Maneja la animación del ataque
     * - Regenera escudo si no está completo
     */
    @Override
    public void actualizar(float delta) {
        if (atacando) {
            actualizarHitboxAtaque();
            tiempoAtaque += delta;
            if (tiempoAtaque >= duracionAnimacionAtaque) {
                atacando = false;
            }
        }

        tiempoRegeneracionEscudo += delta;
        if (tiempoRegeneracionEscudo >= TIEMPO_REGENERACION_BASE && escudo < escudoMaximo) {
            escudo++;
            tiempoRegeneracionEscudo = 0f;
        }
    }

    // ——— INTERFAZ RecargableInterface ———

    /** Aumenta el escudo sin pasarse del máximo. */
    @Override
    public void recargar(int cantidad) {
        escudo = Math.min(escudo + cantidad, escudoMaximo);
        System.out.println(getNombre() + " recupera escudo: +" + cantidad);
    }

    // ——— RECEPCIÓN DE DAÑO ———

    /** El escudo absorbe daño primero; si se agota, la vida recibe el resto. */
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

    /** Devuelve el estado actual del escudo en texto. */
    public String getEscudo() {
        return "Escudo: " + escudo + "/" + escudoMaximo;
    }
}
