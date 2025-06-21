// DaniableInterface.java
package com.proyectofinal;

/**
 * Interfaz para entidades que pueden recibir daño.
 */
public interface DaniableInterface {
    /**
     * Aplica daño a la entidad.
     * @param cantidad puntos de daño.
     */
    void recibirDanio(int cantidad);
}
