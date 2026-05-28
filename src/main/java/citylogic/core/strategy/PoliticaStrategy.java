package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;

public interface PoliticaStrategy {
    void applicaModificatori(StatoCitta stato, TickStats stats);
}
