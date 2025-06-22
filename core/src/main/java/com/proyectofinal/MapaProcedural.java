package com.proyectofinal;

import java.util.Random;
import com.badlogic.gdx.math.GridPoint2;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

public class MapaProcedural {
    public enum Tile { PASTO_VERDE, PASTO_AMARILLO, CAMINO }

    private final int width, height;
    private final Tile[][] base;
    private final Random rand;

    /**
     * @param width  ancho en tiles
     * @param height alto en tiles
     * @param seed   semilla
     * @param spawnX columna de spawn
     * @param spawnY fila de spawn
     */
    public MapaProcedural(int width, int height, long seed, int spawnX, int spawnY) {
        this.width  = width;
        this.height = height;
        this.rand   = new Random(seed);
        this.base   = new Tile[height][width];

        rellenarVerde();
        generarParchesAmarillo();
        generarCaminoPorSpawn(spawnX, spawnY, 3);  // grosor = 3
    }

    private void rellenarVerde() {
        for (int y=0; y<height; y++)
            for (int x=0; x<width; x++)
                base[y][x] = Tile.PASTO_VERDE;
    }

    private void generarParchesAmarillo() {
        int numP = 5;
        int minT = (width*height)/200;
        int maxT = (width*height)/100;
        for (int i=0; i<numP; i++) {
            int sx = rand.nextInt(width), sy = rand.nextInt(height);
            int tam = minT + rand.nextInt(maxT-minT+1);
            List<GridPoint2> frontier = new ArrayList<>();
            frontier.add(new GridPoint2(sx,sy));
            base[sy][sx] = Tile.PASTO_AMARILLO;
            for (int c=0; c<tam && !frontier.isEmpty(); c++) {
                GridPoint2 p = frontier.remove(rand.nextInt(frontier.size()));
                Integer[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                Collections.shuffle(Arrays.asList(dirs), rand);
                for (Integer[] d: dirs) {
                    int nx = p.x+d[0], ny = p.y+d[1];
                    if (nx>=0&&nx<width&&ny>=0&&ny<height
                        && base[ny][nx]==Tile.PASTO_VERDE) {
                        base[ny][nx]=Tile.PASTO_AMARILLO;
                        frontier.add(new GridPoint2(nx,ny));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Carve un camino recto de grosor t que pasa por (spawnX,spawnY).
     * Elige horizontal o vertical al azar, pero siempre cruzará el spawn.
     */
    private void generarCaminoPorSpawn(int spawnX, int spawnY, int grosor) {
        boolean vertical = rand.nextBoolean();
        if (vertical) {
            // camino vertical en la columna spawnX
            for (int dx = -grosor/2; dx <= grosor/2; dx++) {
                int col = spawnX + dx;
                if (col < 0 || col >= width) continue;
                for (int y=0; y<height; y++)
                    base[y][col] = Tile.CAMINO;
            }
        } else {
            // camino horizontal en la fila spawnY
            for (int dy = -grosor/2; dy <= grosor/2; dy++) {
                int row = spawnY + dy;
                if (row < 0 || row >= height) continue;
                for (int x=0; x<width; x++)
                    base[row][x] = Tile.CAMINO;
            }
        }
    }

    /** Devuelve el tile o verde si estás fuera de mapa */
    public Tile getTile(int x, int y) {
        if (x<0||x>=width||y<0||y>=height) return Tile.PASTO_VERDE;
        return base[y][x];
    }
}
