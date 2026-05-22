// Nicolò
package citylogic.domain.entities;

/**
 * Classe base astratta per ogni entità posizionabile sulla griglia (UrbanGrid).
 * Segue il Domain Design Model.
 */
public abstract class UrbanEntity {
    
    private final double placementCost;
    private int developmentLevel;

    public UrbanEntity(double placementCost) {
        this.placementCost = placementCost;
        this.developmentLevel = 1; // KAN 21 -> ?
    }

    public double getPlacementCost() {
        return placementCost;
    }

    public int getDevelopmentLevel() {
        return developmentLevel;
    }

    public void upgradeLevel() {
        this.developmentLevel++;
    }
}