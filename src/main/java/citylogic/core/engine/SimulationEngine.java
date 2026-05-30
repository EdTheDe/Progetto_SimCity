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

/**
 * Motore principale della simulazione.
 * Gestisce l'aggiornamento dello stato in base agli edifici e alle politiche.
 */
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
        // La città parte sempre senza malus o bonus aggiuntivi
        this.politicaAttiva = new PoliticaNeutrale();
        this.observers = new ArrayList<>();
        this.random = new Random();
        this.activeEvent = null;
    }

    // Registra un componente in ascolto (es. UI)
    public void addObserver(CityObserver observer) {
        if (!observers.contains(observer)) {
            observers.add(observer);
        }
    }

    // Rimuove un componente dalla lista degli aggiornamenti
    public void removeObserver(CityObserver observer) {
        observers.remove(observer);
    }

    // Forza l'aggiornamento immediato di tutti gli observer
    public void forceNotifyObservers() {
        for (CityObserver observer : observers) {
            observer.onSimulationUpdated(stato);
        }
    }

    // Cambia politica. Il controllo != null evita crash al tick successivo
    public void setPoliticaAttiva(PoliticaStrategy nuovaPolitica) {
        if (nuovaPolitica != null) {
            this.politicaAttiva = nuovaPolitica;
        }
    }

    public PoliticaStrategy getPoliticaAttiva() {
        return this.politicaAttiva;
    }

    public List<UrbanEntity> getActiveEntities() {
        return griglia.getActiveEntities();
    }

    /**
     * Aggiorna la simulazione di un tick.
     * Calcola entrate, popolazioni, modificatori ed eventi.
     */
    public void tick() {
        TickStats stats = new TickStats();

        // 1. Lettura dei contributi dalla griglia usando il polimorfismo e controllo copertura
        for (UrbanEntity entity : griglia.getActiveEntities()) {
            
            // Calcolo statistiche fisse dell'entità per UI (es. Acqua richiesta)
            if (entity instanceof citylogic.domain.entities.Building) {
                stats.addAcquaRichiesta(((citylogic.domain.entities.Building) entity).getWaterDemand());
                stats.addEnergiaRichiesta(((citylogic.domain.entities.Building) entity).getEnergyDemand());
            }
            if (entity instanceof citylogic.domain.entities.WaterPlant) {
                stats.addAcquaFornita(((citylogic.domain.entities.WaterPlant) entity).getWaterOutput());
            }
            if (entity instanceof citylogic.domain.entities.PowerPlant) {
                stats.addEnergiaFornita(((citylogic.domain.entities.PowerPlant) entity).getEnergyOutput());
            }

            if (entity.isFunctioning() && checkCoverage(entity)) {
                entity.processTick(stato, stats);
                if (entity instanceof citylogic.domain.entities.Industrial) {
                    stats.addIndustriaAttiva();
                }
            }
        }

        double modFelicita = calcolaModificatoreFelicita();

        // 2. Gestione Dinamica Popolazione
        // Se c'è spazio, la popolazione cresce in base alla felicità corrente
        if (stato.getPopolazione() < stats.getCapacitaAbitativa()) {
            int crescita = (int) Math.ceil((stats.getCapacitaAbitativa() - stato.getPopolazione()) * 0.2 * modFelicita);
            stato.setPopolazione(stato.getPopolazione() + Math.max(1, crescita));
        } else if (stato.getPopolazione() > stats.getCapacitaAbitativa()) {
            stato.setPopolazione(stats.getCapacitaAbitativa());
        }

        int popAttiva = Math.max(1, stato.getPopolazione()); // Evita divisioni per 0

        // 3. Ricalcolo Metriche di Base
        // Pesa i punti forniti dagli edifici sulla popolazione reale
        stato.setLavoro(((double) stats.getPostiLavoro() / popAttiva) * 100.0);
        stato.setSicurezza((((double) stats.getPuntiSicurezza() * modFelicita) / popAttiva) * 100.0);
        stato.setSanita((((double) stats.getPuntiSanita() * modFelicita) / popAttiva) * 100.0);

        stato.setEcologia(100.0 - (((double) stats.getPuntiInquinamento() / popAttiva) * 30.0));

        // 4. Entrate Commerciali
        stato.addFinanze(stats.getRedditoCommerciale() * modFelicita);

        // 5. Applicazione Malus e Dinamiche Globali
        stato.setAcquaFornita(stats.getAcquaFornita());
        stato.setAcquaRichiesta(stats.getAcquaRichiesta());
        stato.setEnergiaFornita(stats.getEnergiaFornita());
        stato.setEnergiaRichiesta(stats.getEnergiaRichiesta());
        applicaDinamicheGlobali();

        // 6. Applicazione Strategy (Politiche Cittadine)
        politicaAttiva.applicaModificatori(stato, stats);

        // 7. Gestione Eventi Randomici
        gestisciEventi(stats);

        // 8. Controllo Game Over (Finanze negative)
        if (stato.getFinanze() < 0) {
            ticksInNegativeFunds++;
        } else {
            ticksInNegativeFunds = 0;
        }

        // 9. Notifica gli Observer (UI)
        forceNotifyObservers();

        if (ticksInNegativeFunds > 5) {
            for (CityObserver obs : observers) {
                obs.onGameOver();
            }
        }
        
        stato.addTicket();
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
                int tipoEvento = random.nextInt(4);
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
                        eventDesc = "La nazione è in guerra! Disoccupazione, tristezza e instabilità globale.";
                        break;
                    case 3:
                        activeEvent = new citylogic.core.events.PioggiaDiMeteoritiEvent(stato, griglia);
                        eventName = "Pioggia di Meteoriti";
                        eventDesc = "Una letale pioggia di meteoriti si è abbattuta sulla città! Edifici distrutti e panico diffuso.";
                        break;
                }
                for (CityObserver obs : observers) {
                    obs.onEventStarted(eventName, eventDesc);
                }
            }
        }
    }

    public boolean checkCoverage(UrbanEntity entity) {
        if (entity instanceof Road) return true;

        int x = entity.getX();
        int y = entity.getY();
        if (x < 0 || y < 0) return false;

        if (!griglia.hasAdjacentRoad(x, y)) {
            return false;
        }

        // Le infrastrutture pubbliche e di servizio non hanno bisogno di polizia/pompieri/acqua per generare risorse, 
        // a patto che abbiano la strada.
        if (entity instanceof PoliceStation || entity instanceof FireStation || 
            entity instanceof Hospital || entity instanceof PowerPlant || 
            entity instanceof WaterPlant || entity instanceof School || entity instanceof GreenArea) {
            return true;
        }

        boolean polizia = false, pompieri = false, ospedale = false;
        boolean acqua = stato.getAcquaFornita() >= stato.getAcquaRichiesta();
        boolean luce = stato.getEnergiaFornita() >= stato.getEnergiaRichiesta();
        
        for (UrbanEntity e : griglia.getActiveEntities()) {
            if (polizia && pompieri && ospedale) break;
            
            int distMax = Math.max(Math.abs(e.getX() - x), Math.abs(e.getY() - y));
            int raggio = 5 + (e.getDevelopmentLevel() * 2);

            if (e instanceof PoliceStation && distMax <= raggio) polizia = true;
            if (e instanceof FireStation && distMax <= raggio) pompieri = true;
            if (e instanceof Hospital && distMax <= raggio) ospedale = true;
        }
        
        return polizia && pompieri && ospedale && acqua && luce;
    }

    public String getMotivoInattivita(UrbanEntity entity) {
        if (!entity.isFunctioning()) return "Fondi insufficienti";
        if (checkCoverage(entity)) return "Attivo";
        
        int x = entity.getX();
        int y = entity.getY();
        if (x < 0 || y < 0) return "Fuori mappa";

        if (!(entity instanceof Road) && !griglia.hasAdjacentRoad(x, y)) {
            return "Nessun collegamento stradale";
        }

        if (entity instanceof PoliceStation || entity instanceof FireStation || 
            entity instanceof Hospital || entity instanceof PowerPlant || 
            entity instanceof WaterPlant || entity instanceof School || entity instanceof GreenArea) {
            return "Errore sconosciuto";
        }

        boolean polizia = false, pompieri = false, ospedale = false;
        boolean acqua = stato.getAcquaFornita() >= stato.getAcquaRichiesta();
        boolean luce = stato.getEnergiaFornita() >= stato.getEnergiaRichiesta();
        
        for (UrbanEntity e : griglia.getActiveEntities()) {
            if (polizia && pompieri && ospedale) break;
            
            int distMax = Math.max(Math.abs(e.getX() - x), Math.abs(e.getY() - y));
            int raggio = 5 + (e.getDevelopmentLevel() * 2);

            if (e instanceof PoliceStation && distMax <= raggio) polizia = true;
            if (e instanceof FireStation && distMax <= raggio) pompieri = true;
            if (e instanceof Hospital && distMax <= raggio) ospedale = true;
        }

        StringBuilder motivi = new StringBuilder();
        if (!polizia) motivi.append("Manca Polizia. ");
        if (!pompieri) motivi.append("Manca Pompieri. ");
        if (!ospedale) motivi.append("Manca Ospedale. ");
        if (!acqua) motivi.append("Manca Acqua. ");
        if (!luce) motivi.append("Manca Elettricità. ");
        
        return motivi.toString().trim();
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
