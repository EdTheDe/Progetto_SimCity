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

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        persistenceManager = new PersistenceManager(tempDir.toString());
    }

    @Test
    void inizializzaDirectory_CreaDirectorySeNonEsiste() {
        Path nuovaDir = tempDir.resolve("nuova_cartella_salvataggi");
        new PersistenceManager(nuovaDir.toString());

        assertTrue(Files.exists(nuovaDir), "La directory di salvataggio dovrebbe essere creata dal costruttore.");
    }

    @Test
    void salvaPartita_CreaFileJsonCorrettamente() throws IOException {
        StatoCitta statoDummy = new StatoCitta();
        String nomeFile = "salvataggio_test";

        persistenceManager.salvaPartita(statoDummy, nomeFile);

        Path fileCreato = tempDir.resolve(nomeFile + ".json");
        assertTrue(Files.exists(fileCreato), "Il file JSON deve essere creato.");
        assertTrue(Files.size(fileCreato) > 0, "Il file JSON non deve essere vuoto.");
    }

    @Test
    void caricaPartita_LeggeFileJsonCorrettamente() throws IOException {
        StatoCitta statoOriginale = new StatoCitta();
        statoOriginale.setPopolazione(1500); // Simuliamo un dato modificato da salvare

        String nomeFile = "salvataggio_caricamento";
        persistenceManager.salvaPartita(statoOriginale, nomeFile);

        StatoCitta statoCaricato = persistenceManager.caricaPartita(nomeFile);

        assertNotNull(statoCaricato, "Lo stato caricato non deve essere null.");
        assertEquals(1500, statoCaricato.getPopolazione(), "I dati salvati e ricaricati devono combaciare.");
    }

    @Test
    void caricaPartita_LanciaEccezione_QuandoFileNonEsiste() {
        String nomeFileInesistente = "partita_fantasma";

        IOException exception = assertThrows(IOException.class,
                () -> persistenceManager.caricaPartita(nomeFileInesistente));

        assertTrue(exception.getMessage().contains("File di salvataggio non trovato"));
    }
}