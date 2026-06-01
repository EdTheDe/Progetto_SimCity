package citylogic.ui;

import citylogic.domain.state.StatoCitta;
import citylogic.domain.map.UrbanGrid;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

import citylogic.core.engine.SimulationEngine;
import citylogic.core.strategy.PoliticaNeutrale;
import citylogic.core.strategy.PoliticaAmbientale;
import citylogic.core.strategy.PoliticaIndustriale;

/**
 * Barra superiore preposta alla visualizzazione delle statistiche macroeconomiche 
 * (Finanze e Popolazione) e all'intercettazione degli eventi critici per far comparire gli alert a schermo.
 */
public class TopBar extends HBox implements citylogic.core.engine.CityObserver {

    private Label lblFinanze, lblPopolazione, lblTickets;
    private ProgressBar pbSicurezza, pbSanita, pbEcologia, pbFelicita, pbLavoro;
    private ChoiceBox<String> selettorePolitica;
    private SimulationEngine engine;
    
    private UrbanGrid logica;
    private StatoCitta stato;
    private MappaGriglia mappaVisiva;
    private TimeBar timeBarRef;

    /**
     * Ritorna il motore di simulazione.
     *
     * @return SimulatinEngine logico corrente agganciato alla barra.
     */
    public SimulationEngine getSimulationEngine() {
        return engine;
    }

