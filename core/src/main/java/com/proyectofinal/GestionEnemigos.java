package com.proyectofinal;

import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;

import java.util.List;
import java.util.Iterator;

/**
 * Clase de ayuda para gestionar los enemigos en pantalla.
 * Se encarga de eliminar enemigos muertos tras completar su animación.
 */
public class GestionEnemigos {

    /**
     * Actualiza la lista de enemigos, eliminando aquellos que deben ser eliminados
     * tras completar su animación de muerte.
     *
     * @param enemigos Lista de enemigos a actualizar
     * @return Número de enemigos eliminados en esta actualización
     */
    public static int actualizarEnemigos(List<? extends Enemigo> enemigos) {
        if (enemigos == null) return 0;

        int eliminados = 0;
        Iterator<? extends Enemigo> iter = enemigos.iterator();

        while (iter.hasNext()) {
            Enemigo enemigo = iter.next();

            // Si el enemigo está listo para ser eliminado
            if (enemigo.isReadyToRemove()) {
                iter.remove();
                eliminados++;
                System.out.println("Enemigo eliminado del juego tras animación de muerte");
            }
        }

        return eliminados;
    }

    /**
     * Comprueba colisiones de todos los proyectiles con los enemigos.
     *
     * @param stage Stage donde se encuentran los actores
     * @param enemigos Lista de enemigos para comprobar colisiones
     */
    public static void comprobarColisionesProyectiles(Stage stage, List<? extends Enemigo> enemigos) {
        if (stage == null || enemigos == null || enemigos.isEmpty()) return;

        // Comprobar colisiones para todos los actores en el stage
        for (Actor actor : stage.getActors()) {
            if (actor instanceof FlechaActor) {
                ((FlechaActor) actor).comprobarColisiones(enemigos);
            } else if (actor instanceof HechizoActor) {
                ((HechizoActor) actor).comprobarColisiones(enemigos);
            }
        }

        // Imprime un log para comprobar que se está ejecutando
        System.out.println("Comprobando colisiones de proyectiles con " + enemigos.size() + " enemigos");
    }
}
