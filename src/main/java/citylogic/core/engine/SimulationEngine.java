package citylogic.core.engine;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;
import citylogic.domain.entities.UrbanEntity;
import citylogic.domain.entities.UrbanEntityFactory;
import citylogic.domain.entities.*;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.map.Cell;
import citylogic.core.strategy.PoliticaStrategy;
import citylogic.core.strategy.PoliticaNeutrale;
import citylogic.core.strategy.PoliticaAmbientale;
import citylogic.core.strategy.PoliticaIndustriale;
import citylogic.core.events.RandomEvent;
import citylogic.core.events.PrimaveraEvent;
import citylogic.core.events.CrisiEconomicaEvent;
import citylogic.core.events.GuerraEvent;
import citylogic.infrastructure.SaveGameData;
import citylogic.infrastructure.SavedEntityData;

import java.util.List;
import java.util.ArrayList;
import java.util.Random;

public class SimulationEngine {
    private StatoCitta stato;
    private UrbanGrid griglia;
    private PoliticaStrategy politicaAttiva;

    private List<CityObserver> observers;
    private RandomEvent activeEvent;
    private Random random;
    private int ticksInNegativeFunds = 0;

    public SimulationEngine(StatoCitta stato, UrbanGrid griglia) {
        this.stato = stato;
        this.griglia = griglia;
        this.politicaAttiva = new PoliticaNeutrale();
        this.observers = new ArrayList<>();
        this.random = new Random();
        this.activeEvent = null;
    }

    public void addObserver(CityObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(CityObserver observer) {
        observers.remove(observer);
    }

    private void notifyObservers() {
        for (CityObserver observer : observers) {
            observer.onSimulationUpdated(stato);
        }
    }

    public void setPoliticaAttiva(PoliticaStrategy nuovaPolitica) {
        if (nuovaPolitica != null) {
            this.politicaAttiva = nuovaPolitica;
        }
    }

    public PoliticaStrategy getPoliticaAttiva() {
        return this.politicaAttiva;
    }

    public void tick() {
        TickStats stats = new TickStats();

        // 1. Lettura dei contributi dalla griglia usando il polimorfismo e controllo copertura
        for (UrbanEntity entity : griglia.getActiveEntities()) {
            if (entity.isFunctioning() && checkCoverage(entity)) {
                entity.processTick(stato, stats);
            }
        }

        double modFelicita = calcolaModificatoreFelicita();

        // 2. Gestione Dinamica Popolazione
        if (stato.getPopolazione() < stats.getCapacitaAbitativa()) {
            int crescita = (int) Math.ceil((stats.getCapacitaAbitativa() - stato.getPopolazione()) * 0.2 * modFelicita);
            stato.setPopolazione(stato.getPopolazione() + Math.max(1, crescita));
        } else if (stato.getPopolazione() > stats.getCapacitaAbitativa()) {
            stato.setPopolazione(stats.getCapacitaAbitativa());
        }

        int popAttiva = Math.max(1, stato.getPopolazione()); // Evita divisioni per 0

        // 3. Ricalcolo Metriche di Base
        stato.setLavoro(((double) stats.getPostiLavoro() / popAttiva) * 100.0);
        stato.setSicurezza((((double) stats.getPuntiSicurezza() * modFelicita) / popAttiva) * 100.0);
        stato.setSanita((((double) stats.getPuntiSanita() * modFelicita) / popAttiva) * 100.0);

        stato.setEcologia(100.0 - (((double) stats.getPuntiInquinamento() / popAttiva) * 30.0));

        // 4. Entrate Commerciali
        stato.addFinanze(stats.getRedditoCommerciale() * modFelicita);

        // 5. Applicazione Malus e Dinamiche Globali
        applicaDinamicheGlobali();

        // 6. Applicazione Strategy (Politiche Cittadine)
        politicaAttiva.applicaModificatori(stato);

        // 7. Gestione Eventi Randomici
        gestisciEventi(stats);

        // 8. Controllo Game Over (Finanze negative)
        if (stato.getFinanze() < 0) {
            ticksInNegativeFunds++;
        } else {
            ticksInNegativeFunds = 0;
        }

        // 9. Notifica gli Observer (UI)
        notifyObservers();

        if (ticksInNegativeFunds > 5) {
            for (CityObserver obs : observers) {
                obs.onGameOver();
            }
        }
    }

    private void gestisciEventi(TickStats stats) {
        if (activeEvent != null) {
            // Applica modificatori e decrementa durata
            activeEvent.applyModifiers(stato, stats);
            activeEvent.decrementTick();

            if (activeEvent.isExpired()) {
                activeEvent = null; // Fine evento
            }
        } else {
            // Possibilità del 5% di triggerare un nuovo evento
            if (random.nextDouble() < 0.05) {
                int tipoEvento = random.nextInt(3);
                String eventName = "";
                String eventDesc = "";
                switch (tipoEvento) {
                    case 0: 
                        activeEvent = new PrimaveraEvent(); 
                        eventName = "Primavera";
                        eventDesc = "Il clima è fantastico! La felicità aumenta e la criminalità diminuisce.";
                        break;
                    case 1: 
                        activeEvent = new CrisiEconomicaEvent(); 
                        eventName = "Crisi Economica";
                        eventDesc = "L'economia è in ginocchio. Le entrate commerciali crollano e la criminalità sale.";
                        break;
                    case 2: 
                        activeEvent = new GuerraEvent(); 
                        eventName = "Guerra";
                        eventDesc = "Conflitto armato in corso! Le tasse sono alle stelle ma la popolazione è terrorizzata.";
                        break;
                }
                for (CityObserver obs : observers) {
                    obs.onEventStarted(eventName, eventDesc);
                }
            }
        }
    }

    private boolean checkCoverage(UrbanEntity entity) {
        // Le infrastrutture non hanno bisogno di copertura
        if (entity instanceof PoliceStation || entity instanceof FireStation || 
            entity instanceof Hospital || entity instanceof PowerPlant || 
            entity instanceof WaterPlant || entity instanceof Road || entity instanceof School) {
            return true;
        }

        int x = entity.getX();
        int y = entity.getY();
        if (x < 0 || y < 0) return false;

        int raggioCopertura = 5;
        boolean polizia = false, pompieri = false, ospedale = false, acqua = false, luce = false;
        
        int minX = Math.max(0, x - raggioCopertura);
        int maxX = Math.min(griglia.getWidth() - 1, x + raggioCopertura);
        int minY = Math.max(0, y - raggioCopertura);
        int maxY = Math.min(griglia.getHeight() - 1, y + raggioCopertura);

        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                Cell c = griglia.getCell(i, j);
                if (c.isOccupied()) {
                    UrbanEntity e = c.getEntity();
                    if (e instanceof PoliceStation) polizia = true;
                    if (e instanceof FireStation) pompieri = true;
                    if (e instanceof Hospital) ospedale = true;
                    if (e instanceof WaterPlant) acqua = true;
                    if (e instanceof PowerPlant) luce = true;
                }
            }
        }
        
