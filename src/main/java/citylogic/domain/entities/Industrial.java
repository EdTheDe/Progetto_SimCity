//N
package citylogic.domain.entities;

/**
 * Edificio Industriale.
 */
public class Industrial extends Building {
    
    private double basePollution;   //Inquinamento prodotto da una fabbrica
    private int baseJobsProduced;       

    public Industrial(double placementCost, double energyDemand, double waterDemand, double basePollution, int baseJobsProduced) {
        super(placementCost, energyDemand, waterDemand);
        this.basePollution = basePollution;
        this.baseJobsProduced = baseJobsProduced;
    }

    public double getBasePollution() {
        return basePollution * getDevelopmentLevel();
    }

    public int getJobsProduced() {
        return baseJobsProduced * getDevelopmentLevel();
    }
}