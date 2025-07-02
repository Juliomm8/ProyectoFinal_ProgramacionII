package com.proyectofinal;

import java.util.Random;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.math.GridPoint2;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Arrays;

/**
 * Clase que genera un mapa procedural con distintos tipos de tiles y elementos decorativos.
 * Incluye caminos, parches de pasto amarillo, overlays, arboles y piedras.
 */
public class MapaProcedural {
    // Tipos de tiles posibles
    public enum Tile { PASTO_VERDE, PASTO_AMARILLO, CAMINO }

    // Dimensiones del mapa
    private final int width, height;
    // Matriz de tiles base del mapa
    private final Tile[][] base;
    // Capas para overlays visuales
    private final boolean[][] overlayVerde;
    private final boolean[][] overlayAmarillo;
    private final Random rand;
    private final long seed;

    // Elementos decorativos
    private List<Arbol> arboles = new ArrayList<>();
    private List<Piedra> piedras = new ArrayList<>();

    // Constantes de control visual
    private static final int TILE_SIZE = 32;
    private static final int MIN_DIST = 128;
    private static final int MIN_DIST_PIEDRAS = 64;

    /**
     * Constructor de mapa procedural.
     */
    public MapaProcedural(int width, int height, long seed, int spawnX, int spawnY) {
        this.width = width;
        this.height = height;
        this.seed = seed;
        this.rand = new Random(seed);
        this.base = new Tile[height][width];
        this.overlayVerde = new boolean[height][width];
        this.overlayAmarillo = new boolean[height][width];

        rellenarVerde();
        generarParchesAmarillo();
        generarCaminoPorSpawn(spawnX, spawnY, 2);
        generarOverlays();
        generarElementos();
    }

