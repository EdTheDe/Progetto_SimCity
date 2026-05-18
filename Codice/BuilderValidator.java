package citylogic.core.validation;

import citylogic.domain.StatoCitta;
import citylogic.domain.map.Cella;
import citylogic.domain.entities.EntitaUrbana;

/**
 * Validatore responsabile di garantire il rispetto delle regole del dominio
 * prima della costruzione di una nuova Entità Urbana.
 */
public class BuilderValidator {

    /**
     * Valida se un'entità può essere costruita in una determinata cella.
     * * @param entita L'entità da piazzare.
     * @param cella La cella di destinazione.
     * @param stato Lo stato attuale della città (per verificare i fondi).
     * @throws CostruzioneException se una regola di business viene violata.
     */
    public void validaCostruzione(EntitaUrbana entita, Cella cella, StatoCitta stato) throws CostruzioneException {
        
        // 1. Verifica che lo spazio sia libero
        if (cella.isOccupata()) {
            throw new CostruzioneException("Costruzione fallita: La cella selezionata è già occupata.");
        }

        // 2. Verifica la disponibilità economica
        if (stato.getFinanze() < entita.getCostoPiazzamento()) {
            throw new CostruzioneException(
                String.format("Fondi insufficienti. Costo: %.2f, Disponibili: %.2f", 
                              entita.getCostoPiazzamento(), stato.getFinanze())
            );
        }

        // 3. (Esempio di espansione futura) Verifica adiacenza strade
        if (entita.richiedeStrada() && !cella.haStradaAdiacente()) {
             throw new CostruzioneException("L'edificio deve essere adiacente a una strada.");
        }
    }
}

// Eccezione personalizzata per la validazione
class CostruzioneException extends Exception {
    public CostruzioneException(String message) {
        super(message);
    }
}