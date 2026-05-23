//N
package citylogic.domain.entities;

/**
 * Edificio Industriale.
 */
public class Industrial extends Building {
    
    private double basePollution;   //Inquinamento prodotto da una fabbrica
    private int jobsProduced;

    public Industrial(double placementCost, double energyDemand, double waterDemand, double basePollution, int jobsProduced) {
        super(placementCost, energyDemand, waterDemand);
        this.basePollution = basePollution;
        this.jobsProduced = jobsProduced;
    }

    public double getBasePollution() {
        return basePollution;
    }

    public int getJobsProduced() {
        return jobsProduced;
    }
}