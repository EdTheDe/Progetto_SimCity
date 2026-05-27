package citylogic.ui;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public class SideBar extends VBox {

    private Button pulsanteAttivo = null;
    private String stilePrecedente = "";

    public SideBar(MappaGriglia mappa) {
        setSpacing(8);
        setPadding(new Insets(15));
        setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 12; -fx-border-color: #bdc3c7; -fx-border-radius: 12;");
        setPrefWidth(160);
        setPickOnBounds(false); 

        Label lblTitolo = new Label("COSTRUZIONE");
        lblTitolo.setStyle("-fx-font-weight: bold; -fx-font-size: 14px; -fx-text-fill: #2c3e50;");
        getChildren().add(lblTitolo);

        addButtonEdificio(mappa, "🏠 Residenziale", "residential", "#3498db");
        addButtonEdificio(mappa, "🏭 Industriale", "industrial", "#9b59b6");
        addButtonEdificio(mappa, "🏬 Commerciale", "commercial", "#f1c40f");
        addButtonEdificio(mappa, "⚡ Elettricità", "powerplant", "#34495e");
        addButtonEdificio(mappa, "💧 Idrico", "waterplant", "#2980b9");
        addButtonEdificio(mappa, "🛡️ Polizia", "police", "#3498db");
        addButtonEdificio(mappa, "🏥 Scuola", "school", "#2ecc71");
        addButtonEdificio(mappa, "🚒 Pompieri", "firestation", "#e74c3c");
        addButtonEdificio(mappa, "🏥 Ospedale", "hospital", "#e74c3c");
        addButtonEdificio(mappa, "🛣️ Strada", "road", "#7f8c8d");
    }

    private void addButtonEdificio(MappaGriglia mappa, String etichetta, String tipo, String colore) {
        Button btn = new Button(etichetta);
        btn.setMaxWidth(Double.MAX_VALUE);
        
        // Stile di base (linea colorata solo a sinistra)
        String stileBase = "-fx-background-color: white; -fx-border-color: " + colore + "; -fx-border-width: 0 0 0 5px; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 10px; -fx-font-weight: bold;";
        
        // Stile attivato (riquadro completo e sfondo azzurrino)
        String stileAttivato = "-fx-background-color: #e8f4f8; -fx-border-color: " + colore + "; -fx-border-width: 2 2 2 5px; -fx-cursor: hand; -fx-alignment: center-left; -fx-padding: 8px; -fx-font-weight: bold;";

        btn.setStyle(stileBase);

        btn.setOnAction(e -> {
            if (pulsanteAttivo == btn) {
                // TOGGLE OFF: Cliccato per la seconda volta, si spegne
                btn.setStyle(stileBase);
                pulsanteAttivo = null;
                mappa.setTipoEdificioSelezionato(null); // Comunica alla mappa di spegnere la griglia
            } else {
                // TOGGLE ON: Si accende questo pulsante e si spegne l'eventuale precedente
                if (pulsanteAttivo != null) {
                    pulsanteAttivo.setStyle(stilePrecedente); 
                }
                pulsanteAttivo = btn;
                stilePrecedente = stileBase;
                btn.setStyle(stileAttivato);
                mappa.setTipoEdificioSelezionato(tipo); // Passa solo la stringa logica (es. "road")
            }
        });
        getChildren().add(btn);
    }
}