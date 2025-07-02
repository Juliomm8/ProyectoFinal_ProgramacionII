package com.proyectofinal;

/**
 * Excepción lanzada al intentar equipar un objeto que no cumple los
 * requisitos de clase o nivel del jugador.
 */
public class ItemNotEquippableException extends RuntimeException {
    /**
     * Crea la excepción con un mensaje personalizado.
     * @param mensaje mensaje descriptivo del error
     */
    public ItemNotEquippableException(String mensaje) {
        super(mensaje);
    }
}
