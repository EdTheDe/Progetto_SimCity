// Nicolò
package citylogic.domain.map;

import java.util.List;
import java.util.ArrayList;
import citylogic.domain.entities.UrbanEntity;

/**
 * Mappa della simulazione (logica).
 * Gestisce array bidimensionali di oggetti Cell.
 */
public class UrbanGrid {

    private final int width;
    private final int height;
    private final Cell[][] grid;
    private List<UrbanEntity> activeEntities;   // Nuova lista per tenere traccia di tutto ciò che è costruito

    /**
     * Costruttore di default. 
     * KAN-4 -> "generare una mappa logica 20x20".
     */
    public UrbanGrid() {
        this.width = 20;
        this.height = 20;
        this.grid = new Cell[width][height];
        initializeGrid();
        this.activeEntities = new ArrayList<>(); // lista aggiunta alla griglia
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

    /**
     * Per ciclare la lista degli edifici.
     * @return
     */
    public List<UrbanEntity> getActiveEntities() {
        return activeEntities;
    }

    /**
     * Piazza un'entità sulla griglia se la cella è valida e libera.
     * @param entity L'entità da piazzare (es. Residential, Commercial)
     * @param x Coordinata X
     * @param y Coordinata Y
     */
    public void placeEntity(UrbanEntity entity, int x, int y) {
        // 1. Recupera la cella in sicurezza (il controllo Out-of-Bounds è già in getCell!)
        Cell targetCell = getCell(x, y);

        // 2. Assegna l'entità.
        // Se la cella è occupata, setEntity() in Cell.java lancerà in automatico la IllegalStateException
        targetCell.setEntity(entity);

        // 3. Se la riga sopra va a buon fine (nessuna eccezione), registriamo l'edificio
        activeEntities.add(entity);
    }

    /**
     * Rimuove un'entità dalla griglia alle coordinate specificate (Demolizione).
     * @param x Coordinata X
     * @param y Coordinata Y
     * @return true se un'entità è stata effettivamente rimossa, false se la cella era già vuota.
     */
    public boolean removeEntity(int x, int y) {
        // 1. Recupera la cella (isWithinBounds è gestito in automatico)
        Cell targetCell = getCell(x, y);

        // 2. Controlla se c'è effettivamente qualcosa da demolire
        if (!targetCell.isOccupied()) {
            return false; // La cella è già vuota, non facciamo nulla
        }

        // 3. Salviamo un riferimento all'entità prima di cancellarla dalla cella
        UrbanEntity entityToRemove = targetCell.getEntity();

        // 4. Svuotiamo la cella
        targetCell.clearEntity();

        // 5. Rimuoviamo l'entità dalla lista globale per fermare i calcoli del "Tick"
        activeEntities.remove(entityToRemove);

        return true;
    }
}