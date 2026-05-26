package citylogic.infrastructure;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.map.Cell;
import citylogic.domain.entities.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class PersistenceManager {

    private final ObjectMapper objectMapper;
    private final Path saveDirectory;

    public PersistenceManager(String directoryPath) {
        this.saveDirectory = Paths.get(directoryPath);
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        inizializzaDirectory();
    }

    private void inizializzaDirectory() {
        try {
            if (Files.notExists(this.saveDirectory)) {
                Files.createDirectories(this.saveDirectory);
            }
        } catch (Exception e) {
            throw new RuntimeException("Impossibile inizializzare la directory di salvataggio: " + saveDirectory, e);
        }
    }

    public void salvaPartita(StatoCitta stato, UrbanGrid logica, String nomeFile) throws Exception {
        DatiSalvataggio ds = new DatiSalvataggio();
        
        ds.finanze = stato.getFinanze();
        ds.popolazione = stato.getPopolazione();
        ds.felicita = stato.getFelicita();
        ds.ecologia = stato.getEcologia();
        ds.lavoro = stato.getLavoro();
        ds.sicurezza = stato.getSicurezza();
        ds.sanita = stato.getSanita();

        for (int i = 0; i < logica.getWidth(); i++) {
            for (int j = 0; j < logica.getHeight(); j++) {
                Cell cella = logica.getCell(i, j);
                if (cella.isOccupied()) {
                    UrbanEntity ent = cella.getEntity();
                    String tipo = "";
                    if (ent instanceof Residential) tipo = "residential";
                    else if (ent instanceof Industrial) tipo = "industrial";
                    else if (ent instanceof Commercial) tipo = "commercial";
                    else if (ent instanceof PowerPlant) tipo = "powerplant";
                    else if (ent instanceof WaterPlant) tipo = "waterplant";
                    else if (ent instanceof PoliceStation) tipo = "police";
                    else if (ent instanceof School) tipo = "school";
                    else if (ent instanceof FireStation) tipo = "firestation";
                    else if (ent instanceof Hospital) tipo = "hospital";
                    else if (ent instanceof Road) tipo = "road";
                    
                    ds.edifici.add(new EdificioSalvato(tipo, i, j, ent.getDevelopmentLevel()));
                }
            }
        }

        Path fileDestinazione = saveDirectory.resolve(nomeFile + ".json");
        objectMapper.writeValue(fileDestinazione.toFile(), ds);
    }

    public void caricaPartita(String nomeFile, StatoCitta stato, UrbanGrid logica) throws Exception {
        Path fileSorgente = saveDirectory.resolve(nomeFile + ".json");
        if (Files.notExists(fileSorgente)) {
            throw new Exception("File di salvataggio non trovato.");
        }
        
        DatiSalvataggio ds = objectMapper.readValue(fileSorgente.toFile(), DatiSalvataggio.class);

        stato.addFinanze(ds.finanze - stato.getFinanze());
        stato.setPopolazione(ds.popolazione);
        stato.setFelicita(ds.felicita);
        stato.setEcologia(ds.ecologia);
        stato.setLavoro(ds.lavoro);
        stato.setSicurezza(ds.sicurezza);
        stato.setSanita(ds.sanita);

        for (int i = 0; i < logica.getWidth(); i++) {
            for (int j = 0; j < logica.getHeight(); j++) {
                logica.removeEntity(i, j);
            }
        }

        for (EdificioSalvato ed : ds.edifici) {
            UrbanEntity entity = UrbanEntityFactory.createEntity(ed.tipo);
            
            if (entity != null) {
                try {
                    java.lang.reflect.Field field = entity.getClass().getSuperclass().getDeclaredField("developmentLevel");
                    field.setAccessible(true);
                    field.setInt(entity, ed.livello);
                } catch (Exception ex) {
                    try {
                        java.lang.reflect.Field field = entity.getClass().getDeclaredField("developmentLevel");
                        field.setAccessible(true);
                        field.setInt(entity, ed.livello);
                    } catch (Exception ex2) {}
                }
                logica.placeEntity(entity, ed.x, ed.y);
            }
        }
    }

    public static class DatiSalvataggio {
        public double finanze;
        public int popolazione;
        public double felicita;
        public double ecologia;
        public double lavoro;
        public double sicurezza;
        public double sanita;
        public List<EdificioSalvato> edifici = new ArrayList<>();
    }

    public static class EdificioSalvato {
        public String tipo;
        public int x;
        public int y;
        public int livello;
        
        public EdificioSalvato() {}
        public EdificioSalvato(String tipo, int x, int y, int livello) {
            this.tipo = tipo;
            this.x = x;
            this.y = y;
            this.livello = livello;
        }
    }
}