package com.proyectofinal;

import java.util.Random;
import com.badlogic.gdx.math.GridPoint2;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

/**
 * Genera un mapa con:
 *  - parches compactos de pasto amarillo,
 *  - varios caminos rectos finos,
 *  - overlays de hierba dispersa.
 */
public class MapaProcedural {
    public enum Tile { PASTO_VERDE, PASTO_AMARILLO, CAMINO }

    private final int width, height;
    private final Tile[][]        base;
    private final boolean[][]     overlayVerde;
    private final boolean[][]     overlayAmarillo;
    private final Random          rand;

    /**
     * @param width  ancho en tiles
     * @param height alto en tiles
     * @param seed   semilla de aleatoriedad
     */
    public MapaProcedural(int width, int height, long seed) {
        this.width           = width;
        this.height          = height;
        this.rand            = new Random(seed);
        this.base            = new Tile[height][width];
        this.overlayVerde    = new boolean[height][width];
        this.overlayAmarillo = new boolean[height][width];

        rellenarVerde();
        generarParchesAmarillo();
        generarCaminos(4, 1);  // 4 caminos, grosor=1 tile
        generarOverlays();
    }

    private void rellenarVerde() {
        for (int y = 0; y < height; y++)
            for (int x = 0; x < width; x++)
                base[y][x] = Tile.PASTO_VERDE;
    }

    private void generarParchesAmarillo() {
        int numParches   = 6;
        int minTam       = (width * height) / 500;  // ~0.2%
        int maxTam       = (width * height) / 200;  // ~0.5%
        for (int i = 0; i < numParches; i++) {
            int sx  = rand.nextInt(width), sy = rand.nextInt(height);
            int tam = minTam + rand.nextInt(maxTam - minTam + 1);

            List<GridPoint2> frontier = new ArrayList<>();
            frontier.add(new GridPoint2(sx, sy));
            base[sy][sx] = Tile.PASTO_AMARILLO;

            for (int c = 0; c < tam && !frontier.isEmpty(); c++) {
                GridPoint2 p = frontier.remove(rand.nextInt(frontier.size()));
                Integer[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
                Collections.shuffle(Arrays.asList(dirs), rand);

                for (Integer[] d : dirs) {
                    int nx = p.x + d[0], ny = p.y + d[1];
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height
                        && base[ny][nx] == Tile.PASTO_VERDE) {
                        base[ny][nx] = Tile.PASTO_AMARILLO;
                        frontier.add(new GridPoint2(nx, ny));
                        break;
                    }
                }
            }
        }
    }

    /**
     * Genera varios caminos rectos finos.
     * @param numCaminos cuántas líneas generar
     * @param grosor     grosor de cada línea en tiles
     */
    private void generarCaminos(int numCaminos, int grosor) {
        int mitad = grosor / 2;
        for (int i = 0; i < numCaminos; i++) {
            if (rand.nextBoolean()) {
                // vertical
                int col = rand.nextInt(width);
                for (int dx = -mitad; dx <= mitad; dx++) {
                    int cc = col + dx;
                    if (cc < 0 || cc >= width) continue;
                    for (int y = 0; y < height; y++) {
                        base[y][cc] = Tile.CAMINO;
                    }
                }
            } else {
                // horizontal
                int row = rand.nextInt(height);
                for (int dy = -mitad; dy <= mitad; dy++) {
                    int rr = row + dy;
                    if (rr < 0 || rr >= height) continue;
                    for (int x = 0; x < width; x++) {
                        base[rr][x] = Tile.CAMINO;
                    }
                }
            }
        }
    }

    private void generarOverlays() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (base[y][x] == Tile.PASTO_VERDE
                    && rand.nextDouble() < 0.05) {
                    overlayVerde[y][x] = true;
                }
                if (base[y][x] == Tile.PASTO_AMARILLO
                    && rand.nextDouble() < 0.05) {
                    overlayAmarillo[y][x] = true;
                }
            }
        }
    }

    /** Devuelve el tile en (x,y) o PASTO_VERDE si está fuera de rango */
    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height)
            return Tile.PASTO_VERDE;
        return base[y][x];
    }

    public boolean hasOverlayVerde(int x, int y) {
        return x>=0&&x<width&&y>=0&&y<height && overlayVerde[y][x];
    }
    public boolean hasOverlayAmarillo(int x, int y) {
        return x>=0&&x<width&&y>=0&&y<height && overlayAmarillo[y][x];
    }
}
