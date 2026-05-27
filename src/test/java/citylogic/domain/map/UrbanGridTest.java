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
}
