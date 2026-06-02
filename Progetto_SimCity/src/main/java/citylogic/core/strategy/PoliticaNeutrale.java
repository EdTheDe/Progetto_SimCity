package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;

import citylogic.domain.state.TickStats;

public class PoliticaNeutrale implements PoliticaStrategy {
    @Override
    public void applicaModificatori(StatoCitta stato, TickStats stats) {
        // Non esegue modifiche: la simulazione segue l'andamento base delle infrastrutture
    }
}