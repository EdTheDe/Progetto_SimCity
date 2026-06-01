package citylogic.ui;

import citylogic.core.engine.SimulationEngine;
import citylogic.domain.state.StatoCitta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.VBox;

/**
 * Controller-Contenitore che funge da blocco in basso a destra dello schermo.
 * Istanzia ed orchestra l'aggiornamento dei widget legati al tempo, alle notifiche e alle risorse.
 */
public class TimeBar extends VBox implements citylogic.core.engine.CityObserver {

    private final PannelloNotifiche pannelloNotifiche;
    private final PannelloRisorse pannelloRisorse;
    private final PannelloControlliTempo pannelloTempo;
    private final SimulationEngine motore;

    /**
     * Assegna il posizionamento dei layout delegati, unificando le informazioni sensibili in una colonna.
     *
     * @param motore Il gestore della logica temporale.
     * @param topBar Barra superiore da costringere agli update.
     * @param mappa  Mappa centrale da ripristinare o forzare al refresh.
     * @param stato  Il modello dei dati in cui pescare i numeri.
     */
    public TimeBar(SimulationEngine motore, TopBar topBar, MappaGriglia mappa, StatoCitta stato) {
        this.motore = motore;
        
        setSpacing(15);
        setPadding(new Insets(15));
        setMinWidth(180);
        setMaxWidth(180);
        setPrefWidth(180);
        setAlignment(Pos.BOTTOM_RIGHT);
        setPickOnBounds(false);

        pannelloNotifiche = new PannelloNotifiche();
        pannelloRisorse = new PannelloRisorse();
        pannelloTempo = new PannelloControlliTempo(motore, topBar, mappa, stato);

        getChildren().addAll(pannelloNotifiche, pannelloRisorse, pannelloTempo);
    }

    /**
     * Espone al mondo esterno (come pop-up e menu di sistema) la facoltà di spegnere 
     * lo scorrere logico del timer agendo sul sotto-modulo preposto.
     */
    public void fermaEImpostaManuale() {
        pannelloTempo.fermaEImpostaManuale();
    }

    /**
     * Intercetta l'avvenuta esecuzione di un tick per informare i riquadri inferiori a cambiare valori.
     *
     * @param stato Lo stato modificato pronto ad essere letto.
     */
    @Override
    public void onSimulationUpdated(StatoCitta stato) {
		
		// Implementazione dell'interfaccia Observer: smista il task del refresh grafico ai sotto-pannelli responsabili
        pannelloRisorse.aggiornaRisorse(stato);
        pannelloNotifiche.aggiornaNotifiche(motore);
    }
}
