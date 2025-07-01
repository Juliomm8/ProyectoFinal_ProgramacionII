package com.proyectofinal;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.utils.Array;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase gestora para manejar la colección de enemigos y su eliminación
 * después de morir
 */
public class GestorEnemigos {
    private Array<Enemigo> enemigos;

    public GestorEnemigos() {
        enemigos = new Array<Enemigo>();
    }

    /**
     * Añade un enemigo a la colección
     * @param enemigo El enemigo a añadir
     */
    public void agregarEnemigo(Enemigo enemigo) {
        enemigos.add(enemigo);
    }

    /**
     * Actualiza todos los enemigos y elimina los que ya han completado
     * su animación de muerte
     * @param deltaTime Tiempo transcurrido desde el último frame
     * @param playerX Posición X del jugador
     * @param playerY Posición Y del jugador
     */
    public void actualizarEnemigos(float deltaTime, float playerX, float playerY) {
        // Actualizar todos los enemigos
        for (Enemigo enemigo : enemigos) {
            enemigo.update(deltaTime, playerX, playerY);
        }

        // Verificar cuántos enemigos están marcados para eliminación
        int marcadosParaEliminar = 0;
        for (Enemigo e : enemigos) {
            if (e.debeEliminarse()) marcadosParaEliminar++;
        }

        if (marcadosParaEliminar > 0) {
            System.out.println(marcadosParaEliminar + " enemigos marcados para eliminación");
        }

        // Eliminar enemigos marcados para eliminación (recorriendo el array desde el final)
        for (int i = enemigos.size - 1; i >= 0; i--) {
            if (enemigos.get(i).debeEliminarse()) {
                System.out.println("Eliminando enemigo de la colección en índice " + i);
                // Liberar recursos antes de eliminar
                enemigos.get(i).dispose();
                enemigos.removeIndex(i);
            }
        }
    }

    /**
     * Renderiza todos los enemigos que no estén marcados para eliminar
     * @param batch SpriteBatch para dibujar
     */
    public void renderizarEnemigos(SpriteBatch batch) {
        for (Enemigo enemigo : enemigos) {
            enemigo.render(batch);
        }
    }

    /**
     * Convierte el Array de enemigos a una Lista para facilitar su uso en comprobaciones de colisiones
     * @return Lista de enemigos
     */
    public List<Enemigo> getEnemigosComoLista() {
        List<Enemigo> listaEnemigos = new ArrayList<Enemigo>();
        for (Enemigo enemigo : enemigos) {
            listaEnemigos.add(enemigo);
        }
        return listaEnemigos;
    }

    /**
     * Devuelve el número de enemigos vivos
     * @return Cantidad de enemigos vivos
     */
    public int getNumeroEnemigosVivos() {
        int vivos = 0;
        for (Enemigo enemigo : enemigos) {
            if (enemigo.estaVivo()) {
                vivos++;
            }
        }
        return vivos;
    }

    /**
     * Obtiene la lista de enemigos
     * @return Array con los enemigos
     */
    public Array<Enemigo> getEnemigos() {
        return enemigos;
    }

    /**
     * Libera los recursos utilizados por los enemigos
     */
    public void dispose() {
        enemigos.clear();
    }
}
