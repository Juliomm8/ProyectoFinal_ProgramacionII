package com.proyectofinal;

public class TestPociones {
    public static void main(String[] args) {
        Jugador jugador = new Jugador("Héroe", 100, 20);
        Pocion pocionHP  = new PocionHP("Poción de Vida", 30);
        Pocion pocionEXP = new PocionEXP("Poción de EXP", 1); // sube 1 nivel

        System.out.println("Vida inicial: " + jugador.getVida());
        pocionHP.consumir(jugador);
        System.out.println("Vida tras Poción HP: " + jugador.getVida());

        System.out.println("Nivel inicial: " + jugador.getNivel());
        pocionEXP.consumir(jugador);
        System.out.println("Nivel tras Poción EXP: " + jugador.getNivel());
    }
}
