package com.proyectofinal;

/**
 * Pruebas de consumo de pociones para las distintas clases.
 */

public class TestPociones {
    public static void main(String[] args) {
        // --- Prueba de HP y EXP en Jugador ---
        Jugador jugador = new Jugador("Héroe", 100, 20, 100f, 100f, 32f, 32f, 1);
        Pocion pocionHP = new PocionHP("Poción de Vida", 30);
        Pocion pocionEXP = new PocionEXP("Poción de EXP", 1);

        System.out.println("Vida inicial: " + jugador.getVida());
        pocionHP.consumir(jugador);
        System.out.println("Vida tras Poción HP: " + jugador.getVida());
        System.out.println();

        System.out.println("Nivel inicial: " + jugador.getNivel());
        pocionEXP.consumir(jugador);
        System.out.println("Nivel tras Poción EXP: " + jugador.getNivel());
        System.out.println();

        // --- Prueba de PocionMana con Mago ---
        Mago mago = new Mago("Gandalf", 80, 12, 5,2,2,2,2);
        PocionMana pocMana = new PocionMana("Poción de Maná", 20);

        System.out.println("Mana inicial de " + mago.getNombre() + ": " + mago.getMana());
        pocMana.consumir(mago);
        System.out.println("Mana tras Poción Mana: " + mago.getMana());
        System.out.println();

        // --- Prueba de PocionMana con Caballero ---
        Caballero cab = new Caballero("Arthur", 120, 15,2,2,2,2,2);

        System.out.println("Escudo inicial de " + cab.getNombre() + ": " + cab.getEscudo());
        pocMana.consumir(cab);
        System.out.println("Escudo tras Poción Mana: " + cab.getEscudo());
        System.out.println();

        // --- Prueba de PocionFlechas con Arquero ---
        Arquero arquero = new Arquero("Legolas", 70, 15, 0.85f, 10,2,2,2,2);
        PocionFlechas pocFlecha = new PocionFlechas("Poción de Flechas", 5);

        System.out.println("Flechas iniciales de " + arquero.getNombre() + ": " + arquero.getFlechas());
        pocFlecha.consumir(arquero);
        System.out.println("Flechas tras Poción Flechas: " + arquero.getFlechas());
    }
}

