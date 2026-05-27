package citylogic.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Gestisce il salvataggio e il caricamento dello stato della simulazione su file JSON.
 * Ottimizzato con Java NIO.2 per operazioni sicure sul File System locale.
 */
public class PersistenceManager {

    private final ObjectMapper objectMapper;
    private final Path saveDirectory;

    public PersistenceManager(String directoryPath) {
        this.saveDirectory = Paths.get(directoryPath);
        this.objectMapper = new ObjectMapper();

        // Configurazioni per Jackson: formattazione leggibile e supporto per le date
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.objectMapper.registerModule(new JavaTimeModule());
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        inizializzaDirectory();
    }

    /**
     * Assicura che la directory di destinazione esista prima di operare.
     */
    private void inizializzaDirectory() {
        try {
            if (Files.notExists(this.saveDirectory)) {
                Files.createDirectories(this.saveDirectory);
            }
        } catch (IOException e) {
            throw new RuntimeException("Impossibile inizializzare la directory di salvataggio: " + saveDirectory, e);
        }
    }

    /**
     * Serializza l'intero oggetto SaveGameData (StatoCitta + Edifici) e lo scrive su disco.
     */
    public void salvaPartita(SaveGameData datiPartita, String nomeFile) throws IOException {
        Path fileDestinazione = saveDirectory.resolve(nomeFile + ".json");
        objectMapper.writeValue(fileDestinazione.toFile(), datiPartita);
        System.out.println("Partita salvata con successo in: " + fileDestinazione.toAbsolutePath());
    }

    /**
     * Legge il JSON dal disco (tramite nome del file) e ricostruisce l'albero degli oggetti in memoria.
     */
    public SaveGameData caricaPartita(String nomeFile) throws IOException {
        Path fileSorgente = saveDirectory.resolve(nomeFile + ".json");

        if (Files.notExists(fileSorgente)) {
            throw new IOException("File di salvataggio non trovato: " + fileSorgente.getFileName());
        }

        SaveGameData datiPartita = objectMapper.readValue(fileSorgente.toFile(), SaveGameData.class);
        System.out.println("Partita caricata con successo.");
        return datiPartita;
    }

    /**
     * NUOVO: Legge il JSON dal disco tramite percorso ASSOLUTO (ideale per il JFileChooser dell'Interfaccia Grafica).
     */
    public SaveGameData loadGame(String absolutePath) {
        try {
            Path fileSorgente = Paths.get(absolutePath);
            if (Files.notExists(fileSorgente)) {
                System.err.println("File di salvataggio non trovato al percorso: " + absolutePath);
                return null;
            }

            SaveGameData datiPartita = objectMapper.readValue(fileSorgente.toFile(), SaveGameData.class);
            System.out.println("Partita caricata con successo da: " + absolutePath);
            return datiPartita;

        } catch (IOException e) {
            System.err.println("Errore durante il caricamento del salvataggio: " + e.getMessage());
            return null;
        }
    }
}