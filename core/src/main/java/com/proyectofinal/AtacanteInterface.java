package com.proyectofinal;

/**
 * Interfaz para entidades capaces de ejecutar ataques.
 * Cualquier clase que implemente esta interfaz debe definir cómo ataca y cuánto daño inflige.
 */
public interface AtacanteInterface {

    /**
     * Ejecuta un ataque y devuelve la cantidad de daño que produce.
     * Este valor puede depender de estadísticas del personaje, arma equipada, o habilidades.
     *
     * @return daño infligido por el ataque
     */
    int atacar();
}
