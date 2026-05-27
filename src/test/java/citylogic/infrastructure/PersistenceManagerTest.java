package citylogic.infrastructure;

import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceManagerTest {

    @Test
    void testSaveAndLoad() {
        // Inizializzazione del manager in una cartella di test
        PersistenceManager pm = new PersistenceManager("test_saves");
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid(10, 10);

        // Modifichiamo le finanze per verificare che vengano salvate correttamente
        stato.addFinanze(4000.0); // Se parte da 1000.0, ora sarà 5000.0

        // Creiamo il nuovo oggetto contenitore che il manager si aspetta
        SaveGameData dataToSave = new SaveGameData();
        dataToSave.setFinanze(stato.getFinanze());

        // Corretto: ora usa setEdifici come definito in SaveGameData
        dataToSave.setEdifici(new ArrayList<>());

        try {
            // Test di Salvataggio
            pm.salvaPartita(dataToSave, "test_save");
            File tempFile = new File("test_saves/test_save.json");
            assertTrue(tempFile.exists(), "Il file JSON deve esistere dopo il salvataggio");

            // Test di Caricamento
            SaveGameData loadedData = pm.caricaPartita("test_save");
            assertNotNull(loadedData, "Il caricamento deve restituire un oggetto SaveGameData valido");
            assertEquals(5000.0, loadedData.getFinanze(), "Le finanze devono combaciare dopo il caricamento");

        } catch (Exception e) {
            fail("Eccezione durante il salvataggio/caricamento: " + e.getMessage());
        } finally {
            // Pulizia finale: eliminiamo il file di test per non sporcare il progetto
            File tempFile = new File("test_saves/test_save.json");
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }
}