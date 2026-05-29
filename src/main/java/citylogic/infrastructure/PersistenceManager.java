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

    /**
     * Crea un oggetto SaveGameData contenente lo stato economico e la posizione
     * esatta di tutti gli edifici presenti sulla mappa.
     */
    public SaveGameData impacchettaDati(StatoCitta stato, UrbanGrid griglia) {
        SaveGameData dati = new SaveGameData();

        dati.setFinanze(stato.getFinanze());
        dati.setPopolazione(stato.getPopolazione());
        dati.setFelicita(stato.getFelicita());
        dati.setEcologia(stato.getEcologia());
        dati.setSicurezza(stato.getSicurezza());
        dati.setSanita(stato.getSanita());
        dati.setLavoro(stato.getLavoro());

        List<SavedEntityData> listaEdifici = new ArrayList<>();
        for (int x = 0; x < griglia.getWidth(); x++) {
            for (int y = 0; y < griglia.getHeight(); y++) {
                Cell cella = griglia.getCell(x, y);
                if (cella.isOccupied()) {
                    UrbanEntity entita = cella.getEntity();
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
     * Ripristina i parametri della simulazione e ricostruisce la griglia
     * posizionando gli edifici memorizzati nel file JSON di salvataggio.
     */
    public void ripristinaDati(SaveGameData dati, StatoCitta stato, UrbanGrid griglia) {
        if (dati == null) return;

        stato.setFinanze(dati.getFinanze());
        stato.setPopolazione(dati.getPopolazione());
        stato.setFelicita(dati.getFelicita());
        stato.setEcologia(dati.getEcologia());
        stato.setSicurezza(dati.getSicurezza());
        stato.setSanita(dati.getSanita());
        stato.setLavoro(dati.getLavoro());

        griglia.azzeraMappa();

        if (dati.getEdifici() != null) {
            for (SavedEntityData d : dati.getEdifici()) {
                try {
                    UrbanEntity nuovaEntita = UrbanEntityFactory.createEntity(d.getTipo());
                    if (nuovaEntita != null) {
                        for (int i = 1; i < d.getLivello(); i++) {
                            nuovaEntita.upgradeLevel();
                        }
                        griglia.placeEntity(nuovaEntita, d.getX(), d.getY());
                    }
                } catch (Exception e) {
                    System.err.println("Impossibile caricare l'edificio a (" + d.getX() + "," + d.getY() + "): " + e.getMessage());
                }
            }
        }
    }
}