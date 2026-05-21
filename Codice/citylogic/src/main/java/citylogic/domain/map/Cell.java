// Nicolò
package citylogic.domain.map;

import citylogic.domain.entities.UrbanEntity;

/**
 * Rappresenta una cella (cell): unità singola della griglia (UrbanGrid).
 * Contiene le sue coordinate e una possibile entità (UrbanEntity).
 */
public class Cell {

    private final int x;
    private final int y;
    private UrbanEntity entity;

    /**
     * Inizializza una cella vuota alle coordinate date.
     * KAN-4 -> "Tutte le celle devono risultare inizialmente vuote".
     */
    public Cell(int x, int y) {
        this.x = x;
        this.y = y;
        this.entity = null; 
    }

    public int getX() { 
        return x; 
    }
    
    public int getY() { 
        return y; 
    }

    /**
     * Controlla se la cella è vuota o no.
     * KAN-9 -> occupazione cell.
     */
    public boolean isOccupied() {
        return this.entity != null;
    }

    public UrbanEntity getEntity() {
        return entity;
    }

    public void setEntity(UrbanEntity entity) {
        if (isOccupied()) {
            throw new IllegalStateException("Cannot build here: Cell (" + x + ", " + y + ") is already occupied.");
        }
        this.entity = entity;
    }

    public void clearEntity() {
        this.entity = null;
    }
}