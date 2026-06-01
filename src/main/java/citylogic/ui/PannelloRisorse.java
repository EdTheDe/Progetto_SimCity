package citylogic.ui;

import citylogic.domain.state.StatoCitta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.VBox;

/**
 * Pannello preposto al monitoraggio visivo dei valori critici di input/output
 * (come la fornitura idrica e la rete elettrica).
 */
public class PannelloRisorse extends VBox {

    private final ProgressBar pbAcqua;
    private final Label lblAcqua;
    private final ProgressBar pbEnergia;
    private final Label lblEnergia;

    /**
     * Assembla la struttura con barra e testo per le sezioni energetiche e idriche della città.
     */
    public PannelloRisorse() {
        setSpacing(8);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 12; -fx-border-color: rgba(189, 195, 199, 0.8); -fx-border-radius: 12;");
        setAlignment(Pos.CENTER);
        setPrefWidth(160);
        setMaxWidth(160);

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

        getChildren().addAll(waterBox, powerBox);
    }

    /**
     * Esegue il ricalcolo percentuale tra l'unità domandata dalla rete e la produzione generata,
     * tingendo le barre di rosso se il deficit minaccia il funzionamento corretto degli edifici.
     *
     * @param stato Contenitore dei dati statici aggiornati.
     */
    public void aggiornaRisorse(StatoCitta stato) {
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
    }
}
