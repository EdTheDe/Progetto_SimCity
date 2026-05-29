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
        StatoCitta stato = new StatoCitta(); // Finanze: 3000.0
        UrbanGrid grid = new UrbanGrid(10, 10);
        
        PowerPlant power = new PowerPlant(500.0, 30.0, 100); // Mantenimento: 30.0
        grid.placeEntity(power, 0, 0);
        
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        engine.tick();
        
        // Verifica che il tick abbia elaborato la centrale deducendo il mantenimento (3000 - 30 = 2970)
        assertEquals(2970.0, stato.getFinanze(), "Il tick deve dedurre il mantenimento dell'infrastruttura");
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
        // AC 27.2: Isolamento del test sugli eventi
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
        // AC 28.2 - STRESS TEST REALE: Creiamo una mini-città per superare i severi vincoli di checkCoverage
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid(10, 10);
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        // 1. Piazziamo TUTTI i servizi base necessari per far vivere i cittadini
        grid.placeEntity(new PowerPlant(500, 10, 100), 0, 0);
        grid.placeEntity(new WaterPlant(500, 10, 100), 1, 0);
        grid.placeEntity(new PoliceStation(500, 10, 50), 2, 0);
        grid.placeEntity(new FireStation(500, 10, 50), 3, 0);
        grid.placeEntity(new Hospital(500, 10, 50), 4, 0);
        
        // Facciamo un Tick a vuoto affinché le centrali eroghino acqua ed energia nel sistema
        engine.tick();
        
        // 2. Ora piazziamo la casa (che finalmente risulterà coperta e funzionante!)
        Residential residenziale = new Residential(100.0, 10.0, 10.0, 100);
        grid.placeEntity(residenziale, 0, 1);
        
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
}