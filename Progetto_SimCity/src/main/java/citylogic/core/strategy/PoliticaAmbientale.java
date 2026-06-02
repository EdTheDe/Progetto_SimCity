package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;

import citylogic.domain.state.TickStats;

public class PoliticaAmbientale implements PoliticaStrategy {
    @Override
    public void applicaModificatori(StatoCitta stato, TickStats stats) {
        int numIndustrie = stats.getIndustrieAttive();
        
        // Favorisce l'ambiente convertendo l'industria. Coda scalabile in base alle industrie.
        stato.setEcologia(stato.getEcologia() + 10.0 + (5.0 * numIndustrie));
        stato.setLavoro(stato.getLavoro() - 8.0);
        stato.addFinanze(-20.0 * numIndustrie); // Costo di mantenimento della transizione ecologica per industria
    }
}