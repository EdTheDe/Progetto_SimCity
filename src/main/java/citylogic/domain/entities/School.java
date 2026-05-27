//N
package citylogic.domain.entities;

public class School extends StateBuilding {
    public School(double placementCost, double baseMaintenanceCost, int baseEducationCapacity) {
        super(placementCost, baseMaintenanceCost, baseEducationCapacity);
    }

    @Override
    public void processTick(citylogic.domain.state.StatoCitta stato, citylogic.domain.state.TickStats stats) {
        super.processTick(stato, stats);
        stats.addPuntiFelicita(getMaxCapacity()); // La scuola inietta felicità extra
    }
}