package com.proyectofinal;

import com.badlogic.gdx.math.Rectangle;
import java.util.List;

/**
 * Representa al jugador con inventario de pociones y nivel.
 */
public class Jugador extends Personaje {
    private float x, y;             // Posicion del jugador
    private float width, height;    // Tamano del collider
    private int nivel;              // Nivel actual del jugador
    protected String direccion;     // Direccion hacia la que mira: "IZQUIERDA" o "DERECHA"
    private boolean muerto;         // Estado de vida del jugador

    // Constructor
    public Jugador(String nombre, int vida, int ataque,
                   float x, float y, float width, float height, int nivel) {
        super(nombre, vida, ataque);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.nivel = nivel;
        this.direccion = "DERECHA";
        this.muerto = false;
    }

    // Getters basicos
    public float getWidth() { return width; }
    public float getHeight() { return height; }
    public String getNombre() { return nombre; }
    public int getVida() { return vida; }
    public float getX() { return x; }
    public float getY() { return y; }
    public int getNivel() { return nivel; }
    public boolean estaMuerto() { return muerto; }

    // Marcar jugador como muerto
    public void morir() {
        muerto = true;
        System.out.println("El jugador ha muerto");
    }

    // Devuelve el dano base del jugador
    public int getDanoBase() {
        return ataque;
    }

    // Devuelve el collider del jugador para colisiones
    public Rectangle getCollider() {
        return new Rectangle(x, y, width, height);
    }

    // Actualiza la posicion del jugador
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Movimiento con delta y actualizacion de direccion
    public void mover(float dirX, float dirY, float delta) {
        float speed = 200f * delta;
        this.x += dirX * speed;
        this.y += dirY * speed;

        if (dirX < 0) direccion = "IZQUIERDA";
        else if (dirX > 0) direccion = "DERECHA";
        // Si dirX es 0, se mantiene la direccion anterior
    }

    /**
     * Verifica si un item puede ser equipado por el jugador.
     * @param item objeto a comprobar
     * @return true si el item cumple requisitos de clase y nivel
     */
    public boolean puedeEquipar(Item item) {
        return item != null && item.cumpleRequisitos(this);
    }

    /**
     * Intenta equipar un item. Lanza excepcion si no se cumplen los requisitos.
     * @param item objeto a equipar
     */
    public void equiparItem(Item item) {
        if (!puedeEquipar(item)) {
            throw new ItemNotEquippableException(
                "Este objeto no puede ser equipado por tu clase o nivel.");
        }
        System.out.println(nombre + " ha equipado " + item.getNombre());
    }

    /**
     * Metodo generico de ataque. Puede ser sobrescrito por subclases.
     * @param enemigos lista de enemigos cercanos
     * @return true si el ataque fue efectivo
     */
    public boolean atacar(List<? extends Enemigo> enemigos) {
        System.out.println(nombre + " realiza un ataque basico");
        return false;
    }

    /**
     * Aplica dano al jugador. Si la vida cae a 0, el jugador muere.
     * Tambien controla que la vida no supere el maximo si se recibe "curacion negativa".
     */
    @Override
    public void recibirDanio(int danio) {
        vida -= danio;
        if (danio < 0 && vida > vidaMaxima) {
            vida = vidaMaxima;
        }
        if (vida <= 0 && !muerto) {
            vida = 0;
            morir();
            System.out.println(nombre + " ha muerto con");
        }
    }

    // Sube un nivel al jugador
    public void subirNivel() {
        nivel++;
    }

    /**
     * Metodo vacio para ser sobrescrito en subclases. Llamado cada frame.
     * @param delta tiempo entre frames
     */
    public void actualizar(float delta) {
        // Implementacion vacia
    }
}
