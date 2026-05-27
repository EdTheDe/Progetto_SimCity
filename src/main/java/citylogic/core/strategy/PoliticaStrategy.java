package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;

public interface PoliticaStrategy {
    void applicaModificatori(StatoCitta stato);
}
