// Nicolò
package citylogic.domain.entities;

/**
 * Classe base astratta per ogni entità posizionabile sulla griglia (UrbanGrid).
 * Segue il Domain Design Model.
 */
public abstract class UrbanEntity {
    
    private final double placementCost;
    private int developmentLevel;
    private int x = -1;
    private int y = -1;

    public UrbanEntity(double placementCost) {
        this.placementCost = placementCost;
        this.developmentLevel = 1; // KAN 21 -> ?
    }
    
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }
    
    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public double getPlacementCost() {
        return placementCost;
    }

    public int getDevelopmentLevel() {
        return developmentLevel;
    }

    public void upgradeLevel() {
        // Blocco di sicurezza: impedisce di superare il livello 5
        if (this.developmentLevel < 5) {
            this.developmentLevel++;
        }
    }

    public boolean isFunctioning() {
        return true;
    }

    public void processTick(citylogic.domain.state.StatoCitta stato, citylogic.domain.state.TickStats stats) {
        // Default: no-op
    }
}