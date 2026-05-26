package citylogic.infrastructure;

import citylogic.domain.state.StatoCitta;
import java.util.List;

public class SaveGameData {
    private StatoCitta statoCitta;
    private List<SavedEntityData> edificiSalvati;

    public SaveGameData() {}

    public SaveGameData(StatoCitta statoCitta, List<SavedEntityData> edificiSalvati) {
        this.statoCitta = statoCitta;
        this.edificiSalvati = edificiSalvati;
    }

    public StatoCitta getStatoCitta() { return statoCitta; }
    public void setStatoCitta(StatoCitta statoCitta) { this.statoCitta = statoCitta; }

    public List<SavedEntityData> getEdificiSalvati() { return edificiSalvati; }
    public void setEdificiSalvati(List<SavedEntityData> edificiSalvati) { this.edificiSalvati = edificiSalvati; }
}