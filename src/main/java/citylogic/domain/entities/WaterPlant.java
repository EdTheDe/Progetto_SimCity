//N
package citylogic.domain.entities;

/**
 * Immette acqua nella Rete Idrica della città.
 */
public class WaterPlant extends Infrastructure {

    public WaterPlant(double placementCost, double baseMaintenanceCost, int baseWaterOutput) {
        super(placementCost, baseMaintenanceCost, baseWaterOutput);
    }

    public int getWaterOutput() {
        return getMaxCapacity();
    }
}