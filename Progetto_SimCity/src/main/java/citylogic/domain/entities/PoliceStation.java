//N
package citylogic.domain.entities;

public class PoliceStation extends StateBuilding {
    public PoliceStation(double placementCost, double baseMaintenanceCost, int baseSecurityCapacity) {
        super(placementCost, baseMaintenanceCost, baseSecurityCapacity);
    }

    @Override
    public void processTick(citylogic.domain.state.StatoCitta stato, citylogic.domain.state.TickStats stats) {
        super.processTick(stato, stats);
        stats.addPuntiSicurezza(getMaxCapacity());
    }
}