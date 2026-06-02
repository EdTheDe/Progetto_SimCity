package citylogic.domain.entities;

public class Hospital extends StateBuilding {
    public Hospital(double placementCost, double baseMaintenanceCost, int baseHealthCapacity) {
        super(placementCost, baseMaintenanceCost, baseHealthCapacity);
    }

    @Override
    public void processTick(citylogic.domain.state.StatoCitta stato, citylogic.domain.state.TickStats stats) {
        super.processTick(stato, stats);
        stats.addPuntiSanita(getMaxCapacity()); // L'ospedale fornisce i punti sanità al motore logico
    }
}