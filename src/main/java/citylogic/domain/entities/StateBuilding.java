//N
package citylogic.domain.entities;

/**
 * Categoria base per i servizi pubblici (Polizia, Scuola, Pompieri) 
 * che influiscono direttamente sulle metriche di StatoCitta.
 */
public abstract class StateBuilding extends Infrastructure {

    public StateBuilding(double placementCost, double baseMaintenanceCost, int baseMaxCapacity) {
        super(placementCost, baseMaintenanceCost, baseMaxCapacity);
    }
}