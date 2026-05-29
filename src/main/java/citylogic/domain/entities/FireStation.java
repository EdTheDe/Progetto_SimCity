//N
package citylogic.domain.entities;

public class FireStation extends StateBuilding {
    public FireStation(double placementCost, double baseMaintenanceCost, int baseFirefightingCapacity) {
        super(placementCost, baseMaintenanceCost, baseFirefightingCapacity);
    }

    @Override
    public void processTick(citylogic.domain.state.StatoCitta stato, citylogic.domain.state.TickStats stats) {
        super.processTick(stato, stats);
        stats.addPuntiSicurezza(getMaxCapacity());
    }
}