//N
package citylogic.domain.entities;

/**
 * Tipi di entità urbane (UrbanEntity) che richiedono risorse (acqua e corrente) per funzionare.
 */
public abstract class Building extends UrbanEntity {
    
    private double energyDemand;    //"costo" in energia
    private double waterDemand;     //"costo" in acqua

    public Building(double placementCost, double energyDemand, double waterDemand) {
        super(placementCost);
        this.energyDemand = energyDemand;
        this.waterDemand = waterDemand;
    }

    /**
     * Il consumo di energia scala dinamicamente in base al livello di sviluppo dell'edificio.
     */
    public double getEnergyDemand() {
        return energyDemand * getDevelopmentLevel();
    }

    /**
     * Il consumo di acqua scala dinamicamente in base al livello di sviluppo dell'edificio.
     */
    public double getWaterDemand() {
        return waterDemand * getDevelopmentLevel();
    }
}