package citylogic.core.strategy;

import citylogic.domain.state.StatoCitta;

import citylogic.domain.state.TickStats;

/**
 * Applica la politica ambientale.
 * Aumenta l'ecologia ma riduce fondi e occupazione in base alle industrie.
 */
public class PoliticaAmbientale implements PoliticaStrategy {
    @Override
    public void applicaModificatori(StatoCitta stato, TickStats stats) {
        int numIndustrie = stats.getIndustrieAttive();
        
        // Converte l'industria per favorire l'ambiente
        stato.setEcologia(stato.getEcologia() + 10.0 + (5.0 * numIndustrie));
        stato.setLavoro(stato.getLavoro() - 8.0);
        stato.addFinanze(-20.0 * numIndustrie); 
    }
}