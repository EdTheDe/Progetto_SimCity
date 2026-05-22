package citylogic.core.validation;

import citylogic.domain.StatoCitta;
import citylogic.domain.map.Cella;
import citylogic.domain.entities.EntitaUrbana;
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
    private EntitaUrbana entitaMock;

    @Mock
    private Cella cellaMock;

    @Mock
    private StatoCitta statoMock;

    @BeforeEach
    void setUp() {
        builderValidator = new BuilderValidator();
    }

    @Test
    void validaCostruzione_Successo_QuandoSpazioLiberoEFondiSufficienti() {
        // Arrange
        when(cellaMock.isOccupata()).thenReturn(false);
        when(entitaMock.getCostoPiazzamento()).thenReturn(100.0);
        when(statoMock.getFinanze()).thenReturn(150.0);

        // Act & Assert
        assertDoesNotThrow(() -> builderValidator.validaCostruzione(entitaMock, cellaMock, statoMock),
                "La validazione non dovrebbe lanciare eccezioni se i requisiti sono soddisfatti.");
    }

    @Test
    void validaCostruzione_LanciaEccezione_QuandoCellaOccupata() {
        // Arrange
        when(cellaMock.isOccupata()).thenReturn(true);

        // Act & Assert
        CostruzioneException exception = assertThrows(CostruzioneException.class, 
                () -> builderValidator.validaCostruzione(entitaMock, cellaMock, statoMock));
        
        assertEquals("Costruzione fallita: La cella selezionata è già occupata.", exception.getMessage());
    }

    @Test
    void validaCostruzione_LanciaEccezione_QuandoFondiInsufficienti() {
        // Arrange
        when(cellaMock.isOccupata()).thenReturn(false); // Passa la prima regola
        when(entitaMock.getCostoPiazzamento()).thenReturn(200.0);
        when(statoMock.getFinanze()).thenReturn(50.0);

        // Act & Assert
        CostruzioneException exception = assertThrows(CostruzioneException.class, 
                () -> builderValidator.validaCostruzione(entitaMock, cellaMock, statoMock));
        
        assertEquals("Fondi insufficienti. Costo: 200,00, Disponibili: 50,00", exception.getMessage().replace(".", ",")); // Gestione della virgola mobile in base al locale
    }
}