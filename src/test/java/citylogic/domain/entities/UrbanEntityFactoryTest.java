package citylogic.domain.entities;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class UrbanEntityFactoryTest {

    @Test
    void testFactoryCreationSuccess() {
        UrbanEntity res = UrbanEntityFactory.createEntity("residential");
        assertTrue(res instanceof Residential, "La factory deve ritornare un'istanza di Residential");
        
        // Verifichiamo anche l'aggiunta fatta dal tuo collega per il JSON
        UrbanEntity police = UrbanEntityFactory.createEntity("policestation");
        assertTrue(police instanceof PoliceStation, "La factory deve gestire la stringa JSON 'policestation'");
    }

    @Test
    void testFactoryUnknownTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> UrbanEntityFactory.createEntity("invalid_building"), 
            "La factory deve lanciare eccezione per stringhe sconosciute");
    }

    @Test
    void testFactoryNullTypeThrowsException() {
        assertThrows(IllegalArgumentException.class, () -> UrbanEntityFactory.createEntity(null), 
            "La factory deve lanciare eccezione per input nulli");
    }
}