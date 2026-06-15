package citylogic.domain.state;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class StatoCittaTest {

    @Test
    void testInitialParameters() {
        // AC 23.1: Integrità dei parametri
        StatoCitta stato = new StatoCitta();
        
        assertEquals(0, stato.getPopolazione(), "Popolazione iniziale deve essere 0");
        // FIX: Corretto il valore da 1000.0 a 4500.0 per combaciare con il dominio
        assertEquals(4500.0, stato.getFinanze(), "Finanze iniziali default"); 
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
        
        stato.setLavoro(200.0);
        assertEquals(100.0, stato.getLavoro(), "Lavoro shouldn't exceed 100");
        
        stato.setSicurezza(-10.0);
        assertEquals(0.0, stato.getSicurezza(), "Sicurezza shouldn't be below 0");
        
        stato.setSanita(50.0);
        assertEquals(50.0, stato.getSanita(), "Sanita should be set correctly within range");
    }

    // --- STRESS TEST AGGIUNTI ---

    @Test
    void testResetState() {
        StatoCitta stato = new StatoCitta();
        
        // Corrompiamo lo stato iniziale
        stato.setPopolazione(5000);
        stato.setFinanze(0.0);
        stato.setFelicita(10.0);
        stato.setAcquaFornita(500.0);
        
        // P3 carica una nuova partita o il giocatore fa "Nuova Partita"
        stato.reset();
        
        // Verifichiamo che TUTTO sia tornato alle origini
        assertEquals(0, stato.getPopolazione(), "Il reset deve azzerare la popolazione");
        assertEquals(4500.0, stato.getFinanze(), "Il reset deve riportare le finanze a 4500.0");
        assertEquals(50.0, stato.getFelicita(), "Il reset deve riportare la felicità a 50.0");
        assertEquals(0.0, stato.getAcquaFornita(), "Il reset deve azzerare le metriche di servizio");
    }

    @Test
    void testAddFinanze() {
        StatoCitta stato = new StatoCitta(); // Finanze iniziali: 4500.0
        
        // Simulazione di entrate (es. tasse commerciali)
        stato.addFinanze(500.0);
        assertEquals(5000.0, stato.getFinanze(), "addFinanze deve incrementare correttamente il budget");
        
        // Simulazione di uscite (es. mantenimento edifici o eventi catastrofici)
        stato.addFinanze(-1000.0);
        assertEquals(4000.0, stato.getFinanze(), "addFinanze con valori negativi deve dedurre correttamente il budget");
    }
}