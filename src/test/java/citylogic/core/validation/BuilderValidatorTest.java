package citylogic.core.validation;

import citylogic.domain.entities.*;
import citylogic.domain.map.Cell;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Test per validare le regole imposte dal gioco quando si tenta di posizionare un edificio.
 */
public class BuilderValidatorTest {

    // Helper method per generare una griglia 10x10 su cui testare
    private UrbanGrid createTestGrid() {
        return new UrbanGrid(10, 10);
    }

    @Test
    void testValidaCostruzione_CellaOccupata() {
        BuilderValidator validator = new BuilderValidator(createTestGrid());
        StatoCitta stato = new StatoCitta();

        // Creiamo una cella e ci piazziamo subito un edificio residenziale
        Cell cellaOccupata = new Cell(0, 0);
        cellaOccupata.setEntity(new Residential(100.0, 10.0, 10.0, 50));

        // Prepariamo l'edificio che vorremmo costruire
        Residential nuovaEntity = new Residential(100.0, 10.0, 10.0, 50);

        // Verifichiamo che la regola RegolaSpazioLibero intercetti il problema e blocchi l'azione
        CostruzioneException thrown = assertThrows(
                CostruzioneException.class,
                () -> validator.validaCostruzione(nuovaEntity, cellaOccupata, stato),
                "Deve lanciare eccezione se la cella è occupata"
        );

        // Controllo aggiuntivo sul messaggio di errore per capire se è stata innescata la regola giusta
        assertTrue(thrown.getMessage().contains("già occupata"));
    }

    @Test
    void testValidaCostruzione_FondiInsufficienti() {
        BuilderValidator validator = new BuilderValidator(createTestGrid());
        StatoCitta stato = new StatoCitta();
        stato.setFinanze(50.0); // Diamo alla città solo 50 monete

        Cell cellaVuota = new Cell(0, 0);
        // Costruire questo edificio richiede molto più di 50 monete
        Residential entityDaCostruire = new Residential(2000.0, 10.0, 10.0, 50);

        // La RegolaFondiSufficienti deve lanciare un'eccezione
        assertThrows(CostruzioneException.class, () -> validator.validaCostruzione(entityDaCostruire, cellaVuota, stato),
                "Deve lanciare eccezione per fondi insufficienti");
    }

    @Test
    void testValidaCostruzione_SuccessoConServizi() {
        UrbanGrid grid = createTestGrid();
        BuilderValidator validator = new BuilderValidator(grid);
        StatoCitta stato = new StatoCitta();

        // Questo test è costruito per far passare la logica della RegolaCollegamentoServizi.
        // Forniamo manualmente tutti e tre i servizi richiesti vicini al punto di costruzione (0,0).
        grid.placeEntity(new PoliceStation(500, 10, 50), 0, 1);
        grid.placeEntity(new FireStation(500, 10, 50), 1, 0);
        grid.placeEntity(new Hospital(500, 10, 50), 1, 1);

        Cell cellaVuota = grid.getCell(0, 0);
        Residential nuovaEntity = new Residential(100.0, 10.0, 10.0, 50);

        // Se tutti i requisiti sono soddisfatti, il costruttore lavora silenziosamente (non lancia nulla)
        assertDoesNotThrow(() -> validator.validaCostruzione(nuovaEntity, cellaVuota, stato),
                "La costruzione deve avere successo se i servizi base sono presenti nelle vicinanze");
    }

    @Test
    void testValidaCostruzione_BordoMappa() {
        UrbanGrid grid = createTestGrid();
        BuilderValidator validator = new BuilderValidator(grid);
        StatoCitta stato = new StatoCitta();

        // Questo test assicura che il calcolo matematico della RegolaCollegamentoServizi
        // non provochi un crash se il raggio di scansione esce fuori dai bordi della mappa.
        grid.placeEntity(new PoliceStation(500, 10, 50), 0, 1);
        grid.placeEntity(new FireStation(500, 10, 50), 1, 0);
        grid.placeEntity(new Hospital(500, 10, 50), 1, 1);

        Cell bordo = grid.getCell(0, 0); // (0,0) è un angolo, la scansione a sinistra (-x) andrebbe fuori mappa
        Residential nuovaEntity = new Residential(100.0, 10.0, 10.0, 50);

        // Deve gestire bene i confini e scansionare solo ciò che rientra tra [0, max]
        assertDoesNotThrow(() -> validator.validaCostruzione(nuovaEntity, bordo, stato),
                "Il validatore deve gestire correttamente le celle ai bordi (0,0) senza andare in crash per ArrayOutOfBounds");
    }
}