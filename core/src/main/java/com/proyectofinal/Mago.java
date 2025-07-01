package com.proyectofinal;

import java.util.List;

/**
 * Subclase de Jugador especializada en ataques mágicos con mana.
 */
public class Mago extends Jugador implements RecargableInterface {
    private int mana;
    private int costoHechizo = 10;
    private int alcance = 5; // Mayor alcance que otras clases

    public Mago(String nombre, int vida, int ataque,
                float x, float y, float width, float height,
                int manaInicial) {
        super(nombre, vida, ataque, x, y, width, height, 0);
        this.mana = manaInicial;
    }

    public int getMana() {
        return mana;
    }

    /**
     * Ataque básico del mago: consume mana y causa daño a distancia.
     *
     * @return
     */
    public boolean atacar(List<? extends Enemigo> enemigos) {
        if (mana >= costoHechizo) {
            mana -= costoHechizo;
            System.out.println(getNombre() + " lanza un hechizo! Mana restante: " + mana);

            // La lógica de aplicar daño ahora está en HechizoActor
            // Este método solo registra el lanzamiento del hechizo
        } else {
            System.out.println(getNombre() + " no tiene suficiente mana para lanzar un hechizo.");
        }
        return false;
    }

    /**
     * Recarga mana al mago.
     */
    @Override
    public void recargar(int cantidad) {
        mana += cantidad;
        System.out.println(getNombre() + " recupera " + cantidad + " puntos de mana. Mana actual: " + mana);
    }

    /**
     * Ataque básico: hechizo simple sin coste de mana.
     */
    public void ataque1() {
        System.out.println(getNombre() + " lanza hechizo básico sin consumir mana.");
    }

    /**
     * Ataque secundario: hechizo poderoso de área que consume mana.
     */
    public void ataque2() {
        int cost = 10;
        if (mana >= cost) {
            mana -= cost;
            System.out.println(getNombre() + " lanza hechizo de área, consume " + cost + " mana. Mana restante: " + mana);
            // Aquí se aplicaría el efecto de área a enemigos cercanos
        } else {
            System.out.println(getNombre() + " no tiene suficiente mana para hechizo de área.");
        }
    }

    public void consumirMana(int costoHechizoEspecial) {
        if (mana >= costoHechizoEspecial) {
            mana -= costoHechizoEspecial;
            System.out.println(getNombre() + " consume " + costoHechizoEspecial + " mana.");
        } else {
            System.out.println(getNombre() + " no tiene suficiente mana para consumir el hechizo especial.");
        }
    }
}
