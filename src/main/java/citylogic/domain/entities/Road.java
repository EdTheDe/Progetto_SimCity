//N
package citylogic.domain.entities;

/**
 * Rappresenta un "mattoncino" di strada sulla griglia urbana.
 * Estensione di UrbanEntity perchè non richiede connessioni.
 */
public class Road extends UrbanEntity {

    /**
     * Costruttore per la strada.
     * @param placementCost Il costo di piazzamento iniziale.
     */
    public Road(double placementCost) {
        super(placementCost);
    }
}