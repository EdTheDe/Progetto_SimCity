//N
package citylogic.domain.entities;

/**
 * Immette energia nella Rete Elettrica della città.
 */
public class PowerPlant extends Infrastructure {

    public PowerPlant(double placementCost, double baseMaintenanceCost, int baseEnergyOutput) {
        super(placementCost, baseMaintenanceCost, baseEnergyOutput);
    }

    public int getEnergyOutput() {
        return getMaxCapacity();
    }
}