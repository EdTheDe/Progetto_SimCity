//N
package citylogic.domain.entities;

/**
 * Edificio Residenziale.
 */
public class Residential extends Building {
    
    private int baseHousingCapacity; //capacità abitativa

    public Residential(double placementCost, double energyDemand, double waterDemand, int baseHousingCapacity) {
        super(placementCost, energyDemand, waterDemand);
        this.baseHousingCapacity = baseHousingCapacity;
    }

    /**
     * Calcola la capacità abitativa dinamicamente in base al livello di sviluppo.
     * @return
     */
    public int getHousingCapacity() {
        return baseHousingCapacity * getDevelopmentLevel();
    }

    @Override
    public void processTick(citylogic.domain.state.StatoCitta stato, citylogic.domain.state.TickStats stats) {
        stats.addCapacitaAbitativa(getHousingCapacity());
    }
}