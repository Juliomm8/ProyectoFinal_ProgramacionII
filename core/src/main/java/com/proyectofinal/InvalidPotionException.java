package com.proyectofinal;

/**
 * Excepción lanzada cuando el jugador intenta utilizar una pocion invalida.
 * Se usa para evitar el consumo de pociones que no tienen efecto,
 * por ejemplo si la vida o el mana ya están al máximo.
 */
public class InvalidPotionException extends RuntimeException {
    /**
     * Crea una nueva InvalidPotionException con el mensaje especificado.
     * @param mensaje descripción del error
     */
    public InvalidPotionException(String mensaje) {
        super(mensaje);
    }
}
