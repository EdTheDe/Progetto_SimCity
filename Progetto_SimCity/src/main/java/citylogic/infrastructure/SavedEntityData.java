package citylogic.infrastructure;

public class SavedEntityData {
    private int x;
    private int y;
    private String tipo;
    private int livello;

    // Costruttore vuoto richiesto per la deserializzazione JSON
    public SavedEntityData() {}

    public SavedEntityData(int x, int y, String tipo, int livello) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.livello = livello;
    }

    // Getters e Setters
    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getLivello() { return livello; }
    public void setLivello(int livello) { this.livello = livello; }
}