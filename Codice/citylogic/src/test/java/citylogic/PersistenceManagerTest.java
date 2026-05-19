package citylogic.infrastructure;

import citylogic.domain.Citta;
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
        Citta cittaDummy = new Citta(); // Sostituire con un setup appropriato se il costruttore richiede parametri
        String nomeFile = "salvataggio_test";

        // Act
        persistenceManager.salvaPartita(cittaDummy, nomeFile);

        // Assert
        Path fileCreato = tempDir.resolve(nomeFile + ".json");
        assertTrue(Files.exists(fileCreato), "Il file JSON deve essere creato.");
        assertTrue(Files.size(fileCreato) > 0, "Il file JSON non deve essere vuoto.");
    }

    @Test
    void caricaPartita_LeggeFileJsonCorrettamente() throws IOException {
        // Arrange
        Citta cittaOriginale = new Citta(); // Istanza da salvare
        String nomeFile = "salvataggio_caricamento";
        persistenceManager.salvaPartita(cittaOriginale, nomeFile);

        // Act
        Citta cittaCaricata = persistenceManager.caricaPartita(nomeFile);

        // Assert
        assertNotNull(cittaCaricata, "La città caricata non deve essere null.");
        // Nota: se la classe Citta ha implementato il metodo equals(), puoi anche fare:
        // assertEquals(cittaOriginale, cittaCaricata);
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
}ù