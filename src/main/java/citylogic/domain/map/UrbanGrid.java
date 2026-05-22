// Nicolò
package citylogic.domain.map;

/**
 * Mappa della simulazione (logica).
 * Gestisce array bidimensionali di oggetti Cell.
 */
public class UrbanGrid {

    private final int width;
    private final int height;
    private final Cell[][] grid;

    /**
     * Costruttore di default. 
     * KAN-4 -> "generare una mappa logica 20x20".
     */
    public UrbanGrid() {
        this.width = 20;
        this.height = 20;
        this.grid = new Cell[width][height];
        initializeGrid();
    }

    private void initializeGrid() {
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                grid[i][j] = new Cell(i, j);
            }
        }
    }

    public int getWidth() { 
        return width; 
    }
    
    public int getHeight() { 
        return height; 
    }

    /**
     * Metodi per controllare la validità delle coordinate.
     */
    public boolean isWithinBounds(int x, int y) {
        return x >= 0 && x < width && y >= 0 && y < height;
    }

    /**
     * Gestione cella in sicurezza.
     */
    public Cell getCell(int x, int y) {
        if (!isWithinBounds(x, y)) {
            throw new IllegalArgumentException("Coordinates out of bounds: (" + x + ", " + y + ")");
        }
        return grid[x][y];
    }
}