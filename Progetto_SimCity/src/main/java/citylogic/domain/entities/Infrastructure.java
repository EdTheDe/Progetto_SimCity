//N
package citylogic.domain.entities;

/**
 * Categoria di entità destinate alla produzione di risorse.
 * Hanno un limite operazionale (maxCapacity) e necessitano di un costo di mantenimento.
 */
public abstract class Infrastructure extends UrbanEntity {
    
    private final double baseMaintenanceCost;
    private final int baseMaxCapacity;

    public Infrastructure(double placementCost, double baseMaintenanceCost, int baseMaxCapacity) {
        super(placementCost);
        this.baseMaintenanceCost = baseMaintenanceCost;
        this.baseMaxCapacity = baseMaxCapacity;
    }

    /**
     * Il costo di mantenimento aumenta con il livello dell'infrastruttura.
     */
    public double getMaintenanceCost() {
        return baseMaintenanceCost * getDevelopmentLevel();
    }

    /**
     * La capacità operativa (quanta energia/acqua/servizi fornisce) scala con il livello.
     */
    public int getMaxCapacity() {
        return baseMaxCapacity * getDevelopmentLevel();
    }

    @Override
    public void processTick(citylogic.domain.state.StatoCitta stato, citylogic.domain.state.TickStats stats) {
        stato.addFinanze(-getMaintenanceCost());
    }
}