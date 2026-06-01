package citylogic.infrastructure;

/**
 * Modello dati semplificato (POJO) che rappresenta un singolo edificio nel salvataggio.
 * Contiene solo i dati essenziali: dove si trova, cos'è e che livello ha raggiunto.
 */
public class SavedEntityData {
    private int x;
    private int y;
    private String tipo;    // Es: "Residential", "Hospital"
    private int livello;    // Livello di sviluppo raggiunto

    /**
     * Costruttore vuoto.
     * È OBBLIGATORIO per la libreria Jackson, che lo usa per istanziare l'oggetto vuoto
     * durante la lettura del file prima di iniettarci i dati tramite i Setter.
     */
    public SavedEntityData() {}

    /**
     * Costruttore completo usato durante la fase di salvataggio.
     */
    public SavedEntityData(int x, int y, String tipo, int livello) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.livello = livello;
    }

    // Getters e Setters standard
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getLivello() { return livello; }
    public void setLivello(int livello) { this.livello = livello; }
}