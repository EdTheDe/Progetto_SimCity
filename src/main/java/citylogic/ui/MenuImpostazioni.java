package citylogic.ui;

import citylogic.core.engine.SimulationEngine;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import citylogic.infrastructure.PersistenceManager;
import citylogic.infrastructure.SaveGameData;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.File;

/**
 * Controller per il menu delle impostazioni che gestisce il salvataggio,
 * il caricamento, il reset e l'uscita dalla sessione di gioco.
 */
public class MenuImpostazioni {

    private final UrbanGrid logica;
    private final StatoCitta stato;
    private final MappaGriglia mappaVisiva;
    private final SimulationEngine engine;
    private final TopBar topBar;
    private final TimeBar timeBarRef;

    /**
     * Costruttore che inizializza le dipendenze necessarie per operare sui dati di gioco.
     *
     * @param logica      Il modello della griglia urbana.
     * @param stato       Lo stato dei fondi e della città.
     * @param mappaVisiva Il componente di renderizzazione grafica.
     * @param engine      Il motore della simulazione.
     * @param topBar      La barra superiore per l'aggiornamento dei widget.
     * @param timeBarRef  Il gestore del tempo per indurre le pause in fase di configurazione.
     */
    public MenuImpostazioni(UrbanGrid logica, StatoCitta stato, MappaGriglia mappaVisiva, SimulationEngine engine, TopBar topBar, TimeBar timeBarRef) {
        this.logica = logica;
        this.stato = stato;
        this.mappaVisiva = mappaVisiva;
        this.engine = engine;
        this.topBar = topBar;
        this.timeBarRef = timeBarRef;
    }

    /**
     * Instanzia l'interfaccia grafica del menu delle impostazioni in un pop-up modale.
     */
    public void mostra() {
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Impostazioni");

        VBox layout = new VBox(15);
        layout.setPadding(new Insets(25));
        layout.setAlignment(Pos.CENTER);
        layout.setStyle("-fx-background-color: #ecf0f1;");

        Button btnImporta = creaBottone("📥 Importa Partita", "#3498db");
        btnImporta.setOnAction(e -> eseguiImportazione(popup));

        Button btnEsporta = creaBottone("📤 Esporta Partita", "#9b59b6");
        btnEsporta.setOnAction(e -> eseguiEsportazione(popup));

        Button btnRicomincia = creaBottone("🔄 Ricomincia", "#f39c12");
        btnRicomincia.setOnAction(e -> eseguiReset(popup));

        Button btnEsci = creaBottone("X Esci", "#e74c3c");
        btnEsci.setOnAction(e -> System.exit(0));

        layout.getChildren().addAll(btnImporta, btnEsporta, btnRicomincia, btnEsci);

        Scene scena = new Scene(layout, 320, 350);
        popup.setScene(scena);
        popup.setResizable(false);
        popup.showAndWait();
    }

    /**
     * Costruisce un bottone generico per il menu applicando stili costanti.
     *
     * @param testo  Il testo da mostrare sul bottone.
     * @param colore Codice colore esadecimale per i bordi.
     * @return       L'oggetto Button configurato.
     */
    private Button creaBottone(String testo, String colore) {
        Button btn = new Button(testo);
        btn.setMaxWidth(Double.MAX_VALUE);
        btn.setStyle("-fx-font-size: 16px; -fx-font-weight: bold; -fx-background-color: white; -fx-border-color: " + colore + "; -fx-border-width: 2px; -fx-border-radius: 8px; -fx-background-radius: 8px; -fx-padding: 12px; -fx-cursor: hand;");
        return btn;
    }

    /**
     * Gestisce la procedura per caricare i dati di una partita salvata dal disco (file JSON).
     *
     * @param popup Il riferimento alla finestra modale corrente per poterla chiudere a fine azione.
     */
    private void eseguiImportazione(Stage popup) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Importa Salvataggio JSON");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON", "*.json"));
        File file = fileChooser.showOpenDialog(popup);
        if (file != null && logica != null && stato != null) {
            try {
				// Inietta il path assoluto del FileChooser per far scrivere json nella directory corretta
                PersistenceManager pm = new PersistenceManager(file.getParent());
                SaveGameData datiCaricati = pm.caricaPartita(file.getName().replace(".json", ""));
                pm.ripristinaDati(datiCaricati, stato, logica);
                mappaVisiva.rinfrescaMappaCompleta();
                topBar.aggiornaDati(stato);
                popup.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Raccoglie i dati dello stato attuale e li salva in un file JSON.
     *
     * @param popup Il riferimento alla finestra modale corrente per poterla chiudere a fine azione.
     */
    private void eseguiEsportazione(Stage popup) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Salva Partita");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("File JSON", "*.json"));
        File file = fileChooser.showSaveDialog(popup);
        if (file != null && logica != null && stato != null) {
            try {
                PersistenceManager pm = new PersistenceManager(file.getParent());
                
                // Chiama il metodo di serializzazione che parsa le istanze OOP in oggetti SaveGameData
                SaveGameData datiDaSalvare = pm.impacchettaDati(stato, logica);
                pm.salvaPartita(datiDaSalvare, file.getName().replace(".json", ""));
                popup.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    /**
     * Pulisce l'intera griglia della mappa, azzera le finanze e resetta i parametri politici e di tempo,
     * riportando il gioco al suo stato vergine originale.
     *
     * @param popup Il riferimento alla finestra modale corrente.
     */
    private void eseguiReset(Stage popup) {
        if (logica != null && stato != null && mappaVisiva != null) {
            if (timeBarRef != null) {
                timeBarRef.fermaEImpostaManuale();
            }
            
            // Loop destruttivo che annulla il layer logico della grid
            for (int i = 0; i < logica.getWidth(); i++) {
                for (int j = 0; j < logica.getHeight(); j++) {
                    logica.removeEntity(i, j);
                }
            }
            
            stato.reset(); 
            
            mappaVisiva.rinfrescaMappaCompleta();
            
            topBar.resetPolitica(); 
            
            topBar.aggiornaDati(stato); 
            
            if (engine != null) {
				// Costringe tutti i listener a rileggere i dati azzerati tramite il pattern Observer
                engine.forceNotifyObservers();
            }
        }
        popup.close();
        
        TutorialPopup.mostraTutorial();
    }
}
