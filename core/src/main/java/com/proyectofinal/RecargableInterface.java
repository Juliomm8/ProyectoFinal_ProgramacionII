package com.proyectofinal;

/**
 * Interfaz que define el comportamiento de los personajes que pueden recargar un recurso.
 * Este recurso puede ser mana, escudo, flechas u otro dependiendo del tipo de personaje.
 */
public interface RecargableInterface {

    /**
     * Metodo que deben implementar los personajes para recargar su recurso.
     * Por ejemplo, el mago recargaria mana, el arquero flechas, el caballero escudo.
     *
     * @param cantidad la cantidad a recargar
     */
    void recargar(int cantidad);
}
