package com.proyectofinal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.List;
import java.util.Iterator;

/**
 * Clase de utilidad para gestionar enemigos en pantalla.
 * Se encarga de actualizar el estado de cada enemigo y manejar colisiones con proyectiles.
 */
public class GestionEnemigos {

    /**
     * Recorre la lista de enemigos y elimina los que ya terminaron su animación de muerte.
     *
     * @param enemigos Lista de enemigos activos
     * @return cantidad de enemigos eliminados
     */
    public static int actualizarEnemigos(List<? extends Enemigo> enemigos) {
        if (enemigos == null) return 0;

        int eliminados = 0;
        Iterator<? extends Enemigo> iter = enemigos.iterator();

        while (iter.hasNext()) {
            Enemigo enemigo = iter.next();

            if (enemigo.isReadyToRemove()) {
                iter.remove(); // Eliminar de la lista
                eliminados++;
                System.out.println("Enemigo eliminado del juego tras animacion de muerte");
            }
        }

        return eliminados;
    }

    /**
     * Verifica colisiones de todos los proyectiles activos con los enemigos.
     *
     * @param stage Stage actual que contiene todos los actores
     * @param enemigos Lista de enemigos para comprobar colision
     */
    public static void comprobarColisionesProyectiles(Stage stage, List<? extends Enemigo> enemigos) {
        if (stage == null || enemigos == null || enemigos.isEmpty()) return;

        for (Actor actor : stage.getActors()) {
            if (actor instanceof FlechaActor) {
                ((FlechaActor) actor).comprobarColisiones(enemigos);
            } else if (actor instanceof HechizoActor) {
                ((HechizoActor) actor).comprobarColisiones(enemigos);
            }
            // Puedes agregar más proyectiles aquí, por ejemplo:
            // else if (actor instanceof BolaDeFuegoActor) { ... }
        }
    }
}
