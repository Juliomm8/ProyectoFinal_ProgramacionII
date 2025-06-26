package com.proyectofinal;

import com.badlogic.gdx.math.Rectangle;  // Para Rectangle

/**
 * Representa al jugador con inventario de pociones y nivel.
 */
public class Jugador extends Personaje {
    private float x, y; // Posición del jugador
    private float width, height; // Tamaño del jugador
    private int nivel; // Nivel del jugador

    // Constructor de la clase Jugador
    public Jugador(String nombre, int vida, int ataque, float x, float y, float width, float height, int nivel) {
        super(nombre, vida, ataque);  // Llamada al constructor de la superclase (Personaje)
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.nivel = nivel; // Establecer el nivel
    }

    // Métodos getter
    public String getNombre() {
        return nombre;
    }

    public int getVida() {
        return vida;
    }

    public int getAtaque() {
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

    // Métodos setter
    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    // Método para obtener el collider del jugador
    public Rectangle getCollider() {
        return new Rectangle(x, y, width, height);  // Collider para el jugador
    }

    // Método para mover al jugador
    public void mover(float direccionX, float direccionY, float delta) {
        // Velocidad en función del delta para hacer el movimiento frame rate independiente
        float speed = 200f * delta; // Ajusta el valor de la velocidad según sea necesario
        // Nueva posición del jugador
        this.x += direccionX * speed;
        this.y += direccionY * speed;
    }

    // Método para recibir daño
    public void recibirDanio(int dano) {
        this.vida -= dano;
        if (this.vida < 0) {
            this.vida = 0;  // Evita que la vida sea negativa
        }
    }

    // Método para atacar (retorna el valor de daño)
    public int atacar() {
        return this.ataque;
    }

    // Método para recoger poción
    public void recogerPocion(Pocion pocion) {
        if (pocion instanceof PocionHP) {
            this.vida += pocion.getCantidad();  // Sumar la cantidad de la poción a la vida
        } else if (pocion instanceof PocionEXP) {
            this.nivel += pocion.getCantidad(); // Sumar la cantidad de la poción al nivel
        } else if (pocion instanceof PocionMana) {
            // Aquí podrías agregar lógica para el caso de PocionMana
        } else if (pocion instanceof PocionFlechas) {
            // Aquí podrías agregar lógica para el caso de PocionFlechas
        }
    }

    // Método para subir nivel
    public void subirNivel() {
        this.nivel++;  // Aumenta el nivel del jugador
    }
}
