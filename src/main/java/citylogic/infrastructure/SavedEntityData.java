package citylogic.infrastructure;

public class SavedEntityData {
    private int x;
    private int y;
    private String tipo;
    private int livelloSviluppo;

    public SavedEntityData() {}

    public SavedEntityData(int x, int y, String tipo, int livelloSviluppo) {
        this.x = x;
        this.y = y;
        this.tipo = tipo;
        this.livelloSviluppo = livelloSviluppo;
    }

    public int getX() { return x; }
    public void setX(int x) { this.x = x; }

    public int getY() { return y; }
    public void setY(int y) { this.y = y; }

    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }

    public int getLivelloSviluppo() { return livelloSviluppo; }
    public void setLivelloSviluppo(int livelloSviluppo) { this.livelloSviluppo = livelloSviluppo; }
}