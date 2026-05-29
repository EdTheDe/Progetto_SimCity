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

    // --- TEST PER I COSTI DI MANTENIMENTO ---

    @Test
    void testInfrastructureMaintenanceDeduction() {
        Hospital hospital = new Hospital(400.0, 40.0, 60);
        StatoCitta stato = new StatoCitta();
        TickStats stats = new TickStats();

        hospital.processTick(stato, stats);

        assertEquals(4460.0, stato.getFinanze(), "L'infrastruttura deve dedurre il costo di mantenimento base");
    }

    @Test
    void testInfrastructureMaintenanceScaling() {
        Hospital hospital = new Hospital(400.0, 40.0, 60);
        StatoCitta stato = new StatoCitta();
        TickStats stats = new TickStats();

        hospital.upgradeLevel(); 
        hospital.processTick(stato, stats);

        assertEquals(4420.0, stato.getFinanze(), "L'infrastruttura al livello 2 deve dedurre il doppio del mantenimento");
    }

    @Test
    void testUpgradeLevelCap() {
        citylogic.domain.entities.Commercial com = new citylogic.domain.entities.Commercial(100.0, 10.0, 10.0, 100.0);
        
        // Tentiamo di fare upgrade 20 volte di fila (simulando un giocatore che preme il tasto o un bug)
        for (int i = 0; i < 20; i++) {
            com.upgradeLevel();
        }
        
        // Verifichiamo che il livello si sia bloccato a un limite massimo sensato (es. Livello 5)
        assertEquals(5, com.getDevelopmentLevel(), 
            "Il livello di sviluppo deve bloccarsi a un tetto massimo (es. 5) per evitare che i moltiplicatori esplodano");
    }
}
