package citylogic.core.events;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;

public class CrisiEconomicaEvent extends RandomEvent {

    public CrisiEconomicaEvent() {
        super("Crisi Economica", 3); // dura 3 tick
    }

    @Override
    public void applyModifiers(StatoCitta stato, TickStats stats) {
        // Riduce notevolmente le finanze ad ogni tick dell'evento
        stato.addFinanze(-500.0);
        
        // Abbassa drasticamente i posti di lavoro generati e la sicurezza (criminalità sale)
        stato.setLavoro(stato.getLavoro() - 30.0);
        stato.setSicurezza(stato.getSicurezza() - 25.0);
    }
}
