package com.proyectofinal;

/**
 * Clase que representa una pocion que restaura escudo al personaje tipo Caballero.
 * Hereda de la clase abstracta Pocion.
 */
public class PocionEscudo extends Pocion {

    /**
     * Constructor de la pocion de escudo.
     * @param nombre Nombre de la pocion (ejemplo: "Escudo de hierro")
     * @param valor Cantidad de escudo que va a restaurar
     */
    public PocionEscudo(String nombre, int valor) {
        // Llama al constructor de la clase padre Pocion con tipo "Escudo" y una cantidad de 1
        super(nombre, "Escudo", valor, 1);
    }

    /**
     * Metodo que aplica el efecto de la pocion al personaje.
     * Solo puede ser utilizada por un Caballero.
     * @param personaje Instancia del personaje que intenta consumir la pocion
     */
    @Override
    public void consumir(Personaje personaje) {
        // Verifica si el personaje es una instancia de Caballero
        if (personaje instanceof Caballero caballero) {
            // Aplica la cantidad de escudo al caballero
            caballero.recargar(valor);
            System.out.println(personaje.getNombre() + " ha recuperado " + valor + " puntos de escudo.");
        } else {
            // Si no es caballero, la pocion no tiene efecto
            System.out.println(personaje.getNombre() + " no puede usar esta pocion de escudo.");
        }
    }
}
