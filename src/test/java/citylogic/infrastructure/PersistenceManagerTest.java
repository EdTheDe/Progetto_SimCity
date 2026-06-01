package citylogic.infrastructure;

import citylogic.domain.state.StatoCitta;
import org.junit.jupiter.api.Test;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test Unitari e di Integrazione per garantire che il salvataggio su file funzioni a dovere.
 */
public class PersistenceManagerTest {

    @Test
    void testSaveAndLoadWithEntities() {
        // Utilizziamo una directory di test dedicata in modo da non "sporcare" i salvataggi reali dei giocatori
        PersistenceManager pm = new PersistenceManager("test_saves");
        StatoCitta stato = new StatoCitta(); // Finanze di base: 4500.0

        // Simuliamo un aumento di soldi
        stato.addFinanze(4000.0); // Totale atteso: 8500.0

        // Prepariamo i dati fasulli da salvare
        SaveGameData dataToSave = new SaveGameData();
        dataToSave.setFinanze(stato.getFinanze());
        dataToSave.setPopolazione(150);

        // Prepariamo un paio di edifici per vedere se la libreria serializza bene le liste
        List<SavedEntityData> edifici = new ArrayList<>();
        edifici.add(new SavedEntityData(5, 5, "Residential", 2));
        edifici.add(new SavedEntityData(10, 2, "PowerPlant", 1));
        dataToSave.setEdifici(edifici);

        // Oggetto file che useremo per le verifiche
        File tempFile = new File("test_saves/test_save_full.json");

        try {
            // 1. Tenta il salvataggio
            pm.salvaPartita(dataToSave, "test_save_full");
            // Verifica se il file è stato effettivamente scritto su disco
            assertTrue(tempFile.exists(), "Il file JSON deve esistere sul disco dopo il salvataggio");

            // 2. Tenta il caricamento da disco
            SaveGameData loadedData = pm.caricaPartita("test_save_full");

            // 3. Verifica l'integrità dei dati primitivi
            assertNotNull(loadedData, "Il caricamento deve restituire un oggetto SaveGameData valido");
            assertEquals(8500.0, loadedData.getFinanze(), "Le finanze devono combaciare dopo il caricamento (4500 + 4000)");
            assertEquals(150, loadedData.getPopolazione(), "La popolazione deve essere salvata correttamente");

            // 4. Verifica profonda (Deep Inspection) della lista degli edifici
            assertEquals(2, loadedData.getEdifici().size(), "Deve ricaricare esattamente 2 edifici");
            assertEquals("Residential", loadedData.getEdifici().get(0).getTipo(), "Il tipo del primo edificio deve essere Residential");
            assertEquals(2, loadedData.getEdifici().get(0).getLivello(), "Il livello del primo edificio deve essere conservato");
            assertEquals(10, loadedData.getEdifici().get(1).getX(), "La coordinata X del secondo edificio deve essere corretta");

        } catch (Exception e) {
            fail("Eccezione imprevista durante il salvataggio/caricamento: " + e.getMessage());
        } finally {
            // Blocco finally vitale: si assicura di cancellare il file temporaneo anche se i test falliscono
            if (tempFile.exists()) {
                tempFile.delete();
            }
        }
    }

    @Test
    void testCaricaPartitaInesistenteLanciaEccezione() {
        PersistenceManager pm = new PersistenceManager("test_saves");

        // Verifichiamo che il programma si comporti correttamente quando un file non esiste.
        // assertThrows si aspetta che la lambda function generi un'eccezione specifica.
        assertThrows(IOException.class, () -> pm.caricaPartita("salvataggio_fantasma_404"),
                "caricaPartita deve lanciare IOException se si tenta di caricare un file inesistente");
    }

    @Test
    void testLoadGameAbsoluteInesistenteRitornaNull() {
        PersistenceManager pm = new PersistenceManager("test_saves");

        // Questo test controlla il metodo pensato per la UI, che usa try-catch per non far crashare l'app grafica
        // ma si limita a ritornare null in caso di fallimento.
        SaveGameData result = pm.loadGame("C:/percorso/inventato/assolutamente/falso.json");

        assertNull(result, "loadGame con percorso assoluto errato deve ritornare null (gestito internamente con try-catch)");
    }
}