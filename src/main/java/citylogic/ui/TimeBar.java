package citylogic.ui;

import citylogic.core.engine.SimulationEngine;
import citylogic.domain.state.StatoCitta;
import citylogic.domain.entities.UrbanEntity;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import javafx.util.StringConverter;
import java.util.List;
import java.util.HashSet;
import java.util.Set;

public class TimeBar extends VBox implements citylogic.core.engine.CityObserver {

    private final Timeline simulazioneAuto;
    private boolean inEsecuzione = false;
    
    private ProgressBar pbAcqua;
    private Label lblAcqua;
    
    private ProgressBar pbEnergia;
    private Label lblEnergia;

    private Label lblNotifiche;
    private SimulationEngine motore;

    public TimeBar(SimulationEngine motore, TopBar topBar, MappaGriglia mappa, StatoCitta stato) {
        this.motore = motore;
        
        setSpacing(15); // Spazio tra i vari box
        setPadding(new Insets(15));
        setMinWidth(180);
        setMaxWidth(180);
        setPrefWidth(180);
        setAlignment(Pos.BOTTOM_RIGHT);
        setPickOnBounds(false);

        // --- BOX 1: NOTIFICHE INATTIVITA' ---
        VBox notifBox = new VBox(5);
        notifBox.setPadding(new Insets(10));
        notifBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 12; -fx-border-color: rgba(189, 195, 199, 0.8); -fx-border-radius: 12;");
        notifBox.setAlignment(Pos.TOP_CENTER);
        notifBox.setMaxHeight(120);
        notifBox.setPrefWidth(160);
        notifBox.setMaxWidth(160);

        Label lblNotifTitle = new Label("Edifici Inattivi");
        lblNotifTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #e74c3c;");
        
        lblNotifiche = new Label("Tutto ok");
        lblNotifiche.setStyle("-fx-font-size: 10px; -fx-text-fill: #2c3e50;");
        lblNotifiche.setWrapText(true);
        lblNotifiche.setMaxWidth(140);

        ScrollPane scrollNotif = new ScrollPane(lblNotifiche);
        scrollNotif.setFitToWidth(false);
        scrollNotif.setStyle("-fx-background-color: transparent; -fx-background: transparent;");
        scrollNotif.setPrefViewportHeight(80);
        scrollNotif.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollNotif.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);

        notifBox.getChildren().addAll(lblNotifTitle, scrollNotif);


        // --- BOX 2: STATS ACQUA/ENERGIA ---
        VBox statsBox = new VBox(8);
        statsBox.setPadding(new Insets(10));
        statsBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 12; -fx-border-color: rgba(189, 195, 199, 0.8); -fx-border-radius: 12;");
        statsBox.setAlignment(Pos.CENTER);
        statsBox.setPrefWidth(160);
        statsBox.setMaxWidth(160);

        // Acqua
        VBox waterBox = new VBox(2);
        waterBox.setAlignment(Pos.CENTER);
        Label lblWaterTitle = new Label("Acqua");
        lblWaterTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #2980b9;");
        pbAcqua = new ProgressBar(1.0);
        pbAcqua.setPrefWidth(90);
        pbAcqua.setStyle("-fx-accent: #3498db;");
        lblAcqua = new Label("0 / 0");
        lblAcqua.setStyle("-fx-font-size: 10px; -fx-text-fill: #2c3e50;");
        waterBox.getChildren().addAll(lblWaterTitle, pbAcqua, lblAcqua);

        // Energia
        VBox powerBox = new VBox(2);
        powerBox.setAlignment(Pos.CENTER);
        Label lblPowerTitle = new Label("Energia");
        lblPowerTitle.setStyle("-fx-font-weight: bold; -fx-font-size: 11px; -fx-text-fill: #f39c12;");
        pbEnergia = new ProgressBar(1.0);
        pbEnergia.setPrefWidth(90);
        pbEnergia.setStyle("-fx-accent: #f1c40f;");
        lblEnergia = new Label("0 / 0");
        lblEnergia.setStyle("-fx-font-size: 10px; -fx-text-fill: #2c3e50;");
        powerBox.getChildren().addAll(lblPowerTitle, pbEnergia, lblEnergia);

        statsBox.getChildren().addAll(waterBox, powerBox);


        // --- BOX 3: TIME CONTROLS ---
        VBox timeBox = new VBox(10);
        timeBox.setPadding(new Insets(10));
        timeBox.setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 12; -fx-border-color: rgba(189, 195, 199, 0.8); -fx-border-radius: 12;");
        timeBox.setAlignment(Pos.CENTER);
        timeBox.setPrefWidth(160);
        timeBox.setMaxWidth(160);

