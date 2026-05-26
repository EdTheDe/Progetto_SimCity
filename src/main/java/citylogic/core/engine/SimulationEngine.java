package citylogic.core.engine;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;
import citylogic.domain.entities.UrbanEntity;
import citylogic.domain.map.UrbanGrid;
import citylogic.core.strategy.PoliticaStrategy;
import citylogic.core.strategy.PoliticaNeutrale;
import citylogic.core.strategy.PoliticaAmbientale;
import citylogic.core.strategy.PoliticaIndustriale;
import citylogic.core.events.RandomEvent;
import citylogic.core.events.PrimaveraEvent;
import citylogic.core.events.CrisiEconomicaEvent;
import citylogic.core.events.GuerraEvent;
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

        // 1. Lettura dei contributi dalla griglia usando il polimorfismo
        for (UrbanEntity entity : griglia.getActiveEntities()) {
            if (entity.isFunctioning()) {
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

        // 8. Notifica gli Observer (UI)
        notifyObservers();
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
            // Possibilità del 15% di triggerare un nuovo evento
            if (random.nextDouble() < 0.15) {
                int tipoEvento = random.nextInt(3);
                switch (tipoEvento) {
                    case 0: activeEvent = new PrimaveraEvent(); break;
                    case 1: activeEvent = new CrisiEconomicaEvent(); break;
                    case 2: activeEvent = new GuerraEvent(); break;
                }
            }
        }
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
}