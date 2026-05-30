package citylogic.core.events;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;

/**
 * Evento conflitto armato.
 * Riduce la sicurezza, la felicità e la popolazione del 10%.
 */
public class GuerraEvent extends RandomEvent {

    public GuerraEvent() {
        super("Guerra", 2); // dura 2 tick
    }

    @Override
    public void applyModifiers(StatoCitta stato, TickStats stats) {
        // La sicurezza crolla e la popolazione diminuisce a causa della guerra
        stato.setSicurezza(stato.getSicurezza() - 40.0);
        stato.setFelicita(stato.getFelicita() - 30.0);
        stato.setPopolazione((int)(stato.getPopolazione() * 0.9)); // Diminuisce del 10%
    }
}
