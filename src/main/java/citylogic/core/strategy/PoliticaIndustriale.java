package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;

public class PoliticaIndustriale implements PoliticaStrategy {
    @Override
    public void applicaModificatori(StatoCitta stato) {
        // Incrementa posti di lavoro e introiti a scapito dell'ambiente urbano
        stato.setLavoro(stato.getLavoro() + 15.0);
        stato.setEcologia(stato.getEcologia() - 12.0);
        stato.addFinanze(200.0); // Introiti derivati da sgravi e incentivi di produzione
    }
}