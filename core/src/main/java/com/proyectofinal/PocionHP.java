package com.proyectofinal;

/**
 * Clase que representa una pocion que restaura HP (vida) al personaje.
 * Hereda de la clase abstracta Pocion.
 */
public class PocionHP extends Pocion {

    /**
     * Constructor de la pocion de vida.
     * @param nombre Nombre descriptivo de la pocion (ej. "Pocion de vida menor")
     * @param valor Cantidad de HP que restaura al ser consumida
     */
    public PocionHP(String nombre, int valor) {
        // Llama al constructor de la clase base Pocion, tipo "HP" y cantidad 1
        super(nombre, "HP", valor, 1);
    }

    /**
     * Metodo que aplica el efecto de la pocion sobre el personaje.
     * Restaura vida si el personaje no esta ya al maximo.
     * @param personaje Personaje que consume la pocion
     */
    @Override
    public void consumir(Personaje personaje) {
        // Verifica si el personaje ya tiene la vida al maximo
        if (personaje.getVida() >= personaje.getVidaMaxima()) {
            throw new InvalidPotionException(
                "No puedes usar una pocion de vida si ya tienes la salud completa.");
        }

        // Guardamos la vida antes de aplicar el efecto
        int vidaAntes = personaje.getVida();

        // Llamamos a recibirDanio con valor negativo para curar al personaje
        personaje.recibirDanio(-valor);

        // Guardamos la vida despues de aplicar el efecto
        int vidaDespues = personaje.getVida();
        int restaurado = vidaDespues - vidaAntes;

        // Mensaje informativo en consola
        System.out.println(personaje.getNombre() +
            " ha restaurado " + restaurado + " HP. (" +
            vidaAntes + " → " + vidaDespues + ")");
    }
}
