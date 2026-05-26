package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;

public class PoliticaNeutrale implements PoliticaStrategy {
    @Override
    public void applicaModificatori(StatoCitta stato) {
        // Non esegue modifiche: la simulazione segue l'andamento base delle infrastrutture
    }
}