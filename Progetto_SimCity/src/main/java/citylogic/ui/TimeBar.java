package citylogic.ui;

import citylogic.core.engine.SimulationEngine;
import citylogic.domain.state.StatoCitta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

public class TimeBar extends VBox implements citylogic.core.engine.CityObserver {

    private final PannelloNotifiche pannelloNotifiche;
    private final PannelloRisorse pannelloRisorse;
    private final PannelloControlliTempo pannelloTempo;
    private final SimulationEngine motore;

    public TimeBar(SimulationEngine motore, TopBar topBar, MappaGriglia mappa, StatoCitta stato) {
        this.motore = motore;
        
        setSpacing(15);
        setPadding(new Insets(15));
        setMinWidth(180);
        setMaxWidth(180);
        setPrefWidth(180);
        setAlignment(Pos.BOTTOM_RIGHT);
        setPickOnBounds(false);

        // Istanziazione delle classi delegate
        pannelloNotifiche = new PannelloNotifiche();
        pannelloRisorse = new PannelloRisorse();
        pannelloTempo = new PannelloControlliTempo(motore, topBar, mappa, stato);

        getChildren().addAll(pannelloNotifiche, pannelloRisorse, pannelloTempo);
    }

    public void fermaEImpostaManuale() {
        pannelloTempo.fermaEImpostaManuale();
    }

    @Override
    public void onSimulationUpdated(StatoCitta stato) {
        // Delega l'aggiornamento visivo ai rispettivi pannelli
        pannelloRisorse.aggiornaRisorse(stato);
        pannelloNotifiche.aggiornaNotifiche(motore);
    }
}
