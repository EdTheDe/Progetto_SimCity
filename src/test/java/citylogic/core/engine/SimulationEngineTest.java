package citylogic.core.engine;

import citylogic.domain.entities.*;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import citylogic.core.events.PrimaveraEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationEngineTest {

    @Test
    void testTickProcessConInfrastruttura() {
        // AC 10.1 & 10.2 - Usiamo una infrastruttura per testare il tick base, 
        // poiché le infrastrutture ignorano i vincoli di checkCoverage.
        StatoCitta stato = new StatoCitta(); // Finanze: 4500.0
        UrbanGrid grid = new UrbanGrid(10, 10);
        
        PowerPlant power = new PowerPlant(500.0, 30.0, 100); // Mantenimento: 30.0
        grid.placeEntity(power, 0, 0);
        grid.placeEntity(new Road(10.0), 0, 1); // La centrale necessita di una strada
        
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        engine.tick();
        
        // Verifica che il tick abbia elaborato la centrale deducendo il mantenimento (4500 - 30 = 4470)
        assertEquals(4470.0, stato.getFinanze(), "Il tick deve dedurre il mantenimento dell'infrastruttura");
        assertEquals(100.0, stato.getEnergiaFornita(), "La centrale deve aver registrato la sua energia nello StatoCitta");
    }

    @Test
    void testObserverPattern() {
        // AC 20.1: Verifica che la UI venga avvisata a fine tick
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid(10, 10);
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        final boolean[] wasNotified = {false};
        CityObserver observer = new CityObserver() {
            @Override
            public void onSimulationUpdated(StatoCitta state) {
                wasNotified[0] = true;
            }
        };
        
        engine.addObserver(observer);
        engine.tick();
        
        assertTrue(wasNotified[0], "L'Observer doveva essere notificato al termine del tick");
    }

    @Test
    void testRandomEventsPersistence() {
        // AC 50.2: Isolamento del test sugli eventi
        StatoCitta stato = new StatoCitta();
        
        PrimaveraEvent event = new PrimaveraEvent(); // dura 4 tick, aumenta felicita di 20
        double felicitaIniziale = stato.getFelicita(); // 50.0
        
        // Applichiamo manualmente l'evento
        event.applyModifiers(stato, null);
        event.decrementTick();
        
        assertEquals(70.0, stato.getFelicita(), "La Primavera deve aver aumentato la felicità a 70");
        assertEquals(3, event.getRemainingTicks(), "L'evento deve scalare di durata dopo il decrement");
        assertFalse(event.isExpired(), "L'evento non deve essere espirato al primo tick");
    }

    @Test
    void testDemographicGrowthWithFullCoverage() {
        // AC 51.2 - STRESS TEST REALE: Creiamo una mini-città per superare i severi vincoli di checkCoverage
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid(10, 10);
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        // 1. Piazziamo TUTTI i servizi base necessari per far vivere i cittadini
        grid.placeEntity(new PowerPlant(500, 10, 100), 0, 0);
        grid.placeEntity(new WaterPlant(500, 10, 100), 1, 0);
        grid.placeEntity(new PoliceStation(500, 10, 50), 2, 0);
        grid.placeEntity(new FireStation(500, 10, 50), 3, 0);
        grid.placeEntity(new Hospital(500, 10, 50), 4, 0);
        
        // Piazziamo le strade adiacenti a tutti i servizi
        for(int i = 0; i <= 4; i++) {
            grid.placeEntity(new Road(10.0), i, 1);
        }
        
        // Facciamo un Tick a vuoto affinché le centrali eroghino acqua ed energia nel sistema
        engine.tick();
        
        // 2. Ora piazziamo la casa (che finalmente risulterà coperta e funzionante, vicina alla strada in 0,1)
        Residential residenziale = new Residential(100.0, 10.0, 10.0, 100);
        grid.placeEntity(residenziale, 0, 2);
        
        assertEquals(0, stato.getPopolazione(), "Prima del tick la popolazione deve essere zero");
        
        // 3. Facciamo il Tick vitale
        engine.tick();
        
        assertTrue(stato.getPopolazione() > 0 && stato.getPopolazione() <= 100, 
            "La popolazione deve crescere perché la casa è coperta da tutti i servizi necessari");
    }

    @Test
    void testGameOverTrigger() {
        // STRESS TEST: Verifichiamo la condizione di sconfitta (Bancarotta prolungata)
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid(10, 10);
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        // Portiamo le finanze in negativo estremo
        stato.setFinanze(-1500.0);
        
        final boolean[] gameOverTriggered = {false};
        engine.addObserver(new CityObserver() {
            @Override
            public void onSimulationUpdated(StatoCitta stato) {}
            
            @Override
            public void onGameOver() {
                gameOverTriggered[0] = true;
            }
        });
        
        // Simuliamo 6 tick consecutivi in bancarotta (il limite engine è > 5)
        for(int i = 0; i <= 6; i++) {
            engine.tick();
        }
        
        assertTrue(gameOverTriggered[0], "La UI (Observer) deve ricevere l'evento onGameOver() dopo 5 tick consecutivi con fondi negativi");
    }

    @Test
    void testGameOverReset() {
        // AC 52.1 - Verifica che il contatore bancarotta si resetti se si torna in positivo
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid(10, 10);
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        final boolean[] gameOverTriggered = {false};
        engine.addObserver(new CityObserver() {
            @Override
            public void onSimulationUpdated(StatoCitta stato) {}
            @Override
            public void onGameOver() { gameOverTriggered[0] = true; }
        });
        
        stato.setFinanze(-100.0);
        for(int i = 0; i < 4; i++) engine.tick(); // 4 ticks in rosso
        
        stato.setFinanze(500.0); // Salvataggio all'ultimo tick
        engine.tick(); // Tick in verde (resetta)
        
        stato.setFinanze(-100.0);
        for(int i = 0; i < 3; i++) engine.tick(); // 3 ticks in rosso
        
        assertFalse(gameOverTriggered[0], "Il Game Over non deve triggerare se la serie di 5 tick viene interrotta");
    }

    @Test
    void testDemographicExodus() {
        // AC 51.2 - Tettonica Demografica (Esodo)
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid(10, 10);
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        // Impostiamo una popolazione molto superiore alla capacità
        stato.setPopolazione(1000); 
        // Nessun edificio residenziale = 0 capacità
        
        engine.tick();
        
        assertEquals(0, stato.getPopolazione(), "La popolazione in eccesso deve abbandonare istantaneamente la città se non c'è capacità");
    }

    @Test
    void testMaintenanceCosts() {
        // AC 53.1 - Detrazione periodica dei costi di mantenimento
        StatoCitta stato = new StatoCitta();
        stato.setFinanze(1000.0);
        UrbanGrid grid = new UrbanGrid(10, 10);
        
        grid.placeEntity(new PoliceStation(100, 15, 50), 0, 0); // Costo: 15
        grid.placeEntity(new Hospital(100, 20, 50), 1, 0); // Costo: 20
        // Mettiamo le strade
        grid.placeEntity(new Road(5.0), 0, 1);
        grid.placeEntity(new Road(5.0), 1, 1);
        
        SimulationEngine engine = new SimulationEngine(stato, grid);
        engine.tick(); // Road ha manutenzione? Road ha solo costo di piazzamento nel costruttore standard, assumiamo 0 o quello che è.
        // Se Police e Hospital costano 15+20=35
        // Il bilancio dovrebbe scendere. Verifichiamo che sia sceso di almeno 35 (potrebbe esserci la strada)
        assertTrue(stato.getFinanze() <= 1000.0 - 35.0, "I costi di mantenimento devono essere detratti");
    }

    @Test
    void testInactivityReason() {
        // AC 31.1 - Rilevamento errore di inattività
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid(10, 10);
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        Residential res = new Residential(100, 10, 10, 50);
        grid.placeEntity(res, 5, 5);
        
        String motivo = engine.getMotivoInattivita(res);
        assertTrue(motivo.contains("Nessun collegamento stradale"), "Deve segnalare mancanza di strada: " + motivo);
        
        grid.placeEntity(new Road(5), 5, 6); // Aggiunta strada
        motivo = engine.getMotivoInattivita(res);
        assertTrue(motivo.contains("Manca Polizia") && motivo.contains("Manca Ospedale"), "Deve segnalare mancanza di servizi: " + motivo);
    }
    
    @Test
    void testCoverageRadiusLimit() {
        // AC 24.1 - Verifica raggio di copertura stretto
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid(20, 20);
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        // Livello 1 -> Raggio 5 + (1*2) = 7. Mettiamo la polizia a distanza 10
        grid.placeEntity(new PoliceStation(100, 10, 50), 0, 0);
        grid.placeEntity(new FireStation(100, 10, 50), 0, 1);
        grid.placeEntity(new Hospital(100, 10, 50), 0, 2);
        
        // Piazzo la casa a distanza X=10, fuori raggio
        Residential res = new Residential(100, 10, 10, 50);
        grid.placeEntity(res, 10, 0);
        grid.placeEntity(new Road(5), 10, 1); // Strada vicina alla casa
        
        assertFalse(engine.checkCoverage(res), "La casa deve risultare NON coperta perché i servizi sono fuori raggio (>7)");
    }
}