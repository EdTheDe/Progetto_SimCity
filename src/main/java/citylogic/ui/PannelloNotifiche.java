package citylogic.ui;

import citylogic.core.engine.SimulationEngine;
import citylogic.domain.entities.UrbanEntity;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import java.util.HashSet;
import java.util.Set;

public class PannelloNotifiche extends VBox {

    private final Label lblNotifiche;

    public PannelloNotifiche() {
        setSpacing(5);
        setPadding(new Insets(10));
        setStyle("-fx-background-color: rgba(255, 255, 255, 0.85); -fx-background-radius: 12; -fx-border-color: rgba(189, 195, 199, 0.8); -fx-border-radius: 12;");
        setAlignment(Pos.TOP_CENTER);
        setMaxHeight(120);
        setPrefWidth(160);
        setMaxWidth(160);

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

        getChildren().addAll(lblNotifTitle, scrollNotif);
    }

    public void aggiornaNotifiche(SimulationEngine motore) {
        Set<String> problemi = new HashSet<>();
        for (UrbanEntity e : motore.getActiveEntities()) {
            String motivo = motore.getMotivoInattivita(e);
            if (!motivo.equals("Attivo")) {
                problemi.add(e.getClass().getSimpleName() + ": " + motivo);
            }
        }

        if (problemi.isEmpty()) {
            lblNotifiche.setText("Tutto ok");
            lblNotifiche.setStyle("-fx-font-size: 10px; -fx-text-fill: #27ae60;"); 
        } else {
            StringBuilder sb = new StringBuilder();
            for (String p : problemi) {
                sb.append("- ").append(p).append("\n");
            }
            lblNotifiche.setText(sb.toString().trim());
            lblNotifiche.setStyle("-fx-font-size: 10px; -fx-text-fill: #c0392b; -fx-font-weight: bold;");
        }
    }
}
