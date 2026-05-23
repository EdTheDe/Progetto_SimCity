package citylogic.core.validation;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.map.Cell;
import citylogic.domain.entities.UrbanEntity;
import java.util.List;

/**
 * Validatore modulare responsabile di garantire il rispetto delle regole del dominio.
 * Rispetta l'Open/Closed Principle: nuove regole possono essere aggiunte agilmente.
 */
public class BuilderValidator {

    private final List<RegolaCostruzione> regole;

    public BuilderValidator() {
        // Registrazione delle regole attive nel motore
        this.regole = List.of(
            new RegolaSpazioLibero(),
            new RegolaFondiSufficienti()
        );
    }

    /**
     * Esegue tutte le validazioni necessarie prima di autorizzare la costruzione.
     */
    public void validaCostruzione(UrbanEntity entita, Cell cella, StatoCitta stato) throws CostruzioneException {
        for (RegolaCostruzione regola : regole) {
            regola.valida(entita, cella, stato);
        }
    }
}

// --- INTERFACCIA E IMPLEMENTAZIONI DELLE REGOLE ---

interface RegolaCostruzione {
    void valida(UrbanEntity entita, Cell cella, StatoCitta stato) throws CostruzioneException;
}

class RegolaSpazioLibero implements RegolaCostruzione {
    @Override
    public void valida(UrbanEntity entita, Cell cella, StatoCitta stato) throws CostruzioneException {
        if (cella.isOccupied()) {
            throw new CostruzioneException("Costruzione fallita: La cella selezionata è già occupata.");
        }
    }
}

class RegolaFondiSufficienti implements RegolaCostruzione {
    @Override
    public void valida(UrbanEntity entita, Cell cella, StatoCitta stato) throws CostruzioneException {
        if (stato.getFinanze() < entita.getPlacementCost()) {
            throw new CostruzioneException(
                String.format("Fondi insufficienti. Costo: %.2f, Disponibili: %.2f", 
                              entita.getPlacementCost(), stato.getFinanze())
            );
        }
    }
}

// --- ECCEZIONE PERSONALIZZATA ---

class CostruzioneException extends Exception {
    public CostruzioneException(String message) {
        super(message);
    }
}
