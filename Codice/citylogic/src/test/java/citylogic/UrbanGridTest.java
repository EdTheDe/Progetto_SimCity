//N
package citylogic; // <-- Il pacchetto ora è solo citylogic!

// Devi importare le tue classi perché ora il test si trova in una cartella diversa da loro
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.map.Cell;
import citylogic.domain.entities.UrbanEntity;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class UrbanGridTest {

    private UrbanGrid grid;

    /**
     * Classe concreta "fittizia" usata solo per testare l'inserimento
     * senza dipendere dalle implementazioni reali di Edifici.
     */
    static class TestEntity extends UrbanEntity {
        public TestEntity(double cost) {
            super(cost);
        }
    }

    // Questo metodo viene eseguito in automatico prima di ogni singolo @Test
    @BeforeEach
    void setUp() {
        grid = new UrbanGrid(); // Genera una mappa nuova per ogni test
    }

    @Test
    void testGridInitialization() {
        // Verifica la KAN-4: La mappa deve essere 20x20
        assertEquals(20, grid.getWidth(), "La larghezza della griglia dovrebbe essere 20");
        assertEquals(20, grid.getHeight(), "L'altezza della griglia dovrebbe essere 20");

        // Verifica la KAN-4: Tutte le celle devono essere vuote all'inizio
        Cell cellaProva = grid.getCell(5, 5);
        assertNotNull(cellaProva, "La cella non dovrebbe essere null");
        assertFalse(cellaProva.isOccupied(), "La cella dovrebbe essere vuota appena creata");
    }

    @Test
    void testCellPlacementAndOccupation() {
        Cell cell = grid.getCell(10, 10);
        TestEntity edificio1 = new TestEntity(150.0);

        // Posizioniamo l'entità
        cell.setEntity(edificio1);
        
        // Verifichiamo che la cella risulti occupata e che i dati base siano corretti
        assertTrue(cell.isOccupied(), "La cella deve risultare occupata dopo la costruzione");
        assertEquals(edificio1, cell.getEntity(), "L'entità nella cella deve essere quella posizionata");
        assertEquals(1, cell.getEntity().getDevelopmentLevel(), "Il livello di partenza deve essere 1 (KAN-21)");

        // Verifica la KAN-9: Il sistema deve rifiutare la costruzione su una cella occupata
        TestEntity edificio2 = new TestEntity(200.0);
        
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            cell.setEntity(edificio2);
        });
        
        assertTrue(exception.getMessage().contains("già occupata"), 
                   "Dovrebbe lanciare un'eccezione se si prova a costruire sopra un altro edificio");
    }

    @Test
    void testOutOfBoundsException() {
        // Verifica che il sistema gestisca correttamente coordinate fuori mappa
        assertThrows(IllegalArgumentException.class, () -> {
            grid.getCell(20, 20); // Indici validi vanno da 0 a 19
        });
        
        assertThrows(IllegalArgumentException.class, () -> {
            grid.getCell(-1, 5);
        });
    }
}