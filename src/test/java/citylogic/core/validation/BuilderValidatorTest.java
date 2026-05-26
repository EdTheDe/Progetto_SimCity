package citylogic.core.validation;

import citylogic.domain.entities.Residential;
import citylogic.domain.map.Cell;
import citylogic.domain.state.StatoCitta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuilderValidatorTest {

    @Test
    void testValidaCostruzione_CellaOccupata() {
        // AC 9.1: Prevenzione sovrascrittura
        BuilderValidator validator = new BuilderValidator();
        StatoCitta stato = new StatoCitta(); // Finanze default: 1000.0
        
        Cell cellaOccupata = new Cell(0, 0);
        cellaOccupata.setEntity(new Residential(100.0, 10.0, 10.0, 50)); // Cella occupata
        
        Residential nuovaEntity = new Residential(100.0, 10.0, 10.0, 50);
        
        CostruzioneException thrown = assertThrows(
            CostruzioneException.class,
            () -> validator.validaCostruzione(nuovaEntity, cellaOccupata, stato),
            "Expected CostruzioneException to throw, but it didn't"
        );

        assertTrue(thrown.getMessage().contains("già occupata"));
    }

    @Test
    void testValidaCostruzione_FondiInsufficienti() {
        // AC 9.2: Controllo capienza budget
        BuilderValidator validator = new BuilderValidator();
        StatoCitta stato = new StatoCitta();
        
        // Svuotiamo il budget
        stato.addFinanze(-1000.0); // Finanze = 0
        
        Cell cellaVuota = new Cell(0, 0);
        Residential entityDaCostruire = new Residential(2000.0, 10.0, 10.0, 50); // Costo > Finanze
        
        CostruzioneException thrown = assertThrows(
            CostruzioneException.class,
            () -> validator.validaCostruzione(entityDaCostruire, cellaVuota, stato),
            "Expected CostruzioneException to throw due to lack of funds"
        );

        assertTrue(thrown.getMessage().contains("Fondi insufficienti"));
        assertEquals(0.0, stato.getFinanze(), "Budget cittadino deve rimanere invariato");
    }

    @Test
    void testValidaCostruzione_Successo() {
        BuilderValidator validator = new BuilderValidator();
        StatoCitta stato = new StatoCitta(); // Finanze default: 1000.0
        Cell cellaVuota = new Cell(0, 0);
        Residential nuovaEntity = new Residential(100.0, 10.0, 10.0, 50);
        
        assertDoesNotThrow(() -> validator.validaCostruzione(nuovaEntity, cellaVuota, stato));
    }
}
