package citylogic.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class TutorialPopup {

    public static void mostraTutorial() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Benvenuto Sindaco! - Tutorial");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(20));
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1;");

        Label lblTitolo = new Label("SimCity: Guida Iniziale");
        lblTitolo.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Label lblTesto = new Label(
            "Benvenuto Sindaco. Il tuo obiettivo è espandere la città gestendo Popolazione, Denaro e Felicità.\n\n" +
            "Strade: Sono l'infrastruttura di base. Ogni zona o edificio deve essere obbligatoriamente collegato a una strada per funzionare.\n\n" +
            "Zone Residenziali: Aumentano la Popolazione fornendo abitazioni ai cittadini.\n" +
            "Zone Commerciali: Creano posti di lavoro e producono Denaro tramite le tasse.\n" +
            "Zone Industriali: Generano posti di lavoro aumentando la felicità, ma producono inquinamento.\n\n" +
            "Servizi Essenziali (Acqua ed Elettricità): Nessun edificio può operare senza risorse. Le Centrali Elettriche e gli Impianti Idrici forniscono rispettivamente corrente e acqua a tutta la città. Se l'approvvigionamento non copre la domanda, gli edifici non collegati o senza risorse smetteranno di funzionare.\n\n" +
            "Servizi Pubblici:\n\n" +
            "Stazioni di Polizia e Caserme dei Pompieri: Garantiscono la sicurezza e proteggono le zone dai disastri.\n\n" +
            "Ospedali e Scuole: Forniscono salute e istruzione, supportando lo sviluppo della città.\n\n" +
            "Nota sui Servizi Pubblici: Ogni edificio, per funzionare correttamente, ha bisogno di essere coperto da almeno un Ospedale, una Centrale di Polizia e una Caserma dei Pompieri nel raggio di 7 caselle (il raggio d'azione aumenta migliorando di livello i servizi).\n\n" +
            "Aree Verdi: Mitigano l'impatto dell'inquinamento e migliorano le condizioni di vita.\n\n" +
            "Costi di Mantenimento: Tutti gli edifici, eccezion fatta per le strade, richiedono costi di mantenimento costanti che vengono sottratti dal tuo Denaro ad ogni tick di simulazione.\n\n" +
            "Politiche Cittadine: Dal menu in alto puoi selezionare tre diverse Politiche:\n" +
            " - Politica Neutrale: Nessun bonus o malus applicato, la città segue il suo andamento normale.\n" +
            " - Politica Ambientale: Riduce l'inquinamento (aumentando l'Ecologia) in base al numero di industrie attive, ma comporta un costo monetario aggiuntivo e riduce leggermente i posti di lavoro.\n" +
            " - Politica Industriale: Genera ingenti incassi economici e aumenta l'occupazione per ogni industria attiva, ma penalizza pesantemente l'Ecologia urbana.\n\n" +
            "Felicità: Indica la qualità della vita complessiva. Questo valore sale se i cittadini hanno lavoro, copertura dei servizi (come sanità e istruzione) e aree verdi. Scende drasticamente a causa dell'inquinamento, della mancanza di acqua o elettricità. Attenzione! Bassi livelli di Felicità garantiscono introiti minori, quindi stai attento ai bisogni dei tuoi cittadini\n\n" +
            "Migliorare e Demolire: Puoi potenziare o distruggere gli edifici esistenti. Assicurati prima di aver deselezionato lo shop, poi fai clic con il tasto sinistro sull'edificio desiderato per aprire il menu delle azioni.\n\n" +
            "Sconfitta: La partita termina con il game over se il tuo Denaro scende sotto lo zero per piu di 5 tick."
        );
        lblTesto.setWrapText(true);
        lblTesto.setStyle("-fx-font-size: 14px; -fx-text-fill: #34495e; -fx-line-spacing: 0.5em;");

        ScrollPane scroll = new ScrollPane(lblTesto);
        scroll.setFitToWidth(true);
        scroll.setStyle("-fx-background-color: transparent; -fx-background: transparent; -fx-border-color: #bdc3c7; -fx-border-radius: 8px; -fx-padding: 10px;");
        scroll.setPrefHeight(300);

        Button btnChiudi = new Button("Inizia a Giocare");
        btnChiudi.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: #27ae60; -fx-text-fill: white; -fx-padding: 10px 20px; -fx-background-radius: 8px; -fx-cursor: hand;");
        btnChiudi.setOnAction(e -> popup.close());

        layout.getChildren().addAll(lblTitolo, scroll, btnChiudi);

        Scene scena = new Scene(layout, 550, 450);
        popup.setScene(scena);
        popup.setResizable(false);
        popup.show();
    }
}
