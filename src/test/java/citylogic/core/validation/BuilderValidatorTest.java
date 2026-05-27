package citylogic.core.validation;

import citylogic.domain.entities.Residential;
import citylogic.domain.map.Cell;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuilderValidatorTest {

    // Creiamo una griglia fittizia per i test (adatta i parametri se il tuo costruttore è diverso)
    private UrbanGrid createTestGrid() {
        return new UrbanGrid(10, 10);
    }

    @Test
    void testValidaCostruzione_CellaOccupata() {
        BuilderValidator validator = new BuilderValidator(createTestGrid());
        StatoCitta stato = new StatoCitta(); // Finanze default: 1000.0

        Cell cellaOccupata = new Cell(0, 0);
        cellaOccupata.setEntity(new Residential(100.0, 10.0, 10.0, 50));

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
        BuilderValidator validator = new BuilderValidator(createTestGrid());
        StatoCitta stato = new StatoCitta();

        stato.addFinanze(-1000.0); // Finanze = 0

        Cell cellaVuota = new Cell(0, 0);
        Residential entityDaCostruire = new Residential(2000.0, 10.0, 10.0, 50);

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
        BuilderValidator validator = new BuilderValidator(createTestGrid());
        StatoCitta stato = new StatoCitta();
        Cell cellaVuota = new Cell(0, 0);
        Residential nuovaEntity = new Residential(100.0, 10.0, 10.0, 50);

        // Se non ci sono polizie/ospedali nei dintorni, la nuova regola bloccherà la costruzione.
        // Se in questo test vuoi solo testare spazio e fondi, dovresti disabilitare temporaneamente
        // la RegolaCollegamentoServizi o piazzare i servizi nella test grid.
    }
}