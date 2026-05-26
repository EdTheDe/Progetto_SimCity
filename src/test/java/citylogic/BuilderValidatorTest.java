package citylogic; // <-- CORRETTO

import citylogic.core.validation.BuilderValidator;
import citylogic.core.validation.CostruzioneException;
import citylogic.domain.state.StatoCitta;
import citylogic.domain.map.Cell;
import citylogic.domain.entities.UrbanEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BuilderValidatorTest {

    private BuilderValidator builderValidator;

    @Mock
    private UrbanEntity entitaMock;

    @Mock
    private Cell cellaMock;

    @Mock
    private StatoCitta statoMock;

    @BeforeEach
    void setUp() {
        builderValidator = new BuilderValidator();
    }

    @Test
    void validaCostruzione_Successo_QuandoSpazioLiberoEFondiSufficienti() {
        when(cellaMock.isOccupied()).thenReturn(false);
        when(entitaMock.getPlacementCost()).thenReturn(100.0);
        when(statoMock.getFinanze()).thenReturn(150.0);

        assertDoesNotThrow(() -> builderValidator.validaCostruzione(entitaMock, cellaMock, statoMock),
                "La validazione non dovrebbe lanciare eccezioni se i requisiti sono soddisfatti.");
    }

    @Test
    void validaCostruzione_Successo_QuandoFondiEsattamenteSufficienti() {
        // Arrange: Il giocatore ha i soldi contati per l'edificio
        when(cellaMock.isOccupied()).thenReturn(false);
        when(entitaMock.getPlacementCost()).thenReturn(100.0);
        when(statoMock.getFinanze()).thenReturn(100.0);

        // Act & Assert
        assertDoesNotThrow(() -> builderValidator.validaCostruzione(entitaMock, cellaMock, statoMock),
                "La validazione deve passare se i fondi del giocatore sono esattamente pari al costo dell'edificio.");
    }

    @Test
    void validaCostruzione_LanciaEccezione_QuandoCellaOccupata() {
        when(cellaMock.isOccupied()).thenReturn(true);

        CostruzioneException exception = assertThrows(CostruzioneException.class,
                () -> builderValidator.validaCostruzione(entitaMock, cellaMock, statoMock));

        assertEquals("Costruzione fallita: La cella selezionata è già occupata.", exception.getMessage());
    }

    @Test
    void validaCostruzione_LanciaEccezione_QuandoFondiInsufficienti() {
        when(cellaMock.isOccupied()).thenReturn(false);
        when(entitaMock.getPlacementCost()).thenReturn(200.0);
        when(statoMock.getFinanze()).thenReturn(50.0);

        CostruzioneException exception = assertThrows(CostruzioneException.class,
                () -> builderValidator.validaCostruzione(entitaMock, cellaMock, statoMock));

        // Uso di String.format per rispettare fedelmente l'eccezione lanciata dalla classe originale
        String expectedMessage = String.format("Fondi insufficienti. Costo: %.2f, Disponibili: %.2f", 200.0, 50.0);
        assertEquals(expectedMessage, exception.getMessage());
    }
}