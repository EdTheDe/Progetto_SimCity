package citylogic.ui;

import citylogic.core.engine.SimulationEngine;
import citylogic.core.validation.BuilderValidator;
import citylogic.domain.map.UrbanGrid;
import citylogic.domain.state.StatoCitta;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Scale;
import javafx.stage.Stage;

/**
 * Punto di ingresso principale (Entry Point) dell'applicazione JavaFX.
 * Si occupa di assemblare la logica del gioco e di costruire la finestra principale scalabile.
 */
public class CityApp extends Application {
    private StatoCitta statoCitta;
    private UrbanGrid grigliaLogica;
    private BuilderValidator validatore;
    private SimulationEngine motore;

    /**
     * Inizializza lo stage principale, i componenti logici, i layout visivi
     * e configura il sistema di ridimensionamento dinamico (scaling) per la finestra.
     *
     * @param primaryStage La finestra principale fornita dal motore JavaFX.
     */
    @Override
    public void start(Stage primaryStage) {
        statoCitta = new StatoCitta();
        grigliaLogica = new UrbanGrid(24, 16);
        validatore = new BuilderValidator(grigliaLogica);
        motore = new SimulationEngine(statoCitta, grigliaLogica);

        AnchorPane root = new AnchorPane();
        root.setStyle("-fx-background-color: #1a252f;");

        TopBar barraSuperiore = new TopBar();
        barraSuperiore.aggiornaDati(statoCitta);
        barraSuperiore.setSimulationEngine(motore);
        motore.addObserver(barraSuperiore);

        MappaGriglia mappaVisiva = new MappaGriglia(grigliaLogica, validatore, statoCitta, barraSuperiore);
        barraSuperiore.setRiferimenti(grigliaLogica, statoCitta, mappaVisiva);

        SideBar barraLaterale = new SideBar(mappaVisiva);
        TimeBar barraTempo = new TimeBar(motore, barraSuperiore, mappaVisiva, statoCitta);
        motore.addObserver(barraTempo);
		barraSuperiore.setTimeBar(barraTempo);
        StackPane areaGiocoBase = new StackPane();
        areaGiocoBase.setPrefSize(1280, 720);
        areaGiocoBase.setMinSize(1280, 720);
        areaGiocoBase.setMaxSize(1280, 720);

        try {
            Image sfondImg = new Image(getClass().getResourceAsStream("/immagini/sfondo_isola.jpeg"));
            ImageView sfondoView = new ImageView(sfondImg);
            sfondoView.setFitWidth(1280);
            sfondoView.setFitHeight(720);
            areaGiocoBase.getChildren().add(sfondoView);
        } catch (Exception e) {
            areaGiocoBase.setStyle("-fx-background-color: #87CEEB;");
        }

        mappaVisiva.setTranslateY(10);
        areaGiocoBase.getChildren().add(mappaVisiva);

        AnchorPane areaGiocoCompleta = new AnchorPane();
        areaGiocoCompleta.setPrefSize(1280, 720);
        areaGiocoCompleta.setMinSize(1280, 720);
        areaGiocoCompleta.setMaxSize(1280, 720);

        areaGiocoCompleta.getChildren().add(areaGiocoBase);
        AnchorPane.setTopAnchor(areaGiocoBase, 0.0);
        AnchorPane.setBottomAnchor(areaGiocoBase, 0.0);
        AnchorPane.setLeftAnchor(areaGiocoBase, 0.0);
        AnchorPane.setRightAnchor(areaGiocoBase, 0.0);

        areaGiocoCompleta.getChildren().addAll(barraSuperiore, barraLaterale, barraTempo);

        AnchorPane.setTopAnchor(barraSuperiore, 0.0);
        AnchorPane.setLeftAnchor(barraSuperiore, 0.0);
        AnchorPane.setRightAnchor(barraSuperiore, 0.0);

        AnchorPane.setTopAnchor(barraLaterale, 80.0);
        AnchorPane.setLeftAnchor(barraLaterale, 20.0);

        AnchorPane.setBottomAnchor(barraTempo, 30.0);
        AnchorPane.setRightAnchor(barraTempo, 30.0);

        Pane contenitoreScalabile = new Pane(areaGiocoCompleta);
		// Ancoraggio per rendere responsive i componenti
        Scale scaleTransform = new Scale();
		// Imposta il Pivot (0,0), ovvero l'angolo in alto a sinistra, come punto fisso per l'ancoraggio dello zoom
        scaleTransform.setPivotX(0);
        scaleTransform.setPivotY(0);
		// Calcola costantemente la ratio (es. 1920/1280 = 1.5x) e scala l'intero AnchorPane proporzionalmente
        scaleTransform.xProperty().bind(root.widthProperty().divide(1280.0));
        scaleTransform.yProperty().bind(root.heightProperty().divide(720.0));

        areaGiocoCompleta.getTransforms().add(scaleTransform);

        AnchorPane.setTopAnchor(contenitoreScalabile, 0.0);
        AnchorPane.setBottomAnchor(contenitoreScalabile, 0.0);
        AnchorPane.setLeftAnchor(contenitoreScalabile, 0.0);
        AnchorPane.setRightAnchor(contenitoreScalabile, 0.0);

        root.getChildren().add(contenitoreScalabile);

        Scene scene = new Scene(root, 1280, 720);
        primaryStage.setTitle("CityLogic Simulator");
        primaryStage.setScene(scene);
        primaryStage.show();

        TutorialPopup.mostraTutorial();
    }

    /**
     * Metodo standard per l'avvio di un'applicazione Java.
     *
     * @param args Parametri da riga di comando.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
