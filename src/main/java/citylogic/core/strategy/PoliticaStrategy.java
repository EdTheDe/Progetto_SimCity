package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;

/**
 * Interfaccia per le politiche cittadine (Pattern Strategy).
 * Modifica i parametri globali della simulazione.
 */
public interface PoliticaStrategy {
    
    /**
     * Applica l'effetto della politica allo stato.
     */
    void applicaModificatori(StatoCitta stato, TickStats stats);
}
