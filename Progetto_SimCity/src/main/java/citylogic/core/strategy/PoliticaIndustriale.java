package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.state.TickStats;

public class PoliticaIndustriale implements PoliticaStrategy {
    @Override
    public void applicaModificatori(StatoCitta stato, TickStats stats) {
        int numIndustrie = stats.getIndustrieAttive();
        
        // Incrementa posti di lavoro e introiti a scapito dell'ambiente urbano in proporzione alle industrie
        stato.setLavoro(stato.getLavoro() + (5.0 * numIndustrie));
        stato.setEcologia(stato.getEcologia() - (5.0 * numIndustrie));
        stato.addFinanze(50.0 * numIndustrie); // Introiti derivati da sgravi e incentivi di produzione
    }
}