    /**
     * Instanzia i gruppi HBox posizionandoli ai lati opposti per gestire l'Header del programma.
     */
    public TopBar() {
        setSpacing(20);
        setPadding(new Insets(15, 20, 15, 30)); 
        setStyle("-fx-background-color: transparent;");
        setAlignment(Pos.CENTER_LEFT);
        setPickOnBounds(false);

        Button btnImpostazioni = new Button();
        try {
            ImageView iconaImpostazioni = new ImageView(new Image(getClass().getResourceAsStream("/immagini/impostazioni.png")));
            iconaImpostazioni.setFitWidth(24);
            iconaImpostazioni.setFitHeight(24);
            btnImpostazioni.setGraphic(iconaImpostazioni);
        } catch (Exception e) {
            btnImpostazioni.setText("⚙️");
        }
        btnImpostazioni.setMinHeight(48);
        btnImpostazioni.setMaxHeight(48);
        btnImpostazioni.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-background-radius: 15; -fx-border-color: #bdc3c7; -fx-border-radius: 15; -fx-cursor: hand; -fx-padding: 0 15px;");
        
        btnImpostazioni.setOnAction(e -> {
            MenuImpostazioni menu = new MenuImpostazioni(logica, stato, mappaVisiva, engine, this, timeBarRef);
            menu.mostra();
        });

        lblFinanze = new Label();
        lblPopolazione = new Label();
        lblPopolazione.setStyle("-fx-font-size: 16px; -fx-font-weight: bold;");

        lblTickets = new Label();
        lblTickets.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: black;");

        HBox contenitoreSinistra = new HBox(20, lblFinanze, lblPopolazione, lblTickets);
        contenitoreSinistra.setMinHeight(48);
        contenitoreSinistra.setMaxHeight(48);
        contenitoreSinistra.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-padding: 0 20; -fx-background-radius: 15; -fx-border-color: #bdc3c7; -fx-border-radius: 15;");
        contenitoreSinistra.setAlignment(Pos.CENTER_LEFT);

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        selettorePolitica = new ChoiceBox<>();
        selettorePolitica.getItems().addAll("⚪ Nessuna Politica", "🟢 Tassa Ambientale", "🏭 Sviluppo Industriale");
        selettorePolitica.setValue("⚪ Nessuna Politica"); 
        selettorePolitica.setStyle("-fx-font-weight: bold; -fx-background-color: white; -fx-background-radius: 12; -fx-border-color: #bdc3c7; -fx-border-radius: 12; -fx-padding: 2px 10px;");
		// Pattern Strategy che cambia immediatamente la classe strategica del motore alle variazioni sul widget
        selettorePolitica.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (engine == null) return;
            switch (newValue) {
                case "🟢 Tassa Ambientale": engine.setPoliticaAttiva(new PoliticaAmbientale()); break;
                case "🏭 Sviluppo Industriale": engine.setPoliticaAttiva(new PoliticaIndustriale()); break;
                default: engine.setPoliticaAttiva(new PoliticaNeutrale()); break;
            }
        });

        pbSicurezza = new ProgressBar(0);
        pbSanita = new ProgressBar(0);
        pbEcologia = new ProgressBar(0);
        pbFelicita = new ProgressBar(0);
        pbLavoro = new ProgressBar(0);

        HBox contenitoreDestra = new HBox(15, 
            selettorePolitica,
            creaBarra("🚨 Sicurezza", pbSicurezza, "-fx-accent: #3498db;"),
            creaBarra("🏥 Sanità", pbSanita, "-fx-accent: #e74c3c;"),
            creaBarra("🌱 Ecologia", pbEcologia, "-fx-accent: #2ecc71;"),
            creaBarra("😊 Felicità", pbFelicita, "-fx-accent: #f1c40f;"),
            creaBarra("💼 Lavoro", pbLavoro, "-fx-accent: #9b59b6;")
        );
        contenitoreDestra.setStyle("-fx-background-color: rgba(255, 255, 255, 0.9); -fx-padding: 10 20; -fx-background-radius: 15; -fx-border-color: #bdc3c7; -fx-border-radius: 15;");
        contenitoreDestra.setAlignment(Pos.CENTER_LEFT);

        getChildren().addAll(btnImpostazioni, contenitoreSinistra, spacer, contenitoreDestra);
    }

    /**
     * Imposta il motore di gioco attivo.
     *
     * @param engine Reference al cuore della simulazione.
     */
    public void setSimulationEngine(SimulationEngine engine) { this.engine = engine; }
    
    /**
     * Collega logica e interfaccia permettendo al blocco top di mettere in pausa la TimeBar in caso di reset o caricamento.
     *
     * @param timeBar Reference al modulo che governa lo slider tempo.
     */
    public void setTimeBar(TimeBar timeBar) { this.timeBarRef = timeBar; }
    
    /**
     * Raccoglie gli oggetti indispensabili per i reset operati dal menu ingranaggio.
     *
     * @param logica      Modello dei dati a griglia.
     * @param stato       Dati statistici in memoria.
     * @param mappaVisiva Quadro di gioco frontale.
     */
    public void setRiferimenti(UrbanGrid logica, StatoCitta stato, MappaGriglia mappaVisiva) {
        this.logica = logica;
        this.stato = stato;
        this.mappaVisiva = mappaVisiva;
    }

    /**
     * Assembla ed incapsula un blocco testo+barra in verticale.
     *
     * @param nome   L'etichetta associata al concetto (es. 'Lavoro', 'Sanità').
     * @param bar    L'oggetto ProgressBar da colorare e restituire appeso.
     * @param colore Direttiva CSS dedicata a quel progress bar.
     * @return       Il VBox finale già formattato.
     */
    private VBox creaBarra(String nome, ProgressBar bar, String colore) {
        Label lbl = new Label(nome);
        lbl.setStyle("-fx-font-size: 12px; -fx-font-weight: bold;");
        bar.setStyle(colore);
        bar.setPrefWidth(100);
        VBox box = new VBox(5, lbl, bar);
        box.setAlignment(Pos.CENTER);
        return box;
    }

    /**
     * Funzione dedicata a cambiare il solo testo dell'header al variare dei calcoli economici.
     * Gestisce dinamicamente anche il colore rosso se i fondi vanno in negativo.
     *
     * @param stato Il modello StatoCitta da leggere.
     */
    public void aggiornaDati(StatoCitta stato) {
        if (stato.getFinanze() < 0) {
            lblFinanze.setText(String.format("💰 -$%.2f", Math.abs(stato.getFinanze())));
            lblFinanze.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;"); 
        } else {
            lblFinanze.setText(String.format("💰 $%.2f", stato.getFinanze()));
            lblFinanze.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-text-fill: #27ae60;"); 
        }
        lblPopolazione.setText("👥 " + stato.getPopolazione());
        
        lblTickets.setText("🎫 " + stato.getTickets());
        
        pbSicurezza.setProgress(stato.getSicurezza() / 100.0);
        pbSanita.setProgress(stato.getSanita() / 100.0);
        pbEcologia.setProgress(stato.getEcologia() / 100.0);
        pbFelicita.setProgress(stato.getFelicita() / 100.0);
        pbLavoro.setProgress(stato.getLavoro() / 100.0);
    }
    
    /**
     * Riporta silenziosamente a default il menu grafico delle politiche cittadine.
     * Attivato principalmente dopo i ripristini e importazioni dei salvataggi.
     */
    public void resetPolitica() {
        if (selettorePolitica != null) {
            selettorePolitica.setValue("⚪ Nessuna Politica");
        }
    }

    /**
     * Intercetta la notifica broadcast ad Observer e aggiorna la striscia testi superiori.
     *
     * @param stato Lo stato contenente i nuovi valori ricalcolati dal tick.
     */
    @Override
    public void onSimulationUpdated(StatoCitta stato) {
        aggiornaDati(stato);
    }

    /**
     * Riceve un avviso di un evento imprevisto dal cuore logico della città
     * per visualizzarne titolo e contenuto grafico al centro dello schermo.
     *
     * @param eventName   L'intestazione superiore del pannello avviso.
     * @param description Il corpo testuale da recapitare all'utente.
     */
    @Override
    public void onEventStarted(String eventName, String description) {
        javafx.stage.Window window = (this.getScene() != null) ? this.getScene().getWindow() : null;
        GestoreEventiUI.mostraEvento(eventName, description, timeBarRef, window);
    }

    /**
     * Segnala alla UI che le condizioni di fallimento per la partita si sono realizzate.
     */
    @Override
    public void onGameOver() {
        GestoreEventiUI.mostraGameOver();
    }
}
