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
 * Validatore modulare responsabile di garantire il rispetto delle regole del dominio.
 * Rispetta l'Open/Closed Principle: nuove regole possono essere aggiunte agilmente.
 */
public class BuilderValidator {

    private final List<RegolaCostruzione> regole;

    // MODIFICA: Ora riceve la griglia in modo da poter scansionare i dintorni
    public BuilderValidator(UrbanGrid griglia) {
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

// NUOVA REGOLA
class RegolaCollegamentoServizi implements RegolaCostruzione {
    private final UrbanGrid griglia;
    private final int raggioCopertura;

    public RegolaCollegamentoServizi(UrbanGrid griglia, int raggioCopertura) {
        this.griglia = griglia;
        this.raggioCopertura = raggioCopertura;
    }

    @Override
    public void valida(UrbanEntity entita, Cell cella, StatoCitta stato) throws CostruzioneException {
        // I servizi pubblici non possono richiedere se stessi per essere costruiti, altrimenti bloccheremmo il gioco
        if (entita instanceof PoliceStation || entita instanceof FireStation || entita instanceof Hospital) {
            return;
        }

        boolean polizia = false;
        boolean pompieri = false;
        boolean ospedale = false;

        int x = cella.getX();
        int y = cella.getY();

        // Calcoliamo i limiti della scansione per evitare errori "Out of Bounds" sui bordi della mappa
        int minX = Math.max(0, x - raggioCopertura);
        int maxX = Math.min(griglia.getWidth() - 1, x + raggioCopertura);
        int minY = Math.max(0, y - raggioCopertura);
        int maxY = Math.min(griglia.getHeight() - 1, y + raggioCopertura);

        // Scansione delle celle circostanti
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

        // Verifica finale: se manca un servizio lanciamo la tua CostruzioneException
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