        Label lblTempo = new Label("TIME CONTROLS");
        lblTempo.setStyle("-fx-font-weight: bold; -fx-font-size: 12px; -fx-text-fill: #2c3e50;");

        Slider sliderVelocita = new Slider(1, 3, 1);
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

        Button btnAzioneTempo = new Button("SKIP ▶");
        btnAzioneTempo.setMaxWidth(Double.MAX_VALUE);
        btnAzioneTempo.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-color: #ecf0f1; -fx-cursor: hand;");

        simulazioneAuto = new Timeline(new KeyFrame(Duration.seconds(1), ev -> {
            int tickMultiplier = (int) sliderVelocita.getValue();
            for (int i = 0; i < tickMultiplier; i++) {
                motore.tick(); 
            }
            topBar.aggiornaDati(stato);
            mappa.rinfrescaMappaCompleta();
        }));
        simulazioneAuto.setCycleCount(Animation.INDEFINITE);

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

        timeBox.getChildren().addAll(lblTempo, sliderVelocita, btnAzioneTempo);


        // --- AGGIUNTA AL VBOX PRINCIPALE ---
        getChildren().addAll(notifBox, statsBox, timeBox);
    }

    private void fermaSimulazione(Button btn) {
        simulazioneAuto.pause();
        inEsecuzione = false;
        btn.setText("SKIP ▶");
        btn.setStyle("-fx-font-weight: bold; -fx-font-size: 13px; -fx-background-color: #ecf0f1; -fx-text-fill: black; -fx-cursor: hand;");
    }

    @Override
    public void onSimulationUpdated(StatoCitta stato) {
        // Aggiorna Acqua
        double wFornita = stato.getAcquaFornita();
        double wRichiesta = stato.getAcquaRichiesta();
        lblAcqua.setText("Prod: " + (int)wFornita + " | Rich: " + (int)wRichiesta);
        
        if (wRichiesta > 0) {
            double ratio = wFornita / wRichiesta;
            pbAcqua.setProgress(Math.min(ratio, 1.0));
            if (ratio < 1.0) {
                pbAcqua.setStyle("-fx-accent: #e74c3c;"); 
                lblAcqua.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else {
                pbAcqua.setStyle("-fx-accent: #3498db;"); 
                lblAcqua.setStyle("-fx-font-size: 10px; -fx-text-fill: #2c3e50;");
            }
        } else {
            pbAcqua.setProgress(1.0);
            pbAcqua.setStyle("-fx-accent: #3498db;");
            lblAcqua.setStyle("-fx-font-size: 10px; -fx-text-fill: #2c3e50;");
        }

        // Aggiorna Energia
        double eFornita = stato.getEnergiaFornita();
        double eRichiesta = stato.getEnergiaRichiesta();
        lblEnergia.setText("Prod: " + (int)eFornita + " | Rich: " + (int)eRichiesta);
        
        if (eRichiesta > 0) {
            double ratio = eFornita / eRichiesta;
            pbEnergia.setProgress(Math.min(ratio, 1.0));
            if (ratio < 1.0) {
                pbEnergia.setStyle("-fx-accent: #e74c3c;"); 
                lblEnergia.setStyle("-fx-font-size: 10px; -fx-text-fill: #e74c3c; -fx-font-weight: bold;");
            } else {
                pbEnergia.setStyle("-fx-accent: #f1c40f;"); 
                lblEnergia.setStyle("-fx-font-size: 10px; -fx-text-fill: #2c3e50;");
            }
        } else {
            pbEnergia.setProgress(1.0);
            pbEnergia.setStyle("-fx-accent: #f1c40f;");
            lblEnergia.setStyle("-fx-font-size: 10px; -fx-text-fill: #2c3e50;");
        }

        // Aggiorna Notifiche
        Set<String> problemi = new HashSet<>();
        for (UrbanEntity e : motore.getActiveEntities()) {
            String motivo = motore.getMotivoInattivita(e);
            if (!motivo.equals("Attivo")) {
                problemi.add(e.getClass().getSimpleName() + ": " + motivo);
            }
        }

        if (problemi.isEmpty()) {
            lblNotifiche.setText("Tutto ok");
            lblNotifiche.setStyle("-fx-font-size: 10px; -fx-text-fill: #27ae60;"); // Verde
        } else {
            StringBuilder sb = new StringBuilder();
            for (String p : problemi) {
                sb.append("- ").append(p).append("\n");
            }
            lblNotifiche.setText(sb.toString().trim());
            lblNotifiche.setStyle("-fx-font-size: 10px; -fx-text-fill: #c0392b; -fx-font-weight: bold;"); // Rosso
        }
    }
}