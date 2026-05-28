package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PoliticaStrategyTest {

    @Test
    void testPoliticaAmbientaleConIndustrie() {
        // AC 16.1
        PoliticaStrategy policy = new PoliticaAmbientale();
        StatoCitta stato = new StatoCitta();
        TickStats stats = new TickStats();
        
        // Setup iniziale
        stato.setEcologia(50.0);
        stato.setLavoro(50.0);
        stato.setFinanze(1000.0); 
        
        // Simuliamo 3 industrie attive nel Tick corrente
        stats.addIndustriaAttiva();
        stats.addIndustriaAttiva();
        stats.addIndustriaAttiva(); 
        
        policy.applicaModificatori(stato, stats);
        
        // Verifica matematica esatta
        // Ecologia: 50.0 + 10.0 (base) + (5.0 * 3) = 75.0
        assertEquals(75.0, stato.getEcologia(), "L'ecologia deve aumentare calcolando il bonus base + bonus per industria");
        // Lavoro: 50.0 - 8.0 = 42.0
        assertEquals(42.0, stato.getLavoro(), "Il lavoro deve diminuire di 8.0 come malus fisso");
        // Finanze: 1000.0 - (20.0 * 3) = 940.0
        assertEquals(940.0, stato.getFinanze(), "Le finanze devono scendere di 20 per ogni industria (costo transizione ecologica)");
    }

    @Test
    void testPoliticaIndustrialeConIndustrie() {
        // AC 17.1
        PoliticaStrategy policy = new PoliticaIndustriale();
        StatoCitta stato = new StatoCitta();
        TickStats stats = new TickStats();
        
        stato.setEcologia(100.0);
        stato.setLavoro(50.0);
        stato.setFinanze(1000.0);
        
        // Simuliamo 4 industrie attive
        for(int i=0; i<4; i++) {
            stats.addIndustriaAttiva();
        }
        
        policy.applicaModificatori(stato, stats);
        
        // Verifica matematica esatta
        // Ecologia: 100.0 - (5.0 * 4) = 80.0
        assertEquals(80.0, stato.getEcologia(), "L'ecologia deve diminuire di 5 per ogni industria attiva");
        // Lavoro: 50.0 + (5.0 * 4) = 70.0
        assertEquals(70.0, stato.getLavoro(), "Il lavoro deve aumentare di 5 per ogni industria attiva");
        // Finanze: 1000.0 + (50.0 * 4) = 1200.0
        assertEquals(1200.0, stato.getFinanze(), "Le finanze devono salire di 50 per ogni industria (introiti detassazione)");
    }

    @Test
    void testPoliticheSenzaIndustrie() {
        // STRESS TEST: Cosa succede se il sindaco attiva le politiche ma non ha ancora costruito fabbriche?
        StatoCitta stato = new StatoCitta();
        TickStats stats = new TickStats(); // Zero industrie di default
        
        stato.setEcologia(50.0);
        stato.setLavoro(50.0);
        stato.setFinanze(1000.0);
        
        PoliticaIndustriale policyInd = new PoliticaIndustriale();
        policyInd.applicaModificatori(stato, stats);
        
        // La politica industriale scala SOLO in base alle industrie. Se sono 0, non deve fare nulla.
        assertEquals(50.0, stato.getEcologia(), "Senza industrie, la politica industriale non deve intaccare l'ecologia");
        assertEquals(1000.0, stato.getFinanze(), "Senza industrie, non ci sono bonus economici");

        PoliticaAmbientale policyAmb = new PoliticaAmbientale();
        policyAmb.applicaModificatori(stato, stats);
        
        // La politica ambientale ha invece un effetto base (+10 ecologia, -8 lavoro) a prescindere dalle fabbriche
        assertEquals(60.0, stato.getEcologia(), "La politica ambientale deve applicare almeno il suo bonus base (+10)");
        assertEquals(42.0, stato.getLavoro(), "La politica ambientale toglie 8 di lavoro come malus base");
        assertEquals(1000.0, stato.getFinanze(), "Nessuna industria da convertire = nessun costo in finanze sottratto");
    }
    
    @Test
    void testPoliticaNeutrale() {
        // STRESS TEST: Verifica che il Neutral Strategy (Pattern Null Object) non alteri nulla
        PoliticaStrategy policy = new PoliticaNeutrale();
        StatoCitta stato = new StatoCitta();
        TickStats stats = new TickStats();
        
        double ecologiaIniziale = stato.getEcologia();
        double finanzeIniziali = stato.getFinanze();
        
        policy.applicaModificatori(stato, stats);
        
        assertEquals(ecologiaIniziale, stato.getEcologia(), "La politica neutrale non deve alterare l'ecologia");
        assertEquals(finanzeIniziali, stato.getFinanze(), "La politica neutrale non deve alterare le finanze");
    }
}