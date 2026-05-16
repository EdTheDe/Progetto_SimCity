package citylogic.infrastructure;

import citylogic.domain.Citta;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.io.File;
import java.io.IOException;

/**
 * Gestisce il salvataggio e il caricamento dello stato della simulazione su file JSON.
 */
public class PersistenceManager {

    private final ObjectMapper objectMapper;
    private final String saveDirectory;

    public PersistenceManager(String saveDirectory) {
        this.saveDirectory = saveDirectory;
        this.objectMapper = new ObjectMapper();
        
        // Formatta il JSON per renderlo leggibile (Pretty Printing)
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        
        // Assicurati che la cartella di salvataggio esista
        File dir = new File(saveDirectory);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    /**
     * Serializza l'oggetto Citta e lo salva su disco[cite: 484, 493].
     */
    public void salvaPartita(Citta citta, String nomeFile) throws IOException {
        File fileDestinazione = new File(saveDirectory + File.separator + nomeFile + ".json");
        objectMapper.writeValue(fileDestinazione, citta);
        System.out.println("Partita salvata con successo in: " + fileDestinazione.getAbsolutePath());
    }

    /**
     * Legge un file JSON dal disco e ricostruisce l'oggetto Citta.
     */
    public Citta caricaPartita(String nomeFile) throws IOException {
        File fileSorgente = new File(saveDirectory + File.separator + nomeFile + ".json");
        if (!fileSorgente.exists()) {
            throw new IOException("File di salvataggio non trovato: " + nomeFile);
        }
        Citta citta = objectMapper.readValue(fileSorgente, Citta.class);
        System.out.println("Partita caricata con successo.");
        return citta;
    }
}