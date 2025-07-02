package com.proyectofinal;

/**
 * Clase que representa una pocion que otorga experiencia (EXP) al personaje.
 * Hereda de la clase abstracta Pocion.
 */
public class PocionEXP extends Pocion {

    /**
     * Constructor de la pocion de experiencia.
     * @param nombre Nombre de la pocion (ejemplo: "Pocion de sabiduria")
     * @param valor Cantidad de experiencia que otorga (aunque no se usa directamente en este ejemplo)
     */
    public PocionEXP(String nombre, int valor) {
        // Llama al constructor de la clase padre Pocion con tipo "EXP" y una cantidad de 1 uso
        super(nombre, "EXP", valor, 1);
    }

    /**
     * Metodo que aplica el efecto de la pocion al personaje.
     * Solo puede ser utilizada por un objeto que herede de Jugador.
     * @param personaje Instancia del personaje que intenta consumir la pocion
     */
    @Override
    public void consumir(Personaje personaje) {
        // Verifica si el personaje es una instancia de Jugador
        if (personaje instanceof Jugador) {
            // Aplica el efecto de subir nivel al jugador
            ((Jugador) personaje).subirNivel();
            System.out.println(personaje.getNombre() + " ha ganado " + valor + " EXP.");
        } else {
            // Si no es un jugador, la pocion no tiene efecto
            System.out.println(personaje.getNombre() + " no puede recibir EXP.");
        }
    }
}
