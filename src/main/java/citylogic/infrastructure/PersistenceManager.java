package citylogic.infrastructure;

import citylogic.domain.state.Citta;
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
     * Serializza l'intero oggetto Citta e lo scrive su disco.
     */
    public void salvaPartita(Citta citta, String nomeFile) throws IOException {
        Path fileDestinazione = saveDirectory.resolve(nomeFile + ".json");
        objectMapper.writeValue(fileDestinazione.toFile(), citta);
        System.out.println("Partita salvata con successo in: " + fileDestinazione.toAbsolutePath());
    }

    /**
     * Legge il JSON dal disco e ricostruisce l'albero degli oggetti in memoria.
     */
    public Citta caricaPartita(String nomeFile) throws IOException {
        Path fileSorgente = saveDirectory.resolve(nomeFile + ".json");
        
        if (Files.notExists(fileSorgente)) {
            throw new IOException("File di salvataggio non trovato: " + fileSorgente.getFileName());
        }
        
        Citta citta = objectMapper.readValue(fileSorgente.toFile(), Citta.class);
        System.out.println("Partita caricata con successo.");
        return citta;
    }
}