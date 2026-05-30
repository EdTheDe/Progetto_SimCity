package citylogic.domain.state;

/**
 * Mantiene i dati e i parametri attuali della città.
 * Aggiornato ad ogni tick.
 */
public class StatoCitta {
    private int popolazione;
    private double finanze;
    
    // Metriche percentuali (0.0 - 100.0) che determinano la qualità della vita
    private double felicita;
    private double ecologia;
    private double lavoro;
    private double sicurezza;
    private double sanita;
    
    // Gestione risorse primarie di rete
    private double acquaFornita;
    private double acquaRichiesta;
    private double energiaFornita;
    private double energiaRichiesta;
    
    private int tickets;

    /**
     * Inizializza lo stato con i parametri di partenza.
     */
    public StatoCitta() {
        this.popolazione = 0;
        this.finanze = 4500.0;
        this.felicita = 50.0;
        this.ecologia = 100.0; // Parte pulita
        this.lavoro = 0.0;
        this.sicurezza = 0.0;
        this.sanita = 0.0;
        this.tickets=0;
    }

    public void addFinanze(double delta) { this.finanze += delta; }
    public void setPopolazione(int pop) { this.popolazione = Math.max(0, pop); }
    
    public void setAcquaFornita(double v) { this.acquaFornita = v; }
    public void setAcquaRichiesta(double v) { this.acquaRichiesta = v; }
    public void setEnergiaFornita(double v) { this.energiaFornita = v; }
    public void setEnergiaRichiesta(double v) { this.energiaRichiesta = v; }
    
    public void setFelicita(double v) { this.felicita = clamp(v); }
    public void setEcologia(double v) { this.ecologia = clamp(v); }
    public void setLavoro(double v) { this.lavoro = clamp(v); }
    public void setSicurezza(double v) { this.sicurezza = clamp(v); }
    public void setSanita(double v) { this.sanita = clamp(v); }
    /**
     * Forza un valore nel range percentuale 0-100.
     */
    private double clamp(double value) {
        return Math.max(0.0, Math.min(100.0, value));
    }

    /**
     * Resetta i valori per una nuova partita o un caricamento.
     */
    public void reset() {
        this.popolazione = 0;
        this.finanze = 4500.0;
        this.felicita = 50.0; 
        this.ecologia = 100.0;
        this.lavoro = 0.0;
        this.sicurezza = 0.0;
        this.sanita = 0.0;
        this.acquaFornita = 0.0;
        this.acquaRichiesta = 0.0;
        this.energiaFornita = 0.0;
        this.energiaRichiesta = 0.0;
        this.tickets = 0;
    }

    public int getPopolazione() { return popolazione; }
    public double getFinanze() { return finanze; }
    public double getFelicita() { return felicita; }
    public double getEcologia() { return ecologia; }
    public double getLavoro() { return lavoro; }
    public double getSicurezza() { return sicurezza; }
    public double getSanita() { return sanita; }
    
    public double getAcquaFornita() { return acquaFornita; }
    public double getAcquaRichiesta() { return acquaRichiesta; }
    public double getEnergiaFornita() { return energiaFornita; }
    public double getEnergiaRichiesta() { return energiaRichiesta; }
    public int getTickets() { return tickets; }


    public void setFinanze(double finanze) {
        this.finanze = finanze;
    }
    
    /**
     * Passa al tick successivo.
     */
    public void addTicket() { 
        this.tickets++; 
    }
}
