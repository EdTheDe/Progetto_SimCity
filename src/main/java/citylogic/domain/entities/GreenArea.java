//N
package citylogic.domain.entities;

/**
 * Rappresenta un'area verde per contrastare l'inquinamento delle industrie. 
 * Migliora l'ecologia della città.
 */
public class GreenArea extends Infrastructure {

    /**
     * Costruttore per l'Area Verde.
     * @param placementCost Costo di piazzamento iniziale.
     * @param baseMaintenanceCost Costo di manutenzione mensile base.
     * @param basePollutionReduction Capacità base di riduzione dell'inquinamento.
     */
    public GreenArea(double placementCost, double baseMaintenanceCost, int basePollutionReduction) {
        super(placementCost, baseMaintenanceCost, basePollutionReduction);
    }

    /**
     * Restituisce il valore di inquinamento abbattuto, scalato in base al livello.
     * Alias semantico per aiutare il collega P2 nel SimulationEngine.
     */
    public int getPollutionReduction() {
        return getMaxCapacity();
    }
}