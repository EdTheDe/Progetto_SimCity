package citylogic.domain.state;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StatoCittaTest {

    @Test
    void testInitialParameters() {
        // AC 23.1: Integrità dei parametri
        StatoCitta stato = new StatoCitta();
        
        assertEquals(0, stato.getPopolazione(), "Popolazione iniziale deve essere 0");
        assertEquals(1000.0, stato.getFinanze(), "Finanze inziali default");
        assertEquals(50.0, stato.getFelicita(), "Felicità iniziale media");
        assertEquals(100.0, stato.getEcologia(), "Ecologia iniziale max");
        assertEquals(0.0, stato.getLavoro(), "Lavoro iniziale 0");
        assertEquals(0.0, stato.getSicurezza(), "Sicurezza iniziale 0");
        assertEquals(0.0, stato.getSanita(), "Sanita iniziale 0");
    }

    @Test
    void testClampingLimits() {
        // Clamp test: Verifica che i valori non superino i range stabiliti (0-100)
        StatoCitta stato = new StatoCitta();
        
        stato.setFelicita(150.0);
        assertEquals(100.0, stato.getFelicita(), "Felicita shouldn't exceed 100");
        
        stato.setEcologia(-20.0);
        assertEquals(0.0, stato.getEcologia(), "Ecologia shouldn't be below 0");
        
        stato.setPopolazione(-100);
        assertEquals(0, stato.getPopolazione(), "Popolazione non può essere negativa");
    }
}
