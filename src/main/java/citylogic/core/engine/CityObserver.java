package citylogic.core.engine;

import citylogic.domain.state.StatoCitta;

/**
 * Interfaccia per il pattern Observer.
 * Permette alla UI di ascoltare gli aggiornamenti del motore senza accoppiamento diretto.
 */
public interface CityObserver {
    void onSimulationUpdated(StatoCitta stato);
    default void onEventStarted(String eventName, String description) {}
    default void onGameOver() {}
}
