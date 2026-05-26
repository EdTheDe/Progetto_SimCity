package citylogic.domain.state;

/**
 * Raccoglie le metriche accumulate durante un songolo tick
 * da tutti gli edifici della griglia.
 */
public class TickStats {
    private int capacitaAbitativa = 0;
    private int postiLavoro = 0;
    private int puntiInquinamento = 0;
    private double redditoCommerciale = 0.0;
    private int puntiSicurezza = 0;
    private int puntiSanita = 0;

    // 1. Aggiungi la nuova variabile per la felicità
    private int puntiFelicita = 0;

    public void addCapacitaAbitativa(int v) { this.capacitaAbitativa += v; }
    public void addPostiLavoro(int v) { this.postiLavoro += v; }
    public void addPuntiInquinamento(int v) { this.puntiInquinamento += v; }
    public void addRedditoCommerciale(double v) { this.redditoCommerciale += v; }
    public void addPuntiSicurezza(int v) { this.puntiSicurezza += v; }
    public void addPuntiSanita(int v) { this.puntiSanita += v; }

    // 2. Aggiungi il metodo richiesto da School.java
    public void addPuntiFelicita(int v) { this.puntiFelicita += v; }

    public int getCapacitaAbitativa() { return capacitaAbitativa; }
    public int getPostiLavoro() { return postiLavoro; }
    public int getPuntiInquinamento() { return puntiInquinamento; }
    public double getRedditoCommerciale() { return redditoCommerciale; }
    public int getPuntiSicurezza() { return puntiSicurezza; }
    public int getPuntiSanita() { return puntiSanita; }

    // 3. Aggiungi il getter per coerenza
    public int getPuntiFelicita() { return puntiFelicita; }
}