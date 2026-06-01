package citylogic.infrastructure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.map.Cell;
import citylogic.domain.entities.UrbanEntity;
import citylogic.domain.entities.UrbanEntityFactory;

/**
 * Gestisce il salvataggio e il caricamento dello stato della simulazione su file JSON.
 * Utilizza le API Java NIO.2 (java.nio.file) per garantire operazioni sicure ed efficienti sul File System.
 */
public class PersistenceManager {

    // Mapper principale per la conversione tra oggetti Java e stringhe JSON
    private final ObjectMapper objectMapper;
    // Percorso della cartella dove verranno salvati i file
    private final Path saveDirectory;

    /**
     * Costruttore: imposta la directory e configura il parser JSON.
     * @param directoryPath Il percorso della cartella di salvataggio.
     */
    public PersistenceManager(String directoryPath) {
        this.saveDirectory = Paths.get(directoryPath);
        this.objectMapper = new ObjectMapper();

        // Configurazioni per Jackson:
        // 1. INDENT_OUTPUT rende il JSON "pretty printed" (facile da leggere per gli umani)
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        // 2. JavaTimeModule supporta le nuove API di data/ora di Java 8+
        this.objectMapper.registerModule(new JavaTimeModule());
        // 3. Evita di scrivere le date come timestamp numerici incomprensibili
        this.objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // Assicura che la cartella esista all'avvio
        inizializzaDirectory();
    }

    /**
     * Metodo privato che crea la directory di destinazione se non esiste già.
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
     * Serializza l'oggetto SaveGameData (che fa da DTO - Data Transfer Object) e lo scrive su disco.
     */
    public void salvaPartita(SaveGameData datiPartita, String nomeFile) throws IOException {
        Path fileDestinazione = saveDirectory.resolve(nomeFile + ".json");
        objectMapper.writeValue(fileDestinazione.toFile(), datiPartita);
        System.out.println("Partita salvata con successo in: " + fileDestinazione.toAbsolutePath());
    }

    /**
     * Legge il JSON dal disco tramite il nome del file (nella cartella di default) e ricostruisce gli oggetti.
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
     * Legge il JSON dal disco tramite un percorso ASSOLUTO.
     * È utile per le interfacce grafiche (es. quando l'utente usa un JFileChooser per selezionare il file ovunque nel PC).
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

    /**
     * Raccoglie i dati vitali dalla simulazione in corso (StatoCitta e Mappa) e li impacchetta in un oggetto isolato e pronto per il salvataggio.
     */
    public SaveGameData impacchettaDati(StatoCitta stato, UrbanGrid griglia) {
        SaveGameData dati = new SaveGameData();

        // Salvataggio statistiche globali
        dati.setFinanze(stato.getFinanze());
        dati.setPopolazione(stato.getPopolazione());
        dati.setFelicita(stato.getFelicita());
        dati.setEcologia(stato.getEcologia());
        dati.setSicurezza(stato.getSicurezza());
        dati.setSanita(stato.getSanita());
        dati.setLavoro(stato.getLavoro());

        // Scansione della griglia per salvare solo le celle occupate (ottimizzazione)
        List<SavedEntityData> listaEdifici = new ArrayList<>();
        for (int x = 0; x < griglia.getWidth(); x++) {
            for (int y = 0; y < griglia.getHeight(); y++) {
                Cell cella = griglia.getCell(x, y);
                if (cella.isOccupied()) {
                    UrbanEntity entita = cella.getEntity();
                    // Salviamo il nome della classe per sapere cosa ricostruire in fase di caricamento
                    String tipoSemplice = entita.getClass().getSimpleName();
                    int livello = entita.getDevelopmentLevel();
                    listaEdifici.add(new SavedEntityData(x, y, tipoSemplice, livello));
                }
            }
        }
        dati.setEdifici(listaEdifici);

        return dati;
    }

    /**
     * Processo inverso: prende i dati da un SaveGameData e ricrea il mondo di gioco sovrascrivendo quello attuale.
     */
    public void ripristinaDati(SaveGameData dati, StatoCitta stato, UrbanGrid griglia) {
        if (dati == null) return;

        // Ripristino delle statistiche
        stato.setFinanze(dati.getFinanze());
        stato.setPopolazione(dati.getPopolazione());
        stato.setFelicita(dati.getFelicita());
        stato.setEcologia(dati.getEcologia());
        stato.setSicurezza(dati.getSicurezza());
        stato.setSanita(dati.getSanita());
        stato.setLavoro(dati.getLavoro());

        // Pulizia della vecchia mappa
        griglia.azzeraMappa();

        // Riposizionamento degli edifici
        if (dati.getEdifici() != null) {
            for (SavedEntityData d : dati.getEdifici()) {
                try {
                    // Utilizza la Factory per ricreare le istanze corrette in base al nome salvato (es. "Residential")
                    UrbanEntity nuovaEntita = UrbanEntityFactory.createEntity(d.getTipo());
                    if (nuovaEntita != null) {
                        // Ripristina i livelli (partendo da 1 e facendo upgrade)
                        for (int i = 1; i < d.getLivello(); i++) {
                            nuovaEntita.upgradeLevel();
                        }
                        // Piazza l'entità sulla griglia
                        griglia.placeEntity(nuovaEntita, d.getX(), d.getY());
                    }
                } catch (Exception e) {
                    System.err.println("Impossibile caricare l'edificio a (" + d.getX() + "," + d.getY() + "): " + e.getMessage());
                }
            }
        }
    }
}