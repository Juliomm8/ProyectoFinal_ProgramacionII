package com.proyectofinal;

/**
 * Poción que restaura HP al personaje.
 * Aplica curación inmediata al ser consumida.
 */
public class PocionHP extends Pocion {

    /**
     * Constructor de la poción de vida.
     * @param nombre Nombre descriptivo de la poción
     * @param valor Cantidad de HP que restaura
     */
    public PocionHP(String nombre, int valor) {
        super(nombre, "HP", valor, 1);
    }

    /**
     * Consume la poción y restaura vida al personaje.
     * @param personaje Personaje que consume la poción
     */
    @Override
    public void consumir(Personaje personaje) {
        int vidaAntes = personaje.getVida();
        personaje.recibirDanio(-valor);  // Restaurar salud (valor negativo de daño)
        int vidaDespues = personaje.getVida();
        int restaurado = vidaDespues - vidaAntes;

        System.out.println(personaje.getNombre() + " ha restaurado " + restaurado + " HP. (" +
                           vidaAntes + " → " + vidaDespues + ")");
    }
}
