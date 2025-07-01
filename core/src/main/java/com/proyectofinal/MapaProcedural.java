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
    private final long            seed;

    private List<Arbol> arboles = new ArrayList<>();  // Lista para almacenar los árboles generados
    private List<Piedra> piedras = new ArrayList<>();  // Lista para almacenar las piedras generadas
    private static final int TILE_SIZE = 32;  // Tamaño de cada celda en el mapa (ajusta el valor según tu diseño)
    private static final int MIN_DIST = 128;  // Distancia mínima entre los árboles (ajusta el valor según lo necesites)
    private static final int MIN_DIST_PIEDRAS = 64;  // Distancia mínima entre piedras

    /**
     * @param width   ancho en tiles
     * @param height  alto en tiles
     * @param seed    semilla de aleatoriedad
     * @param spawnX  columna del spawn (donde pasa el camino)
     * @param spawnY  fila del spawn
     */
    public MapaProcedural(int width, int height, long seed, int spawnX, int spawnY) {
        this.width           = width;
        this.height          = height;
        this.seed            = seed;
        this.rand            = new Random(seed);
        this.base            = new Tile[height][width];
        this.overlayVerde    = new boolean[height][width];
        this.overlayAmarillo = new boolean[height][width];

        rellenarVerde();
        generarParchesAmarillo();
        generarCaminoPorSpawn(spawnX, spawnY, 2);  // grosor = 2
        generarOverlays();
        generarElementos();  // Llama a la generación de elementos en el mapa
    }

    private void rellenarVerde() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Verificar si está en el borde
                if (x == 0 || x == width - 1 || y == 0 || y == height - 1) {
                    // Asignar piedras en los límites
                    base[y][x] = Tile.CAMINO;
                } else {
                    // Asignar pasto verde en las demás posiciones
                    base[y][x] = Tile.PASTO_VERDE;
                }
            }
        }
    }

    private void generarParchesAmarillo() {
        int numParches = 10 + rand.nextInt(100);
        int minTam = (width * height) / 200;  // 0.5%
        int maxTam = (width * height) / 100;  // 1%
        for (int i = 0; i < numParches; i++) {
            int sx = rand.nextInt(width);
            int sy = rand.nextInt(height);

            // Evitar que los parches amarillos se generen en los bordes
            if (sx == 0 || sx == width - 1 || sy == 0 || sy == height - 1) {
                continue;
            }

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
     * Crea un camino recto de grosor "grosor" que cruza el spawn (spawnX, spawnY) y genera ramificaciones en direcciones opuestas al camino.
     */
    private void generarCaminoPorSpawn(int spawnX, int spawnY, int grosor) {
        boolean vertical = rand.nextBoolean();  // Determina si el camino es vertical o horizontal
        int mitad = grosor / 2;  // Para grosor de 3, mitad será 1 (esto cubre 3 tiles: spawnX-1, spawnX, spawnX+1)

        // Crear camino principal de grosor 3
        if (vertical) {
            // Crear camino vertical de grosor 3
            for (int dx = -mitad; dx <= mitad; dx++) {  // -1, 0, +1 para un grosor de 3
                int col = spawnX + dx;  // Desplazamos las columnas en un rango de -1 a +1 para el grosor
                if (col < 0 || col >= width) continue;  // Aseguramos que no se salga del mapa
                for (int y = 0; y < height; y++) {
                    base[y][col] = Tile.CAMINO;  // Asignamos CAMINO en la columna y todas las filas
                    // Verificamos si debemos generar una ramificación en esta posición
                    generarRamificacion(col, y, vertical);
                }
            }
        } else {
            // Crear camino horizontal de grosor 3
            for (int dy = -mitad; dy <= mitad; dy++) {  // -1, 0, +1 para un grosor de 3
                int row = spawnY + dy;  // Desplazamos las filas en un rango de -1 a +1 para el grosor
                if (row < 0 || row >= height) continue;  // Aseguramos que no se salga del mapa
                for (int x = 0; x < width; x++) {
                    base[row][x] = Tile.CAMINO;  // Asignamos CAMINO en la fila y todas las columnas
                    // Verificamos si debemos generar una ramificación en esta posición
                    generarRamificacion(x, row, vertical);
                }
            }
        }
    }

    /**
     * Genera una ramificación en una posición específica del camino
     * a lo largo del camino principal. Las ramificaciones se generan
     * a los costados del camino, en dirección opuesta al camino principal.
     */
    private void generarRamificacion(int x, int y, boolean vertical) {
        if (rand.nextDouble() < 0.05) {  // % de probabilidad de generar una ramificación
            // Longitud aleatoria entre 8 y 25 tiles
            int longitud = 18 + rand.nextInt(28);

            // Direcciones de ramificación aleatorias
            if (vertical) {
                // Generar ramificación horizontal (de izquierda a derecha o de derecha a izquierda)
                int direccion = rand.nextBoolean() ? 1 : -1;  // 1: hacia la derecha, -1: hacia la izquierda
                for (int i = 0; i < longitud; i++) {
                    int nx = x + i * direccion;
                    if (nx < 0 || nx >= width) continue;  // Verificar si está dentro del mapa
                    // Crear la ramificación con el mismo grosor de 3
                    for (int dy = -1; dy <= 1; dy++) {
                        int ny = y + dy;
                        if (ny >= 0 && ny < height-1) {
                            base[ny][nx] = Tile.CAMINO;  // Asignamos la ramificación
                        }
                    }
                }
            } else {
                // Generar ramificación vertical (de arriba a abajo o de abajo a arriba)
                int direccion = rand.nextBoolean() ? 1 : -1;  // 1: hacia abajo, -1: hacia arriba
                for (int i = 0; i < longitud; i++) {
                    int ny = y + i * direccion;
                    if (ny < 0 || ny >= height-1) continue;  // Verificar si está dentro del mapa
                    // Crear la ramificación con el mismo grosor de 3
                    for (int dx = -1; dx <= 1; dx++) {
                        int nx = x + dx;
                        if (nx >= 0 && nx < width) {
                            base[ny][nx] = Tile.CAMINO;  // Asignamos la ramificación
                        }
                    }
                }
            }
        }
    }

    // Metodo para generar caminos múltiples de grosor 3, sin superposición
    private void generarCaminosAleatorios(int cantidadDeCaminos) {
        for (int i = 0; i < cantidadDeCaminos; i++) {
            // Generar una posición aleatoria de inicio
            int startX = rand.nextInt(width);
            int startY = rand.nextInt(height);

            // Generar dirección del camino (vertical u horizontal)
            boolean vertical = rand.nextBoolean();

            // Generar longitud aleatoria del camino (entre 10 y 25 tiles)
            int longitud = 10 + rand.nextInt(16);  // Longitud entre 10 y 25 tiles

            // Verificar si el camino puede ser generado sin superponerse
            if (verificarSuperposicion(startX, startY, longitud, vertical)) {
                // Si no se superpone, generamos el camino
                generarCamino(startX, startY, longitud, vertical);
            } else {
                // Si se superpone, intentamos crear otro camino
                i--; // Reducción de i para volver a intentarlo
            }
        }
    }

    // Verificar si el camino no se superpone con otro camino ya existente
    private boolean verificarSuperposicion(int startX, int startY, int longitud, boolean vertical) {
        if (vertical) {
            // Verificar superposición en una columna y varias filas
            for (int y = startY; y < startY + longitud && y < height; y++) {
                if (base[y][startX] == Tile.CAMINO) {  // Verifica si ya hay camino
                    return false;
                }
            }
        } else {
            // Verificar superposición en una fila y varias columnas
            for (int x = startX; x < startX + longitud && x < width; x++) {
                if (base[startY][x] == Tile.CAMINO) {  // Verifica si ya hay camino
                    return false;
                }
            }
        }
        return true;
    }

    // Metodo para generar un camino específico de grosor 3, con dirección y longitud
    private void generarCamino(int startX, int startY, int longitud, boolean vertical) {
        int mitad = 1;  // Para grosor de 3, "mitad" es 1 (cubriendo tres tiles: startX-1, startX, startX+1)

        if (vertical) {
            // Generar camino vertical de grosor 3
            for (int dx = -mitad; dx <= mitad; dx++) {  // -1, 0, +1 para un grosor de 3
                int col = startX + dx;
                if (col < 0 || col >= width) continue;  // Aseguramos que no se salga del mapa
                for (int y = startY; y < startY + longitud && y < height; y++) {
                    base[y][col] = Tile.CAMINO;
                }
            }
        } else {
            // Generar camino horizontal de grosor 3
            for (int dy = -mitad; dy <= mitad; dy++) {  // -1, 0, +1 para un grosor de 3
                int row = startY + dy;
                if (row < 0 || row >= height) continue;  // Aseguramos que no se salga del mapa
                for (int x = startX; x < startX + longitud && x < width; x++) {
                    base[row][x] = Tile.CAMINO;
                }
            }
        }
    }

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

    // Método para obtener una textura de árbol aleatoria
    private Texture obtenerArbolAleatorio() {
        try {
            int arbolId = (int) (Math.random() * 4);  // Ejemplo de 4 tipos de árboles
            switch (arbolId) {
                case 0:
                    return new Texture("Mapa/Pasto/arbol_0.png");
                case 1:
                    return new Texture("Mapa/Pasto/arbol_1.png");
                case 2:
                    return new Texture("Mapa/Pasto/arbol_2.png");
                case 3:
                    return new Texture("Mapa/Pasto/arbol_3.png");
                default:
                    return new Texture("Mapa/Pasto/arbol_0.png");  // Valor por defecto
            }
        } catch (Exception e) {
            System.err.println("Error al cargar textura de árbol: " + e.getMessage());
            return new Texture(32, 32, Pixmap.Format.RGBA8888); // Textura vacía como fallback
        }
    }

    // Método para obtener la textura de piedra
    private Texture obtenerTexturaPiedra() {
        try {
            return new Texture("Mapa/Piedras/piedra_Pasto.png");
        } catch (Exception e) {
            System.err.println("Error al cargar textura de piedra: " + e.getMessage());
            return new Texture(32, 32, Pixmap.Format.RGBA8888); // Textura vacía como fallback
        }
    }

    private void generarElementos() {
        // Primero, generamos los árboles
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Generar solo si es pasto verde y con la probabilidad ajustada
                if (base[y][x] == Tile.PASTO_VERDE && Math.random() < 0.005) {  // 0.5% de probabilidad
                    // Comprobar si la posición está lo suficientemente alejada de otros árboles
                    boolean espacioDisponible = true;

                    for (Arbol arbolExistente : arboles) {
                        // Comprobar si la distancia entre el nuevo árbol y el existente es menor que MIN_DIST
                        if (Math.abs(arbolExistente.getCollider().x - x * TILE_SIZE) < MIN_DIST &&
                            Math.abs(arbolExistente.getCollider().y - y * TILE_SIZE) < MIN_DIST) {
                            espacioDisponible = false;
                            break;
                        }
                    }

                    // Si hay espacio, generar el árbol
                    if (espacioDisponible) {
                        Texture arbolTex = obtenerArbolAleatorio();  // Obtener textura aleatoria
                        Arbol arbol = new Arbol(arbolTex);
                        arbol.colocar(x * TILE_SIZE, y * TILE_SIZE);  // Coloca el árbol en la posición
                        arboles.add(arbol);  // Agrega el árbol a la lista de elementos
                    }
                }
            }
        }

        // Ahora, generamos las piedras solo en pasto verde
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                // Generar solo si es pasto verde y con probabilidad más alta
                if (base[y][x] == Tile.PASTO_VERDE && Math.random() < 0.01) {  // 1% de probabilidad
                    // Comprobar si la posición está lo suficientemente alejada de otros elementos
                    boolean espacioDisponible = true;

                    // Verificar distancia con árboles
                    for (Arbol arbolExistente : arboles) {
                        if (Math.abs(arbolExistente.getCollider().x - x * TILE_SIZE) < MIN_DIST &&
                            Math.abs(arbolExistente.getCollider().y - y * TILE_SIZE) < MIN_DIST) {
                            espacioDisponible = false;
                            break;
                        }
                    }

                    // Verificar distancia con otras piedras
                    if (espacioDisponible) {
                        for (Piedra piedraExistente : piedras) {
                            if (Math.abs(piedraExistente.getCollider().x - x * TILE_SIZE) < MIN_DIST_PIEDRAS &&
                                Math.abs(piedraExistente.getCollider().y - y * TILE_SIZE) < MIN_DIST_PIEDRAS) {
                                espacioDisponible = false;
                                break;
                            }
                        }
                    }

                    // Si hay espacio disponible, generar la piedra
                    if (espacioDisponible) {
                        Texture piedraTex = obtenerTexturaPiedra();
                        Piedra piedra = new Piedra(piedraTex);
                        piedra.colocar(x * TILE_SIZE, y * TILE_SIZE);
                        piedras.add(piedra);
                    }
                }
            }
        }
    }

    public List<Arbol> getArboles() {
        return arboles;
    }

    public List<Piedra> getPiedras() {
        return piedras;
    }

    /**
     * Obtiene el tile en (x,y), o PASTO_VERDE si está fuera de rango.
     */
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

    /**
     * Libera los recursos utilizados por el mapa
     */
    public void dispose() {
        // Liberar texturas de árboles
        for (Arbol arbol : arboles) {
            if (arbol.texture != null) {
                arbol.texture.dispose();
            }
        }

        // Liberar texturas de piedras
        for (Piedra piedra : piedras) {
            if (piedra.texture != null) {
                piedra.texture.dispose();
            }
        }
    }
}