        return polizia && pompieri && ospedale && acqua && luce;
    }

    private void applicaDinamicheGlobali() {
        if (stato.getEcologia() < 60) {
            stato.setSanita(stato.getSanita() - 15.0);
        }

        double mediaServizi = (stato.getEcologia() + stato.getLavoro() + stato.getSicurezza() + stato.getSanita()) / 4.0;
        double penalita = 0.0;

        if (stato.getEcologia() < 60) penalita += 10.0;
        if (stato.getLavoro() < 60) penalita += 10.0;
        if (stato.getSicurezza() < 60) penalita += 10.0;
        if (stato.getSanita() < 60) penalita += 10.0;

        stato.setFelicita(mediaServizi - penalita);
    }

    private double calcolaModificatoreFelicita() {
        return 0.5 + (stato.getFelicita() / 100.0);
    }

    /**
     * Crea un oggetto SaveGameData contenente lo stato economico e la posizione
     * esatta di tutti gli edifici presenti sulla mappa.
     */
    public SaveGameData impacchettaDatiSalvataggio() {
        SaveGameData dati = new SaveGameData();

        // Salva i parametri correnti della città
        dati.setFinanze(this.stato.getFinanze());
        dati.setPopolazione(this.stato.getPopolazione());
        dati.setFelicita(this.stato.getFelicita());
        dati.setEcologia(this.stato.getEcologia());
        dati.setSicurezza(this.stato.getSicurezza());
        dati.setSanita(this.stato.getSanita());
        dati.setLavoro(this.stato.getLavoro());

        // Scansiona la griglia per trovare tutti gli edifici da salvare
        List<SavedEntityData> listaEdifici = new ArrayList<>();
        for (int x = 0; x < griglia.getWidth(); x++) {
            for (int y = 0; y < griglia.getHeight(); y++) {
                Cell cella = griglia.getCell(x, y);
                if (cella.isOccupied()) {
                    UrbanEntity entita = cella.getEntity();
                    // Ottiene il nome della classe come stringa (es. "Residential", "Road")
                    String tipoSemplice = entita.getClass().getSimpleName();
                    int livello = entita.getDevelopmentLevel();

                    // Crea il record con coordinate x, y, tipo e livello di upgrade
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
    public void ripristinaDatiSalvataggio(SaveGameData dati) {
        if (dati == null) return;

        // 1. Ripristina i valori numerici dello stato della città
        this.stato.setFinanze(dati.getFinanze());
        this.stato.setPopolazione(dati.getPopolazione());
        this.stato.setFelicita(dati.getFelicita());
        this.stato.setEcologia(dati.getEcologia());
        this.stato.setSicurezza(dati.getSicurezza());
        this.stato.setSanita(dati.getSanita());
        this.stato.setLavoro(dati.getLavoro());

        // 2. Svuota completamente la griglia logica corrente per evitare sovrapposizioni
        this.griglia.azzeraMappa();

        // 3. Rigenera gli edifici dalle coordinate salvate
        if (dati.getEdifici() != null) {
            for (SavedEntityData d : dati.getEdifici()) {
                try {
                    // Utilizza la factory per istanziare nuovamente l'edificio corretto tramite la stringa del tipo
                    UrbanEntity nuovaEntita = UrbanEntityFactory.createEntity(d.getTipo());

                    if (nuovaEntita != null) {
                        // Esegue i cicli di upgrade necessari per ritornare al livello salvato
                        for (int i = 1; i < d.getLivello(); i++) {
                            nuovaEntita.upgradeLevel();
                        }
                        // Riposiziona l'edificio ricostruito nella griglia logica
                        this.griglia.placeEntity(nuovaEntita, d.getX(), d.getY());
                    }
                } catch (Exception e) {
                    System.err.println("Impossibile caricare l'edificio a (" + d.getX() + "," + d.getY() + "): " + e.getMessage());
                }
            }
        }
    }
}