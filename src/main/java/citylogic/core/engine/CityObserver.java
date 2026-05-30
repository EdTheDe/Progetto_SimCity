package citylogic.core.engine;

import citylogic.domain.state.StatoCitta;

/**
 * Interfaccia Observer per aggiornare la UI ad ogni tick.
 */
public interface CityObserver {
    void onSimulationUpdated(StatoCitta stato);
    default void onEventStarted(String eventName, String description) {}
    default void onGameOver() {}
}
