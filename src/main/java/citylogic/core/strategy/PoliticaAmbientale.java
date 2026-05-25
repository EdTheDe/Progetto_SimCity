package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;

public class PoliticaAmbientale implements PoliticaStrategy {
    @Override
    public void applicaModificatori(StatoCitta stato) {
        // Favorisce l'ambiente penalizzando la produzione industriale e le finanze operative
        stato.setEcologia(stato.getEcologia() + 15.0);
        stato.setLavoro(stato.getLavoro() - 8.0);
        stato.addFinanze(-100.0); // Costo di mantenimento della transizione ecologica
    }
}