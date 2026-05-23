//N
package citylogic.domain.entities;

/**
 * Edificio Residenziale.
 */
public class Residential extends Building {
    
    private int housingCapacity; //capacità abitativa

    public Residential(double placementCost, double energyDemand, double waterDemand, int housingCapacity) {
        super(placementCost, energyDemand, waterDemand);
        this.housingCapacity = housingCapacity;
    }

    public int getHousingCapacity() {
        return housingCapacity;
    }
}