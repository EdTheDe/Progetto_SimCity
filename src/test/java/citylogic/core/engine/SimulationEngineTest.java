package citylogic.core.engine;

import citylogic.domain.entities.Residential;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import citylogic.core.events.PrimaveraEvent;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimulationEngineTest {

    @Test
    void testTickProcessAndReset() {
        // AC 10.1 & 10.2
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid();
        
        // Piazziamo un paio di edifici per verificare che passi su "active entities"
        Residential r1 = new Residential(100.0, 10.0, 10.0, 50);
        grid.placeEntity(r1, 0, 0);
        
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        engine.tick();
        
        // Verifica che la popolazione sia cresciuta in base alla capacità dell'edificio
        assertTrue(stato.getPopolazione() > 0, "Popolazione dovrebbe crescere col tick in presenza di residenziali");
    }

    @Test
    void testObserverPattern() {
        // AC 20.1
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid();
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        // Creiamo un observer dummy
        final boolean[] wasNotified = {false};
        CityObserver observer = new CityObserver() {
            @Override
            public void onSimulationUpdated(StatoCitta state) {
                wasNotified[0] = true;
            }
        };
        
        engine.addObserver(observer);
        engine.tick();
        
        assertTrue(wasNotified[0], "Observer doveva essere notificato al termine del tick");
    }

    @Test
    void testRandomEventsPersistence() {
        // AC 27.2
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid();
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        PrimaveraEvent event = new PrimaveraEvent(); // dura 4 tick, aumenta felicita di 20
        double felicitaIniziale = stato.getFelicita();
        
        // Applichiamo per un tick (simulando l'evento triggerato)
        event.applyModifiers(stato, null);
        event.decrementTick();
        
        assertTrue(stato.getFelicita() > felicitaIniziale, "Primavera deve aumentare felicita");
        assertEquals(3, event.getRemainingTicks(), "L'evento deve scalare di durata");
        assertFalse(event.isExpired(), "L'evento non deve essere espirato");
    }

    @Test
    void testDemographicGrowth() {
        // AC 28.2
        StatoCitta stato = new StatoCitta();
        UrbanGrid grid = new UrbanGrid();
        
        Residential r = new Residential(100.0, 10.0, 10.0, 100);
        grid.placeEntity(r, 0, 0);
        
        SimulationEngine engine = new SimulationEngine(stato, grid);
        
        assertEquals(0, stato.getPopolazione());
        
        engine.tick();
        
        assertTrue(stato.getPopolazione() > 0 && stato.getPopolazione() <= 100, "Popolazione deve aumentare verso la capacity attrattiva");
    }
}
