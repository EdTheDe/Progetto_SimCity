package citylogic;

import citylogic.domain.state.StatoCitta;
import citylogic.infrastructure.PersistenceManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;


import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

class PersistenceManagerTest {

    private PersistenceManager persistenceManager;

    // JUnit crea una cartella temporanea che viene eliminata dopo l'esecuzione del test
    @TempDir
    Path tempDir; 

    @BeforeEach
    void setUp() {
        // Inizializziamo il manager puntando alla directory temporanea
        persistenceManager = new PersistenceManager(tempDir.toString());
    }

    @Test
    void inizializzaDirectory_CreaDirectorySeNonEsiste() {
        // Arrange & Act
        Path nuovaDir = tempDir.resolve("nuova_cartella_salvataggi");
        new PersistenceManager(nuovaDir.toString());

        // Assert
        assertTrue(Files.exists(nuovaDir), "La directory di salvataggio dovrebbe essere creata dal costruttore.");
    }

    @Test
    void salvaPartita_CreaFileJsonCorrettamente() throws IOException {
        // Arrange
        StatoCitta cittaDummy = new StatoCitta(); // Sostituire con un setup appropriato se il costruttore richiede parametri
        String nomeFile = "salvataggio_test";

        // Act
        persistenceManager.salvaPartita(cittaDummy, nomeFile);

        // Assert
        Path fileCreato = tempDir.resolve(nomeFile + ".json");
        assertTrue(Files.exists(fileCreato), "Il file JSON deve essere creato.");
        assertTrue(Files.size(fileCreato) > 0, "Il file JSON non deve essere vuoto.");
    }

    @Test
    void caricaPartita_VerificaIntegritaDati() throws IOException {
        // Arrange: Creiamo una città e alteriamo le statistiche base per assicurarci
        // che vengano lette le modifiche e non i valori di default.
        StatoCitta cittaOriginale = new StatoCitta();
        cittaOriginale.setPopolazione(5000);
        cittaOriginale.addFinanze(2000.0); // Di base è 1000, quindi diventerà 3000.0
        cittaOriginale.setFelicita(85.5);
        cittaOriginale.setEcologia(30.0);

        String nomeFile = "salvataggio_integrita";
        persistenceManager.salvaPartita(cittaOriginale, nomeFile);

        // Act: Ricarichiamo la partita
        StatoCitta cittaCaricata = persistenceManager.caricaPartita(nomeFile);

        // Assert: Verifichiamo che i dati non si siano persi o corrotti
        assertNotNull(cittaCaricata, "La città caricata non deve essere null.");
        assertEquals(5000, cittaCaricata.getPopolazione(), "La popolazione caricata non corrisponde a quella salvata.");
        assertEquals(3000.0, cittaCaricata.getFinanze(), "Le finanze caricate non corrispondono a quelle salvate.");
        assertEquals(85.5, cittaCaricata.getFelicita(), "La felicità caricata non corrisponde a quella salvata.");
        assertEquals(30.0, cittaCaricata.getEcologia(), "L'ecologia caricata non corrisponde a quella salvata.");
    }

    @Test
    void caricaPartita_LanciaEccezione_QuandoFileEIlleggibileCorrotto() throws IOException {
        // Arrange: Creiamo un file JSON invalido/corrotto manualmente nella cartella
        String nomeFileCorrotto = "salvataggio_corrotto";
        Path fileCorrotto = tempDir.resolve(nomeFileCorrotto + ".json");
        Files.writeString(fileCorrotto, "{ JSON ROTTO NON VALIDO ]");

        // Act & Assert: Jackson dovrebbe lanciare una MismatchedInputException o JsonParseException
        // (entrambe figlie di IOException)
        assertThrows(IOException.class,
                () -> persistenceManager.caricaPartita(nomeFileCorrotto),
                "Il sistema deve lanciare un'eccezione se un utente prova a caricare un salvataggio manomesso/corrotto.");
    }

    @Test
    void caricaPartita_LanciaEccezione_QuandoFileNonEsiste() {
        // Arrange
        String nomeFileInesistente = "partita_fantasma";

        // Act & Assert
        IOException exception = assertThrows(IOException.class, 
                () -> persistenceManager.caricaPartita(nomeFileInesistente));
        
        assertTrue(exception.getMessage().contains("File di salvataggio non trovato"));
    }
}