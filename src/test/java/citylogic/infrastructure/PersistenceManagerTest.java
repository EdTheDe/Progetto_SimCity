package citylogic.infrastructure;

import citylogic.domain.state.StatoCitta;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceManagerTest {

    @Test
    void testSaveAndLoadWithEntities() {
        // Inizializzazione del manager in una cartella di test temporanea
        PersistenceManager pm = new PersistenceManager("test_saves");
        StatoCitta stato = new StatoCitta(); // Finanze partono da 3000.0

        // Modifichiamo le finanze per verificare che vengano salvate correttamente
        stato.addFinanze(4000.0); // 3000.0 + 4000.0 = 7000.0

        SaveGameData dataToSave = new SaveGameData();
        dataToSave.setFinanze(stato.getFinanze());
        dataToSave.setPopolazione(150);

        // STRESS TEST: Aggiungiamo veri edifici per testare la serializzazione JSON di Jackson
        List<SavedEntityData> edifici = new ArrayList<>();
        edifici.add(new SavedEntityData(5, 5, "Residential", 2));
        edifici.add(new SavedEntityData(10, 2, "PowerPlant", 1));
        dataToSave.setEdifici(edifici);

        File tempFile = new File("test_saves/test_save_full.json");

        try {
            // 1. Test di Salvataggio
            pm.salvaPartita(dataToSave, "test_save_full");
            assertTrue(tempFile.exists(), "Il file JSON deve esistere sul disco dopo il salvataggio");

            // 2. Test di Caricamento
            SaveGameData loadedData = pm.caricaPartita("test_save_full");
            
            // 3. Verifiche di integrità
            assertNotNull(loadedData, "Il caricamento deve restituire un oggetto SaveGameData valido");
            assertEquals(7000.0, loadedData.getFinanze(), "Le finanze devono combaciare dopo il caricamento (3000 + 4000)");
            assertEquals(150, loadedData.getPopolazione(), "La popolazione deve essere salvata correttamente");
            
            // 4. Verifica profonda degli edifici (Cruciale per P3)
            assertEquals(2, loadedData.getEdifici().size(), "Deve ricaricare esattamente 2 edifici");
            assertEquals("Residential", loadedData.getEdifici().get(0).getTipo(), "Il tipo del primo edificio deve essere Residential");
            assertEquals(2, loadedData.getEdifici().get(0).getLivello(), "Il livello del primo edificio deve essere conservato");
            assertEquals(10, loadedData.getEdifici().get(1).getX(), "La coordinata X del secondo edificio deve essere corretta");

        } catch (Exception e) {
            fail("Eccezione imprevista durante il salvataggio/caricamento: " + e.getMessage());
        } finally {
            // Pulizia finale: eliminiamo il file per non lasciare spazzatura nel progetto
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Test
    void testCaricaPartitaInesistenteLanciaEccezione() {
        PersistenceManager pm = new PersistenceManager("test_saves");
        
        // Verifica che il metodo standard lanci un'eccezione se il file non c'è (previene crash silenziosi)
        assertThrows(IOException.class, () -> pm.caricaPartita("salvataggio_fantasma_404"),
                "caricaPartita deve lanciare IOException se si tenta di caricare un file inesistente");
    }

    @Test
    void testLoadGameAbsoluteInesistenteRitornaNull() {
        PersistenceManager pm = new PersistenceManager("test_saves");
        
        // Verifica che il metodo progettato per la UI (JFileChooser) gestisca l'errore ritornando null
        SaveGameData result = pm.loadGame("C:/percorso/inventato/assolutamente/falso.json");
        
        assertNull(result, "loadGame con percorso assoluto errato deve ritornare null (gestito internamente con try-catch)");
    }
}