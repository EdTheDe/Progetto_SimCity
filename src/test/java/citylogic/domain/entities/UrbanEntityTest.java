package citylogic.domain.entities;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UrbanEntityTest {

    @Test
    void testResidentialCommercialPlacementCost() {
        // AC 6.1: Deduzione costo (simulata dalla validazione in altre parti, ma qui testiamo l'attributo)
        Residential res = new Residential(200.0, 10.0, 10.0, 50);
        Commercial com = new Commercial(300.0, 15.0, 15.0, 100.0);
        
        assertEquals(200.0, res.getPlacementCost());
        assertEquals(300.0, com.getPlacementCost());
    }

    @Test
    void testResidentialContribution() {
        // AC 6.2: Contributo statistico Residenziale
        Residential res = new Residential(100.0, 10.0, 10.0, 50);
        TickStats stats = new TickStats();
        StatoCitta stato = new StatoCitta();
        
        res.processTick(stato, stats);
        assertEquals(50, stats.getCapacitaAbitativa());
    }

    @Test
    void testCommercialContribution() {
        // AC 6.3: Contributo statistico Commerciale
        Commercial com = new Commercial(100.0, 10.0, 10.0, 250.0);
        TickStats stats = new TickStats();
        StatoCitta stato = new StatoCitta();
        
        com.processTick(stato, stats);
        assertEquals(250.0, stats.getRedditoCommerciale());
    }

    @Test
    void testIndustrialContribution() {
        // AC 7.1, 7.2: Impatto economico, occupazionale e inquinante
        Industrial ind = new Industrial(500.0, 50.0, 50.0, 30.0, 100);
        TickStats stats = new TickStats();
        StatoCitta stato = new StatoCitta();
        
        ind.processTick(stato, stats);
        assertEquals(100, stats.getPostiLavoro());
        assertEquals(30, stats.getPuntiInquinamento());
    }

    @Test
    void testLevelMultiplier() {
        // AC 21.1: Moltiplicatore di output
        Commercial com = new Commercial(100.0, 10.0, 10.0, 100.0);
        TickStats stats1 = new TickStats();
        StatoCitta stato = new StatoCitta();
        
        com.processTick(stato, stats1);
        assertEquals(100.0, stats1.getRedditoCommerciale());
        
        com.upgradeLevel(); // Level 2
        TickStats stats2 = new TickStats();
        com.processTick(stato, stats2);
        
        assertEquals(200.0, stats2.getRedditoCommerciale(), "Il reddito deve essere moltiplicato per il livello");
    }

    @Test
    void testGreenAreaMitigation() {
        // AC 29.1: Mitigazione inquinamento
        GreenArea park = new GreenArea(50.0, 5.0, -20); // Notare che passa l'inquinamento, la classe aggiunge ai punti.
        TickStats stats = new TickStats();
        StatoCitta stato = new StatoCitta();
        
        park.processTick(stato, stats);
        assertEquals(-20, stats.getPuntiInquinamento(), "La zona verde deve dare un contributo negativo (mitigazione) ai punti inquinamento");
    }
}
