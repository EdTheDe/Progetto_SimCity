package citylogic.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class SideBar extends VBox {

    private Button pulsanteAttivo = null;
    private String stilePrecedente = "";

    public SideBar(MappaGriglia mappa) {
        setSpacing(8);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 12; -fx-border-color: #bdc3c7; -fx-border-radius: 12;");
        setPrefWidth(180); // Rimessa a 180 come richiesto
        setPickOnBounds(false); 

        Label lblTitolo = new Label("COSTRUZIONE");
        lblTitolo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        getChildren().add(lblTitolo);

        // Parametri: mappa, etichetta, costo visivo, tipo logico, colore bordo
        addButtonEdificio(mappa, "🏠 Residenziale", 100, "residential", "#3498db");
        addButtonEdificio(mappa, "🏭 Industriale", 150, "industrial", "#9b59b6");
        addButtonEdificio(mappa, "🏬 Commerciale", 200, "commercial", "#f1c40f");
        addButtonEdificio(mappa, "⚡ Elettricità", 500, "powerplant", "#34495e");
        addButtonEdificio(mappa, "💧 Idrico", 400, "waterplant", "#2980b9");
        addButtonEdificio(mappa, "🚓 Polizia", 300, "police", "#3498db");
        addButtonEdificio(mappa, "🏥 Scuola", 250, "school", "#2ecc71");
        addButtonEdificio(mappa, "🚒 Pompieri", 350, "firestation", "#e74c3c");
        addButtonEdificio(mappa, "🏥 Ospedale", 600, "hospital", "#e74c3c");
        addButtonEdificio(mappa, "🌳 Area Verde", 50, "greenarea", "#2ecc71");
        addButtonEdificio(mappa, "🚦 Strada", 10, "road", "#7f8c8d");
    }

    private void addButtonEdificio(MappaGriglia mappa, String etichetta, int costo, String tipo, String colore) {
        Button btn = new Button();
        btn.setMaxWidth(Double.MAX_VALUE);
        
        // Costruzione del layout interno al pulsante
        Label lblNome = new Label(etichetta);
        Label lblCosto = new Label("$" + costo);
        lblCosto.setStyle("-fx-text-fill: #27ae60; -fx-font-weight: bold;"); // Colore verde per indicare il denaro

        Region distanziatore = new Region();
        HBox.setHgrow(distanziatore, Priority.ALWAYS); // Spinge il costo a destra

        HBox contenuto = new HBox(lblNome, distanziatore, lblCosto);
        contenuto.setAlignment(Pos.CENTER_LEFT);
        btn.setGraphic(contenuto);
        
        String stileBase = "-fx-background-color: white; -fx-border-color: " + colore + "; -fx-border-width: 0 0 0 5px; -fx-cursor: hand; -fx-padding: 8px 4px;";
        String stileAttivato = "-fx-background-color: #e8f4f8; -fx-border-color: " + colore + "; -fx-border-width: 2 2 2 5px; -fx-cursor: hand; -fx-padding: 6px 2px;";

        btn.setStyle(stileBase);

        btn.setOnAction(e -> {
            if (pulsanteAttivo == btn) {
                // TOGGLE OFF
                btn.setStyle(stileBase);
                pulsanteAttivo = null;
                mappa.setTipoEdificioSelezionato(null); 
            } else {
                // TOGGLE ON
                if (pulsanteAttivo != null) {
                    pulsanteAttivo.setStyle(stilePrecedente); 
                }
                pulsanteAttivo = btn;
                stilePrecedente = stileBase;
                btn.setStyle(stileAttivato);
                mappa.setTipoEdificioSelezionato(tipo); 
            }
        });
        getChildren().add(btn);
    }
}
