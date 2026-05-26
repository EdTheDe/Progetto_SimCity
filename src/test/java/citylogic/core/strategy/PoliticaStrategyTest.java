package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PoliticaStrategyTest {

    @Test
    void testPoliticaAmbientale() {
        // AC 16.1
        PoliticaStrategy policy = new PoliticaAmbientale();
        StatoCitta stato = new StatoCitta();
        
        stato.setEcologia(50.0);
        stato.setLavoro(50.0);
        stato.addFinanze(-stato.getFinanze()); // azzeriamo finanze
        stato.addFinanze(1000.0);
        
        policy.applicaModificatori(stato);
        
        assertTrue(stato.getEcologia() > 50.0, "Ecologia deve aumentare");
        assertTrue(stato.getLavoro() < 50.0, "Lavoro deve diminuire");
        assertTrue(stato.getFinanze() < 1000.0, "Le finanze devono scendere (costo transizione)");
    }

    @Test
    void testPoliticaIndustriale() {
        // AC 17.1
        PoliticaStrategy policy = new PoliticaIndustriale();
        StatoCitta stato = new StatoCitta();
        
        stato.setEcologia(100.0);
        stato.setLavoro(50.0);
        stato.addFinanze(-stato.getFinanze());
        stato.addFinanze(1000.0);
        
        policy.applicaModificatori(stato);
        
        assertTrue(stato.getEcologia() < 100.0, "Ecologia deve avere un malus pesante");
        assertTrue(stato.getLavoro() > 50.0, "Lavoro deve aumentare");
        assertTrue(stato.getFinanze() > 1000.0, "Le finanze devono salire (introiti detassazione)");
    }
}
