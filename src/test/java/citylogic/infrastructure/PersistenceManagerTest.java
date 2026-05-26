package citylogic.infrastructure;

import citylogic.core.engine.SimulationEngine;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import org.junit.jupiter.api.Test;
import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceManagerTest {

    @Test
    void testSaveAndLoad() {
        // AC 18.1 & 18.2
        PersistenceManager pm = new PersistenceManager("test_saves");
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid();
        
        stato.addFinanze(4000.0); // Raggiunge 5000.0
        
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        try {
            // Save
            pm.salvaPartita(stato, "test_save");
            File tempFile = new File("test_saves/test_save.json");
            assertTrue(tempFile.exists(), "Il file JSON deve esistere");
            
            // Load
            StatoCitta loadedStato = pm.caricaPartita("test_save");
            assertNotNull(loadedStato, "Il caricamento deve restituire uno StatoCitta");
            assertEquals(5000.0, loadedStato.getFinanze(), "Le finanze devono combaciare dopo il load");
            
        } catch (Exception e) {
            fail("Exception during save/load: " + e.getMessage());
        } finally {
            File tempFile = new File("test_saves/test_save.json");
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}
