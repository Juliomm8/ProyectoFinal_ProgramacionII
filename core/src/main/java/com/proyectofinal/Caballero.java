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
     *
     * @return
     */
    public boolean atacar(List<? extends Enemigo> enemigos) {
        if (atacando || !puedeAtacar()) return false;

        System.out.println(getNombre() + " realiza un ataque demoledor!");

        // Verificar si hay enemigos para atacar
        if (enemigos == null || enemigos.isEmpty()) {
            System.out.println("No hay enemigos para atacar");
            return false;
        }

        // Contar enemigos antes del ataque
        int enemigosVivos = 0;
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) enemigosVivos++;
        }
        System.out.println("Enemigos vivos antes del ataque: " + enemigosVivos);

        // Aplicar daño a los enemigos en rango
        int enemigosGolpeados = 0;
        for (Enemigo e : enemigos) {
            if (e.estaVivo()) {
                boolean enRango = estaEnRango(e);
                System.out.println("Enemigo en posición (" + e.getX() + ", " + e.getY() + ") " +
                    (enRango ? "ESTÁ" : "NO ESTÁ") + " en rango");

                if (enRango) {
                    System.out.println("Aplicando daño letal al enemigo");
                    e.recibirDanio(9999); // Usar un valor grande pero no MAX_VALUE para evitar overflow
                    enemigosGolpeados++;
                }
            }
        }

        System.out.println("Enemigos golpeados en este ataque: " + enemigosGolpeados);
        return enemigosGolpeados > 0;
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
        // Aumentar el tamaño vertical del área para facilitar colisiones
        float alturaAmpliada = getHeight() * 1.5f;
        float offsetY = (alturaAmpliada - getHeight()) / 2f;

        Rectangle area;
        if ("DERECHA".equals(direccion)) {
            area = new Rectangle(
                getX() + getWidth() * 0.5f,
                getY() - offsetY,
                rangoAtaque,
                alturaAmpliada);
        } else {
            area = new Rectangle(
                getX() - rangoAtaque + getWidth() * 0.5f,
                getY() - offsetY,
                rangoAtaque,
                alturaAmpliada);
        }

        Rectangle hitJugador = getCollider();
        Rectangle hitE = e.getHitbox();

        boolean enRango = area.overlaps(hitE) || hitJugador.overlaps(hitE);

        System.out.println("Área de ataque: x=" + area.x + ", y=" + area.y + ", w=" + area.width + ", h=" + area.height);
        if (enRango) {
            System.out.println("Hitbox enemigo: x=" + hitE.x + ", y=" + hitE.y + ", w=" + hitE.width + ", h=" + hitE.height);
        }

        return enRango;
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
}
