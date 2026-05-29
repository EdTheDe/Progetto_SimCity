package citylogic.ui;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class GestoreEventiUI {

    public static void mostraEvento(String eventName, String description, TimeBar timeBarRef, javafx.stage.Window owner) {
        if (timeBarRef != null) {
            timeBarRef.fermaEImpostaManuale();
        }

        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        VBox popupContent = new VBox(15);
        popupContent.setStyle("-fx-background-color: rgba(44, 62, 80, 0.95); -fx-padding: 30; -fx-background-radius: 20; -fx-border-color: #f39c12; -fx-border-width: 4; -fx-border-radius: 20; -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.8), 20, 0, 0, 0);");
        popupContent.setAlignment(Pos.CENTER);
        
        Label lblTitle = new Label("!!! EVENTO: " + eventName.toUpperCase() + " !!!");
        lblTitle.setStyle("-fx-text-fill: #f39c12; -fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label lblDesc = new Label(description);
        lblDesc.setStyle("-fx-text-fill: white; -fx-font-size: 16px; -fx-wrap-text: true; -fx-text-alignment: center;");
        lblDesc.setMaxWidth(400);
        
        Button btnClose = new Button("RICEVUTO");
        btnClose.setStyle("-fx-background-color: #f39c12; -fx-text-fill: #2c3e50; -fx-font-weight: bold; -fx-font-size: 16px; -fx-padding: 10 30; -fx-cursor: hand; -fx-background-radius: 10;");
        btnClose.setOnAction(e -> popupStage.close());
        
        popupContent.getChildren().addAll(lblTitle, lblDesc, btnClose);
        
        Scene scena = new Scene(popupContent);
        scena.setFill(javafx.scene.paint.Color.TRANSPARENT);
        popupStage.setScene(scena);
        
        if (owner != null) {
            popupStage.initOwner(owner);
        }
        
        popupStage.show();
    }

    public static void mostraGameOver() {
        // Usiamo Platform.runLater per eseguire il dialogo fuori dal ciclo di animazione
        javafx.application.Platform.runLater(() -> {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
            alert.setTitle("GAME OVER");
            alert.setHeaderText("Bancarotta!");
            alert.setContentText("I fondi della tua città sono stati in rosso troppo a lungo. Il sindaco è stato rimosso dall'incarico.");
            
            // Per uscire in sicurezza dopo la chiusura dell'alert
            alert.showAndWait();
            System.exit(0);
        });
    }
}
