package citylogic.ui;

import citylogic.core.engine.SimulationEngine;
import citylogic.domain.state.StatoCitta;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;

/**
 * Box contenente i controlli visivi e logici per avanzare nel tempo o gestirne la velocità.
 */
public class PannelloControlliTempo extends VBox {

    private final Timeline simulazioneAuto;
    private boolean inEsecuzione = false;
    private final Slider sliderVelocita;
    private final Button btnAzioneTempo;

    /**
     * Costruisce lo slider per il tempo, istanzia il game loop di JavaFX (Timeline)
     * e implementa la logica del player (Play, Stop, Skip).
     *
     * @param motore Il motore logico della simulazione responsabile del conteggio.
     * @param topBar Riferimento all'header per aggiornarne i valori.
     * @param mappa  Riferimento alla mappa da forzare per il render grafico.
     * @param stato  Classe dati dello stato di simulazione complessivo.
     */
    public PannelloControlliTempo(SimulationEngine motore, TopBar topBar, MappaGriglia mappa, StatoCitta stato) {
        setSpacing(10);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 12; -fx-border-color: rgba(189, 195, 199, 0.8); -fx-border-radius: 12;");
        setAlignment(Pos.CENTER);
        setPrefWidth(160);
        setMaxWidth(160);

        Label lblTempo = new Label("TIME CONTROLS");
        lblTempo.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;");

        sliderVelocita = new Slider(1, 3, 1);
        sliderVelocita.setOrientation(javafx.geometry.Orientation.VERTICAL);
        sliderVelocita.setMinHeight(120); 
        sliderVelocita.setMajorTickUnit(1);
        sliderVelocita.setMinorTickCount(0);
        sliderVelocita.setSnapToTicks(true);
        sliderVelocita.setShowTickMarks(true);
        sliderVelocita.setShowTickLabels(true);
        sliderVelocita.setLabelFormatter(new StringConverter<Double>() {
            @Override
            public String toString(Double n) {
                return n.intValue() + "x";
            }
            @Override
            public Double fromString(String s) {
                return Double.parseDouble(s.replace("x", ""));
            }
        });

        btnAzioneTempo = new Button("SKIP ▶");
        btnAzioneTempo.setMaxWidth(Double.MAX_VALUE);
        btnAzioneTempo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-color: #ecf0f1; -fx-cursor: hand;");

		// Loop  avanzamento dei tick che viene eseguito matematicamente ad ogni secondo 
        simulazioneAuto = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            int tickMultiplier = (int) sliderVelocita.getValue();
            for (int i = 0; i < tickMultiplier; i++) {
                motore.tick(); 
            }
            topBar.aggiornaDati(stato);
            mappa.rinfrescaMappaCompleta();
        }));
        simulazioneAuto.setCycleCount(Animation.INDEFINITE);

		// Ascolatore slider che estisce automaticamente le transizioni Pause/Play 
        sliderVelocita.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal.intValue() > 1) {
                simulazioneAuto.play();
                inEsecuzione = true;
                btnAzioneTempo.setText("STOP 🛑");
                btnAzioneTempo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-color: #e74c3c; -fx-text-fill: white; -fx-cursor: hand;");
            } else {
                fermaSimulazione(btnAzioneTempo);
            }
        });

        btnAzioneTempo.setOnAction(e -> {
            if (inEsecuzione) {
                sliderVelocita.setValue(1);
                fermaSimulazione(btnAzioneTempo);
            } else {
                motore.tick();
                topBar.aggiornaDati(stato);
                mappa.rinfrescaMappaCompleta();
            }
        });

        getChildren().addAll(lblTempo, sliderVelocita, btnAzioneTempo);
    }

    /**
     * Mette in pausa la Timeline di JavaFX e resetta la dicitura del bottone a modalità skip manuale.
     *
     * @param btn Il bottone d'azione del tempo da ripristinare stilisticamente.
     */
    private void fermaSimulazione(Button btn) {
        simulazioneAuto.pause();
        inEsecuzione = false;
        btn.setText("SKIP ▶");
        btn.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-color: #ecf0f1; -fx-text-fill: black; -fx-cursor: hand;");
    }

    /**
     * Riporta lo slider temporale a velocità 1 e forza la messa in pausa dell'orologio interno.
     */
    public void fermaEImpostaManuale() {
        sliderVelocita.setValue(1);
        fermaSimulazione(btnAzioneTempo);
    }
}
