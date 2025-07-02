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
    /** Indica si la hitbox de ataque está activa */
    private boolean hitboxActiva = false;

    /** Hitbox del ataque cuerpo a cuerpo para detectar colisiones. */
    private Rectangle hitboxAtaque = new Rectangle();


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
     * Inicia el ataque activando la hitbox y marcando el estado.
     */
    public void iniciarAtaque() {
        atacando = true;
        tiempoAtaque = 0f;
        hitboxActiva = true;
    }

    /**
     * Finaliza el ataque y desactiva la hitbox.
     */
    public void terminarAtaque() {
        atacando = false;
        hitboxActiva = false;
        tiempoAtaque = 0f;
    }

    /**
     * Posiciona la hitbox de ataque justo frente al caballero. Se utiliza
     * únicamente mientras se ejecuta la animación de ataque.
     */
    private void actualizarHitboxAtaque() {
        float altura = getHeight() * 1.5f;
        float offsetY = (altura - getHeight()) / 2f;

        float x = "DERECHA".equals(direccion)
            ? getX() + getWidth()
            : getX() - rangoAtaque;

        hitboxAtaque.set(x, getY() - offsetY, rangoAtaque, altura);
    }

    /**
     * Mata de un solo golpe a cualquier enemigo en la misma fila
     * y dentro de rango frontal.
     *
     * @return
     */
    public boolean atacar(List<? extends Enemigo> enemigos) {
        if (!atacando || !hitboxActiva) return false;
        hitboxActiva = false;
        atacando = true;
        tiempoAtaque = 0f;
        actualizarHitboxAtaque();

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
                    System.out.println("Aplicando daño al enemigo");
                    e.recibirDanio(getDanoBase());
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
            actualizarHitboxAtaque();
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
        return hitboxAtaque.overlaps(e.getHitbox());
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
