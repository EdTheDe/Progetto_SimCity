//N
package citylogic.domain.entities;

/**
 * Edificio commerciale.
 */
public class Commercial extends Building {
    
    private double baseIncome;  //Reddito per la città.

    public Commercial(double placementCost, double energyDemand, double waterDemand, double baseIncome) {
        super(placementCost, energyDemand, waterDemand);
        this.baseIncome = baseIncome;
    }

    public double getBaseIncome() {
        return baseIncome * getDevelopmentLevel();
    }
}