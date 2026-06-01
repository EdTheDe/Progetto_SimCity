package citylogic.core.validation;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.map.Cell;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.entities.UrbanEntity;
import citylogic.domain.entities.PoliceStation;
import citylogic.domain.entities.FireStation;
import citylogic.domain.entities.Hospital;
import java.util.List;

/**
 * Validatore modulare responsabile di garantire il rispetto delle regole del dominio di gioco.
 * Progettato per rispettare l'Open/Closed Principle (facile aggiungere nuove regole senza modificare le vecchie).
 */
public class BuilderValidator {

    // Lista contenente tutte le regole che devono essere superate
    private final List<RegolaCostruzione> regole;

    /**
     * Costruttore: riceve la griglia in modo che le regole possano esplorare la mappa.
     */
    public BuilderValidator(UrbanGrid griglia) {
        // Registrazione delle regole attive.
        // NOTA: RegolaCollegamentoServizi non è inserita qui di default nel codice originale.
        this.regole = List.of(
                new RegolaSpazioLibero(),
                new RegolaFondiSufficienti()
        );
    }

    /**
     * Cicla tutte le regole. Se una fallisce, interrompe il ciclo lanciando l'eccezione.
     */
    public void validaCostruzione(UrbanEntity entita, Cell cella, StatoCitta stato) throws CostruzioneException {
        for (RegolaCostruzione regola : regole) {
            regola.valida(entita, cella, stato);
        }
    }
}

// --- INTERFACCIA E IMPLEMENTAZIONI DELLE REGOLE ---

/**
 * Interfaccia comune per tutte le regole di costruzione.
 */
interface RegolaCostruzione {
    void valida(UrbanEntity entita, Cell cella, StatoCitta stato) throws CostruzioneException;
}

/**
 * Verifica che la cella di destinazione non contenga già un edificio.
 */
class RegolaSpazioLibero implements RegolaCostruzione {
    @Override
    public void valida(UrbanEntity entita, Cell cella, StatoCitta stato) throws CostruzioneException {
        if (cella.isOccupied()) {
            throw new CostruzioneException("Costruzione fallita: La cella selezionata è già occupata.");
        }
    }
}

/**
 * Verifica che la cassa della città abbia abbastanza soldi per coprire il costo.
 */
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

/**
 * Regola complessa: verifica che un'area sia coperta da Polizia, Pompieri e Ospedale.
 */
class RegolaCollegamentoServizi implements RegolaCostruzione {
    private final UrbanGrid griglia;
    private final int raggioCopertura;

    public RegolaCollegamentoServizi(UrbanGrid griglia, int raggioCopertura) {
        this.griglia = griglia;
        this.raggioCopertura = raggioCopertura;
    }

    @Override
    public void valida(UrbanEntity entita, Cell cella, StatoCitta stato) throws CostruzioneException {
        // Eccezione alla regola: per costruire i servizi stessi non serve averli già
        if (entita instanceof PoliceStation || entita instanceof FireStation || entita instanceof Hospital) {
            return;
        }

        // Flag di copertura
        boolean polizia = false;
        boolean pompieri = false;
        boolean ospedale = false;

        int x = cella.getX();
        int y = cella.getY();

        // Calcoliamo i limiti del rettangolo di scansione.
        // L'uso di Math.max e Math.min protegge dai famigerati errori "Array Index Out Of Bounds" ai bordi della griglia.
        int minX = Math.max(0, x - raggioCopertura);
        int maxX = Math.min(griglia.getWidth() - 1, x + raggioCopertura);
        int minY = Math.max(0, y - raggioCopertura);
        int maxY = Math.min(griglia.getHeight() - 1, y + raggioCopertura);

        // Scansione di ogni cella vicina
        for (int i = minX; i <= maxX; i++) {
            for (int j = minY; j <= maxY; j++) {
                UrbanEntity e = griglia.getCell(i, j).getEntity();
                if (e != null) {
                    if (e instanceof PoliceStation) polizia = true;
                    if (e instanceof FireStation) pompieri = true;
                    if (e instanceof Hospital) ospedale = true;
                }
            }
        }

        // Verifica dei risultati: lancia l'eccezione alla prima mancanza riscontrata
        if (!polizia) {
            throw new CostruzioneException("Costruzione bloccata: l'area non è coperta da una Centrale di Polizia.");
        }
        if (!pompieri) {
            throw new CostruzioneException("Costruzione bloccata: l'area non è coperta da una Caserma dei Pompieri.");
        }
        if (!ospedale) {
            throw new CostruzioneException("Costruzione bloccata: l'area non è coperta da un Ospedale.");
        }
    }
}