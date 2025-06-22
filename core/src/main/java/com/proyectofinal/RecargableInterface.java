package com.proyectofinal;

/**
 * Marca a los personajes que pueden recargar un recurso propio
 * (mana, escudo o flechas) mediante un valor dado.
 */
public interface RecargableInterface {
    /**
     * Recarga el recurso específico de este personaje.
     * @param cantidad la cantidad a recargar
     */
    void recargar(int cantidad);
}
