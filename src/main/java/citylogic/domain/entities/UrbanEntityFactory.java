//N
package citylogic.domain.entities;

/**
 * Factory centralizzata per la creazione di qualsiasi entità sulla mappa.
 * Isola i costi di bilanciamento e disaccoppia il motore dalle implementazioni concrete.
 */
public class UrbanEntityFactory {

    /**
     * Crea un'entità in base alla stringa richiesta.
     * @param type Il nome dell'entità (es. "residential", "powerplant", "road")
     * @return Un'istanza polimorfica di UrbanEntity
     */
    public static UrbanEntity createEntity(String type) {
        if (type == null) {
            throw new IllegalArgumentException("Entity type cannot be null");
        }

        switch (type.toLowerCase()) {

            // --- ZONE (BUILDINGS) ---
            case "residential":
                // Costo, Energia, Acqua, Capacità abitativa
                return new Residential(100.0, 10.0, 5.0, 20);
            case "industrial":
                // Costo, Energia, Acqua, Inquinamento base, Lavori base
                return new Industrial(150.0, 20.0, 15.0, 15.0, 10);
            case "commercial":
                // Costo, Energia, Acqua, Reddito base
                return new Commercial(120.0, 12.0, 8.0, 25.0);

            // --- INFRASTRUTTURE DI RETE ---
            case "powerplant":
                // Costo, Mantenimento, Energia immessa
                return new PowerPlant(500.0, 50.0, 100);
            case "waterplant":
                // Costo, Mantenimento, Acqua immessa
                return new WaterPlant(400.0, 40.0, 100);

            // --- SERVIZI STATALI ---
            case "police":
            case "policestation": // Aggiunto per compatibilità con il caricamento da file JSON
                // Costo, Mantenimento, Capacità sicurezza
                return new PoliceStation(300.0, 30.0, 50);
            case "school":
                // Costo, Mantenimento, Capacità istruzione
                return new School(250.0, 25.0, 40);
            case "firestation":
                // Costo, Mantenimento, Capacità pompieri
                return new FireStation(300.0, 30.0, 50);

            // --- VIABILITÀ ---
            case "road":
                // Solo costo di piazzamento
                return new Road(10.0);
            // Ospedale
            case "hospital":
                // Costo di piazzamento, Mantenimento, Capacità sanità erogata
                return new Hospital(400.0, 40.0, 60);
            // --- ECOLOGIA E PARCHI ---
            case "greenarea":
                // Costo, Mantenimento, Riduzione inquinamento
                return new GreenArea(50.0, 5.0, -20);
            default:
                throw new IllegalArgumentException("Unknown entity type: " + type);
        }
    }
}