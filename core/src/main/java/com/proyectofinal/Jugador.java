package com.proyectofinal;

import com.badlogic.gdx.math.Rectangle;
import java.util.List;

/**
 * Representa al jugador con inventario de pociones y nivel.
 */
public class Jugador extends Personaje {
    private float x, y;             // Posición del jugador
    private float width, height;    // Tamaño y collider
    private int nivel;              // Nivel del jugador
    protected String direccion;     // "IZQUIERDA" o "DERECHA"
    public float getWidth()  { return width; }
    public float getHeight() { return height; }

    /**
     * @param nombre    Nombre del jugador
     * @param vida      Puntos de vida iniciales
     * @param ataque    Daño base de ataque
     * @param x         Posición X inicial
     * @param y         Posición Y inicial
     * @param width     Ancho del collider
     * @param height    Alto del collider
     * @param nivel     Nivel inicial
     */
    public Jugador(String nombre,
                   int vida,
                   int ataque,
                   float x,
                   float y,
                   float width,
                   float height,
                   int nivel) {
        super(nombre, vida, ataque);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.nivel = nivel;
        this.direccion = "DERECHA"; // valor por defecto
    }

    // ——————————— Getters ———————————

    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    /** Daño base que inflige el jugador. */
    public int getDanoBase() {
        return ataque;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public int getNivel() {
        return nivel;
    }

    // ——————————— Setters y utilidad ———————————

    /** Actualiza la posición del jugador. */
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    /** Collider para detección de colisiones. */
    public Rectangle getCollider() {
        return new Rectangle(x, y, width, height);
    }

    /**
     * Mueve al jugador.
     * @param dirX  -1 izquierda, +1 derecha, 0 sin horizontal
     * @param dirY  -1 abajo, +1 arriba, 0 sin vertical
     * @param delta Delta time para velocidad independiente de FPS
     */
    public void mover(float dirX, float dirY, float delta) {
        float speed = 200f * delta;

        // Aplicar movimiento en ambas direcciones
        this.x += dirX * speed;
        this.y += dirY * speed;

        // Actualizar dirección solo si hay movimiento horizontal
        // Esto permite que el sprite mantenga su orientación durante movimientos verticales
        if (dirX < 0) {
            direccion = "IZQUIERDA";
        } else if (dirX > 0) {
            direccion = "DERECHA";
        }
        // Nota: No cambiamos la dirección si dirX es 0, manteniendo la orientación anterior
    }

    /**
     * Método de ataque genérico para el jugador.
     * Las subclases pueden sobrescribir este método para implementar su lógica específica.
     * @param enemigos Lista de enemigos que pueden ser afectados por el ataque
     */
    public void atacar(List<? extends Enemigo> enemigos) {
        // Implementación genérica que puede ser sobrescrita
        System.out.println(nombre + " realiza un ataque básico");
    }

    // ——————————— Interacciones ———————————

    /**
     * Recibe daño directo, resta a la vida.
     */
    @Override
    public void recibirDanio(int danio) {
        vida -= danio;
        if (vida < 0) vida = 0;
    }

    /**
     * Recoge una poción y aplica su efecto.
     */
    public void recogerPocion(Pocion pocion) {
        int cantidad = pocion.getCantidad();
        if (pocion instanceof PocionHP) {
            vida = Math.min(vida + cantidad, vida);
        } else if (pocion instanceof PocionEXP) {
            nivel += cantidad;
        } else if (pocion instanceof PocionMana) {
            // Lógica para maná
        } else if (pocion instanceof PocionFlechas) {
            // Lógica para flechas
        }
    }

    /** Sube un nivel. */
    public void subirNivel() {
        nivel++;
    }

    /**
     * Método para actualización por frame. Debe ser implementado por subclases.
     * @param delta tiempo entre frames para cálculos independientes de FPS
     */
    public void actualizar(float delta) {
        // Implementación base vacía, las subclases deben sobrescribir este método
    }
}
