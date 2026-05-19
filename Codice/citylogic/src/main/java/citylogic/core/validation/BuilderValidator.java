package citylogic.core.validation;

import citylogic.domain.StatoCitta;
import citylogic.domain.map.Cella;
import citylogic.domain.entities.EntitaUrbana;
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
    public void validaCostruzione(EntitaUrbana entita, Cella cella, StatoCitta stato) throws CostruzioneException {
        for (RegolaCostruzione regola : regole) {
            regola.valida(entita, cella, stato);
        }
    }
}

// --- INTERFACCIA E IMPLEMENTAZIONI DELLE REGOLE ---

interface RegolaCostruzione {
    void valida(EntitaUrbana entita, Cella cella, StatoCitta stato) throws CostruzioneException;
}

class RegolaSpazioLibero implements RegolaCostruzione {
    @Override
    public void valida(EntitaUrbana entita, Cella cella, StatoCitta stato) throws CostruzioneException {
        if (cella.isOccupata()) {
            throw new CostruzioneException("Costruzione fallita: La cella selezionata è già occupata.");
        }
    }
}

class RegolaFondiSufficienti implements RegolaCostruzione {
    @Override
    public void valida(EntitaUrbana entita, Cella cella, StatoCitta stato) throws CostruzioneException {
        if (stato.getFinanze() < entita.getCostoPiazzamento()) {
            throw new CostruzioneException(
                String.format("Fondi insufficienti. Costo: %.2f, Disponibili: %.2f", 
                              entita.getCostoPiazzamento(), stato.getFinanze())
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