    /** Llena todo el mapa con pasto verde excepto los bordes. */
    private void rellenarVerde() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1)
                    base[y][x] = Tile.CAMINO;
                else
                    base[y][x] = Tile.PASTO_VERDE;
            }
        }
    }

    /** Genera parches aleatorios de pasto amarillo. */
    private void generarParchesAmarillo() {
        int numParches = 10 + rand.nextInt(100);
        int minTam = (width * height) / 200;
        int maxTam = (width * height) / 100;
        for (int i = 0; i < numParches; i++) {
            int sx = rand.nextInt(width);
            int sy = rand.nextInt(height);
            if (sx == 0 || sx == width - 1 || sy == 0 || sy == height - 1) continue;
            int tam = minTam + rand.nextInt(maxTam - minTam + 1);
            List<GridPoint2> frontier = new ArrayList<>();
            frontier.add(new GridPoint2(sx, sy));
            base[sy][sx] = Tile.PASTO_AMARILLO;
            for (int c = 0; c < tam && !frontier.isEmpty(); c++) {
                GridPoint2 p = frontier.remove(rand.nextInt(frontier.size()));
                Integer[][] dirs = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};
                Collections.shuffle(Arrays.asList(dirs), rand);
                for (Integer[] d : dirs) {
                    int nx = p.x + d[0], ny = p.y + d[1];
                    if (nx >= 0 && nx < width && ny >= 0 && ny < height && base[ny][nx] == Tile.PASTO_VERDE) {
                        base[ny][nx] = Tile.PASTO_AMARILLO;
                        frontier.add(new GridPoint2(nx, ny));
                        break;
                    }
                }
            }
        }
    }

    /** Genera camino recto cruzando el punto de spawn y sus ramificaciones. */
    private void generarCaminoPorSpawn(int spawnX, int spawnY, int grosor) {
        boolean vertical = rand.nextBoolean();
        int mitad = grosor / 2;
        if (vertical) {
            for (int dx = -mitad; dx <= mitad; dx++) {
                int col = spawnX + dx;
                if (col < 0 || col >= width) continue;
                for (int y = 0; y < height; y++) {
                    base[y][col] = Tile.CAMINO;
                    generarRamificacion(col, y, vertical);
                }
            }
        } else {
            for (int dy = -mitad; dy <= mitad; dy++) {
                int row = spawnY + dy;
                if (row < 0 || row >= height) continue;
                for (int x = 0; x < width; x++) {
                    base[row][x] = Tile.CAMINO;
                    generarRamificacion(x, row, vertical);
                }
            }
        }
    }

    /** Genera una ramificación aleatoria desde el camino principal. */
    private void generarRamificacion(int x, int y, boolean vertical) {
        if (rand.nextDouble() < 0.05) {
            int longitud = 18 + rand.nextInt(28);
            int direccion = rand.nextBoolean() ? 1 : -1;
            for (int i = 0; i < longitud; i++) {
                int nx = x + (vertical ? i * direccion : 0);
                int ny = y + (!vertical ? i * direccion : 0);
                if (nx < 0 || nx >= width || ny < 0 || ny >= height) continue;
                for (int o = -1; o <= 1; o++) {
                    int ax = vertical ? nx : nx + o;
                    int ay = vertical ? ny + o : ny;
                    if (ax >= 0 && ax < width && ay >= 0 && ay < height)
                        base[ay][ax] = Tile.CAMINO;
                }
            }
        }
    }

    /** Genera capas de pasto disperso sobre pasto verde y amarillo. */
    private void generarOverlays() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (base[y][x] == Tile.PASTO_VERDE && rand.nextDouble() < 0.05)
                    overlayVerde[y][x] = true;
                if (base[y][x] == Tile.PASTO_AMARILLO && rand.nextDouble() < 0.05)
                    overlayAmarillo[y][x] = true;
            }
        }
    }

    /** Obtiene una textura de arbol aleatoria entre 4 posibles. */
    private Texture obtenerArbolAleatorio() {
        try {
            int arbolId = (int) (Math.random() * 4);
            return new Texture("Mapa/Pasto/arbol_" + arbolId + ".png");
        } catch (Exception e) {
            System.err.println("Error al cargar arbol: " + e.getMessage());
            return new Texture(32, 32, Pixmap.Format.RGBA8888);
        }
    }

    /** Carga la textura para las piedras del mapa. */
    private Texture obtenerTexturaPiedra() {
        try {
            return new Texture("Mapa/Piedras/piedra_Pasto.png");
        } catch (Exception e) {
            System.err.println("Error al cargar piedra: " + e.getMessage());
            return new Texture(32, 32, Pixmap.Format.RGBA8888);
        }
    }

    /** Genera arboles y piedras sobre el mapa. */
    private void generarElementos() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (base[y][x] == Tile.PASTO_VERDE && Math.random() < 0.005) {
                    boolean espacioDisponible = true;
                    for (Arbol a : arboles) {
                        if (Math.abs(a.getCollider().x - x * TILE_SIZE) < MIN_DIST &&
                            Math.abs(a.getCollider().y - y * TILE_SIZE) < MIN_DIST) {
                            espacioDisponible = false;
                            break;
                        }
                    }
                    if (espacioDisponible) {
                        Arbol arbol = new Arbol(obtenerArbolAleatorio());
                        arbol.colocar(x * TILE_SIZE, y * TILE_SIZE);
                        arboles.add(arbol);
                    }
                }
            }
        }

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (base[y][x] == Tile.PASTO_VERDE && Math.random() < 0.01) {
                    boolean espacioDisponible = true;
                    for (Arbol a : arboles) {
                        if (Math.abs(a.getCollider().x - x * TILE_SIZE) < MIN_DIST &&
                            Math.abs(a.getCollider().y - y * TILE_SIZE) < MIN_DIST) {
                            espacioDisponible = false;
                            break;
                        }
                    }
                    for (Piedra p : piedras) {
                        if (Math.abs(p.getCollider().x - x * TILE_SIZE) < MIN_DIST_PIEDRAS &&
                            Math.abs(p.getCollider().y - y * TILE_SIZE) < MIN_DIST_PIEDRAS) {
                            espacioDisponible = false;
                            break;
                        }
                    }
                    if (espacioDisponible) {
                        Piedra piedra = new Piedra(obtenerTexturaPiedra());
                        piedra.colocar(x * TILE_SIZE, y * TILE_SIZE);
                        piedras.add(piedra);
                    }
                }
            }
        }
    }

    // Getters y funciones de ayuda
    public List<Arbol> getArboles() { return arboles; }
    public List<Piedra> getPiedras() { return piedras; }
    public Tile getTile(int x, int y) {
        if (x < 0 || x >= width || y < 0 || y >= height) return Tile.PASTO_VERDE;
        return base[y][x];
    }
    public boolean hasOverlayVerde(int x, int y) {
        return x>=0 && x<width && y>=0 && y<height && overlayVerde[y][x];
    }
    public boolean hasOverlayAmarillo(int x, int y) {
        return x>=0 && x<width && y>=0 && y<height && overlayAmarillo[y][x];
    }

    /** Libera recursos graficos del mapa. */
    public void dispose() {
        for (Arbol a : arboles) if (a.texture != null) a.texture.dispose();
        for (Piedra p : piedras) if (p.texture != null) p.texture.dispose();
    }
}
