package citylogic.core.events;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;

/**
 * Evento positivo stagionale.
 * Aumenta la felicità e l'ecologia della città.
 */
public class PrimaveraEvent extends RandomEvent {

    public PrimaveraEvent() {
        super("Primavera", 4); // dura 4 tick
    }

    @Override
    public void applyModifiers(StatoCitta stato, TickStats stats) {
        // Boom di felicità ed ecologia
        stato.setFelicita(stato.getFelicita() + 20.0);
        stato.setEcologia(stato.getEcologia() + 15.0);
    }
}
