package citylogic.ui;

import citylogic.domain.state.StatoCitta;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;

public class TopBar extends HBox {

    private Label lblFinanze, lblPopolazione;
    private ProgressBar pbSicurezza, pbSanita, pbEcologia, pbFelicita;
    private ChoiceBox<String> selettoreModalita;

    public TopBar() {
        setSpacing(20);
        // Margine sinistro impostato a 20 per allineamento perfetto con SideBar
        setPadding(new Insets(15, 20, 15, 30)); 
        setStyle("-fx-background-color: transparent;");
        setAlignment(Pos.CENTER_LEFT);

        // --- BOTTONE IMPOSTAZIONI ---
        Button btnImpostazioni = new Button();
        try {
            ImageView iconaImpostazioni = new ImageView(new Image(getClass().getResourceAsStream("/immagini/impostazioni.png")));
            iconaImpostazioni.setFitWidth(24);
            iconaImpostazioni.setFitHeight(24);
            btnImpostazioni.setGraphic(iconaImpostazioni);
        } catch (Exception e) {
            btnImpostazioni.setText("⚙️");
        }
        // Forza altezza a 48px
        btnImpostazioni.setMinHeight(48);
        btnImpostazioni.setMaxHeight(48);
        btnImpostazioni.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 15; -fx-border-color: #bdc3c7; -fx-border-radius: 15; -fx-cursor: hand; -fx-padding: 0 15px;");
        btnImpostazioni.setOnAction(e -> apriMenuImpostazioni());

        lblFinanze = new Label();
        lblPopolazione = new Label();
        lblPopolazione.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        // --- BLOCCO SINISTRA (FINANZE) ---
        HBox contenitoreSinistra = new HBox(20, lblFinanze, lblPopolazione);
        // Forza altezza a 48px
        contenitoreSinistra.setMinHeight(48);
        contenitoreSinistra.setMaxHeight(48);
        contenitoreSinistra.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-padding: 0 20; -fx-background-radius: 15; -fx-border-color: #bdc3c7; -fx-border-radius: 15;");
        contenitoreSinistra.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // --- SELETTORE MODALITÀ ---
        selettoreModalita = new ChoiceBox<>();
        selettoreModalita.getItems().addAll("🟢 Sandbox", "🟡 Standard", "🔴 Sfida");
        selettoreModalita.setValue("🟡 Standard"); 
        selettoreModalita.setStyle("-fx-font-weight: bold; -fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #bdc3c7; -fx-border-radius: 12; -fx-padding: 2px 10px;");
        
        pbSicurezza = new ProgressBar(0);
        pbSanita = new ProgressBar(0);
        pbEcologia = new ProgressBar(0);
        pbFelicita = new ProgressBar(0);

        // Blocco di destra
        HBox contenitoreDestra = new HBox(15, 
            selettoreModalita,
            creaBarra("🛡️ Sicurezza", pbSicurezza, "-fx-accent: #3498db;"),
            creaBarra("🏥 Sanità", pbSanita, "-fx-accent: #e74c3c;"),
            creaBarra("🌱 Ecologia", pbEcologia, "-fx-accent: #2ecc71;"),
            creaBarra("😊 Felicità", pbFelicita, "-fx-accent: #f1c40f;")
        );
        contenitoreDestra.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-padding: 10 20; -fx-background-radius: 15; -fx-border-color: #bdc3c7; -fx-border-radius: 15;");
        contenitoreDestra.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(btnImpostazioni, contenitoreSinistra, spacer, contenitoreDestra);
    }

    private VBox creaBarra(String nome, ProgressBar bar, String colore) {
        Label lbl = new Label(nome);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        bar.setStyle(colore);
        bar.setPrefWidth(100);
        VBox box = new VBox(5, lbl, bar);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    private void apriMenuImpostazioni() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Impostazioni");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1;");

        Button btnImporta = creaBottoneMenu("📥 Importa Partita", "#3498db");
        btnImporta.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Importa Salvataggio JSON");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON", "*.json"));
            File file = fileChooser.showOpenDialog(popup);
            if (file != null) System.out.println("Import: " + file.getAbsolutePath());
        });

        Button btnEsporta = creaBottoneMenu("📤 Esporta Partita", "#9b59b6");
        btnEsporta.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Salva Partita");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON", "*.json"));
            File file = fileChooser.showSaveDialog(popup);
            if (file != null) System.out.println("Export: " + file.getAbsolutePath());
        });

        Button btnRicomincia = creaBottoneMenu("🔄 Ricomincia", "#f39c12");
        btnRicomincia.setOnAction(e -> { popup.close(); });

        Button btnEsci = creaBottoneMenu("❌ Esci", "#e74c3c");
        btnEsci.setOnAction(e -> { System.exit(0); });

        layout.getChildren().addAll(btnImporta, btnEsporta, btnRicomincia, btnEsci);

        Scene scena = new Scene(layout, 320, 350);
        popup.setScene(scena);
        popup.setResizable(false);
        popup.showAndWait();
    }

    private Button creaBottoneMenu(String testo, String colore) {
        Button btn = new Button(testo);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: white; -fx-border-color: " + colore + "; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 12px; -fx-cursor: hand;");
        return btn;
    }

    public void aggiornaDati(StatoCitta stato) {
        if (stato.getFinanze() < 0) {
            lblFinanze.setText(String.format("💰 -$%.2f", Math.abs(stato.getFinanze())));
            lblFinanze.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;"); 
        } else {
            lblFinanze.setText(String.format("💰 $%.2f", stato.getFinanze()));
            lblFinanze.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;"); 
        }
        lblPopolazione.setText("👥 " + stato.getPopolazione());
        pbSicurezza.setProgress(stato.getSicurezza() / 100.0);
        pbSanita.setProgress(stato.getSanita() / 100.0);
        pbEcologia.setProgress(stato.getEcologia() / 100.0);
        pbFelicita.setProgress(stato.getFelicita() / 100.0);
    }
}