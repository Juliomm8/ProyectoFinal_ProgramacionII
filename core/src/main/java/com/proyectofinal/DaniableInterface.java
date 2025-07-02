// DaniableInterface.java
package com.proyectofinal;

/**
 * Interfaz para entidades que pueden recibir dano en combate.
 * Puede ser implementada por jugadores, enemigos, jefes u objetos destructibles.
 */
public interface DaniableInterface {
    /**
     * Metodo que aplica dano a la entidad.
     * @param cantidad cantidad de puntos de dano a reducir.
     */
    void recibirDanio(int cantidad);
}
