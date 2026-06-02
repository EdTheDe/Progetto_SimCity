package citylogic.core.validation;

import citylogic.domain.entities.*;
import citylogic.domain.map.Cell;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class BuilderValidatorTest {

    private UrbanGrid createTestGrid() {
        return new UrbanGrid(10, 10);
    }

    @Test
    void testValidaCostruzione_CellaOccupata() {
        BuilderValidator validator = new BuilderValidator(createTestGrid());
        StatoCitta stato = new StatoCitta();

        Cell cellaOccupata = new Cell(0, 0);
        cellaOccupata.setEntity(new Residential(100.0, 10.0, 10.0, 50));

        Residential nuovaEntity = new Residential(100.0, 10.0, 10.0, 50);

        CostruzioneException thrown = assertThrows(
                CostruzioneException.class,
                () -> validator.validaCostruzione(nuovaEntity, cellaOccupata, stato),
                "Deve lanciare eccezione se la cella è occupata"
        );

        assertTrue(thrown.getMessage().contains("già occupata"));
    }

    @Test
    void testValidaCostruzione_FondiInsufficienti() {
        BuilderValidator validator = new BuilderValidator(createTestGrid());
        StatoCitta stato = new StatoCitta();
        stato.setFinanze(50.0); // Budget molto basso

        Cell cellaVuota = new Cell(0, 0);
        Residential entityDaCostruire = new Residential(2000.0, 10.0, 10.0, 50);

        assertThrows(CostruzioneException.class, () -> validator.validaCostruzione(entityDaCostruire, cellaVuota, stato),
                "Deve lanciare eccezione per fondi insufficienti");
    }

    @Test
    void testValidaCostruzione_SuccessoConServizi() {
        UrbanGrid grid = createTestGrid();
        BuilderValidator validator = new BuilderValidator(grid);
        StatoCitta stato = new StatoCitta();
        
        // STRESS TEST: Per far passare la RegolaCollegamentoServizi, dobbiamo piazzare i servizi necessari
        grid.placeEntity(new PoliceStation(500, 10, 50), 0, 1);
        grid.placeEntity(new FireStation(500, 10, 50), 1, 0);
        grid.placeEntity(new Hospital(500, 10, 50), 1, 1);

        Cell cellaVuota = grid.getCell(0, 0);
        Residential nuovaEntity = new Residential(100.0, 10.0, 10.0, 50);

        // Se la validazione passa, non deve lanciare eccezioni
        assertDoesNotThrow(() -> validator.validaCostruzione(nuovaEntity, cellaVuota, stato),
                "La costruzione deve avere successo se i servizi base sono presenti nelle vicinanze");
    }

    @Test
    void testValidaCostruzione_BordoMappa() {
        // STRESS TEST: Verifica che la scansione dei servizi ai bordi non causi crash (IndexOutOfBounds)
        UrbanGrid grid = createTestGrid();
        BuilderValidator validator = new BuilderValidator(grid);
        StatoCitta stato = new StatoCitta();
        
        // Piazziamo i servizi al bordo (0,0)
        grid.placeEntity(new PoliceStation(500, 10, 50), 0, 1);
        grid.placeEntity(new FireStation(500, 10, 50), 1, 0);
        grid.placeEntity(new Hospital(500, 10, 50), 1, 1);

        Cell bordo = grid.getCell(0, 0);
        Residential nuovaEntity = new Residential(100.0, 10.0, 10.0, 50);

        assertDoesNotThrow(() -> validator.validaCostruzione(nuovaEntity, bordo, stato),
                "Il validatore deve gestire correttamente le celle ai bordi (0,0) senza andare in crash");
    }
}