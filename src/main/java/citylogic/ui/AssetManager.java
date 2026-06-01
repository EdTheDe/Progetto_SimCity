package citylogic.ui;

import citylogic.domain.entities.*;
import citylogic.domain.map.UrbanGrid;
import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

/**
 * Gestisce il caricamento e la memorizzazione in cache delle risorse grafiche del simulatore.
 * Evita il caricamento multiplo degli stessi file dal disco per ottimizzare le prestazioni.
 */
public class AssetManager {

    private static final Map<String, Image> imageCache = new HashMap<>();
    private static final double CELL_SIZE = 35.0;

    /**
     * Calcola e restituisce l'immagine corretta per una determinata entità urbana.
     * Gestisce i livelli di sviluppo degli edifici e calcola automaticamente
     * le adiacenze per determinare l'asset visivo corretto delle strade.
     *
     * @param e             L'entità urbana da renderizzare.
     * @param x             Coordinata X dell'entità sulla griglia logica.
     * @param y             Coordinata Y dell'entità sulla griglia logica.
     * @param grigliaLogica Il riferimento alla griglia logica per il calcolo delle adiacenze.
     * @return              L'oggetto Image corrispondente, preso dalla cache o caricato dal disco.
     */
    public static Image ottieniImmagine(UrbanEntity e, int x, int y, UrbanGrid grigliaLogica) {
        String nomeFile = "";
        int livello = e.getDevelopmentLevel();
        
        // Cap di sicurezza: blocca l'indice a 4 per prevenire la ricerca di file inesistenti (es. "house_5.png")
        if (livello > 4) livello = 4;

        if (e instanceof Residential) nomeFile = "house_" + livello + ".png";
        else if (e instanceof Industrial) nomeFile = "industry_" + livello + ".png";
        else if (e instanceof Commercial) nomeFile = "commercial_" + livello + ".png";
        else if (e instanceof PowerPlant) nomeFile = "powerplant_" + livello + ".png";
        else if (e instanceof WaterPlant) nomeFile = "waterplant_" + livello + ".png";
        else if (e instanceof PoliceStation) nomeFile = "police_" + livello + ".png";
        else if (e instanceof School) nomeFile = "school_" + livello + ".png";
        else if (e instanceof Hospital) nomeFile = "hospital_" + livello + ".png";
        else if (e instanceof FireStation) nomeFile = (livello == 1) ? "firestation.png" : "firestation_" + livello + ".png";
        else if (e instanceof GreenArea) nomeFile = "greenarea_" + livello + ".png";
        else if (e instanceof Road) {
            // Analisi Adiacenze: AND logico tra la verifica dei bordi della mappa e l'effettiva presenza di un'istanza Road
            boolean nord = y > 0 && grigliaLogica.getCell(x, y - 1).getEntity() instanceof Road;
            boolean sud = y < grigliaLogica.getHeight() - 1 && grigliaLogica.getCell(x, y + 1).getEntity() instanceof Road;
            boolean est = x < grigliaLogica.getWidth() - 1 && grigliaLogica.getCell(x + 1, y).getEntity() instanceof Road;
            boolean ovest = x > 0 && grigliaLogica.getCell(x - 1, y).getEntity() instanceof Road;

            // Algoritmo di auto-tiling stradale: decide il suffisso in base alle booleane attive
            if (nord && sud && est && ovest) nomeFile = "road_cross.png";
            else if (sud && est && ovest) nomeFile = "road_t_s.png";
            else if (nord && est && ovest) nomeFile = "road_t_n.png";
            else if (nord && sud && est) nomeFile = "road_t_e.png";
            else if (nord && sud && ovest) nomeFile = "road_t_w.png";
            else if (nord && est) nomeFile = "road_curve_ne.png";
            else if (nord && ovest) nomeFile = "road_curve_nw.png";
            else if (sud && est) nomeFile = "road_curve_se.png";
            else if (sud && ovest) nomeFile = "road_curve_sw.png";
            else if (nord || sud) nomeFile = "road_v.png";
            else nomeFile = "road_h.png";
        }

        // Cache Hit: l'immagine è già in RAM, evita la latenza di lettura dal disco
        if (imageCache.containsKey(nomeFile)) {
            return imageCache.get(nomeFile);
        }

        try {
            Image img = new Image(AssetManager.class.getResourceAsStream("/immagini/" + nomeFile), CELL_SIZE * 1.8, CELL_SIZE * 1.8, true, true);
            imageCache.put(nomeFile, img);
            return img;
        } catch (Exception ex) {
            return null; 
        }
    }
}
