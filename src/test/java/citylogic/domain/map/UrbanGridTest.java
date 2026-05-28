package citylogic.domain.map;

import citylogic.domain.entities.Residential;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UrbanGridTest {

    @Test
    void testGridInitialization() {
        UrbanGrid grid = new UrbanGrid(10, 10);
        // Verifichiamo che i limiti siano impostati (es. 20x20 o in base al costruttore, che attualmente è 24x14)
        // Per AC 4.1 il requisito è la corretta istanziazione
        assertNotNull(grid.getCell(0, 0), "Cells must be instantiated");
        
        // AC 4.2 - Disponibilità delle celle
        assertFalse(grid.getCell(0, 0).isOccupied(), "Initial cell should not be occupied");
    }

    @Test
    void testGridOutOfBounds() {
        UrbanGrid grid = new UrbanGrid(10, 10);
        assertThrows(IllegalArgumentException.class, () -> grid.getCell(-1, -1), "Should throw exception on out of bounds");
        assertThrows(IllegalArgumentException.class, () -> grid.getCell(100, 100), "Should throw exception on out of bounds");
    }

    @Test
    void testRemoveEntity() {
        // AC 10.1 / KAN-10
        UrbanGrid grid = new UrbanGrid(10, 10);
        Residential res = new Residential(100.0, 10.0, 10.0, 50);
        
        grid.placeEntity(res, 5, 5);
        assertTrue(grid.getCell(5, 5).isOccupied(), "Cell should be occupied after placing entity");
        assertTrue(grid.getActiveEntities().contains(res), "Active entities should contain the placed entity");

        boolean removed = grid.removeEntity(5, 5);
        assertTrue(removed, "removeEntity should return true when an entity is removed");
        assertFalse(grid.getCell(5, 5).isOccupied(), "Cell should be empty after removal");
        assertFalse(grid.getActiveEntities().contains(res), "Active entities should not contain removed entity");
    }

    // --- TEST DI STRESS DEL CODICE ---

    @Test
    void testPlaceEntityAlreadyOccupied() {
        UrbanGrid grid = new UrbanGrid(10, 10);
        Residential res1 = new Residential(100.0, 10.0, 10.0, 50);
        Residential res2 = new Residential(100.0, 10.0, 10.0, 50);
        
        grid.placeEntity(res1, 2, 2);
        
        // Tentativo di costruzione su una cella già occupata (Verifica KAN-9)
        assertThrows(IllegalStateException.class, () -> grid.placeEntity(res2, 2, 2), 
            "Deve lanciare IllegalStateException se si prova a costruire su una cella già occupata");
    }

    @Test
    void testPlaceNullEntity() {
        UrbanGrid grid = new UrbanGrid(10, 10);
        
        // Tentativo di passare un'entità null alla griglia
        assertThrows(IllegalArgumentException.class, () -> grid.placeEntity(null, 3, 3), 
            "Deve lanciare IllegalArgumentException se si prova a piazzare un'entità null");
    }

    @Test
    void testRemoveEmptyCell() {
        UrbanGrid grid = new UrbanGrid(10, 10);
        
        // Tentativo di rimozione su una cella appena inizializzata (quindi vuota)
        boolean removed = grid.removeEntity(4, 4);
        
        assertFalse(removed, "removeEntity deve ritornare false se si cerca di demolire su una cella vuota");
    }
}
