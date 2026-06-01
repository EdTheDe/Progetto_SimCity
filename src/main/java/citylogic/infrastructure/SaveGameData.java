package citylogic.infrastructure;

import java.util.ArrayList;
import java.util.List;

/**
 * Contenitore principale (Root Object) del file di salvataggio JSON.
 * Raccoglie tutte le statistiche e l'elenco di tutti gli edifici presenti.
 */
public class SaveGameData {
    // Statistiche generali della città
    private double finanze;
    private int popolazione;
    private double felicita;
    private double ecologia;
    private double sicurezza;
    private double sanita;
    private double lavoro;

    // Lista contenente le posizioni e i tipi di tutti gli edifici costruiti
    private List<SavedEntityData> edifici;

    public SaveGameData() {
        // Inizializza la lista per evitare che Jackson o la logica di gioco incontrino fastidiosi NullPointerException
        this.edifici = new ArrayList<>();
    }

    // Getters e Setters per ogni proprietà
    public double getFinanze() { return finanze; }
    public void setFinanze(double finanze) { this.finanze = finanze; }

    public int getPopolazione() { return popolazione; }
    public void setPopolazione(int popolazione) { this.popolazione = popolazione; }

    public double getFelicita() { return felicita; }
    public void setFelicita(double felicita) { this.felicita = felicita; }

    public double getEcologia() { return ecologia; }
    public void setEcologia(double ecologia) { this.ecologia = ecologia; }

    public double getSicurezza() { return sicurezza; }
    public void setSicurezza(double sicurezza) { this.sicurezza = sicurezza; }

    public double getSanita() { return sanita; }
    public void setSanita(double sanita) { this.sanita = sanita; }

    public double getLavoro() { return lavoro; }
    public void setLavoro(double lavoro) { this.lavoro = lavoro; }

    public List<SavedEntityData> getEdifici() { return edifici; }
    public void setEdifici(List<SavedEntityData> edifici) { this.edifici = edifici; }
}